package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResult;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequest create(ItemRequest itemRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDtoResult> getAllUser(Long userId) {
        List<ItemRequestDtoResult> getAllItemRequest = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId);
        Map<ItemRequest, List<Item>> items = itemRepository.findAllByRequestIdInAndAvailableTrue(itemRequests)
                .stream()
                .collect(groupingBy(Item::getRequestId, toList()));
        return getItemRequestDtoResults(getAllItemRequest, itemRequests, items);
    }

    @Override
    public List<ItemRequestDtoResult> getAll(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequestDtoResult> getAllItemRequest = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdNot(userId, pageable);
        Map<ItemRequest, List<Item>> items = itemRequestListMap(itemRequests);
        return getItemRequestDtoResults(getAllItemRequest, itemRequests, items);
    }

    @Override
    public ItemRequestDtoResult getRequestId(Long userId, Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("запрос не найден"));
        List<Item> items = itemRepository.findByRequestId(itemRequest);
        List<ItemItemRequestDto> itemsToAnswer;
        if (!items.isEmpty()) {
            itemsToAnswer = items.stream()
                    .map(ItemMapper::toItemItemRequestDto)
                    .collect(toList());
            return ItemRequestMapper.toItemRequestItemDto(itemRequest, itemsToAnswer);
        }
        return ItemRequestMapper.toItemRequestItemDto(itemRequest, Collections.emptyList());
    }

    private List<ItemRequestDtoResult> getItemRequestDtoResults(List<ItemRequestDtoResult> getAllItemRequest,
                                                                List<ItemRequest> itemRequests, Map<ItemRequest,
            List<Item>> items) {
        for (ItemRequest ir : itemRequests) {
            ItemRequestDtoResult itemRequestDtoResult;
            List<ItemItemRequestDto> itemsToAnswer;
            if (!items.isEmpty()) {
                itemsToAnswer = items.get(ir)
                        .stream()
                        .filter(item -> item.getRequestId().getId().equals(ir.getId()))
                        .map(ItemMapper::toItemItemRequestDto)
                        .collect(Collectors.toList());
                itemRequestDtoResult = ItemRequestMapper.toItemRequestItemDto(ir, itemsToAnswer);
                getAllItemRequest.add(itemRequestDtoResult);
            } else {
                itemRequestDtoResult = ItemRequestMapper.toItemRequestItemDto(ir, Collections.emptyList());
                getAllItemRequest.add(itemRequestDtoResult);
            }
        }
        return getAllItemRequest;
    }

    private Map<ItemRequest, List<Item>> itemRequestListMap(List<ItemRequest> itemRequests) {
        Map<ItemRequest, List<Item>> items = itemRepository.findAllByRequestIdInAndAvailableTrue(itemRequests)
                .stream()
                .collect(groupingBy(Item::getRequestId, toList()));
        return items;
    }
}
