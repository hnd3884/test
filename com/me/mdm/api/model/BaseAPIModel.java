package com.me.mdm.api.model;

import javax.ws.rs.core.UriInfo;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.api.paging.annotations.BaseAPILoginUserIDparam;
import com.me.mdm.api.paging.annotations.BaseAPICustomerIDParam;

public class BaseAPIModel
{
    @BaseAPICustomerIDParam
    private Long customerId;
    @BaseAPILoginUserIDparam
    private Long userId;
    private String userName;
    private Long logInId;
    private String requestUri;
    
    public Long getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Long userId) {
        this.userId = userId;
    }
    
    public Long getLogInId() {
        return this.logInId;
    }
    
    public void setLogInId(final Long logInId) {
        this.logInId = logInId;
    }
    
    public String getRequestUri() {
        return this.requestUri;
    }
    
    public void setRequestUri(final String requestUri) {
        this.requestUri = requestUri;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public void setCustomerUserDetails(final ContainerRequestContext requestContext) {
        final String customerIdStr = (String)requestContext.getProperty("X-Customer");
        if (customerIdStr != null && !customerIdStr.equalsIgnoreCase("All")) {
            this.customerId = Long.parseLong(customerIdStr);
        }
        final User user = (User)requestContext.getSecurityContext().getUserPrincipal();
        final UriInfo uriInfo = requestContext.getUriInfo();
        this.userId = user.getUserID();
        this.logInId = user.getLoginID();
        this.userName = user.getName();
        this.requestUri = uriInfo.getRequestUri().toString();
    }
}
