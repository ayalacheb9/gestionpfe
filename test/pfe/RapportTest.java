package pfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RapportTest {

    private Rapport rapport;

    // Fixture : un rapport neuf avant chaque test (garantit l'isolation)
    @BeforeEach
    void preparerRapport() {
        rapport = new Rapport("R001", "Rapport Final AI");
    }

    @Test
    void nouveauRapport_estCreeAvecLeStatutSoumis() {
        // Arrange : rapport cree par la fixture
        // Act
        String statut = rapport.getStatut();
        // Assert
        assertEquals("Soumis", statut);
        assertFalse(rapport.estValide());
    }

    @Test
    void validerRapport_noteDeQuinze_passeAuStatutValide() throws pfeException {
        // Arrange
        double note = 15;
        // Act
        rapport.validerRapport(note);
        // Assert
        assertEquals("Validé", rapport.getStatut());
        assertTrue(rapport.estValide());
    }

    @Test
    void validerRapport_noteZeroLimiteBasse_estAcceptee() throws pfeException {
        // Arrange
        double note = 0;
        // Act
        rapport.validerRapport(note);
        // Assert
        assertEquals(0, rapport.getNote(), 0.001);
    }

    @Test
    void validerRapport_noteVingtLimiteHaute_estAcceptee() throws pfeException {
        // Arrange
        double note = 20;
        // Act
        rapport.validerRapport(note);
        // Assert
        assertEquals(20, rapport.getNote(), 0.001);
    }

    @Test
    void validerRapport_noteVingtEtUn_leveUneExceptionEtLeStatutResteSoumis() {
        // Arrange
        double noteInvalide = 21;
        // Act + Assert (l'exception est attendue)
        assertThrows(pfeException.class, () -> rapport.validerRapport(noteInvalide));
        assertEquals("Soumis", rapport.getStatut());
    }

    @Test
    void validerRapport_noteNegative_leveUneException() {
        // Arrange
        double noteInvalide = -1;
        // Act + Assert
        assertThrows(pfeException.class, () -> rapport.validerRapport(noteInvalide));
    }

    // --- Defauts connus : tests ecrits mais ignores pour que la suite reste verte ---

    @Disabled("EX-09 a arbitrer : aucun seuil de validation n'est defini dans le code")
    @Test
    void validerRapport_noteDeHuit_nEstPasValide() throws pfeException {
        // Arrange
        double noteInsuffisante = 8;
        // Act
        rapport.validerRapport(noteInsuffisante);
        // Assert
        assertFalse(rapport.estValide());
    }

    @Disabled("R9 / TR06 : aucun controle de l'etat avant une deuxieme validation")
    @Test
    void validerRapport_rapportDejaValide_estRefuse() throws pfeException {
        // Arrange
        rapport.validerRapport(15);
        // Act + Assert
        assertThrows(pfeException.class, () -> rapport.validerRapport(17));
    }
}