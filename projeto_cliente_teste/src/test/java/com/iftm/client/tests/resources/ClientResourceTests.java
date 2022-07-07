package com.iftm.client.tests.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourceTests {

	private int qtdClientes = 11;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	// Para o teste real da aplicação iremos comentar ou retirar.
	// @MockBean
	// private ClientService service;

	@Test
	public void testarListarTodosClientesRetornamOKeClientes() throws Exception {
		/* configuração do meu mock */

		// List<ClientDTO> listaClientes = new ArrayList<ClientDTO>();
		// listaClientes.add(new ClientDTO(
		// new Client(7l, "Jose Saramago", "10239254871", 5000.0,
		// Instant.parse("1996-12-23T07:00:00Z"), 0)));
		// listaClientes.add(new ClientDTO(new Client(4l, "Carolina Maria de Jesus",
		// "10419244771", 7500.0,
		// Instant.parse("1996-12-23T07:00:00Z"), 0)));
		// listaClientes.add(new ClientDTO(
		// new Client(8l, "Toni Morrison", "10219344681", 10000.0,
		// Instant.parse("1940-02-23T07:00:00Z"), 0)));
		// Page<ClientDTO> page = new PageImpl<ClientDTO>(listaClientes);
		// when(service.findAllPaged(any())).thenReturn(page);
		// qtdClientes = 3;

		// iremos realizar o teste
		mockMvc.perform(get("/clients")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 7L).exists())
				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 4L).exists())
				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 8L).exists())
				.andExpect(jsonPath("$.numberOfElements").value(qtdClientes));
	}

	@Test
	public void testarBuscaPorIdExistenteRetornaJsonCorreto() throws Exception {
		Long idExistente = 3L;
		ResultActions resultado = mockMvc.perform(get("/clients/{id}", 3L)
				.accept(MediaType.APPLICATION_JSON));

		resultado.andExpect(status().isOk());
		resultado.andExpect(jsonPath("$.id").exists());
		resultado.andExpect(jsonPath("$.id").value(idExistente));
		resultado.andExpect(jsonPath("$.name").exists());
		resultado.andExpect(jsonPath("$.name").value("Clarice Lispector"));
	}

	@Test
	public void testarBuscaPorIdNaoExistenteRetornaNotFound() throws Exception {
		Long idNaoExistente = 300L;
		ResultActions resultado = mockMvc.perform(get("/clients/{id}", idNaoExistente)
				.accept(MediaType.APPLICATION_JSON));

		resultado.andExpect(status().isNotFound());
		resultado.andExpect(jsonPath("$.error").exists());
		resultado.andExpect(jsonPath("$.error").value("Resource not found"));
		resultado.andExpect(jsonPath("$.message").value("Entity not found"));
		resultado.andExpect(jsonPath("$.status").value(404));
	}

	// Atividade: insert deveria retornar "created" (código 201), bem como o produto criado. Verifique no mínimo dois atributos.
	@Test
	public void insertRetornaCreatedEProdutoCriado() throws Exception {

		ClientDTO clientDTO = new ClientDTO(
			new Client(7l, "Ana Maria Silva", "00012345678", 7000.0, Instant.parse("2019-10-01T08:25:24.00Z"), 1));

		String json = objectMapper.writeValueAsString(clientDTO);

		ResultActions result = mockMvc.perform(post("/clients")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.cpf").exists());
		result.andExpect(jsonPath("$.cpf").value("00012345678"));
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.name").value("Ana Maria Silva"));
	}

	// Atividade: delete deveria retornar "no content" (código 204) quando o id existir
	@Test
	public void deleteRetornaNoContentQuandoIdExistir() throws Exception {
		Long idExistente = 3L;
		ResultActions resultado = mockMvc.perform(delete("/clients/{id}", idExistente)
				.accept(MediaType.APPLICATION_JSON));

		resultado.andExpect(status().is(204));
	}

	// Atividade: delete deveria retornar “not found” (código 404) quando o id não existir
	@Test
	public void deleteRetornaNotFoundQuandoIdNaoExistir() throws Exception {
		Long idInexistente = 1000L;
		ResultActions resultado = mockMvc.perform(delete("/clients/{id}", idInexistente)
				.accept(MediaType.APPLICATION_JSON));

		resultado.andExpect(status().isNotFound());
		resultado.andExpect(jsonPath("$.error").exists());
		resultado.andExpect(jsonPath("$.error").value("Resource not found"));
		resultado.andExpect(jsonPath("$.message").value("Id not found 1000"));
		resultado.andExpect(jsonPath("$.status").value(404));
	}

	// Atividade: findByIncome deveria retornar OK (código 200), bem como os clientes que tenham o Income informado.
	// Verificar se o Json Paginado tem a quantidade de clientes correta e se os clientes retornados são aqueles esperados.
	// (similar ao exemplo feito em sala de aula).
	@Test
	public void findByIncomeRetornaOk() throws Exception {

		double income = 1500.0;
		qtdClientes = 3;

		ResultActions resultado = mockMvc.perform(get("/clients/income/")
				.param("income", String.valueOf(income))
				.accept(MediaType.APPLICATION_JSON));

		resultado.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 1L).exists())
				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 9L).exists())
				.andExpect(jsonPath("$.content[?(@.id =='%s')]", 10L).exists())
				.andExpect(jsonPath("$.content[?(@.income =='%s')]", income).exists())
				.andExpect(jsonPath("$.content[?(@.income =='%s')]", income).exists())
				.andExpect(jsonPath("$.content[?(@.income =='%s')]", income).exists())
				.andExpect(jsonPath("$.numberOfElements").value(qtdClientes));
	}

	// Atividade: update deveria retornar “ok” (código 200), bem como o json do produto atualizado para um id existente,
    // verifique no mínimo dois atributos. (similar ao insert, precisa passar o json modificado).
	@Test
	public void updateRetornaOk() throws Exception {
		
		ClientDTO clientDTO = new ClientDTO(
			new Client(7l, "Ana Maria Silva", "00012345678", 8000.0,
				Instant.parse("1992-10-14T08:25:24.00Z"), 1));

		String json = objectMapper.writeValueAsString(clientDTO);

		ResultActions result = mockMvc.perform(put("/clients/{id}", 7l)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.cpf").exists());
		result.andExpect(jsonPath("$.cpf").value("00012345678"));
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.name").value("Ana Maria Silva"));
		result.andExpect(jsonPath("$.income").exists());
		result.andExpect(jsonPath("$.income").value(8000.0));
	}

	// Atividade: retornar “not found” (código 204) quando o id não existir. 
	// Fazer uma assertion para verificar no json de retorno se o campo “error” contém a string “Resource not found”.
	@Test
	public void updateRetornaNotFound() throws Exception {
		ClientDTO clientDTO = new ClientDTO(new Client(13l, "Ana Maria Silva", "00012345678", 5000.0,
				Instant.parse("1992-10-14T08:25:24.00Z"), 1));

		String json = objectMapper.writeValueAsString(clientDTO);

		ResultActions result = mockMvc.perform(put("/clients/{id}", 13l)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().is(404));
	}

}