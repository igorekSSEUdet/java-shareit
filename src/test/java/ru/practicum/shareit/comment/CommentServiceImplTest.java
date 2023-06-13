package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.comment.CommentRepository;
import ru.practicum.shareit.item.service.comment.CommentServiceImpl;
import ru.practicum.shareit.user.mapper.CommentDtoMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentDtoMapper commentDtoMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void addCommentTest() {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.existsByBookerIdAndItemIdAndEndTimeIsBefore(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(true);

        RequestCommentDto request = new RequestCommentDto();
        request.setText("comment");

        long authorId = 1L;
        long itemId = 1L;

        Comment comment = new Comment();
        Comment addedComment = comment;
        addedComment.setId(1L);
        comment.setText(request.getText());
        when(commentDtoMapper.toComment(request, authorId, itemId)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(addedComment);

        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText(request.getText());
        when(commentDtoMapper.toCommentDto(addedComment)).thenReturn(dto);

        CommentDto result = commentService.addComment(request, authorId, itemId);

        assertEquals(result.getId(), 1L);
        assertEquals(result.getText(), "comment");

    }
}
