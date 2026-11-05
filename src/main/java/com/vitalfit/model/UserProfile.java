package com.vitalfit.model;

/**
 * Encapsulates physical biometric profile of an athlete for personalized metric calculations.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
public class UserProfile {
    private int userId;
    private double weightKg;
    private double heightCm;
    private int age;
    private String gender; // "MALE", "FEMALE", "OTHER"
    private String fitnessLevel; // "BEGINNER", "INTERMEDIATE", "ADVANCED"

    public UserProfile() {
        this.weightKg = 70.0;
        this.heightCm = 175.0;
        this.age = 25;
        this.gender = "OTHER";
        this.fitnessLevel = "INTERMEDIATE";
    }

    public UserProfile(int userId, double weightKg, double heightCm, int age, String gender, String fitnessLevel) {
        this.userId = userId;
        this.weightKg = weightKg > 0 ? weightKg : 70.0;
        this.heightCm = heightCm > 0 ? heightCm : 175.0;
        this.age = age > 0 ? age : 25;
        this.gender = gender != null ? gender : "OTHER";
        this.fitnessLevel = fitnessLevel != null ? fitnessLevel : "INTERMEDIATE";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(String fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }
}
