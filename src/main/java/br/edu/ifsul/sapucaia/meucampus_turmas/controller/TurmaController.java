package br.edu.ifsul.sapucaia.meucampus_turmas.controller;

import br.edu.ifsul.sapucaia.meucampus_turmas.dto.AtualizarTurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaDetalhesResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Turmas", description = "Operações para gestão da oferta de turmas acadêmicas")
@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final BuscarTurmasService buscarTurmasService;

    private final BuscarTurmasPorSemestreService buscarTurmasPorSemestreService;

    private final CadastrarTurmaService cadastrarTurmaService;

    private final AtualizarTurmaService atualizarTurmaService;

    private final DeletarTurmaService deletarTurmaService;

    private final BuscarTurmaPorIdService buscarTurmaPorIdService;

    private final BuscarDetalhesTurmaService buscarDetalhesTurmaService;

    @GetMapping
    @ResponseStatus(OK)
    public List<TurmaResponseDTO> buscarTurmas(){
        return buscarTurmasService.buscar();
    }

    @GetMapping("/semestre")
    @ResponseStatus(OK)
    public List<TurmaResponseDTO> buscarTurmasPorSemestre(@RequestParam String semestre) {
        return buscarTurmasPorSemestreService.buscar(semestre);
    }

    @Operation(
            summary = "Cadastra uma nova turma",
            description = "Cria uma turma validando unicidade do código, consistência de horários e existência do professor no serviço de Cadastro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Turma cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, código já cadastrado ou professorId inexistente no Cadastro", content = @Content),
            @ApiResponse(responseCode = "503", description = "Serviço de Cadastro indisponível ou tempo limite excedido", content = @Content)
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public TurmaResponseDTO cadastrar(@Valid @RequestBody TurmaRequestDTO dto) {
        return cadastrarTurmaService.cadastrar(dto);
    }

    @Operation(
            summary = "Atualiza uma turma existente",
            description = "Atualiza os campos informados da turma. Quando professorId é enviado, confere sua existência no serviço de Cadastro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, código duplicado ou professorId inexistente no Cadastro", content = @Content),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada", content = @Content),
            @ApiResponse(responseCode = "503", description = "Serviço de Cadastro indisponível ou tempo limite excedido", content = @Content)
    })
    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public TurmaResponseDTO atualizar(
            @Parameter(description = "ID da turma", example = "1") @PathVariable Long id,
            @Valid @RequestBody AtualizarTurmaRequestDTO dto) {
        return atualizarTurmaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        deletarTurmaService.deletar(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public TurmaResponseDTO buscarTurma(@PathVariable Long id) {
        return buscarTurmaPorIdService.buscar(id);
    }

    @Operation(
            summary = "Busca os detalhes da turma com os dados do professor",
            description = "Consulta composta que retorna os dados da turma acompanhados do nome e e-mail do professor, buscados em tempo real no serviço de Cadastro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes da turma encontrados"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada", content = @Content),
            @ApiResponse(responseCode = "503", description = "Serviço de Cadastro indisponível ou tempo limite excedido", content = @Content)
    })
    @GetMapping("/{id}/detalhes")
    @ResponseStatus(OK)
    public TurmaDetalhesResponseDTO buscarDetalhes(
            @Parameter(description = "ID da turma", example = "1") @PathVariable Long id) {
        return buscarDetalhesTurmaService.buscar(id);
    }
}
