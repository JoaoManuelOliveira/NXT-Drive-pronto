package br.com.ifsp.edu.br.nxtdrive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoCartaoRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoPixRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoResponseDTO;
import br.com.ifsp.edu.br.nxtdrive.model.Pedido;
import br.com.ifsp.edu.br.nxtdrive.model.StatusPedido;
import br.com.ifsp.edu.br.nxtdrive.repository.PedidoRepository;
import br.com.ifsp.edu.br.nxtdrive.service.PagamentoService;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceUuidTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pagamentoService, "webhookUrl", "https://api.nxtdrive.com/webhooks");
    }

    @Test
    @DisplayName("Garante que a entidade Pedido é identificada por UUID e possui setters/getters consistentes")
    void deveGarantirIdentificadorUuidNaEntidadePedido() {
        UUID uuidEsperado = UUID.randomUUID();
        Pedido pedido = new Pedido(new BigDecimal("150.00"), "Peça Automotiva", "cliente@nxtdrive.com");
        pedido.setId(uuidEsperado);

        assertEquals(uuidEsperado, pedido.getId());
        assertEquals(uuidEsperado, pedido.getIdPedido());
        assertEquals(StatusPedido.PENDENTE, pedido.getStatus());
        assertEquals(new BigDecimal("150.00"), pedido.getValorTotal());
    }

    @Test
    @DisplayName("Garante que os DTOs utilizam UUID para identificação do pedido")
    void deveGarantirUsoDeUuidNosDtosDePagamento() {
        UUID pedidoUuid = UUID.randomUUID();

        // DTO Pix
        PagamentoPixRequestDTO pixDTO = new PagamentoPixRequestDTO(
                new BigDecimal("99.90"), "comprador@nxtdrive.com", "Kit Freio", pedidoUuid);
        assertEquals(pedidoUuid, pixDTO.getPedidoId());

        // DTO Cartão
        PagamentoCartaoRequestDTO cartaoDTO = new PagamentoCartaoRequestDTO(
                "token_123", 3, "visa", "comprador@nxtdrive.com",
                new BigDecimal("299.90"), pedidoUuid, "Kit Suspensão");
        assertEquals(pedidoUuid, cartaoDTO.getPedidoId());

        // DTO Resposta
        Long paymentIdMercadoPago = 987654321L;
        PagamentoResponseDTO responseDTO = new PagamentoResponseDTO(
                paymentIdMercadoPago,
                pedidoUuid,
                "approved",
                "accredited",
                "00020126580014br.gov.bcb.pix...",
                "iVBORw0KGgoAAAANSUhEUg...",
                "Pagamento aprovado",
                new BigDecimal("299.90")
        );

        assertEquals(pedidoUuid, responseDTO.getPedidoId(), "O pedidoId na resposta deve ser estritamente UUID");
        assertEquals(paymentIdMercadoPago, responseDTO.getPaymentId(), "O paymentId deve representar o ID Long do Mercado Pago");
    }

    @Test
    @DisplayName("Garante que a consulta de status pelo UUID do Pedido localiza o pedido no repositório")
    void deveConsultarStatusPorUuidDoPedido() {
        UUID pedidoUuid = UUID.randomUUID();
        Pedido pedidoExistente = new Pedido(new BigDecimal("250.00"), "Pneu Aro 17", "user@nxtdrive.com");
        pedidoExistente.setId(pedidoUuid);
        pedidoExistente.setStatus(StatusPedido.PAGO);
        pedidoExistente.setPaymentId(1122334455L);

        Mockito.when(pedidoRepository.findById(pedidoUuid)).thenReturn(Optional.of(pedidoExistente));

        // Como o paymentClient chamaria a API externa real sem credenciais de teste,
        // o service possui fallback gracioso que retorna o pedido persistido caso a API externa falhe.
        PagamentoResponseDTO resultado = pagamentoService.consultarStatusPorPedido(pedidoUuid);

        assertNotNull(resultado);
        assertEquals(pedidoUuid, resultado.getPedidoId());
        assertEquals(1122334455L, resultado.getPaymentId());
        assertTrue(resultado.getStatus().equalsIgnoreCase("approved") || resultado.getStatus().equalsIgnoreCase("PAGO"));
        Mockito.verify(pedidoRepository, Mockito.atLeastOnce()).findById(pedidoUuid);
    }

    @Test
    @DisplayName("Garante o mapeamento correto do enum StatusPedido a partir do retorno da API externa")
    void deveMapearStatusMercadoPagoCorretamente() {
        assertEquals(StatusPedido.PAGO, StatusPedido.fromMercadoPago("approved"));
        assertEquals(StatusPedido.REJEITADO, StatusPedido.fromMercadoPago("rejected"));
        assertEquals(StatusPedido.CANCELADO, StatusPedido.fromMercadoPago("cancelled"));
        assertEquals(StatusPedido.CANCELADO, StatusPedido.fromMercadoPago("refunded"));
        assertEquals(StatusPedido.CANCELADO, StatusPedido.fromMercadoPago("charged_back"));
        assertEquals(StatusPedido.EM_PROCESSAMENTO, StatusPedido.fromMercadoPago("in_process"));
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, StatusPedido.fromMercadoPago("pending"));
        assertEquals(StatusPedido.PENDENTE, StatusPedido.fromMercadoPago(null));
    }
}
