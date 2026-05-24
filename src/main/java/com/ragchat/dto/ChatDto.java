package com.ragchat.dto;

import com.ragchat.entity.ChatMessage;
import com.ragchat.entity.ChatSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ChatDto {

    // ============================================================
    // Session Request DTOs
    // ============================================================

    public static class CreateSessionRequest {
        @NotBlank(message = "Session name must not be blank")
        @Size(max = 255, message = "Session name must not exceed 255 characters")
        private String name;

        public CreateSessionRequest() {}
        public CreateSessionRequest(String name) { this.name = name; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class RenameSessionRequest {
        @NotBlank(message = "New name must not be blank")
        @Size(max = 255, message = "Session name must not exceed 255 characters")
        private String name;

        public RenameSessionRequest() {}
        public RenameSessionRequest(String name) { this.name = name; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // ============================================================
    // Message Request DTOs
    // ============================================================

    public static class AddMessageRequest {
        @NotNull(message = "Sender must not be null")
        private ChatMessage.SenderType sender;

        @NotBlank(message = "Content must not be blank")
        private String content;

        private String context;

        public AddMessageRequest() {}
        public ChatMessage.SenderType getSender() { return sender; }
        public void setSender(ChatMessage.SenderType sender) { this.sender = sender; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }
    }

    // ============================================================
    // Response DTOs
    // ============================================================

    public static class SessionResponse {
        private Long id;
        private String name;
        private String username;
        private boolean favorite;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private long messageCount;

        public SessionResponse() {}

        public static SessionResponse from(ChatSession session, long messageCount) {
            SessionResponse resp = new SessionResponse();
            resp.id = session.getId();
            resp.name = session.getName();
            resp.username = session.getUsername();
            resp.favorite = session.isFavorite();
            resp.createdAt = session.getCreatedAt();
            resp.updatedAt = session.getUpdatedAt();
            resp.messageCount = messageCount;
            return resp;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getUsername() { return username; }
        public boolean isFavorite() { return favorite; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public long getMessageCount() { return messageCount; }
    }

    public static class MessageResponse {
        private Long id;
        private Long sessionId;
        private ChatMessage.SenderType sender;
        private String content;
        private String context;
        private LocalDateTime createdAt;

        public MessageResponse() {}

        public static MessageResponse from(ChatMessage message) {
            MessageResponse resp = new MessageResponse();
            resp.id = message.getId();
            resp.sessionId = message.getSession().getId();
            resp.sender = message.getSender();
            resp.content = message.getContent();
            resp.context = message.getContext();
            resp.createdAt = message.getCreatedAt();
            return resp;
        }

        public Long getId() { return id; }
        public Long getSessionId() { return sessionId; }
        public ChatMessage.SenderType getSender() { return sender; }
        public String getContent() { return content; }
        public String getContext() { return context; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    // ============================================================
    // Generic API Response Wrapper
    // ============================================================

    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse() {}

        public static <T> ApiResponse<T> success(String message, T data) {
            ApiResponse<T> resp = new ApiResponse<>();
            resp.success = true;
            resp.message = message;
            resp.data = data;
            return resp;
        }

        public static <T> ApiResponse<T> success(T data) {
            return success("Operation successful", data);
        }

        public static <T> ApiResponse<T> error(String message) {
            ApiResponse<T> resp = new ApiResponse<>();
            resp.success = false;
            resp.message = message;
            return resp;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public T getData() { return data; }
    }
}
