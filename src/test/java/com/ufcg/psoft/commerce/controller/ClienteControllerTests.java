package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.cliente.ClienteGetResponseDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteInteresseRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteInteresseResponseDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Clientes")
public class ClienteControllerTests {

    final String URI_CLIENTES = "/clientes";

    @Autowired
    MockMvc driver;

    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    SaborRepository saborRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Cliente cliente;

    Endereco endereco;

    ClientePostPutRequestDTO clientePostPutRequestDTO;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        endereco = Endereco.builder()
                .numero(11)
                .cep("78451-103")
                .complemento("Ao lado da igreja da serra")
                .build();

        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(endereco)
                .codigoAcesso("123456")
                .build()
        );

        clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                .nome(cliente.getNome())
                .endereco(cliente.getEndereco())
                .email("jorginho@gmail.com")
                .codigoAcesso(cliente.getCodigoAcesso())
                .build();

    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class ClienteVerificacaoNome {

        @BeforeEach
        void setUp(){
            objectMapper.registerModule(new JavaTimeModule());
        }
        @Test
        @DisplayName("Quando criamos cliente com dados válidos")
        void ClienteController_DevePassar_quandoCriamosClienteValido() throws Exception {
            // Arrange
            endereco = Endereco.builder()
                    .numero(13)
                    .cep("23232-100")
                    .complemento("Na lateral da UFCG")
                    .build();

            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Capitu de Melo")
                    .endereco(endereco)
                    .codigoAcesso("122222")
                    .email("campinafoood@gmail.com")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getCep(), resultado.getEndereco().getCep()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getComplemento(), resultado.getEndereco().getComplemento()),
                    () -> assertEquals(clientePostPutRequestDTO.getCodigoAcesso(), resultado.getCodigoAcesso())
            );
        }


        @Test
        @DisplayName("Quando alteramos o nome do cliente com dados válidos")
        void ClienteController_DevePassar_quandoAlteramosNomeDoClienteValido() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setNome("Cliente Um Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

            // Assert
            assertEquals("Cliente Um Alterado", resultado.getNome());

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco(), resultado.getEndereco()),
                    () -> assertEquals(clientePostPutRequestDTO.getCodigoAcesso(), resultado.getCodigoAcesso())
            );
        }

        @Test
        @DisplayName("Quando alteramos dados validos de cliente existente")
        void ClienteController_DevePassar_quandoAlteramosDadosClienteValido() throws Exception {
            // Arrange
            endereco = Endereco.builder()
                    .cep("09696-000")
                    .numero(90)
                    .complemento("Rua dos bobos")
                    .build();

            clientePostPutRequestDTO.setNome("Humbert Humbert");
            clientePostPutRequestDTO.setCodigoAcesso("675842");
            clientePostPutRequestDTO.setEndereco(endereco);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals("Humbert Humbert", resultado.getNome()),
                    () -> assertEquals("675842", resultado.getCodigoAcesso()),
                    () -> assertEquals("09696-000", resultado.getEndereco().getCep()),
                    () -> assertEquals("Rua dos bobos", resultado.getEndereco().getComplemento()),
                    () -> assertEquals(90, resultado.getEndereco().getNumero())
            );
        }

        @Test
        @DisplayName("Quando alteramos dados de cliente inexistente")
        void ClienteController_DeveFalhar_quandoAlteramosDadosClienteInexistente() throws Exception {
            // Arrange
            endereco = Endereco.builder()
                    .cep("09696-000")
                    .numero(90)
                    .complemento("Rua dos bobos")
                    .build();

            clientePostPutRequestDTO.setNome("Humbert Humbert");
            clientePostPutRequestDTO.setCodigoAcesso("675842");
            clientePostPutRequestDTO.setEndereco(endereco);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + 9)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
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
        @DisplayName("Quando alteramos dados de cliente com codigo de acesso inválido")
        void ClienteController_DeveFalhar_quandoAlteramosDadosClienteComCodigoAcessoInvalido() throws Exception {
            // Arrange
            endereco = Endereco.builder()
                    .numero(11)
                    .cep("78451-103")
                    .complemento("Ao lado da igreja da serra")
                    .build();

            cliente = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Um da Silva")
                    .endereco(endereco)
                    .codigoAcesso("123456")
                    .email("campinafoood@gmail.com")
                    .build()
            );

            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome(cliente.getNome())
                    .endereco(cliente.getEndereco())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .email("campinafoood@gmail.com")
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "111222")
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
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
        @DisplayName("Quando criar cliente com dados inválidos")
        void ClienteController_DeveFalhar_quandoCriarClienteComDadosInvalidos() throws Exception {
            // Arrange
            endereco = Endereco.builder()
                    .cep(null)
                    .numero(null)
                    .complemento("    ")
                    .build();

            clientePostPutRequestDTO.setNome("");
            clientePostPutRequestDTO.setCodigoAcesso(null);
            clientePostPutRequestDTO.setEndereco(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("NOME NÃO PODE SER VAZIO"))),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("ENDEREÇO NÃO PODE SER VAZIO"))),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("CODIGO DE ACESSO NÃO PODE SER VAZIO")))
            );
        }

        @Test
        @DisplayName("Quando alteramos dados invalidos de um cliente")
        void ClienteController_DeveFalhar_quandoAlteramosClienteComDadosInvalidos() throws Exception {
            // Arrange

            endereco = Endereco.builder()
                    .cep(null)
                    .numero(null)
                    .complemento("")
                    .build();

            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome(" ")
                    .endereco(null)
                    .codigoAcesso(null)
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("NOME NÃO PODE SER VAZIO"))),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("ENDEREÇO NÃO PODE SER VAZIO"))),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("CODIGO DE ACESSO NÃO PODE SER VAZIO")))
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente com nome nulo")
        void ClienteController_DeveFalhar_quandoAlteramosNomeDoClienteParaNulo() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setNome(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
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
        @DisplayName("Quando alteramos o cliente com nome vazio")
        void ClienteController_DeveFalhar_quandoAlteramosNomeDoClienteParaVazio() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
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
    }


    @Nested
    @DisplayName("Conjunto de casos de verificação do endereço")
    class ClienteVerificacaoEndereco {

        @Test
        @DisplayName("Quando alteramos o endereço do cliente com dados válidos")
        void ClienteController_DevePassar_quandoAlteramosEnderecoDoClienteValido() throws Exception {
            // Arrange
            Cliente c = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Um da Silva")
                    .endereco(Endereco.builder()
                            .numero(241)
                            .cep("78965-103")
                            .complemento("Pracinha Nufuturo")
                            .build())
                    .codigoAcesso("123456")
                    .email("campinafoood@gmail.com")
                    .build()
            );

            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome(cliente.getNome())
                    .endereco(cliente.getEndereco())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .email("campinafoood@gmail.com")
                    .build();

            clientePostPutRequestDTO.setEndereco(c.getEndereco());

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

            // Assert
            assertAll(
                    () -> assertEquals(c.getEndereco().getComplemento(), resultado.getEndereco().getComplemento()),
                    () -> assertEquals(c.getEndereco().getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(c.getEndereco().getCep(), resultado.getEndereco().getCep())
            );
        }

        @Test
        @DisplayName("Quando alteramos o endereço do cliente para nulo")
        void ClienteController_DeveFalhar_quandoAlteramosEnderecoDoClienteParaNulo() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setEndereco(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereço não pode ser vazio.", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o endereço do cliente para vazio")
        void ClienteController_DeveFalhar_quandoAlteramosEnderecoDoClienteParaVazio() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome(cliente.getNome())
                    .endereco(Endereco.builder()
                            .cep(null)
                            .numero(null)
                            .complemento("")
                            .build())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .email("campinafoood@gmail.com")
                    .build();

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("COMPLEMENTO NÃO PODE SER VAZIO"))),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("CEP NÃO PODE SER VAZIO"))),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("NÚMERO NÃO PODE SER VAZIO")))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto com os métodos Http")
    class ClienteHttpMethods {

        @Test
        @DisplayName("GET - Quando retornamos um cliente com dados válidos")
        void ClienteController_DevePassar_quandoRecuperaUmClienteValido() throws Exception {
            // Arrange

            objectMapper.registerModule(new JavaTimeModule());
            endereco = Endereco.builder()
                    .numero(13)
                    .cep("23232-100")
                    .complemento("Na lateral da UFCG")
                    .build();
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Capitu de Melo")
                    .endereco(endereco)
                    .codigoAcesso("122222")
                    .build();

            Cliente c = clienteRepository.save(objectMapper.convertValue(clientePostPutRequestDTO, Cliente.class));

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + c.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteGetResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteGetResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getCep(), resultado.getEndereco().getCep()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getComplemento(), resultado.getEndereco().getComplemento())
            );
        }

        @Test
        @DisplayName("GET - Quando retornamos cliente com id inválido")
        void ClienteController_DeveFalhar_quandoRecuperaUmClienteComIdInvalido() throws Exception {
            // Arrange
            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId() + 51))
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
        @DisplayName("DELETE - Deve deletar um cliente com sucesso")
        void ClienteController_DevePassar_quandoDeletarClienteValido() throws Exception {
            // Arrange
            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Um da Silva")
                    .endereco(Endereco.builder()
                            .numero(11)
                            .cep("78451-103")
                            .complemento("Ao lado da igreja da serra")
                            .build())
                    .codigoAcesso("798740")
                    .build()
            );

            assertEquals(2, clienteRepository.count());

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente2.getCodigoAcesso()))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
            assertEquals(1, clienteRepository.count());
        }

        @Test
        @DisplayName("GET ALL - Quando recuperamos todos os clientes")
        void ClienteController_DevePassar_quandoRecuperamosTodosOsClientes() throws Exception {
            // Arrange
            endereco = Endereco.builder()
                    .numero(11)
                    .cep("78451-103")
                    .complemento("Ao lado da igreja da serra")
                    .build();

            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Um da Silva")
                    .endereco(endereco)
                    .codigoAcesso("123456")
                    .build()
            );

            ClientePostPutRequestDTO clientePostPutRequestDTO2 = ClientePostPutRequestDTO.builder()
                    .nome(cliente.getNome())
                    .endereco(cliente.getEndereco())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .build();

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<ClienteGetResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<ClienteGetResponseDTO>>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(cliente.getId(), resultado.get(0).getId().longValue()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.get(0).getNome()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getNumero(), resultado.get(0).getEndereco().getNumero()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getCep(), resultado.get(0).getEndereco().getCep()),
                    () -> assertEquals(clientePostPutRequestDTO.getEndereco().getComplemento(), resultado.get(0).getEndereco().getComplemento()),
                    () -> assertEquals(cliente2.getId(), resultado.get(1).getId().longValue()),
                    () -> assertEquals(clientePostPutRequestDTO2.getNome(), resultado.get(1).getNome()),
                    () -> assertEquals(clientePostPutRequestDTO2.getEndereco().getNumero(), resultado.get(1).getEndereco().getNumero()),
                    () -> assertEquals(clientePostPutRequestDTO2.getEndereco().getCep(), resultado.get(1).getEndereco().getCep()),
                    () -> assertEquals(clientePostPutRequestDTO2.getEndereco().getComplemento(), resultado.get(1).getEndereco().getComplemento())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do código de acesso")
    class ClienteVerificacaoCodigoAcesso {
        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente para nulo")
        void ClienteController_DeveFalhar_quandoAlteramosCodigoAcessoDoClienteParaNulo() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigoAcesso(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("CODIGO DE ACESSO DEVE TER EXATAMENTE 6 DIGITOS NUMERICOS"))),
                    () -> assertTrue(resultado.getErrors().stream().anyMatch(
                            (msg) -> msg.toUpperCase().contains("CODIGO DE ACESSO NÃO PODE SER VAZIO")
                    ))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente com mais de 6 digitos")
        void ClienteController_DeveFalhar_quandoAlteramosCodigoAcessoDoClienteComMaisDe6Digitos() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigoAcesso("1234567");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente com menos de 6 digitos")
        void ClienteController_DeveFalhar_quandoAlteramosCodigoAcessoDoClienteComMenosDe6Digitos() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigoAcesso("12345");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente com caracteres não numéricos")
        void ClienteController_DeveFalhar_quandoAlteramosCodigoAcessoDoClienteComCaracteresNaoNumericos() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigoAcesso("a*c4e@");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
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
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class ClienteVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando buscamos por todos clientes salvos")
        void ClienteController_DevePassar_quandoBuscamosPorTodosClienteSalvos() throws Exception {
            // Arrange

            // Vamos ter 3 clientes no banco
            Cliente cliente1 = Cliente.builder()
                    .nome("Cliente Dois Almeida")
                    .endereco(Endereco.builder()
                            .numero(100)
                            .cep("78111-103")
                            .complemento("Av. PitsA")
                            .build())
                    .codigoAcesso("246810")
                    .build();
            Cliente cliente2 = Cliente.builder()
                    .nome("Cliente Três Lima")
                    .endereco(Endereco.builder()
                            .numero(204)
                            .cep("00451-103")
                            .complemento("Distrito dos Testadores")
                            .build())
                    .codigoAcesso("135790")
                    .build();
            clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<ClienteGetResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<ClienteGetResponseDTO>>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(3, resultado.size()),
                    () -> assertTrue(resultado.stream().anyMatch(
                            (c) -> c.getId().longValue() == cliente1.getId())),
                    () -> assertTrue(resultado.stream().anyMatch(
                            (c) -> c.getId().longValue() == cliente2.getId())),
                    () -> assertTrue(resultado.stream().anyMatch(
                            (c) -> c.getId().longValue() == cliente.getId()))
            );
        }

        @Test
        @DisplayName("Quando buscamos um cliente salvo pelo id")
        void ClienteController_DevePassar_quandoBuscamosPorUmClienteSalvo() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteGetResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(cliente.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando buscamos um cliente inexistente")
        void ClienteController_DeveFalhar_quandoBuscamosPorUmClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + 999999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Cliente não existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com dados válidos")
        void ClienteController_DevePassar_quandoCriamosClienteValido() throws Exception {
            // Arrange

            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Capitu de Melo")
                    .endereco(Endereco.builder()
                            .numero(204)
                            .cep("00451-103")
                            .complemento("Distrito dos Testadores")
                            .build())
                    .email("campinafoood@gmail.com")
                    .codigoAcesso("122222")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome())
            );

        }

        @Test
        @DisplayName("Quando alteramos o cliente com dados válidos")
        void ClienteController_DevePassar_quandoAlteramosClienteComDadosValidos() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

            // Assert
            assertAll(
                    () -> assertEquals(cliente.getId(), resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente inexistente")
        void ClienteController_DeveFalhar_quandoAlteramosClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Cliente não existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente passando código de acesso inválido")
        void ClienteController_DeveFalhar_quandoAlteramosClienteCodigoAcessoInvalido() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "invalido")
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
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
        @DisplayName("Quando excluímos um cliente salvo")
        void ClienteController_DevePassar_quandoExcluimosClienteValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando excluímos um cliente inexistente")
        void ClienteController_DeveFalhar_quandoExcluimosClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Cliente não existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando excluímos um cliente salvo passando código de acesso inválido")
        void ClienteController_DeveFalhar_quandoExcluimosClienteComCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "invalido"))
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
    @DisplayName("Conjunto de casos de demonstrar interesse em sabores")
    class ClienteDemonstrarInteresseEmSabores {

        Estabelecimento estabelecimento;
        Sabor sabor;

        @BeforeEach
        void setUp() {
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoAcesso("654321")
                    .build()
            );
            sabor = saborRepository.save(Sabor.builder()
                    .nome("Sabor Um")
                    .tipo('S')
                    .precoM(10.0)
                    .precoG(20.0)
                    .disponivel(false)
                    .estabelecimento(estabelecimento)
                    .build());
        }

        @AfterEach
        void tearDown() {
            estabelecimentoRepository.deleteAll();
            saborRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando demonstramos interesse em um sabor válido")
        void ClienteController_DevePassar_quandoDemonstramosInteresseEmSaborValido() throws Exception {
            // Arrange
            ClienteInteresseRequestDTO clienteInteresseRequestDTO = ClienteInteresseRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .idEstabelecimento(estabelecimento.getId())
                    .sabor(sabor.getNome())
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteInteresseRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteInteresseResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteInteresseResponseDTO.class);
            // Assert
            assertAll(
                    () -> assertEquals(resultado.getNome(), "Cliente Um da Silva"),
                    () -> assertEquals(1, resultado.getInteresses().size()
                    )
            );
        }

        @Test
        @DisplayName("Quando demonstramos interesse em um sabor com código de acesso inválido")
        void ClienteController_DeveFalhar_quandoDemonstramosInteresseEmSaborComCodigoAcessoInvalido() throws Exception {
            // Arrange
            ClienteInteresseRequestDTO clienteInteresseRequestDTO = ClienteInteresseRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoAcesso("invalido")
                    .idEstabelecimento(estabelecimento.getId())
                    .sabor(sabor.getNome())
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteInteresseRequestDTO)))
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

        @Test
        @DisplayName("Quando demonstramos interesse em um sabor inexistente")
        void ClienteController_DeveFalhar_quandoDemonstramosInteresseEmSaborInexistente() throws Exception {
            // Arrange
            ClienteInteresseRequestDTO clienteInteresseRequestDTO = ClienteInteresseRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .idEstabelecimento(estabelecimento.getId())
                    .sabor("Inexistente")
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteInteresseRequestDTO)))
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
        @DisplayName("Quando um cliente inexistente demonstra interesse em um sabor")
        void ClienteController_DeveFalhar_quandoDemonstramosInteresseEmSaborComUmClienteInexistente() throws Exception {
            // Arrange
            ClienteInteresseRequestDTO clienteInteresseRequestDTO = ClienteInteresseRequestDTO.builder()
                    .idCliente(999999L)
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .idEstabelecimento(estabelecimento.getId())
                    .sabor(sabor.getNome())
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteInteresseRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Cliente não existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando um cliente demonstra interesse em um sabor que já está disponível")
        void ClienteController_DeveFalhar_quandoDemonstramosInteresseEmSaborJaDisponivel() throws Exception {
            // Arrange
            sabor.setDisponivel(true);
            saborRepository.save(sabor);

            ClienteInteresseRequestDTO clienteInteresseRequestDTO = ClienteInteresseRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .idEstabelecimento(estabelecimento.getId())
                    .sabor(sabor.getNome())
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteInteresseRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O sabor consultado ja esta disponivel!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando um cliente demonstra interesse em um sabor de um estabelecimento com id Invalido")
        void ClienteController_DeveFalhar_quandoOEstabelecimentoEInvalido() throws Exception {
            // Arrange
            ClienteInteresseRequestDTO clienteInteresseRequestDTO = ClienteInteresseRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoAcesso(cliente.getCodigoAcesso())
                    .idEstabelecimento((estabelecimento.getId() + 1))
                    .sabor(sabor.getNome())
                    .build();

            // Act
            String responseJsonString = driver.perform(patch(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteInteresseRequestDTO)))
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
}
