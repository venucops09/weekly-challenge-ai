package com.aiproject.week3_tasks.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileManagerTest {
    private UserProfileManager manager;

    @BeforeEach
    void setUp() {
        manager = new UserProfileManager();
    }

    @Test
    @DisplayName("Add user with valid username and email")
    void testAddUserSuccess() {
        assertTrue(manager.addUser("alice", "alice@example.com"));
        assertEquals(1, manager.getUserCount());
    }

    @Test
    @DisplayName("Add user with null or empty username/email should fail")
    void testAddUserInvalidInput() {
        assertFalse(manager.addUser(null, "test@example.com"));
        assertFalse(manager.addUser("", "test@example.com"));
        assertFalse(manager.addUser("bob", null));
        assertFalse(manager.addUser("bob", ""));
    }

    @Test
    @DisplayName("Add duplicate user should fail")
    void testAddDuplicateUser() {
        assertTrue(manager.addUser("alice", "alice@example.com"));
        assertFalse(manager.addUser("alice", "alice2@example.com"));
    }

    @Test
    @DisplayName("Get email for existing and non-existing user")
    void testGetEmail() {
        manager.addUser("alice", "alice@example.com");
        assertEquals("alice@example.com", manager.getEmail("alice"));
        assertNull(manager.getEmail("bob"));
    }

    @Test
    @DisplayName("Update email for existing user")
    void testUpdateEmailSuccess() {
        manager.addUser("alice", "alice@example.com");
        assertTrue(manager.updateEmail("alice", "newalice@example.com"));
        assertEquals("newalice@example.com", manager.getEmail("alice"));
    }

    @Test
    @DisplayName("Update email for non-existing user or invalid email")
    void testUpdateEmailFailure() {
        assertFalse(manager.updateEmail("bob", "bob@example.com"));
        manager.addUser("alice", "alice@example.com");
        assertFalse(manager.updateEmail("alice", null));
        assertFalse(manager.updateEmail("alice", ""));
    }

    @Test
    @DisplayName("Remove user success and failure cases")
    void testRemoveUser() {
        manager.addUser("alice", "alice@example.com");
        assertTrue(manager.removeUser("alice"));
        assertFalse(manager.removeUser("alice"));
        assertFalse(manager.removeUser("bob"));
    }

    @Test
    @DisplayName("Get user count")
    void testGetUserCount() {
        assertEquals(0, manager.getUserCount());
        manager.addUser("alice", "alice@example.com");
        manager.addUser("bob", "bob@example.com");
        assertEquals(2, manager.getUserCount());
    }
} 