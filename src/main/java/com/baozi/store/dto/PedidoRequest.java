package com.baozi.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Corpo do POST /pedidos: só os identificadores e a quantidade. */
public record PedidoRequest(
        @NotNull(message = "clienteId é obrigatório") Long clienteId,
        @NotNull(message = "produtoId é obrigatório") Long produtoId,
        @NotNull(message = "quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser de pelo menos 1") Integer quantidade) {
}
