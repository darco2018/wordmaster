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
@Table(name = "headline_exercise")
@Entity
public class HeadlineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "headline_exercise_generator")
    @SequenceGenerator(name = "headline_exercise_generator", sequenceName = "headline_exercise_seq", initialValue = 10, allocationSize = 1)
    @Column(name = "headline_exercise_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
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
    private OffsetDateTime lastUpdated;

    @PrePersist
    void onPersist() {
        this.dateCreated = OffsetDateTime.now();
        this.lastUpdated = this.dateCreated;
    }

    @PreUpdate
    void onUpdate() {
        this.lastUpdated = OffsetDateTime.now();
    }

}
