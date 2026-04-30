package com.umb.mcoronado.reservation.exception;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(Long id) {
        super("Reservation not found for id: " + id);
    }
}
