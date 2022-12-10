package hr.fer.apuw.lab1.service.impl;

import hr.fer.apuw.lab1.dto.request.CreatePostDTO;
import hr.fer.apuw.lab1.dto.request.UpdatePostDTO;
import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;
import hr.fer.apuw.lab1.repository.PostRepository;
import hr.fer.apuw.lab1.repository.UserRepository;
import hr.fer.apuw.lab1.service.PostService;
import hr.fer.apuw.lab1.service.UserService;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;

  private final UserRepository userRepository;

  @Autowired
  public PostServiceImpl(PostRepository postRepository, UserRepository userRepository){
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Post getPostById(Long postId) {
    return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with the given id was not found!"));
  }

  @Override
  public List<Post> getAllPosts() {
    return postRepository.findAll();
  }

  @Override
  public List<Post> getAllPostsByUser(User user) {
    return postRepository.findAllByAuthor(user);
  }

  @Override
  public Post createPost(CreatePostDTO postDto) {
    Post newPost = new Post();

    User author = userRepository.findById(postDto.getAuthorId()).orElseThrow(() -> new EntityNotFoundException("User with given ID was not found!"));

    newPost.setContent(postDto.getContent());
    newPost.setImage(postDto.getImage());
    newPost.setAuthor(author);

    return postRepository.save(newPost);
  }

  @Override
  public Post updatePost(Long postId, UpdatePostDTO postDTO) {

    Post oldPost = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with the given id was not found!"));
    oldPost.setContent(postDTO.getContent());
    oldPost.setImage(postDTO.getImage());

    return postRepository.save(oldPost);
  }

  @Override
  public void deletePost(Long postId) {
    if (postRepository.findById(postId).isEmpty()) throw new EntityNotFoundException("Post with the given id was not found!");

    postRepository.deleteById(postId);
  }
}
