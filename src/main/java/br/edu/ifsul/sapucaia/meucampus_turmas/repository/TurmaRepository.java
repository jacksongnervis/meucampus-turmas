package br.edu.ifsul.sapucaia.meucampus_turmas.repository;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.LocalTime;
import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @NativeQuery("""
            SELECT EXISTS (
             SELECT 1 FROM turma t
             WHERE t.sala = :sala
               AND t.semestre = :semestre
               AND t.dias_semana && CAST(:diasSemana AS smallint[])
               AND t.horario_inicial < :horarioFinal
               AND t.horario_final   > :horarioInicial
               AND t.id <> :idIgnorado)
            """)
    boolean existsConflitoPorHorario(String sala, String semestre, String diasSemana, LocalTime horarioInicial, LocalTime horarioFinal, Long idIgnorado);

    Page<Turma> findAllByProfessorIdAndSemestre(Long professorId, String semestre, Pageable pageable);

    Page<Turma> findAllByProfessorId(Long professorId, Pageable pageable);

    Page<Turma> findAllBySemestre(String semestre, Pageable pageable);
}
