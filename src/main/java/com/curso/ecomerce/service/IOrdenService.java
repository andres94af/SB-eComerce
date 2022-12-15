package com.curso.ecomerce.service;

import java.util.List;
import java.util.Optional;

import com.curso.ecomerce.model.Orden;
import com.curso.ecomerce.model.Usuario;

public interface IOrdenService {
	public List<Orden> findAll();
	Optional<Orden> findById (Integer id);
	public Orden save(Orden orden);
	public String generarNumeroOrden();
	public List<Orden> findByUsuario(Usuario usuario);
}
