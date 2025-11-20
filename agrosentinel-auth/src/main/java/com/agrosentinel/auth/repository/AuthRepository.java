package com.agrosentinel.auth.repository;

import com.agrosentinel.auth.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthRepository implements PanacheRepository<User> {

    public boolean existsByUsername(String username) {
        return find("username", username).firstResult() != null;
    }

    public boolean existsByEmail(String email) {
        return find("email", email).firstResult() != null;
    }

    public User findOneByUsername(String username) {
        return find("username", username).firstResult();
    }

    public User findOneByEmail(String email) {
        return find("email", email).firstResult();
    }

}
