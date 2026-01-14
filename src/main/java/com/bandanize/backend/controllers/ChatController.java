package com.bandanize.backend.controllers;

import com.bandanize.backend.models.ChatMessageModel;
import com.bandanize.backend.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bands/{bandId}/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatMessageModel>> getChatHistory(@PathVariable Long bandId) {
        return ResponseEntity.ok(chatService.getChatHistory(bandId));
    }

    @PostMapping
    public ResponseEntity<ChatMessageModel> sendMessage(@PathVariable Long bandId,
            @RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        String message = (String) payload.get("message");
        return ResponseEntity.ok(chatService.sendMessage(bandId, userId, message));
    }
}
