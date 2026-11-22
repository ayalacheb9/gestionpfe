package pfe.pages;

import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CreationPage {
    private final Page page;

    private static final String CHAMP_ID = "[data-testid='rapport-id']";
    private static final String CHAMP_TITRE = "[data-testid='rapport-titre']";
    private static final String BOUTON_CREER = "[data-testid='rapport-creer']";
    private static final String MESSAGE = "[data-testid='rapport-message']";
    private static final String LIEN_LISTE = "[data-testid='lien-liste']";

    public CreationPage(Page page) {
        this.page = page;
    }

    public void verifierPageAffichee() {
        page.waitForURL("**/creer.html");
        assertThat(page.locator(BOUTON_CREER)).isVisible();
    }

    public void creerRapport(String identifiant, String titre) {
        page.locator(CHAMP_ID).fill(identifiant);
        page.locator(CHAMP_TITRE).fill(titre);
        page.locator(BOUTON_CREER).click();
    }

    public void verifierMessage(String texte) {
        assertThat(page.locator(MESSAGE)).containsText(texte);
    }

    public void ouvrirListe() {
        page.locator(LIEN_LISTE).click();
    }
}