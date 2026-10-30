package br.com.ifsp.edu.br.nxtdrive.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AlterarSenhaRequestDTO {

	@NotBlank(message = "A senha atual é obrigatória")
	private String senhaAtual;

	@NotBlank(message = "A nova senha é obrigatória")
	@Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres")
	private String novaSenha;

	@NotBlank(message = "A confirmação de senha é obrigatória")
	private String confirmacaoSenha;

	public AlterarSenhaRequestDTO() {
	}

	public AlterarSenhaRequestDTO(String senhaAtual, String novaSenha, String confirmacaoSenha) {
		this.senhaAtual = senhaAtual;
		this.novaSenha = novaSenha;
		this.confirmacaoSenha = confirmacaoSenha;
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}
}
