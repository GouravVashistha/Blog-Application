package com.blog_app_apis.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CategroyDTO {
    private Integer categoryId;
    @NotEmpty
    @Size(min = 4, message = "Category Title must be min  of 4 Character")
    private String categoryTitle;

    @NotEmpty
    @Size(min = 10, message = "Category Description must be min  of 4 Character")
    private String categoryDecription;
}
