package com.example.kindergarten.infrastructure;

import com.example.kindergarten.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
