
package com.pawpal.dao;

import com.pawpal.model.*;
import com.pawpal.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements BaseDAO<User> {

    private static final String SELECT_USERS =
            "SELECT u.user_id, u.full_name, u.email, u.password, u.city, u.role, " +
            "v.vet_id, v.specialization, v.license_no " +
            "FROM users u LEFT JOIN vets v ON v.user_id = u.user_id ";

    @Override
    public boolean add(User u) {

        String userSql = "INSERT INTO users " +
                "(full_name, email, password, city, role) VALUES (?,?,?,?,?)";

        String vetSql = "INSERT INTO vets " +
                "(user_id, specialization, license_no) VALUES (?,?,?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    userSql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, u.getFullName());
                ps.setString(2, u.getEmail());
                ps.setString(3, u.getPassword());
                ps.setString(4, u.getCity());
                ps.setString(5, u.getRole());

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        u.setUserId(keys.getInt(1));
                    }
                }

                if (u instanceof Veterinarian) {
                    Veterinarian vet = (Veterinarian) u;

                    try (PreparedStatement ps2 = con.prepareStatement(vetSql)) {
                        ps2.setInt(1, u.getUserId());
                        ps2.setString(2, vet.getSpecialization());
                        ps2.setString(3, vet.getLicenseNo());
                        ps2.executeUpdate();
                    }
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("That email is already registered.");
            } else {
                System.out.println("Error registering user: " + e.getMessage());
            }
            return false;
        }
    }

    public User login(String email, String password) {

        String sql = SELECT_USERS + "WHERE u.email = ? AND u.password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<User> findAll() {

        List<User> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT_USERS)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        return list;
    }

    @Override
    public boolean delete(int id) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM users WHERE user_id = ?")) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {

        int id = rs.getInt("user_id");
        String name = rs.getString("full_name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String city = rs.getString("city");

        String role = rs.getString("role");

        switch (role) {
            case "OWNER":
                return new PetOwner(id, name, email, password, city);

            case "VET":
                return new Veterinarian(
                        id, name, email, password, city,
                        rs.getInt("vet_id"),
                        rs.getString("specialization"),
                        rs.getString("license_no"));

            default:
                return new Admin(id, name, email, password, city);
        }
    }
}
