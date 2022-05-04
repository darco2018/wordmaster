package com.ust.wordmaster.user;

import com.ust.wordmaster.headline_exercise.HeadlineExercise;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
/*When you provide your own constructor then Lombok doesn't create constructor with all args that @Builder is using.
So you should just add annotation @AllArgsConstructor */
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "AppUser")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    // GenerationType.SEQUENCE because it is very efficient and allows Hibernate to decide when to perform the insert statement
    @SequenceGenerator(name = "user_generator", sequenceName = "AppUserSeq", initialValue = 100, allocationSize = 1)
    @Column(name = "userID", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 5, max = 100)
    private String email;

    /*Once we have defined the owning side of the relationship (HeadlineExercise), Hibernate already has all the information it needs
    to map that relationship in our database. To make this association bidirectional, all we'll have to do is to define
     the referencing side(User).
     Defining the direction of the relationship between entities has no impact on the database mapping. It only defines
     the directions in which we use that relationship in our domain model.*/
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    /*the value of mappedBy is the name of the association-mapping attribute on the owning side. With this, we have
    now established a bidirectional association between our User and HeadlineExercise entities.*/
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




