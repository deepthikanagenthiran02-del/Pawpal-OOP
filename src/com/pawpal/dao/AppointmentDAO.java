package com.pawpal.dao;

import com.pawpal.model.Appointment;
import com.pawpal.model.User;
import com.pawpal.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO implements BaseDAO<Appointment> {

    private static final String SELECT_BASE =
        "SELECT a.appointment_id, a.pet_id, a.vet_id, a.appointment_date, a.appointment_time, " +
        "a.reason, a.status, p.pet_name, uo.full_name AS owner_name, uv.full_name AS vet_name " +
        "FROM appointments a " +
        "JOIN pets p ON a.pet_id = p.pet_id " +
        "JOIN users uo ON p.owner_id = uo.user_id " +
        "JOIN vets v ON a.vet_id = v.vet_id " +
        "JOIN users uv ON v.user_id = uv.user_id ";
    private static final String ORDER = " ORDER BY a.appointment_date, a.appointment_time";

    // CREATE
    @Override
    public boolean add(Appointment a) {
        String sql = "INSERT INTO appointments (pet_id, vet_id, appointment_date, appointment_time, reason) " +
                     "VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getPetId());
            ps.setInt(2, a.getVetId());
            ps.setDate(3, Date.valueOf(a.getDate()));
            ps.setTime(4, Time.valueOf(a.getTime()));
            ps.setString(5, a.getReason());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) System.out.println("That vet is already booked at that date and time.");
            else System.out.println("Error booking appointment: " + e.getMessage());
            return false;
        }
    }

    // READ (everything)
    @Override
    public List<Appointment> findAll() {
        return query(SELECT_BASE + ORDER);
    }

    // READ (only what this user is allowed to see - polymorphic scope)
    public List<Appointment> findForUser(User user) {
        return query(SELECT_BASE + user.getAppointmentScopeSql() + ORDER);
    }

    // UPDATE (reschedule)
    public boolean updateDetails(int id, LocalDate date, LocalTime time, String reason) {
        String sql = "UPDATE appointments SET appointment_date = ?, appointment_time = ?, reason = ? " +
                     "WHERE appointment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(time));
            ps.setString(3, reason);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) System.out.println("That vet is already booked at that date and time.");
            else System.out.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }

    // UPDATE (status)
    public boolean updateStatus(int id, String status) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE appointments SET status = ? WHERE appointment_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    @Override
    public boolean delete(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM appointments WHERE appointment_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }

    private List<Appointment> query(String sql) {
        List<Appointment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setAppointmentId(rs.getInt("appointment_id"));
                a.setPetId(rs.getInt("pet_id"));
                a.setVetId(rs.getInt("vet_id"));
                a.setDate(rs.getDate("appointment_date").toLocalDate());
                a.setTime(rs.getTime("appointment_time").toLocalTime());
                a.setReason(rs.getString("reason"));
                a.setStatus(rs.getString("status"));
                a.setPetName(rs.getString("pet_name"));
                a.setOwnerName(rs.getString("owner_name"));
                a.setVetName(rs.getString("vet_name"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Error loading appointments: " + e.getMessage());
        }
        return list;
    }
}
