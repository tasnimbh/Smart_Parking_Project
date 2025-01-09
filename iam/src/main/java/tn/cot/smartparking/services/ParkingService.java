package tn.cot.smartparking.services;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import tn.cot.smartparking.entities.ParkingSpot;
import tn.cot.smartparking.entities.Reservation;
import tn.cot.smartparking.repositories.ParkingSpotRepository;
import tn.cot.smartparking.repositories.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class ParkingService {

    @Inject
    private ParkingSpotRepository parkingSpotRepository;

    @Inject
    private ReservationRepository reservationRepository;

    public List<ParkingSpot> findAvailableSpots() {
        return parkingSpotRepository.findByIsAvailable(true);
    }

    public Optional<Reservation> reserveSpot(String spotId, String userId, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<ParkingSpot> spot = parkingSpotRepository.findById(spotId);
        if (spot.isPresent() && spot.get().isAvailable()) {
            List<Reservation> existingReservations = reservationRepository.findByParkingSpotIdAndEndTimeAfter(spotId, startTime);
            if (existingReservations.isEmpty()) {
                Reservation reservation = new Reservation();
                reservation.setParkingSpotId(spotId);
                reservation.setUserId(userId);
                reservation.setStartTime(startTime);
                reservation.setEndTime(endTime);
                reservationRepository.save(reservation);
                spot.get().setAvailable(false);
                parkingSpotRepository.save(spot.get());
                return Optional.of(reservation);
            }
        }
        return Optional.empty();
    }
}
