package pfe;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Map<String, String> utilisateurs = new ConcurrentHashMap<>();

    public AuthController() {
        utilisateurs.put("etudiant", "test123");
    }

    private ResponseEntity<Map<String, String>> erreur(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(Map.of("erreur", message));
    }

    // POST /api/auth/login -> 200 ou 401
    @PostMapping("/login")
    public ResponseEntity<?> connecter(@RequestBody Map<String, String> corps, HttpSession session) {
        String u = corps.get("username");
        String p = corps.get("password");
        if (u == null || p == null || !p.equals(utilisateurs.get(u))) {
            return erreur(HttpStatus.UNAUTHORIZED, "Identifiant ou mot de passe incorrect");
        }
        session.setAttribute(AuthConfig.SESSION_KEY, u);
        return ResponseEntity.ok(Map.of("utilisateur", u));
    }

    // POST /api/auth/register -> 201, 400 ou 409
    @PostMapping("/register")
    public ResponseEntity<?> inscrire(@RequestBody Map<String, String> corps) {
        String u = corps.get("username");
        String p = corps.get("password");
        if (u == null || u.isBlank() || p == null || p.isBlank()) {
            return erreur(HttpStatus.BAD_REQUEST, "Tous les champs sont obligatoires");
        }
        if (p.length() < 4) {
            return erreur(HttpStatus.BAD_REQUEST, "Le mot de passe doit faire au moins 4 caractères");
        }
        if (utilisateurs.putIfAbsent(u, p) != null) {
            return erreur(HttpStatus.CONFLICT, "Cet identifiant est déjà pris");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("utilisateur", u));
    }

    // POST /api/auth/logout -> 204
    @PostMapping("/logout")
    public ResponseEntity<?> deconnecter(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}