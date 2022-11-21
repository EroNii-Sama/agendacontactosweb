package edu.nur.agendacontactosweb.controllers;

import edu.nur.agendacontactosweb.models.Usuario;
import edu.nur.agendacontactosweb.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import javax.swing.text.html.parser.Entity;

@RestController
@RequestMapping("api/usuario")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // POST /api/usuario/login
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario loginUser) {
        Usuario storedUser = usuarioRepository.findByUserName(loginUser.getUserName());
        if (storedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!storedUser.getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        storedUser.setPassword("");
        return ResponseEntity.ok(storedUser);

    }
}
