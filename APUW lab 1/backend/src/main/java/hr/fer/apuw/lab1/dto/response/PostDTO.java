package hr.fer.apuw.lab1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

  private Long id;

  private String content;

  private String image;

  private Long authorId;
}
