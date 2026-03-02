package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select count(b) > 0 from Booking b " +
            "where b.booker.id = :userId " +
            "and b.item.id = :itemId " +
            "and b.end < :now " +
            "and b.status = :status")
    boolean hasCompletedBooking(@Param("userId") Long userId,
                                @Param("itemId") Long itemId,
                                @Param("now") LocalDateTime now,
                                @Param("status") BookingStatus status);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2")
    List<Booking> findAllByBookerIdAndCurrent(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2")
    List<Booking> findAllByItemOwnerIdAndCurrent(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.id in ?1 and b.status = 'APPROVED'")
    List<Booking> findAllApprovedByItemIds(List<Long> itemIds);

    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingStatus status);
}
