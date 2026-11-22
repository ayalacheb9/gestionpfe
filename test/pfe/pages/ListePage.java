package pfe.pages;

import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ListePage {
    private final Page page;

    private static final String TABLEAU = "[data-testid='rapports-table']";
    private static final String BOUTON_DECONNEXION = "[data-testid='logout-button']";

    public ListePage(Page page) {
        this.page = page;
    }

    public void verifierPageAffichee() {
        page.waitForURL("**/liste.html");
        assertThat(page.locator(TABLEAU)).isVisible();
    }

    public void verifierRapportPresent(String identifiant) {
        assertThat(page.locator("[data-testid='ligne-" + identifiant + "']")).isVisible();
    }

    public void seDeconnecter() {
        page.locator(BOUTON_DECONNEXION).click();
    }
}