package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    private Booking bookingCURRENT;
    private Booking bookingPAST;
    private Booking bookingFUTURE;
    private Booking bookingWATTING;
    private Booking bookingREJECTED;
    Pageable pageable;
    private User booker;
    private User owner;
    private Item item;
    LocalDateTime currentTime;

    @BeforeEach
    void setUp() {
        currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        owner = new User(1L, "Lev", "lev@mail.ru");
        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("drill pro")
                .available(true)
                .owner(owner)
                .build();
        booker = new User(2L, "Mark", "mark@mail.ru");
        bookingPAST = new Booking(2L, currentTime.minusDays(10), currentTime.minusDays(5), item,
                booker, Status.APPROVED);
        bookingCURRENT = new Booking(1L, currentTime.minusDays(1), currentTime.plusDays(4), item,
                booker, Status.APPROVED);
        bookingFUTURE = new Booking(3L, currentTime.plusDays(6), currentTime.plusDays(8), item, booker,
                Status.APPROVED);
        bookingWATTING = new Booking(4L, currentTime.plusDays(9), currentTime.plusDays(10), item,
                booker, Status.WAITING);
        bookingREJECTED = new Booking(5L, currentTime.plusDays(11), currentTime.plusDays(12), item,
                booker, Status.REJECTED);
        pageable = Pageable.unpaged();
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(bookingCURRENT);
        bookingRepository.save(bookingPAST);
        bookingRepository.save(bookingFUTURE);
        bookingRepository.save(bookingWATTING);
        bookingRepository.save(bookingREJECTED);
    }

    @Test
    void findAllByBookerId() {
        List<Booking> bookings =
                bookingRepository.findAllByBookerId(booker.getId(), pageable).getContent();
        assertEquals(5, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfter() {
        List<Booking> bookings =
                bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(),
                        currentTime, currentTime, pageable).getContent();
        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        List<Booking> bookings =
                bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), currentTime, pageable).getContent();
        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
        List<Booking> bookings =
                bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), currentTime, pageable).getContent();
        assertEquals(3, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStatusWATTING() {
        List<Booking> bookings =
                bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.WAITING, pageable).getContent();
        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStatusREJECTED() {
        List<Booking> bookings =
                bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.REJECTED, pageable).getContent();
        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItem_OwnerId() {
        List<Booking> bookings =
                bookingRepository.findAllByItem_OwnerId(owner.getId(), pageable);
        assertEquals(5, bookings.size());
    }

    @Test
    void findAllByItem_OwnerIdAndStartBeforeAndEndAfter() {
        List<Booking> bookings
                = bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfter(owner.getId(), currentTime, currentTime,
                pageable);
        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItem_OwnerIdAndEndBefore() {
        List<Booking> bookings =
                bookingRepository.findAllByItem_OwnerIdAndEndBefore(owner.getId(), currentTime, pageable);
        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItem_OwnerIdAndStartAfter() {
        List<Booking> bookings =
                bookingRepository.findAllByItem_OwnerIdAndStartAfter(owner.getId(), currentTime, pageable);
        assertEquals(3, bookings.size());
    }

    @Test
    void findAllByItem_OwnerIdAndStatus() {
        List<Booking> bookings =
                bookingRepository.findAllByItem_OwnerIdAndStatus(owner.getId(), Status.WAITING, pageable);
        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemInAndStatusEquals() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings =
                bookingRepository.findByItemInAndStatusEquals(List.of(item), sort, Status.APPROVED);
        assertEquals(3, bookings.size());
    }

    @Test
    void findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc() {
        Booking booking = bookingRepository
        .findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc(owner.getId(),
                        item.getId(), currentTime, Status.APPROVED).orElse(null);
        assertEquals(bookingPAST.getId(), booking.getId());
        assertEquals(bookingPAST.getStart(), booking.getStart());
        assertEquals(bookingPAST.getEnd(), booking.getEnd());
        assertEquals(bookingPAST.getStatus(), booking.getStatus());
    }

    @Test
    void findFirstByItem_OwnerIdAndIdAndStartAfterAndStatusEqualsOrderByStart() {
        Booking booking = bookingRepository
                .findFirstByItem_OwnerIdAndIdAndStartAfterAndStatusEqualsOrderByStart(
                        owner.getId(), item.getId(), currentTime, Status.APPROVED).orElse(null);
        assertNull(booking);
    }

    @Test
    void findBookingByItem_IdAndBooker_IdAndEndBefore() {
        List<Booking> bookings =
                bookingRepository.findBookingByItem_IdAndBooker_IdAndEndBefore(item.getId(), owner.getId(),
                        currentTime);
        assertEquals(0, bookings.size());
    }
}