package hr.fer.apuw.lab1.service;

import hr.fer.apuw.lab1.exception.RequestDeniedException;
import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;
import hr.fer.apuw.lab1.repository.PostRepository;
import hr.fer.apuw.lab1.repository.UserRepository;
import hr.fer.apuw.lab1.service.impl.PostServiceImpl;
import hr.fer.apuw.lab1.service.impl.UserServiceImpl;
import hr.fer.apuw.lab1.util.PostGeneratingUtil;
import hr.fer.apuw.lab1.util.UserGeneratingUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  private UserService userService;

  @Mock
  private PostService postService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PostRepository postRepository;

  private PasswordEncoder passwordEncoder;

  @Before
  public void setup(){
    passwordEncoder = new BCryptPasswordEncoder();
    this.postService = new PostServiceImpl(postRepository, userRepository);
    this.userService = new UserServiceImpl(userRepository, postService, passwordEncoder);
  }

  @Test
  public void testGetOneUserCorrectParams(){
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    User foundUser = userService.getUserById(mockUser.getId());

    verify(userRepository, times(1)).findById(any(Long.class));

    Assert.assertEquals(foundUser, mockUser);
  }

  @Test
  public void testGetOneUserThrowsWhenNotExist(){
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getUserById(mockUser.getId()));

    verify(userRepository, times(1)).findById(any(Long.class));
  }

  @Test
  public void testGetAll(){
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userRepository.findAll()).thenReturn(List.of(mockUser));

    List<User> foundUsers = userService.getAllUsers();

    verify(userRepository, times(1)).findAll();

    Assert.assertEquals(1, foundUsers.size());
    Assert.assertEquals(foundUsers, List.of(mockUser));
  }

  @Test
  public void testCreateUserOKParams(){
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userRepository.existsByUsername(mockUser.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(false);
    when(userRepository.save(mockUser)).thenReturn(mockUser);

    User createdUser = userService.createUser(mockUser);

    verify(userRepository, times(1)).existsByUsername(any(String.class));
    verify(userRepository, times(1)).existsByEmail(any(String.class));
    verify(userRepository, times(1)).save(any(User.class));

    assertEquals(createdUser, mockUser);
  }

  @Test
  public void testCreateUserUsernameExists(){
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userRepository.existsByUsername(mockUser.getUsername())).thenReturn(true);

    assertThrows(RequestDeniedException.class, () -> userService.createUser(mockUser));

    verify(userRepository, times(1)).existsByUsername(any(String.class));
    verify(userRepository, times(0)).existsByEmail(any(String.class));
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  public void testCreateUserEmailExists() {
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userRepository.existsByUsername(mockUser.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(true);

    assertThrows(RequestDeniedException.class, () -> userService.createUser(mockUser));

    verify(userRepository, times(1)).existsByUsername(any(String.class));
    verify(userRepository, times(1)).existsByEmail(any(String.class));
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  public void testUpdateUserParamsOK(){
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    when(userRepository.save(mockUser)).thenReturn(mockUser);

    User updatedUser = userService.updateUser(mockUser.getId(), mockUser);

    assertEquals(updatedUser, mockUser);

    verify(userRepository, times(1)).findById(mockUser.getId());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  public void testUpdateUserNotExist(){
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.updateUser(mockUser.getId(), mockUser));

    verify(userRepository, times(1)).findById(mockUser.getId());
    verify(userRepository, times(0)).save(any(User.class));
  }


  @Test
  public void testDeleteUserOKParams(){
    User mockUser = UserGeneratingUtil.createMockUser();
    Post mockPost = PostGeneratingUtil.createMockPost();
    mockPost.setAuthor(mockUser);

    when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    when(postRepository.findAllByAuthor(mockUser)).thenReturn(List.of(mockPost));
    when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
    //when(postRepository.deleteById(any(Long.class))).thenReturn(Optional.of(mockPost));

    userService.deleteUser(mockUser.getId());

    verify(userRepository, times(1)).findById(mockUser.getId());
    verify(postRepository, times(1)).findAllByAuthor(any(User.class));
    verify(postRepository, times(1)).findById(any(Long.class));
    verify(postRepository, times(1)).deleteById(mockPost.getId());
    verify(userRepository, times(1)).delete(any(User.class));
  }

  @Test
  public void testDeleteUserNotExist(){
    User mockUser = UserGeneratingUtil.createMockUser();
    Post mockPost = PostGeneratingUtil.createMockPost();
    mockPost.setAuthor(mockUser);

    when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(mockUser.getId()));

    verify(userRepository, times(1)).findById(mockUser.getId());
    verify(postRepository, times(0)).findAllByAuthor(any(User.class));
    verify(postRepository, times(0)).findById(any(Long.class));
    verify(postRepository, times(0)).deleteById(mockPost.getId());
    verify(userRepository, times(0)).delete(any(User.class));
  }
}
