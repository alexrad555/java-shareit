package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.mapper.LinkedBookingMapper;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.controller.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CommentResponseMapper.class, LinkedBookingMapper.class})
public interface ItemMapper {

    Item toItem(ItemCreateRequest request);

    Item toItemUpdate(ItemUpdateRequest request);

    @Mapping(target = "requestId", source = "request.id")
    ItemResponse toResponse(Item item);

    ItemEntity toEntity(Item item);

    Item toItem(ItemEntity itemEntity);

    List<Item> toItem(List<ItemEntity> items);

    List<ItemEntity> toEntity(List<Item> items);
}
