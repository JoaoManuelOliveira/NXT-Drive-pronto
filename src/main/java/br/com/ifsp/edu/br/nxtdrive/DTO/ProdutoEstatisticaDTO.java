package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.math.BigDecimal;

public class ProdutoEstatisticaDTO {

    private String nome;
    private Long quantidadeVendida;
    private BigDecimal totalFaturado;

    public ProdutoEstatisticaDTO() {
        this.quantidadeVendida = 0L;
        this.totalFaturado = BigDecimal.ZERO;
    }

    public ProdutoEstatisticaDTO(String nome, Long quantidadeVendida, BigDecimal totalFaturado) {
        this.nome = nome;
        this.quantidadeVendida = quantidadeVendida != null ? quantidadeVendida : 0L;
        this.totalFaturado = totalFaturado != null ? totalFaturado : BigDecimal.ZERO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(Long quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }

    public BigDecimal getTotalFaturado() {
        return totalFaturado;
    }

    public void setTotalFaturado(BigDecimal totalFaturado) {
        this.totalFaturado = totalFaturado;
    }
}
