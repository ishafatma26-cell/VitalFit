package com.vitalfit.strategy;

import com.vitalfit.model.WorkoutType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory and Registry for WorkoutCalculator strategies.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public class WorkoutCalculatorFactory {

    private static final Map<WorkoutType, WorkoutCalculator> STRATEGY_REGISTRY = new EnumMap<>(WorkoutType.class);

    static {
        STRATEGY_REGISTRY.put(WorkoutType.CARDIO, new CardioWorkoutCalculator());
        STRATEGY_REGISTRY.put(WorkoutType.STRENGTH, new StrengthWorkoutCalculator());
        STRATEGY_REGISTRY.put(WorkoutType.YOGA, new YogaWorkoutCalculator());
        STRATEGY_REGISTRY.put(WorkoutType.HIIT, new HIITWorkoutCalculator());
        STRATEGY_REGISTRY.put(WorkoutType.OTHER, new CardioWorkoutCalculator());
    }

    public static WorkoutCalculator getCalculator(WorkoutType type) {
        if (type == null) {
            return STRATEGY_REGISTRY.get(WorkoutType.OTHER);
        }
        return STRATEGY_REGISTRY.getOrDefault(type, STRATEGY_REGISTRY.get(WorkoutType.OTHER));
    }

    public static WorkoutCalculator getCalculator(String activityType) {
        WorkoutType type = WorkoutType.fromString(activityType);
        return getCalculator(type);
    }

    public static void registerStrategy(WorkoutType type, WorkoutCalculator calculator) {
        if (type != null && calculator != null) {
            STRATEGY_REGISTRY.put(type, calculator);
        }
    }
}
