package hr.fer.apuw.lab1.service;

import hr.fer.apuw.lab1.model.User;

import java.util.List;

public interface UserService {

  /**
   * Returns the user with the given id.
   * @throws jakarta.persistence.EntityNotFoundException if the user with the given id does not exist
   */
  User getUserById(Long id);

  /**
   * Returns all users
   * @return
   */
  List<User> getAllUsers();

  /**
   * Creates a new user. The given User object has null for id attribute. The returned object has the id attribute
   * set to the assigned id.
   * @return
   */
  User createUser(User user);

  /**
   * Deletes the user with the given id. Also deletes all their posts.
   * @throws jakarta.persistence.EntityNotFoundException if the id is null or a user with the given id does not exist
   */
  void deleteUser(Long userId);

  /**
   * Updates the user, and returns the updated version of the user object.
   * @throws jakarta.persistence.EntityNotFoundException if the user is null or if the user does not exist
   */
  User updateUser(Long userId, User user);

  //TODO: metoda za login?
}
