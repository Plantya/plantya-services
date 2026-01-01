package io.plantya.iot.cluster.repository;

import io.plantya.iot.cluster.entity.Cluster;
import io.plantya.iot.common.dto.query.QueryData;
import io.plantya.iot.common.dto.request.ClusterQueryParam;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClusterRepository implements PanacheRepository<Cluster> {

    public List<Cluster> findAllExistingClusters(ClusterQueryParam queryParam) {
        QueryData queryData = buildQuery(queryParam);
        return find(queryData.query(), queryData.params().toArray())
                .page(Page.of(queryParam.getPage() - 1, queryParam.getSize()))
                .list();
    }

    public Optional<Cluster> findByClusterId(String clusterId) {
        return find("clusterId", clusterId).firstResultOptional();
    }

    public Optional<Cluster> findByClusterName(String clusterName) {
        return find("clusterName", clusterName).firstResultOptional();
    }

    public Cluster save(Cluster cluster) {
        persist(cluster);
        flush();
        getEntityManager().refresh(cluster);

        return cluster;
    }

    public void softDelete(String clusterId) {
        update(
                "deletedAt = :deletedAt WHERE clusterId = :clusterId AND deletedAt IS NULL",
                Parameters.with("deletedAt", Instant.now())
                        .and("clusterId", clusterId)
        );
    }

    public long countExistingDevices(ClusterQueryParam queryParam) {
        QueryData queryData = buildQuery(queryParam);
        return count(queryData.query(), queryData.params().toArray());
    }

    // ===== HELPER ===== //
    private QueryData buildQuery(ClusterQueryParam param) {
        StringBuilder query = new StringBuilder("deletedAt IS NULL");
        List<Object> params = new ArrayList<>();

        // Search
        if (param.getSearch() != null && !param.getSearch().isBlank()) {
            query.append(" AND (LOWER(clusterId) LIKE ?1 OR LOWER(clusterName) LIKE ?1)");
            params.add("%" + param.getSearch().toLowerCase() + "%");
        }

        // Sorting
        query.append(" ORDER BY ")
                .append(resolveSortColumn(param.getSort()))
                .append(" ")
                .append(resolveSortOrder(param.getOrder()));

        return new QueryData(query.toString(), params);
    }

    private String resolveSortColumn(String sort) {
        if (sort == null) return "createdAt";

        return switch (sort) {
            case "clusterId" -> "clusterId";
            case "clusterName" -> "clusterName";
            default -> "createdAt";
        };
    }

    private String resolveSortOrder(String order) {
        return "asc".equalsIgnoreCase(order) ? "ASC" : "DESC";
    }
}
