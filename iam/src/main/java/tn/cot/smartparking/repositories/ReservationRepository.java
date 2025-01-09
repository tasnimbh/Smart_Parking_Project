package tn.cot.smartparking.repositories;

import jakarta.data.repository.CrudRepository;
import tn.cot.smartparking.entities.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, String> {
    List<Reservation> findByParkingSpotIdAndEndTimeAfter(String parkingSpotId, LocalDateTime endTime);
}
