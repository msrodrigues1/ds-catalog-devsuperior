package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private long existingID;
	private long notExistingID;
	private long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {
		existingID = 1L;
		notExistingID = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product);
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
		
	}
	
	@Test
	void deleteShouldDeleteObjectWhenIdExistis() {
		repository.deleteById(existingID);

		Optional<Product> result = repository.findById(existingID);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(notExistingID);
		});
	}

	@Test
	void findByIdShouldNonEmptyObjectWhenIdExistis() {
		Optional<Product> result = repository.findById(existingID);
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	void findByIdShouldEmptyObjectWhenIdNotExistis() {
		Optional<Product> result = repository.findById(notExistingID);
		Assertions.assertTrue(result.isEmpty());
	}
}
