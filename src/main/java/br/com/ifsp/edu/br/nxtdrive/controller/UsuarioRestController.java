package br.com.ifsp.edu.br.nxtdrive.controller;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifsp.edu.br.nxtdrive.DTO.AlterarSenhaRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.AtualizarPerfilRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.UsuarioPerfilResponseDTO;
import br.com.ifsp.edu.br.nxtdrive.model.Endereco;
import br.com.ifsp.edu.br.nxtdrive.model.Usuario;
import br.com.ifsp.edu.br.nxtdrive.repository.UsuarioRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class UsuarioRestController {

	private static final Logger log = LoggerFactory.getLogger(UsuarioRestController.class);

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Retorna os dados completos do usuário logado, incluindo o endereço de entrega cadastrado.
	 */
	@GetMapping({ "/usuarios/me", "/clientes/me" })
	public ResponseEntity<?> obterPerfil(Principal principal) {
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("erro", "Usuário não autenticado"));
		}

		return usuarioRepository.findByEmail(principal.getName())
				.map(usuario -> ResponseEntity.ok(UsuarioPerfilResponseDTO.fromEntity(usuario)))
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body((UsuarioPerfilResponseDTO) null));
	}

	/**
	 * Atualiza os dados de perfil (Nome) e endereço completo do usuário logado.
	 */
	@PutMapping("/usuarios/me/perfil")
	public ResponseEntity<?> atualizarPerfil(
			@Valid @RequestBody AtualizarPerfilRequestDTO dto,
			Principal principal) {

		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("erro", "Usuário não autenticado"));
		}

		var usuarioOpt = usuarioRepository.findByEmail(principal.getName());
		if (usuarioOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("erro", "Usuário não encontrado"));
		}

		Usuario usuario = usuarioOpt.get();

		if (dto.getNome() != null && !dto.getNome().isBlank()) {
			usuario.setNome(dto.getNome().trim());
		}

		Endereco endereco = usuario.getEndereco();
		if (endereco == null) {
			endereco = new Endereco();
		}

		endereco.setCep(dto.getCep() != null ? dto.getCep().trim() : "");
		endereco.setLogradouro(dto.getLogradouro() != null ? dto.getLogradouro().trim() : "");
		endereco.setNumero(dto.getNumero() != null ? dto.getNumero().trim() : "");
		endereco.setBairro(dto.getBairro() != null ? dto.getBairro().trim() : "");
		endereco.setLocalidade(dto.getLocalidade() != null ? dto.getLocalidade().trim() : "");
		endereco.setUf(dto.getUf() != null ? dto.getUf().trim().toUpperCase() : "");
		endereco.setComplemento(dto.getComplemento() != null ? dto.getComplemento().trim() : null);

		usuario.setEndereco(endereco);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		log.info("Perfil e endereço atualizados com sucesso para usuário: {}", usuario.getEmail());
		return ResponseEntity.ok(UsuarioPerfilResponseDTO.fromEntity(usuarioSalvo));
	}

	/**
	 * Endpoint seguro para alteração de senha do usuário autenticado.
	 * Exige validação da senha atual com hash BCrypt e confirmação da nova senha.
	 */
	@PatchMapping("/usuarios/alterar-senha")
	public ResponseEntity<?> alterarSenha(
			@Valid @RequestBody AlterarSenhaRequestDTO dto,
			Principal principal) {

		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("erro", "Usuário não autenticado"));
		}

		var usuarioOpt = usuarioRepository.findByEmail(principal.getName());
		if (usuarioOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("erro", "Usuário não encontrado"));
		}

		Usuario usuario = usuarioOpt.get();

		// 1. Valida se a senha atual confere com o hash no banco
		if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
			return ResponseEntity.badRequest()
					.body(Map.of("erro", "A senha atual informada está incorreta."));
		}

		// 2. Valida se a nova senha e a confirmação coincidem
		if (!dto.getNovaSenha().equals(dto.getConfirmacaoSenha())) {
			return ResponseEntity.badRequest()
					.body(Map.of("erro", "A confirmação de senha não coincide com a nova senha."));
		}

		// 3. Valida tamanho mínimo da nova senha
		if (dto.getNovaSenha().trim().length() < 6) {
			return ResponseEntity.badRequest()
					.body(Map.of("erro", "A nova senha deve ter no mínimo 6 caracteres."));
		}

		// 4. Encripta e salva a nova senha
		usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha().trim()));
		usuarioRepository.save(usuario);

		log.info("Senha alterada com sucesso para o usuário: {}", usuario.getEmail());
		return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso!"));
	}
}
