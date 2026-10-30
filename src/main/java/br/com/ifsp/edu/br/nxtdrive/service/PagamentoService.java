package br.com.ifsp.edu.br.nxtdrive.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentPointOfInteraction;
import com.mercadopago.resources.payment.PaymentTransactionData;

import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoCartaoRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoPixRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoResponseDTO;
import br.com.ifsp.edu.br.nxtdrive.model.Pedido;
import br.com.ifsp.edu.br.nxtdrive.model.StatusPedido;
import br.com.ifsp.edu.br.nxtdrive.repository.PedidoRepository;
import br.com.ifsp.edu.br.nxtdrive.repository.UsuarioRepository;

@Service
public class PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

    @Value("${mercadopago.webhook.url}")
    private String webhookUrl;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired(required = false)
    private UsuarioRepository usuarioRepository;

    private final PaymentClient paymentClient;

    public PagamentoService() {
        this.paymentClient = new PaymentClient();
    }

    /**
     * Processa a criação de um pagamento via Pix no Mercado Pago.
     * O pedido é identificado e manipulado internamente com UUID.
     * O UUID do pedido é enviado ao Mercado Pago como externalReference.
     */
    @Transactional
    public PagamentoResponseDTO processarPagamentoPix(PagamentoPixRequestDTO dto) {
        log.info("Processando pagamento Pix para e-mail: {}, valor: {}, pedidoId: {}",
                dto.getEmail(), dto.getValor(), dto.getPedidoId());

        try {
            Pedido pedido = obterOuCriarPedido(dto.getPedidoId(), dto.getValor(), dto.getDescricao(), dto.getEmail());
            pedido.setMetodoPagamento("pix");

            preencherDadosClienteEEndereco(
                    pedido, dto.getNomeCliente(), dto.getCpfCliente(), dto.getTelefoneCliente(),
                    dto.getItensDescricao(), dto.getEnderecoRua(), dto.getEnderecoNumero(),
                    dto.getEnderecoBairro(), dto.getEnderecoCidade(), dto.getEnderecoUf(),
                    dto.getEnderecoCep(), dto.getEnderecoComplemento()
            );

            PaymentCreateRequest paymentRequest = PaymentCreateRequest.builder()
                    .transactionAmount(dto.getValor())
                    .description(dto.getDescricao() != null ? dto.getDescricao() : "Pedido " + pedido.getId())
                    .paymentMethodId("pix")
                    .notificationUrl(webhookUrl)
                    .externalReference(pedido.getId().toString())
                    .payer(PaymentPayerRequest.builder()
                            .email(dto.getEmail())
                            .build())
                    .build();

            Payment payment = paymentClient.create(paymentRequest);
            log.info("Pagamento Pix criado no Mercado Pago. ID MP (Long): {}, Status: {}, Pedido UUID: {}",
                    payment.getId(), payment.getStatus(), pedido.getId());

            String qrCode = null;
            String qrCodeBase64 = null;

            PaymentPointOfInteraction poi = payment.getPointOfInteraction();
            if (poi != null && poi.getTransactionData() != null) {
                PaymentTransactionData transactionData = poi.getTransactionData();
                qrCode = transactionData.getQrCode();
                qrCodeBase64 = transactionData.getQrCodeBase64();
            }

            pedido.setPaymentId(payment.getId());
            pedido.setStatus(StatusPedido.fromMercadoPago(payment.getStatus()));
            pedido.setQrCode(qrCode);
            pedido.setQrCodeBase64(qrCodeBase64);
            pedidoRepository.save(pedido);

            return new PagamentoResponseDTO(
                    payment.getId(),
                    pedido.getId(),
                    payment.getStatus(),
                    payment.getStatusDetail(),
                    qrCode,
                    qrCodeBase64,
                    "Pagamento Pix gerado com sucesso. Aguardando confirmação.",
                    payment.getTransactionAmount()
            );

        } catch (MPApiException apiEx) {
            log.error("Erro da API Mercado Pago ao criar Pix: Status {}, Mensagem: {}",
                    apiEx.getStatusCode(), apiEx.getApiResponse() != null ? apiEx.getApiResponse().getContent() : "", apiEx);
            throw new RuntimeException("Erro ao comunicar com Mercado Pago: " + apiEx.getMessage(), apiEx);
        } catch (MPException mpEx) {
            log.error("Erro SDK Mercado Pago ao criar Pix: {}", mpEx.getMessage(), mpEx);
            throw new RuntimeException("Erro ao processar pagamento Pix: " + mpEx.getMessage(), mpEx);
        }
    }

    /**
     * Processa o pagamento via Cartão de Crédito utilizando token pré-gerado de forma segura no frontend.
     */
    @Transactional
    public PagamentoResponseDTO processarPagamentoCartao(PagamentoCartaoRequestDTO dto) {
        log.info("Processando pagamento Cartão de Crédito para e-mail: {}, parcelas: {}, valor: {}, pedidoId: {}",
                dto.getEmail(), dto.getParcelas(), dto.getValor(), dto.getPedidoId());

        try {
            Pedido pedido = obterOuCriarPedido(dto.getPedidoId(), dto.getValor(), dto.getDescricao(), dto.getEmail());
            pedido.setMetodoPagamento(dto.getMetodoPagamento() != null ? dto.getMetodoPagamento() : "credit_card");

            preencherDadosClienteEEndereco(
                    pedido, dto.getNomeCliente(), dto.getCpfCliente(), dto.getTelefoneCliente(),
                    dto.getItensDescricao(), dto.getEnderecoRua(), dto.getEnderecoNumero(),
                    dto.getEnderecoBairro(), dto.getEnderecoCidade(), dto.getEnderecoUf(),
                    dto.getEnderecoCep(), dto.getEnderecoComplemento()
            );

            PaymentCreateRequest.PaymentCreateRequestBuilder requestBuilder = PaymentCreateRequest.builder()
                    .transactionAmount(dto.getValor())
                    .token(dto.getToken())
                    .description(dto.getDescricao() != null ? dto.getDescricao() : "Pedido " + pedido.getId())
                    .installments(dto.getParcelas() != null ? dto.getParcelas() : 1)
                    .notificationUrl(webhookUrl)
                    .externalReference(pedido.getId().toString())
                    .payer(PaymentPayerRequest.builder()
                            .email(dto.getEmail())
                            .build());

            if (dto.getMetodoPagamento() != null && !dto.getMetodoPagamento().isBlank()) {
                requestBuilder.paymentMethodId(dto.getMetodoPagamento());
            }

            Payment payment = paymentClient.create(requestBuilder.build());
            log.info("Pagamento Cartão processado no Mercado Pago. ID MP: {}, Status: {}, Pedido UUID: {}",
                    payment.getId(), payment.getStatus(), pedido.getId());

            pedido.setPaymentId(payment.getId());
            pedido.setStatus(StatusPedido.fromMercadoPago(payment.getStatus()));
            pedidoRepository.save(pedido);

            String mensagem = "approved".equalsIgnoreCase(payment.getStatus())
                    ? "Pagamento com cartão aprovado com sucesso!"
                    : "Pagamento com cartão processado com status: " + payment.getStatus();

            return new PagamentoResponseDTO(
                    payment.getId(),
                    pedido.getId(),
                    payment.getStatus(),
                    payment.getStatusDetail(),
                    null,
                    null,
                    mensagem,
                    payment.getTransactionAmount()
            );

        } catch (MPApiException apiEx) {
            log.error("Erro da API Mercado Pago ao processar cartão: Status {}, Mensagem: {}",
                    apiEx.getStatusCode(), apiEx.getApiResponse() != null ? apiEx.getApiResponse().getContent() : "", apiEx);
            throw new RuntimeException("Erro ao comunicar com Mercado Pago: " + apiEx.getMessage(), apiEx);
        } catch (MPException mpEx) {
            log.error("Erro SDK Mercado Pago ao processar cartão: {}", mpEx.getMessage(), mpEx);
            throw new RuntimeException("Erro ao processar pagamento Cartão: " + mpEx.getMessage(), mpEx);
        }
    }

    /**
     * Consulta o status do pagamento diretamente pelo UUID do Pedido.
     * Se o pedido tiver um paymentId associado, consulta o estado mais recente no Mercado Pago e atualiza o pedido.
     */
    @Transactional
    public PagamentoResponseDTO consultarStatusPorPedido(UUID pedidoId) {
        log.info("Consultando status do pedido por UUID: {}", pedidoId);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com o ID: " + pedidoId));

        Long paymentId = pedido.getPaymentId();
        if (paymentId != null) {
            try {
                Payment payment = paymentClient.get(paymentId);
                if (payment != null) {
                    pedido.setStatus(StatusPedido.fromMercadoPago(payment.getStatus()));
                    pedidoRepository.save(pedido);

                    String qrCode = null;
                    String qrCodeBase64 = null;
                    if (payment.getPointOfInteraction() != null && payment.getPointOfInteraction().getTransactionData() != null) {
                        qrCode = payment.getPointOfInteraction().getTransactionData().getQrCode();
                        qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
                    }

                    return new PagamentoResponseDTO(
                            payment.getId(),
                            pedido.getId(),
                            payment.getStatus(),
                            payment.getStatusDetail(),
                            qrCode,
                            qrCodeBase64,
                            "Status consultado com sucesso: " + payment.getStatus(),
                            payment.getTransactionAmount()
                    );
                }
            } catch (Exception e) {
                log.warn("Não foi possível atualizar dados com o Mercado Pago para o paymentId {}: {}", paymentId, e.getMessage());
            }
        }

        // Retorna o status atual persistido do pedido caso ainda não haja paymentId ou MP esteja indisponível
        return new PagamentoResponseDTO(
                pedido.getPaymentId(),
                pedido.getId(),
                pedido.getStatus().name(),
                null,
                pedido.getQrCode(),
                pedido.getQrCodeBase64(),
                "Status atual do pedido: " + pedido.getStatus(),
                pedido.getValorTotal()
        );
    }

    /**
     * Consulta o estado do pagamento na API do Mercado Pago a partir do paymentId numérico (exigido pelo SDK)
     * e atualiza o status do pedido correspondente (localizado via externalReference UUID ou paymentId).
     */
    @Transactional
    public PagamentoResponseDTO consultarEAtualizarStatusPorPaymentId(Long paymentId) {
        log.info("Consultando status no Mercado Pago pelo ID da transação (Long): {}", paymentId);

        try {
            Payment payment = paymentClient.get(paymentId);
            if (payment == null) {
                log.warn("Pagamento não encontrado na API do Mercado Pago para ID: {}", paymentId);
                throw new RuntimeException("Pagamento não localizado no Mercado Pago: " + paymentId);
            }

            log.info("Pagamento recuperado da API MP: ID: {}, Status: {}, ExternalReference: {}",
                    payment.getId(), payment.getStatus(), payment.getExternalReference());

            StatusPedido novoStatus = StatusPedido.fromMercadoPago(payment.getStatus());

            // 1. Tenta encontrar o pedido pelo paymentId do Mercado Pago
            Optional<Pedido> pedidoOpt = pedidoRepository.findByPaymentId(paymentId);

            // 2. Se não encontrou por paymentId, localiza pelo externalReference (que contém o UUID do Pedido)
            if (pedidoOpt.isEmpty() && payment.getExternalReference() != null && !payment.getExternalReference().isBlank()) {
                try {
                    UUID pedidoUuid = UUID.fromString(payment.getExternalReference());
                    pedidoOpt = pedidoRepository.findById(pedidoUuid);
                } catch (IllegalArgumentException e) {
                    log.warn("ExternalReference não é um UUID válido: {}", payment.getExternalReference());
                }
            }

            UUID pedidoIdRetorno = null;
            if (pedidoOpt.isPresent()) {
                Pedido pedido = pedidoOpt.get();
                pedido.setPaymentId(payment.getId());
                pedido.setStatus(novoStatus);
                pedidoRepository.save(pedido);
                pedidoIdRetorno = pedido.getId();
                log.info("Pedido UUID {} atualizado com sucesso para o status: {}", pedido.getId(), novoStatus);
            } else {
                log.warn("Nenhum pedido associado encontrado no banco para o paymentId: {}", paymentId);
            }

            String qrCode = null;
            String qrCodeBase64 = null;
            if (payment.getPointOfInteraction() != null && payment.getPointOfInteraction().getTransactionData() != null) {
                qrCode = payment.getPointOfInteraction().getTransactionData().getQrCode();
                qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
            }

            return new PagamentoResponseDTO(
                    payment.getId(),
                    pedidoIdRetorno,
                    payment.getStatus(),
                    payment.getStatusDetail(),
                    qrCode,
                    qrCodeBase64,
                    "Status consultado com sucesso: " + payment.getStatus(),
                    payment.getTransactionAmount()
            );

        } catch (MPApiException apiEx) {
            log.error("Erro da API Mercado Pago ao consultar pagamento {}: Status {}, Mensagem: {}",
                    paymentId, apiEx.getStatusCode(), apiEx.getApiResponse() != null ? apiEx.getApiResponse().getContent() : "", apiEx);
            throw new RuntimeException("Erro da API Mercado Pago: " + apiEx.getMessage(), apiEx);
        } catch (MPException mpEx) {
            log.error("Erro SDK Mercado Pago ao consultar pagamento {}: {}", paymentId, mpEx.getMessage(), mpEx);
            throw new RuntimeException("Erro ao consultar pagamento no Mercado Pago: " + mpEx.getMessage(), mpEx);
        }
    }

    private Pedido obterOuCriarPedido(UUID pedidoId, BigDecimal valor, String descricao, String email) {
        if (pedidoId != null) {
            Optional<Pedido> existente = pedidoRepository.findById(pedidoId);
            if (existente.isPresent()) {
                Pedido p = existente.get();
                if (valor != null) p.setValorTotal(valor);
                if (descricao != null) p.setDescricao(descricao);
                if (email != null) p.setEmailPagador(email);
                return p;
            }
        }
        Pedido novoPedido = new Pedido(valor, descricao, email);
        return pedidoRepository.save(novoPedido);
    }

    private void preencherDadosClienteEEndereco(
            Pedido pedido, String nome, String cpf, String tel, String itens,
            String rua, String num, String bairro, String cidade, String uf, String cep, String comp) {

        if (nome != null && !nome.isBlank()) {
            pedido.setNomeCliente(nome.trim());
        } else if (pedido.getNomeCliente() == null && pedido.getEmailPagador() != null && usuarioRepository != null) {
            try {
                usuarioRepository.findByEmail(pedido.getEmailPagador()).ifPresent(u -> pedido.setNomeCliente(u.getNome()));
            } catch (Exception e) {
                log.warn("Não foi possível buscar nome do usuário por e-mail: {}", e.getMessage());
            }
        }

        if (cpf != null && !cpf.isBlank()) pedido.setCpfCliente(cpf.trim());
        if (tel != null && !tel.isBlank()) pedido.setTelefoneCliente(tel.trim());
        if (itens != null && !itens.isBlank()) pedido.setItensDescricao(itens.trim());

        // Se algum dado do endereço não foi enviado no payload, busca a foto do endereço do usuário logado
        if ((rua == null || rua.isBlank() || num == null || num.isBlank() || cep == null || cep.isBlank())
                && pedido.getEmailPagador() != null && usuarioRepository != null) {
            try {
                var usuarioOpt = usuarioRepository.findByEmail(pedido.getEmailPagador());
                if (usuarioOpt.isPresent() && usuarioOpt.get().getEndereco() != null) {
                    var end = usuarioOpt.get().getEndereco();
                    if (rua == null || rua.isBlank()) rua = end.getLogradouro();
                    if (num == null || num.isBlank()) num = end.getNumero();
                    if (bairro == null || bairro.isBlank()) bairro = end.getBairro();
                    if (cidade == null || cidade.isBlank()) cidade = end.getLocalidade();
                    if (uf == null || uf.isBlank()) uf = end.getUf();
                    if (cep == null || cep.isBlank()) cep = end.getCep();
                    if (comp == null || comp.isBlank()) comp = end.getComplemento();
                }
            } catch (Exception e) {
                log.warn("Não foi possível recuperar endereço cadastrado do perfil: {}", e.getMessage());
            }
        }

        // Validação estrita: não permite pedido sem endereço real cadastrado
        if (cep == null || cep.isBlank() || rua == null || rua.isBlank() || num == null || num.isBlank()) {
            throw new IllegalArgumentException("Endereço de entrega obrigatório: por favor, informe o seu endereço completo (CEP, Rua, Número, Bairro, Cidade e UF) antes de realizar a compra.");
        }

        pedido.setEnderecoRua(rua.trim());
        pedido.setEnderecoNumero(num.trim());
        pedido.setEnderecoBairro(bairro != null ? bairro.trim() : "");
        pedido.setEnderecoCidade(cidade != null ? cidade.trim() : "");
        pedido.setEnderecoUf(uf != null ? uf.trim().toUpperCase() : "");
        pedido.setEnderecoCep(cep.trim());
        if (comp != null && !comp.isBlank()) pedido.setEnderecoComplemento(comp.trim());
    }
}