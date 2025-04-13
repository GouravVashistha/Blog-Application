package com.gaurav.blog.Service;

import com.gaurav.blog.DTO.UserDto;
import com.gaurav.blog.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto,Integer userId);
    UserDto getUserById(Integer userId);
    List<UserDto> getAllUsers();
    void deleteUser(Integer userId);
    Optional<User> fetchUserByEmailId(String tempEmail);
}
