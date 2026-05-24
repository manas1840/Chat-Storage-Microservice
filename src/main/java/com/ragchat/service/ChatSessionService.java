package com.ragchat.service;

import com.ragchat.dto.ChatDto;
import com.ragchat.entity.ChatSession;
import com.ragchat.exception.ResourceNotFoundException;
import com.ragchat.repository.ChatMessageRepository;
import com.ragchat.repository.ChatSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatSessionService {

    private static final Logger log = LoggerFactory.getLogger(ChatSessionService.class);

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    public ChatSessionService(ChatSessionRepository sessionRepository,
                              ChatMessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public ChatDto.SessionResponse createSession(String username, ChatDto.CreateSessionRequest request) {
        log.info("Creating new chat session '{}' for user '{}'", request.getName(), username);
        ChatSession session = new ChatSession(request.getName(), username);
        ChatSession saved = sessionRepository.save(session);
        log.info("Created session with id={} for user='{}'", saved.getId(), username);
        return ChatDto.SessionResponse.from(saved, 0L);
    }

    @Transactional(readOnly = true)
    public Page<ChatDto.SessionResponse> getSessions(String username, int page, int size, boolean favoritesOnly) {
        log.debug("Fetching sessions for user='{}', page={}, size={}, favoritesOnly={}", username, page, size, favoritesOnly);
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatSession> sessions;
        if (favoritesOnly) {
            sessions = sessionRepository.findByUsernameAndFavoriteTrueOrderByUpdatedAtDesc(username, pageable);
        } else {
            sessions = sessionRepository.findByUsernameOrderByUpdatedAtDesc(username, pageable);
        }
        return sessions.map(s -> ChatDto.SessionResponse.from(s, messageRepository.countBySessionId(s.getId())));
    }

    @Transactional(readOnly = true)
    public ChatDto.SessionResponse getSession(String username, Long sessionId) {
        log.debug("Fetching session id={} for user='{}'", sessionId, username);
        ChatSession session = findSessionByIdAndUsername(sessionId, username);
        long count = messageRepository.countBySessionId(sessionId);
        return ChatDto.SessionResponse.from(session, count);
    }

    @Transactional
    public ChatDto.SessionResponse renameSession(String username, Long sessionId, ChatDto.RenameSessionRequest request) {
        log.info("Renaming session id={} to '{}' for user='{}'", sessionId, request.getName(), username);
        ChatSession session = findSessionByIdAndUsername(sessionId, username);
        session.setName(request.getName());
        ChatSession updated = sessionRepository.save(session);
        long count = messageRepository.countBySessionId(sessionId);
        return ChatDto.SessionResponse.from(updated, count);
    }

    @Transactional
    public ChatDto.SessionResponse toggleFavorite(String username, Long sessionId) {
        log.info("Toggling favorite on session id={} for user='{}'", sessionId, username);
        ChatSession session = findSessionByIdAndUsername(sessionId, username);
        session.setFavorite(!session.isFavorite());
        ChatSession updated = sessionRepository.save(session);
        long count = messageRepository.countBySessionId(sessionId);
        return ChatDto.SessionResponse.from(updated, count);
    }

    @Transactional
    public void deleteSession(String username, Long sessionId) {
        log.info("Deleting session id={} for user='{}'", sessionId, username);
        ChatSession session = findSessionByIdAndUsername(sessionId, username);
        sessionRepository.delete(session);
        log.info("Deleted session id={}", sessionId);
    }

    private ChatSession findSessionByIdAndUsername(Long sessionId, String username) {
        return sessionRepository.findByIdAndUsername(sessionId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session", sessionId));
    }
}
