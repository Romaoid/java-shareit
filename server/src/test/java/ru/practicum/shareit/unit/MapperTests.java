package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingView;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForAnswerRequest;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AnswerRequestDto;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class MapperTests {
    private final LocalDateTime startDate = LocalDateTime.now().plusDays(1);
    private final LocalDateTime endDate = LocalDateTime.now().plusDays(2);
    private ItemRequest itemRequest;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test description");

        user = new User();
        user.setId(1L);
        user.setName("Test name");
        user.setEmail("Test@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test text");
        item.setAvailable(true);
        item.setOwner(1L);
    }

    @Test
    public void userMapper_shouldMapUserToUserDto() {
        UserDto result = UserMapper.mapToUserDto(user);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId() == 1L)
                .matches(dto -> dto.getName().equals("Test name"))
                .matches(dto -> dto.getEmail().equals("Test@example.com"));
    }

    @Test
    public void userMapper_shouldMapUserFromCreateReq() {
        UserRequestDto request = new UserRequestDto();
        request.setName("Test name");
        request.setEmail("Test@example.com");

        User result = UserMapper.mapUserFromCreateReq(request);

        assertThat(result)
                .isNotNull()
                .matches(user -> user.getName().equals("Test name"))
                .matches(user -> user.getEmail().equals("Test@example.com"));
    }

    @Test
    public void requestMapper_shouldMapToDto() {
        NewRequestDto result = RequestMapper.mapToDto(itemRequest);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId() == 1L)
                .matches(dto -> dto.getDescription().equals("test description"))
                .matches(dto -> dto.getCreated().equals(itemRequest.getCreated()));
    }

    @Test
    public void requestMapper_shouldMapToDtoWithAnswers() {
        itemRequest.setItems(Set.of(item));

        AnswerRequestDto result = RequestMapper.mapToDtoWithAnswers(itemRequest);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId() == 1L)
                .matches(dto -> dto.getDescription().equals("test description"))
                .matches(dto -> dto.getCreated().equals(itemRequest.getCreated()));

        assertThat(result.getItems())
                .hasSize(1)
                .extracting(
                        ItemDtoForAnswerRequest::getId,
                        ItemDtoForAnswerRequest::getName,
                        ItemDtoForAnswerRequest::getOwnerId)
                .containsExactly(
                        tuple(1L, "Test item", 1L));
    }

    @Test
    public void itemMapper_shouldMapToItemDto() {
        ItemDto result = ItemMapper.mapToItemDto(item);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId() == 1L)
                .matches(dto -> dto.getDescription().equals("Test text"))
                .matches(dto -> dto.getName().equals("Test item"))
                .extracting(ItemDto::isAvailable)
                .isEqualTo(true);
    }

    @Test
    public void itemMapper_shouldMapToItemDtoWithComments() {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("text comment");
        comment.setAuthorName("test user");
        comment.setCreated(LocalDateTime.now());

        LocalDateTime last = LocalDateTime.now().minusDays(1);
        LocalDateTime next = LocalDateTime.now().plusDays(1);

        ItemDtoForOwner result = ItemMapper.mapToItemDto(item, last, next, List.of(comment));

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId() == 1L)
                .matches(dto -> dto.getDescription().equals("Test text"))
                .matches(dto -> dto.getName().equals("Test item"))
                .matches(dto -> dto.getLastBooking().equals(last))
                .matches(dto -> dto.getNextBooking().equals(next))
                .extracting(ItemDtoForOwner::isAvailable)
                .isEqualTo(true);

        assertThat(result.getComments())
                .isNotNull()
                .extracting(
                        CommentDto::getId,
                        CommentDto::getText,
                        CommentDto::getAuthorName,
                        CommentDto::getCreated)
                .containsExactly(
                        tuple(1L, "text comment", "test user", comment.getCreated()));
    }

    @Test
    public void itemMapper_shouldMapToItemDtoForAnswerRequest() {
        ItemDtoForAnswerRequest result = ItemMapper.mapToItemDtoForAnswerRequest(item);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test item");
        assertThat(result.getOwnerId()).isEqualTo(1L);
    }

    @Test
    public void itemMapper_shouldMapToItem() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("description");
        dto.setName("name");
        dto.setAvailable(true);

        Item result = ItemMapper.mapItemFromCreateReq(dto);

        assertThat(result)
                .isNotNull()
                .matches(itm -> itm.getAvailable().equals(true))
                .matches(itm -> itm.getDescription().equals(dto.getDescription()))
                .matches(itm -> itm.getName().equals(dto.getName()));

        dto.setDescription("description update");
        dto.setName("name update");
        dto.setAvailable(false);

        Item resultUpdated = ItemMapper.mapItemFromUpdateReq(result, dto);

        assertThat(resultUpdated)
                .isNotNull()
                .matches(itm -> itm.getAvailable().equals(false))
                .matches(itm -> itm.getDescription().equals(dto.getDescription()))
                .matches(itm -> itm.getName().equals(dto.getName()));
    }

    @Test
    public void bookingMapper_shouldMapRequestToBooking() {
        BookingRequest request = new BookingRequest();

        request.setItemId(2L);
        request.setBookerId(3L);
        request.setStart(startDate);
        request.setEnd(endDate);

        Booking result = BookingMapper.mapRequestToBooking(request);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getItem().getId() == 2L)
                .matches(dto -> dto.getBooker().getId() == 3L)
                .matches(dto -> dto.getStatus().equals(BookingStatus.WAITING))
                .matches(dto -> dto.getEndDate().equals(endDate))
                .matches(dto -> dto.getStartDate().equals(startDate));
    }

    @Test
    public void bookingMapper_shouldMapToBookingDto() {
        Booking book = new Booking();
        book.setStatus(BookingStatus.APPROVED);
        book.setBooker(user);
        book.setItem(item);
        book.setStartDate(startDate);
        book.setEndDate(endDate);
        book.setId(4L);

        BookingDto result = BookingMapper.mapToBookingDto(book);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId().equals(book.getId()))
                .matches(dto -> dto.getItem().getId().equals(item.getId()))
                .matches(dto -> dto.getItem().getName().equals(item.getName()))
                .matches(dto -> dto.getBooker().getId().equals(user.getId()))
                .matches(dto -> dto.getBooker().getName().equals(user.getName()))
                .matches(dto -> dto.getStatus().equals(book.getStatus().toString()))
                .matches(dto -> dto.getEnd().equals(endDate))
                .matches(dto -> dto.getStart().equals(startDate));
    }

    @Test
    public void bookingMapper_shoul() {
        BookingView view = new BookingView() {
            @Override
            public Long getId() {
                return 5L;
            }

            @Override
            public LocalDateTime getStartDate() {
                return startDate;
            }

            @Override
            public LocalDateTime getEndDate() {
                return endDate;
            }

            @Override
            public BookingStatus getStatus() {
                return BookingStatus.REJECTED;
            }

            @Override
            public Long getItemId() {
                return item.getId();
            }

            @Override
            public String getItemName() {
                return item.getName();
            }

            @Override
            public Long getBookerId() {
                return user.getId();
            }

            @Override
            public String getBookerName() {
                return user.getName();
            }
        };

        BookingDto result = BookingMapper.mapToBookingDto(view);

        assertThat(result)
                .isNotNull()
                .matches(dto -> dto.getId().equals(view.getId()))
                .matches(dto -> dto.getItem().getId().equals(item.getId()))
                .matches(dto -> dto.getItem().getName().equals(item.getName()))
                .matches(dto -> dto.getBooker().getId().equals(user.getId()))
                .matches(dto -> dto.getBooker().getName().equals(user.getName()))
                .matches(dto -> dto.getStatus().equals(view.getStatus().toString()))
                .matches(dto -> dto.getEnd().equals(endDate))
                .matches(dto -> dto.getStart().equals(startDate));
    }

}
