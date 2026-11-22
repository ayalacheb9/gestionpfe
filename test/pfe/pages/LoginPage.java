package pfe.pages;

import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage {
    private final Page page;
    private final String urlBase;

    private static final String CHAMP_IDENTIFIANT = "[data-testid='login-username']";
    private static final String CHAMP_MOT_DE_PASSE = "[data-testid='login-password']";
    private static final String BOUTON_CONNEXION = "[data-testid='login-button']";

    public LoginPage(Page page, String urlBase) {
        this.page = page;
        this.urlBase = urlBase;
    }

    public void ouvrir() {
        page.navigate(urlBase + "/login.html");
        verifierPageAffichee();
    }

    public void seConnecter(String identifiant, String motDePasse) {
        page.locator(CHAMP_IDENTIFIANT).fill(identifiant);
        page.locator(CHAMP_MOT_DE_PASSE).fill(motDePasse);
        page.locator(BOUTON_CONNEXION).click();
    }

    public void verifierPageAffichee() {
        page.waitForURL("**/login.html");
        assertThat(page.locator(BOUTON_CONNEXION)).isVisible();
    }
}
