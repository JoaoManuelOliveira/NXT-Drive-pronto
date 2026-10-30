package br.com.ifsp.edu.br.nxtdrive.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class MercadoPagoConfig {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoConfig.class);

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.public.key}")
    private String publicKey;

    @Value("${mercadopago.webhook.url}")
    private String webhookUrl;

    @PostConstruct
    public void init() {
        log.info("Inicializando Mercado Pago Java SDK...");
        com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
        log.info("Mercado Pago Java SDK inicializado com sucesso.");
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }
}
