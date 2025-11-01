package com.supplychainx.service_user.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.UserRepository;
import com.supplychainx.util.PasswordUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements GenericService <User, Long>{
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        // hash the password before saving
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found " + id));
    }

    @Override
    public User update(User user) {
        if (userRepository.findById(user.getUserId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found " + user.getUserId());
        }
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("User not found" + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return users;
    }
}
