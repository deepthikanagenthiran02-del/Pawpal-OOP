
package com.pawpal.dao;

import com.pawpal.model.Pet;
import com.pawpal.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDAO implements BaseDAO<Pet> {

    @Override
    public boolean add(Pet p) {
        String sql = "INSERT INTO pets " +
                "(owner_id, pet_name, species, breed, age) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getOwnerId());
            ps.setString(2, p.getPetName());
            ps.setString(3, p.getSpecies());
            ps.setString(4, p.getBreed());
            ps.setInt(5, p.getAge());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error saving pet: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Pet> findAll() {
        return query("SELECT * FROM pets", 0);
    }

    public List<Pet> findByOwner(int ownerId) {
        return query(
                "SELECT * FROM pets WHERE owner_id = ?",
                ownerId
        );
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM pets WHERE pet_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting pet: " + e.getMessage());
            return false;
        }
    }

    private List<Pet> query(String sql, int ownerId) {
        List<Pet> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (sql.contains("?")) {
                ps.setInt(1, ownerId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Pet pet = new Pet(
                            rs.getInt("pet_id"),
                            rs.getInt("owner_id"),
                            rs.getString("pet_name"),
                            rs.getString("species"),
                            rs.getString("breed"),
                            rs.getInt("age")
                    );

                    // IMPORTANT: Add the pet to the list
                    list.add(pet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading pets: " + e.getMessage());
        }

        return list;
    }
}
