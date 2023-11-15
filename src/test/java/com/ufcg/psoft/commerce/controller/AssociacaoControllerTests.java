package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.repository.AssociacaoRepository;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.entregador.Veiculo;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Associação")
@Transactional
class AssociacaoControllerTests {

    final String URI_ASSOCIACAO = "/associacoes";

    @Autowired
    MockMvc driver;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    EntregadorRepository entregadorRepository;
    
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    Entregador entregador;

    Estabelecimento estabelecimento;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        estabelecimentoRepository.deleteAll();
        entregadorRepository.deleteAll();
        associacaoRepository.deleteAll();

        // Object Mapper suporte para LocalDateTime
        Veiculo veiculo = Veiculo.builder()
                .placa("ABC-1234")
                .cor("Azul")
                .tipo("moto")
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Entregador Um")
                .veiculo(veiculo)
                .codigoAcesso("123456")
                .disponibilidade(true)
                .build()
        );
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .codigoAcesso("654321")
                .build()
        );
    }

    @AfterEach
    void tearDown() {

    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de criacao de associacao")
    class ClienteCriacaoAssociacao {
        @Test
        @DisplayName("Quando criamos uma associacao com sucesso")
        void AssociacaoController_DevePassar_ParaCriarUmaAssociacaoComSucesso() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultado = objectMapper.readValue(responseJsonString, Associacao.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertNotNull(resultado.getId()),

                    () -> assertEquals(entregador.getId(), resultado.getEntregador().getId()),
                    () -> assertEquals(entregador.getNome(), resultado.getEntregador().getNome()),
                    () -> assertEquals(entregador.getDisponibilidade(), resultado.getEntregador().getDisponibilidade()),
                    () -> assertEquals(entregador.getVeiculo(), resultado.getEntregador().getVeiculo()),
                    () -> assertArrayEquals(entregador.getAssociacoes().toArray(), resultado.getEntregador().getAssociacoes().toArray()),

                    () -> assertEquals(estabelecimento.getId(), resultado.getEstabelecimento().getId()),
                    () -> assertArrayEquals(estabelecimento.getAssociacoes().toArray(), resultado.getEstabelecimento().getAssociacoes().toArray())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao com entregador inexistente")
        void AssociacaoController_DeveFalhar_CriarAssociacaoComEntregadorInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", String.valueOf(entregador.getId() + 1))
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("O entregador consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao com estabelecimento inexistente")
        void AssociacaoController_DeveFalhar_CriarAssociacaoComEstabelecimentoInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", entregador.getCodigoAcesso())
                            .param("estabelecimentoId", String.valueOf(estabelecimento.getId() + 1)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao passando codigo de acesso invalido")
        void AssociacaoController_DeveFalhar_CriarAssociacaoComCodigoDeAcessoInvalido() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", entregador.getId().toString())
                            .param("codigoAcesso", "654321")
                            .param("estabelecimentoId", estabelecimento.getId().toString()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(0, associacaoRepository.count()),
                    () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de aprovação de associacao")
    class ClienteAprovacaoAssociacao {

        Associacao associacao;

        @BeforeEach
        void setUp() {
            associacao = associacaoRepository.save(Associacao.builder()
                        .estabelecimento(estabelecimento)
                        .entregador(entregador)
                        .status(false)
                        .build()
            );
            estabelecimento.getAssociacoes().add(associacao);
        }

        @Test
        @DisplayName("Quando aprovamos uma associacao com sucesso")
        void AssociacaoController_DevePassar_quandoAprovamosAssociacaoComSucesso() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(patch(URI_ASSOCIACAO + "/" + associacao.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("status", String.valueOf(true)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultado = objectMapper.readValue(responseJsonString, Associacao.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertEquals(false, resultado.getEntregador().getDisponibilidade()),
                    () -> assertTrue(resultado.getStatus())
            );
        }

        @Test
        @DisplayName("Quando reprovamos uma associacao com sucesso")
        void AssociacaoController_DevePassar_quandoReprovamosAssociacaoComSucesso() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(patch(URI_ASSOCIACAO + "/" + associacao.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("status", String.valueOf(false)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Associacao resultado = objectMapper.readValue(responseJsonString, Associacao.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertEquals(true, resultado.getEntregador().getDisponibilidade()),
                    () -> assertFalse(resultado.getStatus())
            );
        }

        @Test
        @DisplayName("Quando aprovamos uma associação passando Estabelecimento inválido")
        void AssociacaoController_DeveFalhar_quandoAprovamosAssociacaoComEstabelecimentoInvalido() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(patch(URI_ASSOCIACAO + "/" + associacao.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", String.valueOf(estabelecimento.getId() + 1))
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("status", String.valueOf(true)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando aprovamos uma associação passando codigo de acesso inválido")
        void AssociacaoController_DeveFalhar_quandoAprovamosAssociacaoComCodigoDeAcessoInvalido() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(patch(URI_ASSOCIACAO + "/" + associacao.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcesso", "666542")
                            .param("status", String.valueOf(true)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando aprovamos uma associação passando id da associação inválida")
        void AssociacaoController_DeveFalhar_quandoAprovamosAssociacaoComAssociacaoInvalida() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(patch(URI_ASSOCIACAO + "/" + associacao.getId() + 1 + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .param("status", String.valueOf(true)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertEquals("Associacao nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando aprovamos uma associacao passando estabelecimento diferente do salvo em associacao")
        void AssociacaoController_DeveFalhar_quandoAprovamosAssociacaoComEstabelecimentoDiferenteDaAssociacao() throws Exception {
            // Arrange
            Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("111111")
                    .build());
            // Act
            String responseJsonString = driver.perform(patch(URI_ASSOCIACAO + "/" + associacao.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("codigoAcesso", estabelecimento1.getCodigoAcesso())
                            .param("status", String.valueOf(true)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, associacaoRepository.count()),
                    () -> assertEquals("Associacao não pertence a esse estabelecimento!", resultado.getMessage())
            );
        }
    }
}