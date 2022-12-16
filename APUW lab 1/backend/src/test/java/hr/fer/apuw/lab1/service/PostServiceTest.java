package hr.fer.apuw.lab1.service;

import hr.fer.apuw.lab1.dto.request.UpdatePostDTO;
import hr.fer.apuw.lab1.model.Post;
import hr.fer.apuw.lab1.model.User;
import hr.fer.apuw.lab1.repository.PostRepository;
import hr.fer.apuw.lab1.repository.UserRepository;
import hr.fer.apuw.lab1.service.impl.PostServiceImpl;
import hr.fer.apuw.lab1.util.PostGeneratingUtil;
import hr.fer.apuw.lab1.util.UserGeneratingUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

  private PostService postService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PostRepository postRepository;

  @Before
  public void setup(){
    this.postService = new PostServiceImpl(postRepository, userRepository);
  }

  @Test
  public void testGetOnePostOKParams(){
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
    Post foundPost = postService.getPostById(mockPost.getId());

    verify(postRepository, times(1)).findById(mockPost.getId());

    Assert.assertEquals(mockPost, foundPost);
  }

  @Test
  public void testGetOnePostNotExist(){
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postRepository.findById(mockPost.getId())).thenReturn(Optional.empty());

    Assert.assertThrows(EntityNotFoundException.class, () -> postService.getPostById(mockPost.getId()));

    verify(postRepository, times(1)).findById(mockPost.getId());
  }

  @Test
  public void testGetAllPosts(){
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postRepository.findAll()).thenReturn(List.of(mockPost));

    List<Post> foundPosts = postService.getAllPosts();

    verify(postRepository, times(1)).findAll();

    Assert.assertEquals(foundPosts, List.of(mockPost));
  }

  @Test
  public void testCanCreatePostOkParams(){
    User mockuser = UserGeneratingUtil.createMockUser();
    Post mockPost = PostGeneratingUtil.createMockPost();
    mockPost.setAuthor(mockuser);

    when(userRepository.findById(mockuser.getId())).thenReturn(Optional.of(mockuser));
    when(postRepository.save(any(Post.class))).thenReturn(mockPost);

    Post createdPost = postService.createPost(PostGeneratingUtil.createCreatePostDTO());
    Assert.assertEquals(mockPost, createdPost);

    verify(userRepository, times(1)).findById(mockuser.getId());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  public void testCreatePostAuthorNotExist(){
    User mockuser = UserGeneratingUtil.createMockUser();
    Post mockPost = PostGeneratingUtil.createMockPost();
    mockPost.setAuthor(mockuser);

    when(userRepository.findById(mockuser.getId())).thenReturn(Optional.empty());
    Assert.assertThrows(EntityNotFoundException.class, () -> postService.createPost(PostGeneratingUtil.createCreatePostDTO()));

    verify(userRepository, times(1)).findById(mockuser.getId());
    verify(postRepository, times(0)).save(any(Post.class));
  }

  @Test
  public void testUpdatePostOKParams(){
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
    when(postRepository.save(mockPost)).thenReturn(mockPost);

    Post updatedPost = postService.updatePost(mockPost.getId(), PostGeneratingUtil.createUpdatePostDTO());

    Assert.assertEquals(updatedPost, mockPost);

    verify(postRepository, times(1)).findById(any(Long.class));
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  public void testUpdatePostPostNotExist(){
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postRepository.findById(mockPost.getId())).thenReturn(Optional.empty());
    Assert.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(mockPost.getId(), PostGeneratingUtil.createUpdatePostDTO()));

    verify(postRepository, times(1)).findById(any(Long.class));
    verify(postRepository, times(0)).save(any(Post.class));
  }

  @Test
  public void testDeletePostOKParams(){
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

    postService.deletePost(mockPost.getId());

    verify(postRepository, times(1)).findById(any(Long.class));
    verify(postRepository, times(1)).deleteById(mockPost.getId());
  }

  @Test
  public void testDeletePostNotExist(){
    Post mockPost = PostGeneratingUtil.createMockPost();

    when(postRepository.findById(mockPost.getId())).thenReturn(Optional.empty());

    Assert.assertThrows(EntityNotFoundException.class, () -> postService.deletePost(mockPost.getId()));

    verify(postRepository, times(1)).findById(any(Long.class));
    verify(postRepository, times(0)).deleteById(mockPost.getId());
  }

}
