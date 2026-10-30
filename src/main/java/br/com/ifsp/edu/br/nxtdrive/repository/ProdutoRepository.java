package br.com.ifsp.edu.br.nxtdrive.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifsp.edu.br.nxtdrive.model.Categoria;
import br.com.ifsp.edu.br.nxtdrive.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

	List<Produto> findByNomeContaining(String nome);

	List<Produto> findByNomeIgnoreCase(String nome);

	List<Produto> findByNomeStartingWith(String nome);

	Categoria findByNome(String nome); // para fazer querys

}
