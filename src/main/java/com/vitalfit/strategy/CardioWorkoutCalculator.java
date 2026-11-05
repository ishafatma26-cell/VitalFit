package com.vitalfit.strategy;

import com.vitalfit.model.UserProfile;
import com.vitalfit.model.Workout;

/**
 * Cardio Calorie Calculator Strategy.
 * Formula: Calories = MET * Weight(kg) * (Duration(mins) / 60)
 * Base MET by intensity: Low: 6.0, Medium: 8.5, High: 11.5
 * Distance bonus: +10 kcal per km if distance is recorded.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public class CardioWorkoutCalculator implements WorkoutCalculator {

    @Override
    public double calculateCalories(Workout workout, UserProfile profile) {
        if (workout == null || workout.getDurationMinutes() <= 0) return 0.0;
        double weight = (profile != null && profile.getWeightKg() > 0) ? profile.getWeightKg() : 70.0;
        int duration = workout.getDurationMinutes();
        String intensity = workout.getIntensity() != null ? workout.getIntensity().trim().toLowerCase() : "medium";

        double met;
        switch (intensity) {
            case "low":
                met = 6.0;
                break;
            case "high":
                met = 11.5;
                break;
            case "medium":
            default:
                met = 8.5;
                break;
        }

        double baseCalories = met * weight * (duration / 60.0);
        if (workout.getDistanceKm() > 0) {
            baseCalories += workout.getDistanceKm() * 10.0;
        }
        return Math.round(baseCalories * 100.0) / 100.0;
    }
}
