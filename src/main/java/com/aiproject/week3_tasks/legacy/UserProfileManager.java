package com.aiproject.week3_tasks.legacy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class UserProfileManager {
    private final Map<String, String> userProfiles = new HashMap<>();

    public boolean addUser(String username, String email) {
        if (username == null || username.isEmpty() || email == null || email.isEmpty()) {
            return false;
        }
        if (userProfiles.containsKey(username)) {
            return false;
        }
        userProfiles.put(username, email);
        return true;
    }

    public String getEmail(String username) {
        return userProfiles.get(username);
    }

    public boolean updateEmail(String username, String newEmail) {
        if (!userProfiles.containsKey(username) || newEmail == null || newEmail.isEmpty()) {
            return false;
        }
        userProfiles.put(username, newEmail);
        return true;
    }

    public boolean removeUser(String username) {
        if (!userProfiles.containsKey(username)) {
            return false;
        }
        userProfiles.remove(username);
        return true;
    }

    public int getUserCount() {
        return userProfiles.size();
    }

    public Set<String> getAllUsernames() {
        return userProfiles.keySet();
    }
} 