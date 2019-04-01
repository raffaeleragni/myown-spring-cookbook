package api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import static reactor.core.publisher.Mono.empty;

@Component
public class JWTSecurity implements ServerSecurityContextRepository {

  @Value("${security.jwt.token.secret:secret}")
  private String secret = "secret secret secret secret secret secret secret secret";

  @Value("${security.jwt.token.validity-ms:3600000}")
  private long validityMS = 3600000;

  private Key key;

  @Autowired
  private ReactiveUserDetailsService userDetailsService;

  @PostConstruct
  protected void init() throws NoSuchAlgorithmException {
    key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  @Override
  public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
    // No save, we only load because token is in the Authorization header
    return empty();
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange swe) {
    ServerHttpRequest request = swe.getRequest();
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String authToken = authHeader.substring(7);
      return userDetailsService.findByUsername(getUsername(authToken))
          .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()))
          .map(t -> new SecurityContextImpl(t));
    } else {
      return Mono.empty();
    }
  }

  public Mono<String> createToken(String username) {
    return userDetailsService.findByUsername(username).map(userDetails -> {
      Claims claims = Jwts.claims().setSubject(username);
      claims.put("roles", userDetails.getAuthorities());
      Date now = new Date();
      Date validity = new Date(now.getTime() + validityMS);
      return Jwts.builder()
          .setClaims(claims)
          .setIssuedAt(now)
          .setExpiration(validity)
          .signWith(key, HS256)
          .compact();
    });
  }

  private String getUsername(String token) {
    try {
      return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    } catch (JwtException | IllegalArgumentException e) {
      throw new ResponseStatusException(UNAUTHORIZED, "Expired or invalid JWT token");
    }
  }
}
