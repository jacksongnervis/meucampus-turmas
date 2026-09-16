package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CadastrarTurmaService {

    private final TurmaRepository turmaRepository;

    public TurmaResponseDTO cadastrar(TurmaRequestDTO dto) {
        if (turmaRepository.existsByCodigo(dto.getCodigo().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma turma cadastrada com o código: " + dto.getCodigo());
        }

        if (dto.getHorarioFinal().isBefore(dto.getHorarioInicial()) ||
                dto.getHorarioFinal().equals(dto.getHorarioInicial())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O horário final deve ser posterior ao horário inicial");
        }

        Turma turma = TurmaMapper.toEntity(dto);
        Turma salva = turmaRepository.save(turma);

        return TurmaMapper.toResponseDTO(salva);
    }
}
