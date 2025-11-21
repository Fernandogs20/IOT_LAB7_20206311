package pe.pucp.lab06.validacionservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/validar")
public class ValidacionController {

    private static final Pattern DNI_PATTERN = Pattern.compile("\\d{8}");
    private static final String PUCP_DOMAIN = "@pucp.edu.pe";

    @GetMapping("/dni/{dni}")
    public ResponseEntity<?> validarDni(@PathVariable String dni) {
        if (dni == null || !DNI_PATTERN.matcher(dni).matches()) {
            return ResponseEntity.badRequest().body("El DNI no tiene un formato valido");
        }
        return ResponseEntity.ok("DNI válido");
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<?> validarCorreo(@PathVariable String correo) {
        if (correo == null || !correo.endsWith(PUCP_DOMAIN)) {
            return ResponseEntity.badRequest().body("El correo no pertenece al dominio PUCP");
        }
        return ResponseEntity.ok("Correo válido");
    }
}
