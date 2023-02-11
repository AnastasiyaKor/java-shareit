package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
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
    }

    @Test
    void findAllByRequestor_Id() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(requestor.getId());
        assertEquals(1, itemRequests.size());
    }

    @Test
    void findAllByRequestor_IdNot() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdNot(requestor.getId(), pageable);
        assertEquals(0, itemRequests.size());
    }
}