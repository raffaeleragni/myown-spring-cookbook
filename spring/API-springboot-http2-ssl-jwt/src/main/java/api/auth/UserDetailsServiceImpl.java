package api.auth;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import static reactor.core.publisher.Mono.just;

@Component
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

  public static final String ROLE_ADMIN = "admin";
  public static final String ROLE_USER = "user";
  public static final String HAS_ANY_ROLE = "hasAnyRole('"+ROLE_ADMIN+"', '"+ROLE_USER+"')";

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    // TODO implement the user lookup here
    return just(User.withUsername(username)
        .password(username)
        .roles(ROLE_ADMIN).build());
  }

}
