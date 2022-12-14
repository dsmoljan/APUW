package hr.fer.apuw.lab1.controller;

import hr.fer.apuw.lab1.dto.response.PostDTO;
import hr.fer.apuw.lab1.dto.response.UserDetailsDTO;
import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;
import hr.fer.apuw.lab1.service.PostService;
import hr.fer.apuw.lab1.service.UserService;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

//TODO: implementiraj odgovarajući exception handler, koji će presretati sve exceptione i vraćati ih u odgovarajućem formatu!
//također ovdje implementiraj login handler?

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  private final PostService postService;

  private final ModelMapper modelMapper;

  @Autowired
  public UserController(UserService userService, PostService postService, ModelMapper modelMapper){
    this.userService = userService;
    this.postService = postService;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<List<UserDetailsDTO>> getAllUsers(){
    return ResponseEntity.ok((userService.getAllUsers().stream().map(m -> modelMapper.map(m, UserDetailsDTO.class)).collect(Collectors.toList())));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDetailsDTO> getUser(@PathVariable Long userId){
    return ResponseEntity.ok(modelMapper.map(userService.getUserById(userId), UserDetailsDTO.class));
  }

  @PostMapping
  public ResponseEntity<UserDetailsDTO> createUser(@Valid @RequestBody User newUser){
    return ResponseEntity.ok(modelMapper.map(userService.createUser(newUser), UserDetailsDTO.class));
  }

  @PutMapping("/{userId}")
  public ResponseEntity<UserDetailsDTO> updateUser(@PathVariable Long userId, @Valid @RequestBody User newUser){
    return ResponseEntity.ok(modelMapper.map(userService.updateUser(userId, newUser), UserDetailsDTO.class));
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
    userService.deleteUser(userId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{userId}/posts")
  public ResponseEntity<List<PostDTO>> getAllPostsByUser(@PathVariable Long userId){

    User user = userService.getUserById(userId);

    return ResponseEntity.ok(postService.getAllPostsByUser(user).stream().map(p -> modelMapper.map(p, PostDTO.class)).collect(Collectors.toList()));
  }


}
