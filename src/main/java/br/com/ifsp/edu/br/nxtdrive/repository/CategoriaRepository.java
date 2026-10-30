package br.com.ifsp.edu.br.nxtdrive.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifsp.edu.br.nxtdrive.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
	List<Categoria> findByNomeContaining(String nome);

	List<Categoria> findByNomeIgnoreCase(String nome);

	List<Categoria> findByNomeStartingWith(String nome);

	Categoria findByNome(String nome); // para fazer querys

}
