package pfe; // ADAPTER

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RapportValidatorTest {

    @Test // UT-01 : valeur limite basse acceptée
    void titre_de_5_caracteres_est_valide() {
        String titre = "abcde";                                   // Arrange
        boolean r = RapportValidator.titreValide(titre);          // Act
        assertTrue(r);                                            // Assert
    }

    @Test // UT-02 : sous la limite basse refusé (BUG-01)
    void titre_de_4_caracteres_est_refuse() {
        assertFalse(RapportValidator.titreValide("abcd"));
    }

    @Test // UT-03 : valeur limite haute acceptée
    void titre_de_100_caracteres_est_valide() {
        assertTrue(RapportValidator.titreValide("a".repeat(100)));
    }

    @Test // UT-04 : au-dessus de la limite haute refusé (BUG-02)
    void titre_de_101_caracteres_est_refuse() {
        assertFalse(RapportValidator.titreValide("a".repeat(101)));
    }

    @Test // UT-05 : null refusé
    void titre_null_est_refuse() {
        assertFalse(RapportValidator.titreValide(null));
        assertEquals("Le titre est obligatoire.", RapportValidator.verifierTitre(null));
    }

    @Test // UT-06 : chaîne vide ou espaces seuls refusée
    void titre_vide_ou_espaces_est_refuse() {
        assertFalse(RapportValidator.titreValide(""));
        assertFalse(RapportValidator.titreValide("      "));
    }

    @Test // UT-07 : les espaces de bord ne comptent pas
    void espaces_de_bord_ignores() {
        assertFalse(RapportValidator.titreValide("  ab  "));
        assertTrue(RapportValidator.titreValide("  abcde  "));
    }

    @ParameterizedTest(name = "longueur {0} -> valide={1}") // UT-08 : partitions
    @CsvSource({"0,false", "1,false", "4,false", "5,true", "50,true", "100,true", "101,false", "150,false"})
    void partitions_de_longueur(int longueur, boolean attendu) {
        assertEquals(attendu, RapportValidator.titreValide("x".repeat(longueur)));
    }
        @Test // UT-09 : titre valide -> aucun message d'erreur
    void verifierTitre_valide_retourne_null() {
        assertNull(RapportValidator.verifierTitre("abcde"));
    }

    @Test // UT-10 : titre trop court -> message mentionnant 5
    void verifierTitre_trop_court_retourne_message() {
        String message = RapportValidator.verifierTitre("abcd");
        assertNotNull(message);
        assertTrue(message.contains("5"));
    }

    @Test // UT-11 : titre trop long -> message mentionnant 100
    void verifierTitre_trop_long_retourne_message() {
        String message = RapportValidator.verifierTitre("a".repeat(101));
        assertNotNull(message);
        assertTrue(message.contains("100"));
    }

    @Test // UT-12 : titre vide ou espaces -> message "obligatoire"
    void verifierTitre_vide_retourne_message_obligatoire() {
        assertNotNull(RapportValidator.verifierTitre("   "));
        assertNotNull(RapportValidator.verifierTitre(""));
    }
        @Test // UT-13 : balises HTML refusées (BUG-05)
    void titre_avec_balises_html_est_refuse() {
        assertFalse(RapportValidator.titreValide("<b>test</b>"));
        assertFalse(RapportValidator.titreValide("<i>italique</i>"));
        assertNotNull(RapportValidator.verifierTitre("<b>test</b>"));
    }

    @Test // UT-14 : un titre ordinaire reste valide
    void titre_ordinaire_avec_apostrophe_et_chiffres_reste_valide() {
        assertTrue(RapportValidator.titreValide("Rapport de stage 2026 - l'application"));
    }
}
