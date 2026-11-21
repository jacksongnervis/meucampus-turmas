package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.client.CadastroClient;
import br.edu.ifsul.sapucaia.meucampus_turmas.client.dto.ProfessorDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaDetalhesResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.validator.ValidaIdTurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.edu.ifsul.sapucaia.meucampus_turmas.mapper.TurmaMapper.toResponseDTO;

@Service
@RequiredArgsConstructor
public class BuscarDetalhesTurmaService {

    private final TurmaRepository turmaRepository;
    private final ValidaIdTurmaService validaIdTurmaService;
    private final CadastroClient cadastroClient;

    public TurmaDetalhesResponseDTO buscar(Long id) {

        validaIdTurmaService.validar(id);

        Turma turma = turmaRepository.findById(id).get();

        // Se o professor não existir mais no Cadastro, a turma é devolvida com os dados do professor nulos
        Optional<ProfessorDTO> professor = cadastroClient.buscarProfessor(turma.getProfessorId());

        return TurmaDetalhesResponseDTO.builder()
                .turma(toResponseDTO(turma))
                .professorNome(professor.map(ProfessorDTO::nome).orElse(null))
                .professorEmail(professor.map(ProfessorDTO::email).orElse(null))
                .build();
    }
}
