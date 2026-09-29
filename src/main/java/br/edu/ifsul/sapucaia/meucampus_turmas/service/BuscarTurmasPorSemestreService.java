package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.controller.response.BuscarTurmaResponse;
import br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarTurmasPorSemestreService {

    private final TurmaRepository turmaRepository;

    public List<BuscarTurmaResponse> buscar(String semestre) {

        return turmaRepository.findAllBySemestre(semestre)
                .stream()
                .map(TurmaMapper::toResponse)
                .toList();
    }
}
