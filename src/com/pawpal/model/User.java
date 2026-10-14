
package com.pawpal.model;

public abstract class User {

    private int userId;
    private String fullName;
    private String email;
    private String password;
    private String city;

    protected User(int userId, String fullName, String email,
                   String password, String city) {
        this.userId = userId;
        setFullName(fullName);
        setEmail(email);
        setPassword(password);
        this.city = city;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public final void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Enter a valid email.");
        }
        this.email = email.trim();
    }

    public String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                "Password must be at least 6 characters.");
        }
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public abstract String getRole();
    public abstract String getDashboardTitle();
    public abstract String getAppointmentScopeSql();
    public abstract boolean canBookAppointments();
}
