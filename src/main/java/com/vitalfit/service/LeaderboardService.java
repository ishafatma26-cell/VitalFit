package com.vitalfit.service;

import com.vitalfit.dao.WorkoutDAO;
import com.vitalfit.model.Workout;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardService {

    public static class LeaderboardEntry {
        private int rank;
        private int userId;
        private String userName;
        private int totalCalories;
        private double totalDistance;
        private int totalDuration;
        private long totalWorkouts;

        public LeaderboardEntry(int rank, int userId, String userName, int totalCalories, double totalDistance, int totalDuration, long totalWorkouts) {
            this.rank = rank;
            this.userId = userId;
            this.userName = userName;
            this.totalCalories = totalCalories;
            this.totalDistance = totalDistance;
            this.totalDuration = totalDuration;
            this.totalWorkouts = totalWorkouts;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public int getTotalCalories() {
            return totalCalories;
        }

        public double getTotalDistance() {
            return totalDistance;
        }

        public int getTotalDuration() {
            return totalDuration;
        }

        public long getTotalWorkouts() {
            return totalWorkouts;
        }
    }

    private final WorkoutDAO workoutDAO;

    public LeaderboardService() {
        this.workoutDAO = new WorkoutDAO();
    }

    public LeaderboardService(WorkoutDAO workoutDAO) {
        this.workoutDAO = workoutDAO;
    }

    /**
     * Java 8 Stream API processing:
     * - Retrieve workout history with user names
     * - Filter valid records
     * - Group by user ID using Collectors.groupingBy()
     * - Aggregate metrics (calories, distance, duration, count)
     * - Sort in descending order by total calories burned
     * - Assign rank badges (1, 2, 3 podium objects and subsequent ranks)
     */
    public List<LeaderboardEntry> getLeaderboard() throws SQLException {
        List<Workout> allWorkouts = workoutDAO.getAllWorkoutsWithUserNames();

        // Group workouts by user ID using Stream API
        Map<Integer, List<Workout>> userWorkoutsMap = allWorkouts.stream()
                .filter(w -> w.getUserId() > 0)
                .collect(Collectors.groupingBy(Workout::getUserId));

        // Process each user's workouts to construct LeaderboardEntry objects
        List<LeaderboardEntry> unrankedEntries = userWorkoutsMap.entrySet().stream()
                .map(entry -> {
                    int userId = entry.getKey();
                    List<Workout> workouts = entry.getValue();

                    String userName = workouts.stream()
                            .map(Workout::getUserName)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse("User #" + userId);

                    int totalCalories = workouts.stream()
                            .mapToInt(Workout::getCaloriesBurned)
                            .sum();

                    double totalDistance = workouts.stream()
                            .mapToDouble(Workout::getDistanceKm)
                            .sum();

                    int totalDuration = workouts.stream()
                            .mapToInt(Workout::getDurationMinutes)
                            .sum();

                    long totalWorkouts = workouts.size();

                    return new LeaderboardEntry(0, userId, userName, totalCalories,
                            Math.round(totalDistance * 100.0) / 100.0,
                            totalDuration, totalWorkouts);
                })
                .sorted(Comparator.comparingInt(LeaderboardEntry::getTotalCalories).reversed())
                .collect(Collectors.toList());

        // Assign ranks 1, 2, 3...
        List<LeaderboardEntry> rankedList = new ArrayList<>();
        int currentRank = 1;
        for (LeaderboardEntry entry : unrankedEntries) {
            entry.setRank(currentRank++);
            rankedList.add(entry);
        }

        return rankedList;
    }

    public List<LeaderboardEntry> getPodium(List<LeaderboardEntry> fullLeaderboard) {
        return fullLeaderboard.stream()
                .limit(3)
                .collect(Collectors.toList());
    }
}
