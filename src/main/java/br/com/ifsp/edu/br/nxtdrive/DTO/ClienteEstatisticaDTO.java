package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.math.BigDecimal;

public class ClienteEstatisticaDTO {

    private String nome;
    private String email;
    private Long totalPedidos;
    private BigDecimal valorTotalGasto;

    public ClienteEstatisticaDTO() {
        this.totalPedidos = 0L;
        this.valorTotalGasto = BigDecimal.ZERO;
    }

    public ClienteEstatisticaDTO(String nome, String email, Long totalPedidos, BigDecimal valorTotalGasto) {
        this.nome = nome;
        this.email = email;
        this.totalPedidos = totalPedidos != null ? totalPedidos : 0L;
        this.valorTotalGasto = valorTotalGasto != null ? valorTotalGasto : BigDecimal.ZERO;
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

    public Long getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public BigDecimal getValorTotalGasto() {
        return valorTotalGasto;
    }

    public void setValorTotalGasto(BigDecimal valorTotalGasto) {
        this.valorTotalGasto = valorTotalGasto;
    }
}
