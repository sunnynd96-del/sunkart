package com.sunKart.service;

import com.sunKart.model.Role;
import com.sunKart.model.User;
import com.sunKart.repository.RoleRepository;
import com.sunKart.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepo;
    private RoleRepository roleRepo;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String password) {

        if (userRepo.existsByUsername(username)) {
            return null; // username already exists
        }

        User user = new User(username, passwordEncoder.encode(password), email);

        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_USER");
                    return roleRepo.save(newRole);
                });
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepo.save(user);
    }

    public User registerAdmin(String username, String email, String password) {

        if (userRepo.existsByUsername(username)) {
            return null;
        }

        User admin = new User(username, passwordEncoder.encode(password), email);

        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_ADMIN");
                    return roleRepo.save(newRole);
                });
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);

        return userRepo.save(admin);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    
    public void saveUser(User user) {
        userRepo.save(user);
    }
}
