package com.vitalfit.strategy;

import com.vitalfit.model.UserProfile;
import com.vitalfit.model.Workout;

/**
 * Strength Workout Calorie Calculator Strategy.
 * Formula: Calories = Base Burn Rate (kcal/min based on intensity & fitness level) * Duration.
 * Low: 4.5 kcal/min, Medium: 6.5 kcal/min, High: 9.0 kcal/min.
 * Weight scaling factor: (Weight / 70.0)^0.75
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public class StrengthWorkoutCalculator implements WorkoutCalculator {

    @Override
    public double calculateCalories(Workout workout, UserProfile profile) {
        if (workout == null || workout.getDurationMinutes() <= 0) return 0.0;
        double weight = (profile != null && profile.getWeightKg() > 0) ? profile.getWeightKg() : 70.0;
        int duration = workout.getDurationMinutes();
        String intensity = workout.getIntensity() != null ? workout.getIntensity().trim().toLowerCase() : "medium";

        double ratePerMin;
        switch (intensity) {
            case "low":
                ratePerMin = 4.5;
                break;
            case "high":
                ratePerMin = 9.0;
                break;
            case "medium":
            default:
                ratePerMin = 6.5;
                break;
        }

        double weightFactor = Math.pow(weight / 70.0, 0.75);
        double calories = ratePerMin * duration * weightFactor;
        return Math.round(calories * 100.0) / 100.0;
    }
}
