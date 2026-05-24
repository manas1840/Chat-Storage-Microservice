package com.ragchat.service;

import com.ragchat.dto.ChatDto;
import com.ragchat.entity.ChatMessage;
import com.ragchat.entity.ChatSession;
import com.ragchat.exception.ResourceNotFoundException;
import com.ragchat.repository.ChatMessageRepository;
import com.ragchat.repository.ChatSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private ChatSessionRepository sessionRepository;

    @InjectMocks
    private ChatMessageService messageService;

    private ChatSession mockSession;

    @BeforeEach
    void setUp() {
        mockSession = new ChatSession("Test Session", "testuser");
        mockSession.setId(1L);
    }

    @Test
    void addMessage_shouldSaveAndReturnMessage() {
        ChatMessage saved = new ChatMessage(mockSession, ChatMessage.SenderType.USER, "Hello AI!", null);
        saved.setId(10L);

        when(sessionRepository.findByIdAndUsername(1L, "testuser")).thenReturn(Optional.of(mockSession));
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(saved);
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(mockSession);

        ChatDto.AddMessageRequest request = new ChatDto.AddMessageRequest();
        request.setSender(ChatMessage.SenderType.USER);
        request.setContent("Hello AI!");

        ChatDto.MessageResponse result = messageService.addMessage("testuser", 1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello AI!");
        assertThat(result.getSender()).isEqualTo(ChatMessage.SenderType.USER);
    }

    @Test
    void addMessage_shouldThrow_whenSessionNotFound() {
        when(sessionRepository.findByIdAndUsername(99L, "testuser")).thenReturn(Optional.empty());

        ChatDto.AddMessageRequest request = new ChatDto.AddMessageRequest();
        request.setSender(ChatMessage.SenderType.USER);
        request.setContent("Hello!");

        assertThatThrownBy(() -> messageService.addMessage("testuser", 99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getMessages_shouldReturnPagedMessages() {
        ChatMessage msg = new ChatMessage(mockSession, ChatMessage.SenderType.ASSISTANT, "Hi!", null);
        msg.setId(5L);

        Page<ChatMessage> page = new PageImpl<>(List.of(msg), PageRequest.of(0, 20), 1);

        when(sessionRepository.findByIdAndUsername(1L, "testuser")).thenReturn(Optional.of(mockSession));
        when(messageRepository.findBySessionIdOrderByCreatedAtAsc(eq(1L), any())).thenReturn(page);

        Page<ChatDto.MessageResponse> result = messageService.getMessages("testuser", 1L, 0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Hi!");
    }
}
