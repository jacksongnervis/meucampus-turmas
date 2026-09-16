package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.exception.BusinessException;
import br.edu.ifsul.sapucaia.meucampus_turmas.exception.ResourceNotFoundException;
import br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarTurmaService {

    private final TurmaRepository turmaRepository;

    public TurmaResponseDTO atualizar(Long id, TurmaRequestDTO dto) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com o ID: " + id));

        if (turmaRepository.existsByCodigoAndIdNot(dto.getCodigo().trim(), id)) {
            throw new BusinessException("Já existe outra turma cadastrada com o código: " + dto.getCodigo());
        }

        if (dto.getHorarioFinal().isBefore(dto.getHorarioInicial()) ||
                dto.getHorarioFinal().equals(dto.getHorarioInicial())) {
            throw new BusinessException("O horário final deve ser posterior ao horário inicial");
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
