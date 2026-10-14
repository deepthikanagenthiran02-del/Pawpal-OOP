
package com.pawpal.model;

public class Admin extends User {

    public Admin(int userId, String fullName, String email,
                 String password, String city) {

        super(userId, fullName, email, password, city);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard";
    }

    @Override
    public String getAppointmentScopeSql() {
        return "";
    }

    @Override
    public boolean canBookAppointments() {
        return false;
    }
}
