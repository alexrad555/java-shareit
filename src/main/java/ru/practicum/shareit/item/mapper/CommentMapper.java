package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface CommentMapper {
    Comment toComment(CommentEntity commentEntity);

    CommentEntity toEntity(Comment comment);

    List<Comment> toComment(List<CommentEntity> comments);

    List<CommentEntity> toEntity(List<Comment> comments);

    @Mapping(target = "authorName", source = "author.name")
    CommentResponse toResponse(Comment comment);
}
