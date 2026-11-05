package com.vitalfit;

import com.vitalfit.config.DBConnection;
import com.vitalfit.dao.UserDAO;
import com.vitalfit.model.User;
import com.vitalfit.model.UserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User authentication, BCrypt password hashing, and UserPrincipal conversion.
 *
 * @author Isha Fatma (25SCSE1180892) - Team NOVA
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAuthServiceTest {

    private UserDAO userDAO;

    @BeforeAll
    public void setupDb() throws Exception {
        String jdbcUrl = "jdbc:h2:mem:vitalfit_auth_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";
        DBConnection.setTestConnectionDetails(jdbcUrl, "sa", "");

        userDAO = new UserDAO();

        String schemaSql = new String(Files.readAllBytes(Paths.get("schema.sql")));
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : schemaSql.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
        }
    }

    @Test
    @DisplayName("User registration hashes passwords with BCrypt salt and authenticates successfully")
    public void testUserRegistrationAndBCryptAuth() throws Exception {
        User user = new User("Auth Service User", "auth_service@vitalfit.demo", null, "USER");
        boolean registered = userDAO.registerUser(user, "SecureP@ssword123");
        assertTrue(registered);

        User authenticated = userDAO.authenticate("auth_service@vitalfit.demo", "SecureP@ssword123");
        assertNotNull(authenticated);
        assertEquals("Auth Service User", authenticated.getName());
        assertTrue(authenticated.getPasswordHash().startsWith("$2a$"), "Password hash should be BCrypt format");

        // Authenticate with wrong password
        User wrongAuth = userDAO.authenticate("auth_service@vitalfit.demo", "WrongPassword");
        assertNull(wrongAuth);
    }

    @Test
    @DisplayName("UserPrincipal encapsulates user details correctly and identifies admin role")
    public void testUserPrincipalCreation() {
        User user = new User(10, "Aditya Singh", "aditya@vitalfit.demo", "hash", "ADMIN", null);
        UserPrincipal principal = UserPrincipal.fromUser(user);

        assertNotNull(principal);
        assertEquals(10, principal.getUserId());
        assertEquals("aditya@vitalfit.demo", principal.getEmail());
        assertEquals("ADMIN", principal.getRole());
        assertEquals("Aditya Singh", principal.getFullName());
        assertTrue(principal.isAdmin());

        User normalUser = new User(11, "Standard Athlete", "user@vitalfit.demo", "hash", "USER", null);
        UserPrincipal normalPrincipal = UserPrincipal.fromUser(normalUser);
        assertFalse(normalPrincipal.isAdmin());
    }
}
