package br.com.ifsp.edu.br.nxtdrive.model;

public enum StatusPedido {
	PENDENTE, AGUARDANDO_PAGAMENTO,
	PAGO, REJEITADO, CANCELADO, EM_PROCESSAMENTO;

	public static StatusPedido fromMercadoPago(String statusMp) {
		if (statusMp == null)
			return PENDENTE;
		return switch (statusMp.toLowerCase()) {
		case "approved", "pago" -> PAGO;
		case "in_process", "in_mediation" -> EM_PROCESSAMENTO;
		case "rejected" -> REJEITADO;
		case "cancelled", "refunded", "charged_back" -> CANCELADO;
		case "pending" -> AGUARDANDO_PAGAMENTO;
		default -> PENDENTE;
		};
	}
}