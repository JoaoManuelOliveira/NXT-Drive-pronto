package br.com.ifsp.edu.br.nxtdrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifsp.edu.br.nxtdrive.service.EmailService;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

	@Autowired
	private EmailService emailService;

	@PostMapping("/inscrever")
	public ResponseEntity<String> inscreverNewsletter(@RequestParam("email") String email) {
		try {
			emailService.enviarCupomBoasVindas(email);
			return ResponseEntity.ok("Cupom enviado com sucesso!");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Erro ao enviar o e-mail: " + e.getMessage());
		}
	}
}
