package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.math.BigDecimal;

public class EvolucaoVendasDTO {

    private String periodo;
    private BigDecimal totalFaturado;
    private Long quantidadePedidos;

    public EvolucaoVendasDTO() {
        this.totalFaturado = BigDecimal.ZERO;
        this.quantidadePedidos = 0L;
    }

    public EvolucaoVendasDTO(String periodo, BigDecimal totalFaturado, Long quantidadePedidos) {
        this.periodo = periodo;
        this.totalFaturado = totalFaturado != null ? totalFaturado : BigDecimal.ZERO;
        this.quantidadePedidos = quantidadePedidos != null ? quantidadePedidos : 0L;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public BigDecimal getTotalFaturado() {
        return totalFaturado;
    }

    public void setTotalFaturado(BigDecimal totalFaturado) {
        this.totalFaturado = totalFaturado;
    }

    public Long getQuantidadePedidos() {
        return quantidadePedidos;
    }

    public void setQuantidadePedidos(Long quantidadePedidos) {
        this.quantidadePedidos = quantidadePedidos;
    }
}
