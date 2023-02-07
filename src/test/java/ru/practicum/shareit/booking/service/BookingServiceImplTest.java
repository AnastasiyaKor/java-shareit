package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enumeration.BookingState;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStatusException;
import ru.practicum.shareit.exceptions.ValidatorException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    private User owner;
    private User booker;
    private Item item;
    private Item item1;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void init() {
        owner = new User(1L, "Masha", "Masha@mail.ru");
        booker = new User(2L, "Vova", "vova@mail.ru");
        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Drill pro")
                .available(true)
                .owner(owner)
                .build();
        item1 = Item.builder()
                .id(2L)
                .name("Drill")
                .description("Drill pro")
                .available(true)
                .owner(owner)
                .build();
        booking = new Booking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item, booker, Status.WAITING);
        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.now())
                .start(LocalDateTime.now().plusDays(3))
                .itemId(item.getId())
                .build();
    }

    @AfterEach
    void del() {
        owner = null;
        booker = null;
        item = null;
        item1 = null;
        booking = null;
        bookingRequestDto = null;
    }

    @Test
    void save() {
        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);
        Booking bookingSave = bookingService.createRequest(bookingRequestDto, booker.getId());
        assertEquals(bookingSave.getStart(), bookingRequestDto.getStart());
        assertEquals(bookingSave.getEnd(), bookingRequestDto.getEnd());
        assertNotNull(bookingSave.getItem());
        assertNotNull(bookingSave.getBooker());

        verify(userRepository, Mockito.times(1)).findById(booker.getId());
        verify(itemRepository, Mockito.times(1)).findById(item.getId());
        verify(bookingRepository, Mockito.times(1)).save(any());

    }

    @Test
    void savingTheBookingOfNotExistentUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            bookingService.createRequest(bookingRequestDto, booker.getId());
        });
        String message = notFoundException.getMessage();
        String actualMessage = "Пользователь не найден";
        assertEquals(message, actualMessage);
    }

    @Test
    void savingTheBookingOfNotExistentItem() {
        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            bookingService.createRequest(bookingRequestDto, booker.getId());
        });
        String message = notFoundException.getMessage();
        String actualMessage = "Вещь не найдена";
        assertEquals(message, actualMessage);
    }

    @Test
    void savingYourBooking() {
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            bookingService.createRequest(bookingRequestDto, owner.getId());
        });
        String message = notFoundException.getMessage();
        String actualMessage = "Нельзя бронировать свой товар";
        assertEquals(message, actualMessage);
    }

    @Test
    void savingReservationWithTheStatusFalse() {
        item.setAvailable(false);
        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        ValidatorException validatorException = assertThrows(ValidatorException.class, () -> {
            bookingService.createRequest(bookingRequestDto, booker.getId());
        });
        String message = validatorException.getMessage();
        String actualMessage = "Неверые данные";
        assertEquals(message, actualMessage);
    }

    @Test
    void savingReservationWithAnIncorrectProductId() {
        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item1));
        ValidatorException validatorException = assertThrows(ValidatorException.class, () -> {
            bookingService.createRequest(bookingRequestDto, booker.getId());
        });
        String message = validatorException.getMessage();
        String actualMessage = "Неверный идентификатор товара";
        assertEquals(message, actualMessage);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void statusUpdate(Boolean approved) {
        Mockito
                .when(bookingRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booking));
        Booking bookingUpdate = bookingService.changeStatusRequest(owner.getId(), approved, booker.getId());
        assertEquals(bookingUpdate.getId(), booking.getId());
        assertEquals(bookingUpdate.getStart(), booking.getStart());
        assertEquals(bookingUpdate.getEnd(), booking.getEnd());
        if (approved && booking.getStatus() == Status.WAITING) {
            assertEquals(bookingUpdate.getStatus(), Status.APPROVED);
        } else if (booking.getStatus() == Status.WAITING) {
            assertEquals(bookingUpdate.getStatus(), Status.REJECTED);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void updatingTheStatusOfNonExistentBooking(Boolean approved) {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.changeStatusRequest(owner.getId(), approved, booker.getId());
        });
        String message = exception.getMessage();
        String actualMessage = "бронирование не найдено";
        assertEquals(message, actualMessage);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void updatingAnApprovedBooking(Boolean approved) {
        Mockito
                .when(bookingRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booking));
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            booking.setStatus(Status.APPROVED);
            bookingService.changeStatusRequest(owner.getId(), approved, booker.getId());
        });
        String message = exception.getMessage();
        String actualMessage = "Бронирование уже одобрено";
        assertEquals(message, actualMessage);
    }

    @Test
    void updatingTheStatusOfNonExistentItem() {
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.changeStatusRequest(booker.getId(), false, owner.getId());

        });
        String message = exception.getMessage();
        String actualMessage = "Вещь не найдена";
        assertEquals(message, actualMessage);
    }

    @Test
    void bookingRequestByUserId() {
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        Booking bookingGet = bookingService.getId(owner.getId(), booking.getId());
        assertEquals(bookingGet.getId(), booking.getId());
        assertEquals(bookingGet.getStart(), booking.getStart());
        assertEquals(bookingGet.getEnd(), booking.getEnd());
        assertEquals(bookingGet.getStatus(), booking.getStatus());
        assertNotNull(bookingGet.getItem());
        assertNotNull(bookingGet.getBooker());
    }

    @Test
    void checkingNonExistentUserId() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getId(owner.getId(), booking.getId());
        });
        String message = exception.getMessage();
        String actualMessage = "Пользователь не найден";
        assertEquals(message, actualMessage);
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getAllBookingUser(BookingState state) {
        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        switch (state) {
            case ALL:
                Mockito
                        .when(bookingRepository.findAllByBookerId(any(), any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(booking)));
                List<Booking> bookings = bookingService.getAllBookingUser(booker.getId(), state, 0, 10);
                assertEquals(bookings.size(), 1);
                break;
            case CURRENT:
                Mockito
                        .when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                                any(), any(), any(), any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(booking)));
                List<Booking> bookings1 = bookingService.getAllBookingUser(booker.getId(), state, 0, 10);
                assertEquals(bookings1.size(), 1);
                break;
            case PAST:
                Mockito
                        .when(bookingRepository.findAllByBookerIdAndEndBefore(
                                any(), any(), any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(booking)));
                List<Booking> bookings2 = bookingService.getAllBookingUser(booker.getId(), state, 0, 10);
                assertEquals(bookings2.size(), 1);
                break;
            case FUTURE:
                Mockito
                        .when(bookingRepository.findAllByBookerIdAndStartAfter(
                                any(), any(), any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(booking)));
                List<Booking> bookings3 = bookingService.getAllBookingUser(booker.getId(), state, 0, 10);
                assertEquals(bookings3.size(), 1);
                break;
            case WAITING:
            case REJECTED:
                Mockito
                        .when(bookingRepository.findAllByBookerIdAndStatus(
                                any(), any(), any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(booking)));
                List<Booking> bookings4 = bookingService.getAllBookingUser(booker.getId(), state, 0, 10);
                assertEquals(bookings4.size(), 1);
                break;
            case UNSUPPORTED_STATUS:
                UnknownStatusException exception = assertThrows(UnknownStatusException.class, () -> {
                    bookingService.getAllBookingUser(booker.getId(), state, 0, 10);
                });
                String message = exception.getMessage();
                String actualMessage = "Unknown state: UNSUPPORTED_STATUS";
                assertEquals(message, actualMessage);
        }
    }

    @Test
    void getAllBookingNotUser() {
        BookingState state = BookingState.ALL;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getAllBookingUser(booker.getId(), state, 0, 10);
        });
        String message = exception.getMessage();
        String actualMessage = "Пользователь не найден";
        assertEquals(message, actualMessage);
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getAllItemsBookingUser(BookingState state) {
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        switch (state) {
            case ALL:
                Mockito
                        .when(bookingRepository.findAllByItem_OwnerId(any(), any(Pageable.class)))
                        .thenReturn((List.of(booking)));
                List<Booking> bookings = bookingService.getAllItemsBookingUser(owner.getId(), state, 0, 10);
                assertEquals(bookings.size(), 1);
                break;
            case CURRENT:
                Mockito
                        .when(bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfter(
                                any(), any(), any(), any(Pageable.class)))
                        .thenReturn((List.of(booking)));
                List<Booking> bookings1 = bookingService.getAllItemsBookingUser(owner.getId(), state, 0, 10);
                assertEquals(bookings1.size(), 1);
                break;
            case PAST:
                Mockito
                        .when(bookingRepository.findAllByItem_OwnerIdAndEndBefore(
                                any(), any(), any(Pageable.class)))
                        .thenReturn((List.of(booking)));
                List<Booking> bookings2 = bookingService.getAllItemsBookingUser(owner.getId(), state, 0, 10);
                assertEquals(bookings2.size(), 1);
                break;
            case FUTURE:
                Mockito
                        .when(bookingRepository.findAllByItem_OwnerIdAndStartAfter(
                                any(), any(), any(Pageable.class)))
                        .thenReturn((List.of(booking)));
                List<Booking> bookings3 = bookingService.getAllItemsBookingUser(owner.getId(), state, 0, 10);
                assertEquals(bookings3.size(), 1);
                break;
            case WAITING:
            case REJECTED:
                Mockito
                        .when(bookingRepository.findAllByItem_OwnerIdAndStatus(
                                any(), any(), any(Pageable.class)))
                        .thenReturn((List.of(booking)));
                List<Booking> bookings4 = bookingService.getAllItemsBookingUser(owner.getId(), state, 0, 10);
                assertEquals(bookings4.size(), 1);
                break;
            case UNSUPPORTED_STATUS:
                UnknownStatusException exception = assertThrows(UnknownStatusException.class, () -> {
                    bookingService.getAllItemsBookingUser(owner.getId(), state, 0, 10);
                });
                String message = exception.getMessage();
                String actualMessage = "Unknown state: UNSUPPORTED_STATUS";
                assertEquals(message, actualMessage);
        }
    }

    @Test
    void getAllItemsBookingNotUser() {
        BookingState state = BookingState.ALL;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getAllItemsBookingUser(owner.getId(), state, 0, 10);
        });
        String message = exception.getMessage();
        String actualMessage = "Пользователь не найден";
        assertEquals(message, actualMessage);
    }
}