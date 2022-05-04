package com.ust.wordmaster.headline_exercise;

import com.ust.wordmaster.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Table(name = "HeadlineExercise")
@Entity
public class HeadlineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "headline_exercise_generator")
    @SequenceGenerator(name = "headline_exercise_generator", sequenceName = "HeadlineExerciseSeq", initialValue = 10, allocationSize = 1)
    @Column(name = "headlineExerciseID", nullable = false, updatable = false)
    private Long id;

    /*the owning side is usually defined on the ‘many' side of the relationship.
    It's usually the side which owns the foreign key.*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    //HeadlineExercise entity will have a foreign key column named user_id referring to the primary attribute id of our User entity.
    private User user;

    @Column(nullable = false)
    private String title;

    /*
    The <?> lives in the <?>. #[lion, jungle]
    Do people <?> apples? #[eat]
    Never play with <?> and <?>!!! #[spiders, insects]
    */
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @Column(nullable = false)
    private OffsetDateTime dateUpdated;

    @PrePersist
    void onPersist() {
        this.dateCreated = OffsetDateTime.now();
        this.dateUpdated = this.dateCreated;
    }

    @PreUpdate
    void onUpdate() {
        this.dateUpdated = OffsetDateTime.now();
    }

}
