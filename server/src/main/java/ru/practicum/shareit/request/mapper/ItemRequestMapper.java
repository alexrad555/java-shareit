package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface ItemRequestMapper {

    ItemRequestResponse toResponse(ItemRequest itemRequest);

    ItemRequest toRequest(ItemRequestEntity itemRequestEntity);

    List<ItemRequest> toRequest(List<ItemRequestEntity> itemRequestEntityList);

    List<ItemRequestResponse> toResponse(List<ItemRequest> itemRequestList);
}
