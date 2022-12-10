package hr.fer.apuw.lab1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String content;


  //TODO: promijeni u LOB/BLOB sto god ces koristiti
  //https://www.baeldung.com/java-db-storing-files, al googlaj jos kako pohranjivati slike u springu/bazi
  //base64 encoded
  @Lob
  private String image;

  // ManyToOne == jedan user može imati više postova, ali jedan post ima samo jednog autora
  @ManyToOne
  private User author;
}
