package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.strategy.BookingStrategy;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final List<BookingStrategy> strategies;
    private Map<BookingState, BookingStrategy> strategyMap;

    @PostConstruct
    public void init() {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(BookingStrategy::getStateName, Function.identity()));
    }

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        validateBookingCreation(item, bookingDto, userId);
        Booking booking = bookingMapper.toBooking(bookingDto, item, booker);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    private void validateBookingCreation(Item item, BookingDto bookingDto, Long userId) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Некорректные даты бронирования");
        }
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotOwnerException("Только владелец вещи может подтверждать бронирование");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже изменен");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Доступ к бронированию запрещен");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String stateStr, Integer from, Integer size) {
        checkUserExists(userId);
        BookingState state = parseState(stateStr);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());

        List<Booking> bookings = strategyMap.get(state)
                .findBookings(userId, bookingRepository, pageable, false);

        return toDtos(bookings);
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String stateStr, Integer from, Integer size) {
        checkUserExists(userId);
        BookingState state = parseState(stateStr);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> bookings = strategyMap.get(state)
                .findBookings(userId, bookingRepository, pageable, true);

        return toDtos(bookings);
    }

    private BookingState parseState(String stateStr) {
        return BookingState.from(stateStr)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateStr));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private List<BookingDto> toDtos(List<Booking> bookings) {
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
