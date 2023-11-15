package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.cliente.ClienteInteresseRequestDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborPatchStatusDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import com.ufcg.psoft.commerce.service.cliente.ClienteV1InteresseService;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Sabores")
public class SaborControllerTests {
    final String URI_SABORES = "/sabores";

    @Autowired
    MockMvc driver;

    @Autowired
    SaborRepository saborRepository;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ClienteV1InteresseService clienteV1InteresseService;

    ObjectMapper objectMapper = new ObjectMapper();
    Sabor sabor;
    Estabelecimento estabelecimento;
    SaborPostPutRequestDTO saborPostPutRequestDTO;

    @BeforeEach
    void setup() {
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        clienteRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .codigoAcesso("654321")
                .build());
        sabor = saborRepository.save(Sabor.builder()
                .nome("Calabresa")
                .tipo('S')
                .precoM(10.0)
                .precoG(15.0)
                .disponivel(true)
                        .estabelecimento(estabelecimento)
                .build());
        saborPostPutRequestDTO = SaborPostPutRequestDTO.builder()
                .nome(sabor.getNome())
                .tipo(sabor.getTipo())
                .precoM(sabor.getPrecoM())
                .precoG(sabor.getPrecoG())
                .disponivel(sabor.getDisponivel())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class SaborVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando buscamos por todos sabores salvos")
        void SaborController_DevePassar_quandoBuscamosPorTodosSaboresSalvos() throws Exception {
            // Arrange
            // Vamos ter 3 sabores no banco
            Sabor sabor1 = Sabor.builder()
                    .nome("Chocolate")
                    .tipo('D')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .build();
            Sabor sabor2 = Sabor.builder()
                    .nome("Frango")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .build();
            saborRepository.saveAll(Arrays.asList(sabor1, sabor2));

            // Act
            String responseJsonString = driver.perform(get(URI_SABORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<SaborResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(3, resultado.size())
            );
        }

        @Test
        @DisplayName("Quando buscamos um sabor salvo pelo id")
        void SaborController_DevePassar_quandoBuscamosPorUmSaborSalvo() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso()))
                            //.content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor resultado = objectMapper.readValue(responseJsonString, Sabor.class);

            SaborResponseDTO saborResponseDTO = modelMapper.map(resultado, SaborResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(sabor.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(sabor.getNome(), resultado.getNome()),
                    () -> assertEquals(sabor.getTipo(), resultado.getTipo()),
                    () -> assertEquals(sabor.getPrecoM(), resultado.getPrecoM()),
                    () -> assertEquals(sabor.getPrecoG(), resultado.getPrecoG()),
                    () -> assertEquals(sabor.getDisponivel(), resultado.getDisponivel())
            );
        }
        
        @Test
        @DisplayName("Quando buscamos um sabor inexistente")
        void SaborController_DeveFalhar_quandoBuscamosPorUmSaborInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_SABORES + "/999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O sabor consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos um sabor com código de acesso inválido")
        void SaborController_DeveFalhar_quandoBuscamosPorUmSaborComCodigoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
            );
        }

        

        @Test
        @DisplayName("Quando criamos um novo sabor com dados válidos")
        void SaborController_DevePassar_quandoCriarSaborValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(post(URI_SABORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(saborPostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(saborPostPutRequestDTO.getTipo(), resultado.getTipo()),
                    () -> assertEquals(saborPostPutRequestDTO.getPrecoM(), resultado.getPrecoM()),
                    () -> assertEquals(saborPostPutRequestDTO.getPrecoG(), resultado.getPrecoG()),
                    () -> assertEquals(saborPostPutRequestDTO.getDisponivel(), resultado.getDisponivel())
            );
        }
        @Test
        @DisplayName("Quando alteramos o sabor com dados válidos")
        void SaborController_DevePassar_quandoAlteramosSaborValido() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            Long saborId = sabor.getId();
            saborPostPutRequestDTO.setEstabelecimento(estabelecimento);

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getId(), saborId),
                    () -> assertEquals(saborPostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(saborPostPutRequestDTO.getTipo(), resultado.getTipo()),
                    () -> assertEquals(saborPostPutRequestDTO.getPrecoM(), resultado.getPrecoM()),
                    () -> assertEquals(saborPostPutRequestDTO.getPrecoG(), resultado.getPrecoG()),
                    () -> assertEquals(saborPostPutRequestDTO.getDisponivel(), resultado.getDisponivel())
            );
        }

        @Test
        @DisplayName("Quando alteramos um sabor inexistente")
        void SaborController_DeveFalhar_quandoAlteramosSaborInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O sabor consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando alteramos um sabor passando código de acesso inválido")
        void SaborController_DeveFalhar_quandoAlteramosSaborCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando excluímos um sabor salvo")
        void SaborController_DevePassar_quandoExcluimosSaborValido() throws Exception {
            //
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando excluímos um sabor inexistente")
        void SaborController_DeveFalhar_quandoExcluimosSaborInexistente() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_SABORES + "/" + (sabor.getId() + 1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O sabor consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando excluímos um sabor passando código de acesso inválido")
        void SaborController_DeveFalhar_quandoExcluimosSaborCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", "999999"))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando adicionamos um sabor passando código de acesso inválido")
        void SaborController_DeveFalhar_quandoAdicionamosSaborCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(post(URI_SABORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", "999999")
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando adicionamos um sabor passando código de acesso inválido")
        void SaborController_DeveFalhar_quandoVemosTodosSaboresCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_SABORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", "999999"))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Código de acesso inválido!", resultado.getMessage())
            );
        }
    }


    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class SaborVerificacaoNome {

        @Test
        @DisplayName("Quando alteramos um sabor com nome válido")
        void SaborController_DevePassar_quandoAlteramosSaborNomeValido() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            saborPostPutRequestDTO.setNome("Portuguesa");

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.class);

            // Assert
            assertEquals("Portuguesa", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos um sabor com nome vazio")
        void SaborController_DeveFalhar_quandoAlteramosSaborNomeVazio() throws Exception {
            // Arrange
            saborPostPutRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("nome não pode ser nulo ou vazio", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de tipo")
    class SaborVerificacaoTipo {

        @Test
        @DisplayName("Quando alteramos um sabor com tipo válido")
        void SaborController_DevePassar_quandoAlteramosSaborTipoValido() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            saborPostPutRequestDTO.setTipo('S');

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            assertEquals('S', resultado.getTipo());
        }

        @Test
        @DisplayName("Quando alteramos um sabor com tipo nulo")
        void SaborController_DeveFalhar_quandoAlteramosSaborTipoNulo() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            saborPostPutRequestDTO.setTipo(null);

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Tipo deve ser S ou D e não pode ser nulo", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos um sabor com tipo inválido")
        void SaborController_DeveFalhar_quandoAlteramosSaborTipoInvalido() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            saborPostPutRequestDTO.setTipo('P');

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Tipo deve ser S ou D e não pode ser nulo", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de preco")
    class SaborVerificacaoPreco {

        @Test
        @DisplayName("Quando alteramos um sabor com precos válidos")
        void SaborController_DevePassar_quandoAlteramosSaborPrecosValidos() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            saborPostPutRequestDTO.setPrecoM(40.0);
            saborPostPutRequestDTO.setPrecoG(60.0);

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(40.0, resultado.getPrecoM()),
                    () -> assertEquals(60.0, resultado.getPrecoG())
            );
        }

        @Test
        @DisplayName("Quando alteramos um sabor com precos nulos")
        void SaborController_DeveFalhar_quandoAlteramosSaborPrecosVazios() throws Exception {
            // Arrange
            saborPostPutRequestDTO.setPrecoM(null);
            saborPostPutRequestDTO.setPrecoG(null);

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue(resultado.getErrors().contains("precoM não pode ser nulo")),
                    () -> assertTrue(resultado.getErrors().contains("precoG não pode ser nulo"))
            );
        }

        @Test
        @DisplayName("Quando alteramos um sabor com precos inválidos")
        void SaborController_DeveFalhar_quandoAlteramosSaborPrecosInvalidos() throws Exception {
            // Arrange
            saborPostPutRequestDTO.setPrecoM(-10.0);
            saborPostPutRequestDTO.setPrecoG(-250.0);

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue(resultado.getErrors().contains("PrecoM deve ser maior que zero")),
                    () -> assertTrue(resultado.getErrors().contains("PrecoG deve ser maior que zero"))
            );
        }

        @Test
        @DisplayName("Quando alteramos um sabor com precos válidos e inválidos")
        void SaborController_DeveFalhar_quandoAlteramosSaborPrecosValidosEInvalidos() throws Exception {
            // Arrange
            saborPostPutRequestDTO.setPrecoM(40.0);
            saborPostPutRequestDTO.setPrecoG(-250.0);

            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("PrecoG deve ser maior que zero", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de disponibilidade")
    class SaborVerificacaoDisponibilidade {

        @Test
        @DisplayName("Quando alteramos um sabor com disponibilidade válida")
        void SaborController_DevePassar_quandoAlteramosSaborDisponibilidadeValida() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(false)
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", sabor.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            assertFalse(resultado.getDisponivel());
        }

        @Test
        @DisplayName("Quando alteramos um sabor com disponibilidade nula")
        void SaborController_DeveFalhar_quandoAlteramosSaborDisponibilidadeNula() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());

            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(null)
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", sabor.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("disponivel não pode ser nulo", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade de um sabor para false")
        void SaborController_DevePassar_quandoAlteramosDisponibilidadeSaborFalse() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(false)
                    .build();
            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", sabor.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.SaborResponseDTOBuilder.class).build();

            // Assert
            assertFalse(resultado.getDisponivel());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade de um sabor para true")
        void SaborController_DevePassar_quandoAlteramosDisponibilidadeSaborTrue() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(false)
                    .estabelecimento(estabelecimento)
                    .build());

            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(true)
                    .build();
            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", sabor.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.class);

            // Assert
            assertTrue(resultado.getDisponivel());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade de um sabor para false quando já está false")
        void SaborController_DeveFalhar_quandoAlteramosDisponibilidadeSaborFalseQuandoJaEstaFalse() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(false)
                    .estabelecimento(estabelecimento)
                    .build());

            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(false)
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", sabor.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O sabor consultado ja esta indisponivel!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade de um sabor para true quando já está true")
        void SaborController_DeveFalhar_quandoAlteramosDisponibilidadeSaborTrueQuandoJaEstaTrue() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(true)
                    .build();
            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", sabor.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O sabor consultado ja esta disponivel!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade de um sabor com o código de acesso errado")
        void SaborController_DeveFalhar_quandoAlteramosDisponibilidadeSaborCodigoErrado() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(false)
                    .build();
            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", sabor.getId().toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", "aaaaaa")
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Código de acesso inválido!", resultado.getMessage());
        }
        @Test
        @DisplayName("Quando alteramos a disponibilidade de um sabor invalido")
        void SaborController_DeveFalhar_quandoAlteramosDisponibilidadeSaborComSaborInvalido() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(false)
                    .estabelecimento(estabelecimento)
                    .build());

            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(true)
                    .build();

            Long saborId = sabor.getId() + 1;
            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/999999/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", saborId.toString())
                            .param("estabelecimentoId", estabelecimento.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O sabor consultado nao existe!", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação caso o estabelecimento seja nulo")
    class EstabelecimentoNulo{
        @Test
        @DisplayName("Quando buscamos um estabelecimento inexistente no get one")
        void SaborController_DeveFalhar_quandoBuscamosPorUmEstabelecimentoInexistenteNoGetOne() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            Long estabelecimentoId = (estabelecimento.getId() + 1);
            // Act
            String responseJsonString = driver.perform(get(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimentoId.toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }
        @Test
        @DisplayName("Quando buscamos um estabelecimento inexistente no get all")
        void SaborController_DeveFalhar_quandoBuscamosPorUmEstabelecimentoInexistenteNoGetAll() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            Long estabelecimentoId = (estabelecimento.getId() + 1);
            // Act
            String responseJsonString = driver.perform(get(URI_SABORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimentoId.toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso()))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos um estabelecimento inexistente no post")
        void SaborController_DeveFalhar_quandoBuscamosPorUmEstabelecimentoInexistenteNoPost() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            Long estabelecimentoId = (estabelecimento.getId() + 1);
            // Act
            String responseJsonString = driver.perform(post(URI_SABORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimentoId.toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos um estabelecimento inexistente no put")
        void SaborController_DeveFalhar_quandoBuscamosPorUmEstabelecimentoInexistenteNoPut() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            Long estabelecimentoId = (estabelecimento.getId() + 1);
            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimentoId.toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }
        @Test
        @DisplayName("Quando buscamos um estabelecimento inexistente no delete")
        void SaborController_DeveFalhar_quandoBuscamosPorUmEstabelecimentoInexistenteNoDelete() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            Long estabelecimentoId = (estabelecimento.getId() + 1);
            // Act
            String responseJsonString = driver.perform(delete(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimentoId.toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos um estabelecimento inexistente no patch")
        void SaborController_DeveFalhar_quandoBuscamosPorUmEstabelecimentoInexistenteNoPatch() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            Long estabelecimentoId = (estabelecimento.getId() + 1);
            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("saborId", sabor.getId().toString())
                            .param("estabelecimentoId", estabelecimentoId.toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O estabelecimento nao existe!", resultado.getMessage())
            );
        }


    }

    @Nested
    @DisplayName("Conjunto de casos de verificação caso o sabor não seja do estabelecimento")
    class SaborNaoEDoEstabelecimento{
        @Test
        @DisplayName("Quando buscamos um sabor que não é desse estabelecimentono get one")
        void SaborController_DeveFalhar_quandoBuscamosPorUmSaborQueNaoEDesseEstabelecimentoNoGetOne() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build());
            // Act
            String responseJsonString = driver.perform(get(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento1.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Sabor não pertence a este estabelecimento!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos um sabor que não é desse estabelecimento no delete")
        void SaborController_DeveFalhar_quandoBuscamosPorUmSaborQueNaoEDesseEstabelecimentoNoDelete() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build());
            // Act
            String responseJsonString = driver.perform(delete(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento1.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Sabor não pertence a este estabelecimento!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos um sabor que não é desse estabelecimento no put")
        void SaborController_DeveFalhar_quandoBuscamosPorUmSaborQueNaoEDesseEstabelecimentoNoPut() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build());
            // Act
            String responseJsonString = driver.perform(put(URI_SABORES + "/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento1.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Sabor não pertence a este estabelecimento!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos um sabor que não é desse estabelecimento no patch")
        void SaborController_DeveFalhar_quandoBuscamosPorUmSaborQueNaoEDesseEstabelecimentoNoPatch() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()
            objectMapper.registerModule(new JavaTimeModule());
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build());
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Calabresa")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(true)
                    .estabelecimento(estabelecimento)
                    .build());
            Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build());
            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(false)
                    .build();
            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento1.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Sabor não pertence a este estabelecimento!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("testes sobre notificação de cliente")
    class clienteNotification{
        @Test
        @DisplayName("Teste onde os clientes sao notificados com sucesso")
        @Transactional
        void SaborController_DevePassar_deveNotificarComSucessoUmCliente() throws Exception{
            Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build());
            Sabor sabor1 = saborRepository.save(Sabor.builder()
                    .nome("Frango")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(false)
                    .estabelecimento(estabelecimento1)
                    .build());

            Endereco endereco = Endereco.builder()
                    .numero(11)
                    .cep("78451-103")
                    .complemento("Ao lado da igreja da serra")
                    .build();

            Cliente cliente = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Um da Silva")
                    .endereco(endereco)
                    .email("campinafoood@gmail.com")
                    .codigoAcesso("123456")
                    .build()
            );
            ClienteInteresseRequestDTO clienteInteresseRequestDTO = ClienteInteresseRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .idEstabelecimento(estabelecimento1.getId())
                    .sabor(sabor1.getNome())
                    .build();
            clienteV1InteresseService.addInteresse(clienteInteresseRequestDTO);

            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(true)
                    .build();

                    // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor1.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento1.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.class);

             //Assert
            assertTrue(resultado.getDisponivel());
            assertEquals(resultado.getClientes().size(), 0);
        }

        @Test
        @DisplayName("Teste onde os clientes sao notificados com sucesso")
        @Transactional
        void SaborController_DevePassar_deveNotificarComSucessoDoisCliente() throws Exception{
            Estabelecimento estabelecimento1 = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("123456")
                    .build());
            Sabor sabor1 = saborRepository.save(Sabor.builder()
                    .nome("Frango")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(15.0)
                    .disponivel(false)
                    .estabelecimento(estabelecimento1)
                    .build());

            Endereco endereco = Endereco.builder()
                    .numero(11)
                    .cep("78451-103")
                    .complemento("Ao lado da igreja da serra")
                    .build();

            Cliente cliente = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Um da Silva")
                    .endereco(endereco)
                    .codigoAcesso("123456")
                    .email("campinafoood@gmail.com")
                    .build()
            );

            Cliente cliente1 = clienteRepository.save(Cliente.builder()
                    .nome("Fulano de Sousa")
                    .endereco(endereco)
                    .codigoAcesso("654321")
                    .email("campinafoood@gmail.com")

                    .build()
            );
            ClienteInteresseRequestDTO clienteInteresseRequestDTO = ClienteInteresseRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .idEstabelecimento(estabelecimento1.getId())
                    .sabor(sabor1.getNome())
                    .build();
            ClienteInteresseRequestDTO clienteInteresseRequestDTO1 = ClienteInteresseRequestDTO.builder()
                    .idCliente(cliente1.getId())
                    .codigoAcesso(cliente1.getCodigoAcesso())
                    .idEstabelecimento(estabelecimento1.getId())
                    .sabor(sabor1.getNome())
                    .build();
            clienteV1InteresseService.addInteresse(clienteInteresseRequestDTO);
            clienteV1InteresseService.addInteresse(clienteInteresseRequestDTO1);

            SaborPatchStatusDTO saborPatchStatusDTO = SaborPatchStatusDTO.builder()
                    .disponivel(true)
                    .build();


            // Act
            String responseJsonString = driver.perform(patch(URI_SABORES + "/" + sabor1.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("estabelecimentoId", estabelecimento1.getId().toString())
                            .param("estabelecimentoCodigoAcesso", estabelecimento1.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(saborPatchStatusDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            SaborResponseDTO resultado = objectMapper.readValue(responseJsonString, SaborResponseDTO.class);

            //Assert
            assertTrue(resultado.getDisponivel());
            assertEquals(resultado.getClientes().size(), 0);
        }

    }
}
