package archit.springboot.booksocialnetwork.Service;

import archit.springboot.booksocialnetwork.Dto.AuthenticationRequest;
import archit.springboot.booksocialnetwork.Dto.AuthenticationResponse;
import archit.springboot.booksocialnetwork.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserDetailService userDetailService;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration = 60 * 60L;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String extractTokenFromHeader(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> claims,
                              UserDetails userDetails,
                              Long jwtExpiration) {
        var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder().setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim("authorities", authorities)
                .signWith(getSignInKey()).compact();
    }

    private Key getSignInKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }


    public String extractUserNameFromToken(String jwt) {
        Claims claims = getClaimsFromJwt(jwt);
        return claims.getSubject();
    }

    public Boolean isTokenValid(String jwt, UserDetails userDetails) {
        Claims claims = getClaimsFromJwt(jwt);
        return userDetails.getUsername().equals(claims.getSubject())
                &&
                claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

    private Claims getClaimsFromJwt(String jwt) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwt).getBody();
    }

    public String generateToken(HashMap<String, Object> claims, User user) {
        return buildToken(claims, user, jwtExpiration);
    }

}
