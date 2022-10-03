package com.me.mdm.api.controller;

import com.me.mdm.server.apps.businessstore.model.ios.IOSBusinessStoreSyncModel;
import javax.ws.rs.POST;
import com.me.mdm.server.apps.businessstore.model.ios.IOSEnterpriseBusinessStoreModel;
import javax.ws.rs.DELETE;
import com.me.mdm.server.apps.businessstore.model.BusinessStoreModel;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import com.me.mdm.server.apps.businessstore.model.VppUploadModel;
import javax.ws.rs.GET;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import com.me.mdm.api.model.BaseAPIModel;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.apps.businessstore.service.BusinessStoreService;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("apps/account/vpp")
public class IOSBusinessStoreController extends BaseController
{
    protected static Logger logger;
    BusinessStoreService bService;
    
    public IOSBusinessStoreController() {
        this.bService = new BusinessStoreService();
    }
    
    @GET
    @Path("/{businessstore_id}/failure")
    public Map getBusinessStoreAppsFailureDetails(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final String businessStoreID, final Map reqParams) {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(baseAPIModel.getUserId(), baseAPIModel.getCustomerId());
        JSONObject responseJSON = (JSONObject)this.bService.getBusinessStoreAppsFailureDetails(Long.valueOf(businessStoreID), 1, baseAPIModel);
        if (responseJSON == null) {
            responseJSON = new JSONObject();
            responseJSON = JSONUtil.toJSON("status", 204);
        }
        return JSONUtil.convertJSONtoMap(responseJSON);
    }
    
    @PUT
    @Path("/{businessstore_id}")
    public Response modifyVPPToken(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final String businessStoreID, final VppUploadModel vppUploadModel) {
        vppUploadModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(vppUploadModel.getUserId(), vppUploadModel.getCustomerId());
        final JSONObject msgBody = new JSONObject();
        final Long vppFileID = vppUploadModel.getVppFileID();
        final Integer licenseAssignType = vppUploadModel.getLicenseType();
        final String emailAddress = vppUploadModel.getEmailAddress();
        if (vppFileID != null && vppFileID != 0L) {
            msgBody.put("vpp_token_file", (Object)vppUploadModel.getVppFileID());
        }
        if (licenseAssignType != null && licenseAssignType != 0) {
            msgBody.put("license_assign_type", (Object)vppUploadModel.getLicenseType());
        }
        if (emailAddress != null && !emailAddress.equalsIgnoreCase("")) {
            msgBody.put("email_address", (Object)emailAddress);
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg_body", (Object)msgBody);
        jsonObject.put("CUSTOMER_ID", (Object)vppUploadModel.getCustomerId());
        jsonObject.put("userID", (Object)vppUploadModel.getUserId());
        jsonObject.put("userName", (Object)vppUploadModel.getUserName());
        this.bService.modifyStoreDetails(Long.valueOf(businessStoreID), 1, jsonObject);
        return Response.status(202).build();
    }
    
    @DELETE
    @Path("/{businessstore_id}")
    public Response deleteVPPToken(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final Long businessStoreID) {
        final BaseAPIModel baseAPIModel = new BusinessStoreModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(baseAPIModel.getUserId(), baseAPIModel.getCustomerId());
        this.bService.deleteStoreDetails(1, baseAPIModel.getCustomerId(), businessStoreID, baseAPIModel.getUserName());
        return Response.status(202).build();
    }
    
    @GET
    @Path("/{businessstore_id}")
    public IOSEnterpriseBusinessStoreModel getVPPTokenDetails(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final Long businessStoreID) {
        final IOSEnterpriseBusinessStoreModel iosEnterpriseBusinessStoreModel = new IOSEnterpriseBusinessStoreModel();
        iosEnterpriseBusinessStoreModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(iosEnterpriseBusinessStoreModel.getUserId(), iosEnterpriseBusinessStoreModel.getCustomerId());
        final Long customerID = iosEnterpriseBusinessStoreModel.getCustomerId();
        return (IOSEnterpriseBusinessStoreModel)this.bService.getStoreDetails(businessStoreID, customerID, 1);
    }
    
    @POST
    @Path("/sync/{businessstore_id}")
    public Response syncVPPToken(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final Long businessStoreID, final Map reqParams) {
        final BaseAPIModel baseAPIModel = new BusinessStoreModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(baseAPIModel.getUserId(), baseAPIModel.getCustomerId());
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg_body", (Object)JSONUtil.mapToJSON(reqParams));
        final Long customerID = baseAPIModel.getCustomerId();
        this.bService.syncStoreToMDM(jsonObject, customerID, businessStoreID, 1);
        return Response.status(202).build();
    }
    
    @GET
    @Path("/sync/{businessstore_id}")
    public IOSBusinessStoreSyncModel getVPPSyncStatus(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final Long businessStoreID, final Map reqParams) {
        final BaseAPIModel baseAPIModel = new BusinessStoreModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(baseAPIModel.getUserId(), baseAPIModel.getCustomerId());
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg_body", (Object)JSONUtil.mapToJSON(reqParams));
        final Long customerID = baseAPIModel.getCustomerId();
        return (IOSBusinessStoreSyncModel)this.bService.getSyncStatus(businessStoreID, customerID, 1);
    }
    
    @POST
    @Path("/{businessstore_id}/validatepredistribute")
    public Map validatePreAssociation(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final Long businessStoreID, final Map reqParams) {
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg_body", (Object)JSONUtil.mapToJSON(reqParams));
        return this.bService.validatePreAssociation((long)businessStoreID, jsonObject, customerID);
    }
    
    static {
        IOSBusinessStoreController.logger = Logger.getLogger("MDMApiLogger");
    }
}
