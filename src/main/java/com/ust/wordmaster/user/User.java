package com.ust.wordmaster.user;

import com.ust.wordmaster.headline_exercise.HeadlineExercise;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    // GenerationType.SEQUENCE because it is very efficient and allows Hibernate to decide when to perform the insert statement
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", initialValue = 100, allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 5, max = 100)
    private String email;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<HeadlineExercise> headlineExercises;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @Column(nullable = false)
    private OffsetDateTime dateUpdated;

    @PrePersist
    void setDateCreated() {
        this.dateCreated = OffsetDateTime.now();
        this.dateUpdated = this.dateCreated;
    }

    @PreUpdate
    void setDateUpdated() {
        this.dateUpdated = OffsetDateTime.now();
    }


}
