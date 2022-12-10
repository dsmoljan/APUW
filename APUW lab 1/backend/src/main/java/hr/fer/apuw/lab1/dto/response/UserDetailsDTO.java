package hr.fer.apuw.lab1.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {

  private Long id;

  private String username;

  private String email;

  private String firstName;

  private String lastName;
}
