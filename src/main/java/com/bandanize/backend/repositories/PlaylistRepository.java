package com.bandanize.backend.repositories;

import com.bandanize.backend.models.PlaylistModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistModel, Long> {
    List<PlaylistModel> findAll();
}