package com.aviral.finance_backend.service;

import com.aviral.finance_backend.exception.AccessDeniedException;
import com.aviral.finance_backend.exception.ResourceNotFoundException;
import com.aviral.finance_backend.model.User;
import com.aviral.finance_backend.model.enums.Role;
import com.aviral.finance_backend.model.enums.Status;
import com.aviral.finance_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers(User usr) {
        User user = validateUser(usr);
        checkAdmin(user);

        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser, User usr) {
        User user = validateUser(usr);
        checkAdmin(user);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        existing.setName(updatedUser.getName());
        existing.setEmail(updatedUser.getEmail());
        existing.setRole(updatedUser.getRole());
        existing.setStatus(updatedUser.getStatus());

        return userRepository.save(existing);
    }

    public void deleteUser(Long id, User usr) {
        User user = validateUser(usr);
        checkAdmin(user);

        if (id.equals(user.getId())) {
            throw new AccessDeniedException("Admin cannot delete themselves");
        }

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }

        userRepository.deleteById(id);
    }

    public User updateUserRole(Long id, Role role, User user) {

        User admin = validateUser(user);
        checkAdmin(admin);

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        targetUser.setRole(role);
        return userRepository.save(targetUser);
    }

    public User updateUserStatus(Long id, Status status, User user) {

        User admin = validateUser(user);
        checkAdmin(admin);

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        targetUser.setStatus(status);
        return userRepository.save(targetUser);
    }

    private User validateUser(User user) {
        if(user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        if(user.getStatus() == Status.INACTIVE) {
            throw new AccessDeniedException("User is inactive");
        }

        return user;
    }

    private void checkAdmin(User user) {
        if(user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Admin access required");
        }
    }
}
