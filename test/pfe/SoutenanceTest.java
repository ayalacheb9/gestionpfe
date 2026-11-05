package pfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoutenanceTest {

    // Double de test : remplace la vraie Salle.
    // Il repond « libre » ou « occupee » selon ce qu'on lui demande (stub)
    // et note si la reservation a ete demandee (verification d'interaction).
    // Si IntelliJ souligne @Override en rouge, regardez les signatures dans Salle.java
    // (par exemple le type de retour de reserverSalle).
    private static class SalleStub extends Salle {
        private final boolean libre;
        boolean reservee = false;

        SalleStub(boolean libre) {
            super("S101", "Salle 101");
            this.libre = libre;
        }

        @Override
        public boolean verifierDisponibilite(String date) {
            return libre;
        }

        @Override
        public void reserverSalle(String date) {
            reservee = true;
        }
    }

    private Etudiant etudiant;
    private Jury jury;
    private Rapport rapport;

    // Fixture : objets neufs avant chaque test
    @BeforeEach
    void preparer() {
        etudiant = new Etudiant("ET001", "Ali", "Ben Salah", "Informatique", "Genie Logiciel");
        jury = new Jury("J001", "2024-06-15");
        rapport = new Rapport("R001", "Rapport Final AI");
    }

    private Soutenance creerSoutenanceSansSalle() {
        return new Soutenance("AB281", etudiant, jury, null, "2024-06-15", rapport);
    }

    @Test
    void planifierSoutenance_salleLibre_attribueLaSalleEtLaReserve() {
        // Arrange
        SalleStub salleLibre = new SalleStub(true);
        Soutenance soutenance = creerSoutenanceSansSalle();
        // Act
        soutenance.planifierSoutenance(jury, salleLibre);
        // Assert
        assertSame(salleLibre, soutenance.getSalle());
        assertTrue(salleLibre.reservee);
    }

    @Test
    void planifierSoutenance_salleDejaReservee_neChangePasLaSalle() {
        // Arrange
        SalleStub salleOccupee = new SalleStub(false);
        Soutenance soutenance = creerSoutenanceSansSalle();
        // Act
        soutenance.planifierSoutenance(jury, salleOccupee);
        // Assert
        assertNull(soutenance.getSalle());
        assertFalse(salleOccupee.reservee);
    }
}