package hr.fer.apuw.lab1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MethodEntityDTO {

  private String httpMethod;

  private String url;

  private String description;

}
