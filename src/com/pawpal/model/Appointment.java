package com.pawpal.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int appointmentId;
    private int petId;
    private int vetId;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private String status = "PENDING";

    // display-only fields filled by joins
    private String petName;
    private String ownerName;
    private String vetName;

    public Appointment() { }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public int getVetId() { return vetId; }
    public void setVetId(int vetId) { this.vetId = vetId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getVetName() { return vetName; }
    public void setVetName(String vetName) { this.vetName = vetName; }
}
