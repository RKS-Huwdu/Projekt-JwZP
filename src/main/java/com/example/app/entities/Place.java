package com.example.app.entities;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "places")
@Getter
@Setter
@NoArgsConstructor
public class Place{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
