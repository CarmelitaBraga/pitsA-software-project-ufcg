package com.ufcg.psoft.commerce.model.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "enderecos")
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class Endereco {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column
    @NotNull(message = "Número não pode ser vazio.")
    @JsonProperty("numero")
    private Integer numero;

    @Column
    @NotBlank(message = "Cep não pode ser vazio.")
    @JsonProperty("cep")
    private String cep;

    @Column
    @NotBlank(message = "Complemento não pode ser vazio.")
    @JsonProperty("complemento")
    private String complemento;

    @OneToOne
    @JsonProperty("cliente")
    private Cliente cliente;
}
