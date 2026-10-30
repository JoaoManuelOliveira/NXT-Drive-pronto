package br.com.ifsp.edu.br.nxtdrive.controller;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoCartaoRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoPixRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoResponseDTO;
import br.com.ifsp.edu.br.nxtdrive.service.PagamentoService;

@RestController
@RequestMapping("/api/v1/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Value("${mercadopago.public.key}")
    private String publicKey;

    @PostMapping("/pix")
    public ResponseEntity<PagamentoResponseDTO> criarPagamentoPix(@RequestBody PagamentoPixRequestDTO dto) {
        PagamentoResponseDTO response = pagamentoService.processarPagamentoPix(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/cartao")
    public ResponseEntity<PagamentoResponseDTO> criarPagamentoCartao(@RequestBody PagamentoCartaoRequestDTO dto) {
        PagamentoResponseDTO response = pagamentoService.processarPagamentoCartao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Consulta o status do pagamento através do UUID do Pedido.
     */
    @GetMapping("/status/{pedidoId}")
    public ResponseEntity<PagamentoResponseDTO> consultarStatus(@PathVariable UUID pedidoId) {
        PagamentoResponseDTO response = pagamentoService.consultarStatusPorPedido(pedidoId);
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta o status do pagamento especificamente pelo UUID do Pedido.
     */
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagamentoResponseDTO> consultarPorPedido(@PathVariable UUID pedidoId) {
        PagamentoResponseDTO response = pagamentoService.consultarStatusPorPedido(pedidoId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de suporte para consulta pelo ID numérico da transação do Mercado Pago (Long).
     */
    @GetMapping("/transacao/{paymentId}")
    public ResponseEntity<PagamentoResponseDTO> consultarPorTransacaoMercadoPago(@PathVariable Long paymentId) {
        PagamentoResponseDTO response = pagamentoService.consultarEAtualizarStatusPorPaymentId(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> obterPublicKey() {
        return ResponseEntity.ok(Collections.singletonMap("publicKey", publicKey));
    }
}