package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborPatchStatusDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborV2ResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;

import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;

import com.ufcg.psoft.commerce.service.sabor.ISaborPatchStatusService;
import com.ufcg.psoft.commerce.service.sabor.ISaborV1PostService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de estabelecimentos")
public class EstabelecimentoControllerTests {
    @Autowired
    MockMvc driver;
    final String URI_ESTABELECIMENTOS = "/estabelecimentos";
    EstabelecimentoPostPutRequestDTO estabelecimentoPutRequestDTO;
    EstabelecimentoPostPutRequestDTO estabelecimentoPostRequestDTO;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ISaborV1PostService saborV1PostService;

    @Autowired
    ISaborPatchStatusService saborV1PatchStatusService;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    Estabelecimento estabelecimento;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        estabelecimentoPutRequestDTO = EstabelecimentoPostPutRequestDTO.builder()
                .codigoAcesso("123456")
                .build();
        estabelecimentoPostRequestDTO = EstabelecimentoPostPutRequestDTO.builder()
                .codigoAcesso("654321")
                .build();
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .codigoAcesso("123456")
                .build());
    }

    @AfterEach
    void tearDown() {
        estabelecimentoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de validação de Post")
    class EstabelecimentoPostValidacoes{
        @Test
        @DisplayName("Quando criamos um novo estabelecimento com dados válidos")
        void EstabelecimentoController_DevePassar_quandoCriarEstabelecimentoValido() throws Exception {
            // Arrange
            estabelecimentoPostRequestDTO.setEmail("campinafoood@gmail.com");
            // Act
            String responseJsonString = driver.perform(post(URI_ESTABELECIMENTOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimentoPostRequestDTO.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJsonString, EstabelecimentoResponseDTO.EstabelecimentoResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(estabelecimentoPostRequestDTO.getCodigoAcesso(), resultado.getCodigoAcesso())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo estabelecimento com senha com mais de 6 digitos")
        void EstabelecimentoController_DeveFalhar_quandoCriarEstabelecimentoSenhaMaiorSeisDigitos() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            estabelecimentoPostRequestDTO.setCodigoAcesso("66666666");
            // Act
            String responseJsonString = driver.perform(post(URI_ESTABELECIMENTOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimentoPostRequestDTO.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("CODIGO DE ACESSO DEVE TER EXATAMENTE 6 DIGITOS NUMERICOS")
                    ))
            );
        }
        @Test
        @DisplayName("Quando criamos um novo estabelecimento com dados inválidos")
        void EstabelecimentoController_DeveFalhar_quandoCriarEstabelecimentoInvalido() throws Exception {
            // Arrange
            EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoAcesso("13")
                    .email("campinafoood@gmail.com")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_ESTABELECIMENTOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimentoPostPutRequestDTO.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de validação de Put")
    class EstabelecimentoPutValidacoes{
        @Test
        @DisplayName("Quando atualizamos um estabelecimento salvo")
        void EstabelecimentoController_DevePassar_quandoAtualizamosEstabelecimentoValido() throws Exception {
            // Arrange
            estabelecimentoPutRequestDTO.setCodigoAcesso("131289");
            estabelecimentoPutRequestDTO.setEmail("campinafoood@gmail.com");

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EstabelecimentoResponseDTO resultado = objectMapper.readValue(responseJsonString, EstabelecimentoResponseDTO.EstabelecimentoResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getId().longValue(), estabelecimento.getId().longValue()),
                    () -> assertEquals("131289", resultado.getCodigoAcesso())
            );
        }


        @Test
        @DisplayName("Quando alteramos um estabelecimento com codigo de acesso inválido")
        void EstabelecimentoController_DeveFalhar_quandoAlterarEstabelecimentoCodigoAcessoInvalido() throws Exception {
            // Arrange
            EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoAcesso("13")
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("CODIGO DE ACESSO DEVE TER EXATAMENTE 6 DIGITOS NUMERICOS")
                    ))
            );
        }

        @Test
        @DisplayName("Quando tentamos alterar um estabelecimento com id invalido")
        void EstabelecimentoController_DeveFalhar_quandoAlterarEstabelecimentoIdInvalido() throws Exception {
            // Arrange
            EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoAcesso("222222")
                    .email("campinafoood@gmail.com")
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTOS + "/999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(estabelecimentoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType customErrorType = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O estabelecimento nao existe!", customErrorType.getMessage())
            );
        }

        @Test
        @DisplayName("Quando tentamos alterar um estabelecimento com codigoAcesso errado")
        void EstabelecimentoController_DeveFalhar_quandoAlterarEstabelecimentoCodigoAcessoErrado() throws Exception {
            // Arrange
            EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoAcesso("222222")
                    .email("campinafoood@gmail.com")
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "234511")
                            .content(objectMapper.writeValueAsString(estabelecimentoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType customErrorType = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Código de acesso inválido!", customErrorType.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de validação de Cardápio")
    class EstabelecimentoCardapioValidacoes{
        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento")
        void EstabelecimentoController_DevePassar_quandoBuscarCardapioEstabelecimento() throws Exception {
            // Arrange
            SaborPostPutRequestDTO sabor1 = SaborPostPutRequestDTO.builder()
                    .nome("Calabresa")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('S')
                    .build();

            SaborPostPutRequestDTO sabor2 = SaborPostPutRequestDTO.builder()
                    .nome("Mussarela")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor3 = SaborPostPutRequestDTO.builder()
                    .nome("Chocolate")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor4 = SaborPostPutRequestDTO.builder()
                    .nome("Morango")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();

            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();

            estabelecimentoRepository.save(estabelecimento1);

            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor2);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor4);

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento1.getId() + "/sabores")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<SaborV2ResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(4, resultado.size())
            );
        }


        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento que não existe")
        void EstabelecimentoController_DeveFalhar_quandoBuscarCardapioEstabelecimentoInexistente() throws Exception {
            // Arrange
            // Nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/99999/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 404
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }



        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento por tipo (salgado)")
        void EstabelecimentoController_DevePassar_quandoBuscarCardapioEstabelecimentoPorTipo() throws Exception {
            // Arrange
            SaborPostPutRequestDTO sabor1 = SaborPostPutRequestDTO.builder()
                    .nome("Calabresa")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('S')
                    .build();

            SaborPostPutRequestDTO sabor2 = SaborPostPutRequestDTO.builder()
                    .nome("Mussarela")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor3 = SaborPostPutRequestDTO.builder()
                    .nome("Chocolate")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor4 = SaborPostPutRequestDTO.builder()
                    .nome("Morango")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();

            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();
            estabelecimentoRepository.save(estabelecimento1);

            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor2);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor4);


            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento1.getId() + "/sabores/tipo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("tipo", "S")
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<SaborV2ResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(sabor1.getNome(),resultado.get(0).getNome()),
                    () -> assertEquals(sabor1.getPrecoG(),resultado.get(0).getPrecoG()),
                    () -> assertEquals(sabor1.getTipo(),resultado.get(0).getTipo()),
                    () -> assertEquals(sabor2.getNome(),resultado.get(1).getNome()),
                    () -> assertEquals(sabor2.getPrecoG(),resultado.get(1).getPrecoG()),
                    () -> assertEquals(sabor2.getTipo(),resultado.get(1).getTipo())
            );
        }

        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento por tipo (salgado) com parametro minusculo")
        void EstabelecimentoController_DevePassar_quandoBuscarCardapioEstabelecimentoPorTipoSalgadoMinusculo() throws Exception {
            // Arrange
            SaborPostPutRequestDTO sabor1 = SaborPostPutRequestDTO.builder()
                    .nome("Calabresa")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('S')
                    .build();

            SaborPostPutRequestDTO sabor2 = SaborPostPutRequestDTO.builder()
                    .nome("Mussarela")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor3 = SaborPostPutRequestDTO.builder()
                    .nome("Chocolate")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor4 = SaborPostPutRequestDTO.builder()
                    .nome("Morango")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();

            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();
            estabelecimentoRepository.save(estabelecimento1);

            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor2);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor4);


            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento1.getId() + "/sabores/tipo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("tipo", "s")
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<SaborV2ResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(sabor1.getNome(),resultado.get(0).getNome()),
                    () -> assertEquals(sabor1.getPrecoG(),resultado.get(0).getPrecoG()),
                    () -> assertEquals(sabor1.getTipo(),resultado.get(0).getTipo()),
                    () -> assertEquals(sabor2.getNome(),resultado.get(1).getNome()),
                    () -> assertEquals(sabor2.getPrecoG(),resultado.get(1).getPrecoG()),
                    () -> assertEquals(sabor2.getTipo(),resultado.get(1).getTipo())
            );
        }


        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento por tipo (doce)")
        void EstabelecimentoController_DevePassar_quandoBuscarCardapioEstabelecimentoPorTipoDoce() throws Exception {
            // Arrange
            SaborPostPutRequestDTO sabor1 = SaborPostPutRequestDTO.builder()
                    .nome("Calabresa")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('S')
                    .build();

            SaborPostPutRequestDTO sabor2 = SaborPostPutRequestDTO.builder()
                    .nome("Mussarela")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor3 = SaborPostPutRequestDTO.builder()
                    .nome("Chocolate")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor4 = SaborPostPutRequestDTO.builder()
                    .nome("Morango")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();

            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();
            estabelecimentoRepository.save(estabelecimento1);

            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor2);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor4);

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento1.getId() + "/sabores/tipo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("tipo", "d")
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<SaborV2ResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(sabor3.getNome(),resultado.get(0).getNome()),
                    () -> assertEquals(sabor3.getPrecoG(),resultado.get(0).getPrecoG()),
                    () -> assertEquals(sabor3.getTipo(),resultado.get(0).getTipo()),
                    () -> assertEquals(sabor4.getNome(),resultado.get(1).getNome()),
                    () -> assertEquals(sabor4.getPrecoG(),resultado.get(1).getPrecoG()),
                    () -> assertEquals(sabor4.getTipo(),resultado.get(1).getTipo())
            );
        }

        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento por tipo invalido")
        void EstabelecimentoController_DeveFalhar_quandoBuscarCardapioEstabelecimentoPorTipoInvalido() throws Exception {
            // Arrange
            SaborPostPutRequestDTO sabor1 = SaborPostPutRequestDTO.builder()
                    .nome("Calabresa")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('S')
                    .build();

            SaborPostPutRequestDTO sabor2 = SaborPostPutRequestDTO.builder()
                    .nome("Mussarela")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor3 = SaborPostPutRequestDTO.builder()
                    .nome("Chocolate")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor4 = SaborPostPutRequestDTO.builder()
                    .nome("Morango")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();

            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();
            estabelecimentoRepository.save(estabelecimento1);

            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor2);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor4);

            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento1.getId() + "/sabores" + "/tipo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("tipo", "a")
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType customErrorType = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O tipo do Sabor deve ser 'S' ou 'D'", customErrorType.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento ordenado por disponibilidade")
        void EstabelecimentoController_DevePassar_quandoBuscarCardapioEstabelecimentoOrdenadoPorDisponibilidade() throws Exception {
            // Arrange
            SaborPostPutRequestDTO sabor1 = SaborPostPutRequestDTO.builder()
                    .nome("Calabresa")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('S')
                    .build();

            SaborPostPutRequestDTO sabor2 = SaborPostPutRequestDTO.builder()
                    .nome("Mussarela")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor3 = SaborPostPutRequestDTO.builder()
                    .nome("Chocolate")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor4 = SaborPostPutRequestDTO.builder()
                    .nome("Morango")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor5 = SaborPostPutRequestDTO.builder()
                    .nome("Jaca")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();


            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();

            estabelecimentoRepository.save(estabelecimento1);

            SaborPatchStatusDTO sabor1Patch = SaborPatchStatusDTO.builder().disponivel(false).build();

            SaborResponseDTO sabor1Response = saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor2);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor4);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor5);

            saborV1PatchStatusService.atualizarSaborStatus(sabor1Response.getId(), estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1Patch);





            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento1.getId() + "/sabores")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<SaborV2ResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(5, resultado.size()),
                    () -> assertEquals(sabor1.getNome(), resultado.get(4).getNome()),
                    () -> assertEquals(sabor2.getNome(), resultado.get(0).getNome())
            );
        }

        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento por tipo (salgado) ordenado por disponibilidade")
        void EstabelecimentoController_DevePassar_quandoBuscarCardapioEstabelecimentoPorTipoSalgadoOrdenado() throws Exception {
            // Arrange
            SaborPostPutRequestDTO sabor1 = SaborPostPutRequestDTO.builder()
                    .nome("Calabresa")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('S')
                    .build();

            SaborPostPutRequestDTO sabor2 = SaborPostPutRequestDTO.builder()
                    .nome("Mussarela")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor3 = SaborPostPutRequestDTO.builder()
                    .nome("Chocolate")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor4 = SaborPostPutRequestDTO.builder()
                    .nome("Morango")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();
            SaborPostPutRequestDTO sabor5 = SaborPostPutRequestDTO.builder()
                    .nome("Lombo")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor6 = SaborPostPutRequestDTO.builder()
                    .nome("Cachorro quente")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();

            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();
            estabelecimentoRepository.save(estabelecimento1);

            SaborPatchStatusDTO sabor1Patch = SaborPatchStatusDTO.builder().disponivel(false).build();
            SaborPatchStatusDTO sabor5Patch = SaborPatchStatusDTO.builder().disponivel(false).build();

            SaborResponseDTO sabor1Response = saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor2);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor4);
            SaborResponseDTO sabor5Response = saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor5);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor6);

            saborV1PatchStatusService.atualizarSaborStatus(sabor1Response.getId(), estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1Patch);
            saborV1PatchStatusService.atualizarSaborStatus(sabor5Response.getId(), estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor5Patch);


            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento1.getId() + "/sabores/tipo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("tipo", "S")
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<SaborV2ResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(4, resultado.size()),
                    () -> assertEquals(sabor1.getNome(), resultado.get(2).getNome()),
                    () -> assertEquals(sabor5.getNome(), resultado.get(3).getNome()),
                    () -> assertEquals(sabor2.getNome(), resultado.get(0).getNome()),
                    () -> assertEquals(sabor6.getNome(), resultado.get(1).getNome())
            );
        }

        @Test
        @DisplayName("Quando buscamos o cardapio de um estabelecimento por tipo (doce) ordenado por disponibilidade")
        void EstabelecimentoController_DevePassar_quandoBuscarCardapioEstabelecimentoPorTipoDoceOrdenadoPorDisponibilidade() throws Exception {
            // Arrange
            SaborPostPutRequestDTO sabor1 = SaborPostPutRequestDTO.builder()
                    .nome("Calabresa")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('S')
                    .build();

            SaborPostPutRequestDTO sabor2 = SaborPostPutRequestDTO.builder()
                    .nome("Mussarela")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('S')
                    .build();
            SaborPostPutRequestDTO sabor3 = SaborPostPutRequestDTO.builder()
                    .nome("Chocolate")
                    .precoM(25.0)
                    .precoG(35.0)
                    .tipo('D')
                    .build();

            SaborPostPutRequestDTO sabor4 = SaborPostPutRequestDTO.builder()
                    .nome("Morango")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();
            SaborPostPutRequestDTO sabor5 = SaborPostPutRequestDTO.builder()
                    .nome("MM'S")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();
            SaborPostPutRequestDTO sabor6 = SaborPostPutRequestDTO.builder()
                    .nome("Jaca")
                    .precoM(20.0)
                    .precoG(30.0)
                    .tipo('D')
                    .build();

            Estabelecimento estabelecimento1 = Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build();
            estabelecimentoRepository.save(estabelecimento1);

            SaborPatchStatusDTO sabor3Patch = SaborPatchStatusDTO.builder().disponivel(false).build();
            SaborPatchStatusDTO sabor5Patch = SaborPatchStatusDTO.builder().disponivel(false).build();

            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor1);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor2);
            SaborResponseDTO sabor3Response = saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor4);
            SaborResponseDTO sabor5Response = saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor5);
            saborV1PostService.cadastrarSabor(estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor6);


            saborV1PatchStatusService.atualizarSaborStatus(sabor3Response.getId(), estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor3Patch);
            saborV1PatchStatusService.atualizarSaborStatus(sabor5Response.getId(), estabelecimento1.getId(), estabelecimento1.getCodigoAcesso(), sabor5Patch);


            // Act
            String responseJsonString = driver.perform(get(URI_ESTABELECIMENTOS + "/" + estabelecimento1.getId() + "/sabores" + "/tipo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("tipo", "d")
                            .content(objectMapper.writeValueAsString(estabelecimentoPostRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<SaborV2ResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(4, resultado.size()),
                    () -> assertEquals(sabor3.getNome(), resultado.get(2).getNome()),
                    () -> assertEquals(sabor5.getNome(), resultado.get(3).getNome()),
                    () -> assertEquals(sabor4.getNome(), resultado.get(0).getNome()),
                    () -> assertEquals(sabor6.getNome(), resultado.get(1).getNome())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de validação de Delete")
    class EstabelecimentoDeleteValidacoes{
        @Test
        @DisplayName("Quando excluímos um estabelecimento salvo")
        void EstabelecimentoController_DevePassar_quandoExcluimosEstabelecimentoValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
            assertEquals(0,estabelecimentoRepository.findAll().size());
        }

        @Test
        @DisplayName("Quando tentamos excluir um estabelecimento salvo passando um id inválido")
        void EstabelecimentoController_DeveFalhar_quandoExcluimosEstabelecimentoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ESTABELECIMENTOS + "/9999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertTrue(resultado.getMessage().toUpperCase().contains("ESTABELECIMENTO NAO EXISTE"));
        }

        @Test
        @DisplayName("Quando tentamos excluir um estabelecimento salvo passando um id válido mas com o codigo de acesso invalido")
        void EstabelecimentoController_DeveFalhar_quandoExcluimosEstabelecimentoValidoCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_ESTABELECIMENTOS + "/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "222222"))
                    .andExpect(status().isBadRequest()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertTrue(resultado.getMessage().contains("Código de acesso inválido!"));
        }

    }
}
