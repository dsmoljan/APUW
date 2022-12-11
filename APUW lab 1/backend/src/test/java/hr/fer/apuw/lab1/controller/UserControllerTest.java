package hr.fer.apuw.lab1.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  @Autowired
  private MessageSource messageSource;

  private MockMvc mvc;

  private ModelMapper modelMapper;

  private ObjectMapper objectMapper;

  private UserController userController;

  @Mock
  private UserServiceImpl userService;

  @Mock
  private PostService postService;

  private JacksonTester<UserDetailsDTO> jsonUserDetailsResponse;

  private JacksonTester<List<UserDetailsDTO>> jsonUserDetailsListResponse;


  @Before
  public void setup() {
    modelMapper = new ModelMapper();
    objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, new ObjectMapper());
    this.userController = new UserController(userService, postService, modelMapper);
    mvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new ControllerExceptionAdvice(messageSource)).build();
  }


  @Test
  public void testCanRetrieveAllWhenExists() throws Exception{
    User mockUser = UserGeneratingUtil.createMockUser();

    List<User> allUsers = List.of(mockUser);

    when(userService.getAllUsers()).thenReturn(List.of(mockUser));

    mvc.perform(get("/users").
            accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonUserDetailsListResponse.write(allUsers.stream().map(u -> modelMapper.map(u, UserDetailsDTO.class)).collect(Collectors.toList())).getJson()));
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

  @Test
  public void testGetUserThrowsWhenNotExist() throws Exception{
    User mockUser = UserGeneratingUtil.createMockUser();

    when(userService.getUserById(mockUser.getId())).thenThrow(new EntityNotFoundException("User with the given id was not found!"));

    mvc.perform(get("/users/" + mockUser.getId().toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testGetUserInvalidParameter() throws Exception{
    mvc.perform(get("/users/" + "somerandomuser")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testRegisterNewUserOK() throws Exception {
    User mockUser = UserGeneratingUtil.createMockUser();
    final Map<String, String> requestMap = new HashMap<>();

    requestMap.put("username", mockUser.getUsername());
    requestMap.put("email", mockUser.getEmail());
    requestMap.put("firstName", mockUser.getFirstName());
    requestMap.put("lastName", mockUser.getLastName());
    requestMap.put("password", mockUser.getPassword());

    when(userService.createUser(any(User.class))).thenReturn(mockUser);

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestMap)))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonUserDetailsResponse.write(modelMapper.map(mockUser, UserDetailsDTO.class)).getJson()));
  }

  @Test
  public void testRegisterNewUserFailValidation() throws Exception {
    User mockUser = UserGeneratingUtil.createMockUser();
    final Map<String, String> requestMap = new HashMap<>();

    requestMap.put("username", mockUser.getUsername());
    //purposely leave out email
    //requestMap.put("email", mockUser.getEmail());
    requestMap.put("firstName", mockUser.getFirstName());
    requestMap.put("lastName", mockUser.getLastName());
    requestMap.put("password", mockUser.getPassword());

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestMap)))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void testUpdateUserOK() throws Exception {
    User mockUser = UserGeneratingUtil.createMockUser();
    final Map<String, String> requestMap = new HashMap<>();

    requestMap.put("username", mockUser.getUsername());
    requestMap.put("email", mockUser.getEmail());
    requestMap.put("firstName", mockUser.getFirstName());
    requestMap.put("lastName", mockUser.getLastName());
    requestMap.put("password", mockUser.getPassword());

    when(userService.updateUser(longThat(l -> l.equals(mockUser.getId())), any(User.class))).thenReturn(mockUser);

    mvc.perform(put("/users/" + mockUser.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestMap)))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonUserDetailsResponse.write(modelMapper.map(mockUser, UserDetailsDTO.class)).getJson()));
  }

  @Test
  public void testUpdateUserNotExist() throws Exception {
    User mockUser = UserGeneratingUtil.createMockUser();
    final Map<String, String> requestMap = new HashMap<>();

    requestMap.put("username", mockUser.getUsername());
    requestMap.put("email", mockUser.getEmail());
    requestMap.put("firstName", mockUser.getFirstName());
    requestMap.put("lastName", mockUser.getLastName());
    requestMap.put("password", mockUser.getPassword());

    when(userService.updateUser(longThat(l -> l.equals(mockUser.getId())), any(User.class))).thenThrow(new EntityNotFoundException("User with the given id was not found!"));

    mvc.perform(put("/users/" + mockUser.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestMap)))
        .andExpect(status().isNotFound());
  }


  @Test
  public void testDeleteOK() throws Exception {
    User mockUser = UserGeneratingUtil.createMockUser();

    mvc.perform(delete("/users/" + mockUser.getId().toString()).
            contentType(MediaType.APPLICATION_JSON)).
        andExpect(status().isOk());
  }

  @Test
  public void testDeleteUserNotExist() throws Exception {
    User mockUser = UserGeneratingUtil.createMockUser();

    doThrow(new EntityNotFoundException("User with the given id was not found!")).when(userService).deleteUser(mockUser.getId());

    mvc.perform(delete("/users/" + mockUser.getId().toString()).
            contentType(MediaType.APPLICATION_JSON)).
        andExpect(status().isNotFound());

  }







}
