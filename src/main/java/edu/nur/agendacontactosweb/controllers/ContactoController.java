package edu.nur.agendacontactosweb.controllers;

import edu.nur.agendacontactosweb.models.Contacto;
import edu.nur.agendacontactosweb.models.Imagen;
import edu.nur.agendacontactosweb.models.TelefonoContacto;
import edu.nur.agendacontactosweb.models.Usuario;
import edu.nur.agendacontactosweb.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/contacto")
public class ContactoController {

    private final ContactoRepository contactoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ImagenRepository imagenRepository;
    private final TelefonoContactoRepository telefonoContactoRepository;

    @Autowired
    public ContactoController(ContactoRepository contactoRepository,
                              UsuarioRepository usuarioRepository,
                              ImagenRepository imagenRepository,
                              TelefonoContactoRepository telefonoContactoRepository) {
        this.contactoRepository = contactoRepository;
        this.usuarioRepository = usuarioRepository;
        this.imagenRepository = imagenRepository;
        this.telefonoContactoRepository = telefonoContactoRepository;
    }


    // GET /api/contacto/usuario/4
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Contacto>> getContactosUsuario(@PathVariable("usuarioId") Integer usuarioId){
        List<Contacto> contactos = contactoRepository.findByUsuarioUsuarioId(usuarioId);
        contactos.stream().forEach(c -> {
            if(c.getImagen() != null){
                c.setImagenId(c.getImagen().getImagenId());
            }
        });
        return ResponseEntity.ok(contactos);
    }

    // GET /api/contacto/32
    @GetMapping("/{contactoId}")
    public ResponseEntity<Contacto> getContactoById(@PathVariable Integer contactoId){
        Contacto contacto = contactoRepository.findByContactoId(contactoId);
        if(contacto == null){
            return ResponseEntity.notFound().build();
        }
        if(contacto.getImagen() != null){
            contacto.setImagenId(contacto.getImagen().getImagenId());
        }
        return ResponseEntity.ok(contacto);
    }

    @PostMapping()
    public ResponseEntity<Contacto> insertContacto(@RequestBody Contacto obj){
        Usuario usuario =  usuarioRepository.findByUsuarioId(obj.getUsuarioId());
        obj.setUsuario(usuario);

        for (TelefonoContacto telefono : obj.getTelefonos()) {
            telefono.setContacto(obj);
        }

        if(obj.getImagenId() > 0 ){
            Imagen img = imagenRepository.findByImagenId(obj.getImagenId());
            obj.setImagen(img);

            img.setTemporal(false);
            imagenRepository.save(img);
        }


        obj =contactoRepository.save(obj);
        return ResponseEntity.ok(obj);
    }

    // DELETE /api/contacto/4
    @DeleteMapping("/{contactoId}")
    public ResponseEntity<Boolean> eliminarContacto(@PathVariable Integer contactoId){
        Contacto contacto = contactoRepository.findByContactoId(contactoId);

        if(contacto.getImagen() != null){
            contacto.getImagen().setTemporal(true);
            imagenRepository.save(contacto.getImagen());
        }

        contactoRepository.delete(contacto);
        return ResponseEntity.ok(true);
    }


    @PutMapping()
    public ResponseEntity<Contacto> updateContacto(@RequestBody Contacto obj){
        Contacto storedContacto = contactoRepository.findByContactoId(obj.getContactoId());

        Imagen oldImage =null;
        if(storedContacto.getImagen() != null){
            oldImage = imagenRepository.findByImagenId(storedContacto.getImagen().getImagenId());
        }

        Imagen newImage = null;
        if(obj.getImagenId() > 0 ){
            newImage = imagenRepository.findByImagenId(obj.getImagenId());
        }

        if(oldImage != null && oldImage.getImagenId() != newImage.getImagenId()){

            oldImage.setTemporal(true);
            imagenRepository.save(oldImage);

        }

        if(newImage != null){
            newImage.setTemporal(false);
            imagenRepository.save(newImage);

            storedContacto.setImagen(newImage);
        }

        storedContacto.setNombreContacto(obj.getNombreContacto());
        storedContacto.setEmail(obj.getEmail());

        List<TelefonoContacto> toRemove = new ArrayList<>();

        for(TelefonoContacto telefonoContacto : storedContacto.getTelefonos()){

            TelefonoContacto editedPhone = getTelefonoContactoFromList(telefonoContacto.getTelefonoContactoId(), obj.getTelefonos());

            if(editedPhone != null){
                telefonoContacto.setNroContacto(editedPhone.getNroContacto());
            }else{
                toRemove.add(telefonoContacto);

            }
        }
        for(TelefonoContacto objToRemove : toRemove){
            int i = 0;
            for(TelefonoContacto telefono : storedContacto.getTelefonos()){
                if(telefono.getTelefonoContactoId() == objToRemove.getTelefonoContactoId()){
                    storedContacto.getTelefonos().remove(i);
                    break;
                }
                i++;
            }
            objToRemove.setContacto(null);
            telefonoContactoRepository.delete(objToRemove);
        }
        for(TelefonoContacto telefonoContacto : obj.getTelefonos()){
            if(telefonoContacto.getTelefonoContactoId() == 0){
                telefonoContacto.setContacto(storedContacto);
                storedContacto.getTelefonos().add(telefonoContacto);
            }
        }

        storedContacto =contactoRepository.save(storedContacto);
        return ResponseEntity.ok(storedContacto);
    }

    private TelefonoContacto getTelefonoContactoFromList(int telefonoContactoId, List<TelefonoContacto> telefonos){
        for(TelefonoContacto telefono : telefonos){
            if(telefono.getTelefonoContactoId() == telefonoContactoId){
                return telefono;
            }
        }
        return null;
    }
}
