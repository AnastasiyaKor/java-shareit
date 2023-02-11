package ru.practicum.shareit.item.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> add(ItemDto dto, Long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> createComment(CommentRequestDto commentRequestDto, Long userId, Long id) {
        return post("/" + id + "/comment", userId, commentRequestDto);
    }

    public ResponseEntity<Object> update(ItemDto dto, Long userId, Long id) {
        return patch("/" + id,
                userId, dto);
    }

    public ResponseEntity<Object> getByItemId(Long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> search(Long userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> deleteById(Long userId, Long id) {
        return delete("/" + id, userId);
    }
}
