package com.ufcg.psoft.commerce.model.sabor;

import com.fasterxml.jackson.annotation.*;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"clientes"})
@Entity(name = "sabor")
@JsonIdentityInfo(
        scope = Sabor.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Sabor {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_sabor")
    private Long id;

    @Column(nullable = false, name = "desc_nome")
    @JsonProperty("nome")
    private String nome;

    @Column(nullable = false, name = "desc_tipo")
    @JsonProperty("tipo")
    private Character tipo;

    @Column(nullable = false, name = "double_valorMedia")
    @JsonProperty("precoM")
    private Double precoM;

    @Column(nullable = false, name = "double_valorGrande")
    @JsonProperty("precoG")
    private Double precoG;

    @Column(name = "bool_disponibilidade")
    @JsonProperty("disponivel")
    private Boolean disponivel;

    @JsonProperty("estabelecimento")
    @ManyToOne(fetch = FetchType.EAGER,  cascade = CascadeType.REFRESH)
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;


    @ManyToMany
    @JoinTable(
            name = "cliente",
            joinColumns = @JoinColumn(name = "SaborId"),
            inverseJoinColumns = @JoinColumn(name = "ClienteId")
    )
    @Builder.Default
    private Set<Cliente> clientes = new HashSet<>();
}
