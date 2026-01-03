package io.plantya.user.repository;

import io.plantya.user.common.dto.param.UserParam;
import io.plantya.user.common.dto.query.QueryData;
import io.plantya.user.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public List<User> findAllExistingUsers(UserParam param) {
        QueryData queryData = buildQuery(param);

        return find(queryData.query(), queryData.params().toArray())
                .page(Page.of(param.page() - 1, param.size()))
                .list();
    }

    public long countExistingUsers(UserParam param) {
        QueryData queryData = buildQuery(param);
        return count(queryData.query(), queryData.params().toArray());
    }

    public Optional<User> findByUserId(String userId) {
        return find("userId = :userId AND deletedAt IS NULL", Parameters.with("userId", userId))
                .firstResultOptional();
    }

    public Optional<User> findUserByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public User save(User user) {
        persist(user);
        flush();
        getEntityManager().refresh(user);

        return user;
    }

    public void softDelete(String userId) {
        update(
                "deletedAt = :deletedAt WHERE userId = :userId AND deletedAt IS NULL",
                Parameters.with("deletedAt", Instant.now()).and("userId", userId)
        );
    }

    // ===== HELPER ===== //
    private QueryData buildQuery(UserParam param) {
        StringBuilder query = new StringBuilder("deletedAt IS NULL");
        List<Object> params = new ArrayList<>();

        // Search
        if (param.search() != null && !param.search().isBlank()) {
            query.append(" AND ( LOWER(userId) LIKE ?1 OR LOWER(email) LIKE ?1 OR LOWER(name) LIKE ?1 )");
            params.add("%" + param.search().toLowerCase() + "%");
        }

        // Role filter
        if (param.role() != null) {
            query.append(" AND role = ?").append(params.size() + 1);
            params.add(param.role());
        }

        // Sorting
        query.append(" ORDER BY ")
                .append(resolveSortColumn(param.sort()))
                .append(" ")
                .append(resolveSortOrder(param.order()));

        return new QueryData(query.toString(), params);
    }

    private String resolveSortColumn(String sort) {
        if (sort == null) return "createdAt";

        return switch (sort) {
            case "userId" -> "userId";
            case "email" -> "email";
            case "name" -> "name";
            default -> "createdAt";
        };
    }

    private String resolveSortOrder(String order) {
        return "asc".equalsIgnoreCase(order) ? "ASC" : "DESC";
    }
}
