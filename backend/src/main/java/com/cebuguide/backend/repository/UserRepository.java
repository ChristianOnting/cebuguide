package com.cebuguide.backend.repository;

import com.cebuguide.backend.model.Role;
import com.cebuguide.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRole(Role role);
    List<User> findAllByOrderByCreatedAtDesc();
    List<User> findTop5ByOrderByCreatedAtDesc();
}
