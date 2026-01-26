package org.cv.moa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling; // [추가]

@EnableScheduling // [추가]
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "org.cv.moa")
public class CMoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CMoaApplication.class, args);
    }

    @Bean
    ApplicationRunner printMappings(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) {
        return args -> {
            System.out.println("==== Registered MVC mappings ====");
            mapping.getHandlerMethods().forEach((info, method) -> {
                System.out.println(info + " -> " + method);
            });
            System.out.println("================================");
        };
    }
}
