package com.ufcg.psoft.commerce.model.estabelecimento;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"codigoAcesso", "associacoes", "sabores", "pedidos", "filaEntregador", "filaPedidos"})
@Entity
@Table(name = "Estabelecimentos")
public class Estabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "desc_codigoAcesso", nullable = false)
    private String codigoAcesso;

    @Column(name = "desc_email")
    private String email;

    @JsonBackReference
    @OneToMany(mappedBy = "estabelecimento", cascade =  {CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Sabor> sabores;

    @OneToMany(mappedBy = "estabelecimento", cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Associacao> associacoes = new HashSet<>();

    @OneToMany(mappedBy = "estabelecimento", cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Pedido> pedidos = new HashSet<>();

    @OneToMany(mappedBy = "estabelecimento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Entregador> filaEntregador = new LinkedList<>();

    @OneToMany(mappedBy = "orderEstabelecimento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Pedido> filaPedidos = new LinkedList<>();

    public List<Entregador> getFilaEntregador(){
        if(filaEntregador.isEmpty()){
            return filaEntregador;
        }
        List<Entregador> list = this.filaEntregador;
        list.sort(Comparator.comparing(Entregador::getFicha));
        return list;

    }

    public List<Pedido> getFilaPedidos(){
        if(filaPedidos.isEmpty()){
            return filaPedidos;
        }
        List<Pedido> list = this.filaPedidos;
        list.sort(Comparator.comparing(Pedido::getFicha));
        return list;
    }
}
