package hr.fer.apuw.lab1.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter {


  private final AppBasicAuthenticationEntryPoint authenticationEntryPoint;

  private final UserDetailsService userDetailsService;

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public CustomWebSecurityConfigurerAdapter(AppBasicAuthenticationEntryPoint authenticationEntryPoint, UserDetailsService userDetailsService,
                                            PasswordEncoder passwordEncoder){
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }


  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.
        sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ako se ukloni ovaj sessionManagement config, doslovno dobivamo token pri uspješnom basic authu lol koji se onda koristi dalje
        ).
        csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/", "/swagger-ui/**", "/swagger.yaml", "/api-docs/**", "/h2-console/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .httpBasic()
        .authenticationEntryPoint(authenticationEntryPoint);
    return http.build();
  }

}