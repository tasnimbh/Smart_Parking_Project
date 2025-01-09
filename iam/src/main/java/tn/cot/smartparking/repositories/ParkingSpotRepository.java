package tn.cot.smartparking.repositories;

import jakarta.data.repository.CrudRepository;
import tn.cot.smartparking.entities.ParkingSpot;

import java.util.List;

public interface ParkingSpotRepository extends CrudRepository<ParkingSpot, String> {
    List<ParkingSpot> findByIsAvailable(boolean isAvailable);
}
