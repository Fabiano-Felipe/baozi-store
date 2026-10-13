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

