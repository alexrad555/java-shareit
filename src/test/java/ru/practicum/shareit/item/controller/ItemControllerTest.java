package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.mapper.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.http.RequestHeader.X_SHARER_USER_ID;

@WebMvcTest(ItemController.class)
@ContextConfiguration(
        classes = {
                ItemMapperImpl.class,
                CommentMapperImpl.class,
                BookingMapperImpl.class,
                CommentResponseMapperImpl.class,
                LinkedBookingMapperImpl.class,
                ItemController.class
        }
)
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    CommentMapper commentMapper;

    @Test
    void canGetByExistingId() throws Exception {
        User itemOwner = new User(1L, "Tod", "user@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null);
        ItemResponse itemResponse = itemMapper.toResponse(itemFirst);

        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemFirst);

        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mockMvc.perform(
                get("/items/{itemId}", itemFirst.getId())
                        .header(X_SHARER_USER_ID, itemOwner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemResponse response = mapper.readValue(result.getResponse().getContentAsString(), ItemResponse.class);
        Assertions.assertThat(response).isEqualTo(itemResponse);
    }

    @Test
    void canGetAll() throws Exception {
        User itemOwner = new User(1L, "Tod", "user@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null);
        Item itemSecond = new Item(2L, "Pen", "Read pen", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null);
        List<Item> itemList = List.of(itemFirst, itemSecond);
        List<ItemResponse> itemResponseList = itemMapper.toResponse(itemList);

        when(itemService.findAllByOwnerId(anyLong(), anyInt(), anyInt())).thenReturn(itemList);

        MvcResult result = mockMvc.perform(
                        get("/items")
                                .header(X_SHARER_USER_ID, itemOwner.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ItemResponse>  resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(resultMap.size()).isEqualTo(itemResponseList.size());
        Assertions.assertThat(resultMap).containsExactlyInAnyOrderElementsOf(itemResponseList);
    }

    @Test
    void canSearch() throws Exception {
        User itemOwner = new User(1L, "Tod", "user@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null);
        Item itemSecond = new Item(2L, "Pen", "Read pen", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null);
        List<Item> itemList = List.of(itemFirst, itemSecond);

        List<ItemResponse> itemResponseList = itemMapper.toResponse(itemList);
        when(itemService.search(any(), anyLong(), anyInt(), anyInt())).thenReturn(itemList);
        MvcResult result = mockMvc.perform(
                get("/items/search")
                        .header(X_SHARER_USER_ID, itemOwner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "Read"))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ItemResponse> resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(resultMap.size()).isEqualTo(itemResponseList.size());
        Assertions.assertThat(resultMap).containsExactlyInAnyOrderElementsOf(itemResponseList);
    }

    @Test
    void canCreate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User itemOwner = new User(1L, "Tod", "user@user.com");
        ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Book", "Read book", true, 2L);
        String json = objectMapper.writeValueAsString(itemCreateRequest);
        Item item = itemMapper.toItem(itemCreateRequest);
        ItemResponse itemResponse = itemMapper.toResponse(item);
        when(itemService.create(any(),anyLong())).thenReturn(item);

        MvcResult result = mockMvc.perform(
                        post("/items")
                                .header(X_SHARER_USER_ID, itemOwner.getId())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ItemResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ItemResponse.class);
        Assertions.assertThat(response).isEqualTo(itemResponse);
    }

    @Test
    void canUpdate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User itemOwner = new User(1L, "Tod", "user@user.com");
        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest();
        itemUpdateRequest.setId(1L);
        itemUpdateRequest.setName("updateName");
        itemUpdateRequest.setDescription("updateDesc");
        itemUpdateRequest.setAvailable(true);
        String json = objectMapper.writeValueAsString(itemUpdateRequest);
        Item item = itemMapper.toItemUpdate(itemUpdateRequest);
        ItemResponse itemResponse = itemMapper.toResponse(item);
        when(itemService.update(any(),anyLong(), anyLong())).thenReturn(item);

        MvcResult result = mockMvc.perform(
                        patch("/items/{itemId}", item.getId())
                                .header(X_SHARER_USER_ID, itemOwner.getId())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ItemResponse.class);
        Assertions.assertThat(response).isEqualTo(itemResponse);
    }

    @Test
    void canCreateComment() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User itemOwner = new User(1L, "Tod", "user@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null);
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setText("read book");
        String json = objectMapper.writeValueAsString(commentCreateRequest);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("read book");
        comment.setItem(itemFirst);
        comment.setAuthor(itemOwner);
        comment.setCreated(null);
        CommentResponse commentResponse = commentMapper.toResponse(comment);
        when(itemService.createComment(anyLong(),any(), anyLong())).thenReturn(comment);

        MvcResult result = mockMvc.perform(
                        post("/items/{itemId}/comment", itemFirst.getId())
                                .header(X_SHARER_USER_ID, itemOwner.getId())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CommentResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), CommentResponse.class);
        Assertions.assertThat(response.getText()).isEqualTo(commentResponse.getText());
    }
}