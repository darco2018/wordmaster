package com.ust.wordmaster.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class UserCommandLineRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User jane = new User();
        jane.setEmail("jane@dot.com");
        jane = this.userRepository.saveAndFlush(jane);

        User mike = new User();
        mike.setEmail("mike@dot.com");
        mike = this.userRepository.saveAndFlush(mike);
    }
}
