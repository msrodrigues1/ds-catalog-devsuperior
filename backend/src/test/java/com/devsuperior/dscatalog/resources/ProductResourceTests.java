package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private long existingID;
	private long notExistingID;
	private long dependentId;
	private PageImpl<ProductDTO> page; 
	private ProductDTO productDTO;
	
	@BeforeEach
	void setUp() throws Exception {

		existingID = 1L;
		dependentId = 3L;
		notExistingID = 1000L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO)); //instanciando uma pagina na mão - Objeto valido representando uma pagina
		
		Mockito.when(service.findAllPaged((Pageable) ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(service.findById(existingID)).thenReturn(productDTO);
		Mockito.when(service.findById(notExistingID)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.updateProduct(eq(existingID),  ArgumentMatchers.any())).thenReturn(productDTO);
		Mockito.when(service.updateProduct(eq(notExistingID),  ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.insertProduct(ArgumentMatchers.any())).thenReturn(productDTO);
		
		doNothing().when(service).deleteProduct(existingID);
		doThrow(ResourceNotFoundException.class).when(service).deleteProduct(notExistingID);
		doThrow(DatabaseException.class).when(service).deleteProduct(dependentId);
	}
	
	@Test
	void findAllShouldReturnPage() throws Exception {
		
		mockMvc
			.perform(get("/products")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

	}
	
	@Test
	void findByIdShouldReturnProductWhenIdExists() throws Exception {
		mockMvc
			.perform(get("/products/{id}", existingID)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.name").exists())
			.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		
		mockMvc
			.perform(get("/products/{id}", notExistingID)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status()
			.isNotFound());

	}
	
	@Test
	void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		mockMvc
			.perform(put("/products/{id}", existingID)
			.content(jsonBody)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.name").exists())
			.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		mockMvc
			.perform(put("/products/{id}", notExistingID)
			.content(jsonBody)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
	@Test
	void insertShouldReturnProductDTO() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		mockMvc
			.perform(post("/products")
			.content(jsonBody)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	void deleteShouldReturnProductDTOWhenIdExists() throws Exception {
		mockMvc
			.perform(delete("/products/{id}", existingID))
			.andExpect(status().isNoContent());
	}
	
	@Test
	void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		mockMvc
			.perform(delete("/products/{id}", notExistingID))
			.andExpect(status().isNotFound());
	}
	
	@Test
	void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
		mockMvc
			.perform(delete("/products/{id}", dependentId))
			.andExpect(status().isBadRequest());
	}
	
	
}
