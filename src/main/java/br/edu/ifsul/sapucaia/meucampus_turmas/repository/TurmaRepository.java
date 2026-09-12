package br.edu.ifsul.sapucaia.meucampus_turmas.repository;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

interface TurmaRepository extends JpaRepository<Turma, Long> {
}
