package api.config;

import api.auth.JWTSecurity;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

@EnableWebFlux
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class HttpConfigurer {

	@Autowired
	private JWTSecurity jwtSecurity;

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
    return http

        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()

        .securityContextRepository(jwtSecurity)
        .authorizeExchange()
        .pathMatchers("/auth/*").authenticated()
        .pathMatchers(HttpMethod.OPTIONS).permitAll()
        .anyExchange().permitAll()
        .and()

        .build();

  }
  
  @Bean
  RouterFunction<ServerResponse> routerFunction() {
    return route(GET("/"), req
        -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html"))
            .build());
  }
}
