package com.vitalfit.strategy;

import com.vitalfit.model.UserProfile;
import com.vitalfit.model.Workout;

/**
 * Strategy Design Pattern interface for calculating calories burned during workout sessions.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public interface WorkoutCalculator {
    double calculateCalories(Workout workout, UserProfile profile);
}
