package hr.fer.apuw.lab1.service;

import hr.fer.apuw.lab1.dto.request.CreatePostDTO;
import hr.fer.apuw.lab1.dto.request.UpdatePostDTO;
import hr.fer.apuw.lab1.dto.response.PostDTO;
import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;

import java.util.List;

public interface PostService {

  /**
   * Returns the post with the given id.
   * @throws jakarta.persistence.EntityNotFoundException if a post with the given id does not exist
   */
  Post getPostById(Long postId);

  /**
   * Returns all saved posts, from all users.
   */
  List<Post> getAllPosts();

  /**
   * Returns all posts belonging to the given user
   */
  List<Post> getAllPostsByUser(User user);

  /**
   * Creates a new post.
   */
  Post createPost(CreatePostDTO postDTO);

  /**
   * Updates the post.
   * @throws jakarta.persistence.EntityNotFoundException if the post does not exist
   */
  Post updatePost(Long postId, UpdatePostDTO postDTO);

  /**
   * Deletes the post with the given id.
   * @throws jakarta.persistence.EntityNotFoundException if the post with the given id does not exist
   */
  void deletePost(Long postId);
}
