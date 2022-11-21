package edu.nur.agendacontactosweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class TelefonoContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int telefonoContactoId;

    private String nroContacto;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "contactoId", nullable = false)
    private Contacto contacto;
}
