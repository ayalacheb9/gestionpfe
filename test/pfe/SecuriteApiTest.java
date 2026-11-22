package pfe;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecuriteApiTest {

    @Autowired
    private MockMvc mvc;

    // REG-BUG-03 : création sans authentification refusée
    @Test
    void regBug03_post_sans_authentification_refuse() throws Exception {
        mvc.perform(post("/api/rapports").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifiant\":\"SEC-1\",\"titre\":\"Rapport pirate\"}"))
                .andExpect(status().isUnauthorized());
    }

    // SEC-02 : liste sans authentification refusée
    @Test
    void sec02_liste_sans_authentification_refusee() throws Exception {
        mvc.perform(get("/api/rapports")).andExpect(status().isUnauthorized());
    }

    // SEC-03 : validation sans authentification refusée
    @Test
    void sec03_validation_sans_authentification_refusee() throws Exception {
        mvc.perform(patch("/api/rapports/R001/validation")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"note\":10}"))
                .andExpect(status().isUnauthorized());
    }
}