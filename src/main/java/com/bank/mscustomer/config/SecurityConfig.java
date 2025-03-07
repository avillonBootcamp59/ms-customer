package com.bank.mscustomer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(auth -> auth
                .pathMatchers("/api/v1/auth/**").permitAll()
                .anyExchange().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .build();
  }

  @Bean
  public ReactiveJwtDecoder jwtDecoder() {
    byte[] keyBytes = Base64.getDecoder().decode("Xx4K0hF3Qp5Lp3WvF+N+4kSzCvT9uXhQ/VlIhxAs+G4=");
    SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
    return NimbusReactiveJwtDecoder.withSecretKey(key).build();
  }
}
