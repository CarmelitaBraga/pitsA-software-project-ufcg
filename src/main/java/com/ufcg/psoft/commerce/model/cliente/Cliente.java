package com.ufcg.psoft.commerce.model.cliente;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@Entity
@Table(name = "clientes")
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        scope = Cliente.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Cliente {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigoAcesso;

    @Column
    private String nome;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;

    @Column
    private String email;

    @ManyToMany(mappedBy = "clientes", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Sabor> saboresDeInteresse = new HashSet<>();
}
