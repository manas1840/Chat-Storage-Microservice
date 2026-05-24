package com.ragchat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum SenderType {
        USER, ASSISTANT, SYSTEM, ADMIN
    }

    public ChatMessage() {
    }

    public ChatMessage(ChatSession session, SenderType sender, String content, String context) {
        this.session = session;
        this.sender = sender;
        this.content = content;
        this.context = context;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public ChatSession getSession() { return session; }
    public SenderType getSender() { return sender; }
    public String getContent() { return content; }
    public String getContext() { return context; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setSession(ChatSession session) { this.session = session; }
    public void setSender(SenderType sender) { this.sender = sender; }
    public void setContent(String content) { this.content = content; }
    public void setContext(String context) { this.context = context; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
