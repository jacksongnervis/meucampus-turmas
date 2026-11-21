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

        if (professorId != null){

            if(semestre != null && !semestre.isEmpty()){
                response = turmaRepository.findAllByProfessorIdAndSemestre(professorId, semestre, pageable);
            }

            else{
                response = turmaRepository.findAllByProfessorId(professorId, pageable);
            }
        }

        else if(semestre != null && !semestre.isEmpty()){
            response = turmaRepository.findAllBySemestre(semestre, pageable);
        }

        else{
            response = turmaRepository.findAll(pageable);
        }

        return response.map(TurmaMapper::toResponseDTO);
    }
}
