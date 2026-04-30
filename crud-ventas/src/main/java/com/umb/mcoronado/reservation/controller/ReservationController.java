package com.umb.mcoronado.reservation.controller;

import com.umb.mcoronado.reservation.dto.CreateReservationRequest;
import com.umb.mcoronado.reservation.model.entity.Reservation;
import com.umb.mcoronado.reservation.service.ReservationService;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> listAll() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findReservationById(id));
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody CreateReservationRequest request) {
        Reservation created = reservationService.createReservation(
                request.getCustomerName(), request.getDate(), request.getTime(), request.getService());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
