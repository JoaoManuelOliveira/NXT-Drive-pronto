package br.com.ifsp.edu.br.nxtdrive.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifsp.edu.br.nxtdrive.DTO.PedidoAdminResponseDTO;
import br.com.ifsp.edu.br.nxtdrive.model.Pedido;
import br.com.ifsp.edu.br.nxtdrive.model.StatusPedido;
import br.com.ifsp.edu.br.nxtdrive.repository.PedidoRepository;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Lista todos os pedidos cadastrados ordenados do mais recente para o mais antigo.
     */
    @Transactional(readOnly = true)
    public List<PedidoAdminResponseDTO> listarTodosPedidosAdmin() {
        log.info("Listando todos os pedidos para o painel administrativo (ORDER BY dataCriacao DESC)");
        return pedidoRepository.findAllByOrderByDataCriacaoDesc()
                .stream()
                .map(PedidoAdminResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista pedidos com suporte a filtros dinâmicos por status, busca textual (cliente, e-mail, CPF, UUID) e intervalo de datas.
     * A ordenação é mantida estritamente como dataCriacao DESC.
     */
    @Transactional(readOnly = true)
    public List<PedidoAdminResponseDTO> filtrarPedidosAdmin(
            StatusPedido status, String busca, LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.info("Filtrando pedidos admin: status={}, busca={}, inicio={}, fim={}", status, busca, dataInicio, dataFim);
        String termo = (busca != null && !busca.trim().isBlank()) ? busca.trim() : null;
        UUID uuidBusca = extrairUuid(termo);

        return pedidoRepository.filtrarPedidos(status, termo, uuidBusca, dataInicio, dataFim)
                .stream()
                .map(PedidoAdminResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista pedidos paginados com suporte aos filtros combinados.
     */
    @Transactional(readOnly = true)
    public Page<PedidoAdminResponseDTO> filtrarPedidosAdminPaginado(
            StatusPedido status, String busca, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        log.info("Filtrando pedidos admin paginado: status={}, busca={}, inicio={}, fim={}, pageable={}",
                status, busca, dataInicio, dataFim, pageable);
        String termo = (busca != null && !busca.trim().isBlank()) ? busca.trim() : null;
        UUID uuidBusca = extrairUuid(termo);

        return pedidoRepository.filtrarPedidosPaginado(status, termo, uuidBusca, dataInicio, dataFim, pageable)
                .map(PedidoAdminResponseDTO::fromEntity);
    }

    /**
     * Busca um pedido específico pelo seu UUID.
     */
    @Transactional(readOnly = true)
    public PedidoAdminResponseDTO buscarPedidoPorId(UUID id) {
        log.info("Buscando detalhes do pedido ID: {}", id);
        return pedidoRepository.findById(id)
                .map(PedidoAdminResponseDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com o ID: " + id));
    }

    /**
     * Atualiza o status do pedido manualmente pelo painel admin.
     */
    @Transactional
    public PedidoAdminResponseDTO atualizarStatus(UUID id, StatusPedido novoStatus) {
        log.info("Atualizando status do pedido {} para {}", id, novoStatus);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
        pedido.setStatus(novoStatus);
        Pedido salvo = pedidoRepository.save(pedido);
        return PedidoAdminResponseDTO.fromEntity(salvo);
    }

    private UUID extrairUuid(String termo) {
        if (termo == null || termo.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(termo.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
