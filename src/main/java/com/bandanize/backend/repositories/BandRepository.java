package com.bandanize.backend.repositories;

import com.bandanize.backend.models.BandModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface BandRepository extends JpaRepository<BandModel, Long> {
    List<BandModel> findByName(String name);
    List<BandModel> findByGenre(String genre);
}