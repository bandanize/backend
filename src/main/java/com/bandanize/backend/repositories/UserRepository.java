package com.bandanize.backend.repositories;

import com.bandanize.backend.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {
}