package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    private User owner;
    private User requestor;
    private ItemRequest itemRequest;
    private Item item;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Lev", "lev@mail.ru");
        requestor = new User(2L, "Vova", "vova@mail.ru");
        itemRequest = new ItemRequest(1L, "drill", requestor, LocalDateTime.now());
        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("drill pro")
                .available(true)
                .owner(owner)
                .requestId(itemRequest)
                .build();
        pageable = Pageable.unpaged();
        userRepository.save(owner);
        userRepository.save(requestor);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
    }

    @Test
    void search() {
        String text = "drill";
        List<Item> items = itemRepository.search(text);
        assertEquals(1, items.size());
    }

    @Test
    void findAllByOwnerIdOrderById() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(owner.getId(), pageable);
        assertEquals(1, items.size());
    }

    @Test
    void findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue() {
        String text = "drill";
        List<Item> items = itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text, pageable);
        assertEquals(1, items.size());
    }

    @Test
    void findAllByRequestIdIn() {
        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(itemRequest));
        assertEquals(1, items.size());
    }

    @Test
    void findByRequestId() {
        List<Item> items = itemRepository.findByRequestId(itemRequest);
        assertEquals(1, items.size());
    }
}