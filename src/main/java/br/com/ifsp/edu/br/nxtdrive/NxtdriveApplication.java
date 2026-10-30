package br.com.ifsp.edu.br.nxtdrive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.ifsp.edu.br.nxtdrive.model.Usuario;
import br.com.ifsp.edu.br.nxtdrive.model.Usuario.Role;
import br.com.ifsp.edu.br.nxtdrive.repository.UsuarioRepository;

@SpringBootApplication
public class NxtdriveApplication {
	public static void main(String[] args) {
		SpringApplication.run(NxtdriveApplication.class, args);
	}
	
	@Bean
    CommandLineRunner criarAdmin(
            UsuarioRepository repo,
            PasswordEncoder encoder) {

        return args -> {
            if (repo.findByEmail("adminsupremo@gmail.com").isEmpty()) {
                Usuario u = new Usuario();

                u.setNome("Manual");
                u.setEmail("adminsupremo@gmail.com");
                u.setSenha(encoder.encode("admin"));
                u.setAcesso(Role.ADMIN);
                repo.save(u);
                System.out.println("ADMIN criado com sucesso.");
            }
            
            if (repo.findByEmail("usuario@gmail.com").isEmpty()) {
                Usuario user = new Usuario();

                user.setNome("Usuário");
                user.setEmail("usuario@gmail.com");
                user.setSenha(encoder.encode("123456"));
                user.setAcesso(Role.USER);
                repo.save(user);
            }
        };
    }
}