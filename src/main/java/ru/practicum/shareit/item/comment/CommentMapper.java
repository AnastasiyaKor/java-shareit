package ru.practicum.shareit.item.comment;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment fromCommentRequestDto(CommentRequestDto commentRequestDto) {
        return Comment.builder()
                .id(commentRequestDto.getId())
                .text(commentRequestDto.getText())
                .build();
    }
}
