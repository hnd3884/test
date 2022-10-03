package com.me.mdm.api.common.filter;

import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Map;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.me.mdm.api.controller.IDauthorizer;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.common.Validator;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.uac.api.v1.model.User;
import java.lang.annotation.Annotation;
import javax.annotation.security.PermitAll;
import com.me.mdm.server.device.api.annotations.ValidDeviceUDID;
import com.me.mdm.server.device.api.annotations.ValidDevice;
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
public class MDMRestAuthorizationFilter implements ContainerRequestFilter
{
    protected static Logger logger;
    @Context
    private ResourceInfo resourceInfo;
    @Context
    private SecurityContext securityContext;
    @Context
    private HttpServletRequest servletRequest;
    
    public void filter(final ContainerRequestContext containerRequestContext) throws APIHTTPException {
        final Method resourceMethod = this.resourceInfo.getResourceMethod();
        final ValidDevice validDevice = resourceMethod.getAnnotation(ValidDevice.class);
        final ValidDeviceUDID validDeviceUDID = resourceMethod.getAnnotation(ValidDeviceUDID.class);
        try {
            if (resourceMethod.isAnnotationPresent((Class<? extends Annotation>)PermitAll.class)) {
                return;
            }
            final User dcUser = (User)this.securityContext.getUserPrincipal();
            final Long userID = dcUser.getUserID();
            final CustomerInfoUtil customerInfoUtil = CustomerInfoUtil.getInstance();
            final String customerIDStr = customerInfoUtil.isMSP() ? containerRequestContext.getHeaderString("X-Customer") : (customerInfoUtil.getDefaultCustomer() + "");
            final boolean isCustomerAccessibleByUser = resourceMethod.isAnnotationPresent((Class<? extends Annotation>)CustomerSegmented.class) ? this.isCustomerAccessibileByUser(userID, customerIDStr) : Boolean.TRUE;
            final boolean isDeviceValidate = resourceMethod.isAnnotationPresent(ValidDevice.class) ? this.isDeviceAvailableForCustomer(containerRequestContext, validDevice, customerIDStr) : Boolean.TRUE;
            final boolean isDeviceUDIDValidate = resourceMethod.isAnnotationPresent(ValidDeviceUDID.class) ? this.isDeviceUDIDAvailableForCustomer(containerRequestContext, validDeviceUDID, customerIDStr) : Boolean.TRUE;
            final boolean isIDauthorizerPresent = resourceMethod.isAnnotationPresent(Validator.class);
            if (customerInfoUtil.isMSP() && !customerIDStr.equalsIgnoreCase("All")) {
                if (customerIDStr == null) {
                    throw new APIHTTPException("COM0022", new Object[0]);
                }
                CustomerInfoThreadLocal.setCustomerId(customerIDStr);
            }
            containerRequestContext.setProperty("X-Customer", (Object)customerIDStr);
            if (!isCustomerAccessibleByUser) {
                throw new APIHTTPException("COM0013", new Object[0]);
            }
            if (!isDeviceValidate) {
                throw new APIHTTPException("COM0008", new Object[] { "device_id" });
            }
            if (!isDeviceUDIDValidate) {
                throw new APIHTTPException("COM0008", new Object[] { "udid" });
            }
            if (isIDauthorizerPresent) {
                final Validator validator = resourceMethod.getAnnotation(Validator.class);
                if (validator != null) {
                    final String authorizerClass = validator.authorizerClass();
                    final IDauthorizer idAuthorizer = (IDauthorizer)Class.forName(authorizerClass).newInstance();
                    final MultivaluedMap<String, String> pathParameters = (MultivaluedMap<String, String>)containerRequestContext.getUriInfo().getPathParameters();
                    if (pathParameters.containsKey((Object)validator.pathParam())) {
                        final Object id = ((List)pathParameters.get((Object)validator.pathParam())).toArray()[0];
                        idAuthorizer.authorize(customerIDStr, userID, validator.pathParam(), new ArrayList<Object>(Arrays.asList(id)));
                    }
                }
            }
            final String inputStream = this.servletRequest.getParameter("zoho-inputstream");
            if (inputStream != null && !inputStream.trim().isEmpty()) {
                containerRequestContext.setEntityStream((InputStream)new ByteArrayInputStream(inputStream.getBytes(StandardCharsets.UTF_8)));
            }
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            MDMRestAuthorizationFilter.logger.log(Level.SEVERE, "Exception In rest Authorization filter", ex2);
            if (!(ex2 instanceof APIHTTPException)) {
                throw new APIHTTPException("COM0013", new Object[0]);
            }
        }
    }
    
    private boolean isCustomerAccessibileByUser(final Long userID, final String customerID) throws Exception {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        if (!isMSP) {
            return true;
        }
        if (isMSP && customerID != null && customerID.equalsIgnoreCase("all")) {
            return true;
        }
        boolean isCustomerAccessible = this.securityContext.isUserInRole("Common_Write");
        if (!isCustomerAccessible) {
            final List<Map<Long, String>> customerInfo = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userID);
            isCustomerAccessible = MSPWebClientUtil.isCustomerIDAvailableInCustomerList(customerID, (List)customerInfo);
        }
        return isCustomerAccessible;
    }
    
    private boolean isDeviceAvailableForCustomer(final ContainerRequestContext containerRequestContext, final ValidDevice validDevice, final String customerIDStr) throws APIHTTPException {
        try {
            final MultivaluedMap<String, String> pathParameters = (MultivaluedMap<String, String>)containerRequestContext.getUriInfo().getPathParameters();
            if (pathParameters.containsKey((Object)validDevice.pathParam())) {
                final Long deviceId = Long.parseLong(((List)pathParameters.get((Object)validDevice.pathParam())).toArray()[0].toString());
                MDMRestAuthorizationFilter.logger.log(Level.INFO, "validating the device id {0} for the customer id {1}", new Object[] { deviceId, customerIDStr });
                final SelectQuery selectQuery = this.getDeviceQuery(customerIDStr);
                Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceId, 0);
                criteria = MDMDBUtil.andCriteria(criteria, selectQuery.getCriteria());
                selectQuery.setCriteria(criteria);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                if (dataObject.isEmpty()) {
                    throw new APIHTTPException("COM0008", new Object[] { deviceId });
                }
            }
        }
        catch (final DataAccessException ex) {
            MDMRestAuthorizationFilter.logger.log(Level.SEVERE, "Exception while validating device", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return Boolean.TRUE;
    }
    
    private boolean isDeviceUDIDAvailableForCustomer(final ContainerRequestContext containerRequestContext, final ValidDeviceUDID validDeviceUDID, final String customerIDStr) throws APIHTTPException {
        try {
            final MultivaluedMap<String, String> pathParameters = (MultivaluedMap<String, String>)containerRequestContext.getUriInfo().getPathParameters();
            if (pathParameters.containsKey((Object)validDeviceUDID.pathParam())) {
                final String udid = ((List)pathParameters.get((Object)validDeviceUDID.pathParam())).toArray()[0].toString();
                MDMRestAuthorizationFilter.logger.log(Level.INFO, "validating the device id {0} for the customer id {1}", new Object[] { udid, customerIDStr });
                final SelectQuery selectQuery = this.getDeviceQuery(customerIDStr);
                Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
                criteria = MDMDBUtil.andCriteria(criteria, selectQuery.getCriteria());
                selectQuery.setCriteria(criteria);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                if (dataObject.isEmpty()) {
                    throw new APIHTTPException("COM0008", new Object[] { udid });
                }
                final Row resourceRow = dataObject.getFirstRow("Resource");
                containerRequestContext.setProperty("MANAGED_DEVICE_ID", resourceRow.get("RESOURCE_ID"));
            }
        }
        catch (final DataAccessException ex) {
            MDMRestAuthorizationFilter.logger.log(Level.SEVERE, "Exception while validating device", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return Boolean.TRUE;
    }
    
    private SelectQuery getDeviceQuery(final String customerIdStr) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria criteria = null;
        if (ApiFactoryProvider.getUtilAccessAPI().isMSP()) {
            final User dcUser = (User)this.securityContext.getUserPrincipal();
            final Long userID = dcUser.getUserID();
            selectQuery.addJoin(new Join("Resource", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            criteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0);
        }
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        criteria = MDMDBUtil.andCriteria(criteria, new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerIdStr, 0));
        criteria = MDMDBUtil.andCriteria(criteria, selectQuery.getCriteria());
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    static {
        MDMRestAuthorizationFilter.logger = Logger.getLogger("MDMApiLogger");
    }
}
