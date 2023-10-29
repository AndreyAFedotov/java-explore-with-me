package ru.practicum.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.dto.CommentDtoResponseLong;
import ru.practicum.enums.MessageUpdateInitiator;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public static Comment toNewComment(Event event,
                                       User user,
                                       Comment replyTo,
                                       CommentDtoRequest request,
                                       MessageUpdateInitiator messageUpdateInitiator) {

        LocalDateTime time = LocalDateTime.now();

        return Comment.builder()
                .event(event)
                .author(user)
                .replyToId(replyTo)
                .answered(false)
                .text(request.getText())
                .createdOn(time)
                .lastUpdatedOn(time)
                .updateInitiator(messageUpdateInitiator)
                .build();
    }

    public static CommentDtoResponse toCommentDtoResponse(Comment result) {

        CommentDtoResponse response = CommentDtoResponse.builder()
                .id(result.getId())
                .author(result.getAuthor().getId())
                .answered(result.getAnswered())
                .text(result.getText())
                .createdOn(result.getCreatedOn())
                .lastUpdatedOn(result.getLastUpdatedOn())
                .updateInitiator(result.getUpdateInitiator())
                .build();

        if (result.getReplyToId() != null) {
            response.setReplyToId(result.getReplyToId().getId());
        }
        return response;
    }

    public static CommentDtoResponseLong toCommentDtoResponseLong(Comment result) {
        return CommentDtoResponseLong.builder()
                .id(result.getId())
                .event(result.getEvent().getId())
                .author(result.getAuthor().getId())
                .replyToId(result.getReplyToId().getId())
                .answered(result.getAnswered())
                .text(result.getText())
                .createdOn(result.getCreatedOn())
                .lastUpdatedOn(result.getLastUpdatedOn())
                .updateInitiator(result.getUpdateInitiator())
                .build();
    }
}
