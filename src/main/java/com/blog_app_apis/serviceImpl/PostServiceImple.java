package com.blog_app_apis.serviceImpl;

import com.blog_app_apis.Entity.Category;
import com.blog_app_apis.Entity.Post;
import com.blog_app_apis.Entity.User;
import com.blog_app_apis.dtos.PostDTO;
import com.blog_app_apis.dtos.PostResponce;
import com.blog_app_apis.exceptions.ResourceNotFoundException;
import com.blog_app_apis.repository.CategoryRepository;
import com.blog_app_apis.repository.PostRepo;
import com.blog_app_apis.repository.UserRepo;
import com.blog_app_apis.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PostServiceImple implements PostService {
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public PostDTO createPost(PostDTO postDto, Integer userId, Integer categoryId) {

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Post post = this.modelMapper.map(postDto, Post.class);
        post.setImageName("default.png");
        post.setAddDate(new Date());
        post.setUser(user);
        post.setCategory(category);

        Post newPost = this.postRepo.save(post);
        return this.modelMapper.map(newPost, PostDTO.class);
    }
    //===================================== UpDate Post=================================================

    @Override
    public PostDTO updatePost(PostDTO postDto, Integer postId) {

        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setImageName(postDto.getImageName());

        // now we save all in post-repository
        Post updatepost = this.postRepo.save(post);
        return this.modelMapper.map(updatepost, PostDTO.class);
    }

    @Override
    public String deletePost(Integer postId) {
        return "";
    }

    @Override
    public PostResponce getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        return null;
    }

    @Override
    public PostDTO getPostById(Integer postId) {
        return null;
    }

    @Override
    public List<PostDTO> getPostByCategory(Integer categoryId) {
        return List.of();
    }

    @Override
    public List<PostDTO> getPostByUser(Integer userId) {
        return List.of();
    }

    @Override
    public List<PostDTO> searchPosts(String keyword) {
        return List.of();
    }
}
