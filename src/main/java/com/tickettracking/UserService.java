package com.tickettracking;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides services for managing users in the system.
 * This class supports operations such as retrieving all users, finding a user by username,
 * managing the current logged-in user, and initializing default users.
 */
public class UserService {
    private List<User> users;
    private User currentUser;

    public UserService() {
        this.users = new ArrayList<>();
        initializeDefaultUsers();
    }

    private void initializeDefaultUsers() {
        users.add(new User("admin", "admin", User.Role.ADMIN));
        users.add(new User("support1", "support1", User.Role.SUPPORT_STAFF));
        users.add(new User("support2", "support2", User.Role.SUPPORT_STAFF));
        users.add(new User("customer1", "customer1", User.Role.CUSTOMER));
        
        // Set default current user
        currentUser = users.get(1); // support1
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public List<String> getUsernames() {
        return users.stream().map(User::getUsername).toList();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User findUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}