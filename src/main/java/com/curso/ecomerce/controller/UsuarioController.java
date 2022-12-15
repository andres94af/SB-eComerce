package com.curso.ecomerce.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.curso.ecomerce.model.Orden;
import com.curso.ecomerce.model.Usuario;
import com.curso.ecomerce.service.IOrdenService;
import com.curso.ecomerce.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	private final Logger LOGG = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();
	
	@GetMapping("/registro")
	public String create () {
		return "usuario/registro";
	}
	
	@PostMapping("/save")
	public String save(Usuario usuario) {
		LOGG.info("Usuario registro: {}", usuario);
		usuario.setTipo("USER");
		usuario.setPassword(passEncode.encode(usuario.getPassword()));
		usuarioService.save(usuario);
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login() {
		return "usuario/login";
	}
	
	@PostMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) {
		LOGG.info("Acceso: {}", usuario);
		
		Optional<Usuario> user = usuarioService.findByEmail(usuario.getEmail());
		//LOGG.info("Usuario de la BD: {}", user.get());
		
		if (user.isPresent()) {
			session.setAttribute("idusuario", user.get().getId());
			if (user.get().getTipo().equals("ADMIN")) {
				return "redirect:/administrador";
			}else {
				return "redirect:/";
			}
		}else {
			LOGG.info("Usuario no existe");
		}
		return "redirect:/";
	}
	
	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		Usuario u = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		List<Orden> ordenes = ordenService.findByUsuario(u);
		
		model.addAttribute("ordenes", ordenes);
		
		return "usuario/compras";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		LOGG.info("ID de la orden: {}", id);
		Optional<Orden> orden = ordenService.findById(id);
		model.addAttribute("detalles", orden.get().getDetalle());
		
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		return "usuario/detallecompra";
	}
	
	@GetMapping("/cerrar")
	public String cerrarSesion(HttpSession session) {
		session.removeAttribute("idusuario");
		return "redirect:/";
	}

}
