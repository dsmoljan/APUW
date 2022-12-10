package hr.fer.apuw.lab1.repository;

import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByAuthor(User user);
}
