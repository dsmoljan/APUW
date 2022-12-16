package hr.fer.apuw.lab1.util;

import hr.fer.apuw.lab1.dto.request.CreatePostDTO;
import hr.fer.apuw.lab1.dto.request.UpdatePostDTO;
import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;
import org.hibernate.sql.Update;

public class PostGeneratingUtil {

  public static Post createMockPost() {
    Post post = new Post();
    User user = UserGeneratingUtil.createMockUser();

    post.setId(1L);
    post.setAuthor(user);
    post.setContent("Random content");
    post.setImage(null);

    return post;
  }

  public static CreatePostDTO createCreatePostDTO(){
    Post post = createMockPost();
    CreatePostDTO createPostDTO = new CreatePostDTO();
    createPostDTO.setContent(post.getContent());
    createPostDTO.setImage(post.getImage());
    createPostDTO.setAuthorId(post.getAuthor().getId());
    return createPostDTO;
  }

  public static UpdatePostDTO createUpdatePostDTO(){
    Post post = createMockPost();
    UpdatePostDTO updatePostDTO = new UpdatePostDTO();
    updatePostDTO.setContent(post.getContent());
    updatePostDTO.setImage(post.getImage());
    return updatePostDTO;
  }
}
