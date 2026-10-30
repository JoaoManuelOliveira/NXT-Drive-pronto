package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.util.UUID;

import br.com.ifsp.edu.br.nxtdrive.model.Endereco;

public class EnderecoDTO {

	private UUID idEndereco;
	private String cep;
	private String logradouro;
	private String numero;
	private String bairro;
	private String localidade;
	private String uf;
	private String complemento;

	public EnderecoDTO() {
	}

	public EnderecoDTO(String cep, String logradouro, String numero, String bairro, String localidade, String uf, String complemento) {
		this.cep = cep;
		this.logradouro = logradouro;
		this.numero = numero;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
		this.complemento = complemento;
	}

	public static EnderecoDTO fromEntity(Endereco endereco) {
		if (endereco == null) return null;
		EnderecoDTO dto = new EnderecoDTO();
		dto.setIdEndereco(endereco.getIdEndereco());
		dto.setCep(endereco.getCep());
		dto.setLogradouro(endereco.getLogradouro());
		dto.setNumero(endereco.getNumero());
		dto.setBairro(endereco.getBairro());
		dto.setLocalidade(endereco.getLocalidade());
		dto.setUf(endereco.getUf());
		dto.setComplemento(endereco.getComplemento());
		return dto;
	}

	public Endereco toEntity() {
		Endereco end = new Endereco();
		end.setIdEndereco(this.idEndereco);
		end.setCep(this.cep);
		end.setLogradouro(this.logradouro != null ? this.logradouro : "");
		end.setNumero(this.numero != null ? this.numero : "");
		end.setBairro(this.bairro != null ? this.bairro : "");
		end.setLocalidade(this.localidade != null ? this.localidade : "");
		end.setUf(this.uf != null ? this.uf : "");
		end.setComplemento(this.complemento);
		return end;
	}

	public UUID getIdEndereco() {
		return idEndereco;
	}

	public void setIdEndereco(UUID idEndereco) {
		this.idEndereco = idEndereco;
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

	// Alias rua <-> logradouro
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

	// Alias cidade <-> localidade
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
		this.uf = uf;
	}

	// Alias estado <-> uf
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
