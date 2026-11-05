package com.vitalfit.strategy;

import com.vitalfit.model.UserProfile;
import com.vitalfit.model.Workout;

/**
 * High-Intensity Interval Training (HIIT) Calorie Calculator Strategy.
 * Formula: Calories = High-impact MET * Weight(kg) * (Duration(mins) / 60) * EPOC Afterburn (1.15).
 * Low: 8.0 MET, Medium: 10.5 MET, High: 13.0 MET.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public class HIITWorkoutCalculator implements WorkoutCalculator {

    private static final double EPOC_AFTERBURN_FACTOR = 1.15; // 15% excess post-exercise oxygen consumption

    @Override
    public double calculateCalories(Workout workout, UserProfile profile) {
        if (workout == null || workout.getDurationMinutes() <= 0) return 0.0;
        double weight = (profile != null && profile.getWeightKg() > 0) ? profile.getWeightKg() : 70.0;
        int duration = workout.getDurationMinutes();
        String intensity = workout.getIntensity() != null ? workout.getIntensity().trim().toLowerCase() : "medium";

        double met;
        switch (intensity) {
            case "low":
                met = 8.0;
                break;
            case "high":
                met = 13.0;
                break;
            case "medium":
            default:
                met = 10.5;
                break;
        }

        double baseCalories = met * weight * (duration / 60.0);
        double totalWithEpoc = baseCalories * EPOC_AFTERBURN_FACTOR;
        return Math.round(totalWithEpoc * 100.0) / 100.0;
    }
}
