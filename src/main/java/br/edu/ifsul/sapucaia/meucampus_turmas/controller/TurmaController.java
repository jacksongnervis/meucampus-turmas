package br.edu.ifsul.sapucaia.meucampus_turmas.controller;

import br.edu.ifsul.sapucaia.meucampus_turmas.controller.response.BuscarTurmaResponse;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/turma")
@RequiredArgsConstructor
public class TurmaController {

    private final BuscarTurmasService buscarTurmasService;

    private final BuscarTurmasPorSemestreService buscarTurmasPorSemestreService;

    private final CadastrarTurmaService cadastrarTurmaService;

    private final AtualizarTurmaService atualizarTurmaService;

    private final DeletarTurmaService deletarTurmaService;

    private final BuscarTurmaPorIdService buscarTurmaPorIdService;

    @GetMapping
    @ResponseStatus(OK)
    public List<BuscarTurmaResponse> buscarTurmas(){
        return buscarTurmasService.buscar();
    }

    @GetMapping("/semestre/{semestre}")
    @ResponseStatus(OK)
    public List<BuscarTurmaResponse> buscarTurmasPorSemestre(@PathVariable String semestre) {
        return buscarTurmasPorSemestreService.buscar(semestre);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public TurmaResponseDTO cadastrar(@Valid @RequestBody TurmaRequestDTO dto) {
        return cadastrarTurmaService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public TurmaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody TurmaRequestDTO dto) {
        return atualizarTurmaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        deletarTurmaService.deletar(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public BuscarTurmaResponse buscarTurma(@PathVariable Long id) {
        return buscarTurmaPorIdService.buscar(id);
    }
}
