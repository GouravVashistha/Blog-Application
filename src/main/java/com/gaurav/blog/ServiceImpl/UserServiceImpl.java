package com.gaurav.blog.ServiceImpl;

import com.gaurav.blog.DTO.UserDto;
import com.gaurav.blog.Entity.User;
import com.gaurav.blog.Repository.UserRepo;
import com.gaurav.blog.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDto createUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        return null;
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return null;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return List.of();
    }

    @Override
    public void deleteUser(Integer userId) {

    }

    @Override
    public Optional<User> fetchUserByEmailId(String tempEmail) {
        return Optional.empty();
    }
}
