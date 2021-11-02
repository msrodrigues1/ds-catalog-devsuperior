package com.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entities.Client;

/*Camada de Acesso a dados*/
@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{

}
