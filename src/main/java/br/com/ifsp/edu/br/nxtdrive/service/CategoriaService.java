package br.com.ifsp.edu.br.nxtdrive.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.ifsp.edu.br.nxtdrive.model.Categoria;
import br.com.ifsp.edu.br.nxtdrive.repository.CategoriaRepository;

@Service
public class CategoriaService {

	@Autowired
	public CategoriaRepository categoriaRepository;
	
	public boolean adicionarCategoria(Categoria categoria) {

	    List<Categoria> categorias = categoriaRepository.findByNomeIgnoreCase(categoria.getNome());

	    if (!categorias.isEmpty()) {
	        return false;
	    }

	    categoriaRepository.save(categoria);

	    return true;
	}
		

	public List<Categoria> listarCategoriaEspecifica(String nome) {
		return categoriaRepository.findByNomeIgnoreCase(nome);
	}

	public List<Categoria> listarCategorias() {
		return categoriaRepository.findAll();
	}

	
	public boolean editarCategoria(Categoria categoria) {

        Optional<Categoria> categoriaOptional = categoriaRepository.findById(categoria.getIdCategoria());

        if (categoriaOptional.isPresent()) {

            Categoria categoriaEditar = categoriaOptional.get();

            categoriaEditar.setNome(categoria.getNome());

            categoriaRepository.save(categoriaEditar);

            return true;
        }

        return false;
    }

	public void excluirCategoria(UUID idCategoria) {
		categoriaRepository.deleteById(idCategoria);
	}
}