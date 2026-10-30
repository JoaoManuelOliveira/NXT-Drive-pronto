package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public class PagamentoResponseDTO {

    private Long paymentId;
    private UUID pedidoId;
    private String status;
    private String statusDetail;
    private String qrCode;
    private String qrCodeBase64;
    private String mensagem;
    private BigDecimal valor;

    public PagamentoResponseDTO() {
    }

    public PagamentoResponseDTO(Long paymentId, UUID pedidoId, String status, String statusDetail,
            String qrCode, String qrCodeBase64, String mensagem, BigDecimal valor) {
        this.paymentId = paymentId;
        this.pedidoId = pedidoId;
        this.status = status;
        this.statusDetail = statusDetail;
        this.qrCode = qrCode;
        this.qrCodeBase64 = qrCodeBase64;
        this.mensagem = mensagem;
        this.valor = valor;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(UUID pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}