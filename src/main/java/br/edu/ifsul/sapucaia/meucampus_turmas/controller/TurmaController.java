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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "Turmas", description = "Operações para gestão da oferta de turmas acadêmicas")
@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final BuscarTurmasService buscarTurmasService;

    private final CadastrarTurmaService cadastrarTurmaService;

    private final AtualizarTurmaService atualizarTurmaService;

    private final DeletarTurmaService deletarTurmaService;

    private final BuscarTurmaPorIdService buscarTurmaPorIdService;

    private final BuscarDetalhesTurmaService buscarDetalhesTurmaService;

    @Operation(
            summary = "Lista as turmas de forma paginada",
            description = "Listagem paginada com filtros opcionais. Sem 'sort', ordena por nomeDisciplina. " +
                    "Usado pelo serviço de Cadastro (?professorId=) para impedir a remoção de professor responsável por turma. " +
                    "Resposta no formato { content: [...], page: { size, number, totalElements, totalPages } }."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de turmas (content vazio quando nenhum registro atende ao filtro)"),
            @ApiResponse(responseCode = "400", description = "Parâmetro com tipo inválido (ex.: professorId=abc) ou campo de ordenação inexistente", content = @Content)
    })
    @GetMapping
    @ResponseStatus(OK)
    public Page<TurmaResponseDTO> buscarTurmas(
            @Parameter(description = "Filtra pelas turmas do professor (id do serviço de Cadastro)", example = "1")
            @RequestParam(required = false) Long professorId,
            @Parameter(description = "Filtra pelo semestre letivo", example = "2026/2")
            @RequestParam(required = false) String semestre,
            @ParameterObject @PageableDefault(size = 10, sort = "nomeDisciplina") Pageable pageable
    ) {
        return buscarTurmasService.buscar(professorId, semestre, pageable);
    }

    @Operation(
            summary = "Cadastra uma nova turma",
            description = "Cria uma turma validando unicidade do código, consistência de horários, ausência de conflito de horário na sala e existência do professor no serviço de Cadastro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Turma cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, código já cadastrado ou professorId inexistente no Cadastro", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito de horário: já existe turma na mesma sala, semestre e dia com horário sobreposto", content = @Content),
            @ApiResponse(responseCode = "503", description = "Serviço de Cadastro indisponível ou tempo limite excedido", content = @Content)
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public TurmaResponseDTO cadastrar(@Valid @RequestBody TurmaRequestDTO dto) {
        return cadastrarTurmaService.cadastrar(dto);
    }

    @Operation(
            summary = "Atualiza uma turma existente",
            description = "Atualiza os campos informados da turma e confere se o resultado não gera conflito de horário na sala. Quando professorId é enviado, confere sua existência no serviço de Cadastro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, código duplicado ou professorId inexistente no Cadastro", content = @Content),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito de horário: já existe turma na mesma sala, semestre e dia com horário sobreposto", content = @Content),
            @ApiResponse(responseCode = "503", description = "Serviço de Cadastro indisponível ou tempo limite excedido", content = @Content)
    })
    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public TurmaResponseDTO atualizar(
            @Parameter(description = "ID da turma", example = "1") @PathVariable Long id,
            @Valid @RequestBody AtualizarTurmaRequestDTO dto) {
        return atualizarTurmaService.atualizar(id, dto);
    }

    @Operation(
            summary = "Remove uma turma",
            description = "Antes de remover, consulta o serviço de Matrículas (GET /matriculas?turmaId={id}&situacao=ATIVA). " +
                    "A remoção é recusada se houver matrícula ativa."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Turma removida"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Turma possui matrículas ativas", content = @Content),
            @ApiResponse(responseCode = "503", description = "Serviço de Matrículas indisponível ou tempo limite excedido", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deletar(
            @Parameter(description = "ID da turma", example = "1") @PathVariable Long id) {
        deletarTurmaService.deletar(id);
    }

    @Operation(
            summary = "Busca uma turma pelo id",
            description = "Usado pelo serviço de Matrículas para conferir se a turma existe e consultar o número de vagas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma encontrada"),
            @ApiResponse(responseCode = "400", description = "Id com formato inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public TurmaResponseDTO buscarTurma(
            @Parameter(description = "ID da turma", example = "1") @PathVariable Long id) {
        return buscarTurmaPorIdService.buscar(id);
    }

    @Operation(
            summary = "Busca os detalhes da turma com os dados do professor",
            description = "Consulta composta que retorna os dados da turma acompanhados do nome e e-mail do professor, buscados em tempo real no serviço de Cadastro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes da turma encontrados"),
            @ApiResponse(responseCode = "400", description = "Id com formato inválido", content = @Content),
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
