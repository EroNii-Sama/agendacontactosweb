package edu.nur.agendacontactosweb.repositories;

import edu.nur.agendacontactosweb.models.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactoRepository extends JpaRepository<Contacto, Integer> {

    Contacto findByContactoId(Integer contactoId);

    List<Contacto> findByUsuarioUsuarioId(Integer usuarioId);

}
