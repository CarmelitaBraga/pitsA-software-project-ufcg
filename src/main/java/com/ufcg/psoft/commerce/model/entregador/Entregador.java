package com.ufcg.psoft.commerce.model.entregador;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"codigoAcesso", "associacoes", "estabelecimento", "ficha", "pedido"})
@Entity
@Table(name = "Entregadores")

public class Entregador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotEmpty(message = "Codigo de Acesso não pode ser vazio.")
    @JsonIgnore
    private String codigoAcesso;

    @Column
    @NotEmpty(message = "Nome não pode ser vazio.")
    private String nome;
    
    @Column
    @NotNull(message = "disponiblidade não pode ser vazia!")
    private Boolean disponibilidade;

    @Embedded
    private Veiculo veiculo;

    @OneToMany(mappedBy = "entregador", cascade = {CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Associacao> associacoes = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "estabelecimento_id")
    @JsonProperty("estabelecimento")
    @JsonIgnore
    @ToString.Exclude
    private Estabelecimento estabelecimento;

    @OneToOne
    @JsonProperty("pedido")
    @JsonIgnore
    private Pedido pedido;

    @Column
    @JsonProperty("ficha")
    @JsonIgnore
    private Long ficha;


}
