package com.ufcg.psoft.commerce.model.pagamento;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_pagamento")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CartaoCredito.class, name = "CartaoCredito"),
        @JsonSubTypes.Type(value = CartaoDebito.class, name = "CartaoDebito"),
        @JsonSubTypes.Type(value = Pix.class, name = "Pix")

})
@JsonIdentityInfo(
        scope = TipoPagamento.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public abstract class TipoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public abstract Double calculaTotal(Double valorTotal);
}
