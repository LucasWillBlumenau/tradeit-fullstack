package br.com.senior.tradeit.repository;

import br.com.senior.tradeit.entity.user.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    void save(User user);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
