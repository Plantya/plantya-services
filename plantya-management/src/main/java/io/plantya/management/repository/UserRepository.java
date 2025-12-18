package io.plantya.management.repository;

import io.plantya.management.dto.response.UserDeletedResponse;
import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.entity.User;
import io.plantya.management.enums.UserRole;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public long countActiveUser(String search, UserRole role) {
        QuerySpec spec = buildActiveUserQuery(search, role);
        return count(spec.query, spec.params);
    }

    public List<UserResponse> findAllActive(
            int page,
            int size,
            String sort,
            String order,
            String search,
            UserRole role
    ) {
        QuerySpec spec = buildActiveUserQuery(search, role);
        String query = spec.query + " ORDER BY " + resolveSortField(sort) + " " + resolveSortOrder(order);

        List<User> users = find(query, spec.params)
                .page(Page.of(page - 1, size))
                .list();

        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UserResponse> findAllActive(String sort, String order, String search, UserRole role) {
        QuerySpec spec = buildActiveUserQuery(search, role);
        String query = spec.query + " ORDER BY " + resolveSortField(sort) + " " + resolveSortOrder(order);

        List<User> users = find(query, spec.params).list();

        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(String userId) {
        User user = find("userId", userId).singleResult();

        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public void createUser(User user) {
        persist(user);
    }

    public long countDeletedUser() {
        return count("deletedAt IS NOT NULL");
    }

    public List<UserDeletedResponse> findDeletedUserList() {
        List<UserDeletedResponse> list = new ArrayList<>();

        List<User> deletedUserList = find("deletedAt IS NOT NULL").list();
        deletedUserList.forEach(u -> {
            list.add(new UserDeletedResponse(
                    u.getUserId(),
                    u.getEmail(),
                    u.getName(),
                    u.getRole(),
                    u.getDeletedAt()
            ));
        });

        return list;
    }

    public UserDeletedResponse findDeletedUserByUserId (String userId) {
        User user = find("userId = :userId AND deletedAt IS NOT NULL", Parameters.with("userId", userId))
                .singleResult();

        return new UserDeletedResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getDeletedAt()
        );
    }

    public void softDelete(String userId) {
        delete("userId = :userId", Parameters.with("userId", userId));
    }

    // ===== HELPER =====
    private String resolveSortField(String sort) {
        if (sort == null) {
            return "userId";
        }

        return switch (sort) {
            case "email" -> "email";
            case "name" -> "name";
            case "role" -> "role";
            case "createdAt" -> "createdAt";
            default -> "userId";
        };
    }

    private String resolveSortOrder(String order) {
        return "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getUserId(),
                u.getEmail(),
                u.getName(),
                u.getRole(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }

    private String baseActiveQuery() {
        return "deletedAt IS NULL";
    }

    private String searchClause() {
        return """
        AND (
            LOWER(userId) LIKE ?1 OR
            LOWER(email) LIKE ?1 OR
            LOWER(name) LIKE ?1 OR
            LOWER(CAST(role AS text)) LIKE ?1
        )
    """;
    }

    private record QuerySpec(String query, Object[] params) {}

    private QuerySpec buildActiveUserQuery(String search, UserRole role) {
        StringBuilder query = new StringBuilder(baseActiveQuery());
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (search != null && !search.isBlank()) {
            query.append(searchClause());
            params.add("%" + search.toLowerCase() + "%");
            paramIndex++;
        }

        if (role != null) {
            query.append(" AND role = ?").append(paramIndex);
            params.add(role);
        }

        return new QuerySpec(query.toString(), params.toArray());
    }

}
