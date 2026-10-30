package br.com.ifsp.edu.br.nxtdrive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ifsp.edu.br.nxtdrive.DTO.DashboardEstatisticasDTO;
import br.com.ifsp.edu.br.nxtdrive.model.Pedido;
import br.com.ifsp.edu.br.nxtdrive.model.Produto;
import br.com.ifsp.edu.br.nxtdrive.model.StatusPedido;
import br.com.ifsp.edu.br.nxtdrive.repository.PedidoRepository;
import br.com.ifsp.edu.br.nxtdrive.repository.ProdutoRepository;
import br.com.ifsp.edu.br.nxtdrive.service.EstatisticasAdminService;

@ExtendWith(MockitoExtension.class)
public class EstatisticasAdminServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private EstatisticasAdminService estatisticasAdminService;

    @Test
    @DisplayName("Garante cálculo correto de faturamento, ticket médio, clientes, dias e produtos para pedidos PAGOS")
    void deveCalcularEstatisticasCompletas() {
        // Setup de produtos do catálogo
        Produto p1 = new Produto();
        p1.setIdProduto(UUID.randomUUID());
        p1.setNome("Kit Turbo NXT");
        p1.setValor(300.0);

        Produto p2 = new Produto();
        p2.setIdProduto(UUID.randomUUID());
        p2.setNome("Filtro Esportivo");
        p2.setValor(50.0);

        Produto p3 = new Produto();
        p3.setIdProduto(UUID.randomUUID());
        p3.setNome("Escapamento Inox");
        p3.setValor(500.0);

        when(produtoRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        // Setup de Pedidos Pagos
        Pedido pedido1 = new Pedido(new BigDecimal("300.00"), "Pedido 1", "joao@nxtdrive.com");
        pedido1.setNomeCliente("João Silva");
        pedido1.setItensDescricao("1x Kit Turbo NXT");
        pedido1.setStatus(StatusPedido.PAGO);
        pedido1.setDataCriacao(LocalDateTime.now().minusDays(2));

        Pedido pedido2 = new Pedido(new BigDecimal("100.00"), "Pedido 2", "maria@nxtdrive.com");
        pedido2.setNomeCliente("Maria Souza");
        pedido2.setItensDescricao("2x Filtro Esportivo");
        pedido2.setStatus(StatusPedido.PAGO);
        pedido2.setDataCriacao(LocalDateTime.now().minusDays(1));

        Pedido pedido3 = new Pedido(new BigDecimal("600.00"), "Pedido 3", "joao@nxtdrive.com");
        pedido3.setNomeCliente("João Silva");
        pedido3.setItensDescricao("2x Kit Turbo NXT");
        pedido3.setStatus(StatusPedido.PAGO);
        pedido3.setDataCriacao(LocalDateTime.now().minusDays(1));

        when(pedidoRepository.buscarPedidosPorStatusEPeriodo(eq(StatusPedido.PAGO), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(pedido1, pedido2, pedido3));

        // Execução
        DashboardEstatisticasDTO stats = estatisticasAdminService.gerarEstatisticas("ULTIMOS_30_DIAS", null, null);

        // Asserções gerais
        assertNotNull(stats);
        assertEquals(new BigDecimal("1000.00"), stats.getFaturamentoTotal());
        assertEquals(3L, stats.getTotalPedidosPagos());
        // Ticket Médio: 1000 / 3 = 333.33
        assertEquals(new BigDecimal("333.33"), stats.getTicketMedio());

        // Clientes: João gastou 900 (2 pedidos), Maria gastou 100 (1 pedido)
        assertNotNull(stats.getClienteMaiorComprador());
        assertEquals("joao@nxtdrive.com", stats.getClienteMaiorComprador().getEmail());
        assertEquals(new BigDecimal("900.00"), stats.getClienteMaiorComprador().getValorTotalGasto());
        assertEquals(2L, stats.getClienteMaiorComprador().getTotalPedidos());

        assertNotNull(stats.getClienteMenorComprador());
        assertEquals("maria@nxtdrive.com", stats.getClienteMenorComprador().getEmail());
        assertEquals(new BigDecimal("100.00"), stats.getClienteMenorComprador().getValorTotalGasto());
        assertEquals(1L, stats.getClienteMenorComprador().getTotalPedidos());

        // Dias: ontem faturou 700 (pedidos 2 e 3), 2 dias atrás faturou 300
        assertNotNull(stats.getDiaMaiorVenda());
        assertEquals(new BigDecimal("700.00"), stats.getDiaMaiorVenda().getValorFaturado());
        assertEquals(2L, stats.getDiaMaiorVenda().getTotalVendas());

        assertNotNull(stats.getDiaMenorVenda());
        assertEquals(new BigDecimal("300.00"), stats.getDiaMenorVenda().getValorFaturado());
        assertEquals(1L, stats.getDiaMenorVenda().getTotalVendas());

        // Produtos: Kit Turbo vendeu 3 unidades, Filtro vendeu 2, Escapamento vendeu 0
        assertNotNull(stats.getProdutoMaisVendido());
        assertEquals("Kit Turbo NXT", stats.getProdutoMaisVendido().getNome());
        assertEquals(3L, stats.getProdutoMaisVendido().getQuantidadeVendida());

        assertNotNull(stats.getProdutoMenosVendido());
        assertEquals("Escapamento Inox", stats.getProdutoMenosVendido().getNome());
        assertEquals(0L, stats.getProdutoMenosVendido().getQuantidadeVendida());

        // Evolução temporal de vendas
        assertNotNull(stats.getFaturamentoPorPeriodo());
        assertTrue(stats.getFaturamentoPorPeriodo().size() >= 30);
    }

    @Test
    @DisplayName("Garante que dashboard retorna valores zerados e nulos de forma segura quando não há vendas no período")
    void deveRetornarZerosQuandoNaoHouverPedidosNoPeriodo() {
        when(produtoRepository.findAll()).thenReturn(new ArrayList<>());
        when(pedidoRepository.buscarPedidosPorStatusEPeriodo(eq(StatusPedido.PAGO), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        DashboardEstatisticasDTO stats = estatisticasAdminService.gerarEstatisticas("ULTIMOS_7_DIAS", null, null);

        assertNotNull(stats);
        assertEquals(BigDecimal.ZERO.setScale(2), stats.getFaturamentoTotal());
        assertEquals(0L, stats.getTotalPedidosPagos());
        assertEquals(BigDecimal.ZERO.setScale(2), stats.getTicketMedio());
        assertNull(stats.getClienteMaiorComprador());
        assertNull(stats.getClienteMenorComprador());
        assertNull(stats.getDiaMaiorVenda());
        assertNull(stats.getDiaMenorVenda());
        assertNull(stats.getProdutoMaisVendido());
        assertNull(stats.getProdutoMenosVendido());
        assertNotNull(stats.getFaturamentoPorPeriodo());
        assertEquals(8, stats.getFaturamentoPorPeriodo().size()); // 7 dias + hoje
    }
}
