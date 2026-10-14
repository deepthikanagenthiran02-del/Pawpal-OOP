
package com.pawpal.model;

public class PetOwner extends User {

    public PetOwner(int userId, String fullName, String email,
                    String password, String city) {
        super(userId, fullName, email, password, city);
    }

    @Override
    public String getRole() {
        return "OWNER";
    }

    @Override
    public String getDashboardTitle() {
        return "Pet Owner Dashboard";
    }

    @Override
    public String getAppointmentScopeSql() {
        return "WHERE p.owner_id = " + getUserId();
    }

    @Override
    public boolean canBookAppointments() {
        return true;
    }
}
