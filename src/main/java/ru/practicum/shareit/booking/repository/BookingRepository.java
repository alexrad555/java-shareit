package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.entity.BookingEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findAllByBookerId(Long userId, Pageable pageable);

    List<BookingEntity> findAllByBookerIdAndItemId(Long userId, Long itemId);

    List<BookingEntity> findAllByItemOwnerId(Long userId, Pageable pageable);

    Optional<BookingEntity> findFirstByItemIdAndStatusAndStartDateBeforeOrderByStartDateDesc(
            Long id,
            BookingStatus status,
            LocalDateTime start
    );

    Optional<BookingEntity> findFirstByItemIdAndStatusAndStartDateAfterOrderByStartDate(
            Long id,
            BookingStatus status,
            LocalDateTime start
    );
}
