package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResult;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private User owner;
    private User requestor;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void init() {
        owner = new User(2L, "Liza", "Liza@mail.ru");
        requestor = new User(2L, "Vova", "vova@mail.ru");
        itemRequest = new ItemRequest(1L, "Drill pro", requestor, LocalDateTime.now());
        item = Item.builder()
                .id(1L)
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
        requestor = null;
        itemRequest = null;
        item = null;
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest);
        ItemRequest save = itemRequestService.create(itemRequest, owner.getId());
        assertEquals(save, itemRequest);
    }

    @Test
    void createUserNotFound() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            itemRequestService.create(itemRequest, owner.getId());
        });
        String message = notFoundException.getMessage();
        String actualMessage = "Пользователь не найден";
        assertEquals(message, actualMessage);
    }

    @Test
    void getAllUser() {
        List<ItemItemRequestDto> itemItemRequestDto = List.of(new ItemItemRequestDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getRequestId().getId()));
        ItemRequestDtoResult itemRequestDtoResult = new ItemRequestDtoResult(itemRequest.getId(),
                itemRequest.getDescription(), itemRequest.getCreated(), itemItemRequestDto);
        List<ItemRequestDtoResult> list = List.of(itemRequestDtoResult);
        Mockito
                .when(itemRequestRepository.findAllByRequestor_Id(owner.getId()))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(itemRepository.findAllByRequestIdIn(Mockito.any()))
                .thenReturn(List.of(item));
        List<ItemRequestDtoResult> itemRequestDtoResults = itemRequestService.getAllUser(owner.getId());
        assertEquals(itemRequestDtoResults, list);
    }

    @Test
    void getAll() {
        List<ItemItemRequestDto> itemItemRequestDto = List.of(new ItemItemRequestDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getRequestId().getId()));
        ItemRequestDtoResult itemRequestDtoResult = new ItemRequestDtoResult(itemRequest.getId(),
                itemRequest.getDescription(), itemRequest.getCreated(), itemItemRequestDto);
        List<ItemRequestDtoResult> list = List.of(itemRequestDtoResult);
        Mockito
                .when(itemRequestRepository.findAllByRequestor_IdNot(Mockito.any(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(itemRepository.findAllByRequestIdIn(Mockito.any()))
                .thenReturn(List.of(item));

        List<ItemRequestDtoResult> itemRequestDtoResults = itemRequestService.getAll(requestor.getId(), 0, 10);
        assertEquals(itemRequestDtoResults, list);
    }

    @Test
    void getAllNotItems() {
        ItemRequestDtoResult itemRequestDtoResult = new ItemRequestDtoResult(itemRequest.getId(),
                itemRequest.getDescription(), itemRequest.getCreated(), Collections.emptyList());
        List<ItemRequestDtoResult> list = List.of(itemRequestDtoResult);
        Mockito
                .when(itemRequestRepository.findAllByRequestor_IdNot(Mockito.any(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        List<ItemRequestDtoResult> itemRequestDtoResults = itemRequestService.getAll(requestor.getId(), 0, 10);
        assertEquals(itemRequestDtoResults, list);
    }

    @Test
    void getRequestId() {
        List<ItemItemRequestDto> itemItemRequestDto = List.of(new ItemItemRequestDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getRequestId().getId()));
        ItemRequestDtoResult itemRequestDtoResult = new ItemRequestDtoResult(itemRequest.getId(),
                itemRequest.getDescription(), itemRequest.getCreated(), itemItemRequestDto);
        Mockito
                .when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito
                .when(itemRepository.findByRequestId(itemRequest))
                .thenReturn(List.of(item));

        ItemRequestDtoResult get = itemRequestService.getRequestId(owner.getId(), itemRequest.getId());
        assertEquals(get, itemRequestDtoResult);
    }

    @Test
    void getRequestIdNotItems() {
        ItemRequestDtoResult itemRequestDtoResult = new ItemRequestDtoResult(itemRequest.getId(),
                itemRequest.getDescription(), itemRequest.getCreated(), Collections.emptyList());
        Mockito
                .when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        ItemRequestDtoResult get = itemRequestService.getRequestId(owner.getId(), itemRequest.getId());
        assertEquals(get, itemRequestDtoResult);
    }

    @Test
    void getNotFoundRequestId() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestId(owner.getId(), itemRequest.getId());
        });
        String message = notFoundException.getMessage();
        String actualMessage = "запрос не найден";
        assertEquals(message, actualMessage);
    }
}