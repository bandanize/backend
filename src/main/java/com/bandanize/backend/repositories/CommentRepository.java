package com.bandanize.backend.repositories;

import com.bandanize.backend.models.CommentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long> {
    List<CommentModel> findByUser(String user);
}