package com.bandanize.backend.repositories;

import com.bandanize.backend.models.BandInvitationModel;
import com.bandanize.backend.models.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BandInvitationRepository extends JpaRepository<BandInvitationModel, Long> {
    List<BandInvitationModel> findByInvitedUserIdAndStatus(Long userId, InvitationStatus status);

    Optional<BandInvitationModel> findByBandIdAndInvitedUserId(Long bandId, Long userId);

    void deleteByInvitedUser(com.bandanize.backend.models.UserModel invitedUser);

    void deleteByBandId(Long bandId);
}
