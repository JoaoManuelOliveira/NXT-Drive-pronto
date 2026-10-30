package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.ifsp.edu.br.nxtdrive.model.Pedido;
import br.com.ifsp.edu.br.nxtdrive.model.StatusPedido;

public class PedidoAdminResponseDTO {

    private UUID id;
    private String nomeCliente;
    private String emailPagador;
    private String cpfCliente;
    private String telefoneCliente;
    private String itensDescricao;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String complemento;
    private String enderecoCompleto;
    private BigDecimal valorTotal;
    private String metodoPagamento;
    private StatusPedido status;
    private LocalDateTime dataCriacao;
    private Long paymentId;

    public PedidoAdminResponseDTO() {
    }

    public static PedidoAdminResponseDTO fromEntity(Pedido pedido) {
        if (pedido == null) return null;

        PedidoAdminResponseDTO dto = new PedidoAdminResponseDTO();
        dto.setId(pedido.getId());
        dto.setNomeCliente(pedido.getNomeCliente() != null ? pedido.getNomeCliente() : "Cliente NXT Drive");
        dto.setEmailPagador(pedido.getEmailPagador());
        dto.setCpfCliente(pedido.getCpfCliente());
        dto.setTelefoneCliente(pedido.getTelefoneCliente());
        dto.setItensDescricao(pedido.getItensDescricao() != null ? pedido.getItensDescricao() : pedido.getDescricao());
        dto.setRua(pedido.getEnderecoRua());
        dto.setNumero(pedido.getEnderecoNumero());
        dto.setBairro(pedido.getEnderecoBairro());
        dto.setCidade(pedido.getEnderecoCidade());
        dto.setEstado(pedido.getEnderecoUf());
        dto.setCep(pedido.getEnderecoCep());
        dto.setComplemento(pedido.getEnderecoComplemento());

        // Monta o endereço de entrega completo formatado: Rua/Av, Nº - Bairro, Cidade/UF - CEP: XXXXX-XXX (Complemento)
        StringBuilder sb = new StringBuilder();
        if (pedido.getEnderecoRua() != null && !pedido.getEnderecoRua().isBlank()) {
            sb.append(pedido.getEnderecoRua());
            if (pedido.getEnderecoNumero() != null && !pedido.getEnderecoNumero().isBlank()) {
                sb.append(", Nº ").append(pedido.getEnderecoNumero());
            }
            if (pedido.getEnderecoBairro() != null && !pedido.getEnderecoBairro().isBlank()) {
                sb.append(" - ").append(pedido.getEnderecoBairro());
            }
            if (pedido.getEnderecoCidade() != null && !pedido.getEnderecoCidade().isBlank()) {
                sb.append(", ").append(pedido.getEnderecoCidade());
            }
            if (pedido.getEnderecoUf() != null && !pedido.getEnderecoUf().isBlank()) {
                sb.append("/").append(pedido.getEnderecoUf());
            }
            if (pedido.getEnderecoCep() != null && !pedido.getEnderecoCep().isBlank()) {
                sb.append(" - CEP: ").append(pedido.getEnderecoCep());
            }
            if (pedido.getEnderecoComplemento() != null && !pedido.getEnderecoComplemento().isBlank()) {
                sb.append(" (").append(pedido.getEnderecoComplemento()).append(")");
            }
        } else {
            sb.append("Endereço não informado");
        }
        dto.setEnderecoCompleto(sb.toString());

        dto.setValorTotal(pedido.getValorTotal());
        dto.setMetodoPagamento(pedido.getMetodoPagamento());
        dto.setStatus(pedido.getStatus());
        dto.setDataCriacao(pedido.getDataCriacao());
        dto.setPaymentId(pedido.getPaymentId());

        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEmailPagador() {
        return emailPagador;
    }

    public void setEmailPagador(String emailPagador) {
        this.emailPagador = emailPagador;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public String getTelefoneCliente() {
        return telefoneCliente;
    }

    public void setTelefoneCliente(String telefoneCliente) {
        this.telefoneCliente = telefoneCliente;
    }

    public String getItensDescricao() {
        return itensDescricao;
    }

    public void setItensDescricao(String itensDescricao) {
        this.itensDescricao = itensDescricao;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
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

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}
