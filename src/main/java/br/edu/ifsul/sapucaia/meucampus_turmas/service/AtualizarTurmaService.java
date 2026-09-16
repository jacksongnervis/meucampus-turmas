package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.validator.ValidaIdTurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AtualizarTurmaService {

    private final TurmaRepository turmaRepository;
    private final ValidaIdTurmaService validaIdTurmaService;

    public TurmaResponseDTO atualizar(Long id, TurmaRequestDTO dto) {
        validaIdTurmaService.validar(id);
        Turma turma = turmaRepository.findById(id).get();

        if (turmaRepository.existsByCodigoAndIdNot(dto.getCodigo().trim(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe outra turma cadastrada com o código: " + dto.getCodigo());
        }

        if (dto.getHorarioFinal().isBefore(dto.getHorarioInicial()) ||
                dto.getHorarioFinal().equals(dto.getHorarioInicial())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O horário final deve ser posterior ao horário inicial");
        }

        turma.setCodigo(dto.getCodigo().trim());
        turma.setNomeDisciplina(dto.getNomeDisciplina().trim());
        turma.setProfessorId(dto.getProfessorId());
        turma.setSemestre(dto.getSemestre().trim());
        turma.setSala(dto.getSala().trim());
        turma.setDiasSemana(dto.getDiasSemana());
        turma.setHorarioInicial(dto.getHorarioInicial());
        turma.setHorarioFinal(dto.getHorarioFinal());
        turma.setCargaHoraria(dto.getCargaHoraria());
        turma.setNumeroVagas(dto.getNumeroVagas());

        Turma atualizada = turmaRepository.save(turma);

        return TurmaMapper.toResponseDTO(atualizada);
    }
}
