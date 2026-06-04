package folio_lp3.config;

import folio_lp3.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de Spring Security con RBAC
 * Control de acceso basado en roles
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * Configurar cadena de filtros de seguridad
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (ya que usamos JWT)
                .csrf(csrf -> csrf.disable())
                
                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Configurar autorización de endpoints (SIN REPETIR /api/v1)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sin autenticación)
                        .requestMatchers("/autenticacion/**").permitAll()
                        .requestMatchers("/salud/**").permitAll()
                        
                        // Endpoints solo para ADMINISTRADOR
                        .requestMatchers(HttpMethod.POST, "/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/**").hasRole("ADMINISTRADOR")
                        
                        // Endpoints solo para INSTRUCTOR
                        .requestMatchers(HttpMethod.GET, "/consultas/pilar/**").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "/consultas/**").hasRole("INSTRUCTOR")
                        
                        // Endpoints solo para ESTUDIANTE
                        .requestMatchers(HttpMethod.POST, "/consultas/**").hasRole("ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/preguntas-ia/**").hasRole("ESTUDIANTE")
                        
                        // Reportes solo para ADMINISTRADOR e INSTRUCTOR
                        .requestMatchers("/reportes/**").hasAnyRole("ADMINISTRADOR", "INSTRUCTOR")
                        
                        // El resto requiere autenticación
                        .anyRequest().authenticated()
                )
                
                // Política de sesión sin estado (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Agregar filtro JWT antes del filtro de autenticación
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Configurar CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Bean para encriptación de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}