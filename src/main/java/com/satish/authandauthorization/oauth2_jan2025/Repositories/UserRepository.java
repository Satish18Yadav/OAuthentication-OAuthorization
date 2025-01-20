package com.satish.authandauthorization.oauth2_jan2025.Repositories;

import com.satish.authandauthorization.oauth2_jan2025.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
