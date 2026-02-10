package com.bandanize.backend.repositories;

import com.bandanize.backend.models.ChatMessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageModel, Long> {
    List<ChatMessageModel> findByBandIdOrderByTimestampAsc(Long bandId);

    List<ChatMessageModel> findBySender(com.bandanize.backend.models.UserModel sender);

    ChatMessageModel findTopByBandIdOrderByTimestampDesc(Long bandId);

    void deleteByBandId(Long bandId);
}
