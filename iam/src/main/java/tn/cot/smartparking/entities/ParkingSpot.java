package tn.cot.smartparking.entities;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import tn.cot.smartparking.utils.Argon2Utils;
import java.io.Serializable;
import java.security.Principal;
import java.util.UUID;
import java.io.Serializable;
import java.util.UUID;

@Entity("parking_spots")
public class ParkingSpot {
    @Id
    private String id;

    @Column("location")
    private String location;

    @Column("isAvailable")
    private boolean isAvailable;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}