package com.bandanize.backend.services;

import com.bandanize.backend.dtos.BandDTO;
import com.bandanize.backend.exceptions.ResourceNotFoundException;
import com.bandanize.backend.models.BandModel;
import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.BandRepository;
import com.bandanize.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Band-related operations.
 * Handles creation, retrieval, updates, and association of bands with users.
 */
@Service
public class BandService {

    private final BandRepository bandRepository;
    private final UserRepository userRepository;
    private final com.bandanize.backend.repositories.BandInvitationRepository invitationRepository;

    @Autowired
    public BandService(BandRepository bandRepository, UserRepository userRepository,
            com.bandanize.backend.repositories.BandInvitationRepository invitationRepository) {
        this.bandRepository = bandRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<BandDTO> getAllBands() {

        return bandRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ... existing getBandById ...
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public BandDTO getBandById(Long id) {
        BandModel band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));
        return convertToDTO(band);
    }

    // ... existing createBand ...
    @org.springframework.transaction.annotation.Transactional
    public BandDTO createBand(BandModel band) {
        BandModel savedBand = bandRepository.save(band);
        return convertToDTO(savedBand);
    }

    // ... existing createBandWithUser ...
    @org.springframework.transaction.annotation.Transactional
    public BandDTO createBandWithUser(Long userId, BandModel bandDetails) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        BandModel band = new BandModel();
        band.setName(bandDetails.getName());
        band.setPhoto(bandDetails.getPhoto());
        band.setDescription(bandDetails.getDescription());
        band.setGenre(bandDetails.getGenre());
        band.setCity(bandDetails.getCity());
        band.setRrss(bandDetails.getRrss());
        band.setOwner(user);
        band.getUsers().add(user);

        BandModel savedBand = bandRepository.save(band);
        user.getBands().add(savedBand);
        userRepository.save(user);

        return convertToDTO(savedBand);
    }

    // ... existing updateBand ...
    @org.springframework.transaction.annotation.Transactional
    public BandDTO updateBand(Long id, BandModel bandDetails) {
        BandModel band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + id));

        if (bandDetails.getName() != null)
            band.setName(bandDetails.getName());
        if (bandDetails.getPhoto() != null)
            band.setPhoto(bandDetails.getPhoto());
        if (bandDetails.getDescription() != null)
            band.setDescription(bandDetails.getDescription());
        if (bandDetails.getGenre() != null)
            band.setGenre(bandDetails.getGenre());
        if (bandDetails.getCity() != null)
            band.setCity(bandDetails.getCity());
        if (bandDetails.getRrss() != null && !bandDetails.getRrss().isEmpty()) {
            band.setRrss(bandDetails.getRrss());
        }

        BandModel updatedBand = bandRepository.save(band);
        return convertToDTO(updatedBand);
    }

    // ... existing getBandsByUsername ...
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<BandDTO> getBandsByUsername(String username) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return user.getBands().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sends an invitation to a user to join a band.
     * Replaces immediate adding.
     *
     * @param bandId The ID of the band.
     * @param email  The email of the user to invite.
     */
    @org.springframework.transaction.annotation.Transactional
    public void inviteMember(Long bandId, String email) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + bandId));

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (band.getUsers().contains(user)) {
            throw new IllegalArgumentException("User is already a member");
        }

        // Check if invitation exists
        java.util.Optional<com.bandanize.backend.models.BandInvitationModel> existingInvitation = invitationRepository
                .findByBandIdAndInvitedUserId(bandId, user.getId());

        if (existingInvitation.isPresent()) {
            com.bandanize.backend.models.BandInvitationModel invitation = existingInvitation.get();
            if (invitation.getStatus() == com.bandanize.backend.models.InvitationStatus.PENDING) {
                // Already pending
                return;
            } else {
                // Reactivate invitation
                invitation.setStatus(com.bandanize.backend.models.InvitationStatus.PENDING);
                invitationRepository.save(invitation);
                return;
            }
        }

        com.bandanize.backend.models.BandInvitationModel invitation = new com.bandanize.backend.models.BandInvitationModel();
        invitation.setBand(band);
        invitation.setInvitedUser(user);
        invitation.setStatus(com.bandanize.backend.models.InvitationStatus.PENDING);

        invitationRepository.save(invitation);
    }

    public List<com.bandanize.backend.dtos.BandInvitationDTO> getPendingInvitations(Long userId) {
        return invitationRepository
                .findByInvitedUserIdAndStatus(userId, com.bandanize.backend.models.InvitationStatus.PENDING).stream()
                .map(inv -> {
                    com.bandanize.backend.dtos.BandInvitationDTO dto = new com.bandanize.backend.dtos.BandInvitationDTO();
                    dto.setId(inv.getId());
                    dto.setBandName(inv.getBand().getName());
                    dto.setBandId(inv.getBand().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional
    public void acceptInvitation(Long invitationId) {
        com.bandanize.backend.models.BandInvitationModel invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (invitation.getStatus() != com.bandanize.backend.models.InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation is not pending");
        }

        BandModel band = invitation.getBand();
        UserModel user = invitation.getInvitedUser();

        band.getUsers().add(user);
        // Ensure bidirectional consistency if mappedBy is used, though users is owner
        // here
        // But safe to be explicit if session is open
        if (!user.getBands().contains(band)) {
            user.getBands().add(band);
        }

        bandRepository.save(band); // Cascades to user if set up, ensuring consistent relationship

        invitation.setStatus(com.bandanize.backend.models.InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
    }

    @org.springframework.transaction.annotation.Transactional
    public void rejectInvitation(Long invitationId) {
        com.bandanize.backend.models.BandInvitationModel invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        invitation.setStatus(com.bandanize.backend.models.InvitationStatus.REJECTED);
        invitationRepository.save(invitation);
    }

    @org.springframework.transaction.annotation.Transactional
    public void leaveBand(Long bandId, Long userId) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found"));
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!band.getUsers().contains(user)) {
            throw new ResourceNotFoundException("User is not in this band");
        }

        // Remove relationship
        band.getUsers().remove(user);
        user.getBands().remove(band); // Important for consistency

        bandRepository.save(band);
        userRepository.save(user);
    }

    // ... existing addChatMessage ...
    @org.springframework.transaction.annotation.Transactional
    public com.bandanize.backend.models.ChatMessageModel addChatMessage(Long bandId,
            com.bandanize.backend.dtos.ChatMessageRequestDTO request) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + bandId));

        UserModel user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        com.bandanize.backend.models.ChatMessageModel message = new com.bandanize.backend.models.ChatMessageModel();
        message.setBand(band);
        message.setSender(user);
        message.setMessage(request.getMessage());
        message.setTimestamp(java.time.LocalDateTime.now());

        band.getChatMessages().add(message);
        bandRepository.save(band);

        return band.getChatMessages().get(band.getChatMessages().size() - 1);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteBand(Long bandId, Long requesterUserId) {
        BandModel band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + bandId));

        if (band.getOwner() == null || !band.getOwner().getId().equals(requesterUserId)) {
            // Only the owner can delete, or if no owner exists (legacy?), assume permission
            // for now, but strict check is better.
            if (band.getOwner() != null) {
                // Check if user is also owner (redundant but safe)
                throw new org.springframework.security.access.AccessDeniedException(
                        "Only the owner can delete the band");
            }
        }

        // JPA cascades should handle SongLists, Songs, ChatMessages if configured with
        // CascadeType.ALL or orphanRemoval.
        // But the ManyToMany relationship with Users needs careful handling.
        // We must remove this band from all users' band lists before deleting?
        // Actually, if Band is the owner of the relationship in specific ways or if
        // mappedBy is used correctly...
        // BandModel owns the relationship "users" via @JoinTable.
        // UserModel has "bands" mappedBy "users".

        // Explicitly clear associations to be safe and avoid constraint violations if
        // Cascade DELETE isn't perfect
        for (UserModel user : band.getUsers()) {
            user.getBands().remove(band);
            userRepository.save(user); // Update user side
        }
        band.getUsers().clear();
        bandRepository.save(band); // Save empty relationship

        bandRepository.delete(band);
    }

    // ... existing convertToDTO ...
    private BandDTO convertToDTO(BandModel band) {
        BandDTO bandDTO = new BandDTO();
        bandDTO.setId(band.getId());
        bandDTO.setName(band.getName());
        bandDTO.setPhoto(band.getPhoto());
        bandDTO.setDescription(band.getDescription());
        bandDTO.setGenre(band.getGenre());
        bandDTO.setCity(band.getCity());

        // Map ownerId, fallback to first user if null (for legacy data)
        if (band.getOwner() != null) {
            bandDTO.setOwnerId(band.getOwner().getId());
        } else if (!band.getUsers().isEmpty()) {
            bandDTO.setOwnerId(band.getUsers().get(0).getId());
        }

        bandDTO.setRrss(band.getRrss());
        bandDTO.setMembers(band.getUsers().stream()
                .map(user -> new com.bandanize.backend.dtos.UserSummaryDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getEmail()))
                .collect(Collectors.toList()));
        bandDTO.setSongLists(band.getSongLists());
        bandDTO.setChatMessages(band.getChatMessages());
        return bandDTO;
    }
}
