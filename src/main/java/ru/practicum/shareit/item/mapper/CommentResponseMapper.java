package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentResponseMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toResponse(List<Comment> comments);
}
