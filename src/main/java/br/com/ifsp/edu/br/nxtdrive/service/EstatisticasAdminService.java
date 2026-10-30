package br.com.ifsp.edu.br.nxtdrive.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifsp.edu.br.nxtdrive.DTO.ClienteEstatisticaDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.DashboardEstatisticasDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.DiaEstatisticaDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.EvolucaoVendasDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.PeriodoFiltro;
import br.com.ifsp.edu.br.nxtdrive.DTO.ProdutoEstatisticaDTO;
import br.com.ifsp.edu.br.nxtdrive.model.Pedido;
import br.com.ifsp.edu.br.nxtdrive.model.Produto;
import br.com.ifsp.edu.br.nxtdrive.model.StatusPedido;
import br.com.ifsp.edu.br.nxtdrive.repository.PedidoRepository;
import br.com.ifsp.edu.br.nxtdrive.repository.ProdutoRepository;

@Service
public class EstatisticasAdminService {

    private static final Logger log = LoggerFactory.getLogger(EstatisticasAdminService.class);
    private static final Pattern QTD_PATTERN = Pattern.compile("^(\\d+)\\s*[xX]?\\s*(.*)$");

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Gera relatório completo de estatísticas e analytics para o painel administrativo.
     * Considera estritamente pedidos com status PAGO dentro do período especificado.
     */
    @Transactional(readOnly = true)
    public DashboardEstatisticasDTO gerarEstatisticas(String periodoStr, LocalDateTime dataInicioParam, LocalDateTime dataFimParam) {
        PeriodoFiltro periodo = PeriodoFiltro.fromString(periodoStr);

        LocalDateTime fim = (dataFimParam != null)
                ? dataFimParam
                : LocalDateTime.now().with(LocalTime.MAX);

        LocalDateTime inicio;
        if (periodo == PeriodoFiltro.ULTIMOS_7_DIAS) {
            inicio = LocalDateTime.now().minusDays(7).with(LocalTime.MIN);
        } else if (periodo == PeriodoFiltro.ULTIMOS_30_DIAS) {
            inicio = LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        } else if (periodo == PeriodoFiltro.ULTIMOS_12_MESES) {
            inicio = LocalDateTime.now().minusMonths(12).with(LocalTime.MIN);
        } else {
            inicio = (dataInicioParam != null)
                    ? dataInicioParam
                    : LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        }

        if (inicio.isAfter(fim)) {
            LocalDateTime temp = inicio;
            inicio = fim;
            fim = temp;
        }

        log.info("Calculando estatísticas administrativas para período: {} ({} até {})", periodo, inicio, fim);

        // Busca todos os pedidos PAGOS no período
        List<Pedido> pedidosPagos = pedidoRepository.buscarPedidosPorStatusEPeriodo(StatusPedido.PAGO, inicio, fim);

        DashboardEstatisticasDTO dto = new DashboardEstatisticasDTO();

        // 1. FATURAMENTO TOTAL E TOTAL DE PEDIDOS PAGOS
        BigDecimal faturamentoTotal = pedidosPagos.stream()
                .map(p -> p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        long totalPedidosPagos = pedidosPagos.size();

        // 2. TICKET MÉDIO
        BigDecimal ticketMedio = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (totalPedidosPagos > 0) {
            ticketMedio = faturamentoTotal.divide(BigDecimal.valueOf(totalPedidosPagos), 2, RoundingMode.HALF_UP);
        }

        dto.setFaturamentoTotal(faturamentoTotal);
        dto.setTotalPedidosPagos(totalPedidosPagos);
        dto.setTicketMedio(ticketMedio);

        // 3. ESTATÍSTICAS DE CLIENTES (MAIOR E MENOR COMPRADOR)
        calcularEstatisticasClientes(pedidosPagos, dto);

        // 4. ESTATÍSTICAS DE DIAS (MAIOR E MENOR VENDA)
        calcularEstatisticasDias(pedidosPagos, dto);

        // 5. ESTATÍSTICAS DE PRODUTOS (RANKING, MAIS E MENOS VENDIDO)
        calcularEstatisticasProdutos(pedidosPagos, dto);

        // 6. EVOLUÇÃO DAS VENDAS NO PERÍODO (SÉRIE TEMPORAL PARA GRÁFICOS)
        calcularEvolucaoVendas(pedidosPagos, periodo, inicio, fim, dto);

        return dto;
    }

    private void calcularEstatisticasClientes(List<Pedido> pedidosPagos, DashboardEstatisticasDTO dto) {
        if (pedidosPagos.isEmpty()) {
            return;
        }

        Map<String, ClienteEstatisticaDTO> mapaClientes = new HashMap<>();

        for (Pedido p : pedidosPagos) {
            String email = (p.getEmailPagador() != null && !p.getEmailPagador().isBlank())
                    ? p.getEmailPagador().trim()
                    : "cliente@nxtdrive.com.br";

            String nome = (p.getNomeCliente() != null && !p.getNomeCliente().isBlank())
                    ? p.getNomeCliente().trim()
                    : email;

            BigDecimal valorPedido = p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO;

            ClienteEstatisticaDTO clienteDto = mapaClientes.computeIfAbsent(
                    email.toLowerCase(),
                    k -> new ClienteEstatisticaDTO(nome, email, 0L, BigDecimal.ZERO)
            );

            if (clienteDto.getNome() == null || clienteDto.getNome().equalsIgnoreCase(email)) {
                clienteDto.setNome(nome);
            }
            clienteDto.setTotalPedidos(clienteDto.getTotalPedidos() + 1);
            clienteDto.setValorTotalGasto(clienteDto.getValorTotalGasto().add(valorPedido).setScale(2, RoundingMode.HALF_UP));
        }

        List<ClienteEstatisticaDTO> ranking = new ArrayList<>(mapaClientes.values());
        ranking.sort(
                Comparator.comparing(ClienteEstatisticaDTO::getValorTotalGasto).reversed()
                        .thenComparing(Comparator.comparing(ClienteEstatisticaDTO::getTotalPedidos).reversed())
        );

        if (!ranking.isEmpty()) {
            dto.setClienteMaiorComprador(ranking.get(0));
            dto.setClienteMenorComprador(ranking.get(ranking.size() - 1));
        }
    }

    private void calcularEstatisticasDias(List<Pedido> pedidosPagos, DashboardEstatisticasDTO dto) {
        if (pedidosPagos.isEmpty()) {
            return;
        }

        DateTimeFormatter diaFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<LocalDate, DiaEstatisticaDTO> mapaDias = new HashMap<>();

        for (Pedido p : pedidosPagos) {
            if (p.getDataCriacao() == null) continue;
            LocalDate data = p.getDataCriacao().toLocalDate();
            BigDecimal valor = p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO;

            DiaEstatisticaDTO diaDto = mapaDias.computeIfAbsent(
                    data,
                    d -> new DiaEstatisticaDTO(d.format(diaFmt), 0L, BigDecimal.ZERO)
            );

            diaDto.setTotalVendas(diaDto.getTotalVendas() + 1);
            diaDto.setValorFaturado(diaDto.getValorFaturado().add(valor).setScale(2, RoundingMode.HALF_UP));
        }

        List<DiaEstatisticaDTO> ranking = new ArrayList<>(mapaDias.values());
        ranking.sort(
                Comparator.comparing(DiaEstatisticaDTO::getValorFaturado).reversed()
                        .thenComparing(Comparator.comparing(DiaEstatisticaDTO::getTotalVendas).reversed())
        );

        if (!ranking.isEmpty()) {
            dto.setDiaMaiorVenda(ranking.get(0));
            dto.setDiaMenorVenda(ranking.get(ranking.size() - 1));
        }
    }

    private void calcularEstatisticasProdutos(List<Pedido> pedidosPagos, DashboardEstatisticasDTO dto) {
        List<Produto> catalogo = produtoRepository.findAll();
        Map<String, BigDecimal> precosCatalogo = new HashMap<>();
        Map<String, ProdutoEstatisticaDTO> mapaProdutos = new LinkedHashMap<>();

        // Inicializa mapa com produtos do catálogo para garantir listagem e detecção do menos vendido
        for (Produto prod : catalogo) {
            if (prod.getNome() != null && !prod.getNome().isBlank()) {
                String nomeLimpo = prod.getNome().trim();
                precosCatalogo.put(nomeLimpo.toLowerCase(), BigDecimal.valueOf(prod.getValor()).setScale(2, RoundingMode.HALF_UP));
                mapaProdutos.put(nomeLimpo.toLowerCase(), new ProdutoEstatisticaDTO(nomeLimpo, 0L, BigDecimal.ZERO));
            }
        }

        // Processa os itens dos pedidos pagos
        for (Pedido p : pedidosPagos) {
            String textoItens = (p.getItensDescricao() != null && !p.getItensDescricao().isBlank())
                    ? p.getItensDescricao().trim()
                    : p.getDescricao();

            if (textoItens == null || textoItens.isBlank()) {
                textoItens = "Produto Diversos NXT Drive";
            }

            // Separa múltiplos produtos unidos por '+', ',', ';' ou quebras de linha
            String[] partes = textoItens.split("(\\s*\\+\\s*|\\s*,\\s*|\\s*;\\s*|\\r?\\n)");
            int qtdPartes = Math.max(1, partes.length);
            BigDecimal valorPorItem = (p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO)
                    .divide(BigDecimal.valueOf(qtdPartes), 2, RoundingMode.HALF_UP);

            for (String parte : partes) {
                String itemStr = parte.trim();
                if (itemStr.isEmpty()) continue;

                long quantidade = 1L;
                Matcher matcher = QTD_PATTERN.matcher(itemStr);
                if (matcher.matches()) {
                    try {
                        quantidade = Long.parseLong(matcher.group(1));
                        itemStr = matcher.group(2).trim();
                    } catch (Exception ignored) {
                        quantidade = 1L;
                    }
                }

                if (itemStr.isEmpty()) {
                    itemStr = "Item Avulso";
                }

                final String nomeExibicao = itemStr;

                // Identifica o nome padronizado do catálogo se houver correspondência
                String chave = itemStr.toLowerCase();
                for (String nomeCat : precosCatalogo.keySet()) {
                    if (chave.contains(nomeCat) || nomeCat.contains(chave)) {
                        chave = nomeCat;
                        break;
                    }
                }

                ProdutoEstatisticaDTO prodStat = mapaProdutos.computeIfAbsent(
                        chave,
                        k -> new ProdutoEstatisticaDTO(nomeExibicao, 0L, BigDecimal.ZERO)
                );

                prodStat.setQuantidadeVendida(prodStat.getQuantidadeVendida() + quantidade);

                BigDecimal precoUnit = precosCatalogo.get(chave);
                BigDecimal subtotal;
                if (precoUnit != null && precoUnit.compareTo(BigDecimal.ZERO) > 0) {
                    subtotal = precoUnit.multiply(BigDecimal.valueOf(quantidade));
                } else {
                    subtotal = valorPorItem;
                }

                prodStat.setTotalFaturado(prodStat.getTotalFaturado().add(subtotal).setScale(2, RoundingMode.HALF_UP));
            }
        }

        List<ProdutoEstatisticaDTO> ranking = new ArrayList<>(mapaProdutos.values());
        ranking.sort(
                Comparator.comparing(ProdutoEstatisticaDTO::getQuantidadeVendida).reversed()
                        .thenComparing(Comparator.comparing(ProdutoEstatisticaDTO::getTotalFaturado).reversed())
        );

        dto.setVendasPorProduto(ranking);

        if (!ranking.isEmpty()) {
            dto.setProdutoMaisVendido(ranking.get(0));
            dto.setProdutoMenosVendido(ranking.get(ranking.size() - 1));
        }
    }

    private void calcularEvolucaoVendas(
            List<Pedido> pedidosPagos,
            PeriodoFiltro periodo,
            LocalDateTime inicio,
            LocalDateTime fim,
            DashboardEstatisticasDTO dto) {

        long diferencaDias = ChronoUnit.DAYS.between(inicio.toLocalDate(), fim.toLocalDate());
        boolean agrupamentoMensal = (periodo == PeriodoFiltro.ULTIMOS_12_MESES) || (diferencaDias > 31);

        List<EvolucaoVendasDTO> evolucao = new ArrayList<>();

        if (agrupamentoMensal) {
            DateTimeFormatter mesFmt = DateTimeFormatter.ofPattern("MM/yyyy");
            YearMonth inicioMes = YearMonth.from(inicio);
            YearMonth fimMes = YearMonth.from(fim);

            Map<YearMonth, EvolucaoVendasDTO> serieTemporal = new LinkedHashMap<>();
            YearMonth corrente = inicioMes;
            while (!corrente.isAfter(fimMes)) {
                serieTemporal.put(corrente, new EvolucaoVendasDTO(corrente.format(mesFmt), BigDecimal.ZERO, 0L));
                corrente = corrente.plusMonths(1);
            }

            for (Pedido p : pedidosPagos) {
                if (p.getDataCriacao() == null) continue;
                YearMonth ym = YearMonth.from(p.getDataCriacao());
                EvolucaoVendasDTO ponto = serieTemporal.get(ym);
                if (ponto != null) {
                    BigDecimal valor = p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO;
                    ponto.setTotalFaturado(ponto.getTotalFaturado().add(valor).setScale(2, RoundingMode.HALF_UP));
                    ponto.setQuantidadePedidos(ponto.getQuantidadePedidos() + 1);
                }
            }

            evolucao.addAll(serieTemporal.values());
        } else {
            DateTimeFormatter diaFmt = DateTimeFormatter.ofPattern("dd/MM");
            LocalDate inicioDia = inicio.toLocalDate();
            LocalDate fimDia = fim.toLocalDate();

            Map<LocalDate, EvolucaoVendasDTO> serieTemporal = new LinkedHashMap<>();
            LocalDate corrente = inicioDia;
            while (!corrente.isAfter(fimDia)) {
                serieTemporal.put(corrente, new EvolucaoVendasDTO(corrente.format(diaFmt), BigDecimal.ZERO, 0L));
                corrente = corrente.plusDays(1);
            }

            for (Pedido p : pedidosPagos) {
                if (p.getDataCriacao() == null) continue;
                LocalDate d = p.getDataCriacao().toLocalDate();
                EvolucaoVendasDTO ponto = serieTemporal.get(d);
                if (ponto != null) {
                    BigDecimal valor = p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO;
                    ponto.setTotalFaturado(ponto.getTotalFaturado().add(valor).setScale(2, RoundingMode.HALF_UP));
                    ponto.setQuantidadePedidos(ponto.getQuantidadePedidos() + 1);
                }
            }

            evolucao.addAll(serieTemporal.values());
        }

        dto.setFaturamentoPorPeriodo(evolucao);
    }
}
