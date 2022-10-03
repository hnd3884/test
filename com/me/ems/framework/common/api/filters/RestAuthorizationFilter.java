package com.me.ems.framework.common.api.filters;

import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.Iterator;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.persistence.DataObject;
import javax.ws.rs.core.MultivaluedMap;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.ems.framework.uac.api.v1.model.User;
import java.io.IOException;
import java.util.List;
import java.lang.reflect.Method;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import com.me.ems.framework.common.api.annotations.ProductMatched;
import com.me.ems.framework.common.api.annotations.ValidResource;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.ems.framework.common.api.response.APIResponse;
import java.lang.annotation.Annotation;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.common.api.annotations.RestrictMatched;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ResourceInfo;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerRequestFilter;

@Provider
@Priority(2000)
public class RestAuthorizationFilter implements ContainerRequestFilter
{
    private static Logger logger;
    @Context
    private ResourceInfo resourceInfo;
    @Context
    private SecurityContext securityContext;
    @Context
    private HttpServletRequest servletRequest;
    
    public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
        final Method resourceMethod = this.resourceInfo.getResourceMethod();
        final Class resourceClass = this.resourceInfo.getResourceClass();
        final RolesAllowed annotatedRole = resourceMethod.getAnnotation(RolesAllowed.class);
        final CustomerSegmented customerSegmented = resourceMethod.getAnnotation(CustomerSegmented.class);
        final RestrictMatched restrictMatched = resourceMethod.getAnnotation(RestrictMatched.class);
        try {
            final CustomerInfoUtil customerInfoUtil = CustomerInfoUtil.getInstance();
            final String headerCustomerID = containerRequestContext.getHeaderString("X-Customer");
            if (customerInfoUtil.isMSP() && resourceMethod.isAnnotationPresent(CustomerSegmented.class) && customerSegmented.requireCustomerID() && (headerCustomerID == null || headerCustomerID.trim().isEmpty() || headerCustomerID.equalsIgnoreCase("all"))) {
                containerRequestContext.abortWith(APIResponse.errorResponse("CUSTOMER0002"));
            }
            final String customerIDStr = customerInfoUtil.isMSP() ? headerCustomerID : (customerInfoUtil.getDefaultCustomer() + "");
            if (customerInfoUtil.isMSP()) {
                CustomerInfoThreadLocal.setCustomerId(customerIDStr);
                if (customerIDStr != null) {
                    final String isSummaryPage = customerIDStr.equalsIgnoreCase("all") ? "true" : "false";
                    CustomerInfoThreadLocal.setSummaryPage(isSummaryPage);
                }
            }
            final boolean isUserRoleValidForResource = resourceMethod.isAnnotationPresent((Class<? extends Annotation>)RolesAllowed.class) ? this.isResourceAccessibleForUserRole(annotatedRole) : Boolean.TRUE;
            final boolean isCustomerAccessibleByUser = resourceMethod.isAnnotationPresent(CustomerSegmented.class) ? this.isCustomerAccessibileByUser(customerIDStr) : Boolean.TRUE;
            final boolean isResourceAvailable = resourceMethod.isAnnotationPresent(ValidResource.class) ? this.isResourceAvailableInDataBase(containerRequestContext) : Boolean.TRUE;
            final Annotation productAnnotation = resourceClass.getAnnotation(ProductMatched.class);
            final List productClassLevel = resourceClass.isAnnotationPresent(ProductMatched.class) ? Arrays.asList(((ProductMatched)productAnnotation).value()) : new LinkedList<String>();
            final List productMethodLevel = resourceMethod.isAnnotationPresent(ProductMatched.class) ? Arrays.asList(resourceMethod.getAnnotation(ProductMatched.class).value()) : new LinkedList<String>();
            productClassLevel.addAll(productMethodLevel);
            final Response ACCESS_DENIED = APIResponse.errorResponse("USER0002");
            if ((resourceClass.isAnnotationPresent(ProductMatched.class) || resourceMethod.isAnnotationPresent(ProductMatched.class)) && productClassLevel.size() > 0 && !this.checkCurrentProductMatch(productClassLevel)) {
                containerRequestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
            }
            if (resourceMethod.isAnnotationPresent(RestrictMatched.class) && !this.isResourceAccessibleForServer(restrictMatched, containerRequestContext)) {
                RestAuthorizationFilter.logger.log(Level.INFO, "RestAuthFilter: Precondition failed for method due to RestrictMatched " + resourceMethod.getName());
                containerRequestContext.abortWith(ACCESS_DENIED);
            }
            if (!isUserRoleValidForResource || !isCustomerAccessibleByUser) {
                containerRequestContext.abortWith(ACCESS_DENIED);
            }
            if (!isResourceAvailable) {
                throw new APIException("RESOURCE0001");
            }
            if (customerInfoUtil.isMSP()) {
                CustomerInfoThreadLocal.setCustomerId(customerIDStr);
            }
            containerRequestContext.setProperty("X-Customer", (Object)customerIDStr);
            final String inputStream = this.servletRequest.getParameter("zoho-inputstream");
            if (inputStream != null && !inputStream.trim().isEmpty()) {
                containerRequestContext.setEntityStream((InputStream)new ByteArrayInputStream(inputStream.getBytes(StandardCharsets.UTF_8)));
            }
        }
        catch (final Exception ex) {
            if (!(ex instanceof APIException)) {
                final Response ACCESS_DENIED2 = APIResponse.errorResponse("USER0002");
                containerRequestContext.abortWith(ACCESS_DENIED2);
            }
            else {
                containerRequestContext.abortWith(APIResponse.errorResponse((APIException)ex));
            }
        }
    }
    
    private boolean isResourceAccessibleForUserRole(final RolesAllowed allowedRolesForResource) {
        for (final String role : allowedRolesForResource.value()) {
            if (this.securityContext.isUserInRole(role)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    
    private boolean isCustomerAccessibileByUser(final String customerID) throws Exception {
        final boolean isMSPorSAS = CustomerInfoUtil.getInstance().isMSP() || CustomerInfoUtil.isSAS();
        if (!isMSPorSAS) {
            return true;
        }
        if (isMSPorSAS && customerID != null && customerID.equalsIgnoreCase("all")) {
            return true;
        }
        boolean isCustomerAccessible = this.securityContext.isUserInRole("Common_Write");
        final User dcUser = (User)this.securityContext.getUserPrincipal();
        final Long userID = dcUser.getUserID();
        if (!isCustomerAccessible) {
            final List<Map<Long, String>> customerInfo = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userID);
            isCustomerAccessible = MSPWebClientUtil.isCustomerIDAvailableInCustomerList(customerID, customerInfo);
        }
        return isCustomerAccessible;
    }
    
    private boolean isResourceAvailableInDataBase(final ContainerRequestContext containerRequestContext) throws Exception {
        final MultivaluedMap<String, String> pathParameters = (MultivaluedMap<String, String>)containerRequestContext.getUriInfo().getPathParameters();
        if (pathParameters.containsKey((Object)"resourceID")) {
            final Long resourceID = Long.parseLong(((List)pathParameters.get((Object)"resourceID")).toArray()[0].toString());
            final DataObject dataObject = SyMUtil.getCachedPersistence().get("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0));
            if (dataObject.isEmpty()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
    
    private boolean checkCurrentProductMatch(final List productsAllowed) {
        final String currentProduct = ProductUrlLoader.getInstance().getValue("productcode");
        for (final Object productObject : productsAllowed) {
            final String product = (String)productObject;
            if (product.equalsIgnoreCase(currentProduct)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    
    private boolean isResourceAccessibleForServer(final RestrictMatched restrictMatched, final ContainerRequestContext containerRequestContext) {
        if (restrictMatched.value().equals("Probe") && SyMUtil.isProbeServer()) {
            return ProbeMgmtFactoryProvider.getProbeDetailsAPI().isValidProbeAuthKey(containerRequestContext);
        }
        return !restrictMatched.value().equals("SummaryServer") || !SyMUtil.isSummaryServer() || ProbeMgmtFactoryProvider.getProbeDetailsAPI().isValidSummaryServerAuthKey(containerRequestContext);
    }
    
    static {
        RestAuthorizationFilter.logger = Logger.getLogger(RestAuthorizationFilter.class.getName());
    }
}
