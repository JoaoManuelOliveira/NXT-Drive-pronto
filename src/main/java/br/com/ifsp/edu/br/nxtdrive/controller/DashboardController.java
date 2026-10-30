package br.com.ifsp.edu.br.nxtdrive.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.ifsp.edu.br.nxtdrive.model.Categoria;
import br.com.ifsp.edu.br.nxtdrive.model.Produto;
import br.com.ifsp.edu.br.nxtdrive.model.Usuario;
import br.com.ifsp.edu.br.nxtdrive.repository.UsuarioRepository;
import br.com.ifsp.edu.br.nxtdrive.service.CategoriaService;
import br.com.ifsp.edu.br.nxtdrive.service.ProdutoService;

@Controller
public class DashboardController {

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private br.com.ifsp.edu.br.nxtdrive.service.PedidoService pedidoService;

	@Autowired
	private br.com.ifsp.edu.br.nxtdrive.service.EstatisticasAdminService estatisticasAdminService;

	// =========================
	// PAINEL ADMINISTRATIVO
	// =========================

	@GetMapping({"/dashboard", "/admin", "/admin/dashboard", "/estatisticas"})
	@PreAuthorize("hasRole('ADMIN')")
	public String getDashEstatisticas(Model model) {
		model.addAttribute("contexto", "estatisticas");
		model.addAttribute("estatisticas", estatisticasAdminService.gerarEstatisticas("ULTIMOS_30_DIAS", null, null));
		return "dashboard";
	}

	@GetMapping({"/categorias", "/listarCategorias"})
	@PreAuthorize("hasRole('ADMIN')")
	public String listarCategorias(Model model) {
		model.addAttribute("contexto", "categorias");
		model.addAttribute("categorias", categoriaService.listarCategorias());
		model.addAttribute("produtos", produtoService.listarProdutos());
		return "dashboard";
	}

	@GetMapping("/pedidos")
	@PreAuthorize("hasRole('ADMIN')")
	public String listarPedidos(Model model) {
		model.addAttribute("contexto", "pedidos");
		model.addAttribute("pedidos", pedidoService.listarTodosPedidosAdmin());
		return "dashboard";
	}

	// =========================
	// HOME DA LOJA
	// =========================

	@GetMapping("/home")
	public String home(Model model, java.security.Principal principal) {
		boolean isAdmin = false;

		if (principal != null) {
			// Busca o usuário logado pelo e-mail e passa o nome, e-mail, role e endereço salvo
			var usuarioOpt = usuarioRepository.findByEmail(principal.getName());
			if (usuarioOpt.isPresent()) {
				Usuario usuario = usuarioOpt.get();
				model.addAttribute("nomeUsuario", usuario.getNome());
				model.addAttribute("emailUsuario", usuario.getEmail());
				isAdmin = usuario.getAcesso() == Usuario.Role.ADMIN;

				if (usuario.getEndereco() != null) {
					var end = usuario.getEndereco();
					model.addAttribute("cepUsuario", end.getCep());
					model.addAttribute("logradouroUsuario", end.getLogradouro());
					model.addAttribute("numeroUsuario", end.getNumero());
					model.addAttribute("bairroUsuario", end.getBairro());
					model.addAttribute("localidadeUsuario", end.getLocalidade());
					model.addAttribute("ufUsuario", end.getUf());
					model.addAttribute("complementoUsuario", end.getComplemento());
					model.addAttribute("usuarioTemEndereco", end.getCep() != null && !end.getCep().isBlank());
				} else {
					model.addAttribute("usuarioTemEndereco", false);
				}
			}
		}

		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("produtos", produtoService.listarProdutos());
		model.addAttribute("listaCategorias", categoriaService.listarCategorias());

		return "home";
	}

	// =========================
	// IMAGEM DO PRODUTO
	// =========================

	@GetMapping("/produto/imagem/{id}")
	@ResponseBody
	public byte[] imagemProduto(@PathVariable UUID id) {

		Produto produto = produtoService.buscarPorId(id);

		return produto.getFoto();
	}

	// =========================
	// ACESSO NEGADO
	// =========================

	@GetMapping("/403")
	public String acessoNegado() {
		return "403";
	}

	// =========================
	// CATEGORIAS
	// =========================

	@PostMapping("/adicionarCategoria")
	@PreAuthorize("hasRole('ADMIN')")
	public String adicionarCategoria(@ModelAttribute Categoria categoria, Model model) {

		if (!categoriaService.adicionarCategoria(categoria)) {

			model.addAttribute("erro", "Já existe uma categoria com esse nome.");
			model.addAttribute("contexto", "categorias");
			model.addAttribute("categorias", categoriaService.listarCategorias());
			model.addAttribute("produtos", produtoService.listarProdutos());

			return "dashboard";
		}

		return "redirect:/categorias";
	}

	@PostMapping("/editarCategoria")
	@PreAuthorize("hasRole('ADMIN')")
	public String editarCategoria(@ModelAttribute Categoria categoria) {

		categoriaService.editarCategoria(categoria);

		return "redirect:/categorias";
	}

	@PostMapping("/removerCategoria")
	@PreAuthorize("hasRole('ADMIN')")
	public String removerCategoria(@ModelAttribute Categoria categoria) {

		categoriaService.excluirCategoria(categoria.getIdCategoria());

		return "redirect:/categorias";
	}

	@GetMapping("/usuarios")
	@PreAuthorize("hasRole('ADMIN')")
	public String listarUsuarios(Model model) {
		model.addAttribute("contexto", "usuarios");
		model.addAttribute("usuarios", usuarioRepository.findAll());
		return "dashboard";
	}

	@PostMapping("/usuarios/excluir/{id}")
	public String excluirUsuario(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		try {
			usuarioRepository.deleteById(id);
			redirectAttributes.addFlashAttribute("sucesso", "Usuário removido do sistema!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", "Não foi possível excluir o usuário.");
		}
		return "redirect:/usuarios";
	}

	@PostMapping("/usuarios/alterar-role/{id}")
	public String alterarRole(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if (usuario != null) {
			// Alterna entre ADMIN e USER
			if (usuario.getAcesso() == Usuario.Role.ADMIN) {
				usuario.setAcesso(Usuario.Role.USER);
			} else {
				usuario.setAcesso(Usuario.Role.ADMIN);
			}
			usuarioRepository.save(usuario);
			redirectAttributes.addFlashAttribute("sucesso", "Permissão atualizada com sucesso!");
		}
		return "redirect:/usuarios";
	}

}