
package com.pawpal.model;

// Abstract parent class for all types of users in PawPal
public abstract class User {

    // Private variables to store common user details
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private String city;

    // Protected constructor to initialize user details
    // Child classes such as Admin and PetOwner can call this constructor
    protected User(int userId, String fullName, String email,
                   String password, String city) {
        
        this.userId = userId;

         // Set user details using methods that validate the input
        setFullName(fullName);
        setEmail(email);
        setPassword(password);
        this.city = city;
    }

    // Returns the user's ID
    public int getUserId() {
        return userId;
    }

     // Updates the user's ID
    public void setUserId(int userId) {
        this.userId = userId;
    }

     // Returns the user's full name
    public String getFullName() {
        return fullName;
    }

      // Sets the user's full name after validation
    public final void setFullName(String fullName) {

        // Rejects null, empty, or spaces-only names
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

         // Removes spaces from the beginning and end
        this.fullName = fullName.trim();
    }

    // Returns the user's email
    public String getEmail() {
        return email;
    }

     // Sets the email after basic validation
    public final void setEmail(String email) {

         // Rejects null emails or emails without @
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Enter a valid email.");
        }
        this.email = email.trim();
    }

    // Returns the user's password
    public String getPassword() {
        return password;
    }

     // Sets the password after checking its length
    public final void setPassword(String password) {

        // Password must contain at least 6 characters
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                "Password must be at least 6 characters.");
        }
        this.password = password;
    }

     // Returns the user's city
    public String getCity() {
        return city;
    }

    // Updates the user's city
    public void setCity(String city) {
        this.city = city;
    }

     // Abstract methods that child classes must implement
     // Returns the user's role, such as ADMIN or OWNER
    public abstract String getRole();
    // Returns the title of the user's dashboard
    public abstract String getDashboardTitle();
    // Returns the SQL condition used to filter appointments
    public abstract String getAppointmentScopeSql();
    // Indicates whether the user can book appointments
    public abstract boolean canBookAppointments();
}
