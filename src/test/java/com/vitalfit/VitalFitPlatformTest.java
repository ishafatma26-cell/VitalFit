package com.vitalfit;

import com.vitalfit.config.DBConnection;
import com.vitalfit.dao.ChallengeDAO;
import com.vitalfit.dao.UserDAO;
import com.vitalfit.dao.WorkoutDAO;
import com.vitalfit.model.ActivityLog;
import com.vitalfit.model.Challenge;
import com.vitalfit.model.User;
import com.vitalfit.model.Workout;
import com.vitalfit.service.ActivityLogService;
import com.vitalfit.service.LeaderboardService;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VitalFitPlatformTest {

    private UserDAO userDAO;
    private WorkoutDAO workoutDAO;
    private ChallengeDAO challengeDAO;
    private LeaderboardService leaderboardService;

    @BeforeAll
    public void setupDatabase() throws Exception {
        // Use H2 in PostgreSQL compatibility mode with DB_CLOSE_DELAY=-1 to keep in-memory database alive
        String jdbcUrl = "jdbc:h2:mem:vitalfittest;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";
        DBConnection.setTestConnectionDetails(jdbcUrl, "sa", "");

        userDAO = new UserDAO();
        workoutDAO = new WorkoutDAO();
        challengeDAO = new ChallengeDAO();
        leaderboardService = new LeaderboardService(workoutDAO);

        // Read schema.sql content
        String schemaSql = new String(Files.readAllBytes(Paths.get("schema.sql")));

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String[] sqlStatements = schemaSql.split(";");
            for (String sql : sqlStatements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    @Test
    public void testUserRegistrationAndAuthentication() throws Exception {
        User u = new User("Unit Tester", "tester@vitalfit.demo", null, "USER");
        boolean registered = userDAO.registerUser(u, "TestPass@123");
        assertTrue(registered, "User should register successfully");

        User authUser = userDAO.authenticate("tester@vitalfit.demo", "TestPass@123");
        assertNotNull(authUser, "Authentication should succeed with correct password");
        assertEquals("Unit Tester", authUser.getName());

        User failedAuth = userDAO.authenticate("tester@vitalfit.demo", "WrongPassword");
        assertNull(failedAuth, "Authentication should fail with wrong password");
    }

    @Test
    public void testACIDTransactionJoinChallenge() throws Exception {
        // Create user & challenge
        User u = new User("Acid User", "acid@vitalfit.demo", null, "USER");
        userDAO.registerUser(u, "User@123");

        Challenge ch = new Challenge(0, "Transaction Test", "ACID Check", "Calories", 5000, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 864000000));
        challengeDAO.createChallenge(ch);

        // First join (Should succeed)
        boolean firstJoin = challengeDAO.joinChallenge(u.getId(), ch.getId());
        assertTrue(firstJoin, "First join transaction should succeed");

        // Second join (Duplicate check -> Rollback)
        boolean secondJoin = challengeDAO.joinChallenge(u.getId(), ch.getId());
        assertFalse(secondJoin, "Duplicate join should be prevented by transaction logic");
    }

    @Test
    public void testStreamApiLeaderboardAggregation() throws Exception {
        // Fetch leaderboard entries compiled via Java Streams
        List<LeaderboardService.LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard();
        assertNotNull(leaderboard, "Leaderboard should not be null");
        assertFalse(leaderboard.isEmpty(), "Leaderboard should contain aggregated user data");

        // Verify ranks are sequentially assigned starting at 1
        assertEquals(1, leaderboard.get(0).getRank());
        assertTrue(leaderboard.get(0).getTotalCalories() >= leaderboard.get(leaderboard.size() - 1).getTotalCalories(),
                "Leaderboard should be sorted descending by total calories burned");
    }

    @Test
    public void testMultithreadedAsyncActivityLogging() throws Exception {
        ActivityLogService.logAsync("async@vitalfit.demo", "TEST_ASYNC", "Testing multithreaded logging executor");

        // Wait briefly for async thread pool execution
        Thread.sleep(800);

        List<ActivityLog> logs = ActivityLogService.getRecentLogs(20);
        boolean found = logs.stream().anyMatch(l -> "async@vitalfit.demo".equals(l.getUserEmail()) && "TEST_ASYNC".equals(l.getAction()));
        assertTrue(found, "Async log entry should be persisted by worker thread");
    }
}
