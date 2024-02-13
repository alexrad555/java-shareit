package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.mapper.LinkedBookingMapperImpl;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.CommentResponseMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreate;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ContextConfiguration(
        classes = {
                ItemMapperImpl.class,
                CommentMapperImpl.class,
                BookingMapperImpl.class,
                CommentResponseMapperImpl.class,
                LinkedBookingMapperImpl.class,
                ItemRequestMapperImpl.class,
                ItemRequestController.class
        }
)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ItemRequestMapper itemRequestMapper;

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Test
    void canCreate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ItemRequestCreate itemRequestCreate = new ItemRequestCreate();
        itemRequestCreate.setDescription("get book");
        User itemOwner = new User(1L, "Tod", "user@user.com");
        ItemRequest itemRequest = new ItemRequest(1L, "get book", null, Collections.EMPTY_LIST);
        String json = objectMapper.writeValueAsString(itemRequestCreate);
        ItemRequestResponse itemRequestResponse = itemRequestMapper.toResponse(itemRequest);

        when(itemRequestService.create(anyLong(),any())).thenReturn(itemRequest);
        MvcResult result = mockMvc.perform(
                        post("/requests")
                                .header(X_SHARER_USER_ID, itemOwner.getId())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemRequestResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ItemRequestResponse.class);
        Assertions.assertThat(response).isEqualTo(itemRequestResponse);
    }

    @Test
    void canFindAllOwnRequest() throws Exception {
        ItemRequest itemRequestFirst = new ItemRequest(1L, "get book", null, null);
        ItemRequest itemRequestSecond = new ItemRequest(2L, "get pen", null, null);
        List<ItemRequest> itemRequestList = List.of(itemRequestFirst, itemRequestSecond);
        User requestor = new User(1L, "Tod", "user@user.com");
        List<ItemRequestResponse> itemRequestResponse = itemRequestMapper.toResponse(itemRequestList);

        when(itemRequestService.findAllByOwnUserId(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestList);

        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mockMvc.perform(
                        get("/requests")
                                .header(X_SHARER_USER_ID, requestor.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<ItemRequestResponse> itemRequestResponseRes = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(itemRequestResponseRes).isEqualTo(itemRequestResponse);
    }

    @Test
    void canFindAllOtherRequest() throws Exception {
        ItemRequest itemRequestFirst = new ItemRequest(1L, "get book", null, null);
        ItemRequest itemRequestSecond = new ItemRequest(2L, "get pen", null, null);
        List<ItemRequest> itemRequestList = List.of(itemRequestFirst, itemRequestSecond);
        User user = new User(1L, "Tod", "user@user.com");
        List<ItemRequestResponse> itemRequestResponse = itemRequestMapper.toResponse(itemRequestList);

        when(itemRequestService.findAllByOtherUserId(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestList);

        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mockMvc.perform(
                        get("/requests/all")
                                .header(X_SHARER_USER_ID, user.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<ItemRequestResponse> itemRequestResponseRes = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(itemRequestResponseRes).isEqualTo(itemRequestResponse);
    }

    @Test
    void canGetByExistingId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ItemRequest itemRequestFirst = new ItemRequest(1L, "get book", null, null);
        ItemRequestResponse itemRequestResponse = itemRequestMapper.toResponse(itemRequestFirst);
        User user = new User(1L, "Tod", "user@user.com");

        when(itemRequestService.findById(anyLong(), anyLong())).thenReturn(itemRequestFirst);

        MvcResult result = mockMvc.perform(
                        get("/requests/{requestId}", itemRequestFirst.getId())
                                .header(X_SHARER_USER_ID, user.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemRequestResponse response = mapper.readValue(result.getResponse().getContentAsString(), ItemRequestResponse.class);
        Assertions.assertThat(response).isEqualTo(itemRequestResponse);
    }

}