package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.controller.response.BuscarTurmaResponse;
import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.validator.ValidaIdTurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper.toResponse;

@Service
@RequiredArgsConstructor
public class BuscarTurmaPorIdService {

    private final TurmaRepository turmaRepository;

    private final ValidaIdTurmaService validaIdTurmaService;

    public BuscarTurmaResponse buscar(Long id) {

        validaIdTurmaService.validar(id);

        Turma turma = turmaRepository.findById(id).get();

        return toResponse(turma);
    }
}
