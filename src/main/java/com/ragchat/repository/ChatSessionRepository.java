package com.ragchat.repository;

import com.ragchat.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Page<ChatSession> findByUsernameOrderByUpdatedAtDesc(String username, Pageable pageable);

    Page<ChatSession> findByUsernameAndFavoriteTrueOrderByUpdatedAtDesc(String username, Pageable pageable);

    Optional<ChatSession> findByIdAndUsername(Long id, String username);
}
