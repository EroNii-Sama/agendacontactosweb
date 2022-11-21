package edu.nur.agendacontactosweb.controllers;

import edu.nur.agendacontactosweb.AppConfig;
import edu.nur.agendacontactosweb.models.Imagen;
import edu.nur.agendacontactosweb.repositories.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("api/image")
public class ImageController {

    private final ImagenRepository imagenRepository;
    private final AppConfig appConfig;

    @Autowired
    public ImageController(ImagenRepository imagenRepository, AppConfig appConfig) {
        this.imagenRepository = imagenRepository;
        this.appConfig = appConfig;
    }

    @PostMapping()
    public ResponseEntity<Integer> uploadImage(@RequestBody MultipartFile file){
        Path fileStorageLocation = Paths.get(appConfig.getUploadDir())
                .toAbsolutePath().normalize();
        String fileName = file.getOriginalFilename();
        String newFileName = UUID.randomUUID().toString();

        boolean success = false;
        Path targetLocation = fileStorageLocation.resolve(newFileName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!success){
            return ResponseEntity.status(500).body(0);
        }

        Imagen img = new Imagen();
        img.setFechaSubida(new Date());
        img.setTemporal(true);
        img.setFileName(fileName);
        img.setPath(targetLocation.toString());

        img = imagenRepository.save(img);

        return ResponseEntity.ok(img.getImagenId());
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Integer imageId){

        Imagen img = imagenRepository.findByImagenId(imageId);
        if(img == null)
            return ResponseEntity.notFound().build();

        Path path = Paths.get(img.getPath());

        byte[] file = null;
        ByteArrayResource resource = null;
        try {
            file = Files.readAllBytes(path);
            resource = new ByteArrayResource(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + img.getFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }
}
