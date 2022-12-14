package com.curso.ecomerce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.curso.ecomerce.model.Usuario;
import com.curso.ecomerce.service.IDetalleOrdenService;
import com.curso.ecomerce.service.IOrdenService;
import com.curso.ecomerce.service.IUsuarioService;
import com.curso.ecomerce.service.ProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	@Autowired
	private IDetalleOrdenService detalleOrdenService;
	
	//almacena detalles de la orden
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();
	
	//datos de la orden
	Orden orden = new Orden();
	
	@GetMapping("")
	public String home(Model model, HttpSession session) {
		
		log.info("Sesion del usuraio: {}", session.getAttribute("idusuario"));
		
		model.addAttribute("productos", productoService.findAll());
		
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
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
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;
		
		Optional<Producto> optionalProducto = productoService.get(id);
		producto = optionalProducto.get();
		log.info("Prorducto a??adido: {}", producto);
		log.info("Cantidad: {}", cantidad);
		
		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio()*cantidad);
		detalleOrden.setProducto(producto);
		
		//validar que el producto no se a??ada dos veces
		Integer idProducto = producto.getId();
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId() == idProducto);
		
		if (!ingresado) {
			detalles.add(detalleOrden);			
		}
		
		sumaTotal = detalles.stream().mapToDouble(dt-> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		return "usuario/carrito";
	}
	
	//quitar producto del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {
		
		List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();
		
		for(DetalleOrden detalleOrden : detalles) {
			if(detalleOrden.getProducto().getId()!=id) {
				ordenesNueva.add(detalleOrden);
			}
		}
		
		detalles = ordenesNueva;
		
		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt-> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		return "usuario/carrito";
	}
	
	@GetMapping("/getCart")
	public String getCart (Model model, HttpSession session) {
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "usuario/carrito";
	}
	
	@GetMapping("/order")
	public String order (Model model, HttpSession session) {
		
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		
		model.addAttribute("usuario", usuario);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "usuario/resumenorden";
	}
	
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session) {
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		orden.setUsuario(usuario);
		ordenService.save(orden);
		
		//guardar detaller
		for(DetalleOrden dt:detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		
		///limpiar lista y orden
		orden = new Orden();
		detalles.clear();		
		
		return "redirect:/";
	}
	
	@PostMapping("/search")
	public String searchProduct(@RequestParam String nombre, Model model) {
		log.info("Nombre del producto buscado: {}", nombre);
		List<Producto> productos = productoService.findAll().stream()
				.filter(p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

}
