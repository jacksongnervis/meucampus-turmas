package br.edu.ifsul.sapucaia.meucampus_turmas.domain;

import jakarta.persistence.Column;
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
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nomeDisciplina;

    @Column(nullable = false)
    private String idProfessor;

    @Column(nullable = false)
    private int semestre;

    @Column(nullable = false)
    private String sala;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(nullable = false)
    private int cargaHoraria;

    @Column(nullable = false)
    private int numeroVagas;
}
