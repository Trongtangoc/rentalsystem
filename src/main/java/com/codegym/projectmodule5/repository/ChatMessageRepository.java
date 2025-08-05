package com.codegym.projectmodule5.repository;

import com.codegym.projectmodule5.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
