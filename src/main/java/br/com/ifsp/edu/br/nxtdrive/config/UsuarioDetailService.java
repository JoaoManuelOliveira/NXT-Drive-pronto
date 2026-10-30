package br.com.ifsp.edu.br.nxtdrive.config;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.ifsp.edu.br.nxtdrive.model.Usuario;
import br.com.ifsp.edu.br.nxtdrive.repository.UsuarioRepository;

@Service
public class UsuarioDetailService implements UserDetailsService {
	private final UsuarioRepository repository;

	public UsuarioDetailService(UsuarioRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    Usuario usuario = repository.findByEmail(email)
	            .orElseThrow(() ->
	                    new UsernameNotFoundException(
	                            "Usuário não encontrado: " + email
	                    ));

	    return User.withUsername(usuario.getEmail())
	            .password(usuario.getSenha())
	            .roles(usuario.getAcesso().name())
	            .build();
	}
}