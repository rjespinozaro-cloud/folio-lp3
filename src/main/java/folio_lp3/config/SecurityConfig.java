package folio_lp3.config;

import folio_lp3.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ===================================================================
                        // 1. RECURSOS ESTÁTICOS, ARCHIVOS DE ASSETS Y EVIDENCIAS BINARIAS (PÚBLICOS)
                        // ===================================================================
                        // Se consolidan todos los assets y las carpetas de carga del sistema de archivos aquí
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/panel.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/docs/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // ===================================================================
                        // 2. FRAGMENTOS MODULARES (DISEÑO SPA)
                        // ===================================================================
                        // Personaje 1 (Reclutador): Módulos del portafolio público son libres
                        .requestMatchers("/html/modulos-publicos/**").permitAll()

                        // Personaje 2 (Admin): Los fragmentos del panel de gestión se blindan antes que las APIs
                        .requestMatchers("/html/modulos-admin/**").hasAuthority("ADMINISTRADOR")

                        // ===================================================================
                        // 3. ENDPOINTS DE LA API REST (DATOS ESPECÍFICOS)
                        // ===================================================================
                        // Autenticación pública para el login del Administrador y endpoints de salud
                        .requestMatchers(
                                "/api/v1/autenticacion/**",
                                "/api/v1/salud/**"
                        ).permitAll()

                        // ACCESO RECLUTADOR (Cyber Assistant): El chat con IA y su calificación son públicos
                        .requestMatchers(HttpMethod.POST, "/api/v1/preguntas-ia/preguntar").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/preguntas-ia/{id}/calificar").permitAll()

                        // PROTECCIÓN DE ENDPOINTS ADMINISTRATIVOS CRÍTICOS (Se evalúan antes de los comodines genéricos)
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/api/v1/configuracion-ia/**").hasAuthority("ADMINISTRADOR")

                        // ===================================================================
                        // 4. REGLAS GENÉRICAS / COMODINES (SIEMPRE AL FINAL)
                        // ===================================================================
                        // ACCESO EXCLUSIVO ADMINISTRADOR (Mutaciones): Crear, Editar o Borrar exige autoridad
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasAuthority("ADMINISTRADOR")

                        // ACCESO RECLUTADOR (Solo lectura pública genérica para cargar los mosaicos)
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()

                        // Fallback de seguridad perimetral total
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}