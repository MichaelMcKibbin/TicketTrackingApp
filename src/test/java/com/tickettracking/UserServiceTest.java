package com.tickettracking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void defaultUsers_areInitializedCorrectly() {
        List<User> users = userService.getAllUsers();
        assertEquals(4, users.size(), "Expected 4 default users");

        // Check that the expected usernames exist
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("admin")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("support1")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("support2")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("customer1")));
    }

    @Test
    void currentUser_defaultsToSupport1() {
        User current = userService.getCurrentUser();
        assertNotNull(current);
        assertEquals("support1", current.getUsername());
        assertEquals(User.Role.SUPPORT_STAFF, current.getRole());
    }

    @Test
    void getUsernames_returnsListOfUsernames() {
        List<String> usernames = userService.getUsernames();
        assertTrue(usernames.contains("admin"));
        assertTrue(usernames.contains("support1"));
        assertTrue(usernames.contains("support2"));
        assertTrue(usernames.contains("customer1"));
    }

    @Test
    void findUserByUsername_returnsCorrectUser() {
        User admin = userService.findUserByUsername("admin");
        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());
        assertEquals(User.Role.ADMIN, admin.getRole());

        User unknown = userService.findUserByUsername("does-not-exist");
        assertNull(unknown);
    }

    @Test
    void setCurrentUser_changesCurrentUser() {
        User customer = userService.findUserByUsername("customer1");
        assertNotNull(customer);

        userService.setCurrentUser(customer);

        User current = userService.getCurrentUser();
        assertEquals("customer1", current.getUsername());
        assertEquals(User.Role.CUSTOMER, current.getRole());
    }
}
