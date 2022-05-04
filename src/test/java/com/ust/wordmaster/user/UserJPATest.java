package com.ust.wordmaster.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


// @SpringBootTest(classes =   ) would load the entire app with tomcat: @SpringBootTest(classes = )
@DataJpaTest //skips Tomcat, @Transactional, uses h2 by default, autoconfigures testEntityManagera,
// contains @ExtendWith({org.springframework.test.context.junit.jupiter.SpringExtension.class})
// contains @AutoConfigureDataJpa @AutoConfigureTestDatabase @AutoConfigureTestEntityManager

// necessary for the test to pick up H2's url from application-h2.properties rather than use autoconfigured default H2's url
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserJPATest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        User user1 = new User();
        user1.setEmail("user1@dot.com");
        User user2 = new User();
        user2.setEmail("user2@dot.com");

        user1 = this.entityManager.persistFlushFind(user1);
        user2 = this.entityManager.persistFlushFind(user2);

        // check db mapping
        Assertions.assertThat(user1.getEmail()).isEqualTo("user1@dot.com");
        Assertions.assertThat(user1.getId()).isNotNull();

        Assertions.assertThat(user2.getEmail()).isEqualTo("user2@dot.com");
        Assertions.assertThat(user2.getId()).isNotNull();
    }
}