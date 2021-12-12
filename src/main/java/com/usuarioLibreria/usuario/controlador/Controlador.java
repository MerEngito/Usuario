package com.usuarioLibreria.usuario.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controlador {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

}
