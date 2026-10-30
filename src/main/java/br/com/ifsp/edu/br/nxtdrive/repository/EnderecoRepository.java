package br.com.ifsp.edu.br.nxtdrive.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ifsp.edu.br.nxtdrive.model.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
}
