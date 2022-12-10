package hr.fer.apuw.lab1;

import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;
import hr.fer.apuw.lab1.repository.PostRepository;
import hr.fer.apuw.lab1.repository.UserRepository;
import hr.fer.apuw.lab1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab1Application implements CommandLineRunner {

	private final PostRepository postRepository;

	private final UserRepository userRepository;

	@Autowired
	public Lab1Application(UserRepository userRepository, PostRepository postRepository){
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(Lab1Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		User user1 = new User(1L, "dsmoljan", "dorian.smoljan@gmail.com", "password", "Dorian", "Smoljan");
		User user2 = new User(2L, "ihorvat", "ivan.horvat@gmail.com", "password", "Ivan", "Horvat");

		userRepository.save(user1);
		userRepository.save(user2);

		Post post1 = new Post(1L, "This is some content from dsmoljan", null, user1);
		Post post2 = new Post(2L, "This is some content from ihorvat", null, user2);

		postRepository.save(post1);
		postRepository.save(post2);

	}
}
