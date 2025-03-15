package com.example.app.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length=60)
    private RoleName name;

    public Role(RoleName name) {
        this.name = name;
    }
}
