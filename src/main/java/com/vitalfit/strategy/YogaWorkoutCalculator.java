package com.vitalfit.strategy;

import com.vitalfit.model.UserProfile;
import com.vitalfit.model.Workout;

/**
 * Yoga / Flexibility Workout Calorie Calculator Strategy.
 * Formula: Calories = MET * Weight(kg) * (Duration(mins) / 60)
 * Low (Hatha / Restorative): 2.5 MET, Medium (Vinyasa): 3.8 MET, High (Power / Hot Yoga): 5.5 MET.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public class YogaWorkoutCalculator implements WorkoutCalculator {

    @Override
    public double calculateCalories(Workout workout, UserProfile profile) {
        if (workout == null || workout.getDurationMinutes() <= 0) return 0.0;
        double weight = (profile != null && profile.getWeightKg() > 0) ? profile.getWeightKg() : 70.0;
        int duration = workout.getDurationMinutes();
        String intensity = workout.getIntensity() != null ? workout.getIntensity().trim().toLowerCase() : "medium";

        double met;
        switch (intensity) {
            case "low":
                met = 2.5;
                break;
            case "high":
                met = 5.5;
                break;
            case "medium":
            default:
                met = 3.8;
                break;
        }

        double calories = met * weight * (duration / 60.0);
        return Math.round(calories * 100.0) / 100.0;
    }
}
