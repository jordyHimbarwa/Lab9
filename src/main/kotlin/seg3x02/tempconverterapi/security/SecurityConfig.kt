package seg3x02.tempconverterapi.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

/**
 * Configuration de sécurité de l'API de conversion de température.
 *
 * - Authentification « In Memory » avec deux utilisateurs (user1/pass1, user2/pass2).
 * - Authentification Basic (HTTP Basic) exigée pour TOUTES les requêtes.
 * - API sans état (stateless) : aucune session, les identifiants doivent
 *   accompagner chaque requête.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val user1 = User.withUsername("user1")
            .password(passwordEncoder.encode("pass1"))
            .roles("USER")
            .build()
        val user2 = User.withUsername("user2")
            .password(passwordEncoder.encode("pass2"))
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user1, user2)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // API REST appelée par Curl/Postman/Insomnia : pas de jeton CSRF
            .csrf { it.disable() }
            // Toutes les requêtes doivent être authentifiées
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            // Authentification Basic
            .httpBasic(withDefaults())
            // Pas de formulaire de connexion (aucune interface demandée)
            .formLogin { it.disable() }
            // Aucune session : identifiants requis à chaque requête
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        return http.build()
    }
}
