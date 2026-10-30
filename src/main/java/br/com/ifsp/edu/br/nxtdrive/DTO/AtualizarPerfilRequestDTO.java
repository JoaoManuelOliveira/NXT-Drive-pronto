package br.com.ifsp.edu.br.nxtdrive.DTO;

import br.com.ifsp.edu.br.nxtdrive.model.EstadoUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AtualizarPerfilRequestDTO {

	private String nome;

	@NotBlank(message = "O CEP é obrigatório")
	@Size(max = 10, message = "O CEP deve ter no máximo 10 caracteres")
	private String cep;

	@NotBlank(message = "A rua/logradouro é obrigatória")
	private String logradouro;

	@NotBlank(message = "O número é obrigatório")
	private String numero;

	@NotBlank(message = "O bairro é obrigatório")
	private String bairro;

	@NotBlank(message = "A cidade/localidade é obrigatória")
	private String localidade;

	@NotBlank(message = "A UF é obrigatória")
	private String uf;

	private String complemento;

	public AtualizarPerfilRequestDTO() {
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getRua() {
		return logradouro;
	}

	public void setRua(String rua) {
		this.logradouro = rua;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getCidade() {
		return localidade;
	}

	public void setCidade(String cidade) {
		this.localidade = cidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = EstadoUtils.normalizarUf(uf);
	}

	public String getEstado() {
		return uf;
	}

	public void setEstado(String estado) {
		this.uf = estado;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
}
