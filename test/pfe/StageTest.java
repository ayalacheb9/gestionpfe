package pfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StageTest {

    private Stage stage;
    private Etudiant ali;
    private Etudiant sara;

    // Fixture : objets neufs avant chaque test
    @BeforeEach
    void preparer() {
        Entreprise entreprise = new Entreprise("ENT1", "Informatique");
        stage = new Stage("S001", entreprise, "2024-02-01", "2024-07-01");
        ali = new Etudiant("ET001", "Ali", "Ben Salah", "Informatique", "Genie Logiciel");
        sara = new Etudiant("ET002", "Sara", "Mejri", "Informatique", "Réseaux");
    }

    @Test
    void setEtudiant_stageLibre_attribueLeStageALEtudiant() throws pfeException {
        // Arrange : stage libre cree par la fixture
        // Act
        stage.setEtudiant(ali);
        // Assert
        assertSame(ali, stage.getEtudiant());
    }

    @Test
    void setEtudiant_stageDejaAttribue_leveUneExceptionEtGardeLePremierEtudiant() throws pfeException {
        // Arrange
        stage.setEtudiant(ali);
        // Act + Assert
        assertThrows(pfeException.class, () -> stage.setEtudiant(sara));
        assertSame(ali, stage.getEtudiant());
    }
}