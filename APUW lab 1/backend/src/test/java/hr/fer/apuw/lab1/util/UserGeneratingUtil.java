package hr.fer.apuw.lab1.util;

import hr.fer.apuw.lab1.model.User;

public class UserGeneratingUtil {

  public static User createMockUser() {
    User user = new User();
    user.setId(1L);
    user.setFirstName("Dorian");
    user.setLastName("Smoljan");
    user.setEmail("dorian.smoljan@gmail.com");
    user.setPassword("password");
    user.setUsername("dsmoljan");
    return user;
  }


}
