package br.edu.ifsul.sapucaia.meucampus_turmas.domain;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Turma {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nomeDisciplina;

    @Column(nullable = false)
    private Long professorId;

    @Column(nullable = false)
    private String semestre;

    @Column(nullable = false)
    private String sala;

    @Column(nullable = false)
    private List<DiaSemana> diasSemana;

    @Column(nullable = false)
    private LocalTime horarioInicial;

    @Column(nullable = false)
    private LocalTime horarioFinal;

    @Column(nullable = false)
    private int cargaHoraria;

    @Column(nullable = false)
    private int numeroVagas;
}
