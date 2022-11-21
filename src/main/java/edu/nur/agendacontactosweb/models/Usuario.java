package edu.nur.agendacontactosweb.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int usuarioId;

    @Size(max = 100)
    private String fullName;
    @Size(max = 50)
    private String userName;
    @Size(max = 500)
    private String password;

}
