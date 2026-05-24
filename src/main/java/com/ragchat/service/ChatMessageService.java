package com.ragchat.service;

import com.ragchat.dto.ChatDto;
import com.ragchat.entity.ChatMessage;
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
public class ChatMessageService {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageService.class);

    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;

    public ChatMessageService(ChatMessageRepository messageRepository,
                              ChatSessionRepository sessionRepository) {
        this.messageRepository = messageRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public ChatDto.MessageResponse addMessage(String username, Long sessionId, ChatDto.AddMessageRequest request) {
        log.info("Adding message to session id={} for user='{}', sender={}", sessionId, username, request.getSender());
        ChatSession session = sessionRepository.findByIdAndUsername(sessionId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session", sessionId));

        ChatMessage message = new ChatMessage(session, request.getSender(), request.getContent(), request.getContext());
        ChatMessage saved = messageRepository.save(message);

        // Touch the session's updatedAt
        sessionRepository.save(session);

        log.info("Added message id={} to session id={}", saved.getId(), sessionId);
        return ChatDto.MessageResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ChatDto.MessageResponse> getMessages(String username, Long sessionId, int page, int size) {
        log.debug("Fetching messages for session id={}, user='{}', page={}, size={}", sessionId, username, page, size);
        // Ensure session belongs to user
        sessionRepository.findByIdAndUsername(sessionId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session", sessionId));

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId, pageable);
        return messages.map(ChatDto.MessageResponse::from);
    }
}
