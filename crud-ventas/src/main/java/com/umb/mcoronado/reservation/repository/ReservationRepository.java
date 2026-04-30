package com.umb.mcoronado.reservation.repository;

import com.umb.mcoronado.reservation.model.entity.Reservation;
import com.umb.mcoronado.reservation.model.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByDateAndTimeAndStatus(LocalDate date, LocalTime time, ReservationStatus status);
}
