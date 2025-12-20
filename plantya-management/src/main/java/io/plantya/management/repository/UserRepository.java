package io.plantya.management.repository;

import io.plantya.management.dto.response.UserDeletedResponse;
import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.entity.User;
import io.plantya.management.enums.UserRole;
import io.plantya.management.common.mapper.ResponseMapper;
import io.plantya.management.common.repository.UserQueryBuilder;
import io.plantya.management.common.repository.UserQuerySpec;
import io.plantya.management.common.repository.UserSortResolver;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findByUserId(String userId) {
        return find("userId", userId).firstResultOptional();
    }

    public boolean isEmailExists(String email) {
        return count("email = :email", Parameters.with("email", email)) > 0;
    }

    public Optional<Long> getLastUserIndex() {
        return find("SELECT MAX(u.id) FROM User u")
                .project(Long.class)
                .firstResultOptional();
    }

    public void createUser(User user) {
        persist(user);
    }

    public void softDelete(String userId) {
        update(
                "deletedAt = :deletedAt WHERE userId = :userId AND deletedAt IS NULL",
                Parameters.with("deletedAt", Instant.now())
                        .and("userId", userId)
        );
    }

    public void restoreUser(String userId) {
        update("deletedAt = NULL WHERE userId = :userId", Parameters.with("userId", userId));
    }

    // ===== ACTIVE ===== //
    public long countActiveUsers(String search, UserRole role) {
        UserQuerySpec spec = UserQueryBuilder.active(search, role);
        return count(spec.query(), spec.params());
    }

    public List<UserResponse> findAllActive(
            Integer page,
            Integer size,
            String sort,
            String order,
            String search,
            UserRole role
    ) {
        UserQuerySpec spec = UserQueryBuilder.active(search, role);

        return buildQuery(spec, sort, order, page, size).list()
                .stream()
                .map(ResponseMapper::toUserResponse)
                .toList();
    }

    // ===== DELETED ===== //
    public long countDeletedUser(String search, UserRole role) {
        UserQuerySpec spec = UserQueryBuilder.deleted(search, role);
        return count(spec.query(), spec.params());
    }

    public List<UserDeletedResponse> findAllDeleted(
            Integer page,
            Integer size,
            String sort,
            String order,
            String search,
            UserRole role
    ) {
        UserQuerySpec spec = UserQueryBuilder.deleted(search, role);

        return buildQuery(spec, sort, order, page, size).list()
                .stream()
                .map(ResponseMapper::toUserDeletedResponse)
                .toList();
    }

    // ===== HELPER ===== //
    private PanacheQuery<User> buildQuery(
            UserQuerySpec spec,
            String sort,
            String order,
            Integer page,
            Integer size
    ) {
        String jpql = spec.query() + UserSortResolver.orderBy(sort, order);
        PanacheQuery<User> query = find(jpql, spec.params());

        if (page != null && size != null) {
            query.page(Page.of(page - 1, size));
        }

        return query;
    }

}
