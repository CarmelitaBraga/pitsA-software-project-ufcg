package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.entregador.Entregador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregadorRepository extends JpaRepository<Entregador, Long> {
    public Entregador findEntregadorById(Long id);

}
