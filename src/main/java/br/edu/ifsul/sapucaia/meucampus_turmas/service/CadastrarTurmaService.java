package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.validator.ValidaCodigoTurmaService;
import br.edu.ifsul.sapucaia.meucampus_turmas.validation.ValidaHorariosTurmaValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper.toEntity;
import static br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper.toResponseDTO;

@Service
@RequiredArgsConstructor
public class CadastrarTurmaService {

    private final TurmaRepository turmaRepository;
    private final ValidaCodigoTurmaService validaCodigoTurmaService;
    private final ValidaHorariosTurmaValidator validaHorarioTurmaService;

    @Transactional
    public TurmaResponseDTO cadastrar(TurmaRequestDTO dto) {

        validaCodigoTurmaService.validaSeExiste(dto.getCodigo().trim());

        validaHorarioTurmaService.validarHorarioFinalPosteriorHorarioInicial(dto.getHorarioInicial(), dto.getHorarioFinal());

        Turma turma = toEntity(dto);
        Turma salva = turmaRepository.save(turma);

        return toResponseDTO(salva);
    }
}
