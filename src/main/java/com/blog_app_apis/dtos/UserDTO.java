package com.blog_app_apis.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"id", "name", "email", "password", "about"})
public class UserDTO {
    @NotEmpty
    @Size(min = 4, message = "User must be min  of 4 Character")
    private String name;
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    @Size(min = 3, max = 10, message = "Password must be at least 3 digits and max is 10..!!")
    private String password;
    @NotEmpty
    private String about;
}
