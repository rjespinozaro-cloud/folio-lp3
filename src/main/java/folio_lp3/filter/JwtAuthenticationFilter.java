package folio_lp3.filter;

import folio_lp3.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Filtro para validar JWT en cada request.
 * Se ejecuta una sola vez por request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Lista de patrones de ruta a ser excluidas del filtro.
    // Usamos AntPathRequestMatcher para un matching de rutas estándar de Spring.
    private final List<AntPathRequestMatcher> excludedMatchers = Arrays.asList(
            new AntPathRequestMatcher("/"),
            new AntPathRequestMatcher("/html/login.html"),
            new AntPathRequestMatcher("/css/**"),
            new AntPathRequestMatcher("/js/**"),
            new AntPathRequestMatcher("/images/**"),
            new AntPathRequestMatcher("/api/v1/autenticacion/**"),
            new AntPathRequestMatcher("/error")
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Devuelve true si la URI de la solicitud actual coincide con cualquiera de los patrones excluidos.
        return excludedMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Obtener el token del header Authorization
            String token = extraerToken(request);

            if (token != null && jwtUtil.validarToken(token)) {
                // Token válido - Extraer información
                String email = jwtUtil.extraerEmail(token);
                String rol = jwtUtil.extraerRol(token);

                // Crear authorities basado en el rol (SIN "ROLE_")
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(rol));

                // Crear autenticación
                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                // Guardar en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(autenticacion);

                log.debug("Token validado para usuario: {}, con rol: {}", email, rol);
            }
        } catch (Exception e) {
            log.error("Error al procesar el token JWT para la URI {}: {}", request.getRequestURI(), e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extraer token del header Authorization.
     * Formato: "Bearer <token>"
     */
    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }
}
