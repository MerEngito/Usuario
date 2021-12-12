package com.usuarioLibreria.usuario;

import com.usuarioLibreria.usuario.servicio.UsuarioSevicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class UsuarioApplication {

    @Autowired
    private UsuarioSevicio usuarioSevicio;

    public static void main(String[] args) {
        SpringApplication.run(UsuarioApplication.class, args);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usuarioSevicio)
                .passwordEncoder(new BCryptPasswordEncoder());

    }
}
