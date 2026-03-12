package com.juliandonati.api.security.config;

import com.juliandonati.api.security.jwt.JwtAuthEntryPoint;
import com.juliandonati.api.security.jwt.JwtAuthenticatorFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAuthenticatorFilter jwtAuthenticatorFilter;
    private final Environment environment;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)  // Lo deshabilitamos porque nuestra API no usa cookies para la autentificación.
                .exceptionHandling(exception ->
                    exception.authenticationEntryPoint(jwtAuthEntryPoint)
                )
                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    // Cada petición debe CONTENER el token. No hay cookies. Representa una ventaja, ya que la sesión se inicia en
                    // un servidor específico, por lo que el usuario depende de ese servidor.
                )
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/api/v1/auth/**").permitAll();

                    if(environment.acceptsProfiles(Profiles.of("dev")))
                        auth.requestMatchers("/swagger-ui/**","/v3/api-docs/**","/v3/api-docs.yaml","/swagger-resources/**","/webjars/**").permitAll();

                    auth.anyRequest().authenticated();
                });

        http.addFilterBefore(jwtAuthenticatorFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }


    protected void configure(AuthenticationManagerBuilder authMB) throws Exception{
        authMB.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","PATCH"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type" /* Necesario si recibimos peticiones con cuerpo */,"Accept" /* Si tenemos
        peticiones que devuelven una respuesta, se activa por defecto. */));

        configuration.setExposedHeaders(Arrays.asList("Authorization" /* Le estamos diciendo al cliente que cuando enviemos una respuesta vamos a dejar que
        se lea el header Authorization */));

        configuration.setAllowCredentials(true); // El navegador tiene permitido guardar las cookies que recibe.

        configuration.setMaxAge(3600L); // Permite guardar una respuesta por 1 hora para que no se tenga que hacer de vuelta. Hace que la app vaya mucho más
                                        // rápida.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // Creamos un mapa de configuraciones.

        source.registerCorsConfiguration("/**" , configuration); // Registramos las configuraciones para todas las rutas.

        return source;
    }

}
