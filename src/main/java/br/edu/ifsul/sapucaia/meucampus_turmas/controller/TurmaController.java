package br.edu.ifsul.sapucaia.meucampus_turmas.controller;

import br.edu.ifsul.sapucaia.meucampus_turmas.controller.response.BuscarTurmaResponse;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.BuscarTurmasService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/turma")
@RequiredArgsConstructor
public class TurmaController {

    private final BuscarTurmasService buscarTurmasService;

    @GetMapping
    @ResponseStatus(OK)
    public List<BuscarTurmaResponse> buscarTurmas(){
        return buscarTurmasService.buscar();
    }
}
