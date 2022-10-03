package com.me.mdm.api.controller;

import com.me.mdm.server.device.api.annotations.ValidDevice;
import com.me.mdm.api.error.APIHTTPException;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import com.me.mdm.api.common.Validator;
import javax.ws.rs.GET;
import java.util.Iterator;
import com.me.mdm.server.apps.blocklist.service.BlocklistServices;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONObject;
import com.me.mdm.server.apps.blocklist.model.BlocklistPOJO;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.mdm.server.apps.blocklist.model.BlocklistResponsePOJO;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("inventory/apps")
public class InventoryAppsController extends BaseController
{
    @Path("/{app_id}/blocklist/groups")
    @GET
    @Validator(pathParam = "app_id", authorizerClass = "com.me.mdm.server.apps.blocklist.validators.InventoryAppIDAuthorizer")
    public BlocklistResponsePOJO blockListGroupTree(@Context final ContainerRequestContext requestContext, @PathParam("app_id") final Long appID, @Context final UriInfo uriInfo) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        BlocklistPOJO blocklistPOJO = new BlocklistPOJO();
        blocklistPOJO = (BlocklistPOJO)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)BlocklistPOJO.class);
        blocklistPOJO.setCustomerUserDetails(requestContext);
        blocklistPOJO.setAppGroupIds(new ArrayList<Long>(Collections.singleton(appID)));
        final BlocklistServices blocklistServices = new BlocklistServices();
        return blocklistServices.getGroupTreeDataForAppGroups(blocklistPOJO, 2);
    }
    
    @POST
    @Path("/blocklist/groups/tree/{type}")
    public BlocklistResponsePOJO inventoryAppGroupTree(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo, BlocklistPOJO blocklistPOJO, @PathParam("type") final int type, @QueryParam("group_type") final String groupType) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        final List<Long> appGroupIDs = blocklistPOJO.getAppGroupIds();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        blocklistPOJO = (BlocklistPOJO)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)BlocklistPOJO.class);
        blocklistPOJO.setAppGroupIds(appGroupIDs);
        blocklistPOJO.setCustomerUserDetails(requestContext);
        blocklistPOJO.setGroupType(groupType);
        final BlocklistServices blocklistServices = new BlocklistServices();
        return blocklistServices.getGroupTreeDataForAppGroups(blocklistPOJO, type);
    }
    
    @POST
    @Path("/blocklist/devices/tree/{type}")
    public BlocklistResponsePOJO blocklistDeviceTree(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo, BlocklistPOJO blocklistPOJO, @PathParam("type") final int type, @QueryParam("platform_type") final String platform) throws Exception, APIHTTPException {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        final List<Long> appGroupIDs = blocklistPOJO.getAppGroupIds();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        blocklistPOJO = (BlocklistPOJO)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)BlocklistPOJO.class);
        blocklistPOJO.setAppGroupIds(appGroupIDs);
        blocklistPOJO.setCustomerUserDetails(requestContext);
        blocklistPOJO.setPlatform(platform);
        final BlocklistServices blocklistServices = new BlocklistServices();
        return blocklistServices.getDeviceTreeDataForAppGroups(blocklistPOJO, type);
    }
    
    @GET
    @Path("/{app_id}/blocklist/devices")
    @Validator(pathParam = "app_id", authorizerClass = "com.me.mdm.server.apps.blocklist.validators.InventoryAppIDAuthorizer")
    public BlocklistResponsePOJO inevntoryAppDeviceTree(@Context final ContainerRequestContext requestContext, @PathParam("app_id") final Long appID, @Context final UriInfo uriInfo) throws Exception, APIHTTPException {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        BlocklistPOJO blocklistPOJO = new BlocklistPOJO();
        blocklistPOJO = (BlocklistPOJO)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)BlocklistPOJO.class);
        blocklistPOJO.setCustomerUserDetails(requestContext);
        blocklistPOJO.setAppGroupIds(new ArrayList<Long>(Collections.singleton(appID)));
        final BlocklistServices blocklistServices = new BlocklistServices();
        return blocklistServices.getDeviceTreeDataForAppGroups(blocklistPOJO, 2);
    }
    
    @GET
    @Path("/devices/{device_id}/blocklist/apps")
    @ValidDevice
    public BlocklistResponsePOJO blocklistedAppsByDevices(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceID, @Context final UriInfo uriInfo) throws Exception, APIHTTPException {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        final BlocklistPOJO blocklistPOJO = (BlocklistPOJO)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)BlocklistPOJO.class);
        blocklistPOJO.setCustomerUserDetails(requestContext);
        final BlocklistServices blocklistServices = new BlocklistServices();
        return blocklistServices.getAppsBlockedByDevice(blocklistPOJO.getCustomerId(), deviceID, blocklistPOJO);
    }
}
