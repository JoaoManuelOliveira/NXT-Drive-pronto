package br.com.ifsp.edu.br.nxtdrive.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.ifsp.edu.br.nxtdrive.model.Pedido;
import br.com.ifsp.edu.br.nxtdrive.model.StatusPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Optional<Pedido> findByPaymentId(Long paymentId);

    List<Pedido> findByEmailPagadorOrderByDataCriacaoDesc(String emailPagador);

    List<Pedido> findAllByOrderByDataCriacaoDesc();

    @Query("SELECT p FROM Pedido p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:termo IS NULL OR " +
           "  LOWER(p.nomeCliente) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "  LOWER(p.emailPagador) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "  (p.cpfCliente IS NOT NULL AND p.cpfCliente LIKE CONCAT('%', :termo, '%')) OR " +
           "  (:uuidBusca IS NOT NULL AND p.id = :uuidBusca)) AND " +
           "(:dataInicio IS NULL OR p.dataCriacao >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.dataCriacao <= :dataFim) " +
           "ORDER BY p.dataCriacao DESC")
    List<Pedido> filtrarPedidos(
            @Param("status") StatusPedido status,
            @Param("termo") String termo,
            @Param("uuidBusca") UUID uuidBusca,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT p FROM Pedido p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:termo IS NULL OR " +
           "  LOWER(p.nomeCliente) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "  LOWER(p.emailPagador) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "  (p.cpfCliente IS NOT NULL AND p.cpfCliente LIKE CONCAT('%', :termo, '%')) OR " +
           "  (:uuidBusca IS NOT NULL AND p.id = :uuidBusca)) AND " +
           "(:dataInicio IS NULL OR p.dataCriacao >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.dataCriacao <= :dataFim)")
    Page<Pedido> filtrarPedidosPaginado(
            @Param("status") StatusPedido status,
            @Param("termo") String termo,
            @Param("uuidBusca") UUID uuidBusca,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = :status AND p.dataCriacao >= :dataInicio AND p.dataCriacao <= :dataFim ORDER BY p.dataCriacao ASC")
    List<Pedido> buscarPedidosPorStatusEPeriodo(
            @Param("status") StatusPedido status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.status = :status AND p.dataCriacao >= :dataInicio AND p.dataCriacao <= :dataFim")
    BigDecimal somarFaturamentoPorStatusEPeriodo(
            @Param("status") StatusPedido status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.status = :status AND p.dataCriacao >= :dataInicio AND p.dataCriacao <= :dataFim")
    Long contarPedidosPorStatusEPeriodo(
            @Param("status") StatusPedido status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
}
