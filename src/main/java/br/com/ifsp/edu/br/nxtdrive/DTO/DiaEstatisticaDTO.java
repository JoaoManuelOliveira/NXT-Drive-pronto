package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.math.BigDecimal;

public class DiaEstatisticaDTO {

    private String data;
    private Long totalVendas;
    private BigDecimal valorFaturado;

    public DiaEstatisticaDTO() {
        this.totalVendas = 0L;
        this.valorFaturado = BigDecimal.ZERO;
    }

    public DiaEstatisticaDTO(String data, Long totalVendas, BigDecimal valorFaturado) {
        this.data = data;
        this.totalVendas = totalVendas != null ? totalVendas : 0L;
        this.valorFaturado = valorFaturado != null ? valorFaturado : BigDecimal.ZERO;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(Long totalVendas) {
        this.totalVendas = totalVendas;
    }

    public BigDecimal getValorFaturado() {
        return valorFaturado;
    }

    public void setValorFaturado(BigDecimal valorFaturado) {
        this.valorFaturado = valorFaturado;
    }
}
