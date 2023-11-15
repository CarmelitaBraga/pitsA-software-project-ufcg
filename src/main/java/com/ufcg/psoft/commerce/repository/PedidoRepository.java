package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    public Pedido findPedidoById(Long id);

//    public List<Pedido> findPedidosByCliente(Cliente cliente);

    public List<Pedido> findPedidosByEstabelecimento(Estabelecimento estabelecimento);

    public List<Pedido> findPedidosByClienteOrderByTimestampDesc(Cliente cliente);
}
