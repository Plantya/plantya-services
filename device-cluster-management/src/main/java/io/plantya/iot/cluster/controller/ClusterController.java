package io.plantya.iot.cluster.controller;

import io.plantya.iot.cluster.dto.request.ClusterCreateRequest;
import io.plantya.iot.cluster.dto.request.ClusterUpdateRequest;
import io.plantya.iot.cluster.dto.response.ClusterCreateResponse;
import io.plantya.iot.cluster.dto.response.ClusterGetResponse;
import io.plantya.iot.cluster.dto.response.ClusterUpdateResponse;
import io.plantya.iot.cluster.dto.response.PagedClusterResponse;
import io.plantya.iot.cluster.service.ClusterService;
import io.plantya.iot.common.dto.request.ClusterQueryParam;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path( "/api/clusters")
@Produces("application/json")
@Consumes("application/json")
public class ClusterController {

    @Inject
    ClusterService clusterService;

    @GET
    public Response findAllExistingClusters(@BeanParam ClusterQueryParam queryParam) {
        PagedClusterResponse response = clusterService.findAllExistingClusters(queryParam);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @POST
    public Response createCluster(ClusterCreateRequest request) {
        ClusterCreateResponse response = clusterService.createCluster(request);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{clusterId}")
    public Response findByClusterId(@PathParam("clusterId") String clusterId) {
        ClusterGetResponse response = clusterService.findByClusterId(clusterId);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @PATCH
    @Path("/{clusterId}")
    public Response updateCluster(@PathParam("clusterId") String clusterId, ClusterUpdateRequest request) {
        ClusterUpdateResponse response = clusterService.updateCluster(clusterId, request);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @DELETE
    @Path("/{clusterId}")
    public Response deleteCluster(@PathParam("clusterId") String clusterId) {
        clusterService.deleteCluster(clusterId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
