package com.pawpal.service;

import com.pawpal.dao.AppointmentDAO;
import com.pawpal.dao.PetDAO;
import com.pawpal.dao.VetDAO;
import com.pawpal.model.Appointment;
import com.pawpal.model.Pet;
import com.pawpal.model.User;
import com.pawpal.model.Veterinarian;
import java.time.LocalDate;
import java.util.List;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PetDAO petDAO = new PetDAO();
    private final VetDAO vetDAO = new VetDAO();

    // Returns null if booking succeeded, otherwise an error message
    public String book(User user, Appointment a) {
        if (!user.canBookAppointments())
            return "Only pet owners can book appointments.";
        if (a.getDate().isBefore(LocalDate.now()))
            return "Date cannot be in the past.";
        if (!ownsPet(user.getUserId(), a.getPetId()))
            return "That is not one of your pets.";
        if (!vetExists(a.getVetId()))
            return "Invalid Vet ID.";
        return appointmentDAO.add(a) ? null : "Could not book the appointment.";
    }

    public List<Appointment> getAppointmentsFor(User user) {
        return appointmentDAO.findForUser(user);
    }

    private boolean ownsPet(int ownerId, int petId) {
        for (Pet p : petDAO.findByOwner(ownerId)) {
            if (p.getPetId() == petId) return true;
        }
        return false;
    }

    private boolean vetExists(int vetId) {
        for (Veterinarian v : vetDAO.findAll()) {
            if (v.getVetId() == vetId) return true;
        }
        return false;
    }
}