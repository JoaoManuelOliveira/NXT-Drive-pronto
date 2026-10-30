package br.com.ifsp.edu.br.nxtdrive.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.ifsp.edu.br.nxtdrive.model.Produto;
import br.com.ifsp.edu.br.nxtdrive.service.CategoriaService;
import br.com.ifsp.edu.br.nxtdrive.service.ProdutoService;

@Controller
public class ProdutoController {

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private CategoriaService categoriaService;

	@GetMapping("/produtos")
	public String listarProdutos(Model model) {

		model.addAttribute("contexto", "produtos");
		model.addAttribute("categorias", produtoService.listarProdutos());
		model.addAttribute("listaCategoriasCompleta", categoriaService.listarCategorias());

		return "dashboard";
	}
	

	@PostMapping("/adicionarProduto")
	@PreAuthorize("hasRole('ADMIN')")
	public String adicionarProduto(@ModelAttribute Produto produto, @RequestParam("arquivoFoto") MultipartFile foto,
			Model model) throws IOException {

		if (!foto.isEmpty()) {
			produto.setFoto(foto.getBytes());
		}

		if (!produtoService.adicionarProduto(produto)) {

			model.addAttribute("erro", "Já existe um produto com esse nome.");
			model.addAttribute("categorias", produtoService.listarProdutos());
			model.addAttribute("contexto", "produtos");
			model.addAttribute("listaCategoriasCompleta", categoriaService.listarCategorias());

			return "dashboard";
		}

		return "redirect:/produtos";
	}

	@PostMapping("/editarProduto")
	@PreAuthorize("hasRole('ADMIN')")
	public String editarProduto(@ModelAttribute Produto produto, @RequestParam("arquivoFoto") MultipartFile foto)
			throws IOException {

		if (!foto.isEmpty()) {
			produto.setFoto(foto.getBytes());
		}

		produtoService.editarProduto(produto);

		return "redirect:/produtos";
	}

	@PostMapping("/removerProduto")
	@PreAuthorize("hasRole('ADMIN')")
	public String removerProduto(@ModelAttribute Produto produto) {

		produtoService.excluirProduto(produto.getIdProduto());

		return "redirect:/produtos";
	}
}