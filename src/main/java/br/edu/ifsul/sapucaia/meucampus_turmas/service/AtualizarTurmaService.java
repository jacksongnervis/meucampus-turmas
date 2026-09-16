package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.AtualizarTurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.validator.ValidaIdTurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AtualizarTurmaService {

    private final TurmaRepository turmaRepository;
    private final ValidaIdTurmaService validaIdTurmaService;

    public TurmaResponseDTO atualizar(Long id, AtualizarTurmaRequestDTO dto) {
        validaIdTurmaService.validar(id);
        Turma turma = turmaRepository.findById(id).get();

        if (!dto.getCodigo().isBlank()) {
            String novoCodigo = dto.getCodigo().trim();
            if (turmaRepository.existsByCodigoAndIdNot(novoCodigo, id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe outra turma cadastrada com o código: " + novoCodigo);
            }
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

        if (novoFim.isBefore(novoInicio) || novoFim.equals(novoInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O horário final deve ser posterior ao horário inicial");
        }

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

        Turma atualizada = turmaRepository.save(turma);

        return TurmaMapper.toResponseDTO(atualizada);
    }
}
