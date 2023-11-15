package com.ufcg.psoft.commerce.controller;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.cliente.ClienteGetResponseDTO;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorPatchDto;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoPatchRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoPostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;

import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.entregador.Veiculo;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pagamento.Pagamento;
import com.ufcg.psoft.commerce.model.pagamento.Pix;
import com.ufcg.psoft.commerce.model.pedido.ItemVenda;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.pedido.statepedido.*;
import com.ufcg.psoft.commerce.model.pizza.Pizza;
import com.ufcg.psoft.commerce.model.pizza.PizzaG;
import com.ufcg.psoft.commerce.model.pizza.PizzaM;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.entregador.EntregadorV1PatchService;
import com.ufcg.psoft.commerce.service.order.CommandOrderService;
import com.ufcg.psoft.commerce.service.order.ICommandOrderService;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de pedidos")
public class PedidoControllerTests {
    final String URI_PEDIDOS = "/pedidos";

    final String URI_ESTABELECIMENTO = "/estabelecimentos";

    final String URI_ENTREGADORES = "/entregadores";

    @Autowired
    MockMvc driver;

    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    EntregadorV1PatchService entregadorV1PatchService;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ICommandOrderService commandOrderService;

    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);;
    Cliente cliente;
    Cliente cliente1;
    Entregador entregador;
    Entregador entregador2;
    Sabor sabor1;
    Sabor sabor2;
    Sabor sabor3;

    Pizza pizzaM;
    Pizza pizzaM1;

    Pizza pizzaG;
    Estabelecimento estabelecimento;
    Estabelecimento estabelecimento2;

    Pedido pedido;
    Pedido pedido1;
    Pedido pedido2;
    Pedido pedido3;
    ItemVenda itemVenda1;
    ItemVenda itemVenda2;
    PedidoPostPutRequestDTO pedidoPostPutRequestDTO;

    EntregadorPatchDto entregadorPatchDto;

    @BeforeEach
    void setup() {
        pedidoRepository.deleteAll();
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        clienteRepository.deleteAll();
        entregadorRepository.deleteAll();

        objectMapper.registerModule(new JavaTimeModule());
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .codigoAcesso("654321")
                .build());

        estabelecimento2 = estabelecimentoRepository.save(Estabelecimento.builder()
                .codigoAcesso("123456")
                .build());

        sabor1 = saborRepository.save(Sabor.builder()
                .nome("Sabor Um")
                .estabelecimento(estabelecimento)
                .tipo('S')
                .precoM(10D)
                .precoG(20D)
                .disponivel(true)
                .build());
        sabor2 = saborRepository.save(Sabor.builder()
                .nome("Sabor Dois")
                .estabelecimento(estabelecimento)
                .tipo('D')
                .precoM(15D)
                .precoG(30D)
                .disponivel(true)
                .build());

        sabor3 = saborRepository.save(Sabor.builder()
                .nome("Sabor Três")
                .estabelecimento(estabelecimento2)
                .tipo('D')
                .precoM(15D)
                .precoG(30D)
                .disponivel(true)
                .build());

        Endereco end1 = Endereco.builder()
                .cep("12345-67")
                .numero(52)
                .complemento("Rua De Dentro")
                .build();
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Anton Ego")
                .endereco(end1)
                .codigoAcesso("123456")
                .email("campinafoood@gmail.com")
                .build());
        cliente1 = clienteRepository.save(Cliente.builder()
                .nome("Fulano")
                .endereco(end1)
                .codigoAcesso("654321")
                .email("campinafoood@gmail.com")
                .build());

        Veiculo veiculo = Veiculo.builder()
                .cor("Azul")
                .placa("ABC-1234")
                .tipo("Moto")
                .build();

        Veiculo veiculo2 = Veiculo.builder()
                .cor("Laranja")
                .placa("OCO-1234")
                .tipo("Carro")
                .build();

        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Joãozinho")
                .veiculo(veiculo)
                .codigoAcesso("101010")
                .disponibilidade(true)
                .build());

        entregador2 = entregadorRepository.save(Entregador.builder()
                .nome("Jailson")
                .veiculo(veiculo2)
                .codigoAcesso("202020")
                .disponibilidade(true)
                .build());

        pizzaM = PizzaM.builder()
                .sabor1(sabor1)
                .build();

        pizzaM1 = PizzaM.builder()
                .sabor1(sabor3)
                .build();

        pizzaG = PizzaG.builder()
                .sabor1(sabor1)
                .sabor2(sabor2)
                .build();
        itemVenda1 = ItemVenda.builder()
                .pizza(pizzaM)
                .quantidade(1)
                .build();
        itemVenda2 = ItemVenda.builder()
                .pizza(pizzaG)
                .quantidade(2)
                .build();
        ItemVenda itemVenda3 = ItemVenda.builder()
                .pizza(pizzaM1)
                .quantidade(2)
                .build();

        List<ItemVenda> itensVenda = new ArrayList<>();
        itensVenda.add(itemVenda1);
        itensVenda.add(itemVenda2);

        List<ItemVenda> itensVenda2 = new ArrayList<>();
        itensVenda2.add(itemVenda3);

        pedido = Pedido.builder()
                .cliente(cliente)
                .estabelecimento(estabelecimento)
                .endereco(cliente.getEndereco())
                .status(new PedidoEmPreparo())
                .itens(itensVenda)
                .build();
       pedido1 = Pedido.builder()
               .cliente(cliente)
               .estabelecimento(estabelecimento)
               .endereco(cliente.getEndereco())
               .status(new PedidoEmPreparo())
               .itens(itensVenda)
               .build();
       pedido2 = Pedido.builder()
               .cliente(cliente1)
               .estabelecimento(estabelecimento)
               .endereco(cliente1.getEndereco())
               .entregador(entregador)
               .itens(itensVenda)
               .build();
       pedido3 = Pedido.builder()
               .cliente(cliente1)
               .estabelecimento(estabelecimento)
               .endereco(cliente1.getEndereco())
               .entregador(entregador)
               .itens(itensVenda)
               .build();
       pedidoPostPutRequestDTO = PedidoPostPutRequestDTO.builder()
               .endereco(pedido.getEndereco())
               .itens(pedido.getItens())
               .build();

       associacaoRepository.save(Associacao.builder()
                       .entregador(entregador)
                       .estabelecimento(estabelecimento)
                       .status(true)
                       .build());
    }

    @Nested
    @DisplayName("Conjunto de Verificações para Post de Pedido")
    class VerificacoesPostPedido{
        @Test
        @DisplayName("Quando criamos um novo pedido com dados válidos")
        void PedidoController_DevePassar_quandoCriamosUmNovoPedido() throws Exception {
            // Arrange
            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedido.getCliente().getEndereco(), resultado.getEndereco()),
                    () -> assertEquals(2, resultado.getItens().size()),
                    () -> assertEquals(pedido.getItens().toString(), resultado.getItens().toString()),
                    () -> assertEquals(pedido.getCliente().getNome(), resultado.getCliente().getNome()),
                    () -> assertEquals(pedido.getCliente().getId().intValue(), resultado.getCliente().getId().intValue()),
                    () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.getEstabelecimento().getId()),
                    () -> assertEquals(pedido.getTotal(), resultado.getTotal())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com código de acesso inválido")
        void PedidoController_DeveFalhar_quandoCriamosUmNovoPedidoComCodigoDeAcessoInvalido() throws Exception {
            // Arrange
            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", "999999")
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, pedidoRepository.count()),
                    () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com id do cliente inválido")
        void PedidoController_DeveFalhar_quandoCriamosUmNovoPedidoComIdDoClienteInvalido() throws Exception {
            // Arrange
            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", "11111")
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, pedidoRepository.count()),
                    () -> assertEquals("Cliente não existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com id do estabelecimento inválido")
        void PedidoController_DeveFalhar_quandoCriamosUmNovoPedidoComIdDoEstabelecimentoInvalido() throws Exception {
            // Arrange
            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", "11111")
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, pedidoRepository.count()),
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com sabores de outros estabelecimento")
        void PedidoController_DeveFalhar_quandoCriamosUmNovoPedidoComSaboresDeOutroEstabelecimento() throws Exception {
            // Arrange
            Estabelecimento estabelecimento2 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123445")
                    .build());

            Sabor sabor3 = saborRepository.save(Sabor.builder()
                    .estabelecimento(estabelecimento2)
                    .precoM(11D)
                    .precoG(20D)
                    .disponivel(true)
                    .tipo('D')
                    .nome("Sabor 3")
                    .build());

            Pizza pizza = PizzaM.builder()
                    .sabor1(sabor3)
                    .build();

            pedidoPostPutRequestDTO.setItens(Collections.singletonList(ItemVenda.builder().quantidade(1).pizza(pizza).build()));
            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Sabor não pertence a este estabelecimento!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com itens nulos")
        void PedidoController_DeveFalhar_quandoCriamosUmNovoPedidoComItensNulos() throws Exception {
            // Arrange
            pedidoPostPutRequestDTO.setItens(null);
            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Lista de itens não pode ser vazia!", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com pizza nula")
        void PedidoController_DeveFalhar_quandoCriamosUmNovoPedidoComPizzaNula() throws Exception {
            // Arrange
            pedidoPostPutRequestDTO.setItens(Collections.singletonList(ItemVenda.builder().quantidade(1).pizza(null).build()));

            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("É necessário uma pizza para o pedido!", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com quantidade de uma certa pizza nula")
        void PedidoController_DeveFalhar_quandoCriamosUmNovoPedidoComQuantidadeNula() throws Exception {
            // Arrange
            pedidoPostPutRequestDTO.setItens(Collections.singletonList(ItemVenda.builder().quantidade(null).pizza(pizzaM).build()));

            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("É necessário uma quantidade de uma certa pizza para o pedido!", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com pizzas sem sabores")
        void PedidoController_DeveFalhar_quandoCriamosUmNovoPedidoComPizzasSemSabores() throws Exception {
            // Arrange
            Pizza pizza = PizzaM.builder()
                    .sabor1(null)
                    .build();

            pedidoPostPutRequestDTO.setItens(Collections.singletonList(ItemVenda.builder().quantidade(1).pizza(pizza).build()));
            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("É necessário um sabor para a pizza", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo pedido com dados válidos e checamos se o status está como recebido")
        void PedidoController_DevePassar_quandoCriamosUmNovoPedidoStatusRecebido() throws Exception {
            // Arrange
            // Act
            String responseJsonString = driver.perform(post(URI_PEDIDOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())// Codigo 201
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertTrue(resultado.getStatus() instanceof PedidoRecebido)
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de Verificações para Put de Pedido")
    class VerificacoesPutPedido{
        @Test
        @DisplayName("Quando alteramos pedido com dados válidos")
        void PedidoController_DevePassar_quandoAlteramosPedidoValido() throws Exception {
            // Arrange
            Endereco end = Endereco.builder()
                    .cep("55555-55")
                    .numero(51)
                    .complemento("Rua De Fora")
                    .cliente(cliente)
                    .build();

            ItemVenda itemVenda = ItemVenda.builder()
                    .quantidade(1)
                    .pizza(pizzaM)
                    .build();

            pedidoRepository.save(pedido1);
            pedidoPostPutRequestDTO.setItens(Collections.singletonList(itemVenda));
            pedidoPostPutRequestDTO.setEndereco(end);

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido1.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            //.param("pedidoId", pedido.getId().toString())
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);
            // Assert
            assertAll(
                    () -> assertEquals(pedido1.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(pedidoPostPutRequestDTO.getEndereco(), resultado.getEndereco()),
                    () -> assertEquals(pedidoPostPutRequestDTO.getItens().get(0).getPizza(), resultado.getItens().get(0).getPizza()),
                    () -> assertEquals(pedido1.getCliente().getNome(), resultado.getCliente().getNome()),
                    () -> assertEquals(pedido1.getEstabelecimento().getId(), resultado.getEstabelecimento().getId()),
                    () -> assertEquals(pedido1.getTotal(), resultado.getTotal())
            );
        }

        @Test
        @DisplayName("Quando alteramos pedido com Endereco Null")
        void  PedidoController_DevePassar_quandoAlteramosPedidoValidoComEnderecoNull() throws Exception {
            // Arrange
            Pedido pedidoFeito = pedidoRepository.save(pedido);
            Long pedidoId = pedidoFeito.getId();

            Sabor sabor = saborRepository.save(Sabor.builder()
                    .nome("Carne de sol")
                    .estabelecimento(estabelecimento)
                    .tipo('S')
                    .precoM(25.0)
                    .precoG(32.5)
                    .disponivel(true)
                    .build());

            Pizza pizza = PizzaM.builder()
                    .sabor1(sabor)
                    .build();
            List<ItemVenda> itensVenda = new ArrayList<>();
            itensVenda.add(ItemVenda.builder()
                    .quantidade(2)
                    .pizza(pizza)
                    .subTotal(15.2)
                    .build());

            pedidoPostPutRequestDTO = PedidoPostPutRequestDTO.builder()
                    .endereco(null)
                    .itens(itensVenda)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(pedidoId, resultado.getId().longValue()),
                    () -> assertEquals(cliente.getEndereco().getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(cliente.getEndereco().getCep(), resultado.getEndereco().getCep()),
                    () -> assertEquals(cliente.getEndereco().getComplemento(), resultado.getEndereco().getComplemento()),
                    () -> assertEquals(pedidoFeito.getItens().get(0).getPizza(), resultado.getItens().get(0).getPizza()),
                    () -> assertEquals(pedido.getCliente().getNome(), resultado.getCliente().getNome()),
                    () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.getEstabelecimento().getId()),
                    () -> assertEquals(pedido.getTotal(), resultado.getTotal())
            );
        }

        @Test
        @DisplayName("Quando alteramos pedido com Lista de Itens Null")
        void PedidoController_DevePassar_quandoAlteramosPedidoValidoComListaItensNull() throws Exception {
            // Arrange
            Pedido pedidoFeito = pedidoRepository.save(pedido);
            Long pedidoId = pedidoFeito.getId();

            Sabor sabor = saborRepository.save(Sabor.builder()
                    .nome("Carne de sol")
                    .estabelecimento(estabelecimento)
                    .tipo('S')
                    .precoM(25.0)
                    .precoG(32.5)
                    .disponivel(true)
                    .build());

            Pizza pizza = PizzaM.builder()
                    .sabor1(sabor)
                    .build();
            List<ItemVenda> itensVenda = new ArrayList<>();
            itensVenda.add(ItemVenda.builder()
                    .quantidade(2)
                    .pizza(pizza)
                    .subTotal(15.2)
                    .build());

            pedidoPostPutRequestDTO = PedidoPostPutRequestDTO.builder()
                    .endereco(Endereco.builder()
                            .cep("1111111")
                            .complemento("Bla Bla Bla")
                            .numero(10)
                            .build())
                    .itens(null)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(pedidoId, resultado.getId().longValue()),
                    () -> assertEquals(pedidoPostPutRequestDTO.getEndereco().getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(pedidoPostPutRequestDTO.getEndereco().getCep(), resultado.getEndereco().getCep()),
                    () -> assertEquals(pedidoPostPutRequestDTO.getEndereco().getComplemento(), resultado.getEndereco().getComplemento()),
                    () -> assertEquals(pedidoFeito.getItens().get(0).getPizza(), resultado.getItens().get(0).getPizza()),
                    () -> assertEquals(pedido.getCliente().getNome(), resultado.getCliente().getNome()),
                    () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.getEstabelecimento().getId()),
                    () -> assertEquals(pedido.getTotal(), resultado.getTotal())
            );
        }

        @Test
        @DisplayName("Quando alteramos um pedido inexistente")
        void PedidoController_DeveFalhar_quandoAlteramosPedidoInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/4868516")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O pedido consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando alteramos um pedido passando codigo de acesso invalido")
        void PedidoController_DeveFalhar_quandoAlteramosPedidoPassandoCodigoAcessoInvalido() throws Exception {
            // Arrange
            pedidoRepository.deleteAll();

            Pedido pedidoFeito = pedidoRepository.save(pedido);
            Long pedidoId = pedidoFeito.getId();

            Sabor sabor = saborRepository.save(Sabor.builder()
                    .nome("Carne de sol na nata")
                    .estabelecimento(estabelecimento)
                    .tipo('S')
                    .precoM(26.0)
                    .precoG(34.5)
                    .disponivel(true)
                    .build());

            Pizza pizza = PizzaG.builder()
                    .sabor1(sabor)
                    .build();
            List<ItemVenda> itensVenda = new ArrayList<>();
            itensVenda.add(ItemVenda.builder()
                    .quantidade(3)
                    .pizza(pizza)
                    .subTotal(15.2)
                    .build());

            pedidoPostPutRequestDTO = PedidoPostPutRequestDTO.builder()
                    .endereco(null)
                    .itens(itensVenda)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }
    }
    @Nested
    @DisplayName("Conjunto de Verificações para Get One de Pedido")
    class VerificacoesGetPedido{
        @Nested
        @DisplayName("Teste de cliente pegar todos os pedido (getAll)")
        class VerificacaoClienteBuscaTodosPedidos {
            @Test
            @DisplayName("Quando um cliente busca por todos seus pedidos salvos em ordem temporal")
            void PedidoController_DevePassar_quandoClienteBuscaTodosPedidos() throws Exception {
                // Arrange
                StatePedido pedidoRecebido = PedidoRecebido.builder().build();

                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRecebido.setOrderNumber(1);

                pedido = pedidoRepository.save(pedido);
                pedido.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido.confirmaPagamento();
                pedido.terminoPreparo();
                pedido.setEntregador(entregador);
                pedido.atribuidoEntregador();
                pedido.clienteConfirmaEntrega();

                pedido1 = pedidoRepository.save(pedido1);
                StatePedido pedidoRecebido1 = PedidoRecebido.builder().build();
                pedido1.setStatus(pedidoRecebido1);
                pedidoRecebido1.setPedido(pedido1);
                pedidoRecebido1.setOrderNumber(1);

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(2, resultado.size()),
                        () -> assertEquals(pedido1.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedido1.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido1.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido1.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedido1.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedido1.getTotal(), resultado.get(0).getTotal()),
                        () -> assertEquals(pedido.getId(), resultado.get(1).getId()),
                        () -> assertEquals(pedido.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido.getItens().get(0).getPizza(), resultado.get(1).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido.getCliente().getNome(), resultado.get(1).getCliente().getNome()),
                        () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.get(1).getEstabelecimento().getId()),
                        () -> assertEquals(pedido.getTotal(), resultado.get(1).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por pedidos em ordem de tempo e status")
            void PedidoController_DevePassar_quandoClienteBuscaTodosPedidosOrdenadosCronologicamenteEPorStatus() throws Exception {
                // Arrange
                List<ItemVenda> itensVenda = new ArrayList<>();
                itensVenda.add(itemVenda1);
                itensVenda.add(itemVenda2);

                pedido1 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();

                pedido = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                pedido1 = pedidoRepository.save(pedido1);
                StatePedido pedidoRecebido1 = PedidoRecebido.builder().build();
                pedido1.setStatus(pedidoRecebido1);
                pedidoRecebido1.setPedido(pedido1);
                pedidoRecebido1.setOrderNumber(1);

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRecebido.setOrderNumber(1);
                pedido = pedidoRepository.save(pedido);
                pedido.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido.confirmaPagamento();
                pedido.terminoPreparo();
                pedido.setEntregador(entregador);
                pedido.atribuidoEntregador();
                pedido.clienteConfirmaEntrega();

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(2, resultado.size()),
                        () -> assertEquals(pedido1.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedido1.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido1.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido1.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedido1.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedido1.getTotal(), resultado.get(0).getTotal()),
                        () -> assertEquals(pedido.getId(), resultado.get(1).getId()),
                        () -> assertEquals(pedido.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido.getItens().get(0).getPizza(), resultado.get(1).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido.getCliente().getNome(), resultado.get(1).getCliente().getNome()),
                        () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.get(1).getEstabelecimento().getId()),
                        () -> assertEquals(pedido.getTotal(), resultado.get(1).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por 2 pedidos entregues e 1 em rota")
            void PedidoController_DevePassar_quandoClienteBusca3Pedidos2Entregues1EmRota() throws Exception {
                // Arrange
                List<ItemVenda> itensVenda = new ArrayList<>();
                itensVenda.add(itemVenda1);
                itensVenda.add(itemVenda2);

                pedido2 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                pedidoRepository.save(pedido2);
                StatePedido pedidoRecebido2 = PedidoRecebido.builder().build();
                pedido2.setStatus(pedidoRecebido2);
                pedidoRecebido2.setPedido(pedido2);
                pedidoRecebido2.setOrderNumber(1);
                pedido2.confirmaPagamento();
                pedido2.terminoPreparo();
                pedido2.setEntregador(entregador);
                pedido2.atribuidoEntregador();

                sleep(2);

                pedido1 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();


                pedido1 = pedidoRepository.save(pedido1);
                StatePedido pedidoRecebido1 = PedidoRecebido.builder().build();
                pedido1.setStatus(pedidoRecebido1);
                pedidoRecebido1.setPedido(pedido1);
                pedidoRecebido1.setOrderNumber(1);
                pedido1.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido1.confirmaPagamento();
                pedido1.terminoPreparo();
                pedido1.setEntregador(entregador);
                pedido1.atribuidoEntregador();
                pedido1.clienteConfirmaEntrega();

                sleep(2);

                pedido = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRecebido.setOrderNumber(1);
                pedido = pedidoRepository.save(pedido);
                pedido.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido.confirmaPagamento();
                pedido.terminoPreparo();
                pedido.setEntregador(entregador);
                pedido.atribuidoEntregador();
                pedido.clienteConfirmaEntrega();

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(3, resultado.size()),
                        () -> assertEquals(pedido2.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedido2.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido2.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido2.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedido2.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedido2.getTotal(), resultado.get(0).getTotal()),
                        () -> assertEquals(pedido.getId(), resultado.get(1).getId()),
                        () -> assertEquals(pedido.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido.getItens().get(0).getPizza(), resultado.get(1).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido.getCliente().getNome(), resultado.get(1).getCliente().getNome()),
                        () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.get(1).getEstabelecimento().getId()),
                        () -> assertEquals(pedido.getTotal(), resultado.get(1).getTotal()),
                        () -> assertEquals(pedido1.getId(), resultado.get(2).getId()),
                        () -> assertEquals(pedido1.getEndereco(), resultado.get(2).getEndereco()),
                        () -> assertEquals(pedido1.getItens().get(0).getPizza(), resultado.get(2).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido1.getCliente().getNome(), resultado.get(2).getCliente().getNome()),
                        () -> assertEquals(pedido1.getEstabelecimento().getId(), resultado.get(2).getEstabelecimento().getId()),
                        () -> assertEquals(pedido1.getTotal(), resultado.get(2).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por pedidos entregues")
            void PedidoController_DevePassar_quandoClienteBuscaPedidosEntregues() throws Exception {
                // Arrange
                List<ItemVenda> itensVenda = new ArrayList<>();
                itensVenda.add(itemVenda1);
                itensVenda.add(itemVenda2);

                pedido2 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                pedidoRepository.save(pedido2);
                StatePedido pedidoRecebido2 = PedidoRecebido.builder().build();
                pedido2.setStatus(pedidoRecebido2);
                pedidoRecebido2.setPedido(pedido2);
                pedidoRecebido2.setOrderNumber(1);
                pedido2.confirmaPagamento();
                pedido2.terminoPreparo();
                pedido2.setEntregador(entregador);
                pedido2.atribuidoEntregador();

                pedido1 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();


                pedido1 = pedidoRepository.save(pedido1);
                StatePedido pedidoRecebido1 = PedidoRecebido.builder().build();
                pedido1.setStatus(pedidoRecebido1);
                pedidoRecebido1.setPedido(pedido1);
                pedidoRecebido1.setOrderNumber(1);
                pedido1.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido1.confirmaPagamento();
                pedido1.terminoPreparo();
                pedido1.setEntregador(entregador);
                pedido1.atribuidoEntregador();
                pedido1.clienteConfirmaEntrega();

                sleep(2);

                pedido = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRecebido.setOrderNumber(1);
                pedido = pedidoRepository.save(pedido);
                pedido.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido.confirmaPagamento();
                pedido.terminoPreparo();
                pedido.setEntregador(entregador);
                pedido.atribuidoEntregador();
                pedido.clienteConfirmaEntrega();

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/status")
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("status", "entregues"))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(2, resultado.size()),
                        () -> assertEquals(pedido.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedido.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedido.getTotal(), resultado.get(0).getTotal()),
                        () -> assertEquals(pedido1.getId(), resultado.get(1).getId()),
                        () -> assertEquals(pedido1.getEndereco(), resultado.get(1).getEndereco()),
                        () -> assertEquals(pedido1.getItens().get(0).getPizza(), resultado.get(1).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido1.getCliente().getNome(), resultado.get(1).getCliente().getNome()),
                        () -> assertEquals(pedido1.getEstabelecimento().getId(), resultado.get(1).getEstabelecimento().getId()),
                        () -> assertEquals(pedido1.getTotal(), resultado.get(1).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por pedidos recebidos")
            void PedidoController_DevePassar_quandoClienteBuscaPedidosRecebidos() throws Exception {
                // Arrange
                List<ItemVenda> itensVenda = new ArrayList<>();
                itensVenda.add(itemVenda1);
                itensVenda.add(itemVenda2);

                pedido2 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                pedidoRepository.save(pedido2);
                StatePedido pedidoRecebido2 = PedidoRecebido.builder().build();
                pedido2.setStatus(pedidoRecebido2);
                pedidoRecebido2.setPedido(pedido2);
                pedidoRecebido2.setOrderNumber(1);

                sleep(2);

                pedido1 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();


                pedido1 = pedidoRepository.save(pedido1);
                StatePedido pedidoRecebido1 = PedidoRecebido.builder().build();
                pedido1.setStatus(pedidoRecebido1);
                pedidoRecebido1.setPedido(pedido1);
                pedidoRecebido1.setOrderNumber(1);
                pedido1.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido1.confirmaPagamento();
                pedido1.terminoPreparo();
                pedido1.setEntregador(entregador);
                pedido1.atribuidoEntregador();
                pedido1.clienteConfirmaEntrega();

                sleep(2);

                pedido = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRecebido.setOrderNumber(1);
                pedido = pedidoRepository.save(pedido);

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/status")
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("status", "recebidos"))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(2, resultado.size()),
                        () -> assertEquals(pedido.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedido.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedido.getTotal(), resultado.get(0).getTotal()),
                        () -> assertEquals(pedido2.getId(), resultado.get(1).getId()),
                        () -> assertEquals(pedido2.getEndereco(), resultado.get(1).getEndereco()),
                        () -> assertEquals(pedido2.getItens().get(0).getPizza(), resultado.get(1).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido2.getCliente().getNome(), resultado.get(1).getCliente().getNome()),
                        () -> assertEquals(pedido2.getEstabelecimento().getId(), resultado.get(1).getEstabelecimento().getId()),
                        () -> assertEquals(pedido2.getTotal(), resultado.get(1).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por pedidos em preparo")
            void PedidoController_DevePassar_quandoClienteBuscaPedidosEmPreparo() throws Exception {
                // Arrange
                List<ItemVenda> itensVenda = new ArrayList<>();
                itensVenda.add(itemVenda1);
                itensVenda.add(itemVenda2);

                pedido2 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                pedidoRepository.save(pedido2);
                StatePedido pedidoRecebido2 = PedidoRecebido.builder().build();
                pedido2.setStatus(pedidoRecebido2);
                pedidoRecebido2.setPedido(pedido2);
                pedidoRecebido2.setOrderNumber(1);
                pedido2.confirmaPagamento();
                pedido2.terminoPreparo();
                pedido2.setEntregador(entregador);
                pedido2.atribuidoEntregador();

                sleep(2);

                pedido1 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();


                pedido1 = pedidoRepository.save(pedido1);
                StatePedido pedidoRecebido1 = PedidoRecebido.builder().build();
                pedido1.setStatus(pedidoRecebido1);
                pedidoRecebido1.setPedido(pedido1);
                pedidoRecebido1.setOrderNumber(1);
                pedido1.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido1.confirmaPagamento();

                sleep(2);

                pedido = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRecebido.setOrderNumber(1);
                pedido = pedidoRepository.save(pedido);
                pedido.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido.confirmaPagamento();

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/status")
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("status", "em preparo"))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(2, resultado.size()),
                        () -> assertEquals(pedido.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedido.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedido.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedido.getTotal(), resultado.get(0).getTotal()),
                        () -> assertEquals(pedido1.getId(), resultado.get(1).getId()),
                        () -> assertEquals(pedido1.getEndereco(), resultado.get(1).getEndereco()),
                        () -> assertEquals(pedido1.getItens().get(0).getPizza(), resultado.get(1).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido1.getCliente().getNome(), resultado.get(1).getCliente().getNome()),
                        () -> assertEquals(pedido1.getEstabelecimento().getId(), resultado.get(1).getEstabelecimento().getId()),
                        () -> assertEquals(pedido1.getTotal(), resultado.get(1).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por pedidos em rota")
            void PedidoController_DevePassar_quandoClienteBuscaPedidosEmRota() throws Exception {
                // Arrange
                List<ItemVenda> itensVenda = new ArrayList<>();
                itensVenda.add(itemVenda1);
                itensVenda.add(itemVenda2);

                pedido2 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                pedidoRepository.save(pedido2);
                StatePedido pedidoRecebido2 = PedidoRecebido.builder().build();
                pedido2.setStatus(pedidoRecebido2);
                pedidoRecebido2.setPedido(pedido2);
                pedidoRecebido2.setOrderNumber(1);
                pedido2.confirmaPagamento();
                pedido2.terminoPreparo();
                pedido2.setEntregador(entregador);
                pedido2.atribuidoEntregador();

                sleep(2);

                pedido1 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();


                pedido1 = pedidoRepository.save(pedido1);
                StatePedido pedidoRecebido1 = PedidoRecebido.builder().build();
                pedido1.setStatus(pedidoRecebido1);
                pedidoRecebido1.setPedido(pedido1);
                pedidoRecebido1.setOrderNumber(1);
                pedido1.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido1.confirmaPagamento();
                pedido1.terminoPreparo();
                pedido1.setEntregador(entregador);
                pedido1.atribuidoEntregador();
                pedido1.clienteConfirmaEntrega();

                sleep(2);

                pedido = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRecebido.setOrderNumber(1);
                pedido = pedidoRepository.save(pedido);
                pedido.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido.confirmaPagamento();
                pedido.terminoPreparo();
                pedido.setEntregador(entregador);
                pedido.atribuidoEntregador();
                pedido.clienteConfirmaEntrega();

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/status")
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("status", "em rota"))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(1, resultado.size()),
                        () -> assertEquals(pedido2.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedido2.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido2.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido2.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedido2.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedido2.getTotal(), resultado.get(0).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por pedidos prontos")
            void PedidoController_DevePassar_quandoClienteBuscaPedidosProntos() throws Exception {
                // Arrange
                List<ItemVenda> itensVenda = new ArrayList<>();
                itensVenda.add(itemVenda1);
                itensVenda.add(itemVenda2);

                pedido2 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                pedidoRepository.save(pedido2);
                StatePedido pedidoRecebido2 = PedidoRecebido.builder().build();
                pedido2.setStatus(pedidoRecebido2);
                pedidoRecebido2.setPedido(pedido2);
                pedidoRecebido2.setOrderNumber(1);
                pedido2.confirmaPagamento();
                pedido2.terminoPreparo();

                sleep(2);

                pedido1 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();


                pedido1 = pedidoRepository.save(pedido1);
                StatePedido pedidoRecebido1 = PedidoRecebido.builder().build();
                pedido1.setStatus(pedidoRecebido1);
                pedidoRecebido1.setPedido(pedido1);
                pedidoRecebido1.setOrderNumber(1);
                pedido1.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido1.confirmaPagamento();
                pedido1.terminoPreparo();
                pedido1.setEntregador(entregador);
                pedido1.atribuidoEntregador();
                pedido1.clienteConfirmaEntrega();

                sleep(2);

                pedido = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRecebido.setOrderNumber(1);
                pedido = pedidoRepository.save(pedido);
                pedido.setPagamento(Pagamento.builder().tipoPagamento(Pix.builder().build()).valorPagamento(pedido.getTotal()).pago(true).build());
                pedido.confirmaPagamento();
                pedido.terminoPreparo();
                pedido.setEntregador(entregador);
                pedido.atribuidoEntregador();
                pedido.clienteConfirmaEntrega();

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/status")
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("status", "prontos"))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(1, resultado.size()),
                        () -> assertEquals(pedido2.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedido2.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedido2.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedido2.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedido2.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedido2.getTotal(), resultado.get(0).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por pedidos inválido")
            void PedidoController_DeveFalhar_quandoClienteBuscaPedidosFiltroInvalido() throws Exception {
                // Arrange
                List<ItemVenda> itensVenda = new ArrayList<>();
                itensVenda.add(itemVenda1);
                itensVenda.add(itemVenda2);

                pedido2 = Pedido.builder()
                        .cliente(cliente)
                        .estabelecimento(estabelecimento)
                        .endereco(cliente.getEndereco())
                        .entregador(entregador)
                        .itens(itensVenda)
                        .build();
                pedidoRepository.save(pedido2);
                StatePedido pedidoRecebido2 = PedidoRecebido.builder().build();
                pedido2.setStatus(pedidoRecebido2);
                pedidoRecebido2.setPedido(pedido2);
                pedidoRecebido2.setOrderNumber(1);
                pedido2.confirmaPagamento();
                pedido2.terminoPreparo();
                pedido2.setEntregador(entregador);
                pedido2.atribuidoEntregador();

                pedidoRepository.flush();
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/status")
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("status", "cancelado"))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
                // Assert
                assertAll(
                        () -> assertEquals("Status de pedido inválido!", resultado.getMessage())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por todos seus pedidos com cliente inválido")
            void PedidoController_DeveFalhar_quandoClienteBuscaTodosPedidosComClienteInvalido() throws Exception {
                // Arrange
                pedido = pedidoRepository.save(pedido);
                pedido1 = pedidoRepository.save(pedido1);
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS)
                                .param("clienteId", "9999999")
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
                // Assert
                assertAll(
                        () -> assertEquals("Cliente não existe!", resultado.getMessage())
                );
            }


            @Test
            @DisplayName("Quando um cliente busca por todos seus pedidos com código de acesso inválido")
            void PedidoController_DeveFalhar_quandoClienteBuscaTodosPedidosComCodigoDeAcessoInvalido() throws Exception {
                // Arrange
                pedido = pedidoRepository.save(pedido);
                pedido1 = pedidoRepository.save(pedido1);
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", "99999999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por todos seus pedidos com pedidos vazios")
            void PedidoController_DevePassar_quandoClienteBuscaTodosPedidosComPedidosVazios() throws Exception {
                // Arrange

                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });
                // Assert
                assertAll(
                        () -> assertEquals(0, resultado.size())
                );
            }
        }
        @Nested
        @DisplayName("Teste de cliente pegar um pedido dele (getOne)")
        class VerificacaoClienteBuscaUmPedido {
            @Test
            @DisplayName("Quando um cliente busca por um pedido seu")
            void PedidoController_DevePassar_quandoClienteBuscaPedidoPeloId() throws Exception {
                // Arrange
                pedido = pedidoRepository.save(pedido);

                EstabelecimentoResponseDTO estabelecimentoResponseDTO = modelMapper.map(pedido.getEstabelecimento(), EstabelecimentoResponseDTO.class);
                estabelecimentoResponseDTO.setCodigoAcesso(null);
                ClienteGetResponseDTO clienteGetResponseDTO = modelMapper.map(pedido.getCliente(), ClienteGetResponseDTO.class);
                clienteGetResponseDTO.setEndereco(null);
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                // Assert

                assertAll(
                        () -> assertNotNull(resultado.getId()),
                        () -> assertEquals(pedidoPostPutRequestDTO.getEndereco(), resultado.getEndereco()),
                        () -> assertEquals(pedidoPostPutRequestDTO.getItens().toString(), resultado.getItens().toString()),
                        () -> assertEquals(clienteGetResponseDTO, resultado.getCliente()),
                        () -> assertEquals(estabelecimentoResponseDTO, resultado.getEstabelecimento()),
                        () -> assertEquals(pedido.getTotal(), resultado.getTotal())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por um pedido com id do pedido inválido")
            void PedidoController_DeveFalhar_quandoClienteBuscaPedidoComIdInvalido() throws Exception {
                // Arrange
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + "999999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals("O pedido consultado nao existe!", resultado.getMessage())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por um pedido com id do cliente inválido")
            void PedidoController_DeveFalhar_quandoClienteBuscaPedidoComIdDoClienteInvalido() throws Exception {
                // Arrange
                pedido = pedidoRepository.save(pedido);
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", "9999999")
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals("Cliente não existe!", resultado.getMessage())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por um pedido com código de acesso do cliente inválido")
            void PedidoController_DeveFalhar_quandoClienteBuscaPedidoComCodigoAcessoDoClienteInvalido() throws Exception {
                // Arrange
                pedido = pedidoRepository.save(pedido);
                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", "999999"))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
                );
            }

            @Test
            @DisplayName("Quando um cliente busca por um pedido que não é seu")
            void PedidoController_DeveFalhar_quandoClienteBuscaPedidoQueNaoESeu() throws Exception {
                // Arrange
                pedido = pedidoRepository.save(pedido);

                Endereco end = Endereco.builder()
                        .cep("55555-55")
                        .numero(51)
                        .complemento("Rua De Fora")
                        .cliente(cliente)
                        .build();

                Cliente cliente2 = clienteRepository.save(Cliente.builder()
                        .nome("Zé Fulano")
                        .endereco(end)
                        .codigoAcesso("888888")
                        .build());

                // Act
                String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente2.getId().toString())
                                .param("codigoAcesso", cliente2.getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals("Cliente não pertence a esse pedido!", resultado.getMessage())
                );
            }
        }

        @Nested
        @DisplayName("Teste de estabelecimento buscando pedido(getOne e GetAll)")
        class VerificacaoEstabelecimentoBuscaPedido {
            @Test
            @DisplayName("Quando um estabelecimento busca todos os pedidos feitos nele")
            void PedidoController_DevePassar_quandoEstabelecimentoBuscaTodosPedidos() throws Exception {
                // Arrange
                Pedido pedidoUm = pedidoRepository.save(pedido);
                Pedido pedidoDois = pedidoRepository.save(pedido1);

                // Act
                String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento.getId() + URI_PEDIDOS)
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pedidoPostPutRequestDTO)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });

                // Assert
                assertAll(
                        () -> assertEquals(2, resultado.size()),
                        () -> assertEquals(pedidoUm.getId(), resultado.get(0).getId()),
                        () -> assertEquals(pedidoUm.getEndereco(), resultado.get(0).getEndereco()),
                        () -> assertEquals(pedidoUm.getItens().get(0).getPizza(), resultado.get(0).getItens().get(0).getPizza()),
                        () -> assertEquals(pedidoUm.getCliente().getNome(), resultado.get(0).getCliente().getNome()),
                        () -> assertEquals(pedidoUm.getEstabelecimento().getId(), resultado.get(0).getEstabelecimento().getId()),
                        () -> assertEquals(pedidoUm.getTotal(), resultado.get(0).getTotal()),
                        () -> assertEquals(pedidoDois.getId(), resultado.get(1).getId()),
                        () -> assertEquals(pedidoDois.getEndereco(), resultado.get(1).getEndereco()),
                        () -> assertEquals(pedidoDois.getItens().get(0).getPizza(), resultado.get(1).getItens().get(0).getPizza()),
                        () -> assertEquals(pedidoDois.getCliente().getNome(), resultado.get(1).getCliente().getNome()),
                        () -> assertEquals(pedidoDois.getEstabelecimento().getId(), resultado.get(1).getEstabelecimento().getId()),
                        () -> assertEquals(pedidoDois.getTotal(), resultado.get(1).getTotal())
                );
            }

            @Test
            @DisplayName("Quando um estabelecimento busca por um pedido feito nele salvo pelo id primeiro")
            void PedidoController_DevePassar_quandoEstabelecimentoBuscaPedidoPorId() throws Exception {
                // Arrange
                Pedido pedidoUm = pedidoRepository.save(pedido);
                Pedido pedidoDois = pedidoRepository.save(pedido1);

                // Act
                String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento.getId() + URI_PEDIDOS + "/" + pedido.getId())
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                // Assert
                assertAll(
                        () -> assertNotNull(resultado.getId()),
                        () -> assertEquals(pedidoUm.getId(), resultado.getId()),
                        () -> assertEquals(pedidoUm.getEndereco(), resultado.getEndereco()),
                        () -> assertEquals(pedidoUm.getItens().get(0).getPizza(), resultado.getItens().get(0).getPizza()),
                        () -> assertEquals(pedidoUm.getCliente().getNome(), resultado.getCliente().getNome()),
                        () -> assertEquals(pedidoUm.getEstabelecimento().getId(), resultado.getEstabelecimento().getId()),
                        () -> assertEquals(pedidoUm.getTotal(), resultado.getTotal()),
                        () -> assertEquals(pedidoPostPutRequestDTO.getEndereco(), resultado.getEndereco()),
                        () -> assertEquals(pedidoPostPutRequestDTO.getItens().get(0).getPizza(), resultado.getItens().get(0).getPizza()),
                        () -> assertEquals(pedido.getTotal(), resultado.getTotal())
                );
            }

            @Test
            @DisplayName("Quando um estabelecimento busca por um pedido feito nele salvo pelo id inexistente")
            void PedidoController_DeveFalhar_quandoEstabelecimentoBuscaPedidoInexistente() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()

                // Act
                String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento.getId() + URI_PEDIDOS + "/" + "11111111")
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertEquals("O pedido consultado nao existe!", resultado.getMessage());
            }

            @Test
            @DisplayName("Quando um estabelecimento invalido busca por id")
            void PedidoController_DeveFalhar_quandoEstabelecimentoInvalidoBuscaPedido() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()
                Pedido pedidoUm = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/684646" + URI_PEDIDOS + "/" + pedidoUm.getId())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertEquals("O estabelecimento nao existe!", resultado.getMessage());
            }

            @Test
            @DisplayName("Quando um estabelecimento busca por um pedido feito em outro estabelecimento")
            void PedidoController_DeveFalhar_quandoEstabelecimentoBuscaPedidoDeOutroEstabelecimento() throws Exception {
                // Arrange
                pedidoRepository.save(pedido);
                Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                        .codigoAcesso("121212")
                        .build());

                // Act
                String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento1.getId() + URI_PEDIDOS + "/" + pedido.getId())
                                .param("codigoAcesso", estabelecimento1.getCodigoAcesso())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertEquals("Pedido não pertence a este estabelecimento!", resultado.getMessage());
            }

            @Test
            @DisplayName("Quando um estabelecimento busca por um pedido feito em outro estabelecimento")
            void PedidoController_DeveFalhar_quandoEstabelecimentoBuscaPedidoCodigoAcessoInvalido() throws Exception {
                // Arrange
                Pedido pedidoUm = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(get(URI_ESTABELECIMENTO + "/" + estabelecimento.getId() + URI_PEDIDOS + "/" + pedido.getId())
                                .param("codigoAcesso", "252525")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertEquals("Código de acesso inválido!", resultado.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Conjunto de Verificações para Delete de Pedido")
    class VerificacoesDeletePedido{
        @Test
        @DisplayName("Quando um cliente cancela um pedido no status Recebido feito por ele salvo")
        void PedidoController_DevePassar_quandoClienteCancelaPedidoRecebidoSalvo() throws Exception {
            // Arrange
            pedido.setStatus(new PedidoRecebido());
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando um cliente cancela um pedido no status EmPreparo feito por ele salvo")
        void PedidoController_DevePassar_quandoClienteCancelaPedidoEmPreparoSalvo() throws Exception {
            // Arrange
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando um cliente cancelar um pedido inexistente")
        void PedidoController_DeveFalhar_quandoClienteCancelaPedidoInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + "999999" + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O pedido consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um cliente cancelar um pedido com código de acesso inválido")
        void PedidoController_DeveFalhar_quandoClienteCancelaPedidoCodigoAcessoInvalido() throws Exception {
            // Arrange
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId().toString() + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", "999999"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um cliente cancelar um pedido com id do cliente inválido")
        void PedidoController_DeveFalhar_quandoClienteCancelaPedidoComClienteInvalido() throws Exception {
            // Arrange
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", "9999999")
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Cliente não existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um cliente cancelar um pedido que não pertence a ele")
        void PedidoController_DeveFalhar_quandoClienteCancelaPedidoQueNaoPertenceAEle() throws Exception {
            // Arrange
            Endereco end1 = Endereco.builder()
                    .cep("123456")
                    .complemento("Logo ali")
                    .numero(12)
                    .build();
            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Seu zé")
                    .endereco(end1)
                    .codigoAcesso("12345")
                    .build());

            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente2.getId().toString())
                            .param("codigoAcesso", cliente2.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Cliente não pertence a esse pedido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um cliente tenta cancelar um pedido que está no status pronto")
        void PedidoController_DeveFalhar_quandoClienteCancelaPedidoStatePronto() throws Exception {
            // Arrange;
            pedido.setStatus(new PedidoPronto());
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O pedido já está pronto, não pode ser cancelado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um cliente tenta cancelar um pedido que está no status em rota")
        void PedidoController_DeveFalhar_quandoClienteCancelaPedidoStateEmRota() throws Exception {
            // Arrange
            pedido.setStatus(new PedidoEmRota());
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O pedido já está pronto, não pode ser cancelado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um cliente tenta cancelar um pedido que está no status entregue")
        void PedidoController_DeveFalhar_quandoClienteCancelaPedidoStateEntregue() throws Exception {
            // Arrange
            pedido.setStatus(new PedidoEntregue());
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O pedido já está pronto, não pode ser cancelado!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um estabelecimento excluí um pedido feito por ele salvo")
        void PedidoController_DevePassar_quandoEstabelecimentoExcluiPedidoSalvo() throws Exception {
            // Arrange
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/estabelecimento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando um estabelecimento excluir um pedido inexistente")
        void PedidoController_DeveFalhar_quandoEstabelecimentoExcluiPedidoInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + "999999" + "/estabelecimento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O pedido consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um estabelecimento excluir um pedido com código de acesso inválido")
        void PedidoController_DeveFalhar_quandoEstabelecimentoExcluiPedidoCodigoAcessoInvalido() throws Exception {
            // Arrange
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId().toString() + "/estabelecimento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcesso", "999999"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um estabelecimento excluir um pedido com id do estabelecimento inválido")
        void PedidoController_DeveFalhar_quandoEstabelecimentoExcluiPedidoComEstabelecimentoInvalido() throws Exception {
            // Arrange
            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/estabelecimento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", "9999999")
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O estabelecimento nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando um estabelecimento excluir um pedido que não pertence a ele")
        void PedidoController_DeveFalhar_quandoEstabelecimentoExcluiPedidoQueNaoPertenceAEle() throws Exception {
            // Arrange
            Estabelecimento estabelecimento2 = estabelecimentoRepository.save(Estabelecimento.builder().codigoAcesso("123565").build());

            pedido = pedidoRepository.save(pedido);
            // Act
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + "/estabelecimento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento2.getId().toString())
                            .param("codigoAcesso", estabelecimento2.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Pedido não pertence a este estabelecimento!", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de Verificações para Patch de Pedido")
    class VerificacoePatchPedido{
        @Nested
        @DisplayName("Conjunto de verificação de tipos de pagamento")
        class VerificacoesTiposPagamento {
            @Test
            @DisplayName("Quando confirmamos o pagamento de um pedido com PIX passando dados válidos")
            void PedidoController_DevePassar_quandoConfirmamosPagamentoDePedidoValidoPix() throws Exception {
                // Arrange

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRepository.save(pedido);

                Character pagamentoPix = 'P';
                PedidoPatchRequestDTO pedidoPatchRequestDTO = PedidoPatchRequestDTO.builder()
                        .tipoPagamento(pagamentoPix)
                        .build();
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("pedidoId", pedido.getId().toString())
                                .content(objectMapper.writeValueAsString(pedidoPatchRequestDTO)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                JSONObject jsonObject = new JSONObject(responseJsonString);
                String valorPagamentoString = jsonObject.getJSONObject("pagamento").getString("valorPagamento");

                Double valorExpected = pedido.getTotal() * 0.95;
                Double valorResultado = Double.parseDouble(valorPagamentoString);

                // Assert
                assertAll(
                        () -> assertEquals(valorExpected, valorResultado),
                        () -> assertEquals(pedido.getId(), resultado.getId()),
                        () -> assertTrue(resultado.getPagamento().isPago())
                );
            }

            @Test
            @DisplayName("Quando confirmamos o pagamento de um pedido com Cartao de credito passando dados válidos")
            void PedidoController_DevePassar_quandoConfirmamosPagamentoDePedidoValidoCartaoCredito() throws Exception {
                // Arrange

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);
                pedidoRepository.save(pedido);

                Character pagamentoCartaoCredito = 'C';
                PedidoPatchRequestDTO pedidoPatchRequestDTO = PedidoPatchRequestDTO.builder().tipoPagamento(pagamentoCartaoCredito).build();
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("pedidoId", pedido.getId().toString())
                                .content(objectMapper.writeValueAsString(pedidoPatchRequestDTO)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                JSONObject jsonObject = new JSONObject(responseJsonString);
                String valorPagamentoString = jsonObject.getJSONObject("pagamento").getString("valorPagamento");

                Double valorExpected = pedido.getTotal();
                Double valorResultado = Double.parseDouble(valorPagamentoString);


                // Assert
                assertAll(
                        () -> assertEquals(valorExpected, valorResultado),
                        () -> assertEquals(pedido.getId(), resultado.getId()),
                        () -> assertTrue(resultado.getPagamento().isPago())
                );
            }

            @Test
            @DisplayName("Quando confirmamos o pagamento de um pedido com Cartao de debito passando dados válidos")
            void PedidoController_DevePassar_quandoConfirmamosPagamentoDePedidoValidoCartaoDebito() throws Exception {
                // Arrange
                ItemVenda itemVenda1 = ItemVenda.builder()
                        .pizza(pizzaM)
                        .quantidade(1)
                        .build();

                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);

                pedidoRepository.save(pedido);


                Character pagamentoCartaoDebito = 'D';
                PedidoPatchRequestDTO pedidoPatchRequestDTO = PedidoPatchRequestDTO.builder().tipoPagamento(pagamentoCartaoDebito).build();
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("pedidoId", pedido.getId().toString())
                                .content(objectMapper.writeValueAsString(pedidoPatchRequestDTO)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                JSONObject jsonObject = new JSONObject(responseJsonString);
                String valorPagamentoString = jsonObject.getJSONObject("pagamento").getString("valorPagamento");

                Double valorExpected = pedido.getTotal() * 0.975;
                Double valorResultado = Double.parseDouble(valorPagamentoString);

                // Assert
                assertAll(
                        () -> assertEquals(valorExpected, valorResultado),
                        () -> assertEquals(pedido.getId(), resultado.getId()),
                        () -> assertTrue(resultado.getPagamento().isPago())
                );
            }

            @Test
            @DisplayName("Quando confirmamos o pagamento de um pedido com PIX passando dados válidos, o status deve mudar para em preparo")
            void PedidoController_DevePassar_quandoConfirmamosPagamentoDePedidoValidoPixChecaStatus() throws Exception {
                // Arrange
                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);

                pedidoRepository.save(pedido);

                Character pagamentoPix = 'P';
                PedidoPatchRequestDTO pedidoPatchRequestDTO = PedidoPatchRequestDTO.builder()
                        .tipoPagamento(pagamentoPix)
                        .build();
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso())
                                .param("pedidoId", pedido.getId().toString())
                                .content(objectMapper.writeValueAsString(pedidoPatchRequestDTO)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                // Assert
                assertAll(
                        () -> assertTrue(resultado.getStatus() instanceof PedidoEmPreparo)
                );
            }
        }

        @Nested
        @DisplayName("Teste de pedido entregue")
        public class VerificacoesPedidoEntregue {
            @Test
            @DisplayName("Quando confirmamos a entrega de um pedido com sucesso")
            void PedidoController_DevePassar_quandoConfirmamosEntregaPedidoComStatus() throws Exception {
                // Arrange
                estabelecimento.setEmail("campinafoood@gmail.com");
                StatePedido pedidoEmRota = PedidoEmRota.builder().build();
                pedido2.setStatus(pedidoEmRota);
                pedidoEmRota.setPedido(pedido2);

                Pedido pedido3 = pedidoRepository.save(pedido2);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido3.getId() + "/entrega")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente1.getId().toString())
                                .param("codigoAcesso", cliente1.getCodigoAcesso()))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                // Assert
                assertAll(
                        () -> assertTrue(resultado.getStatus() instanceof PedidoEntregue)
                );
            }

            @Test
            @DisplayName("Quando Tentamos mudar o status para entregue caso o status anterior já seja Pedido entregue")
            void PedidoController_DeveFalhar_SeOStatusAnteriorForPedidoEntregue() throws Exception {
                // Arrange
                StatePedido pedidoEntregue = PedidoEntregue.builder().build();
                pedido.setStatus(pedidoEntregue);
                pedidoEntregue.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/entrega")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "Mudança de estado inválida!")
                );
            }

            @Test
            @DisplayName("Quando Tentamos mudar o status para entregue caso o status anterior seja Pedido Em Preparo")
            void PedidoController_DeveFalhar_SeOStatusAnteriorForPedidoEmPreparo() throws Exception {
                // Arrange
                StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/entrega")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "Mudança de estado inválida!")
                );
            }

            @Test
            @DisplayName("Quando passamos cliente com Id inválido")
            void PedidoController_DeveFalhar_SeOIdDoClienteForInvalidoNaEntregaDoPedido() throws Exception {
                // Arrange
                StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/entrega")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", "999999")
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "Cliente não existe!")
                );
            }

            @Test
            @DisplayName("Deve falhar quando o codigo de acesso for invalido")
            void PedidoController_DeveFalhar_SeOCodigoDeAcessoForInvalidoNaEntregaDoPedido() throws Exception {
                // Arrange
                StatePedido pedidoEmRota = PedidoEmRota.builder().build();
                pedido.setStatus(pedidoEmRota);
                pedidoEmRota.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/entrega")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", "invalido"))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "Código de acesso inválido!")
                );
            }

            @Test
            @DisplayName("Quando passamos o ID de um pedido invalido")
            void PedidoController_DeveFalhar_SeOIdDoPedidoForInvalidoNaEntregaDoPedido() throws Exception {
                // Arrange
                StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/9999999/entrega")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "O pedido consultado nao existe!")
                );
            }

            @Test
            @DisplayName("Quando Tentamos mudar o status para entregue porém o pedido não é desse cliente")
            void PedidoController_DeveFalhar_deveFalharSeOPedidoNaoForDoCliente() throws Exception {
                // Arrange
                StatePedido pedidoEmRota = PedidoEmRota.builder().build();
                pedido.setStatus(pedidoEmRota);
                pedidoEmRota.setPedido(pedido);

                pedido2.setStatus(pedidoEmRota);

                Pedido pedido3 = pedidoRepository.save(pedido2);
                pedidoRepository.save(pedido);
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido3.getId() + "/entrega")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("clienteId", cliente.getId().toString())
                                .param("codigoAcesso", cliente.getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "Cliente não pertence a esse pedido!")
                );
            }
        }
        @Nested
        @DisplayName("Testes de pedido pronto")
        public class VerificacoesPedidoPronto {
            @Test
            @DisplayName("Quando o estabelecimento indica o termino de um pedido com sucesso")
            void PedidoController_DevePassar_quandoEstabelecimentoIndicaPedidoPronto() throws Exception {
                // Arrange
                StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/status-pronto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", pedido.getEstabelecimento().getId().toString())
                                .param("codigoAcesso", pedido.getEstabelecimento().getCodigoAcesso()))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                // Assert
                assertAll(
                        () -> assertTrue(resultado.getStatus() instanceof PedidoPronto)
                );
            }

            @Test
            @DisplayName("Quando o estabelecimento indica pedido pronto a um pedido que já está pronto")
            void PedidoController_DeveFalhar_quandoEstabelecimentoIndicaProntoAPedidoJaPronto() throws Exception {
                // Arrange
                StatePedido pedidoEmPreparo = PedidoPronto.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/status-pronto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", pedido.getEstabelecimento().getId().toString())
                                .param("codigoAcesso", pedido.getEstabelecimento().getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "Mudança de estado inválida!")
                );
            }

            @Test
            @DisplayName("Quando o estabelecimento indica pedido pronto passando id de pedido invalido")
            void PedidoController_DeveFalhar_quandoEstabelecimentoIndicaProntoComPedidoIdInvalido() throws Exception {
                // Arrange
                StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + 1000 + "/status-pronto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", pedido.getEstabelecimento().getId().toString())
                                .param("codigoAcesso", pedido.getEstabelecimento().getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "O pedido consultado nao existe!")
                );
            }

            @Test
            @DisplayName("Quando o estabelecimento indica pedido pronto passando id de estabelecimento invalido")
            void PedidoController_DeveFalhar_quandoEstabelecimentoIndicaProntoComEstabelecimentoIdInvalido() throws Exception {
                // Arrange
                StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);
                Long estabId = pedido.getEstabelecimento().getId() + 122;
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/status-pronto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabId.toString())
                                .param("codigoAcesso", pedido.getEstabelecimento().getCodigoAcesso()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "O estabelecimento nao existe!")
                );
            }

            @Test
            @DisplayName("Quando o estabelecimento indica pedido pronto passando codigoAcesso invalido")
            void PedidoController_DeveFalhar_quandoEstabelecimentoIndicaProntoComCodigoAcessoInvalido() throws Exception {
                // Arrange
                StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/status-pronto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", pedido.getEstabelecimento().getId().toString())
                                .param("codigoAcesso", "330001"))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();


                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getMessage(), "Código de acesso inválido!")
                );
            }
        }
        @Nested
        @DisplayName("Casos de alteração do status de um pedido")
        public class VerificacoesAlteracaoEstadoPedido {
            @Test
            @DisplayName("quando atribuimos um entregador a um pedido com sucesso")
            void PedidoController_DevePassar_quandoAtribuimosEntregadorParaPedidoComSucesso() throws Exception {
                StatePedido pedidoPronto = PedidoPronto.builder().build();
                pedido.setStatus(pedidoPronto);
                pedidoPronto.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

                assertEquals(modelMapper.map(entregador, EntregadorResponseDTO.class), resultado.getEntregador());
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador a um pedido com o código do estabelecimento inválido")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorCodigoEstabelecimentoInvalido() throws Exception{
                StatePedido pedidoPronto = PedidoPronto.builder().build();
                pedido.setStatus(pedidoPronto);
                pedidoPronto.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", "0000000")
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "Código de acesso inválido!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador indisponível a um pedido")
            void quandoAtribuimosEntregadorIndisponivelEmPedido() throws Exception{
                StatePedido pedidoPronto = PedidoPronto.builder().build();
                pedido.setStatus(pedidoPronto);
                pedidoPronto.setPedido(pedido);
                entregador.setDisponibilidade(false);
                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso",estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "Entregador está indisponível para entrega!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador a um pedido inexistente")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorIdPedidoInexistente() throws Exception{
                StatePedido pedidoPronto = PedidoPronto.builder().build();
                pedido.setStatus(pedidoPronto);
                pedidoPronto.setPedido(pedido);
                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/00000000" + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "O pedido consultado nao existe!");

            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador a um pedido que não pertence a um estabelecimento")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorComPedidoInvalido() throws Exception{
                StatePedido pedidoPronto = PedidoPronto.builder().build();
                Pedido pedidoSalvo = pedidoRepository.save(pedido2);

                pedidoSalvo.setStatus(pedidoPronto);
                pedidoPronto.setPedido(pedidoSalvo);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedidoSalvo.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento2.getId().toString())
                                .param("codigoAcesso", estabelecimento2.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "Pedido não pertence a este estabelecimento!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador com um estabelecimento que não existe")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorComEstabelecimentoInexistente() throws Exception{
                StatePedido pedidoPronto = PedidoPronto.builder().build();
                pedido.setStatus(pedidoPronto);
                pedidoPronto.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", "000000")
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "O estabelecimento nao existe!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador inexistente a um pedido.")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorInexistente() throws Exception{
                StatePedido pedidoPronto = PedidoPronto.builder().build();
                pedido.setStatus(pedidoPronto);
                pedidoPronto.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", "0000000000"))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "O entregador consultado nao existe!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador que não pertence a um estabelecimento.")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorIncorreto() throws Exception{
                StatePedido pedidoPronto = PedidoPronto.builder().build();
                pedido.setStatus(pedidoPronto);
                pedidoPronto.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador2.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "O entregador não pertence a este estabelecimento!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador a um pedido que está no estado em preparo.")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorcomPedidoEmPreparo() throws Exception{
                StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
                pedido.setStatus(pedidoEmPreparo);
                pedidoEmPreparo.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "Mudança de estado inválida!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador a um pedido que está no estado em rota.")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorcomPedidoEmRota() throws Exception{
                StatePedido pedidoEmRota = PedidoEmRota.builder().build();
                pedido.setStatus(pedidoEmRota);
                pedidoEmRota.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "Mudança de estado inválida!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador a um pedido que está no estado entregue.")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorcomPedidoEntregue() throws Exception{
                StatePedido pedidoEntregue = PedidoEntregue.builder().build();
                pedido.setStatus(pedidoEntregue);
                pedidoEntregue.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);

                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "Mudança de estado inválida!");
            }

            @Test
            @DisplayName("quando tentamos atribuir um entregador a um pedido que está no estado recebido.")
            void PedidoController_DeveFalhar_quandoAtribuimosEntregadorcomPedidoRecebido() throws Exception{
                StatePedido pedidoRecebido = PedidoRecebido.builder().build();
                pedido.setStatus(pedidoRecebido);
                pedidoRecebido.setPedido(pedido);

                Pedido pedido2 = pedidoRepository.save(pedido);
                // Act
                String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId().toString() + "/atribuir-entregador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("estabelecimentoId", estabelecimento.getId().toString())
                                .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                                .param("entregadorId", entregador.getId().toString()))
                        .andExpect(status().isBadRequest())
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                assertEquals(resultado.getMessage(), "Mudança de estado inválida!");

            }
        }
    }


    @Nested
    @DisplayName("Casos de fila de pedido")
    public class VerificacoesAtribuicaoPedidoAFila {
        @Test
        @DisplayName("quando um pedido fica pronto ele vai para a fila de pedidos prontos do estabelecimento automaticamente - nao tem entregador disponivel ainda")
        void PedidoController_DevePassar_PedidoProntoAtribuidoAFilaComSucesso() throws Exception {
            StatePedido pedidoEmPreparo1 = PedidoEmPreparo.builder().build();
            pedido.setStatus(pedidoEmPreparo1);
            pedidoEmPreparo1.setPedido(pedido);

            StatePedido pedidoEmPreparo2 = PedidoEmPreparo.builder().build();
            pedido1.setStatus(pedidoEmPreparo2);
            pedidoEmPreparo2.setPedido(pedido1);

            Pedido pedidoPreparado = pedidoRepository.save(pedido);
            Pedido pedidoPronto = pedidoRepository.save(pedido1);
            commandOrderService.adicionaPedidoFila(pedidoPronto);

            // Act
            String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedidoPreparado.getId() + "/status-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", pedido.getEstabelecimento().getId().toString())
                            .param("codigoAcesso", "654321"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(1L, pedidoPronto.getFicha()),
                    () -> assertEquals(2L, pedidoPreparado.getFicha()),
                    () -> assertTrue(estabelecimento.getFilaPedidos().contains(pedidoPreparado))
            );
        }

        @Test
        @DisplayName("quando um entregador fica no estado disponível e entra da fila de entregadores.")
        void PedidoController_DevePassar_EntregadorDisponivelAtribuidoAFilaComSucesso() throws Exception {
            // Arrange

            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());

            Veiculo veiculo = Veiculo.builder()
                    .cor("Azul")
                    .placa("ABC-1234")
                    .tipo("Moto")
                    .build();

            Veiculo veiculo2 = Veiculo.builder()
                    .cor("Laranja")
                    .placa("OCO-1234")
                    .tipo("Carro")
                    .build();

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Joãozinho")
                    .veiculo(veiculo)
                    .codigoAcesso("101010")
                    .disponibilidade(false)
                    .build());

            entregadorPatchDto = EntregadorPatchDto.builder().disponibilidade(true).build();

            entregador2 = entregadorRepository.save(Entregador.builder()
                    .nome("Jailson")
                    .veiculo(veiculo2)
                    .codigoAcesso("202020")
                    .disponibilidade(true)
                    .build());

            associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .status(true)
                    .build());

            // Act
            String responseJsonString = driver.perform(patch(URI_ENTREGADORES + "/" + entregador.getId() + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPatchDto))
                            .param("codigoAcesso", "101010"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertTrue(resultado.getDisponibilidade()),
                    () -> assertFalse(estabelecimento.getFilaEntregador().isEmpty()),
                    () -> assertEquals(1, estabelecimento.getFilaEntregador().size()),
                    () -> assertEquals(entregador.getId(), estabelecimento.getFilaEntregador().get(0).getId())

            );
        }

        @Test
        @DisplayName("quando um entregador fica no estado de descanso e sai da fila de entregadores.")
        void PedidoController_DevePassar_EntregadorIndisponivelSaiDaFilaComSucesso() throws Exception {
            // Arrange

            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());

            Veiculo veiculo = Veiculo.builder()
                    .cor("Azul")
                    .placa("ABC-1234")
                    .tipo("Moto")
                    .build();

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Joãozinho")
                    .veiculo(veiculo)
                    .codigoAcesso("101010")
                    .disponibilidade(true)
                    .build());

            entregadorPatchDto = EntregadorPatchDto.builder().disponibilidade(false).build();


            Associacao associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .status(true)
                    .build());


            entregador.getAssociacoes().add(associacao);
            estabelecimento.getFilaEntregador().add(entregador);
            entregador.setFicha(1L);
            entregador.setEstabelecimento(estabelecimento);


            // Act
            String responseJsonString = driver.perform(patch(URI_ENTREGADORES + "/" + entregador.getId() + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPatchDto))
                            .param("codigoAcesso", "101010"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertFalse(resultado.getDisponibilidade()),
                    () -> assertTrue(resultado.getEstabelecimento().getFilaEntregador().isEmpty()),
                    () -> assertNull(resultado.getFicha())
            );
        }

        @Test
        @DisplayName("quando um entregador volta para a fila de entregadores depois de pedido entregue com sucesso.")
        void PedidoController_DevePassar_PedidoEntregueEntregadorVoltaParaFilaComSucesso() throws Exception {
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());

            cliente.setEmail("campinafoood@gmail.com");
            
            Veiculo veiculo = Veiculo.builder()
                    .cor("Azul")
                    .placa("ABC-1234")
                    .tipo("Moto")
                    .build();

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Joãozinho")
                    .veiculo(veiculo)
                    .codigoAcesso("101010")
                    .disponibilidade(true)
                    .build());


            Associacao associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .status(true)
                    .build());


            entregador.getAssociacoes().add(associacao);
            entregador.setFicha(1L);
            entregador.setEstabelecimento(estabelecimento);


            StatePedido pedidoEmPreparo = PedidoEmRota.builder().build();
            pedido.setStatus(pedidoEmPreparo);
            pedidoEmPreparo.setPedido(pedido);
            pedido.setEntregador(entregador);

            Pedido pedido2 = pedidoRepository.save(pedido);
            pedido2.getEstabelecimento().setEmail("campinafoood@gmail.com");

            // Act
            String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido2.getId() + "/entrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteId", cliente.getId().toString())
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .param("pedidoId", pedido2.getId().toString()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            // Assert
            assertAll(
                            () -> assertTrue(resultado.getEntregador().getDisponibilidade()),
                            () -> assertFalse(resultado.getEntregador().getEstabelecimento().getFilaEntregador().isEmpty()),
                            () -> assertEquals(resultado.getEntregador().getFicha(), entregador.getFicha())
            );
        }

        @Test
        @DisplayName("Quando um pedido é mudado para pronto, ele tem que ser atribuido automaticamente a um entregador e mudar o status")
        void PedidoController_DevePassar_QuandoUmPedidoEntraNaFilaDePedidosEleEhAtribuidoAutomaticamenteAUmEntregador() throws Exception {
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());

            Veiculo veiculo = Veiculo.builder()
                    .cor("Azul")
                    .placa("ABC-1234")
                    .tipo("Moto")
                    .build();

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Joãozinho")
                    .veiculo(veiculo)
                    .codigoAcesso("101010")
                    .disponibilidade(true)
                    .build());

            Entregador entregador1 = entregadorRepository.save(Entregador.builder()
                            .nome("Felipe")
                            .veiculo(veiculo)
                            .codigoAcesso("010101")
                            .disponibilidade(true)
                            .build());

            Associacao associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .status(true)
                    .build());

            Associacao associacao1 = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador1)
                    .estabelecimento(estabelecimento)
                    .status(true)
                    .build());

            entregador.getAssociacoes().add(associacao);
            entregador.setFicha(1L);
            entregador.setEstabelecimento(estabelecimento);

            entregador1.getAssociacoes().add(associacao1);
            entregador1.setFicha(2L);
            entregador1.setEstabelecimento(estabelecimento);

            estabelecimento.getFilaEntregador().add(entregador);
            estabelecimento.getFilaEntregador().add(entregador1);

            StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
            pedido.setStatus(pedidoEmPreparo);
            pedidoEmPreparo.setPedido(pedido);
            pedido.setEstabelecimento(estabelecimento);

            Pedido pedido3 = pedidoRepository.save(pedido);


            String responseJsonString = driver.perform(patch(URI_PEDIDOS + "/" + pedido3.getId() + "/status-pronto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.class);

            assertAll(
                    () -> assertEquals(1, resultado.getEntregador().getEstabelecimento().getFilaEntregador().size()),
                    () -> assertTrue(resultado.getEntregador().getEstabelecimento().getFilaPedidos().isEmpty()),
                    () -> assertTrue(resultado.getStatus() instanceof PedidoEmRota)
            );
        }
//
        @Test
        @DisplayName("quando um Entregador disponivel aguarda pedido ficar pronto para entrega.")
        void PedidoController_DevePassar_quandoTemosMultiplosPedidosEmFilaEAtribuimosEntregadores() throws Exception {
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());

            Veiculo veiculo = Veiculo.builder()
                    .cor("Azul")
                    .placa("ABC-1234")
                    .tipo("Moto")
                    .build();

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Joãozinho")
                    .veiculo(veiculo)
                    .codigoAcesso("101010")
                    .disponibilidade(true)
                    .build());

            Entregador entregador1 = entregadorRepository.save(Entregador.builder()
                    .nome("VPedro")
                    .veiculo(veiculo)
                    .codigoAcesso("010101")
                    .disponibilidade(false)
                    .build());

            entregadorPatchDto = EntregadorPatchDto.builder().disponibilidade(true).build();


            Associacao associacao = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .status(true)
                    .build());

            Associacao associacao1 = associacaoRepository.save(Associacao.builder()
                    .entregador(entregador1)
                    .estabelecimento(estabelecimento)
                    .status(true)
                    .build());
            //entregador1.getAssociacoes().add(associacao1);


            entregador.getAssociacoes().add(associacao);
            estabelecimento.getFilaEntregador().add(entregador);
            entregador.setFicha(1L);
            entregador.setEstabelecimento(estabelecimento);


            StatePedido pedidoPronto1 = PedidoPronto.builder().build();
            pedido.setStatus(pedidoPronto1);
            pedidoPronto1.setPedido(pedido);
            pedido.setEstabelecimento(estabelecimento);
            pedido.setFicha(1L);
            pedido = pedidoRepository.save(pedido);
            commandOrderService.adicionaPedidoFila(pedido);

            StatePedido pedidoPronto2 = PedidoPronto.builder().build();
            pedido1.setStatus(pedidoPronto2);
            pedidoPronto2.setPedido(pedido1);
            pedido1.setEstabelecimento(estabelecimento);
            pedido1.setFicha(2L);
            pedido1 = pedidoRepository.save(pedido1);
            commandOrderService.adicionaPedidoFila(pedido1);

            StatePedido pedidoPronto3 = PedidoPronto.builder().build();
            pedido2.setStatus(pedidoPronto3);
            pedidoPronto3.setPedido(pedido2);
            pedido2.setEstabelecimento(estabelecimento);
            pedido2 = pedidoRepository.save(pedido2);
            pedido2.setFicha(3L);
            commandOrderService.adicionaPedidoFila(pedido2);

            StatePedido pedidoPronto4 = PedidoPronto.builder().build();
            pedido3.setStatus(pedidoPronto4);
            pedidoPronto4.setPedido(pedido3);
            pedido3.setEstabelecimento(estabelecimento);
            pedido3.setFicha(4L);
            pedido3 = pedidoRepository.save(pedido3);
            commandOrderService.adicionaPedidoFila(pedido3);


            // Act
            String responseJsonString = driver.perform(patch(URI_ENTREGADORES + "/" + entregador1.getId() + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPatchDto))
                            .param("codigoAcesso", "010101"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getEstabelecimento().getFilaPedidos().get(0), pedido2),
                    () -> assertEquals(resultado.getEstabelecimento().getFilaPedidos().get(1), pedido3),
                    () -> assertTrue(resultado.getEstabelecimento().getFilaEntregador().isEmpty()),
                    () -> assertEquals(pedido.getEntregador().getNome(), entregador.getNome()),
                    () -> assertEquals(pedido1.getEntregador().getNome(), resultado.getNome()),
                    () -> assertNull(entregador.getFicha()),
                    () -> assertNull(resultado.getFicha())
            );
        }
    }
}
