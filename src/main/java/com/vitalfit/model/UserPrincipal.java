package com.vitalfit.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable UserPrincipal representing an authenticated user session.
 *
 * @author Isha Fatma (25SCSE1180892) - Team NOVA
 */
public final class UserPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int userId;
    private final String email;
    private final String role;
    private final String fullName;

    public UserPrincipal(int userId, String email, String role, String fullName) {
        this.userId = userId;
        this.email = email;
        this.role = role != null ? role.toUpperCase() : "USER";
        this.fullName = fullName;
    }

    public static UserPrincipal fromUser(User user) {
        if (user == null) return null;
        return new UserPrincipal(user.getId(), user.getEmail(), user.getRole(), user.getName());
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return userId == that.userId && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
