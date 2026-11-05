package com.vitalfit;

import com.vitalfit.config.DBConnection;
import com.vitalfit.dao.WorkoutDAO;
import com.vitalfit.model.Workout;
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
 * Integration and CRUD tests for WorkoutDAO implementing GenericDao<Workout, Integer>.
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkoutDaoTest {

    private WorkoutDAO workoutDAO;

    @BeforeAll
    public void setupDb() throws Exception {
        String jdbcUrl = "jdbc:h2:mem:vitalfit_workout_dao_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";
        DBConnection.setTestConnectionDetails(jdbcUrl, "sa", "");

        workoutDAO = new WorkoutDAO();

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
    @DisplayName("GenericDao CRUD lifecycle for Workout entity")
    public void testGenericDaoWorkoutCrud() throws Exception {
        // 1. Save (GenericDao.save)
        Workout workout = new Workout(0, 1, "Cycling", "Medium", 40, 320, 12.5, new Date(System.currentTimeMillis()), "Evening ride");
        boolean saved = workoutDAO.save(workout);
        assertTrue(saved);
        assertTrue(workout.getId() > 0);

        // 2. Find by ID (GenericDao.findById returning Optional)
        Optional<Workout> foundOpt = workoutDAO.findById(workout.getId());
        assertTrue(foundOpt.isPresent());
        Workout found = foundOpt.get();
        assertEquals("Cycling", found.getActivityType());
        assertEquals(320, found.getCaloriesBurned());

        // 3. Update (GenericDao.update)
        found.setDurationMinutes(50);
        found.setCaloriesBurned(400);
        boolean updated = workoutDAO.update(found);
        assertTrue(updated);

        Optional<Workout> updatedOpt = workoutDAO.findById(workout.getId());
        assertTrue(updatedOpt.isPresent());
        assertEquals(50, updatedOpt.get().getDurationMinutes());
        assertEquals(400, updatedOpt.get().getCaloriesBurned());

        // 4. Find All (GenericDao.findAll)
        List<Workout> all = workoutDAO.findAll();
        assertFalse(all.isEmpty());

        // 5. Delete by ID (GenericDao.deleteById)
        boolean deleted = workoutDAO.deleteById(workout.getId());
        assertTrue(deleted);
        Optional<Workout> afterDelete = workoutDAO.findById(workout.getId());
        assertFalse(afterDelete.isPresent());
    }
}
