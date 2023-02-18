package ru.practicum.ewm.main;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.practicum.ewm.stats.client.StatClient;

@SpringBootApplication
public class EwmMainService {

    @Value("${statserver.url}")
    private String serverUrl;

    @Bean
    public StatClient statClientBean() {
        return new StatClient(serverUrl);
    }

    public static void main(String[] args) {
        SpringApplication.run(EwmMainService.class, args);
    }

}
