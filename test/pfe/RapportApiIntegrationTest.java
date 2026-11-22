package pfe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tests d'intégration de l'API (contrôleurs + authentification, contexte Spring réel). */
@SpringBootTest
@AutoConfigureMockMvc
class RapportApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private static final AtomicInteger SEQ = new AtomicInteger(1000);

    private static String nouvelId() {
        return "IT" + SEQ.incrementAndGet();
    }

    private static String json(String id, String titre) {
        return "{\"identifiant\":\"" + id + "\",\"titre\":\"" + titre + "\"}";
    }

    /** Simule un utilisateur connecté. */
    private static RequestPostProcessor connecte() {
        return request -> {
            request.getSession(true).setAttribute(AuthConfig.SESSION_KEY, "etudiant");
            return request;
        };
    }

    private int creer(String id, String titre) throws Exception {
        return mvc.perform(post("/api/rapports").with(connecte())
                        .contentType(MediaType.APPLICATION_JSON).content(json(id, titre)))
                .andReturn().getResponse().getStatus();
    }

    @Test // IT-01
    void it01_creation_puis_lecture() throws Exception {
        String id = nouvelId();
        mvc.perform(post("/api/rapports").with(connecte())
                        .contentType(MediaType.APPLICATION_JSON).content(json(id, "Rapport integration")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifiant").value(id))
                .andExpect(jsonPath("$.statut").value("Soumis"));
        mvc.perform(get("/api/rapports/" + id).with(connecte()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Rapport integration"));
    }

    @Test // IT-02
    void it02_identifiant_en_doublon_refuse() throws Exception {
        String id = nouvelId();
        assertEquals(201, creer(id, "Premier rapport"));
        assertEquals(409, creer(id, "Doublon rapport"));
        String liste = mvc.perform(get("/api/rapports").with(connecte()))
                .andReturn().getResponse().getContentAsString();
        assertEquals(1, liste.split("\"identifiant\":\"" + id + "\"", -1).length - 1);
    }

    @Test // IT-03
    void it03_rapport_inexistant_404() throws Exception {
        mvc.perform(get("/api/rapports/INEXISTANT").with(connecte())).andExpect(status().isNotFound());
    }

    @Test // IT-04
    void it04_validation_note_correcte() throws Exception {
        String id = nouvelId();
        creer(id, "Rapport a valider");
        mvc.perform(patch("/api/rapports/" + id + "/validation").with(connecte())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"note\":15}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valide").value(true))
                .andExpect(jsonPath("$.note").value(15.0));
        mvc.perform(get("/api/rapports/" + id).with(connecte()))
                .andExpect(jsonPath("$.valide").value(true));
    }

    @Test // IT-05
    void it05_note_hors_plage_400() throws Exception {
        String id = nouvelId();
        creer(id, "Rapport note invalide");
        mvc.perform(patch("/api/rapports/" + id + "/validation").with(connecte())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"note\":25}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/rapports/" + id).with(connecte()))
                .andExpect(jsonPath("$.valide").value(false));
    }

    @Test // IT-06
    void it06_note_absente_400() throws Exception {
        String id = nouvelId();
        creer(id, "Rapport sans note");
        mvc.perform(patch("/api/rapports/" + id + "/validation").with(connecte())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test // IT-07
    void it07_validation_rapport_inexistant_404() throws Exception {
        mvc.perform(patch("/api/rapports/INEXISTANT/validation").with(connecte())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"note\":10}"))
                .andExpect(status().isNotFound());
    }

    @Test // LIM-01
    void lim01_titre_de_5_caracteres_accepte() throws Exception {
        assertEquals(201, creer(nouvelId(), "abcde"));
    }

    @Test // LIM-02
    void lim02_titre_de_100_caracteres_accepte() throws Exception {
        assertEquals(201, creer(nouvelId(), "a".repeat(100)));
    }

    @Test // REG-TITRE-VIDE
    void reg_titre_vide_refuse() throws Exception {
        assertEquals(400, creer(nouvelId(), ""));
    }

    @Test // REG-BUG-01
    void regBug01_titre_trop_court_refuse() throws Exception {
        assertEquals(400, creer(nouvelId(), "ab"));
    }

    @Test // REG-BUG-02
    void regBug02_titre_trop_long_refuse() throws Exception {
        assertEquals(400, creer(nouvelId(), "a".repeat(101)));
    }

    @Test // AUTH-01 : connexion valide puis accès à l'API avec la session obtenue
    void auth01_connexion_valide_ouvre_une_session() throws Exception {
        MvcResult r = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"etudiant\",\"password\":\"test123\"}"))
                .andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) r.getRequest().getSession();
        mvc.perform(get("/api/rapports").session(session)).andExpect(status().isOk());
    }

    @Test // AUTH-02
    void auth02_mot_de_passe_incorrect_401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"etudiant\",\"password\":\"faux\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test // AUTH-03
    void auth03_inscription_mot_de_passe_court_400() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"aya\",\"password\":\"12\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test // AUTH-04
    void auth04_inscription_identifiant_existant_409() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"etudiant\",\"password\":\"abcd\"}"))
                .andExpect(status().isConflict());
    }
}