package com.bandanize.backend.repositories;

import com.bandanize.backend.models.TabModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TabRepository extends JpaRepository<TabModel, Long> {
}