package br.com.ifsp.edu.br.nxtdrive.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.com.ifsp.edu.br.nxtdrive.model.Endereco;

@RestController
@RequestMapping("/api/cep")
public class CepController {

	@GetMapping("/{cep}")
	public Endereco buscarCep(@PathVariable String cep) {
		RestTemplate rest = new RestTemplate();
		String url = "https://viacep.com.br/ws/" + cep + "/json/";
		return rest.getForObject(url, Endereco.class);
	}
}