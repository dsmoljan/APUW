package hr.fer.apuw.lab1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.fer.apuw.lab1.ControllerExceptionAdvice;
import hr.fer.apuw.lab1.dto.response.UserDetailsDTO;
import hr.fer.apuw.lab1.model.User;
import hr.fer.apuw.lab1.service.PostService;
import hr.fer.apuw.lab1.service.impl.UserServiceImpl;
import hr.fer.apuw.lab1.util.UserGeneratingUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  @Autowired
  private MessageSource messageSource;

  private MockMvc mvc;

  private ModelMapper modelMapper;

  private UserController userController;

  @Mock
  private UserServiceImpl userService;

  @Mock
  private PostService postService;

  private JacksonTester<UserDetailsDTO> jsonUserDetailsResponse;

  @Before
  public void setup() {
    modelMapper = new ModelMapper();
    JacksonTester.initFields(this, new ObjectMapper());
    this.userController = new UserController(userService, postService, modelMapper);
    mvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new ControllerExceptionAdvice(messageSource)).build();
  }


  @Test
  public void testCanRetrieveWhenExists() throws Exception {
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userService.getUserById(mockUser.getId())).thenReturn(mockUser);

    mvc.perform(get("/users/" + mockUser.getId().toString()).
        contentType(MediaType.APPLICATION_JSON)).
        andExpect(status().isOk())
        .andExpect(content().json(jsonUserDetailsResponse.write(modelMapper.map(mockUser, UserDetailsDTO.class)).getJson()));
  }





}
