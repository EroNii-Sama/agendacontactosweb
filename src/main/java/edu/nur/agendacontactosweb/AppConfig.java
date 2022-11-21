package edu.nur.agendacontactosweb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String uploadDir;
}
