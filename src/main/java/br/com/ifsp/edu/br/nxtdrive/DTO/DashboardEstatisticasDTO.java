package br.com.ifsp.edu.br.nxtdrive.DTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardEstatisticasDTO {

    private BigDecimal faturamentoTotal = BigDecimal.ZERO;
    private Long totalPedidosPagos = 0L;
    private BigDecimal ticketMedio = BigDecimal.ZERO;

    private ProdutoEstatisticaDTO produtoMaisVendido;
    private ProdutoEstatisticaDTO produtoMenosVendido;

    private ClienteEstatisticaDTO clienteMaiorComprador;
    private ClienteEstatisticaDTO clienteMenorComprador;

    private DiaEstatisticaDTO diaMaiorVenda;
    private DiaEstatisticaDTO diaMenorVenda;

    private List<ProdutoEstatisticaDTO> vendasPorProduto = new ArrayList<>();
    private List<EvolucaoVendasDTO> faturamentoPorPeriodo = new ArrayList<>();

    public DashboardEstatisticasDTO() {
    }

    public BigDecimal getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public void setFaturamentoTotal(BigDecimal faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }

    public Long getTotalPedidosPagos() {
        return totalPedidosPagos;
    }

    public void setTotalPedidosPagos(Long totalPedidosPagos) {
        this.totalPedidosPagos = totalPedidosPagos;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }

    public ProdutoEstatisticaDTO getProdutoMaisVendido() {
        return produtoMaisVendido;
    }

    public void setProdutoMaisVendido(ProdutoEstatisticaDTO produtoMaisVendido) {
        this.produtoMaisVendido = produtoMaisVendido;
    }

    public ProdutoEstatisticaDTO getProdutoMenosVendido() {
        return produtoMenosVendido;
    }

    public void setProdutoMenosVendido(ProdutoEstatisticaDTO produtoMenosVendido) {
        this.produtoMenosVendido = produtoMenosVendido;
    }

    public ClienteEstatisticaDTO getClienteMaiorComprador() {
        return clienteMaiorComprador;
    }

    public void setClienteMaiorComprador(ClienteEstatisticaDTO clienteMaiorComprador) {
        this.clienteMaiorComprador = clienteMaiorComprador;
    }

    public ClienteEstatisticaDTO getClienteMenorComprador() {
        return clienteMenorComprador;
    }

    public void setClienteMenorComprador(ClienteEstatisticaDTO clienteMenorComprador) {
        this.clienteMenorComprador = clienteMenorComprador;
    }

    public DiaEstatisticaDTO getDiaMaiorVenda() {
        return diaMaiorVenda;
    }

    public void setDiaMaiorVenda(DiaEstatisticaDTO diaMaiorVenda) {
        this.diaMaiorVenda = diaMaiorVenda;
    }

    public DiaEstatisticaDTO getDiaMenorVenda() {
        return diaMenorVenda;
    }

    public void setDiaMenorVenda(DiaEstatisticaDTO diaMenorVenda) {
        this.diaMenorVenda = diaMenorVenda;
    }

    public List<ProdutoEstatisticaDTO> getVendasPorProduto() {
        return vendasPorProduto;
    }

    public void setVendasPorProduto(List<ProdutoEstatisticaDTO> vendasPorProduto) {
        this.vendasPorProduto = vendasPorProduto;
    }

    public List<EvolucaoVendasDTO> getFaturamentoPorPeriodo() {
        return faturamentoPorPeriodo;
    }

    public void setFaturamentoPorPeriodo(List<EvolucaoVendasDTO> faturamentoPorPeriodo) {
        this.faturamentoPorPeriodo = faturamentoPorPeriodo;
    }
}
