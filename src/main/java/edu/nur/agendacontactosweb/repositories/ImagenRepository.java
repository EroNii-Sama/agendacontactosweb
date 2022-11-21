package edu.nur.agendacontactosweb.repositories;

import edu.nur.agendacontactosweb.models.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenRepository extends JpaRepository<Imagen, Integer> {

    Imagen findByImagenId(int imagenId);
}
