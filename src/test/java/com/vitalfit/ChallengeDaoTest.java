package com.vitalfit;

import com.vitalfit.config.DBConnection;
import com.vitalfit.dao.ChallengeDAO;
import com.vitalfit.model.Challenge;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration and CRUD tests for ChallengeDAO implementing GenericDao<Challenge, Integer>.
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChallengeDaoTest {

    private ChallengeDAO challengeDAO;

    @BeforeAll
    public void setupDb() throws Exception {
        String jdbcUrl = "jdbc:h2:mem:vitalfit_challenge_dao_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";
        DBConnection.setTestConnectionDetails(jdbcUrl, "sa", "");

        challengeDAO = new ChallengeDAO();

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
    @DisplayName("GenericDao CRUD lifecycle for Challenge entity")
    public void testGenericDaoChallengeCrud() throws Exception {
        // 1. Save (GenericDao.save)
        Challenge challenge = new Challenge(0, "Spring Sprint 50K", "Run 50km in 2 weeks", "Distance", 50.0,
                new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 14 * 86400000L));
        boolean saved = challengeDAO.save(challenge);
        assertTrue(saved);
        assertTrue(challenge.getId() > 0);

        // 2. Find by ID (GenericDao.findById)
        Optional<Challenge> foundOpt = challengeDAO.findById(challenge.getId());
        assertTrue(foundOpt.isPresent());
        assertEquals("Spring Sprint 50K", foundOpt.get().getTitle());
        assertEquals(50.0, foundOpt.get().getTargetGoal(), 0.01);

        // 3. Update (GenericDao.update)
        Challenge found = foundOpt.get();
        found.setTitle("Spring Sprint 60K");
        found.setTargetGoal(60.0);
        boolean updated = challengeDAO.update(found);
        assertTrue(updated);

        Optional<Challenge> updatedOpt = challengeDAO.findById(challenge.getId());
        assertTrue(updatedOpt.isPresent());
        assertEquals("Spring Sprint 60K", updatedOpt.get().getTitle());
        assertEquals(60.0, updatedOpt.get().getTargetGoal(), 0.01);

        // 4. Find All (GenericDao.findAll)
        List<Challenge> all = challengeDAO.findAll();
        assertFalse(all.isEmpty());

        // 5. Delete by ID (GenericDao.deleteById)
        boolean deleted = challengeDAO.deleteById(challenge.getId());
        assertTrue(deleted);

        Optional<Challenge> afterDelete = challengeDAO.findById(challenge.getId());
        assertFalse(afterDelete.isPresent());
    }
}
