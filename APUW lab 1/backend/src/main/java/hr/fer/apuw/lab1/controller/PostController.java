package hr.fer.apuw.lab1.controller;

import hr.fer.apuw.lab1.dto.request.CreatePostDTO;
import hr.fer.apuw.lab1.dto.request.UpdatePostDTO;
import hr.fer.apuw.lab1.dto.response.PostDTO;
import hr.fer.apuw.lab1.service.PostService;
import javax.validation.Valid;
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

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  private final ModelMapper modelMapper;

  @Autowired
  public PostController(PostService postService, ModelMapper modelMapper){
    this.postService = postService;
    this.modelMapper = modelMapper;
  }

  // possible improvement
  // add a "public" field to a post
  // then, this method would return only posts with the public field set to true
  @GetMapping
  public ResponseEntity<List<PostDTO>> getAllPosts(){
    return ResponseEntity.ok(postService.getAllPosts().stream().map(p -> modelMapper.map(p, PostDTO.class)).collect(Collectors.toList()));
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostDTO> getPost(@PathVariable Long postId){
    return ResponseEntity.ok(modelMapper.map(postService.getPostById(postId), PostDTO.class));
  }

  @PostMapping
  public ResponseEntity<PostDTO> createPost(@Valid @RequestBody CreatePostDTO postDTO){
    return ResponseEntity.ok(modelMapper.map(postService.createPost(postDTO), PostDTO.class));
  }

  @PutMapping("/{postId}")
  public ResponseEntity<PostDTO> updatePost(@PathVariable Long postId, @Valid @RequestBody UpdatePostDTO postDTO){
    return ResponseEntity.ok(modelMapper.map(postService.updatePost(postId, postDTO), PostDTO.class));
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId){
    postService.deletePost(postId);
    return ResponseEntity.ok().build();
  }
}
