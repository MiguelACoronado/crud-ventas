package com.umb.mcoronado.reservation.service;

import com.umb.mcoronado.reservation.exception.BusinessRuleViolationException;
import com.umb.mcoronado.reservation.exception.ReservationNotFoundException;
import com.umb.mcoronado.reservation.model.entity.Reservation;
import com.umb.mcoronado.reservation.model.entity.ReservationStatus;
import com.umb.mcoronado.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException(id));
    }

    @Transactional
    public Reservation createReservation(
            String customerName, LocalDate date, LocalTime time, String service) {
        if (reservationRepository.existsByDateAndTimeAndStatus(date, time, ReservationStatus.ACTIVE)) {
            throw new BusinessRuleViolationException(
                    "Another active reservation already exists for the same date and time.");
        }
        Reservation reservation = new Reservation();
        reservation.setCustomerName(customerName);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setService(service);
        reservation.setStatus(ReservationStatus.ACTIVE);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation =
                reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException(id));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleViolationException("Reservation is already cancelled.");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}
