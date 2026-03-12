package com.juliandonati.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "speakers")

@AllArgsConstructor
@NoArgsConstructor
public class Speaker {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 50)
    private String bio;

    @ManyToMany(mappedBy="speakers", fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // Va del lado mappedBy
    // EXCLUÍMOS LOS EVENTOS DE ESTOS DOS MÉTODOS QUE GENERA EL @Data PARA EVITAR UN LOOP INFINITO.
    private Set<Event> events = new HashSet<>();
}
