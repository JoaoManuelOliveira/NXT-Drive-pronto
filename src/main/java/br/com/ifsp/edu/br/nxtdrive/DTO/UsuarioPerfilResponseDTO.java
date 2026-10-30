package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.util.UUID;

import br.com.ifsp.edu.br.nxtdrive.model.Usuario;

public class UsuarioPerfilResponseDTO {

	private UUID id;
	private String nome;
	private String email;
	private String role;
	private boolean isAdmin;
	private EnderecoDTO endereco;

	public UsuarioPerfilResponseDTO() {
	}

	public static UsuarioPerfilResponseDTO fromEntity(Usuario usuario) {
		if (usuario == null) return null;
		UsuarioPerfilResponseDTO dto = new UsuarioPerfilResponseDTO();
		dto.setId(usuario.getIdUsuario());
		dto.setNome(usuario.getNome());
		dto.setEmail(usuario.getEmail());
		dto.setRole(usuario.getAcesso() != null ? usuario.getAcesso().name() : "USER");
		dto.setAdmin(usuario.getAcesso() == Usuario.Role.ADMIN);
		dto.setEndereco(EnderecoDTO.fromEntity(usuario.getEndereco()));
		return dto;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public EnderecoDTO getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}
}
