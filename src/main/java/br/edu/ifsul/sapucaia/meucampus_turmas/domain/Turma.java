package br.edu.ifsul.sapucaia.meucampus_turmas.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Turma {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String codigo;

    private String nomeDisciplina;

    private String idProfessor;

    private int semestre;

    private String sala;

    private LocalTime horario;

    private int cargaHoraria;

    private int numeroVagas;
}
