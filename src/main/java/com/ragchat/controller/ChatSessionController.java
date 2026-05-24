package com.ragchat.controller;

import com.ragchat.dto.ChatDto;
import com.ragchat.service.ChatSessionService;
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
@RequestMapping("/api/v1/sessions")
@Tag(name = "Chat Sessions", description = "Manage chat sessions")
public class ChatSessionController {

    private final ChatSessionService sessionService;

    public ChatSessionController(ChatSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    @Operation(summary = "Create a new chat session")
    public ResponseEntity<ChatDto.ApiResponse<ChatDto.SessionResponse>> createSession(
            Authentication auth,
            @Valid @RequestBody ChatDto.CreateSessionRequest request) {
        ChatDto.SessionResponse session = sessionService.createSession(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ChatDto.ApiResponse.success("Session created successfully", session));
    }

    @GetMapping
    @Operation(summary = "List all sessions for the authenticated user")
    public ResponseEntity<ChatDto.ApiResponse<Page<ChatDto.SessionResponse>>> getSessions(
            Authentication auth,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter only favorites") @RequestParam(defaultValue = "false") boolean favoritesOnly) {
        Page<ChatDto.SessionResponse> sessions = sessionService.getSessions(auth.getName(), page, size, favoritesOnly);
        return ResponseEntity.ok(ChatDto.ApiResponse.success(sessions));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get a specific chat session by ID")
    public ResponseEntity<ChatDto.ApiResponse<ChatDto.SessionResponse>> getSession(
            Authentication auth,
            @PathVariable Long sessionId) {
        ChatDto.SessionResponse session = sessionService.getSession(auth.getName(), sessionId);
        return ResponseEntity.ok(ChatDto.ApiResponse.success(session));
    }

    @PatchMapping("/{sessionId}/rename")
    @Operation(summary = "Rename a chat session")
    public ResponseEntity<ChatDto.ApiResponse<ChatDto.SessionResponse>> renameSession(
            Authentication auth,
            @PathVariable Long sessionId,
            @Valid @RequestBody ChatDto.RenameSessionRequest request) {
        ChatDto.SessionResponse session = sessionService.renameSession(auth.getName(), sessionId, request);
        return ResponseEntity.ok(ChatDto.ApiResponse.success("Session renamed successfully", session));
    }

    @PatchMapping("/{sessionId}/favorite")
    @Operation(summary = "Toggle favorite status of a chat session")
    public ResponseEntity<ChatDto.ApiResponse<ChatDto.SessionResponse>> toggleFavorite(
            Authentication auth,
            @PathVariable Long sessionId) {
        ChatDto.SessionResponse session = sessionService.toggleFavorite(auth.getName(), sessionId);
        return ResponseEntity.ok(ChatDto.ApiResponse.success("Favorite status updated", session));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Delete a chat session and all its messages")
    public ResponseEntity<ChatDto.ApiResponse<Void>> deleteSession(
            Authentication auth,
            @PathVariable Long sessionId) {
        sessionService.deleteSession(auth.getName(), sessionId);
        return ResponseEntity.ok(ChatDto.ApiResponse.success("Session deleted successfully", null));
    }
}
