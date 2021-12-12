package com.usuarioLibreria.usuario.servicio;

import com.usuarioLibreria.usuario.entidad.Foto;
import com.usuarioLibreria.usuario.entidad.Usuario;
import com.usuarioLibreria.usuario.enumeraciones.Rol;
import com.usuarioLibreria.usuario.excepcion.Excepciones;
import com.usuarioLibreria.usuario.repositorio.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioSevicio implements UserDetailsService {//implements UserDetailsService{

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Usuario registrar(MultipartFile archivo, String documento, String nombreCliente, String apellido, String telefono, String clave, String mail) throws Exception {

        validar(documento, nombreCliente, apellido, telefono, clave, mail);

        Usuario usuario = new Usuario();

        usuario.setDocumento(documento);
        usuario.setNombreCliente(nombreCliente);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        usuario.setMail(mail);

        usuario.setRol(Rol.USUARIO);

        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);

        usuario.setAlta(new Date());

        Foto foto = fotoServicio.guardarFoto(archivo);
        usuario.setFoto(foto);

        return usuarioRepositorio.save(usuario);
    }

    //--------------------------VALIDAR--------------------------------------------------------------------------------------------------------------------------------------------------------   

    private void validar(String documento, String nombreCliente, String apellido, String telefono, String clave, String mail) throws Excepciones {

        if (documento == null || documento.isEmpty()) {

            throw new Excepciones("El Documento no puede ser nulo");
        }
        if (nombreCliente == null || nombreCliente.isEmpty()) {

            throw new Excepciones("El nombre no puede ser nulo");
        }
        if (apellido == null || apellido.isEmpty()) {

            throw new Excepciones("El Apellido no puede ser nulo");
        }
        if (telefono == null || telefono.isEmpty()) {

            throw new Excepciones("El Telefono no puede ser nulo");
        }
        if (mail == null || mail.isEmpty()) {

            throw new Excepciones("El Mail no puede ser nulo");
        }
        if (clave == null || clave.isEmpty() || clave.length() <= 6) {

            throw new Excepciones("La Clave no puede ser nulo y tiene que tener mas de 6 digitos");
        }

    }

    //--------------------------REGISTRAR------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Transactional
    public void registrar1(MultipartFile archivo, String documento , String nombreCliente, String apellido, String telefono, String clave, String mail) throws Excepciones {

        validar(documento, nombreCliente, apellido, telefono, clave, mail);

        Usuario usuario = new Usuario();
        usuario.setDocumento(documento);
        usuario.setNombreCliente(nombreCliente);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        usuario.setClave(clave);
        usuario.setMail(mail);
        usuario.setRol(Rol.USUARIO);

        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);

        usuario.setAlta(new Date());

        Foto foto = fotoServicio.guardarFoto(archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);

    }

    //--------------------------MODIFICAR-------------------------------------------------------------------------------------------------------------------------------------------------------
   
    @Transactional
    public void modificar(MultipartFile archivo, String id, String documento, String nombreCliente, String apellido, String telefono,  String clave, String mail) throws Excepciones {

        validar(documento, nombreCliente, apellido, telefono, clave, mail);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setDocumento(documento);
            usuario.setNombreCliente(nombreCliente);
            usuario.setApellido(apellido);
            usuario.setTelefono(telefono);
            usuario.setClave(clave);
            usuario.setMail(mail);
            

            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);

            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizarFoto(idFoto, archivo);
            usuario.setFoto(foto);

            usuarioRepositorio.save(usuario);
        } else {

            throw new Excepciones("No se encontró el usuario solicitado");
        }

    }

    //--------------------------DESHABILITAR-----------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Transactional
    public void deshabilitar(String id) throws Excepciones {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());
            usuarioRepositorio.save(usuario);
        } else {

            throw new Excepciones("No se encontró el usuario solicitado");
        }

    }

    //--------------------------HABILITAR USUARIO-------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Transactional
    public void habilitar(String id) throws Excepciones {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setBaja(null);
            usuarioRepositorio.save(usuario);
        } else {

            throw new Excepciones("No se encontró el usuario solicitado");
        }

    }

    //---------------------------CAMBIAR ROL--------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Transactional
    public void cambiarRol(String id) throws Excepciones {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();

            if (usuario.getRol().equals(Rol.USUARIO)) {

                usuario.setRol(Rol.ADMIN);

            } else if (usuario.getRol().equals(Rol.ADMIN)) {
                usuario.setRol(Rol.USUARIO);
            }
        }
    }

    
    
    @Transactional(readOnly = true)
    public Usuario buscarPorId(String id) throws Excepciones {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario Usuario = respuesta.get();
            return Usuario;
        } else {

            throw new Excepciones("No se encontró el usuario solicitado");
        }

    }

    
    @Transactional(readOnly = true)
    public List<Usuario> todosLosClientes() {

        return usuarioRepositorio.findAll();

    }

    
    
    @Transactional(readOnly = true)
    public List<Usuario> todosLosUsuarios() {

        return usuarioRepositorio.findAll();

    }

    private Usuario buscarPorMail(String mail) {

        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();

            //Creo una lista de permisos! 
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());
            permisos.add(p1);

            //Esto me permite guardar el OBJETO USUARIO LOG, para luego ser utilizado
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario); // llave + valor

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);

            return user;

        } else {
            return null;
        }

    }
}

//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import java.util.List;
//import java.util.ArrayList;
//    @Override
//    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
//            
//        
//             Cliente cliente = clienteRepositorio.buscarPorMail(mail);
//             if(cliente != null){
//           
//            List<GrantedAuthority> permisos = new ArrayList<>();
//                    
//            GrantedAuthority p1 = new SimpleGrantedAuthority("MODULO_PRESTAMO");
//            permisos.add(p1);
//            
//            GrantedAuthority p2 = new SimpleGrantedAuthority("MODULO_PRESTAMO");
//            permisos.add(p2);
//            
//            GrantedAuthority p3 = new SimpleGrantedAuthority("MODULO_PRESTAMO");
//            permisos.add(p3);
//            
//           User user = new User(cliente.getMail(), cliente.getClave(), permisos);
//           return user;
//        }else{
//            return null;
//        }
//    }
//public void validar(String nombre, String apellido, String mail, String clave, String clave2) throws Excepciones {
//
//        if (nombre == null || nombre.isEmpty()) {
//            throw new Excepciones("El nombre del usuario no puede ser nulo");
//        }
//
//        if (apellido == null || apellido.isEmpty()) {
//            throw new Excepciones("El apellido del usuario no puede ser nulo");
//        }
//
//        if (mail == null || mail.isEmpty()) {
//            throw new Excepciones("El mail no puede ser nulo");
//        }
//
//        if (clave == null || clave.isEmpty() || clave.length() <= 6) {
//            throw new Excepciones("La clave del usuario no puede ser nula y tiene que tener mas de seis digitos");
//        }
//        if (!clave.equals(clave2)) {
//            throw new Excepciones("Las claves deben ser iguales");
//        }
//    }
// @Transactional
//    private void modificar1(MultipartFile archivo, String id, String documento, String nombreCliente, String apellido, String telefono, String clave, String mail) throws Exception {
//        validar(documento, nombreCliente, apellido, telefono, clave, mail);
//
//        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
//
//        if (respuesta.isPresent()) {
//            Usuario usuario = respuesta.get();
//            usuario.setDocumento(documento);
//            usuario.setNombreCliente(nombreCliente);
//            usuario.setApellido(apellido);
//            usuario.setTelefono(telefono);
//            usuario.setMail(mail);
//            
//            String encriptada = new BCryptPasswordEncoder().encode(clave);
//            usuario.setClave(encriptada);
//
//            String idFoto = null;
//            if (usuario.getFoto() != null) {
//                idFoto = usuario.getFoto().getId().toString();
//            }
//
//            Foto foto = fotoServicio.actualizarFoto(idFoto, archivo);
//            usuario.setFoto(foto);
//
//            usuarioRepositorio.save(usuario);
//        } else {
//            throw new Excepciones("No se Encontro el Cliente Solicitado. ");
//        }
//    }