package com.vitalfit.model;

/**
 * Enumeration of workout classifications.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public enum WorkoutType {
    CARDIO,
    STRENGTH,
    YOGA,
    HIIT,
    OTHER;

    public static WorkoutType fromString(String type) {
        if (type == null || type.isBlank()) {
            return OTHER;
        }
        String clean = type.trim().toUpperCase();
        switch (clean) {
            case "CARDIO":
            case "RUNNING":
            case "CYCLING":
            case "SWIMMING":
            case "WALKING":
                return CARDIO;
            case "STRENGTH":
            case "WEIGHTLIFTING":
            case "BODYBUILDING":
                return STRENGTH;
            case "YOGA":
            case "PILATES":
            case "STRETCHING":
                return YOGA;
            case "HIIT":
            case "CROSSFIT":
            case "TABATA":
            case "CIRCUIT":
                return HIIT;
            default:
                return OTHER;
        }
    }
}
