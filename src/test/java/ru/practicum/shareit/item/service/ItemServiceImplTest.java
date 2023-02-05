package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatorException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    private User owner;
    private User user;
    private User requestor;
    private ItemRequest itemRequest;
    private Item item;
    private Item item1;
    private Booking booking;
    private static final Comment COMMENT = new Comment(1L, "good", Item.builder().id(1L).build(),
            User.builder().id(1L).build(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    private static final List<Comment> commentList = List.of(COMMENT);
    private static final User BOOKER = new User(3L, "Mark", "Mark@mail.ru");
    private static final Booking LAST = new Booking(1L, LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(2), Item.builder().id(1L).build(), BOOKER, Status.WAITING);
    private static final Booking NEXT = new Booking(1L, LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(1), Item.builder().id(1L).build(), BOOKER, Status.WAITING);

    public static Stream<Arguments> getBookingDtoLastNext() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(LAST, NEXT)
        );
    }

    @BeforeEach
    void init() {
        owner = new User(1L, "Masha", "Masha@mail.ru");
        user = new User(2L, "Liza", "Liza@mail.ru");
        requestor = new User(2L, "Vova", "vova@mail.ru");
        itemRequest = new ItemRequest(1L, "Drill pro", requestor, LocalDateTime.now());
        booking = new Booking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item, BOOKER, Status.WAITING);
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
                .requestId(itemRequest)
                .build();
    }

    @AfterEach
    void del() {
        owner = null;
        item = null;
        item1 = null;
    }


    @Test
    void add() {
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(item);
        Item itemAdd = itemService.add(item, owner.getId());
        assertEquals(itemAdd.getName(), item.getName());
        assertEquals(itemAdd.getDescription(), item.getDescription());
        assertEquals(itemAdd.getAvailable(), item.getAvailable());
        assertEquals(itemAdd.getOwner(), item.getOwner());
    }

    @Test
    void addNotUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            itemService.add(item, owner.getId());
        });
        String message = notFoundException.getMessage();
        String actualMessage = "Пользователь не найден";
        assertEquals(message, actualMessage);
    }

    @Test
    void update() {
        Item updateItem = Item.builder()
                .id(item.getId())
                .name("Drill")
                .description("Drill pro")
                .available(false)
                .owner(owner)
                .build();
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Item itemUpdate = itemService.update(updateItem, owner.getId(), item.getId());
        assertEquals(itemUpdate.getName(), updateItem.getName());
        assertEquals(itemUpdate.getDescription(), updateItem.getDescription());
        assertEquals(itemUpdate.getAvailable(), updateItem.getAvailable());
        assertEquals(itemUpdate.getOwner(), updateItem.getOwner());
    }

    @Test
    void updateUserIncorrect() {
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            itemService.update(item, user.getId(), item.getId());
        });
        String message = notFoundException.getMessage();
        String actualMessage = "Вещь не найдена";
        assertEquals(message, actualMessage);
    }

    @Test
    void getById() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Item itemGet = itemService.getById(item.getId());
        assertEquals(itemGet.getName(), item.getName());
        assertEquals(itemGet.getDescription(), item.getDescription());
        assertEquals(itemGet.getAvailable(), item.getAvailable());
        assertEquals(itemGet.getOwner(), item.getOwner());
    }

    @ParameterizedTest
    @MethodSource({"getBookingDtoLastNext"})
    void getByItemId(Booking last, Booking next) {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc(owner.getId(),
                                item.getId(), currentTime, Status.APPROVED))
                .thenReturn(Optional.ofNullable(last));
        Mockito.when(bookingRepository
                        .findFirstByItem_OwnerIdAndIdAndStartAfterAndStatusEqualsOrderByStart(owner.getId(),
                                item.getId(), currentTime, Status.APPROVED))
                .thenReturn(Optional.ofNullable(next));
        ItemBookingDto itemBookingDto1 = itemService.getByItemId(item.getId(), owner.getId());
        assertEquals(itemBookingDto1.getId(), item.getId());
        assertEquals(itemBookingDto1.getName(), item.getName());
        assertEquals(itemBookingDto1.getDescription(), item.getDescription());
        assertEquals(itemBookingDto1.getAvailable(), item.getAvailable());
        if (last == null || next == null) {
            assertNull(itemBookingDto1.getLastBooking());
            assertNull(itemBookingDto1.getNextBooking());
        } else {
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(itemBookingDto1.getLastBooking())
                        .usingRecursiveComparison()
                        .isEqualTo(BookingMapper.toBookingDtoLastNext(last));
            });
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(itemBookingDto1.getNextBooking())
                        .usingRecursiveComparison()
                        .isEqualTo(BookingMapper.toBookingDtoLastNext(next));
            });
        }
    }

    @Test
    void getByItemIdNoComments() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc(owner.getId(),
                                item.getId(), currentTime, Status.APPROVED))
                .thenReturn(Optional.of(LAST));
        Mockito.when(bookingRepository
                        .findFirstByItem_OwnerIdAndIdAndStartAfterAndStatusEqualsOrderByStart(owner.getId(),
                                item.getId(), currentTime, Status.APPROVED))
                .thenReturn(Optional.of(NEXT));
        ItemBookingDto itemBookingDto1 = itemService.getByItemId(item.getId(), owner.getId());
        assertEquals(itemBookingDto1.getId(), item.getId());
        assertEquals(itemBookingDto1.getName(), item.getName());
        assertEquals(itemBookingDto1.getDescription(), item.getDescription());
        assertEquals(itemBookingDto1.getAvailable(), item.getAvailable());
        assertEquals(itemBookingDto1.getComments(), Collections.emptyList());
    }

    @Test
    void getByItemIdComments() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc(
                                owner.getId(), item.getId(), currentTime, Status.APPROVED))
                .thenReturn(Optional.of(LAST));
        Mockito.when(bookingRepository
                        .findFirstByItem_OwnerIdAndIdAndStartAfterAndStatusEqualsOrderByStart(owner.getId(),
                                item.getId(), currentTime, Status.APPROVED))
                .thenReturn(Optional.of(NEXT));
        Mockito
                .when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(commentList);
        ItemBookingDto itemBookingDto1 = itemService.getByItemId(item.getId(), owner.getId());
        assertEquals(itemBookingDto1.getId(), item.getId());
        assertEquals(itemBookingDto1.getName(), item.getName());
        assertEquals(itemBookingDto1.getDescription(), item.getDescription());
        assertEquals(itemBookingDto1.getAvailable(), item.getAvailable());

        List<CommentDto> commentsDto = commentList.stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
        assertEquals(itemBookingDto1.getComments(), commentsDto);
    }


    @Test
    void getAll() {
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(itemRepository.findAllByOwnerIdOrderById(any(), any(Pageable.class)))
                .thenReturn(List.of(item));
        List<ItemBookingDto> itemBookingDto1 = itemService.getAll(owner.getId(), 0, 10);
        assertEquals(itemBookingDto1.size(), 1);
    }

    @Test
    void search() {
        String text = "drIll";
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(itemRepository
                        .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                                any(), any(), any(Pageable.class)))
                .thenReturn(List.of(item));
        List<Item> items = itemService.search(text, owner.getId(), 0, 10);
        assertEquals(items.size(), 1);
    }

    @Test
    void createComment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "good");
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(bookingRepository.findBookingByItem_IdAndBooker_IdAndEndBefore(
                        item.getId(), owner.getId(), dateTime))
                .thenReturn(List.of(booking));
        Mockito
                .when(commentRepository.save(Mockito.any()))
                .thenReturn(COMMENT);
        Comment commentCreate = itemService.createComment(commentRequestDto, owner.getId(), item.getId());
        assertEquals(commentCreate, COMMENT);
    }

    @Test
    void createCommentUserNotFound() {
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "good");
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            itemService.createComment(commentRequestDto, owner.getId(), item.getId());
        });
        String message = notFoundException.getMessage();
        String actualMessage = "Пользователь не найден";
        assertEquals(message, actualMessage);
    }

    @Test
    void createCommentBookingNotFound() {
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "good");
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        ValidatorException exception = assertThrows(ValidatorException.class, () -> {
            itemService.createComment(commentRequestDto, owner.getId(), item.getId());
        });
        String message = exception.getMessage();
        String actualMessage = "Нельзя ставить отзыв без бронирования, нельзя отправлять пустой текст";
        assertEquals(message, actualMessage);
    }
}