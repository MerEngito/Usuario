package com.usuarioLibreria.usuario.repositorio;


import com.usuarioLibreria.usuario.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    
@Query("SELECT c FROM Cliente c WHERE c.mail = :mail")
public Usuario buscarPorMail(@Param("mail") String mail);




//    @Query("SELECT c FROM Autor c WHERE c.nombre LIKE %:nombre%")
//    public List<Autor> listarAutoresPorNombre(@Param("nombre")String nombre);
//  
//    
    

}
