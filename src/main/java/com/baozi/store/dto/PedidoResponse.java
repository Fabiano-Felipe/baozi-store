package com.baozi.store.dto;

import com.baozi.store.model.Pedido;

/**
 * Resposta dos endpoints de pedido. Além dos ids (clienteId, produtoId),
 * devolve nome do cliente e do produto, para que o print do Postman
 * mostre todas as informações do estudo de caso.
 */
public record PedidoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        Long produtoId,
        String produtoNome,
        Integer quantidade) {

    public static PedidoResponse de(Pedido p) {
        return new PedidoResponse(
                p.getId(),
                p.getCliente().getId(),
                p.getCliente().getNome(),
                p.getProduto().getId(),
                p.getProduto().getNome(),
                p.getQuantidade());
    }
}
