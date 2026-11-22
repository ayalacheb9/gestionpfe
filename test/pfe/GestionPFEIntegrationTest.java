package pfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'integration : les vraies classes travaillent ensemble (aucun double de test).
 * L'application n'a pas de base de donnees : les donnees vivent en memoire.
 * Le jeu de donnees est recree dans @BeforeEach, donc chaque test repart du meme etat
 * (equivalent du rollback) et la suite donne le meme resultat a chaque execution.
 */
class GestionPFEIntegrationTest {

    private GestionAffectations gestion;
    private Etudiant ali;
    private Etudiant sara;
    private Encadrant encadrant1;
    private Encadrant encadrant2;
    private Projet projet1;
    private Projet projet2;
    private Salle salle101;
    private Jury jury;

    // Jeu de donnees de reference, recree avant chaque test
    @BeforeEach
    void preparerJeuDeDonnees() {
        gestion = new GestionAffectations();
        ali = new Etudiant("ET001", "Ali", "Ben Salah", "Informatique", "Genie Logiciel");
        sara = new Etudiant("ET002", "Sara", "Mejri", "Informatique", "Réseaux");
        encadrant1 = new Encadrant("EN001", "Ahmed", "Khalil", "Informatique");
        encadrant2 = new Encadrant("EN002", "Samir", "Bouaziz", "Réseaux");
        projet1 = new Projet("P001", "AI", "2024-01-01", "2024-06-01");
        projet2 = new Projet("P002", "Cloud Computing", "2024-02-01", "2024-07-01");
        salle101 = new Salle("S101", "Salle 101");
        jury = new Jury("J001", "2024-06-15");
    }

    // IT01 : Soutenance + Salle reelle (risque R4)
    @Test
    void planifierSoutenance_memeSalleMemeDate_laSecondeSoutenanceEstRefusee() {
        // Arrange
        Soutenance s1 = new Soutenance("S1", ali, jury, null, "2024-06-15", new Rapport("R001", "Rapport AI"));
        Soutenance s2 = new Soutenance("S2", sara, jury, null, "2024-06-15", new Rapport("R002", "Rapport Cloud"));
        // Act
        s1.planifierSoutenance(jury, salle101);
        s2.planifierSoutenance(jury, salle101);
        // Assert
        assertSame(salle101, s1.getSalle());
        assertNull(s2.getSalle());
    }

    // IT02 : meme salle, dates differentes
    @Test
    void planifierSoutenance_memeSalleDatesDifferentes_lesDeuxSontAcceptees() {
        // Arrange
        Soutenance s1 = new Soutenance("S1", ali, jury, null, "2024-06-15", new Rapport("R001", "Rapport AI"));
        Soutenance s2 = new Soutenance("S2", sara, jury, null, "2024-06-17", new Rapport("R002", "Rapport Cloud"));
        // Act
        s1.planifierSoutenance(jury, salle101);
        s2.planifierSoutenance(jury, salle101);
        // Assert
        assertSame(salle101, s1.getSalle());
        assertSame(salle101, s2.getSalle());
    }

    // IT03 : Rapport + Stage, relation dans les deux sens
    @Test
    void rapportLieAuStage_lesDeuxCotesDeLaRelationSontCoherents() {
        // Arrange
        Stage stage = new Stage("S001", new Entreprise("ENT1", "Informatique"), "2024-02-01", "2024-07-01");
        Rapport rapport = new Rapport("R001", "Rapport AI");
        // Act
        rapport.setStage(stage);
        // Assert
        assertSame(stage, rapport.getStage());
        assertSame(rapport, stage.getRapport());
    }

    // IT04 : Jury + Encadrant, pas de doublon
    @Test
    void ajouterMembre_memeEncadrantDeuxFois_neCreePasDeDoublon() {
        // Arrange : jury et encadrant crees par la fixture
        // Act
        jury.ajouterMembre(encadrant1);
        jury.ajouterMembre(encadrant1);
        // Assert
        assertEquals(1, jury.getMembres().size());
    }

    // IT05 : affectation automatique avec projets et encadrants reels (risque R2)
    @Test
    void affectationAutomatique_choisitLePremierProjetLibre() throws pfeException {
        // Arrange
        ArrayList<Projet> projets = new ArrayList<>();
        projets.add(projet1);
        projets.add(projet2);
        ArrayList<Encadrant> encadrants = new ArrayList<>();
        encadrants.add(encadrant1);
        encadrants.add(encadrant2);
        // Act
        gestion.affectationAutomatique(ali, projets, encadrants);
        // Assert
        assertFalse(projet1.estDisponible());
        assertTrue(projet2.estDisponible());
    }

    // IT06 : plus aucun projet libre
    @Test
    void affectationAutomatique_aucunProjetLibre_leveUneException() throws pfeException {
        // Arrange : les deux projets sont deja pris
        gestion.affecterProjet(ali, projet1, encadrant1);
        gestion.affecterProjet(sara, projet2, encadrant2);
        ArrayList<Projet> projets = new ArrayList<>();
        projets.add(projet1);
        projets.add(projet2);
        ArrayList<Encadrant> encadrants = new ArrayList<>();
        encadrants.add(encadrant1);
        encadrants.add(encadrant2);
        Etudiant troisieme = new Etudiant("ET003", "Chahin", "Dridi", "Informatique", "AI");
        // Act + Assert
        assertThrows(pfeException.class, () -> gestion.affectationAutomatique(troisieme, projets, encadrants));
    }

    // IT07 : charge maximale d'un encadrant (EX-03, a confirmer en lisant Encadrant.java).
    // Si ce test echoue, lisez Encadrant.EncadrantestDisponible() : soit c'est un defaut,
    // soit la limite n'est pas 5. Dans les deux cas, notez-le dans votre rapport.
    @Test
    void affecterProjet_encadrantAvecCinqProjets_leSixiemeEstRefuse() throws pfeException {
        // Arrange : cinq projets affectes au meme encadrant
        for (int i = 1; i <= 5; i++) {
            Projet p = new Projet("PX" + i, "Projet " + i, "2024-01-01", "2024-06-01");
            Etudiant e = new Etudiant("E" + i, "Nom" + i, "Prenom" + i, "Informatique", "AI");
            gestion.affecterProjet(e, p, encadrant1);
        }
        Projet sixieme = new Projet("PX6", "Projet 6", "2024-01-01", "2024-06-01");
        // Act + Assert
        assertThrows(pfeException.class, () -> gestion.affecterProjet(sara, sixieme, encadrant1));
    }

    // IT08 : parcours complet avec les vraies classes, meme ordre que l'ancien scenario principal
    @Test
    void parcoursComplet_affectationStageRapportSoutenanceValidation() throws pfeException {
        // Arrange
        Entreprise entreprise = new Entreprise("ENT1", "Informatique");
        Stage stage = new Stage("S001", entreprise, "2024-02-01", "2024-07-01");
        entreprise.ajouterOffreStage(stage);
        Rapport rapport = new Rapport("R001", "Rapport Final AI");
        rapport.setProjet(projet1);
        jury.ajouterMembre(encadrant1);
        jury.setSalle(salle101);
        // Act
        gestion.affecterProjet(ali, projet1, encadrant1);
        ali.postulerStage(stage);
        stage.setEtudiant(ali);
        stage.setEncadrant(encadrant1);
        ali.soumettreRapport(rapport, stage);
        Soutenance soutenance = new Soutenance("AB281", ali, jury, salle101, "2024-06-15", rapport);
        soutenance.planifierSoutenance(jury, salle101);
        soutenance.setNoteFinale(17.5);
        rapport.validerRapport(soutenance.getNoteFinale());
        // Assert
        assertEquals("Validé", rapport.getStatut());
        assertSame(salle101, soutenance.getSalle());
        assertEquals(17.5, rapport.getNote(), 0.001);
    }
}