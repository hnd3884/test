package com.me.devicemanagement.framework.webclient.api.util;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.api.mapper.RequestMapper;
import java.util.HashMap;

public class APIRequest
{
    public HashMap parameterList;
    public RequestMapper.Entity.Request request;
    public HttpServletRequest httpServletRequest;
    public HttpServletResponse httpServletResponse;
    public int pageIndex;
    public int pageLimit;
    public long totalRecords;
    public String orderby;
    public String sortorder;
    
    public APIRequest() {
        this.parameterList = null;
        this.request = null;
        this.httpServletRequest = null;
        this.httpServletResponse = null;
        this.pageIndex = 1;
        this.pageLimit = 25;
        this.totalRecords = 0L;
        this.orderby = null;
        this.sortorder = "ASC";
    }
    
    public RequestMapper.Entity.Request getRequest() {
        return this.request;
    }
    
    public void setRequest(final RequestMapper.Entity.Request request) {
        this.request = request;
    }
    
    public HashMap getParameterList() {
        return this.parameterList;
    }
    
    public void setParameterList(final HashMap parameterList) {
        this.parameterList = parameterList;
    }
    
    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }
    
    public void setHttpServletRequest(final HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }
    
    public HttpServletResponse getHttpServletResponse() {
        return this.httpServletResponse;
    }
    
    public void setHttpServletResponse(final HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }
    
    public int getPageIndex() {
        return this.pageIndex;
    }
    
    public void setPageIndex(final int pageIndex) {
        this.pageIndex = pageIndex;
    }
    
    public int getPageLimit() {
        return this.pageLimit;
    }
    
    public void setPageLimit(final int pageLimit) {
        this.pageLimit = pageLimit;
    }
    
    public String getOrderby() {
        return this.orderby;
    }
    
    public void setOrderby(final String orderby) {
        this.orderby = orderby;
    }
    
    public String getSortorder() {
        return this.sortorder;
    }
    
    public void setSortorder(final String sortorder) {
        this.sortorder = sortorder;
    }
    
    public long getTotalRecords() {
        return this.totalRecords;
    }
    
    public void setTotalRecords(final long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
