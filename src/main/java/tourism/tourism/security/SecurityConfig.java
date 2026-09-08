package tourism.tourism.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. Denne gør, at du stadig kan bruge din BCryptPasswordEncoder i din LoginController
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Denne deaktiverer Springs automatiske login-skærm, så dit eget login virker!
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 🔓 Dette åbner alle sider op igen!
                )
                .csrf(csrf -> csrf.disable()); // Slå CSRF fra, så dine POST-formularer virker uden problemer

        return http.build();
    }
}
