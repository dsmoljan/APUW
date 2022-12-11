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
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Lab1Application implements CommandLineRunner {

	private final PostRepository postRepository;

	private final UserRepository userRepository;

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

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

		//password je "password"
		User user1 = new User(1L, "dsmoljan", "dorian.smoljan@gmail.com", "$2a$10$r45WPfYbpxDtpaVs4jlEeOxxnW9/KPQquQkkM98HOP5FekWg30MDu", "Dorian", "Smoljan");
		//password je "password"
		User user2 = new User(2L, "ihorvat", "ivan.horvat@gmail.com", "$2a$10$/.TKSBlRGoh2eZkrCcpPCe/rMxg1HfhgtoeBnnCIiNWagJ2AIRqJa", "Ivan", "Horvat");

		userRepository.save(user1);
		userRepository.save(user2);

		Post post1 = new Post(1L, "This is some content from dsmoljan", null, user1);
		Post post2 = new Post(2L, "This is some content from ihorvat", null, user2);

		postRepository.save(post1);
		postRepository.save(post2);

	}
}
