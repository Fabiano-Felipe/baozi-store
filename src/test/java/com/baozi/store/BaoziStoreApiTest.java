package com.baozi.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BaoziStoreApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private long criar(String url, String json) throws Exception {
        MvcResult r = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = mapper.readTree(r.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    @Test
    void fluxoCompletoDoEstudoDeCaso() throws Exception {
        long clienteId = criar("/clientes", "{\"nome\":\"Fabiano2042149\",\"clienteDesde\":\"2026-10-12\"}");
        long produtoId = criar("/produtos", "{\"nome\":\"Baozi de Carne\",\"preco\":8.50,\"estoque\":true}");
        long pedidoId = criar("/pedidos",
                "{\"clienteId\":" + clienteId + ",\"produtoId\":" + produtoId + ",\"quantidade\":3}");

        // GET por ID
        mvc.perform(get("/clientes/" + clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Fabiano2042149")))
                .andExpect(jsonPath("$.clienteDesde", is("2026-10-12")));
        mvc.perform(get("/produtos/" + produtoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Baozi de Carne")))
                .andExpect(jsonPath("$.estoque", is(true)));
        mvc.perform(get("/pedidos/" + pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteNome", is("Fabiano2042149")))
                .andExpect(jsonPath("$.produtoNome", is("Baozi de Carne")))
                .andExpect(jsonPath("$.quantidade", is(3)));

        // GET listar todos
        mvc.perform(get("/clientes")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome", hasItem("Fabiano2042149")));
        mvc.perform(get("/produtos")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome", hasItem("Baozi de Carne")));
        mvc.perform(get("/pedidos")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem((int) pedidoId)));

        // Cliente e produto com pedido não podem ser apagados (409)
        mvc.perform(delete("/clientes/" + clienteId)).andExpect(status().isConflict());
        mvc.perform(delete("/produtos/" + produtoId)).andExpect(status().isConflict());

        // DELETE na ordem correta: pedido -> cliente -> produto
        mvc.perform(delete("/pedidos/" + pedidoId)).andExpect(status().isNoContent());
        mvc.perform(get("/pedidos/" + pedidoId)).andExpect(status().isNotFound());
        mvc.perform(delete("/clientes/" + clienteId)).andExpect(status().isNoContent());
        mvc.perform(delete("/produtos/" + produtoId)).andExpect(status().isNoContent());
        mvc.perform(get("/clientes/" + clienteId)).andExpect(status().isNotFound());
        mvc.perform(get("/produtos/" + produtoId)).andExpect(status().isNotFound());
    }

    @Test
    void clienteDesdeAssumeDataDeHojeQuandoNaoInformada() throws Exception {
        long id = criar("/clientes", "{\"nome\":\"Sem Data\"}");
        mvc.perform(get("/clientes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteDesde", is(java.time.LocalDate.now().toString())));
    }

    @Test
    void validacaoRetorna400ComMensagensPorCampo() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome", is("O nome do cliente é obrigatório")));

        mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"X\",\"preco\":-1,\"estoque\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.preco", is("O preço deve ser maior que zero")));

        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":1,\"produtoId\":1,\"quantidade\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.quantidade", is("A quantidade deve ser de pelo menos 1")));
    }

    @Test
    void jsonMalformadoRetorna400() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("{nome:"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void pedidoComClienteOuProdutoInexistenteRetorna404() throws Exception {
        long produtoId = criar("/produtos", "{\"nome\":\"Baozi\",\"preco\":7.00,\"estoque\":true}");
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":999999,\"produtoId\":" + produtoId + ",\"quantidade\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void pedidoDeProdutoSemEstoqueRetorna409() throws Exception {
        long clienteId = criar("/clientes", "{\"nome\":\"Cliente Teste\"}");
        long produtoId = criar("/produtos", "{\"nome\":\"Baozi Esgotado\",\"preco\":7.00,\"estoque\":false}");
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":" + clienteId + ",\"produtoId\":" + produtoId + ",\"quantidade\":1}"))
                .andExpect(status().isConflict());
    }

    @Test
    void buscarOuApagarIdInexistenteRetorna404() throws Exception {
        mvc.perform(get("/clientes/987654")).andExpect(status().isNotFound());
        mvc.perform(delete("/produtos/987654")).andExpect(status().isNotFound());
        mvc.perform(delete("/pedidos/987654")).andExpect(status().isNotFound());
    }
}
