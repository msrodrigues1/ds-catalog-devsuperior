package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

/** Teste de Unidade - Validar aquele componente especifico - São rapidos **/

@ExtendWith(SpringExtension.class)
class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingID;
	private long notExistingID;
	private long dependentId;
	private PageImpl<Product> page; 
	private Product product;
	private ProductDTO productDTO;
	private Category category;

	@BeforeEach
	void setUp() throws Exception {
		existingID = 1L;
		notExistingID = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product)); //instanciando uma pagina na mão - Objeto valido representando uma pagina
		
		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.findById(existingID)).thenReturn(Optional.of(product));
		
		Mockito.when(repository.findById(notExistingID)).thenReturn(Optional.empty());
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		/* Quando eu chamar deleteById com o id existing esse metodo não vai fazer nada. */
		Mockito.doNothing().when(repository).deleteById(existingID);
		/* Quando eu chamar deleteById com o id not existing, esse metodo vai retornar um Throw EmptyResultDataAccessException. */
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(notExistingID);
		/* Quando eu chamar deleteById e esse ID estiver associado a algum outro objeto, esse metodo vai retornar um Throw DataIntegrityViolationException. */
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(repository.getOne(existingID)).thenReturn(product);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(notExistingID);
		
		Mockito.when(categoryRepository.getOne(existingID)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(notExistingID)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
	}
	
	@Test
	void findByIdShouldReturnProductDTOWhenIdExisting() {
		ProductDTO result = service.findById(existingID);
				
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).findById(existingID);
	}
	
	@Test
	void findByIdShouldThrowResourceNotFoundExceptionWhenNotIdExisting() {
	
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(notExistingID);
		});
		
		Mockito.verify(repository, Mockito.times(1)).findById(notExistingID);
	}
	
	@Test
	void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pageable);
				
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
	}
	
	@Test
	void updateProductShouldReturnProductDTOWhenIdExisting() {
		ProductDTO result = service.updateProduct(existingID, productDTO);
				
		Assertions.assertNotNull(result);

		Mockito.verify(repository, Mockito.times(1)).getOne(existingID);
		Mockito.verify(repository, Mockito.times(1)).save(product);
		
	}
	
	@Test
	void updateProductShouldThrowResourceNotFoundExceptionWhenNotIdExisting() {
	
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.updateProduct(notExistingID, productDTO);
		});

	}
	
	@Test
	void deleteShouldDoNothingWhenIdExistis() {
		Assertions.assertDoesNotThrow(() -> {
			service.deleteProduct(existingID);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingID);
	}
	
	@Test
	void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.deleteProduct(notExistingID);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(notExistingID);
	}
	
	@Test
	void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.deleteProduct(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
}
