
package pfe;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pfe.pages.CreationPage;
import pfe.pages.ListePage;
import pfe.pages.LoginPage;

public class ParcoursCritiqueTest {

    static String URL_BASE;
    static Playwright playwright;
    static Browser browser;
    static ConfigurableApplicationContext application;

    BrowserContext context;
    Page page;

    @BeforeAll
    static void demarrerApplicationEtNavigateur() {
        application = new SpringApplication(GestionPfeApplication.class)
                .run("--server.port=0");

        String port = application.getEnvironment()
                .getProperty("local.server.port");

        URL_BASE = "http://localhost:" + port;

        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
        );
    }

    @AfterAll
    static void fermerApplicationEtNavigateur() {
        if (browser != null) {
            browser.close();
        }

        if (playwright != null) {
            playwright.close();
        }

        if (application != null) {
            application.close();
        }
    }

    @BeforeEach
    void ouvrirPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void fermerContexte() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void parcoursCritiqueComplet() {

        String identifiant = "UI" + System.currentTimeMillis();
        String titre = "Rapport test automatique";

        LoginPage login = new LoginPage(page, URL_BASE);
        CreationPage creation = new CreationPage(page);
        ListePage liste = new ListePage(page);

        // 1. Connexion
        login.ouvrir();
        login.seConnecter("etudiant", "test123");
        creation.verifierPageAffichee();

        // 2. Création d'un rapport
        creation.creerRapport(identifiant, titre);
        creation.verifierMessage("Rapport créé : " + identifiant);

        // 3. Vérification dans la liste
        creation.ouvrirListe();
        liste.verifierPageAffichee();
        liste.verifierRapportPresent(identifiant);

        // 4. Déconnexion
        liste.seDeconnecter();
        login.verifierPageAffichee();
    }
}
