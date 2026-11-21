package cl.lvlupgamer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import cl.lvlupgamer.repositorio.UsuarioRepository;
import cl.lvlupgamer.seguridad.JwtFiltro;
import cl.lvlupgamer.seguridad.JwtService;
import cl.lvlupgamer.seguridad.RespuestasSeguridad;


@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    
    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarios) {
        return correo -> usuarios.findByCorreo(correo)
                .map(u -> User.withUsername(u.getCorreo())
                        .password(u.getPassword())
                        .roles(u.getRol().name())
                        .disabled(!u.isActivo())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("No existe " + correo));
    }

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtService jwt, UsuarioRepository usuarios,
                                       RespuestasSeguridad respuestas) throws Exception {
        http
                
                .cors(Customizer.withDefaults())
                
                
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(respuestas)   
                        .accessDeniedHandler(respuestas))       
                .authorizeHttpRequests(a -> a
                        
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/registro",
                                "/api/v1/auth/refresh").permitAll()

                        
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v1/categorias/**").permitAll()
                        
                        .requestMatchers("/api/v1/productos/**", "/api/v1/categorias/**").hasRole("ADMINISTRADOR")

                        
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMINISTRADOR")

                        
                        
                        .requestMatchers(HttpMethod.POST, "/api/v1/boletas").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/mias").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")

                        
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtFiltro(jwt, usuarios), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
