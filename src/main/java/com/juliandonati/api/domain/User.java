package com.juliandonati.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,  cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "users_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Event> attendedEvents = new HashSet<>();

    public void addAttendedEvent(Event event){
        attendedEvents.add(event);
        event.getAttendedUsers().add(this);
    }

    public void removeAttendedEvent(Event event){
        attendedEvents.remove(event);
        event.getAttendedUsers().remove(this);
    }

    // TENER MÉTODOS EN UN DOMINIO...

    // PROS: Sincronización.
    // CONTRAS: Mezcla de responsabilidades (las entidades deberían ser solo los datos). El usuario conoce al evento y viceversa.
    // Viola el principio de bajo acoplamiento.

    // Podríamos haber hecho un servicio para los métodos.

    // Ahora lo hicimos acá porque es ManyToMany y un poco complejo. No debemos de hacerlo aca si preferimos un modelo más anémico o si
    // las direcciones son unidireccionales o la aplicación es un CRUD simple donde estos métodos no tendrían sentido en este contexto.
}
