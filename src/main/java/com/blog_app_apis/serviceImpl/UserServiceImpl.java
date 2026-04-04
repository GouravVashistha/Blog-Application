package com.blog_app_apis.serviceImpl;

import com.blog_app_apis.Entity.User;
import com.blog_app_apis.dtos.UserDTO;
import com.blog_app_apis.exceptions.InvalidMailException;
import com.blog_app_apis.exceptions.ResourceNotFoundException;
import com.blog_app_apis.repository.UserRepo;
import com.blog_app_apis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

        @Autowired
        private UserRepo userRepo;


        @Override
        public UserDTO createUser(UserDTO userDTO) throws InvalidMailException {

            User user =  this.dtoToUser(userDTO);
            try {
                User savedUser = userRepo.save(user);
                return userToDto(savedUser);
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
            return userToDto(userRepo.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidMailException("Email already in use");
        }
    }

        @Override
        public UserDTO getUserById(Integer userId) {
            User user = getUserOrThrow(userId);
            return this.userToDto(user);
        }

        @Override
        public List<UserDTO> getAllUsers() {
            List<User>  users = this.userRepo.findAll();
//            List<UserDto> UserDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());
//            return UserDtos;
            return users.stream().map(this::userToDto).collect(Collectors.toList());
        }

        @Override
        public void deleteUser(Integer userId) {
            User user = getUserOrThrow(userId);
            this.userRepo.delete(user);
        }

        private User dtoToUser(UserDTO UserDTO){
            User user = new User();
            user.setId(UserDTO.getId());
            user.setEmail(UserDTO.getEmail());
            user.setName(UserDTO.getName());
            user.setPassword(UserDTO.getPassword());
            user.setAbout(UserDTO.getAbout());
            return user;
        }

        public UserDTO userToDto(User user){
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setEmail(user.getEmail());
            userDTO.setName(user.getName());
            userDTO.setPassword(user.getPassword());
            userDTO.setAbout(user.getAbout());
            return userDTO;
        }

    private User getUserOrThrow(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
