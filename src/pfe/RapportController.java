package pfe;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rapports")
public class RapportController {

    private final Map<String, Rapport> rapports = new LinkedHashMap<>();

    public RapportController() {
        rapports.put("R001", new Rapport("R001", "Rapport de stage - Application mobile"));
    }

    private ResponseEntity<Map<String, String>> erreur(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(Map.of("erreur", message));
    }

    // GET /api/rapports -> 200
    @GetMapping
    public List<RapportDTO> lister() {
        List<RapportDTO> liste = new ArrayList<>();
        for (Rapport r : rapports.values()) {
            liste.add(new RapportDTO(r));
        }
        return liste;
    }

    // GET /api/rapports/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<?> lire(@PathVariable String id) {
        Rapport r = rapports.get(id);
        if (r == null) {
            return erreur(HttpStatus.NOT_FOUND, "Rapport introuvable : " + id);
        }
        return ResponseEntity.ok(new RapportDTO(r));
    }

    // POST /api/rapports -> 201, 400 ou 409
    @PostMapping
    public ResponseEntity<?> creer(@RequestBody RapportDTO dto) {
        if (dto.getIdentifiant() == null || dto.getIdentifiant().isBlank()
                || dto.getTitre() == null || dto.getTitre().isBlank()) {
            return erreur(HttpStatus.BAD_REQUEST, "L'identifiant et le titre sont obligatoires.");
        }        String erreurTitre = RapportValidator.verifierTitre(dto.getTitre());
        if (erreurTitre != null) {
            return erreur(HttpStatus.BAD_REQUEST, erreurTitre);
        }
        if (rapports.containsKey(dto.getIdentifiant())) {
            return erreur(HttpStatus.CONFLICT, "Un rapport avec cet identifiant existe déjà.");
        }
        Rapport r = new Rapport(dto.getIdentifiant(), dto.getTitre());
        rapports.put(r.getIdentifiant(), r);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RapportDTO(r));
    }

    // PATCH /api/rapports/{id}/validation -> 200, 400 ou 404
    @PatchMapping("/{id}/validation")
    public ResponseEntity<?> valider(@PathVariable String id, @RequestBody Map<String, Double> corps) {
        Rapport r = rapports.get(id);
        if (r == null) {
            return erreur(HttpStatus.NOT_FOUND, "Rapport introuvable : " + id);
        }
        Double note = corps.get("note");
        if (note == null) {
            return erreur(HttpStatus.BAD_REQUEST, "Le champ note est obligatoire.");
        }
        try {
            r.validerRapport(note);
        } catch (pfeException e) {
            return erreur(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok(new RapportDTO(r));
    }
}