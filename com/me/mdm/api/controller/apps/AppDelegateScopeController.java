package com.me.mdm.api.controller.apps;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import com.me.mdm.server.device.api.model.apps.AppDelegateScopeModel;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.apps.AppDelegateScopeManagement.AppDelegateScopeService;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("apps/{app_id}/labels/{label_id}/delegatepermission")
public class AppDelegateScopeController extends BaseController
{
    private AppDelegateScopeService appDelegateScopeService;
    
    public AppDelegateScopeController() {
        this.appDelegateScopeService = new AppDelegateScopeService();
    }
    
    @PUT
    public AppDelegateScopeModel modifyAppDelegateScope(@Context final ContainerRequestContext containerRequestContext, @PathParam("app_id") final Long appId, @PathParam("label_id") final Long labelId, final AppDelegateScopeModel appDelegateScopeModel) throws Exception {
        appDelegateScopeModel.setCustomerUserDetails(containerRequestContext);
        appDelegateScopeModel.setLabelId(labelId);
        appDelegateScopeModel.setAppId(appId);
        this.appDelegateScopeService.addOrModifyAppDelegateScope(appDelegateScopeModel);
        return appDelegateScopeModel;
    }
    
    @GET
    public AppDelegateScopeModel getAppDelegateScope(@Context final ContainerRequestContext containerRequestContext, @PathParam("app_id") final Long appId, @PathParam("label_id") final Long labelId) throws Exception {
        final AppDelegateScopeModel appDelegateGetModel = this.appDelegateScopeService.initGetResponseObject(containerRequestContext, appId, labelId);
        return this.appDelegateScopeService.getAppDelegateScope(appDelegateGetModel);
    }
}
