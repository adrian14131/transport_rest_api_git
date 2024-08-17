package pl.ozog.transport_rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TransportRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransportRestApiApplication.class, args);

    }

}
