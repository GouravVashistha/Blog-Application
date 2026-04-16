package com.blog_app_apis.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PostDTO {
    private Integer postId;
    private String title;

    private String content;

    private String imageName;

    private Date date;

    private CategroyDTO category;

    private UserDTO user;

    private Set<CommentDTO> comments = new HashSet<>();
}
