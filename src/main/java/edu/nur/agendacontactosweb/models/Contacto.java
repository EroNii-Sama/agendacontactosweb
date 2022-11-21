package edu.nur.agendacontactosweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int contactoId;

    private String nombreContacto;
    private String email;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "imagenId", nullable = true)
    private Imagen imagen;

    @JsonInclude
    @Transient
    private int imagenId;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "usuarioId", nullable = false)
    private Usuario usuario;

    @JsonInclude
    @Transient
    private int usuarioId;

    @OneToMany(mappedBy = "contacto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TelefonoContacto> telefonos;

    public Contacto(){
        telefonos = new ArrayList<>();
    }

    public void setUsuario(Usuario user){
        this.usuario = user;
        if(user != null){
            this.usuarioId = user.getUsuarioId();
        }else{
            this.usuarioId = 0;
        }

    }

    public void setImagen(Imagen img){
        this.imagen = img;
        if(img != null){
            this.imagenId = img.getImagenId();
        }else{
            this.imagenId = 0;
        }
    }
}
