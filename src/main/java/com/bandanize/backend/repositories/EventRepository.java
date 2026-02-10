package com.bandanize.backend.repositories;

import com.bandanize.backend.models.EventModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Long> {
    List<EventModel> findByBandIdOrderByDateAsc(Long bandId);

    void deleteByBandId(Long bandId);
}
