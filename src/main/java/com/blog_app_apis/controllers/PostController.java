package com.blog_app_apis.controllers;

import com.blog_app_apis.dtos.ApiResponse;
import com.blog_app_apis.dtos.PostDTO;
import com.blog_app_apis.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class PostController {

    @Autowired
    private PostService postService;

    //======================================= Create Post==============================================

    @PostMapping("/user/{userId}/category/{categoryId}/posts")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO,
                                              @PathVariable Integer userId,
                                              @PathVariable Integer categoryId) {

        PostDTO createPost = this.postService.createPost(postDTO, userId, categoryId);
        return new ResponseEntity<PostDTO>(createPost, HttpStatus.CREATED);
    }
    //===================================== Get Post By Category ======================================

    @GetMapping("/category/{categoryId}/posts")
    public ResponseEntity<List<PostDTO>> getPostByCategory(@PathVariable Integer categoryId) {
        List<PostDTO> posts = this.postService.getPostByCategory(categoryId);
        return new ResponseEntity<List<PostDTO>>(posts, HttpStatus.OK);
    }
    //======================================= Get Post By User =======x=================================

    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<List<PostDTO>> getPostByUser(@PathVariable Integer userId) {
        List<PostDTO> posts = this.postService.getPostByUser(userId);
        return new ResponseEntity<List<PostDTO>>(posts, HttpStatus.OK);
    }

    //======================================= Update Post ===========================================

    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDTO> UpdatePost(@RequestBody PostDTO postDto, @PathVariable Integer postId) {
        PostDTO updatePost = this.postService.updatePost(postDto, postId);
        return new ResponseEntity<PostDTO>(updatePost, HttpStatus.OK);
    }

    //======================================= Get Post By Id ==========================================
    @GetMapping("/GetPost/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Integer postId) {
        return ResponseEntity.ok(this.postService.getPostById(postId));
    }
    //======================================= Delete Post By Id ========================================

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Integer postId) {
        this.postService.deletePost(postId);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Post Deleted Successfully !!", true), HttpStatus.OK);
    }
    //=======================================Search=================================================

    @GetMapping("/post/search/{keywords}")
    public ResponseEntity<List<PostDTO>> searchPostByTitile(@PathVariable("keywords") String keywords) {
        List<PostDTO> result = this.postService.searchPosts(keywords);
        return new ResponseEntity<List<PostDTO>>(result, HttpStatus.OK);
    }

}
