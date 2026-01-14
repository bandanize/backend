package com.bandanize.backend.repositories;

import com.bandanize.backend.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByEmail(String email);

    List<UserModel> findByEmailContaining(String email);

    List<UserModel> findByUsernameContaining(String username);
}