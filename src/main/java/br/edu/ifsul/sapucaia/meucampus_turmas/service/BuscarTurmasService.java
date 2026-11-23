package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarTurmasService {

    private final TurmaRepository turmaRepository;

    public Page<TurmaResponseDTO> buscar(Long professorId, String semestre, Pageable pageable) {

        Page<Turma> response;
        boolean temSemestre = semestre != null && !semestre.isBlank();

        if (professorId != null) {
            if (temSemestre) {
                response = turmaRepository.findAllByProfessorIdAndSemestre(professorId, semestre.trim(), pageable);
            } else {
                response = turmaRepository.findAllByProfessorId(professorId, pageable);
            }
        } else if (temSemestre) {
            response = turmaRepository.findAllBySemestre(semestre.trim(), pageable);
        } else {
            response = turmaRepository.findAll(pageable);
        }

        return response.map(TurmaMapper::toResponseDTO);
    }
}
