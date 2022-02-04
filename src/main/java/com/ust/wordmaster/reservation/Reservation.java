package com.ust.wordmaster.reservation;

import com.ust.wordmaster.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_generator")
    @SequenceGenerator(name = "reservation_generator",
            sequenceName = "reservation_seq",
            initialValue = 10,
            allocationSize = 1)
    @Column(name = "reservation_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime reservationDate;

    @Column(nullable = false)
    private LocalTime reservationStart;

    @Column(nullable = false)
    private LocalTime reservationEnd;

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