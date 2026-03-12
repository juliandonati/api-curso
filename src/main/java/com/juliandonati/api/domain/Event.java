package com.juliandonati.api.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Entity
@Table(name = "events")
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String location;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "events_speakers",
            joinColumns = @JoinColumn(name = "event_id"), // Por defecto busca la clave primaria
            inverseJoinColumns = @JoinColumn(name = "speaker_id")
    )
    private Set<Speaker> speakers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany(mappedBy = "attendedEvents", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> attendedUsers = new HashSet<>();


    public void addSpeaker(Speaker speaker){
        speakers.add(speaker);
        speaker.getEvents().add(this);
    }

    public void removeSpeaker(Speaker speaker){
        speakers.remove(speaker);
        speaker.getEvents().remove(this);
    }
}
