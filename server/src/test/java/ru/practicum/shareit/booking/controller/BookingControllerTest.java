package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.mapper.LinkedBookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.CommentResponseMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
@ContextConfiguration(
        classes = {
                GlobalExceptionHandler.class,
                ItemMapperImpl.class,
                CommentMapperImpl.class,
                BookingMapperImpl.class,
                CommentResponseMapperImpl.class,
                LinkedBookingMapperImpl.class,
                BookingController.class
        }
)
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BookingMapper bookingMapper;

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Test
    void canCreate() throws Exception {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(1L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(15));
        String json = objectMapper.writeValueAsString(bookingCreateRequest);
        User booker = new User(1L, "Tod", "user@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                booker, null, Collections.EMPTY_LIST, null, null);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now().plusMinutes(10));
        booking.setEndDate(LocalDateTime.now().plusMinutes(15));
        booking.setItem(itemFirst);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        BookingResponse bookingResponse = bookingMapper.toResponse(booking);

        when(bookingService.create(any(), anyLong())).thenReturn(booking);

        MvcResult result = mockMvc.perform(
                        post("/bookings")
                                .header(X_SHARER_USER_ID, booker.getId())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookingResponse bookingResponseRes = objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponse.class);
        Assertions.assertThat(bookingResponseRes).isEqualTo(bookingResponse);
    }

        @Test
    void canUpdate() throws Exception {
        User userOwner = new User(1L, "Tod", "user@user.com");
        User booker = new User(2L, "Bob", "user1@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                booker, null, Collections.EMPTY_LIST, null, null);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now().plusMinutes(10));
        booking.setEndDate(LocalDateTime.now().plusMinutes(15));
        booking.setItem(itemFirst);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        BookingResponse bookingResponse = bookingMapper.toResponse(booking);


        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        MvcResult result = mockMvc.perform(
                        patch("/bookings/{bookingId}", booking.getId())
                                .header(X_SHARER_USER_ID, userOwner.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponse.class);
        Assertions.assertThat(response).isEqualTo(bookingResponse);
    }

    @Test
    void canGetByExistingId() throws Exception {
        User booker = new User(1L, "Tod", "user@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                booker, null, Collections.EMPTY_LIST, null, null);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now().plusMinutes(10));
        booking.setEndDate(LocalDateTime.now().plusMinutes(15));
        booking.setItem(itemFirst);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        BookingResponse bookingResponse = bookingMapper.toResponse(booking);

        when(bookingService.findByIdAndUserId(anyLong(), anyLong())).thenReturn(booking);

        MvcResult result = mockMvc.perform(
                        get("/bookings/{bookingId}", booking.getId())
                                .header(X_SHARER_USER_ID, booker.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookingResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponse.class);
        Assertions.assertThat(response).isEqualTo(bookingResponse);
    }

    @Test
    void willReturnNotDataIntegrityViolation() throws Exception {

        when(bookingService.findAllByBooker(anyLong(), any(), anyInt(), anyInt())).thenThrow(new DataIntegrityViolationException("не найден"));
        mockMvc.perform(
                        get("/bookings")
                                .header(X_SHARER_USER_ID, 100L)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void canGetAllByBooker() throws Exception {
        User booker = new User(1L, "Tod", "user@user.com");
        User owner = new User(2L, "Bob", "user2@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                owner, null, Collections.EMPTY_LIST, null, null);
        Item itemSecond = new Item(2L, "Pen", "Write pen", true,
                owner, null, Collections.EMPTY_LIST, null, null);
        Booking bookingFirst = new Booking();
        bookingFirst.setId(1L);
        bookingFirst.setStartDate(LocalDateTime.now().plusMinutes(10));
        bookingFirst.setEndDate(LocalDateTime.now().plusMinutes(15));
        bookingFirst.setItem(itemFirst);
        bookingFirst.setBooker(booker);
        bookingFirst.setStatus(BookingStatus.WAITING);
        Booking bookingSecond = new Booking();
        bookingSecond.setId(2L);
        bookingSecond.setStartDate(LocalDateTime.now().plusMinutes(10));
        bookingSecond.setEndDate(LocalDateTime.now().plusMinutes(15));
        bookingSecond.setItem(itemSecond);
        bookingSecond.setBooker(booker);
        bookingSecond.setStatus(BookingStatus.WAITING);
        List<Booking> bookingList = List.of(bookingFirst, bookingSecond);

        List<BookingResponse> bookingResponseList = bookingMapper.toResponse(bookingList);

        when(bookingService.findAllByBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookingList);

        MvcResult result = mockMvc.perform(
                        get("/bookings")
                                .header(X_SHARER_USER_ID, booker.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<BookingResponse>  resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(resultMap.size()).isEqualTo(bookingResponseList.size());
        Assertions.assertThat(resultMap).containsExactlyInAnyOrderElementsOf(bookingResponseList);
    }

    @Test
    void canGetAllByOwner() throws Exception {
        User booker = new User(1L, "Tod", "user@user.com");
        User owner = new User(2L, "Bob", "user2@user.com");
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                owner, null, Collections.EMPTY_LIST, null, null);
        Item itemSecond = new Item(2L, "Pen", "Write pen", true,
                owner, null, Collections.EMPTY_LIST, null, null);
        Booking bookingFirst = new Booking();
        bookingFirst.setId(1L);
        bookingFirst.setStartDate(LocalDateTime.now().plusMinutes(10));
        bookingFirst.setEndDate(LocalDateTime.now().plusMinutes(15));
        bookingFirst.setItem(itemFirst);
        bookingFirst.setBooker(booker);
        bookingFirst.setStatus(BookingStatus.WAITING);
        Booking bookingSecond = new Booking();
        bookingSecond.setId(2L);
        bookingSecond.setStartDate(LocalDateTime.now().plusMinutes(10));
        bookingSecond.setEndDate(LocalDateTime.now().plusMinutes(15));
        bookingSecond.setItem(itemSecond);
        bookingSecond.setBooker(booker);
        bookingSecond.setStatus(BookingStatus.WAITING);
        List<Booking> bookingList = List.of(bookingFirst, bookingSecond);

        List<BookingResponse> bookingResponseList = bookingMapper.toResponse(bookingList);

        when(bookingService.findAllByOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookingList);

        MvcResult result = mockMvc.perform(
                        get("/bookings/owner")
                                .header(X_SHARER_USER_ID, owner.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<BookingResponse>  resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(resultMap.size()).isEqualTo(bookingResponseList.size());
        Assertions.assertThat(resultMap).containsExactlyInAnyOrderElementsOf(bookingResponseList);
    }

}