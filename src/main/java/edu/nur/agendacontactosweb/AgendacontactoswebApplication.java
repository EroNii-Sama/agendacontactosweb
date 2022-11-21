package edu.nur.agendacontactosweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        AppConfig.class
})
public class AgendacontactoswebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgendacontactoswebApplication.class, args);
    }

}
