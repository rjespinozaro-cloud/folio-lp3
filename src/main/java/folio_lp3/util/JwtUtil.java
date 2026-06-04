package folio_lp3.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilidad para generar y validar JWT (JSON Web Tokens)
 * Adaptado para JJWT 0.12.3 con API moderna
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    /**
     * Obtener la clave secreta
     */
    private SecretKey obtenerClaveSecreta() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Generar token JWT
     */
    public String generarToken(Long usuarioId, String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("usuarioId", usuarioId)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(obtenerClaveSecreta(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Validar token JWT
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(obtenerClaveSecreta())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extraer email del token
     */
    public String extraerEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(obtenerClaveSecreta())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extraer ID usuario del token
     */
    public Long extraerUsuarioId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(obtenerClaveSecreta())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("usuarioId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extraer rol del token
     */
    public String extraerRol(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(obtenerClaveSecreta())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("rol", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
