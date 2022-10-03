package com.me.mdm.api.controller.apps;

import com.me.mdm.server.profiles.api.model.ProfileAssociationToGroupModel;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;
import javax.ws.rs.PUT;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicySearchModel;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyListModel;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.POST;
import com.me.mdm.api.common.Validator;
import javax.ws.rs.GET;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyAuthorizer;
import com.me.mdm.server.profiles.api.service.AppUpdatePolicyDistributionService;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyService;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("app_update_policy")
public class AppUpdatePolicyController extends BaseController
{
    AppUpdatePolicyService appUpdatePolicyService;
    AppUpdatePolicyDistributionService appUpdatePolicyDistributionService;
    AppUpdatePolicyAuthorizer appUpdatePolicyAuthorizer;
    
    public AppUpdatePolicyController() {
        this.appUpdatePolicyService = new AppUpdatePolicyService();
        this.appUpdatePolicyDistributionService = new AppUpdatePolicyDistributionService();
        this.appUpdatePolicyAuthorizer = new AppUpdatePolicyAuthorizer();
    }
    
    @GET
    @Validator(pathParam = "app_update_policy_id", authorizerClass = "com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyAuthorizer")
    @Path("/{app_update_policy_id}")
    public AppUpdatePolicyModel getAppUpdatePolicy(@Context final ContainerRequestContext containerRequestContext, @PathParam("app_update_policy_id") final Long appUpdatePolicyId) {
        this.appUpdatePolicyAuthorizer.checkIfAppUpdatePolicyFeatureEnabled();
        final AppUpdatePolicyModel appUpdatePolicyModel = this.appUpdatePolicyService.getAppUpdatePolicy(appUpdatePolicyId);
        return appUpdatePolicyModel;
    }
    
    @POST
    public AppUpdatePolicyModel addAppUpdatePolicy(@Context final ContainerRequestContext containerRequestContext, final AppUpdatePolicyModel appUpdatePolicyModel) {
        this.appUpdatePolicyAuthorizer.checkIfAppUpdatePolicyFeatureEnabled();
        appUpdatePolicyModel.setCustomerUserDetails(containerRequestContext);
        this.appUpdatePolicyAuthorizer.validateAppUpdatePolicyModel(appUpdatePolicyModel);
        this.appUpdatePolicyService.addAppUpdatePolicy(appUpdatePolicyModel);
        return this.appUpdatePolicyService.getAppUpdatePolicy(appUpdatePolicyModel.getProfileId());
    }
    
    @GET
    public AppUpdatePolicyListModel getAppUpdatePolicies(@Context final ContainerRequestContext containerRequestContext, @Context final UriInfo uriInfo) throws Exception {
        this.appUpdatePolicyAuthorizer.checkIfAppUpdatePolicyFeatureEnabled();
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        final AppUpdatePolicySearchModel appUpdatePolicySearchModel = (AppUpdatePolicySearchModel)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)AppUpdatePolicySearchModel.class);
        appUpdatePolicySearchModel.setCustomerUserDetails(containerRequestContext);
        return this.appUpdatePolicyService.getAppUpdatePolicies(appUpdatePolicySearchModel);
    }
    
    @PUT
    @Validator(pathParam = "app_update_policy_id", authorizerClass = "com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyAuthorizer")
    @Path("/{app_update_policy_id}")
    public AppUpdatePolicyModel updateAppUpdatePolicy(@Context final ContainerRequestContext containerRequestContext, @PathParam("app_update_policy_id") final Long appUpdatePolicyId, final AppUpdatePolicyModel appUpdatePolicyModel) {
        this.appUpdatePolicyAuthorizer.checkIfAppUpdatePolicyFeatureEnabled();
        appUpdatePolicyModel.setCustomerUserDetails(containerRequestContext);
        appUpdatePolicyModel.setProfileId(appUpdatePolicyId);
        this.appUpdatePolicyAuthorizer.validateAppUpdatePolicyModel(appUpdatePolicyModel);
        this.appUpdatePolicyService.updateAppUpdatePolicy(appUpdatePolicyModel);
        return this.appUpdatePolicyService.getAppUpdatePolicy(appUpdatePolicyModel.getProfileId());
    }
    
    @DELETE
    @Validator(pathParam = "app_update_policy_id", authorizerClass = "com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyAuthorizer")
    @Path("/{app_update_policy_id}")
    public Response deleteAppUpdatePolicy(@Context final ContainerRequestContext containerRequestContext, @PathParam("app_update_policy_id") final Long appUpdatePolicyId) {
        this.appUpdatePolicyAuthorizer.checkIfAppUpdatePolicyFeatureEnabled();
        final AppUpdatePolicyModel appUpdatePolicyModel = new AppUpdatePolicyModel();
        appUpdatePolicyModel.setProfileId(appUpdatePolicyId);
        appUpdatePolicyModel.setCustomerUserDetails(containerRequestContext);
        this.appUpdatePolicyService.deleteAppUpdatePolicy(appUpdatePolicyModel);
        return Response.status(202).build();
    }
    
    @POST
    @Path("/groups")
    public Response associateAppUpdatePolicyToGroups(@Context final ContainerRequestContext containerRequestContext, final ProfileAssociationToGroupModel profileAssociationToGroupModel) {
        this.appUpdatePolicyAuthorizer.checkIfAppUpdatePolicyFeatureEnabled();
        profileAssociationToGroupModel.setCustomerUserDetails(containerRequestContext);
        this.appUpdatePolicyAuthorizer.validateProfileAssociationGroupModel(profileAssociationToGroupModel);
        this.appUpdatePolicyDistributionService.associateProfilesToGroups(profileAssociationToGroupModel);
        return Response.status(202).build();
    }
    
    @DELETE
    @Validator(pathParam = "app_update_policy_id", authorizerClass = "com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyAuthorizer")
    @Path("/{app_update_policy_id}/groups")
    public Response disassociateAppUpdateToGroups(@Context final ContainerRequestContext containerRequestContext, @PathParam("app_update_policy_id") final Long appUpdatePolicyId, final ProfileAssociationToGroupModel profileAssociationToGroupModel) {
        this.appUpdatePolicyAuthorizer.checkIfAppUpdatePolicyFeatureEnabled();
        profileAssociationToGroupModel.setCustomerUserDetails(containerRequestContext);
        profileAssociationToGroupModel.setProfileId(appUpdatePolicyId);
        this.appUpdatePolicyAuthorizer.validateProfileAssociationGroupModel(profileAssociationToGroupModel);
        this.appUpdatePolicyDistributionService.disassociateProfilesToGroups(profileAssociationToGroupModel);
        return Response.status(202).build();
    }
}
