package com.blog_app_apis.service;

import com.blog_app_apis.dtos.CommentDTO;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO, Integer postId, Integer userId);

    void deleteComment(Integer commentId);
}
