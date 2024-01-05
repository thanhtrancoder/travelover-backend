package hcmute.kltn.Backend.component;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenUtil {
	@Value("${jwt.secret}")
	private String JWT_SECRET;
	@Value("${jwt.expiration}")
	private long JWT_EXPIRATION;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

	public String generateAccessToken(User user) {
		Date nowDate = new Date();
		Date expirationDate = new Date(nowDate.getTime() + JWT_EXPIRATION);
		
		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(nowDate)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, JWT_SECRET).compact();
	}
	
	public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token is null, empty or only whitespace", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex);
        } catch (SignatureException ex) {
            LOGGER.error("Signature validation failed");
        }
        
        return false;
    }
     
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }
     
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
