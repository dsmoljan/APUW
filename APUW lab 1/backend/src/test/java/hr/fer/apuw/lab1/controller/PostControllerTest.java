package hr.fer.apuw.lab1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.fer.apuw.lab1.ControllerExceptionAdvice;
import hr.fer.apuw.lab1.dto.request.CreatePostDTO;
import hr.fer.apuw.lab1.dto.request.UpdatePostDTO;
import hr.fer.apuw.lab1.dto.response.PostDTO;
import hr.fer.apuw.lab1.dto.response.UserDetailsDTO;
import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;
import hr.fer.apuw.lab1.service.impl.PostServiceImpl;
import hr.fer.apuw.lab1.util.PostGeneratingUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

  @Autowired
  private MessageSource messageSource;

  private MockMvc mvc;

  private ModelMapper modelMapper;

  private ObjectMapper objectMapper;

  private PostController postController;

  @Mock
  private PostServiceImpl postService;

  private JacksonTester<PostDTO> jsonPostDetailsResponse;

  private JacksonTester<List<PostDTO>> jsonPostDetailsListResponse;

  @Before
  public void setup() {
    modelMapper = new ModelMapper();
    objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, new ObjectMapper());
    this.postController = new PostController(postService, modelMapper);
    mvc = MockMvcBuilders.standaloneSetup(postController)
        .setControllerAdvice(new ControllerExceptionAdvice(messageSource)).build();
  }

  @Test
  public void testCanRetrieveAllWhenExists() throws Exception{
    Post mockPost = PostGeneratingUtil.createMockPost();

    List<Post> allPosts = List.of(mockPost);

    when(postService.getAllPosts()).thenReturn(List.of(mockPost));

    mvc.perform(get("/posts").
            accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonPostDetailsListResponse.write(allPosts.stream().map(p -> modelMapper.map(p, PostDTO.class)).collect(Collectors.toList())).getJson()));
  }

  @Test
  public void testCanRetrieveWhenExists() throws Exception {
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postService.getPostById(mockPost.getId())).thenReturn(mockPost);

    mvc.perform(get("/posts/" + mockPost.getId().toString()).
            contentType(MediaType.APPLICATION_JSON)).
        andExpect(status().isOk())
        .andExpect(content().json(jsonPostDetailsResponse.write(modelMapper.map(mockPost, PostDTO.class)).getJson()));
  }

  @Test
  public void testGetPostThrowsWhenNotExists() throws Exception{
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postService.getPostById(mockPost.getId())).thenThrow(new EntityNotFoundException("Post with the given id was not found!"));

    mvc.perform(get("/posts/" + mockPost.getId().toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testCreateNewPostOK() throws Exception {
    Post mockPost = PostGeneratingUtil.createMockPost();
    final Map<String, String> requestMap = new HashMap<>();

    requestMap.put("content", mockPost.getContent());
    requestMap.put("image", mockPost.getImage());
    requestMap.put("authorId", mockPost.getAuthor().getId().toString());

    when(postService.createPost(any(CreatePostDTO.class))).thenReturn(mockPost);

    mvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestMap)))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonPostDetailsResponse.write(modelMapper.map(mockPost, PostDTO.class)).getJson()));
  }

  @Test
  public void testCreateNewPostInvalidUser() throws Exception {
    Post mockPost = PostGeneratingUtil.createMockPost();
    final Map<String, String> requestMap = new HashMap<>();

    requestMap.put("content", mockPost.getContent());
    requestMap.put("image", mockPost.getImage());
    requestMap.put("authorId", mockPost.getAuthor().getId().toString());

    when(postService.createPost(any(CreatePostDTO.class))).thenThrow(new EntityNotFoundException("User with given ID was not found!"));

    mvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestMap)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testUpdatePostOK() throws Exception {
    Post mockPost = PostGeneratingUtil.createMockPost();
    final Map<String, String> requestMap = new HashMap<>();

    requestMap.put("content", mockPost.getContent());
    requestMap.put("image", mockPost.getImage());
    requestMap.put("authorId", mockPost.getAuthor().getId().toString());

    when(postService.updatePost(longThat(l -> l.equals(mockPost.getId())), any(UpdatePostDTO.class))).thenReturn(mockPost);

    mvc.perform(put("/posts/" + mockPost.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestMap)))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonPostDetailsResponse.write(modelMapper.map(mockPost, PostDTO.class)).getJson()));
  }

  @Test
  public void testUpdatePostNotExist() throws Exception {
    Post mockPost = PostGeneratingUtil.createMockPost();
    final Map<String, String> requestMap = new HashMap<>();

    requestMap.put("content", mockPost.getContent());
    requestMap.put("image", mockPost.getImage());
    requestMap.put("authorId", mockPost.getAuthor().getId().toString());

    when(postService.updatePost(longThat(l -> l.equals(mockPost.getId())), any(UpdatePostDTO.class))).thenThrow(new EntityNotFoundException("Post with the given id was not found!"));

    mvc.perform(put("/posts/" + mockPost.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestMap)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testDeletePostOK() throws Exception {
    Post mockPost = PostGeneratingUtil.createMockPost();

    mvc.perform(delete("/posts/" + mockPost.getId().toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void testDeletePostNotExist() throws Exception {
    Post mockPost = PostGeneratingUtil.createMockPost();

    doThrow(new EntityNotFoundException("Post with the given id was not found!")).when(postService).deletePost(mockPost.getId());

    mvc.perform(delete("/posts/" + mockPost.getId().toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

  }

}
