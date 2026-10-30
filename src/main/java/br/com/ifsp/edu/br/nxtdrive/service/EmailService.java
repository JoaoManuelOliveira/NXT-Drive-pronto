package br.com.ifsp.edu.br.nxtdrive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void enviarCupomBoasVindas(String emailDestino) {
		SimpleMailMessage mensagem = new SimpleMailMessage();

		mensagem.setFrom("seu-email@gmail.com");
		mensagem.setTo(emailDestino);
		mensagem.setSubject("🔥 O PATRÃO ENLOUQUECEU! Seu cupom de 67% OFF chegou na NXT Drive!");

		mensagem.setText("Fala, NXTDRIVEZEIRO!\n\n" + "Seja muito bem-vindo ao Clube NXT Drive!\n\n"
				+ "O patrão enlouqueceu de vez e liberou um desconto insano de 67% OFF para a sua primeira compra no nosso site.\n\n"
				+ "🎫 Seu cupom exclusivo: NXTDRIVEZEIRO\n\n"
				+ "Acesse a loja agora, escolha suas peças de alta performance e garanta essa condição especial antes que ele mude de ideia!\n\n"
				+ "Acelere junto com a gente,\n" + "Equipe NXT Drive");

		mailSender.send(mensagem);
	}
}