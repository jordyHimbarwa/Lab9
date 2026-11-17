package seg3x02.tempconverterapi

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `requete sans identifiants refusee`() {
        mockMvc.perform(get("/temperature-converter/celsius-fahrenheit/50"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `mauvais mot de passe refuse`() {
        mockMvc.perform(get("/temperature-converter/celsius-fahrenheit/50").with(httpBasic("user1", "wrong")))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `user1 peut convertir celsius vers fahrenheit`() {
        mockMvc.perform(get("/temperature-converter/celsius-fahrenheit/50").with(httpBasic("user1", "pass1")))
            .andExpect(status().isOk)
            .andExpect(content().string("122.0"))
    }

    @Test
    fun `user2 peut convertir fahrenheit vers celsius`() {
        mockMvc.perform(get("/temperature-converter/fahrenheit-celsius/122").with(httpBasic("user2", "pass2")))
            .andExpect(status().isOk)
            .andExpect(content().string("50.0"))
    }

    @Test
    fun `mot de passe d'un autre utilisateur refuse`() {
        mockMvc.perform(get("/temperature-converter/celsius-fahrenheit/50").with(httpBasic("user1", "pass2")))
            .andExpect(status().isUnauthorized)
    }
}
