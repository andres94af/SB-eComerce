package com.curso.ecomerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.curso.ecomerce.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}
