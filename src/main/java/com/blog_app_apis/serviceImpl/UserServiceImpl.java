package com.blog_app_apis.serviceImpl;

import com.blog_app_apis.Entity.User;
import com.blog_app_apis.dtos.UserDTO;
import com.blog_app_apis.exceptions.InvalidMailException;
import com.blog_app_apis.exceptions.ResourceNotFoundException;
import com.blog_app_apis.repository.UserRepo;
import com.blog_app_apis.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO createUser(UserDTO userDTO) throws InvalidMailException {

        User user = modelMapper.map(userDTO, User.class);
        try {
            User savedUser = userRepo.save(user);
            return modelMapper.map(user, UserDTO.class);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidMailException("Email already exists");
        }
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, Integer userId) throws InvalidMailException {

        if (userRepo.findByEmail(userDTO.getEmail())
                .filter(u -> u.getId() != userId)
                .isPresent()) {
            throw new InvalidMailException("Email already in use");
        }

        User user = getUserOrThrow(userId);

        if (userDTO.getName() != null)
            user.setName(userDTO.getName());

        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());

        if (userDTO.getPassword() != null)
            user.setPassword(userDTO.getPassword());

        if (userDTO.getAbout() != null)
            user.setAbout(userDTO.getAbout());

        try {
            User savedUser = userRepo.save(user);
            return modelMapper.map(savedUser, UserDTO.class);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidMailException("Email already in use");
        }
    }

    @Override
    public UserDTO getUserById(Integer userId) {
        User user = getUserOrThrow(userId);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .toList(); // Java 16+
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = getUserOrThrow(userId);
        this.userRepo.delete(user);
    }


    private User getUserOrThrow(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
