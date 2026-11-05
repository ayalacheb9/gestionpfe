package pfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestionAffectationsTest {

    private GestionAffectations gestion;
    private Etudiant ali;
    private Etudiant sara;
    private Encadrant encadrant;
    private Projet projet;

    // Fixture : objets neufs avant chaque test
    @BeforeEach
    void preparer() {
        gestion = new GestionAffectations();
        ali = new Etudiant("ET001", "Ali", "Ben Salah", "Informatique", "Genie Logiciel");
        sara = new Etudiant("ET002", "Sara", "Mejri", "Informatique", "Réseaux");
        encadrant = new Encadrant("EN001", "Ahmed", "Khalil", "Informatique");
        projet = new Projet("P001", "AI", "2024-01-01", "2024-06-01");
    }

    @Test
    void affecterProjet_projetEtEncadrantDisponibles_neLevePasException() {
        // Arrange : projet et encadrant neufs (disponibles)
        // Act + Assert
        assertDoesNotThrow(() -> gestion.affecterProjet(ali, projet, encadrant));
    }

    @Test
    void affecterProjet_projetDejaAffecte_leveUneException() throws pfeException {
        // Arrange : le projet est deja affecte a Ali
        gestion.affecterProjet(ali, projet, encadrant);
        // Act + Assert : une deuxieme affectation du meme projet doit etre refusee
        assertThrows(pfeException.class, () -> gestion.affecterProjet(sara, projet, encadrant));
    }
}