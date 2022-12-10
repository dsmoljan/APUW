package hr.fer.apuw.lab1.model;

//strasno, javax se po novom zove jakarta
//https://stackoverflow.com/questions/60021815/why-has-javax-persistence-api-been-replaced-by-jakarta-persistence-api-in-spring

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(unique = true)
  @Size(max=64)
  private String username;

  @NotNull
  @Column(unique = true)
  @Size(max=64)
  private String email;

  @NotNull
  @Size(max=64)
  private String password;

  @NotNull
  private String firstName;

  @NotNull
  private String lastName;
}

