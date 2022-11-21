package edu.nur.agendacontactosweb.repositories;

import edu.nur.agendacontactosweb.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByUsuarioId(int usuarioId);

    Usuario findByUserName(String userName);
}
