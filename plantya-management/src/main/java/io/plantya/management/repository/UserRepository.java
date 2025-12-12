package io.plantya.management.repository;

import io.plantya.management.dto.ActiveUserDto;
import io.plantya.management.dto.DeletedUserDto;
import io.plantya.management.dto.GetUserDto;
import io.plantya.management.dto.response.DeletedUserResponse;
import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.entity.User;
import io.plantya.management.entity.UserRole;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@SuppressWarnings("unchecked")
public class UserRepository implements PanacheRepository<User> {

    public List<UserResponse> findActiveUserList() {
        List<UserResponse> list = new ArrayList<>();

        List<User> activeUserList = find("deleted_at IS NULL").list();
        activeUserList.forEach(u -> {
            list.add(new UserResponse(
                    u.getUserId(),
                    u.getEmail(),
                    u.getName(),
                    u.getRole(),
                    u.getCreatedAt(),
                    u.getUpdatedAt()
            ));
        });

        return list;
    }

    public List<DeletedUserResponse> findDeletedUserList() {
        List<DeletedUserResponse> list = new ArrayList<>();

        List<User> deletedUserList = find("deleted_at IS NOT NULL").list();
        deletedUserList.forEach(u -> {
            list.add(new DeletedUserResponse(
                    u.getUserId(),
                    u.getEmail(),
                    u.getName(),
                    u.getRole(),
                    u.getDeletedAt()
            ));
        });

        return list;
    }

    public GetUserDto findUserByUserId (String userId) {
        User user = find("userId", userId).singleResult();

        return new GetUserDto(user.getUserId(), user.getEmail(), user.getName(), user.getRole(), user.getCreatedAt());
    }

    public void createUser(User user) {
        persist(user);
    }

    public DeletedUserResponse findDeletedUserByUserId (String userId) {
        User user = find("userId = :userId AND deletedAt IS NOT NULL", Parameters.with("userId", userId))
                .singleResult();

        return new DeletedUserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getDeletedAt()
        );
    }

    public void restoreDeletedUserByUserId (String userId) {

    }

    public void softDeleteUser(String userId) {
        update("userId = :userId", Parameters.with("userId", userId));
    }

}
