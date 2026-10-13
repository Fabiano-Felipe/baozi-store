# Baozi Store - API REST

API REST da Baozi Store (loja de pãozinho chinês), desenvolvida para a disciplina **Desenvolvimento Web Back-End**.
Controla **clientes**, **produtos** e **pedidos** (um cliente compra um único produto em determinada quantidade).

**Tecnologias:** Java 17 · Spring Boot 3.2 · Spring Data JPA · Bean Validation · H2 (padrão) ou MySQL · Maven · Postman

## Arquitetura (MVC do Spring)

```
src/main/java/com/baozi/store
├── model/        Entidades JPA: Cliente, Produto, Pedido
├── repository/   Interfaces Spring Data JPA (JpaRepository)
├── controller/   Controllers REST: ClienteController, ProdutoController, PedidoController
├── dto/          PedidoRequest / PedidoResponse (corpo e resposta de /pedidos)
└── exception/    Exceções e GlobalExceptionHandler (respostas de erro em JSON)
```

## Como executar

Pré-requisitos: JDK 17 ou superior e Maven 3.9+.

```bash
# Banco padrão: H2 em arquivo (nenhuma instalação necessária)
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Console do H2: `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:file:./data/baozidb`, usuário `sa`, senha vazia).

### Usando MySQL (opcional)

```bash
export DB_PASSWORD=sua_senha        # Windows (PowerShell): $env:DB_PASSWORD="sua_senha"
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

O banco `baozi_store` é criado automaticamente; as tabelas são geradas pelo Hibernate (`ddl-auto=update`).

### Testes automatizados

```bash
mvn test
```

## Endpoints

| Método | URL | Descrição | Sucesso |
|--------|-----|-----------|---------|
| POST | `/clientes` | Cadastrar cliente | 201 |
| GET | `/clientes/{id}` | Consultar cliente por ID | 200 |
| GET | `/clientes` | Listar todos os clientes | 200 |
| DELETE | `/clientes/{id}` | Apagar cliente | 204 |
| POST | `/produtos` | Cadastrar produto | 201 |
| GET | `/produtos/{id}` | Consultar produto por ID | 200 |
| GET | `/produtos` | Listar todos os produtos | 200 |
| DELETE | `/produtos/{id}` | Apagar produto | 204 |
| POST | `/pedidos` | Registrar pedido | 201 |
| GET | `/pedidos/{id}` | Consultar pedido por ID | 200 |
| GET | `/pedidos` | Listar todos os pedidos | 200 |
| DELETE | `/pedidos/{id}` | Apagar pedido | 204 |

Erros: `400` dados inválidos/JSON malformado · `404` recurso não encontrado · `409` regra de negócio
(apagar cliente/produto que possui pedido; pedir produto sem estoque).

### Exemplos de corpo (JSON)

```json
POST /clientes   { "nome": "Fabiano2042149", "clienteDesde": "2026-10-12" }
POST /produtos   { "nome": "Baozi de Carne", "preco": 8.50, "estoque": true }
POST /pedidos    { "clienteId": 1, "produtoId": 1, "quantidade": 3 }
```

## Testando no Postman

Importe `postman/BaoziStore.postman_collection.json` (Import > arquivo). A coleção guarda os IDs criados em
variáveis, então basta executar as pastas **1 → 4 na ordem**. Os scripts da aba *Tests* mostram os testes
passando (verde) no painel de resposta.

## Entrega no GitHub

```bash
git init
git add .
git commit -m "API REST Baozi Store"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/baozi-store.git
git push -u origin main
```
