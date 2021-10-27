package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;

/*Camada de Serviço*/
@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repository.findAll();
		
		/*
		  	List<CategoryDTO> listDto = new ArrayList<>();
		  
		  	for (Category cat : list) { listDto.add(new CategoryDTO(cat)); }
		 */
		
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());

	}
}
