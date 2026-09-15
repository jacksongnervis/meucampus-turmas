package br.edu.ifsul.sapucaia.meucampus_turmas.controller;

import br.edu.ifsul.sapucaia.meucampus_turmas.controller.response.BuscarTurmaResponse;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.BuscarTurmasPorSemestreService;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.BuscarTurmasService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/turma")
@RequiredArgsConstructor
public class TurmaController {

    private final BuscarTurmasService buscarTurmasService;

    private final BuscarTurmasPorSemestreService buscarTurmasPorSemestreService;

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
}
