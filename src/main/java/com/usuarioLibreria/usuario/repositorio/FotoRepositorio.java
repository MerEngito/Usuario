package com.usuarioLibreria.usuario.repositorio;


import com.usuarioLibreria.usuario.entidad.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepositorio extends JpaRepository<Foto, String>{

    
    
    
}
