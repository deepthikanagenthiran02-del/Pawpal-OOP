
package com.pawpal.dao;

import com.pawpal.model.*;+
import com.pawpal.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO class responsible for user database operations
public class UserDAO implements BaseDAO<User> {


     // SQL query to retrieve user details and related veterinarian details
    // LEFT JOIN also retrieves users who are not veterinarians
    private static final String SELECT_USERS =
            "SELECT u.user_id, u.full_name, u.email, u.password, u.city, u.role, " +
            "v.vet_id, v.specialization, v.license_no " +
            "FROM users u LEFT JOIN vets v ON v.user_id = u.user_id ";

      // ADD: Registers a user by saving their details in the database
    @Override
    public boolean add(User u) {
        
        // SQL query to insert common user details
        String userSql = "INSERT INTO users " +
                "(full_name, email, password, city, role) VALUES (?,?,?,?,?)";
        
        // SQL query to insert additional details for a veterinarian
        String vetSql = "INSERT INTO vets " +
                "(user_id, specialization, license_no) VALUES (?,?,?)";

        try (Connection con = DBConnection.getConnection()) {

             // Starts a transaction so related changes can be handled together
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    userSql, Statement.RETURN_GENERATED_KEYS)) {

                
                 // Supplies user details to the SQL query
                ps.setString(1, u.getFullName());
                ps.setString(2, u.getEmail());
                ps.setString(3, u.getPassword());
                ps.setString(4, u.getCity());
                ps.setString(5, u.getRole());

                ps.executeUpdate();

                // Retrieves the automatically generated user ID
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        u.setUserId(keys.getInt(1));
                    }
                }
                // If the user is a veterinarian, save their extra details
                if (u instanceof Veterinarian) {
                    Veterinarian vet = (Veterinarian) u;

                   
                    try (PreparedStatement ps2 = con.prepareStatement(vetSql)) {
                        ps2.setInt(1, u.getUserId());
                        ps2.setString(2, vet.getSpecialization());
                        ps2.setString(3, vet.getLicenseNo());
                        ps2.executeUpdate();
                    }
                }
                
                // Saves the transaction changes
                con.commit();
                return true;

            } catch (SQLException e) {
                
                 // Cancels transaction changes if an SQL error occurs
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            
            // Displays a message if the email already exists
            if (e.getErrorCode() == 1062) {
                System.out.println("That email is already registered.");
            } else {
                System.out.println("Error registering user: " + e.getMessage());
            }
            return false;
        }
    }

    // LOGIN: Checks the email and password against database records
    public User login(String email, String password) {

        String sql = SELECT_USERS + "WHERE u.email = ? AND u.password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Supplies the login details to the query
            ps.setString(1, email);
            ps.setString(2, password);

            // Executes the query and reads the result
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        // Returns null if login fails or no matching user is found
        return null;
    }

    // FIND ALL: Retrieves all users from the database
    @Override
    public List<User> findAll() {

        List<User> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT_USERS)) {

            // Converts each database row into a Java user object
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        return list;
    }

    // DELETE: Deletes a user using their user ID
    @Override
    public boolean delete(int id) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM users WHERE user_id = ?")) {

            // Sets the ID of the user to delete
            ps.setInt(1, id);
             // Returns true if at least one record was deleted
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // MAP ROW: Converts a database record into the correct Java user object
    private User mapRow(ResultSet rs) throws SQLException {

        int id = rs.getInt("user_id");
        String name = rs.getString("full_name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String city = rs.getString("city");

        // Reads the user's role from the database
        String role = rs.getString("role");

         // Creates an object based on the user's role
        switch (role) {
            case "OWNER":
                return new PetOwner(id, name, email, password, city);

            case "VET":
                return new Veterinarian(
                        id, name, email, password, city,
                        rs.getInt("vet_id"),
                        rs.getString("specialization"),
                        rs.getString("license_no"));
        
            // Creates an Admin object for any other role
            default:
                return new Admin(id, name, email, password, city);
        }
    }
}
