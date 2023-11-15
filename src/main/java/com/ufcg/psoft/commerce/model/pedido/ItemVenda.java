package com.ufcg.psoft.commerce.model.pedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.pizza.Pizza;
import com.ufcg.psoft.commerce.model.pizza.PizzaG;
import com.ufcg.psoft.commerce.model.pizza.PizzaM;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Getter
public class ItemVenda {

    @NotNull(message = "É necessário uma quantidade de uma certa pizza para o pedido!")
    @JsonProperty("quantidade")
    private Integer quantidade;

    @NotNull(message = "É necessário uma pizza para o pedido!")
    @ManyToOne(cascade={CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "pizza_id")
    @Valid
    @JsonProperty("pizza")
    private Pizza pizza;

    @JsonProperty("subtotal")
    @Column(name = "subTotal")
    private Double subTotal;

    public static class ItemVendaBuilder {
        public ItemVenda build() {
            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setQuantidade(this.quantidade);
            itemVenda.setPizza(this.pizza);

            if(this.quantidade != null && pizza!= null){
                if(pizza instanceof PizzaG){
                    if(((PizzaG) pizza).getSabor1() != null && ((PizzaG) pizza).getSabor2() != null){
                        Double subTotal = this.pizza.calcularPreco() * this.quantidade;
                        itemVenda.setSubTotal(subTotal);
                    }
                    else{
                        itemVenda.setSubTotal(null);
                    }
                }
                else{
                    if(((PizzaM) pizza).getSabor1() != null){
                        Double subTotal = this.pizza.calcularPreco() * this.quantidade;
                        itemVenda.setSubTotal(subTotal);
                    }
                    else{
                        itemVenda.setSubTotal(null);
                    }
                }
            }
            else{
                itemVenda.setSubTotal(null);
            }
            return itemVenda;
        }
    }
}
