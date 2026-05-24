package com.ragchat.controller;

import com.ragchat.dto.ChatDto;
import com.ragchat.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/messages")
@Tag(name = "Chat Messages", description = "Manage messages within chat sessions")
public class ChatMessageController {

    private final ChatMessageService messageService;

    public ChatMessageController(ChatMessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @Operation(summary = "Add a message to a chat session")
    public ResponseEntity<ChatDto.ApiResponse<ChatDto.MessageResponse>> addMessage(
            Authentication auth,
            @PathVariable Long sessionId,
            @Valid @RequestBody ChatDto.AddMessageRequest request) {
        ChatDto.MessageResponse message = messageService.addMessage(auth.getName(), sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ChatDto.ApiResponse.success("Message added successfully", message));
    }

    @GetMapping
    @Operation(summary = "Get all messages in a chat session (paginated)")
    public ResponseEntity<ChatDto.ApiResponse<Page<ChatDto.MessageResponse>>> getMessages(
            Authentication auth,
            @PathVariable Long sessionId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Page<ChatDto.MessageResponse> messages = messageService.getMessages(auth.getName(), sessionId, page, size);
        return ResponseEntity.ok(ChatDto.ApiResponse.success(messages));
    }
}
