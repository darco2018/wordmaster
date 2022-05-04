package com.ust.wordmaster.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
// necessary for the test to pick up H2's url from application-h2.properties rather than use autoconfigured default H2's url
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByEmail_thenReturnUser() {

        User user = new User();
        user.setEmail("999@mai.com");
        entityManager.persist(user);
        entityManager.flush();

        User found = userRepository.findByEmail(user.getEmail());

        Assertions.assertThat(found.getEmail())
                .isEqualTo(user.getEmail());
    }

}
