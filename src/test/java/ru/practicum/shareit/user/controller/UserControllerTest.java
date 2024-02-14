package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.user.controller.dto.UserCreateRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.controller.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(
        classes = {
                GlobalExceptionHandler.class,
                UserMapperImpl.class,
                UserController.class
        }
)
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserMapper userMapper;

    @Test
    void canGetByExistingId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        User user = new User(1L, "Tod", "user@user.com");
        UserResponse userResponse = userMapper.toResponse(user);

        when(userService.findById(anyLong())).thenReturn(user);

        MvcResult result = mockMvc.perform(
                        get("/users/{userId}", user.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponse userResponseRes = mapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
        Assertions.assertThat(userResponseRes).isEqualTo(userResponse);
    }

    @Test
    void canGetAll() throws Exception {
        User userFirst = new User(1L, "Tod", "user@user.com");
        User userSecond = new User(2L, "Bob", "user2@user.com");
        List<User> userList = List.of(userFirst, userSecond);
        List<UserResponse> userResponseList = userMapper.toResponse(userList);

        when(userService.findAll()).thenReturn(userList);

        MvcResult result = mockMvc.perform(
                        get("/users")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        List<UserResponse> userResponses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(userResponses.size()).isEqualTo(userResponseList.size());
        Assertions.assertThat(userResponses).containsExactlyInAnyOrderElementsOf(userResponseList);
    }

    @Test
    void canCreate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User(1L, "Tod", "user@user.com");
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName("Tod");
        userCreateRequest.setEmail("user@user.com");
        UserResponse userResponse = userMapper.toResponse(user);
        String json = objectMapper.writeValueAsString(userCreateRequest);

        when(userService.create(any())).thenReturn(user);

        MvcResult result = mockMvc.perform(
                        post("/users")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
        Assertions.assertThat(response).isEqualTo(userResponse);
    }

    @Test
    void canUpdate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User(1L, "NameUpdate", "update@user.com");
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setId(1L);
        userUpdateRequest.setName("NameUpdate");
        userUpdateRequest.setEmail("update@user.com");
        String json = objectMapper.writeValueAsString(userUpdateRequest);
        UserResponse userResponse = userMapper.toResponse(user);

        when(userService.update(any())).thenReturn(user);

        MvcResult result = mockMvc.perform(
                        patch("/users/{userId}", user.getId())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
        Assertions.assertThat(response).isEqualTo(userResponse);
    }

    @Test
    void willReturnDuplicate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setId(1L);
        userUpdateRequest.setName("NameUpdate");
        userUpdateRequest.setEmail("update@user.com");
        String json = objectMapper.writeValueAsString(userUpdateRequest);

        when(userService.update(any())).thenThrow(new DuplicateException("дубликат"));

        mockMvc.perform(
                        patch("/users/{userId}", 1L)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void canDelete() throws Exception {
        mockMvc.perform(
                        delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }

}