package hr.fer.apuw.lab1.util;

import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;

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
}
