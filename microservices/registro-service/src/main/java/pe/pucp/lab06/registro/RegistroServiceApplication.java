package pe.pucp.lab06.registro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class RegistroServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegistroServiceApplication.class, args);
    }
}
