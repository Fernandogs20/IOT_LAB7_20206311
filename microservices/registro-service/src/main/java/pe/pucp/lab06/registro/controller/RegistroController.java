package pe.pucp.lab06.registro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pe.pucp.lab06.registro.feign.ValidacionClient;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RegistroController {

    @Autowired
    private ValidacionClient validacionClient;

    static class RegistroRequest {
        public String dni;
        public String correo;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            ResponseEntity<String> dniResp = validacionClient.validarDni(request.dni);
            if (dniResp != null && !dniResp.getStatusCode().is2xxSuccessful()) {
                response.put("mensaje", dniResp.getBody());
                response.put("success", false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ResponseEntity<String> correoResp = validacionClient.validarCorreo(request.correo);
            if (correoResp != null && !correoResp.getStatusCode().is2xxSuccessful()) {
                response.put("mensaje", correoResp.getBody());
                response.put("success", false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Si llega aquí, las validaciones pasaron
            response.put("mensaje", "Registro válido");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("mensaje", "Error al comunicarse con servicio de validación: " + ex.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
