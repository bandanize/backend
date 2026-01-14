package com.bandanize.backend.services;

import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.BandModel;
import com.bandanize.backend.models.ChatMessageModel;
import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.BandRepository;
import com.bandanize.backend.repositories.ChatMessageRepository;
import com.bandanize.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private BandRepository bandRepository;
    @Autowired
    private UserRepository userRepository;

    public List<ChatMessageModel> getChatHistory(Long bandId) {
        return chatMessageRepository.findByBandIdOrderByTimestampAsc(bandId);
    }

    public ChatMessageModel sendMessage(Long bandId, Long userId, String message) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found"));
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChatMessageModel chatMessage = new ChatMessageModel();
        chatMessage.setBand(band);
        chatMessage.setSender(user);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(chatMessage);
    }
}
