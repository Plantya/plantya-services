package io.plantya.user.common.dto.request;

import io.plantya.user.domain.UserRole;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

public class UserQueryParam {

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

    @QueryParam("role")
    private UserRole role;

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

    public UserRole getRole() {
        return role;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}