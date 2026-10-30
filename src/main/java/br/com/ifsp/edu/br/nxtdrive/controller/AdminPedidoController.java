package br.com.ifsp.edu.br.nxtdrive.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifsp.edu.br.nxtdrive.DTO.PedidoAdminResponseDTO;
import br.com.ifsp.edu.br.nxtdrive.model.StatusPedido;
import br.com.ifsp.edu.br.nxtdrive.service.PedidoService;

@RestController
@RequestMapping("/api/v1/admin/pedidos")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminPedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * Endpoint protegido exclusivo para ADMIN que lista pedidos em ordem decrescente (dataCriacao DESC),
     * com suporte a filtros opcionais combinados:
     * - status (PAGO, PENDENTE, CANCELADO, REJEITADO, etc.)
     * - busca textual (nome do cliente, email, CPF ou ID UUID)
     * - período (dataInicio e dataFim)
     * - paginação opcional via Pageable (page e size)
     */
    @GetMapping
    public ResponseEntity<?> listarPedidosAdmin(
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @PageableDefault(sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        String termo = (busca != null && !busca.isBlank()) ? busca : cliente;

        if (page != null || size != null) {
            Page<PedidoAdminResponseDTO> pagina = pedidoService.filtrarPedidosAdminPaginado(
                    status, termo, dataInicio, dataFim, pageable);
            return ResponseEntity.ok(pagina);
        }

        List<PedidoAdminResponseDTO> pedidos = pedidoService.filtrarPedidosAdmin(
                status, termo, dataInicio, dataFim);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Endpoint protegido para buscar um pedido por ID (UUID).
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoAdminResponseDTO> buscarPedidoPorId(@PathVariable UUID id) {
        PedidoAdminResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Endpoint para atualização manual do status do pedido pelo administrador.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoAdminResponseDTO> atualizarStatus(
            @PathVariable UUID id,
            @RequestParam("status") StatusPedido status) {
        PedidoAdminResponseDTO atualizado = pedidoService.atualizarStatus(id, status);
        return ResponseEntity.ok(atualizado);
    }
}
