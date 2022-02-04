package com.ust.wordmaster.user;

import com.ust.wordmaster.reservation.Reservation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(nullable = false,  unique = true)
    private String email;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Reservation> userReservations;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @Column(nullable = false)
    private OffsetDateTime dateUpdated;

    @PrePersist
    void setDateCreated(){
        this.dateCreated = OffsetDateTime.now();
        this.dateUpdated = this.dateCreated;
    }

    @PreUpdate
    void setDateUpdated(){
        this.dateUpdated = OffsetDateTime.now();
    }


}
