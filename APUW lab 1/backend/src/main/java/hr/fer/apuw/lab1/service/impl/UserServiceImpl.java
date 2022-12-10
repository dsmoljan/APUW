package hr.fer.apuw.lab1.service.impl;

import hr.fer.apuw.lab1.exception.RequestDeniedException;
import hr.fer.apuw.lab1.model.User;
import hr.fer.apuw.lab1.repository.UserRepository;
import hr.fer.apuw.lab1.service.PostService;
import hr.fer.apuw.lab1.service.UserService;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final PostService postService;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, PostService postService){
    this.userRepository = userRepository;
    this.postService = postService;
  }

  @Override
  public User getUserById(Long id) {
    Optional<User> optionalUser = userRepository.findById(id);

    return optionalUser.orElseThrow(() -> new  EntityNotFoundException("User with the given id was not found!"));
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public User createUser(User user) {
    if (userRepository.existsByUsername(user.getUsername())){
      throw new RequestDeniedException("User with the given username already exists!");
      }
    else if (userRepository.existsByEmail(user.getEmail())){
      throw new RequestDeniedException("User with the given email already exists!");
    }


    return userRepository.save(user);
  }

  @Override
  public void deleteUser(Long userId) {
    Optional<User> optionalUser = userRepository.findById(userId);
    User user = optionalUser.orElseThrow(() -> new  EntityNotFoundException("User with the given id was not found!"));

    // when deleting a user, we also have to delete all their posts
    postService.getAllPostsByUser(user).forEach(post -> postService.deletePost(post.getId()));

    userRepository.delete(user);
  }

  @Override
  public User updateUser(Long userId, User user) {
    if (userRepository.findById(userId).isEmpty()) throw new EntityNotFoundException("User with the given id was not found!");

    user.setId(userId);
    return userRepository.save(user);
  }
}
