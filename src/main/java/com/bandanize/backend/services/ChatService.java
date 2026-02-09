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
    @Autowired
    private NotificationService notificationService;

    public List<ChatMessageModel> getChatHistory(Long bandId) {
        return chatMessageRepository.findByBandIdOrderByTimestampAsc(bandId);
    }

    public ChatMessageModel sendMessage(Long bandId, Long userId, String message) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found"));
        UserModel sender = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChatMessageModel chatMessage = new ChatMessageModel();
        chatMessage.setBand(band);
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(LocalDateTime.now());

        ChatMessageModel savedMessage = chatMessageRepository.save(chatMessage);

        // Check for mentions (Simple implementation: @Name)
        if (message.contains("@")) {
            // Very basic: iterate over band members and check if their name is in the
            // message
            for (UserModel member : band.getUsers()) {
                if (!member.getId().equals(sender.getId()) && message.contains("@" + member.getName())) {
                    notificationService.createChatMentionNotification(band, sender, member);
                }
            }
        }

        return savedMessage;
    }
}
