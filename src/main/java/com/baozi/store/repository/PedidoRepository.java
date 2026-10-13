package com.baozi.store.repository;

import com.baozi.store.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Query derivada: o Spring Data gera o SQL a partir do nome do método
    boolean existsByClienteId(Long clienteId);

    boolean existsByProdutoId(Long produtoId);
}
