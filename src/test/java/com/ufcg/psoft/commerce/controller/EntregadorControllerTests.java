package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorPatchDto;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoPostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.entregador.Veiculo;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pedido.ItemVenda;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.pedido.statepedido.PedidoEmPreparo;
import com.ufcg.psoft.commerce.model.pizza.PizzaG;
import com.ufcg.psoft.commerce.model.pizza.PizzaM;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.AssociacaoRepository;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.service.associacao.AssociacaoV1PostService;
import com.ufcg.psoft.commerce.service.entregador.EntregadorV1PatchService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Entregadores")
public class EntregadorControllerTests {

    final String URI_ENTREGADORES = "/entregadores";
    @Autowired
    MockMvc driver;
    @Autowired
    EntregadorRepository entregadorRepository;
    ObjectMapper objectMapper = new ObjectMapper();
    Entregador entregador;
    Entregador entregador2;
    EntregadorPostPutRequestDTO entregadorPostPutRequestDTO;
    EntregadorPostPutRequestDTO entregadorPostPutRequestDTO2;
    EntregadorPatchDto entregadorPatchDto1;
    EntregadorPatchDto entregadorPatchDto2;
    Estabelecimento estabelecimento;
    Associacao associacao;

    @Autowired
    EntregadorV1PatchService entregadorV1PatchService;

    @Autowired
    AssociacaoRepository associacaoRepository;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        Veiculo veiculo = Veiculo.builder()
                .placa("ABC-1234")
                .cor("Azul")
                .tipo("moto")
                .build();

        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Lana Del Rey")
                .codigoAcesso("123456")
                .veiculo(veiculo)
                .disponibilidade(true)
                .build()
        );

        entregadorRepository.flush();

        entregador2 = Entregador.builder()
                .nome("Arthur")
                .codigoAcesso("55421")
                .veiculo(veiculo)
                .disponibilidade(false)
                .build();

        entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                .nome(entregador.getNome())
                .codigoAcesso("55247")
                .veiculo(entregador.getVeiculo())
                .disponibilidade(false)
                .build();

        entregadorPostPutRequestDTO2 = EntregadorPostPutRequestDTO.builder()
                .nome(entregador2.getNome())
                .codigoAcesso(entregador2.getCodigoAcesso())
                .veiculo(entregador2.getVeiculo())
                .disponibilidade(false)
                .build();

        entregadorPatchDto1 = EntregadorPatchDto.builder()
                .disponibilidade(true)
                .build();

        entregadorPatchDto2 = EntregadorPatchDto.builder()
                .disponibilidade(false)
                .build();

    }

    @BeforeEach
    void tearDown() {
        associacaoRepository.deleteAll();
        entregadorRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de fluxos básicos API Rest")
    class EntregadorVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando buscamos por todos entregadores salvos")
        void EntregadorController_DevePassar_quandoBuscamosTodosEntregadores() throws Exception {
            // Arrange
            // Vamos ter 3 entregadores no banco
            Veiculo veiculo1 = Veiculo.builder()
                    .placa("GHF-1212")
                    .cor("Prata")
                    .tipo("carro")
                    .build();

            Entregador entregador1 = Entregador.builder()
                    .nome("Jose")
                    .codigoAcesso("654321")
                    .veiculo(veiculo1)
                    .disponibilidade(false)
                    .build();

            Veiculo veiculo2 = Veiculo.builder()
                    .placa("MRD-0217")
                    .cor("Preto")
                    .tipo("carro")
                    .build();

            Entregador entregador2 = Entregador.builder()
                    .nome("Halloran")
                    .codigoAcesso("217217")
                    .veiculo(veiculo2)
                    .disponibilidade(false)
                    .build();
            entregadorRepository.saveAll(Arrays.asList(entregador1, entregador2));

            // Act
            String responseJsonString = driver.perform(get(URI_ENTREGADORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<EntregadorResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertEquals(3, resultado.size());
        }


        @Test
        @DisplayName("Quando buscamos por um entregador salvo pelo id")
        void EntregadorController_DevePassar_quandoBuscamosEntregadorPorId() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(entregador.getId(), resultado.getId()),
                    () -> assertEquals(entregador.getNome(), resultado.getNome()),
                    () -> assertEquals(entregador.getVeiculo(), resultado.getVeiculo())
            );
        }


        @Test
        @DisplayName("Quando buscamos um entregador inexistente")
        void EntregadorController_DeveFalhar_quandoBuscamosEntregadorInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_ENTREGADORES + "/999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O entregador consultado nao existe!", resultado.getMessage());
        }


        @Test
        @DisplayName("Quando criamos um entregador com dados válidos")
        void EntregadorController_DevePassar_quandoCriamosEntregadorValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(post(URI_ENTREGADORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO2)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(entregadorPostPutRequestDTO2.getNome(), resultado.getNome()),
                    () -> assertEquals(entregadorPostPutRequestDTO2.getVeiculo(), resultado.getVeiculo())
            );
        }

        @Test
        @DisplayName("Quando alteramos o entregador com dados válidos")
        void EntregadorController_DevePassar_quandoAlteramosEntregadorValido() throws Exception {
            // Arrange
            Long entregadorId = entregador.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(entregadorId, resultado.getId()),
                    () -> assertEquals(entregadorPostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(entregadorPostPutRequestDTO.getVeiculo(), resultado.getVeiculo())
            );
        }


        @Test
        @DisplayName("Quando alteramos um entregador inexistente")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O entregador consultado nao existe!", resultado.getMessage());
        }


        @Test
        @DisplayName("Quando alteramos um entregador passando um código de acesso inválido")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }


        @Test
        @DisplayName("Quando excluímos um entregador salvo")
        void EntregadorController_DevePassar_quandoExcluimosEntregadorSalvo() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();



            // Assert
            assertTrue(responseJsonString.isBlank());
        }


        @Test
        @DisplayName("Quando excluímos um entregador inexistente")
        void EntregadorController_DeveFalhar_quandoExcluimosEntregadorInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ENTREGADORES + "/999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O entregador consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando excluímos um entregador passando um código de acesso inválido")
        void EntregadorController_DeveFalhar_quandoExcluimosEntregadorComCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }
    }


    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class EntregadorVerificacaoNome {

        @Test
        @DisplayName("Quando alteramos o nome do entregador com dados válidos")
        void EntregadorController_DevePassar_quandoAlteramosNomeDoEntregadorValido() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.setNome("Lana Del Rey Alterada");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertEquals("Lana Del Rey Alterada", resultado.getNome());
        }


        @Test
        @DisplayName("Quando alteramos o entregador com nome vazio")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComNomeVazio() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome não pode ser vazio.", resultado.getErrors().get(0))
            );
        }


        @Test
        @DisplayName("Quando alteramos o entregador passando codigo de acesso inválido")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }
    }


    @Nested
    @DisplayName("Conjunto de casos de verificação de placa")
    class EntregadorVerificacaoPlaca  {

        @Test
        @DisplayName("Quando criamos um entregador com uma placa inválida")
        void EntregadorController_DeveFalhar_quandoCriamosEntregadorComPlacaInvalida() throws  Exception{
            entregadorRepository.deleteAll();
            entregadorPostPutRequestDTO.getVeiculo().setPlaca("");

            String responseJsonString = driver.perform(post(URI_ENTREGADORES)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("codigoAcesso", entregador.getCodigoAcesso())
                    .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Placa do veiculo e obrigatoria", resultado.getErrors().get(0))
            );


        }

        @Test
        @DisplayName("Quando criamos um entregador com placa nula")
        void EntregadorController_DeveFalhar_quandoCriamosUmEntregadorComPlacaNula() throws Exception{
            entregadorRepository.deleteAll();
            entregadorPostPutRequestDTO.getVeiculo().setPlaca(null);

            String responseJsonString = driver.perform(post(URI_ENTREGADORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Placa do veiculo e obrigatoria", resultado.getErrors().get(0))
            );
        }


        @Test
        @DisplayName("Quando alteramos o entregador com placa válida")
        void EntregadorController_DevePassar_quandoAlteramosEntregadorComPlacaValida() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.getVeiculo().setPlaca("DEF-3456");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);
            Veiculo veiculoResultado = resultado.getVeiculo();

            // Assert
            assertEquals("DEF-3456", veiculoResultado.getPlaca());
        }


        @Test
        @DisplayName("Quando alteramos o entregador com placa vazia")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComPlacaVazia() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.getVeiculo().setPlaca("");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Placa do veiculo e obrigatoria", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o entregador passando codigo de acesso inválido")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }
    }


    @Nested
    @DisplayName("Conjunto de casos de verificação de tipo")
    class EntregadorVerificacaoTipo {

        @Test
        @DisplayName("Quando criamos um entregador com tipo de veículo inválido")
        void EntregadorController_DeveFalhar_quandoCriamosUmEntregadorComTipoInvalido() throws Exception {
            entregadorRepository.deleteAll();
            entregadorPostPutRequestDTO.getVeiculo().setTipo("Carroça com 2 burros de potência");

            String responseJsonString = driver.perform(post(URI_ENTREGADORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Tipo do veiculo deve ser moto ou carro", resultado.getErrors().get(0))
            );



        }

        @Test
        @DisplayName("Quando criamos um entregador com tipo de veículo nulo")
        void EntregadorController_DeveFalhar_quandoCriamosUmEntregadorComTipoNulo() throws Exception {
            entregadorRepository.deleteAll();
            entregadorPostPutRequestDTO.getVeiculo().setTipo(null);

            String responseJsonString = driver.perform(post(URI_ENTREGADORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", resultado.getMessage());


        }

        @Test
        @DisplayName("Quando alteramos o entregador com tipo de veiculo válido")
        void EntregadorController_DevePassar_quandoAlteramosEntregadorComTipoVeiculoValido() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.getVeiculo().setTipo("carro");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);
            Veiculo veiculoResultado = resultado.getVeiculo();
            // Assert
            assertEquals("carro", veiculoResultado.getTipo());
        }

        @Test
        @DisplayName("Quando alteramos o entregador com tipo de veiculo nulo")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComTipoVeiculoVazio() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.getVeiculo().setTipo(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("Erros de validacao encontrados", resultado.getMessage());


        }


        @Test
        @DisplayName("Quando alteramos o entregador com tipo de veiculo inválido")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComTipoVeiculoInvalido() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.getVeiculo().setTipo("bicicleta");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Tipo do veiculo deve ser moto ou carro", resultado.getErrors().get(0))
            );

        }


        @Test
        @DisplayName("Quando alteramos o tipo passando codigo de acesso inválido")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de cor do veiculo")
    class EntregadorVerificacaoCorVeiculo {

        @Test
        @DisplayName("Quando alteramos o entregador com cor do veiculo válida")
        void EntregadorController_DevePassar_quandoAlteramosEntregadorComCorVeiculoValida() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.getVeiculo().setCor("preto");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);
            Veiculo veiculoResultado = resultado.getVeiculo();
            // Assert
            assertEquals("preto", veiculoResultado.getCor());
        }


        @Test
        @DisplayName("Quando alteramos o entregador com cor do veiculo vazia")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComCorVeiculoVazia() throws Exception {
            // Arrange
            entregadorPostPutRequestDTO.getVeiculo().setCor("");

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Cor do veiculo e obrigatoria", resultado.getErrors().get(0))
            );


        }


        @Test
        @DisplayName("Quando alteramos a cor do veiculo passando codigo de acesso inválido")
        void EntregadorController_DeveFalhar_quandoAlteramosEntregadorComCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }
    }


    @Nested
    @DisplayName("Conjunto de casos de alteração de disponibilidade do entregador")
    class EntregadorDefinirDisponibilidade {

        @Test
        @DisplayName("Quando alteramos a disponibilidade do entregador para disponível")
        void EntregadorController_DevePassar_quandoAlteramosDisponibilidadeParaDisponivel() throws Exception {
            // Arrange

            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());

            Veiculo veiculo = Veiculo.builder()
                    .placa("ABC-1234")
                    .cor("Azul")
                    .tipo("moto")
                    .build();

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Entregador Um")
                    .veiculo(veiculo)
                    .codigoAcesso("123456")
                    .disponibilidade(false)
                    .build()
            );
            associacao = associacaoRepository.save(Associacao.builder()
                    .estabelecimento(estabelecimento)
                    .entregador(entregador)
                    .status(true)
                    .build()
            );
            estabelecimento.getAssociacoes().add(associacao);

            // Act
            String responseJsonString = driver.perform(patch(URI_ENTREGADORES + "/" + entregador.getId() + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPatchDto1)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertTrue(resultado.getDisponibilidade());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade do entregador para disponível porém não está associado a nada")
        void EntregadorController_DeveFalhar_quandoAlteramosDisponibilidadeParaDisponivelSemAssociacaoDefinida() throws Exception {
            // Arrange

            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());

            Veiculo veiculo = Veiculo.builder()
                    .placa("ABC-1234")
                    .cor("Azul")
                    .tipo("moto")
                    .build();

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Entregador Um")
                    .veiculo(veiculo)
                    .codigoAcesso("123456")
                    .disponibilidade(false)
                    .build()
            );

            // Act
            String responseJsonString = driver.perform(patch(URI_ENTREGADORES + "/" + entregador.getId() + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPatchDto1)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O Entregador não está aprovado em nenhum estabelecimento!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade do entregador para indisponível")
        void EntregadorController_DevePassar_quandoAlteramosDisponibilidadeParaIndisponivel() throws Exception {
            // Arrange
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());

            Veiculo veiculo = Veiculo.builder()
                    .placa("ABC-1234")
                    .cor("Azul")
                    .tipo("moto")
                    .build();

            entregador = entregadorRepository.save(Entregador.builder()
                    .nome("Entregador Um")
                    .veiculo(veiculo)
                    .codigoAcesso("123456")
                    .disponibilidade(true)
                    .build()
            );
            associacao = associacaoRepository.save(Associacao.builder()
                    .estabelecimento(estabelecimento)
                    .entregador(entregador)
                    .status(true)
                    .build()
            );

            estabelecimento.getAssociacoes().add(associacao);
            entregador.setFicha(1L);
            estabelecimento.getFilaEntregador().add(entregador);
            // Act
            String responseJsonString = driver.perform(patch(URI_ENTREGADORES + "/" + entregador.getId() + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPatchDto2)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, EntregadorResponseDTO.class);

            // Assert
            assertFalse(resultado.getDisponibilidade());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade de um entregador inexistente")
        void EntregadorController_DeveFalhar_quandoAlteramosDisponibilidadeDeEntregadorInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(patch(URI_ENTREGADORES + "/" + 999999 + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(entregadorPatchDto1)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O entregador consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade de um entregador passando codigo de acesso inválido")
        void EntregadorController_DevePassar_quandoAlteramosDisponibilidadeDeEntregadorComCodigoAcessoInvalido() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(patch(URI_ENTREGADORES + "/" + entregador.getId() + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(entregadorPatchDto1)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }
    }
}
