package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarTurmasService {

    private final TurmaRepository turmaRepository;

    public List<TurmaResponseDTO> buscar() {

        return turmaRepository.findAll()
                .stream()
                .map(TurmaMapper::toResponseDTO)
                .toList();
    }
}
