package com.stockmanage.automobile.service.impl;

import com.stockmanage.automobile.dto.UserDTO;
import com.stockmanage.automobile.model.User;
import com.stockmanage.automobile.repository.UserRepository;
import com.stockmanage.automobile.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }

        User user = convertToEntity(userDTO);
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Check if username is being changed and if it's already taken
        if (!user.getUsername().equals(userDTO.getUsername()) && 
                userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDTO.getUsername());
        }

        // Check if email is being changed and if it's already taken
        if (!user.getEmail().equals(userDTO.getEmail()) && 
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }

        updateUserFromDTO(user, userDTO);
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> getUsersByRole(User.Role role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    }

    private User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        // Password would typically be set here, possibly with encryption
        // This is simplified for the example
        user.setPassword("defaultPassword");
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        return user;
    }

    private void updateUserFromDTO(User user, UserDTO dto) {
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        // Password is not updated here for simplicity
    }
} 