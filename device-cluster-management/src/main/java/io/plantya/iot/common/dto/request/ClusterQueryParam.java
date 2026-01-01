package io.plantya.iot.common.dto.request;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

public class ClusterQueryParam {

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

    public void setSize(int size) {
        this.size = size;
    }

    public void setPage(int page) {
        this.page = page;
    }
}