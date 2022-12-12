package com.curso.ecomerce.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.curso.ecomerce.model.DetalleOrden;
import com.curso.ecomerce.model.Orden;
import com.curso.ecomerce.model.Producto;
import com.curso.ecomerce.service.ProductoService;

@Controller
@RequestMapping("/")
public class HomeController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProductoService productoService;
	
	//almacena detalles de la orden
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();
	
	//datos de la orden
	Orden orden = new Orden();
	
	@GetMapping("")
	public String home(Model model) {
		model.addAttribute("productos", productoService.findAll());
		return "usuario/home";
	}
	
	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable("id") Integer id, Model model) {
		log.info("Id enviado como parametro: {}", id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();
		model.addAttribute("producto", producto);
		return "usuario/productohome";
	}
	
	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad) {
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;
		
		Optional<Producto> optionalProducto = productoService.get(id);
		producto = optionalProducto.get();
		
		log.info("Prorducto añadido: {}", producto);
		log.info("Cantidad: {}", cantidad);
		
		return "usuario/carrito";
	}

}
