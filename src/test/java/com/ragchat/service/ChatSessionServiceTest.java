package com.ragchat.service;

import com.ragchat.dto.ChatDto;
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
class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private ChatMessageRepository messageRepository;

    @InjectMocks
    private ChatSessionService sessionService;

    private ChatSession mockSession;

    @BeforeEach
    void setUp() {
        mockSession = new ChatSession("Test Session", "testuser");
        mockSession.setId(1L);
    }

    @Test
    void createSession_shouldReturnSessionResponse() {
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(mockSession);

        ChatDto.CreateSessionRequest request = new ChatDto.CreateSessionRequest("Test Session");
        ChatDto.SessionResponse result = sessionService.createSession("testuser", request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Session");
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    void getSession_shouldReturnSession_whenExists() {
        when(sessionRepository.findByIdAndUsername(1L, "testuser")).thenReturn(Optional.of(mockSession));
        when(messageRepository.countBySessionId(1L)).thenReturn(3L);

        ChatDto.SessionResponse result = sessionService.getSession("testuser", 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMessageCount()).isEqualTo(3L);
    }

    @Test
    void getSession_shouldThrow_whenNotFound() {
        when(sessionRepository.findByIdAndUsername(99L, "testuser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSession("testuser", 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void renameSession_shouldUpdateName() {
        when(sessionRepository.findByIdAndUsername(1L, "testuser")).thenReturn(Optional.of(mockSession));
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(mockSession);
        when(messageRepository.countBySessionId(1L)).thenReturn(0L);

        ChatDto.RenameSessionRequest request = new ChatDto.RenameSessionRequest("New Name");
        sessionService.renameSession("testuser", 1L, request);

        assertThat(mockSession.getName()).isEqualTo("New Name");
        verify(sessionRepository).save(mockSession);
    }

    @Test
    void toggleFavorite_shouldFlipFavoriteStatus() {
        assertThat(mockSession.isFavorite()).isFalse();

        when(sessionRepository.findByIdAndUsername(1L, "testuser")).thenReturn(Optional.of(mockSession));
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(mockSession);
        when(messageRepository.countBySessionId(1L)).thenReturn(0L);

        sessionService.toggleFavorite("testuser", 1L);

        assertThat(mockSession.isFavorite()).isTrue();
    }

    @Test
    void deleteSession_shouldDeleteSession() {
        when(sessionRepository.findByIdAndUsername(1L, "testuser")).thenReturn(Optional.of(mockSession));

        sessionService.deleteSession("testuser", 1L);

        verify(sessionRepository, times(1)).delete(mockSession);
    }

    @Test
    void getSessions_shouldReturnPagedSessions() {
        Page<ChatSession> page = new PageImpl<>(List.of(mockSession), PageRequest.of(0, 20), 1);
        when(sessionRepository.findByUsernameOrderByUpdatedAtDesc(eq("testuser"), any())).thenReturn(page);
        when(messageRepository.countBySessionId(1L)).thenReturn(2L);

        Page<ChatDto.SessionResponse> result = sessionService.getSessions("testuser", 0, 20, false);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMessageCount()).isEqualTo(2L);
    }
}
