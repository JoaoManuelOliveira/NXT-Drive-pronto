package br.com.ifsp.edu.br.nxtdrive.controller;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifsp.edu.br.nxtdrive.DTO.MercadoPagoWebhookDTO;
import br.com.ifsp.edu.br.nxtdrive.service.PagamentoService;

@RestController
@RequestMapping("/api/v1/webhooks")
@CrossOrigin(origins = "*")
public class WebhookController {

	private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

	@Autowired
	private PagamentoService pagamentoService;

	@PostMapping("/mercadopago")
	public ResponseEntity<Map<String, String>> receberNotificacaoMercadoPago(
			@RequestBody(required = false) MercadoPagoWebhookDTO payload,
			@RequestParam(value = "id", required = false) String paramId,
			@RequestParam(value = "topic", required = false) String topic,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "data.id", required = false) String dataIdParam) {

		log.info("Webhook recebido. ID: {}, Topic: {}, Type: {}, DataId: {}", paramId, topic, type, dataIdParam);

		Long paymentId = null;

		// Extrai o ID do pagamento do corpo JSON.
		if (payload != null) {
			String eventType = payload.getType();

			if (eventType == null && payload.getAction() != null && payload.getAction().startsWith("payment")) {
				eventType = "payment";
			}

			if (eventType == null || "payment".equalsIgnoreCase(eventType)) {

				if (payload.getData() != null && payload.getData().getId() != null) {

					try {
						paymentId = Long.valueOf(payload.getData().getId());
					} catch (NumberFormatException e) {
						log.warn("ID inválido no webhook: {}", payload.getData().getId());
					}

				} else if (payload.getId() != null) {
					paymentId = payload.getId();
				}
			}
		}

		// Caso não exista ID no corpo, verifica os parâmetros da URL.
		if (paymentId == null) {
			String rawId = paramId != null ? paramId : dataIdParam;
			String rawType = type != null ? type : topic;

			if (rawId != null && (rawType == null || "payment".equalsIgnoreCase(rawType))) {

				try {
					paymentId = Long.valueOf(rawId);
				} catch (NumberFormatException e) {
					log.warn("ID inválido recebido via URL: {}", rawId);
				}
			}
		}

		// Consulta o pagamento no Mercado Pago.
		if (paymentId != null) {
			try {
				log.info("Atualizando pagamento Mercado Pago: {}", paymentId);

				pagamentoService.consultarEAtualizarStatusPorPaymentId(paymentId);

			} catch (Exception e) {
				log.error("Erro ao processar webhook do pagamento {}: {}", paymentId, e.getMessage(), e);

				return ResponseEntity.ok(Collections.singletonMap("status", "error_processing_validation"));
			}
		} else {
			log.info("Webhook ignorado: nenhum ID de pagamento válido.");
		}

		return ResponseEntity.ok(Collections.singletonMap("status", "ok"));
	}
}