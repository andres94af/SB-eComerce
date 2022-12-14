package com.curso.ecomerce.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringBootSecurity {

  @Bean
  public BCryptPasswordEncoder getEncoder() {
      return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  	http.csrf().disable().authorizeRequests()
  		.requestMatchers("/administrador/**").hasRole("ADMIN")
  		.requestMatchers("/productos/**").hasRole("ADMIN")
  		.and().formLogin().loginPage("/usuario/login")
  		.permitAll().defaultSuccessUrl("/usuario/acceder");
  	
		return http.build();
  }
}
