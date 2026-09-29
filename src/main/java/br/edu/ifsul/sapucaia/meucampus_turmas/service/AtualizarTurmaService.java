package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.AtualizarTurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.validator.ValidaCodigoTurmaService;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.validator.ValidaIdTurmaService;
import br.edu.ifsul.sapucaia.meucampus_turmas.validation.ValidaHorariosTurmaValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

import static br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper.toResponseDTO;

@Service
@RequiredArgsConstructor
public class AtualizarTurmaService {

    private final TurmaRepository turmaRepository;
    private final ValidaIdTurmaService validaIdTurmaService;
    private final ValidaCodigoTurmaService validaCodigoTurmaService;
    private final ValidaHorariosTurmaValidator validaHorariosTurma;

    @Transactional
    public TurmaResponseDTO atualizar(Long id, AtualizarTurmaRequestDTO dto) {

        validaIdTurmaService.validar(id);

        Turma turma = turmaRepository.findById(id).get();

        atualizaCamposInformados(dto, turma);

        return toResponseDTO(turma);
    }

    private void atualizaCamposInformados(AtualizarTurmaRequestDTO dto, Turma turma) {

        if (!dto.getCodigo().isBlank()) {

            String novoCodigo = dto.getCodigo().trim();

            validaCodigoTurmaService.validaSeExisteForaDoId(novoCodigo, turma.getId());
            turma.setCodigo(novoCodigo);
        }

        if (!dto.getNomeDisciplina().isBlank()) {
            turma.setNomeDisciplina(dto.getNomeDisciplina().trim());
        }

        if (dto.getProfessorId() != null) {
            turma.setProfessorId(dto.getProfessorId());
        }

        if (!dto.getSemestre().isBlank()) {
            turma.setSemestre(dto.getSemestre().trim());
        }

        if (!dto.getSala().isBlank()) {
            turma.setSala(dto.getSala().trim());
        }

        if (dto.getDiasSemana() != null && !dto.getDiasSemana().isEmpty()) {
            turma.setDiasSemana(dto.getDiasSemana());
        }

        LocalTime novoInicio = dto.getHorarioInicial() != null ? dto.getHorarioInicial() : turma.getHorarioInicial();
        LocalTime novoFim = dto.getHorarioFinal() != null ? dto.getHorarioFinal() : turma.getHorarioFinal();

        validaHorariosTurma.validarHorarioFinalPosteriorHorarioInicial(novoInicio, novoFim);

        if (dto.getHorarioInicial() != null) {
            turma.setHorarioInicial(dto.getHorarioInicial());
        }

        if (dto.getHorarioFinal() != null) {
            turma.setHorarioFinal(dto.getHorarioFinal());
        }

        if (dto.getCargaHoraria() != null) {
            turma.setCargaHoraria(dto.getCargaHoraria());
        }

        if (dto.getNumeroVagas() != null) {
            turma.setNumeroVagas(dto.getNumeroVagas());
        }
    }
}
