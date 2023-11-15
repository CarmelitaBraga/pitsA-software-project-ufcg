package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    public Cliente findClienteById(Long id);
}
