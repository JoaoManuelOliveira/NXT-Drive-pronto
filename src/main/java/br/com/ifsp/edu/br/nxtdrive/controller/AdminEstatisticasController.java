package br.com.ifsp.edu.br.nxtdrive.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifsp.edu.br.nxtdrive.DTO.DashboardEstatisticasDTO;
import br.com.ifsp.edu.br.nxtdrive.service.EstatisticasAdminService;

@RestController
@RequestMapping("/api/v1/admin/estatisticas")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminEstatisticasController {

    @Autowired
    private EstatisticasAdminService estatisticasAdminService;

    /**
     * Endpoint protegido exclusivo para ADMIN que retorna métricas analíticas e estatísticas completas:
     * - Faturamento total e ticket médio (somente pedidos PAGOS)
     * - Produtos mais e menos vendidos com ranking
     * - Clientes com maior e menor volume de compras
     * - Dias com maior e menor faturamento
     * - Série temporal de evolução de vendas para gráficos
     *
     * Parâmetros opcionais:
     * - periodo: ULTIMOS_7_DIAS, ULTIMOS_30_DIAS, ULTIMOS_12_MESES, CUSTOM
     * - dataInicio e dataFim (obrigatórios se periodo=CUSTOM)
     */
    @GetMapping
    public ResponseEntity<DashboardEstatisticasDTO> obterEstatisticas(
            @RequestParam(defaultValue = "ULTIMOS_30_DIAS") String periodo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        DashboardEstatisticasDTO estatisticas = estatisticasAdminService.gerarEstatisticas(periodo, dataInicio, dataFim);
        return ResponseEntity.ok(estatisticas);
    }
}
