package br.com.ifsp.edu.br.nxtdrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.ifsp.edu.br.nxtdrive.model.Usuario;
import br.com.ifsp.edu.br.nxtdrive.repository.UsuarioRepository;

@Controller
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping({ "/" })
	public String login() {
		return "index";
	}

	@GetMapping("/cadastro")
	public String cadastro() {
		return "index";
	}

	@PostMapping("/cadastro")
	public String realizarCadastro(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
		try {

			if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
				redirectAttributes.addFlashAttribute("erro", "Este e-mail já está em uso!");
				return "redirect:/cadastro";
			}

			// Define a permissão padrão do usuario
			if (usuario.getAcesso() == null) {
				usuario.setAcesso(Usuario.Role.USER);
			}

			// Criptografa a senha e salva
			usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
			usuarioRepository.save(usuario);

			redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso! Faça login.");
			return "redirect:/login";

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("erro", "Ocorreu um erro ao realizar o cadastro.");
			return "redirect:/cadastro";
		}
	}

}