package com.pawpal.dao;

import com.pawpal.model.Veterinarian;
import com.pawpal.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VetDAO implements BaseDAO<Veterinarian> {

    private static final String SELECT_VETS =
            "SELECT u.user_id, u.full_name, u.email, u.password, u.city, " +
            "v.vet_id, v.specialization, v.license_no " +
            "FROM vets v JOIN users u ON v.user_id = u.user_id ";

    // CREATE - a vet is also a user, so reuse UserDAO
    // (inserts into users + vets in one transaction)
    @Override
    public boolean add(Veterinarian vet) {
        return new UserDAO().add(vet);
    }

    // READ (all)
    @Override
    public List<Veterinarian> findAll() {
        return query(SELECT_VETS + "ORDER BY v.vet_id", -1);
    }

    // READ (one)
    public Veterinarian findById(int vetId) {
        List<Veterinarian> list = query(SELECT_VETS + "WHERE v.vet_id = ?", vetId);
        return list.isEmpty() ? null : list.get(0);
    }

    // UPDATE - changes both tables in one transaction
    public boolean update(Veterinarian vet) {
        String userSql = "UPDATE users SET full_name = ?, email = ?, city = ? WHERE user_id = ?";
        String vetSql  = "UPDATE vets SET specialization = ?, license_no = ? WHERE vet_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(userSql);
                 PreparedStatement ps2 = con.prepareStatement(vetSql)) {

                ps1.setString(1, vet.getFullName());
                ps1.setString(2, vet.getEmail());
                ps1.setString(3, vet.getCity());
                ps1.setInt(4, vet.getUserId());
                ps1.executeUpdate();

                ps2.setString(1, vet.getSpecialization());
                ps2.setString(2, vet.getLicenseNo());
                ps2.setInt(3, vet.getVetId());
                boolean ok = ps2.executeUpdate() > 0;

                con.commit();
                return ok;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("That email or licence number is already in use.");
            } else {
                System.out.println("Error updating vet: " + e.getMessage());
            }
            return false;
        }
    }

    // DELETE - removes the vet's user account (the vets row goes with it)
    @Override
    public boolean delete(int vetId) {
        String sql = "DELETE FROM users WHERE user_id = " +
                     "(SELECT user_id FROM vets WHERE vet_id = ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vetId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                System.out.println("Cannot delete: this vet still has appointments.");
            } else {
                System.out.println("Error deleting vet: " + e.getMessage());
            }
            return false;
        }
    }

    // Shared query helper
    private List<Veterinarian> query(String sql, int vetId) {
        List<Veterinarian> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (sql.contains("?")) {
                ps.setInt(1, vetId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Veterinarian(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("city"),
                            rs.getInt("vet_id"),
                            rs.getString("specialization"),
                            rs.getString("license_no")));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading vets: " + e.getMessage());
        }

        return list;
    }
}
