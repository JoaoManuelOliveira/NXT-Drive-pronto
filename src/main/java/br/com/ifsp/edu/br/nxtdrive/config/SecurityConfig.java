package br.com.ifsp.edu.br.nxtdrive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Ativa o funcionamento do @PreAuthorize nos Controllers
public class SecurityConfig {

    @Bean
    SecurityFilterChain seguranca(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/api/v1/webhooks/**", "/api/v1/pagamentos/**", "/api/v1/admin/**"))
                .authorizeHttpRequests(regras -> regras
                        // 1. Recursos estáticos, telas públicas e erros
                        .requestMatchers("/", "/login", "/cadastro", "/403", "/css/**", "/js/**", "/img/**", "/*.css").permitAll()
                        
                        // 2. Imagem pública do produto
                        .requestMatchers("/produto/imagem/**").permitAll()

                        // 3. Endpoints públicos da API (Webhooks do Mercado Pago, rotas de pagamento, CEP e Newsletter)
                        .requestMatchers("/api/v1/webhooks/**", "/api/v1/pagamentos/**", "/api/cep/**", "/api/newsletter/**").permitAll()

                        // 4. Rotas de administração exclusivas para ADMIN
                        .requestMatchers(
                                "/dashboard/**", "/admin/**", "/pedidos/**", "/estatisticas/**", "/api/v1/admin/**",
                                "/produtos/**", "/categorias/**", "/adicionarCategoria/**", "/editarCategoria/**", "/removerCategoria/**",
                                "/adicionarProduto/**", "/editarProduto/**", "/removerProduto/**", 
                                "/usuarios/**", "/listarCategorias/**"
                        ).hasRole("ADMIN")

                        // 5. Páginas do cliente (USER ou ADMIN)
                        .requestMatchers("/home")
                        .hasAnyRole("USER", "ADMIN")

                        // 6. Qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/")               // Página onde está o formulário de login (index)
                        .loginProcessingUrl("/login")  // Endpoint POST acionado pelo formulário
                        .usernameParameter("email")
                        .passwordParameter("senha")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/?error")
                        .permitAll()
                )

                .logout(sair -> sair
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout")
                        .permitAll()
                )

                .exceptionHandling(excecao -> excecao
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/403");
                        })
                )

                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}