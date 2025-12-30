package io.plantya.iot.common.dto.request;

import io.plantya.iot.device.domain.DeviceStatus;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

public class DeviceQueryParam {

    @DefaultValue("1")
    @QueryParam("page")
    private int page;

    @DefaultValue("10")
    @QueryParam("size")
    private int size;

    @QueryParam("search")
    private String search;

    @QueryParam("sort")
    private String sort;

    @QueryParam("order")
    private String order;

    @QueryParam("status")
    private DeviceStatus status;

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSearch() {
        return search;
    }

    public String getSort() {
        return sort;
    }

    public String getOrder() {
        return order;
    }

    public DeviceStatus getStatus() {
        return status;
    }
}