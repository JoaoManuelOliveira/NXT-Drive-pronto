package br.com.ifsp.edu.br.nxtdrive.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "endereco")
public class Endereco {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID idEndereco;

	@NotBlank(message = "O CEP é obrigatório")
	@Size(max = 10, message = "O CEP deve ter no máximo 10 caracteres")
	@Column(name = "cep", nullable = false, length = 10)
	private String cep;

	@NotBlank(message = "A rua/logradouro é obrigatória")
	@Column(name = "logradouro", nullable = false)
	private String logradouro;

	@NotBlank(message = "O número é obrigatório")
	@Column(name = "numero", nullable = false)
	private String numero;

	@NotBlank(message = "O bairro é obrigatório")
	@Column(name = "bairro", nullable = false)
	private String bairro;

	@NotBlank(message = "A cidade/localidade é obrigatória")
	@Column(name = "localidade", nullable = false)
	private String localidade;

	@NotBlank(message = "O estado/UF é obrigatório")
	@Size(min = 2, max = 2, message = "A UF deve conter exatamente 2 caracteres")
	@Column(name = "uf", nullable = false, length = 2)
	private String uf;

	@Column(name = "complemento")
	private String complemento;

	public Endereco() {
	}

	public Endereco(String cep, String logradouro, String numero, String bairro, String localidade, String uf, String complemento) {
		this.cep = cep;
		this.logradouro = logradouro;
		this.numero = numero;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
		this.complemento = complemento;
	}

	public UUID getIdEndereco() {
		return idEndereco;
	}

	public void setIdEndereco(UUID idEndereco) {
		this.idEndereco = idEndereco;
	}

	// Alias para id
	public UUID getId() {
		return idEndereco;
	}

	public void setId(UUID id) {
		this.idEndereco = id;
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

	/**
	 * Retorna o endereço formatado de acordo com o padrão do sistema:
	 * Rua/Av, Nº - Bairro, Cidade/UF - CEP: XXXXX-XXX (Complemento)
	 */
	public String formatarEnderecoCompleto() {
		StringBuilder sb = new StringBuilder();
		if (logradouro != null && !logradouro.isBlank()) {
			sb.append(logradouro);
			if (numero != null && !numero.isBlank()) {
				sb.append(", Nº ").append(numero);
			}
			if (bairro != null && !bairro.isBlank()) {
				sb.append(" - ").append(bairro);
			}
			if (localidade != null && !localidade.isBlank()) {
				sb.append(", ").append(localidade);
			}
			if (uf != null && !uf.isBlank()) {
				sb.append("/").append(uf);
			}
			if (cep != null && !cep.isBlank()) {
				sb.append(" - CEP: ").append(cep);
			}
			if (complemento != null && !complemento.isBlank()) {
				sb.append(" (").append(complemento).append(")");
			}
		}
		return sb.toString();
	}
}