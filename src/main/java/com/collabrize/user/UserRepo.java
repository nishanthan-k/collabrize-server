package com.collabrize.user;

import org.springframework.data.jpa.repository.JpaRepository;
import com.collabrize.user.domain.User;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthId(String oauthId);
}
