package com.bandanize.backend.repositories;

import com.bandanize.backend.models.ChatReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {
    Optional<ChatReadStatus> findByBandIdAndUserId(Long bandId, Long userId);

    void deleteByBandId(Long bandId);
}
