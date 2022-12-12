package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoLastNext;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatorException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item add(Item item, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item, Long userId, Long itemId) {
        userRepository.findById(userId);
        Item updateItem = getById(itemId);
        if (Objects.equals(updateItem.getOwner().getId(), userId)) {
            if (item.getName() != null) {
                updateItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                updateItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null && item.getAvailable() != updateItem.getAvailable()) {
                updateItem.setAvailable(item.getAvailable());
            }
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
        return itemRepository.save(updateItem);
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
        Optional<Booking> last = bookingRepository
                .findFirstByItem_Owner_IdAndItem_IdOrderByStart(userId, item.getId());
        if (last.isPresent()) {
            lastBooking = BookingMapper.toBookingDtoLastNext(last.get());
        }
        Optional<Booking> next = bookingRepository
                .findFirstByItem_OwnerIdAndIdOrderByStartDesc(userId, item.getId());
        if (next.isPresent()) {
            nextBooking = BookingMapper.toBookingDtoLastNext(next.get());
        }

        List<Comment> comments = getAllByItemId(itemId);
        if (comments.isEmpty()) {
            return ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, Collections.emptyList());
        }
        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, commentsDto);
    }

    @Override
    public List<ItemBookingDto> getAll(Long userId) {
        userRepository.findById(userId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        List<ItemBookingDto> itemsBooking = new ArrayList<>();
        for (Item item : items) {
            BookingDtoLastNext lastBooking = null;
            BookingDtoLastNext nextBooking = null;
            Optional<Booking> last = bookingRepository
                    .findFirstByItem_Owner_IdAndItem_IdOrderByStart(userId, item.getId());
            Optional<Booking> next = bookingRepository
                    .findFirstByItem_OwnerIdAndIdOrderByStartDesc(userId, item.getId());
            if (last.isPresent()) {
                lastBooking = BookingMapper.toBookingDtoLastNext(last.get());
            }
            if (next.isPresent()) {
                nextBooking = BookingMapper.toBookingDtoLastNext(next.get());
            }
            List<Comment> comments = getAllByItemId(item.getId());
            if (comments.isEmpty()) {
                itemsBooking.add(ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, Collections.emptyList()));
            } else {
                List<CommentDto> commentsDto = comments.stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList());
                itemsBooking.add(ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, commentsDto));
            }
        }
        return itemsBooking;
    }

    @Override
    public List<Item> search(String text, long userId) {
        userRepository.findById(userId);
        return (itemRepository.findAll()).stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Comment createComment(Comment comment) {
        LocalDateTime dateTime = LocalDateTime.now();
        Long itemId = comment.getItem().getId();
        Long userId = comment.getAuthor().getId();
        List<Booking> bookings = bookingRepository.findBookingByItem_IdAndBooker_IdAndEndBefore(
                itemId, userId, dateTime);
        if (bookings.isEmpty() || comment.getText().isEmpty()) {
            throw new ValidatorException("Нельзя ставить отзыв без бронирования, нельзя отправлять пустой текст");
        }
        comment.setCreated(dateTime);
        return commentRepository.save(comment);


    }

    @Override
    public List<Comment> getAllByItemId(Long itemId) {
        getById(itemId);
        return commentRepository.findAllByItemId(itemId);
    }
}
