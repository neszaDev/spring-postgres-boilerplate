package com.example.boilerplate.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.boilerplate.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
public class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Test
  void createAndFindByEmail() {
    User u = new User();
    u.setEmail("repo-test@example.com");
    u.setPasswordHash("hashme");
    u.setFullName("Repo Test");
    userRepository.save(u);
    assertNotNull(u.getId());
    var found = userRepository.findByEmail("repo-test@example.com");
    assertTrue(found.isPresent());
    assertEquals("Repo Test", found.get().getFullName());
  }
}
