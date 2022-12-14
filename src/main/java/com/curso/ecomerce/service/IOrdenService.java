package com.curso.ecomerce.service;

import java.util.List;

import com.curso.ecomerce.model.Orden;

public interface IOrdenService {
	public List<Orden> findAll();
	public Orden save(Orden orden);
	public String generarNumeroOrden();
}
