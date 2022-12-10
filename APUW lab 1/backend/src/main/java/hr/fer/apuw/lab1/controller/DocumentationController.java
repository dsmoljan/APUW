package hr.fer.apuw.lab1.controller;

import hr.fer.apuw.lab1.dto.response.MethodEntityDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller's goal is to return appropriate documentation about this web service
 */
@RestController
@RequestMapping("/documentation")
public class DocumentationController {

  // mozda ti je bolje za dokumentaciju koristiti nešto poput https://www.baeldung.com/spring-rest-openapi-documentation
  // dakle, kombinirati taj spring library s swaggerom, pa samo još vidi mogu li se i kako opisi dodavati


  @GetMapping
  public List<MethodEntityDTO> getDocumentation() {
    return null;
  }


  private List<MethodEntityDTO> getMethodsDescriptons(){
    List<MethodEntityDTO> methodEntityDTOList = new ArrayList<>();

    MethodEntityDTO getAllUsersMethodDTO = new MethodEntityDTO("GET", "/users", "Returns all registered users ids");

    return null;

  }




}
