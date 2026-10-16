
package com.pawpal.model;
// Veterinarian is a type of User, so it extends (inherits from) the User class
public class Veterinarian extends User {
    
    // Extra details that only a vet has (private = encapsulation)
    private int vetId; // id of the vet in the vets table
    private String specialization; // the vet's area of work, e.g. Surgery
    private String licenseNo; // the vet's licence number
    
    // Constructor: creates a Veterinarian object
    public Veterinarian(int userId, String fullName, String email,
                        String password, String city,
                        int vetId, String specialization, String licenseNo) {
    // Send the common user details to the parent class (User)
        super(userId, fullName, email, password, city);
    // Set the vet-only details
        this.vetId = vetId;
        this.specialization = specialization;
        this.licenseNo = licenseNo;
    }
    // ----- Getters: read the private fields -----
    public int getVetId() {
        return vetId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getLicenseNo() {
        return licenseNo;
    }
      // ----- Methods overridden from User (polymorphism) -----
 
    // The role saved in the database for a vet
    @Override
    public String getRole() {
        return "VET";
    }
// Title shown in the menu when a vet is logged in
    @Override
    public String getDashboardTitle() {
        return "Veterinarian Dashboard";
    }
 // A vet can only see their own appointments
    @Override
    public String getAppointmentScopeSql() {
        return "WHERE v.user_id = " + getUserId();
    }
 // A vet cannot book appointments (only pet owners can)
    @Override
    public boolean canBookAppointments() {
        return false;
  // ----- Setters: change the private fields (used when updating a vet) -----      
    public void setSpecialization(String specialization) {
    this.specialization = specialization;
}
    public void setLicenseNo(String licenseNo) {
    this.licenseNo = licenseNo;    
    }
}
