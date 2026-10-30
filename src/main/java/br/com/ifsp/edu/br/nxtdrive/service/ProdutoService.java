package br.com.ifsp.edu.br.nxtdrive.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifsp.edu.br.nxtdrive.model.Produto;
import br.com.ifsp.edu.br.nxtdrive.repository.ProdutoRepository;

@Service
public class ProdutoService {

	@Autowired
	public ProdutoRepository produtoRepository;

	public boolean adicionarProduto(Produto produto) {

		List<Produto> produtos = produtoRepository.findByNomeIgnoreCase(produto.getNome());

		if (!produtos.isEmpty()) {
			return false;
		}

		produto.setCodigoProduto(UUID.randomUUID());
		produtoRepository.save(produto);

		return true;
	}

	public List<Produto> listarProdutos() {
		return produtoRepository.findAll();
	}

	public boolean editarProduto(Produto produto) {

		Optional<Produto> produtoOptional = produtoRepository.findById(produto.getIdProduto());

		if (produtoOptional.isPresent()) {

			Produto novoProduto = produtoOptional.get();

			novoProduto.setNome(produto.getNome());
			novoProduto.setDescricao(produto.getDescricao());
			novoProduto.setValor(produto.getValor());
			novoProduto.setCategoria(produto.getCategoria());

			// Só altera a foto se uma nova foi enviada
			if (produto.getFoto() != null && produto.getFoto().length > 0) {
				novoProduto.setFoto(produto.getFoto());
			}

			produtoRepository.save(novoProduto);

			return true;
		}

		return false;
	}
	
	public Produto buscarPorId(UUID id) {
		return produtoRepository.findById(id).orElse(null);
	}

	public void excluirProduto(UUID idProduto) {
		produtoRepository.deleteById(idProduto);
		;
	}

}
