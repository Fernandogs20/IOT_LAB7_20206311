Microservices skeleton for Lab06

This folder contains three Spring Boot skeleton projects:
- eureka-server (port 8761)
- validacion-service (port 8081)
- registro-service (port 8080)

Quick run (each project requires Java 17+ and Maven):

1. Start eureka-server:
   cd microservices/eureka-server
   mvn spring-boot:run

2. Start validacion-service:
   cd microservices/validacion-service
   mvn spring-boot:run

3. Start registro-service:
   cd microservices/registro-service
   mvn spring-boot:run

Notes:
- registro-service uses Feign client to call validacion-service
- All services register with Eureka running at http://localhost:8761
- Adjust application.properties if running on different hosts/ports
