package at.videc.survia;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableVaadin
@EnableConfigurationProperties
public class SurviaBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurviaBootApplication.class, args);
    }

}
