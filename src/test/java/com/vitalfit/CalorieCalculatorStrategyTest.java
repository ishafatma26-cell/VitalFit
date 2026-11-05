package com.vitalfit;

import com.vitalfit.model.UserProfile;
import com.vitalfit.model.Workout;
import com.vitalfit.model.WorkoutType;
import com.vitalfit.strategy.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WorkoutCalculator strategies and Factory.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public class CalorieCalculatorStrategyTest {

    @Test
    @DisplayName("CardioCalculator calculates calories correctly across intensities and distance bonus")
    public void testCardioCalculator() {
        WorkoutCalculator calc = new CardioWorkoutCalculator();
        UserProfile profile = new UserProfile(1, 70.0, 175.0, 25, "MALE", "INTERMEDIATE");

        // 60 mins medium intensity with 70kg: MET 8.5 * 70 * (60/60) = 595.0 + 10km * 10 = 695.0
        Workout w = new Workout(0, 1, "Running", "Medium", 60, 0, 10.0, null, null);
        double calories = calc.calculateCalories(w, profile);
        assertEquals(695.0, calories, 0.01);

        // Low intensity: MET 6.0 * 70 * 0.5 = 210.0
        Workout wLow = new Workout(0, 1, "Running", "Low", 30, 0, 0.0, null, null);
        assertEquals(210.0, calc.calculateCalories(wLow, profile), 0.01);

        // High intensity: MET 11.5 * 70 * 1.0 = 805.0
        Workout wHigh = new Workout(0, 1, "Running", "High", 60, 0, 0.0, null, null);
        assertEquals(805.0, calc.calculateCalories(wHigh, profile), 0.01);
    }

    @Test
    @DisplayName("StrengthCalculator scales with duration, intensity, and user weight")
    public void testStrengthCalculator() {
        WorkoutCalculator calc = new StrengthWorkoutCalculator();
        UserProfile profile = new UserProfile(1, 70.0, 180.0, 28, "MALE", "ADVANCED");

        // Medium intensity 45 mins: 6.5 * 45 * 1.0 = 292.5
        Workout w = new Workout(0, 1, "Strength", "Medium", 45, 0, 0.0, null, null);
        assertEquals(292.5, calc.calculateCalories(w, profile), 0.01);

        // High intensity 60 mins: 9.0 * 60 = 540.0
        Workout wHigh = new Workout(0, 1, "Strength", "High", 60, 0, 0.0, null, null);
        assertEquals(540.0, calc.calculateCalories(wHigh, profile), 0.01);
    }

    @Test
    @DisplayName("YogaCalculator applies low-impact MET values")
    public void testYogaCalculator() {
        WorkoutCalculator calc = new YogaWorkoutCalculator();
        UserProfile profile = new UserProfile(1, 60.0, 165.0, 24, "FEMALE", "BEGINNER");

        // 60 mins low intensity (2.5 MET) * 60kg = 150.0
        Workout w = new Workout(0, 1, "Yoga", "Low", 60, 0, 0.0, null, null);
        assertEquals(150.0, calc.calculateCalories(w, profile), 0.01);

        // 60 mins high intensity (5.5 MET) * 60kg = 330.0
        Workout wHigh = new Workout(0, 1, "Yoga", "High", 60, 0, 0.0, null, null);
        assertEquals(330.0, calc.calculateCalories(wHigh, profile), 0.01);
    }

    @Test
    @DisplayName("HIITCalculator includes 15% EPOC afterburn multiplier")
    public void testHIITCalculator() {
        WorkoutCalculator calc = new HIITWorkoutCalculator();
        UserProfile profile = new UserProfile(1, 80.0, 180.0, 30, "MALE", "ADVANCED");

        // 30 mins high intensity: MET 13.0 * 80 * 0.5 = 520.0 * 1.15 = 598.0
        Workout w = new Workout(0, 1, "HIIT", "High", 30, 0, 0.0, null, null);
        assertEquals(598.0, calc.calculateCalories(w, profile), 0.01);
    }

    @ParameterizedTest
    @CsvSource({
            "Running, CARDIO",
            "Cycling, CARDIO",
            "Strength, STRENGTH",
            "Yoga, YOGA",
            "HIIT, HIIT",
            "Crossfit, HIIT",
            "UnknownActivity, OTHER"
    })
    @DisplayName("WorkoutType and Factory resolve correct strategy dynamically")
    public void testFactoryResolution(String activityName, WorkoutType expectedType) {
        WorkoutType resolved = WorkoutType.fromString(activityName);
        assertEquals(expectedType, resolved);

        WorkoutCalculator calc = WorkoutCalculatorFactory.getCalculator(activityName);
        assertNotNull(calc);
    }

    @Test
    @DisplayName("Boundary tests: zero or negative duration yields zero calories")
    public void testBoundaryValues() {
        WorkoutCalculator calc = new CardioWorkoutCalculator();
        UserProfile profile = new UserProfile();

        Workout zeroDuration = new Workout(0, 1, "Running", "High", 0, 0, 0.0, null, null);
        assertEquals(0.0, calc.calculateCalories(zeroDuration, profile));

        Workout negativeDuration = new Workout(0, 1, "Running", "High", -10, 0, 0.0, null, null);
        assertEquals(0.0, calc.calculateCalories(negativeDuration, profile));

        assertEquals(0.0, calc.calculateCalories(null, profile));
    }
}
