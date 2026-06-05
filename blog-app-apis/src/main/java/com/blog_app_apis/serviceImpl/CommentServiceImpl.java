package com.blog_app_apis.serviceImpl;

import com.blog_app_apis.Entity.Comment;
import com.blog_app_apis.Entity.Post;
import com.blog_app_apis.Entity.User;
import com.blog_app_apis.dtos.CommentDTO;
import com.blog_app_apis.exceptions.ResourceNotFoundException;
import com.blog_app_apis.repository.CommentRepo;
import com.blog_app_apis.repository.PostRepo;
import com.blog_app_apis.repository.UserRepo;
import com.blog_app_apis.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CommentDTO createComment(CommentDTO commentDTO, Integer postId, Integer userId) {
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "PostId", postId));

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));

        Comment comment = this.modelMapper.map(commentDTO, Comment.class);
        comment.setPost(post);
        comment.setUser(user);

        Comment saveComment = this.commentRepo.save(comment);
        return this.modelMapper.map(saveComment, CommentDTO.class);
    }

    @Override
    public void deleteComment(Integer commentId) {
        Comment com = this.commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "commentId", commentId));

        this.commentRepo.delete(com);
    }
}
