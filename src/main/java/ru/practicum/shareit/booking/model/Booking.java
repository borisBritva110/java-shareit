package ru.practicum.shareit.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start time cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @NotNull(message = "End time cannot be null")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @NotNull(message = "Item ID cannot be null")
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @NotNull(message = "Booker ID cannot be null")
    @Column(name = "booker_id", nullable = false)
    private Long bookerId;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;
}