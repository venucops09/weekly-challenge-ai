package com.aiproject.week3_tasks.service;

import com.aiproject.week3_tasks.legacy.UserProfileManager;
import com.aiproject.week3_tasks.model.UserProfile;
import com.aiproject.week3_tasks.repository.UserProfileRepository;
import com.aiproject.week3_tasks.exception.UserNotFoundException;
import com.aiproject.week3_tasks.exception.InvalidUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * UserProfileService
 *
 * <b>Overview:</b>
 * <p>
 * The UserProfileService is a Spring-managed service responsible for managing user profiles in the application.
 * It provides CRUD operations, validation, import/export, batch utilities, and comprehensive reporting for user data.
 * </p>
 *
 * <b>Key Features:</b>
 * <ul>
 *   <li>Create, read, update, and delete user profiles</li>
 *   <li>Input validation for usernames and emails</li>
 *   <li>Bulk import from legacy systems</li>
 *   <li>Export user data to CSV</li>
 *   <li>Batch operations (deactivate/reactivate, domain-based actions)</li>
 *   <li>Comprehensive user analytics and reporting</li>
 * </ul>
 *
 * <b>Usage Patterns:</b>
 * <pre>
 * // Typical usage in a controller
 * @Autowired
 * private UserProfileService userProfileService;
 *
 * UserProfile user = userProfileService.createUser("jdoe", "jdoe@example.com", "John", "Doe");
 * UserProfile fetched = userProfileService.getUser("jdoe");
 * userProfileService.updateEmail("jdoe", "john.doe@newmail.com");
 * String csv = userProfileService.exportToCsv();
 * String report = userProfileService.generateComprehensiveUserReport(30);
 * </pre>
 *
 * <b>Dependencies:</b>
 * <ul>
 *   <li>{@link com.aiproject.week3_tasks.repository.UserProfileRepository}</li>
 *   <li>{@link com.aiproject.week3_tasks.legacy.UserProfileManager}</li>
 *   <li>{@link com.aiproject.week3_tasks.model.UserProfile}</li>
 *   <li>{@link com.aiproject.week3_tasks.exception.UserNotFoundException}</li>
 *   <li>{@link com.aiproject.week3_tasks.exception.InvalidUserException}</li>
 *   <li>Spring Framework (Service, Autowired, etc.)</li>
 * </ul>
 *
 * <b>Examples:</b>
 * <pre>
 * // Basic creation
 * userProfileService.createUser("alice", "alice@mail.com", "Alice", "Smith");
 *
 * // Batch deactivate users by domain
 * int deactivated = userProfileService.deactivateUsersByDomain("example.com");
 *
 * // Generate a comprehensive report
 * String report = userProfileService.generateComprehensiveUserReport(7);
 * </pre>
 *
 * <b>API Reference:</b>
 * <ul>
 *   <li>UserProfile createUser(String username, String email, String firstName, String lastName)</li>
 *   <li>UserProfile getUser(String username)</li>
 *   <li>UserProfile updateEmail(String username, String newEmail)</li>
 *   <li>UserProfile updateName(String username, String firstName, String lastName)</li>
 *   <li>void deleteUser(String username)</li>
 *   <li>List&lt;UserProfile&gt; listAllUsers()</li>
 *   <li>List&lt;UserProfile&gt; findUsersByDomain(String domain)</li>
 *   <li>int importFromLegacy()</li>
 *   <li>String exportToCsv()</li>
 *   <li>void deactivateUser(String username)</li>
 *   <li>void reactivateUser(String username)</li>
 *   <li>long countActiveUsers()</li>
 *   <li>long countInactiveUsers()</li>
 *   <li>List&lt;UserProfile&gt; findUsersByName(String namePart)</li>
 *   <li>int deactivateUsersByDomain(String domain)</li>
 *   <li>Map&lt;String, Long&gt; getUserCountByDomain()</li>
 *   <li>String generateComprehensiveUserReport(int daysRecent)</li>
 * </ul>
 */
@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    @Autowired
    private UserProfileRepository repository;
    @Autowired
    private UserProfileManager legacyManager;


    /**
     * Create a new user profile.
     */
    public UserProfile createUser(String username, String email, String firstName, String lastName) {
        logger.info("Creating user: {}", username);
        validateUsername(username);
        validateEmail(email);

        if (repository.existsByUsername(username)) {
            logger.warn("Username already exists: {}", username);
            throw new InvalidUserException("Username already exists");
        }

        UserProfile user = new UserProfile(username, email, firstName, lastName);
        repository.save(user);
        logger.info("User created: {}", username);
        return user;
    }

    /**
     * Get a user profile by username.
     */
    public UserProfile getUser(String username) {
        logger.info("Fetching user: {}", username);
        return repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    /**
     * Update a user's email.
     */
    public UserProfile updateEmail(String username, String newEmail) {
        logger.info("Updating email for user: {}", username);
        validateEmail(newEmail);
        UserProfile user = getUser(username);
        user.setEmail(newEmail);
        repository.save(user);
        logger.info("Email updated for user: {}", username);
        return user;
    }

    /**
     * Update a user's name.
     */
    public UserProfile updateName(String username, String firstName, String lastName) {
        logger.info("Updating name for user: {}", username);
        UserProfile user = getUser(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        repository.save(user);
        logger.info("Name updated for user: {}", username);
        return user;
    }

    /**
     * Delete a user profile.
     */
    public void deleteUser(String username) {
        logger.info("Deleting user: {}", username);
        UserProfile user = getUser(username);
        repository.delete(user);
        logger.info("User deleted: {}", username);
    }

    /**
     * List all users.
     */
    public List<UserProfile> listAllUsers() {
        logger.info("Listing all users");
        return repository.findAll();
    }

    /**
     * Search users by email domain.
     */
    public List<UserProfile> findUsersByDomain(String domain) {
        logger.info("Searching users by domain: {}", domain);
        List<UserProfile> result = new ArrayList<>();
        for (UserProfile user : repository.findAll()) {
            if (user.getEmail() != null && user.getEmail().endsWith("@" + domain)) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * Validate username.
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty() || username.length() > 50) {
            logger.error("Invalid username: {}", username);
            throw new InvalidUserException("Invalid username");
        }
    }

    /**
     * Validate email.
     */
    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches() || email.length() > 100) {
            logger.error("Invalid email: {}", email);
            throw new InvalidUserException("Invalid email");
        }
    }

    /**
     * Bulk import users from legacy manager.
     */
    public int importFromLegacy() {
        logger.info("Importing users from legacy manager");
        int count = 0;
        for (String username : legacyManager.getAllUsernames()) {
            String email = legacyManager.getEmail(username);
            if (!repository.existsByUsername(username)) {
                UserProfile user = new UserProfile(username, email, null, null);
                repository.save(user);
                count++;
            }
        }
        logger.info("Imported {} users from legacy", count);
        return count;
    }

    /**
     * Export all users to a CSV string.
     */
    public String exportToCsv() {
        logger.info("Exporting users to CSV");
        StringBuilder sb = new StringBuilder();
        sb.append("username,email,firstName,lastName\n");
        for (UserProfile user : repository.findAll()) {
            sb.append(user.getUsername()).append(",")
              .append(user.getEmail()).append(",")
              .append(user.getFirstName() != null ? user.getFirstName() : "").append(",")
              .append(user.getLastName() != null ? user.getLastName() : "").append("\n");
        }
        return sb.toString();
    }

    // Additional utility and batch methods to reach 200+ lines
    /**
     * Deactivate a user profile.
     */
    public void deactivateUser(String username) {
        logger.info("Deactivating user: {}", username);
        UserProfile user = getUser(username);
        user.setActive(false);
        repository.save(user);
        logger.info("User deactivated: {}", username);
    }

    /**
     * Reactivate a user profile.
     */
    public void reactivateUser(String username) {
        logger.info("Reactivating user: {}", username);
        UserProfile user = getUser(username);
        user.setActive(true);
        repository.save(user);
        logger.info("User reactivated: {}", username);
    }

    /**
     * Count users by status.
     */
    public long countActiveUsers() {
        logger.info("Counting active users");
        return repository.findAll().stream().filter(UserProfile::isActive).count();
    }

    public long countInactiveUsers() {
        logger.info("Counting inactive users");
        return repository.findAll().stream().filter(u -> !u.isActive()).count();
    }

    /**
     * Find users by partial name match.
     */
    public List<UserProfile> findUsersByName(String namePart) {
        logger.info("Finding users by name part: {}", namePart);
        List<UserProfile> result = new ArrayList<>();
        for (UserProfile user : repository.findAll()) {
            String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
            if (fullName.contains(namePart.toLowerCase())) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * Batch deactivate users by domain.
     */
    public int deactivateUsersByDomain(String domain) {
        logger.info("Batch deactivating users by domain: {}", domain);
        int count = 0;
        for (UserProfile user : repository.findAll()) {
            if (user.getEmail() != null && user.getEmail().endsWith("@" + domain) && user.isActive()) {
                user.setActive(false);
                repository.save(user);
                count++;
            }
        }
        logger.info("Deactivated {} users for domain: {}", count, domain);
        return count;
    }

    /**
     * Get a map of domain to user count.
     */
    public Map<String, Long> getUserCountByDomain() {
        logger.info("Getting user count by domain");
        Map<String, Long> domainCount = new HashMap<>();
        for (UserProfile user : repository.findAll()) {
            if (user.getEmail() != null) {
                String[] parts = user.getEmail().split("@");
                if (parts.length == 2) {
                    domainCount.put(parts[1], domainCount.getOrDefault(parts[1], 0L) + 1);
                }
            }
        }
        return domainCount;
    }

    /**
     * Generates a comprehensive report of user statistics, including:
     * - Active/inactive counts
     * - Domain breakdown
     * - Top domains
     * - Users with missing information
     * - Users with duplicate emails
     * - Longest/shortest names
     * - Users created in the last N days
     * - Users with legacy data
     * - And more...
     *
     * @param daysRecent Number of days to consider for "recent" users
     * @return A formatted multi-section report as a String
     */
    public String generateComprehensiveUserReport(int daysRecent) {
        logger.info("Generating comprehensive user report for last {} days", daysRecent);
        List<UserProfile> allUsers = repository.findAll();

        int[] counts = countActiveInactiveUsers(allUsers);
        int activeCount = counts[0], inactiveCount = counts[1];

        Map<String, Long> domainCount = buildDomainCount(allUsers);
        List<Map.Entry<String, Long>> topDomains = new ArrayList<>(domainCount.entrySet());
        topDomains.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        Set<String> duplicateEmails = findDuplicateEmails(allUsers);
        List<UserProfile> missingInfo = findUsersWithMissingInfo(allUsers);
        List<UserProfile> recentUsers = findRecentUsers(allUsers, daysRecent);
        List<UserProfile> legacyUsers = findLegacyUsers(allUsers);
        UserProfile longestNameUser = findLongestNameUser(allUsers);
        UserProfile shortestNameUser = findShortestNameUser(allUsers);

        String report = formatUserReport(
            allUsers.size(), activeCount, inactiveCount, topDomains,
            missingInfo, duplicateEmails, recentUsers, daysRecent,
            legacyUsers, longestNameUser, shortestNameUser
        );

        logger.info("Comprehensive user report generated.");
        return report;
    }

    private int[] countActiveInactiveUsers(List<UserProfile> users) {
        int active = 0, inactive = 0;
        for (UserProfile user : users) {
            if (user.isActive()) active++;
            else inactive++;
        }
        return new int[]{active, inactive};
    }

    private Map<String, Long> buildDomainCount(List<UserProfile> users) {
        Map<String, Long> domainCount = new HashMap<>();
        for (UserProfile user : users) {
            if (user.getEmail() != null && user.getEmail().contains("@")) {
                String domain = user.getEmail().substring(user.getEmail().indexOf('@') + 1);
                domainCount.put(domain, domainCount.getOrDefault(domain, 0L) + 1);
            }
        }
        return domainCount;
    }

    private Set<String> findDuplicateEmails(List<UserProfile> users) {
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (UserProfile user : users) {
            String email = user.getEmail();
            if (email != null && !seen.add(email)) {
                duplicates.add(email);
            }
        }
        return duplicates;
    }

    private List<UserProfile> findUsersWithMissingInfo(List<UserProfile> users) {
        List<UserProfile> missing = new ArrayList<>();
        for (UserProfile user : users) {
            if (user.getFirstName() == null || user.getLastName() == null ||
                user.getFirstName().trim().isEmpty() || user.getLastName().trim().isEmpty()) {
                missing.add(user);
            }
        }
        return missing;
    }

    private List<UserProfile> findRecentUsers(List<UserProfile> users, int daysRecent) {
        List<UserProfile> recent = new ArrayList<>();
        Date now = new Date();
        long msInDay = 24 * 60 * 60 * 1000L;
        for (UserProfile user : users) {
            Date createdAt = null;
            try {
                createdAt = (Date) UserProfile.class.getMethod("getCreatedAt").invoke(user);
            } catch (Exception e) {
                // skip
            }
            if (createdAt != null && (now.getTime() - createdAt.getTime()) <= daysRecent * msInDay) {
                recent.add(user);
            }
        }
        return recent;
    }

    private List<UserProfile> findLegacyUsers(List<UserProfile> users) {
        List<UserProfile> legacy = new ArrayList<>();
        for (UserProfile user : users) {
            if (user.getUsername() != null && user.getUsername().startsWith("legacy_")) {
                legacy.add(user);
            }
        }
        return legacy;
    }

    private UserProfile findLongestNameUser(List<UserProfile> users) {
        UserProfile result = null;
        int maxLen = -1;
        for (UserProfile user : users) {
            String fullName = (user.getFirstName() != null ? user.getFirstName() : "") +
                              (user.getLastName() != null ? user.getLastName() : "");
            if (result == null || fullName.length() > maxLen) {
                result = user;
                maxLen = fullName.length();
            }
        }
        return result;
    }

    private UserProfile findShortestNameUser(List<UserProfile> users) {
        UserProfile result = null;
        int minLen = Integer.MAX_VALUE;
        for (UserProfile user : users) {
            String fullName = (user.getFirstName() != null ? user.getFirstName() : "") +
                              (user.getLastName() != null ? user.getLastName() : "");
            if (result == null || fullName.length() < minLen) {
                result = user;
                minLen = fullName.length();
            }
        }
        return result;
    }

    private String formatUserReport(
        int totalUsers, int activeCount, int inactiveCount,
        List<Map.Entry<String, Long>> topDomains,
        List<UserProfile> missingInfo, Set<String> duplicateEmails,
        List<UserProfile> recentUsers, int daysRecent,
        List<UserProfile> legacyUsers,
        UserProfile longestNameUser, UserProfile shortestNameUser
    ) {
        StringBuilder report = new StringBuilder();
        report.append("=== User Report ===\n");
        report.append("Total users: ").append(totalUsers).append("\n");
        report.append("Active: ").append(activeCount).append(", Inactive: ").append(inactiveCount).append("\n\n");
        report.append("Top 3 Domains:\n");
        for (int i = 0; i < Math.min(3, topDomains.size()); i++) {
            report.append("  ").append(topDomains.get(i).getKey())
                  .append(": ").append(topDomains.get(i).getValue()).append("\n");
        }
        report.append("\n");
        report.append("Users with missing info: ").append(missingInfo.size()).append("\n");
        for (UserProfile u : missingInfo) {
            report.append("  - ").append(u.getUsername()).append("\n");
        }
        report.append("\n");
        report.append("Duplicate emails: ").append(duplicateEmails.size()).append("\n");
        for (String email : duplicateEmails) {
            report.append("  - ").append(email).append("\n");
        }
        report.append("\n");
        report.append("Recent users (last ").append(daysRecent).append(" days): ").append(recentUsers.size()).append("\n");
        for (UserProfile u : recentUsers) {
            report.append("  - ").append(u.getUsername()).append("\n");
        }
        report.append("\n");
        report.append("Legacy users: ").append(legacyUsers.size()).append("\n");
        for (UserProfile u : legacyUsers) {
            report.append("  - ").append(u.getUsername()).append("\n");
        }
        report.append("\n");
        report.append("User with longest name: ")
              .append(longestNameUser != null ? longestNameUser.getUsername() : "N/A").append("\n");
        report.append("User with shortest name: ")
              .append(shortestNameUser != null ? shortestNameUser.getUsername() : "N/A").append("\n");
        return report.toString();
    }

    /**
     * Processes and analyzes all user data with a wide range of analytics, transformations, and reporting.
     * This method is intentionally complex and long for demonstration purposes.
     *
     * @return A detailed multi-section report as a String.
     */
    public String processAndAnalyzeAllUserData() {
        List<UserProfile> allUsers = repository.findAll();
        UserActivityAnalytics activity = analyzeUserActivity(allUsers);
        DomainAnalytics domain = analyzeDomains(allUsers);
        Set<String> duplicateEmails = findDuplicateEmails(allUsers);
        NameAnalytics name = analyzeNames(allUsers);
        UsernameAnalytics username = analyzeUsernames(allUsers);
        List<UserProfile> recentUsers = findRecentUsers(allUsers, 7);
        return formatAnalyticsReport(activity, domain, duplicateEmails, name, username, recentUsers);
    }

    /**
     * Analyzes user activity, counting active and inactive users and collecting lists of each.
     * @param users List of UserProfile objects
     * @return UserActivityAnalytics containing counts and lists
     */
    private UserActivityAnalytics analyzeUserActivity(List<UserProfile> users) {
        int active = 0, inactive = 0;
        List<UserProfile> activeUsers = new ArrayList<>();
        List<UserProfile> inactiveUsers = new ArrayList<>();
        for (UserProfile user : users) {
            if (user.isActive()) {
                active++;
                activeUsers.add(user);
            } else {
                inactive++;
                inactiveUsers.add(user);
            }
        }
        return new UserActivityAnalytics(active, inactive, activeUsers, inactiveUsers);
    }

    /**
     * Analyzes user email domains and activity by domain.
     * @param users List of UserProfile objects
     * @return DomainAnalytics with users grouped by domain and activity counts
     */
    private DomainAnalytics analyzeDomains(List<UserProfile> users) {
        Map<String, List<UserProfile>> usersByDomain = new HashMap<>();
        Map<String, Integer> domainActivity = new HashMap<>();
        for (UserProfile user : users) {
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                String domain = email.substring(email.indexOf('@') + 1);
                usersByDomain.computeIfAbsent(domain, k -> new ArrayList<>()).add(user);
                domainActivity.put(domain, domainActivity.getOrDefault(domain, 0) + (user.isActive() ? 1 : 0));
            }
        }
        return new DomainAnalytics(usersByDomain, domainActivity);
    }

    /**
     * Analyzes user names for length and palindromes.
     * @param users List of UserProfile objects
     * @return NameAnalytics with name length stats and palindrome users
     */
    private NameAnalytics analyzeNames(List<UserProfile> users) {
        int maxNameLength = 0, minNameLength = Integer.MAX_VALUE, palindromeCount = 0;
        List<UserProfile> palindromeUsers = new ArrayList<>();
        for (UserProfile user : users) {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String fullName = (firstName != null ? firstName : "") + (lastName != null ? lastName : "");
            maxNameLength = Math.max(maxNameLength, fullName.length());
            minNameLength = Math.min(minNameLength, fullName.length());
            String lowerFullName = fullName.toLowerCase();
            if (lowerFullName.equals(new StringBuilder(lowerFullName).reverse().toString()) && fullName.length() > 2) {
                palindromeUsers.add(user);
                palindromeCount++;
            }
        }
        return new NameAnalytics(maxNameLength, minNameLength, palindromeCount, palindromeUsers);
    }

    /**
     * Analyzes usernames for length statistics.
     * @param users List of UserProfile objects
     * @return UsernameAnalytics with username length stats
     */
    private UsernameAnalytics analyzeUsernames(List<UserProfile> users) {
        int maxUsernameLength = 0, minUsernameLength = Integer.MAX_VALUE;
        for (UserProfile user : users) {
            String username = user.getUsername();
            if (username != null) {
                maxUsernameLength = Math.max(maxUsernameLength, username.length());
                minUsernameLength = Math.min(minUsernameLength, username.length());
            }
        }
        return new UsernameAnalytics(maxUsernameLength, minUsernameLength);
    }

    /**
     * Formats the analytics report from all analytics objects.
     * @param activity UserActivityAnalytics
     * @param domain DomainAnalytics
     * @param duplicateEmails Set of duplicate emails
     * @param name NameAnalytics
     * @param username UsernameAnalytics
     * @param recentUsers List of recent users
     * @return Formatted report string
     */
    private String formatAnalyticsReport(
        UserActivityAnalytics activity,
        DomainAnalytics domain,
        Set<String> duplicateEmails,
        NameAnalytics name,
        UsernameAnalytics username,
        List<UserProfile> recentUsers
    ) {
        StringBuilder report = new StringBuilder();
        report.append("=== User Data Analytics Report ===\n");
        report.append("Active: ").append(activity.activeCount).append(", Inactive: ").append(activity.inactiveCount).append("\n");
        report.append("Domains: ").append(domain.usersByDomain.keySet()).append("\n");
        report.append("Duplicate emails: ").append(duplicateEmails.size()).append("\n");
        report.append("Max name length: ").append(name.maxNameLength).append(", Min name length: ").append(name.minNameLength).append("\n");
        report.append("Palindrome users: ").append(name.palindromeCount).append("\n");
        report.append("Max username length: ").append(username.maxUsernameLength).append(", Min username length: ").append(username.minUsernameLength).append("\n");
        report.append("Recent users (last 7 days): ").append(recentUsers.size()).append("\n");
        report.append("\n--- End of Report ---\n");
        return report.toString();
    }

    /**
     * Holds analytics results for user activity.
     */
    private static class UserActivityAnalytics {
        final int activeCount, inactiveCount;
        final List<UserProfile> activeUsers, inactiveUsers;
        UserActivityAnalytics(int a, int i, List<UserProfile> act, List<UserProfile> inact) {
            this.activeCount = a; this.inactiveCount = i; this.activeUsers = act; this.inactiveUsers = inact;
        }
    }
    /**
     * Holds analytics results for domain analysis.
     */
    private static class DomainAnalytics {
        final Map<String, List<UserProfile>> usersByDomain;
        final Map<String, Integer> domainActivity;
        DomainAnalytics(Map<String, List<UserProfile>> u, Map<String, Integer> d) {
            this.usersByDomain = u; this.domainActivity = d;
        }
    }
    /**
     * Holds analytics results for name analysis.
     */
    private static class NameAnalytics {
        final int maxNameLength, minNameLength, palindromeCount;
        final List<UserProfile> palindromeUsers;
        NameAnalytics(int max, int min, int pal, List<UserProfile> pals) {
            this.maxNameLength = max; this.minNameLength = min; this.palindromeCount = pal; this.palindromeUsers = pals;
        }
    }
    /**
     * Holds analytics results for username analysis.
     */
    private static class UsernameAnalytics {
        final int maxUsernameLength, minUsernameLength;
        UsernameAnalytics(int max, int min) {
            this.maxUsernameLength = max; this.minUsernameLength = min;
        }
    }

    // ... more methods as needed
} 