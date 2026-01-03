package io.plantya.user.repository;

import io.plantya.user.common.dto.param.UserParam;
import io.plantya.user.common.dto.query.QueryData;
import io.plantya.user.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UserDeletedRepository implements PanacheRepository<User> {

    public List<User> findAllDeletedUsers(UserParam userParam) {
        QueryData queryData = buildQuery(userParam);

        return find(queryData.query(), queryData.params().toArray())
                .page(Page.of(userParam.page() - 1, userParam.size()))
                .list();
    }

    public long countDeletedUsers(UserParam userParam) {
        QueryData queryData = buildQuery(userParam);
        return count(queryData.query(), queryData.params().toArray());
    }

    public User findDeletedByUserId(String userId) {
        return find(
                "userId = :userId AND deletedAt IS NOT NULL",
                Parameters.with("userId", userId)
        ).firstResult();
    }

    public void restoreUser(User user) {
        user.setDeletedAt(null);
        flush();
        getEntityManager().refresh(user);
    }

    // ===== HELPER ===== //
    private QueryData buildQuery(UserParam param) {
        StringBuilder query = new StringBuilder("deletedAt IS NOT NULL");
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
