package com.stockmanage.automobile.service;

import com.stockmanage.automobile.dto.UserDTO;
import com.stockmanage.automobile.model.User;

import java.util.List;

public interface UserService {
    
    List<UserDTO> getAllUsers();
    
    UserDTO getUserById(Long id);
    
    UserDTO getUserByUsername(String username);
    
    UserDTO getUserByEmail(String email);
    
    UserDTO createUser(UserDTO userDTO);
    
    UserDTO updateUser(Long id, UserDTO userDTO);
    
    void deleteUser(Long id);
    
    List<UserDTO> getUsersByRole(User.Role role);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
} 