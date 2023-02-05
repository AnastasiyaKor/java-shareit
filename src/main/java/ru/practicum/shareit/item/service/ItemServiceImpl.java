package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoLastNext;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatorException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public Item add(Item item, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        item.setOwner(user);
        if (item.getRequestId() == null) {
            item.setRequestId(null);
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Item item, Long userId, Long itemId) {
        userRepository.findById(userId);
        Item updateItem = getById(itemId);
        if (Objects.equals(updateItem.getOwner().getId(), userId)) {
            if (item.getName() != null && !item.getName().isBlank()) {
                updateItem.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                updateItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                updateItem.setAvailable(item.getAvailable());
            }
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
        return updateItem;
    }

    @Override
    public Item getById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        return item.get();
    }

    @Override
    public ItemBookingDto getByItemId(Long itemId, Long userId) {
        userRepository.findById(userId);
        Item item = getById(itemId);
        BookingDtoLastNext lastBooking = null;
        BookingDtoLastNext nextBooking = null;
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Optional<Booking> last = bookingRepository
                .findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc(
                        userId, item.getId(), currentTime, Status.APPROVED);
        if (last.isPresent()) {
            lastBooking = BookingMapper.toBookingDtoLastNext(last.get());
        }
        Optional<Booking> next = bookingRepository
                .findFirstByItem_OwnerIdAndIdAndStartAfterAndStatusEqualsOrderByStart(
                        userId, item.getId(), currentTime, Status.APPROVED);
        if (next.isPresent()) {
            nextBooking = BookingMapper.toBookingDtoLastNext(next.get());
        }
        List<Comment> comments = getAllByItemId(itemId);
        if (comments.isEmpty()) {
            return ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, Collections.emptyList());
        }
        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
        return ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, commentsDto);
    }

    @Override
    public List<ItemBookingDto> getAll(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        LocalDateTime now = LocalDateTime.now();
        userRepository.findById(userId);
        List<ItemBookingDto> itemsBooking = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId, pageable);
        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        Map<Item, List<Booking>> lastNextBooking = bookingRepository
                .findByItemInAndStatusEquals(items, Sort.by(ASC, "start"), Status.APPROVED)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));
        for (Item item : items) {
            BookingDtoLastNext lastBooking = null;
            BookingDtoLastNext nextBooking = null;
            if (!lastNextBooking.isEmpty()) {
                if (lastNextBooking.get(item) != null) {
                    lastBooking = BookingMapper.toBookingDtoLastNext(Objects.requireNonNull(lastNextBooking.get(item)
                            .stream()
                            .filter(booking -> !booking.getStart().isAfter(now))
                            .max(Comparator.comparing(Booking::getStart)).orElse(null)));
                    nextBooking = BookingMapper.toBookingDtoLastNext((Objects.requireNonNull(lastNextBooking.get(item)
                            .stream()
                            .filter(booking -> booking.getStart().isAfter(now))
                            .sorted()
                            .findFirst().orElse(null))));
                }
            }
            if (comments.isEmpty()) {
                itemsBooking.add(ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, Collections.emptyList()));
            } else {
                if (comments.get(item) != null) {
                    List<CommentDto> commentsDto = comments.get(item).stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    itemsBooking.add(ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, commentsDto));
                }
            }
        }
        return itemsBooking;
    }

    @Override
    public List<Item> search(String text, long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        userRepository.findById(userId);
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                text, text, pageable);
    }

    @Override
    @Transactional
    public Comment createComment(CommentRequestDto commentRequestDto, Long userId, Long itemId) {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Item item = getById(itemId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        List<Booking> bookings = bookingRepository.findBookingByItem_IdAndBooker_IdAndEndBefore(
                itemId, userId, dateTime);
        if (bookings.isEmpty()) {
            throw new ValidatorException("Нельзя ставить отзыв без бронирования, нельзя отправлять пустой текст");
        }
        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        comment.setCreated(dateTime);
        comment.setItem(item);
        comment.setAuthor(user);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getAllByItemId(Long itemId) {
        getById(itemId);
        return commentRepository.findAllByItemId(itemId);
    }
}
