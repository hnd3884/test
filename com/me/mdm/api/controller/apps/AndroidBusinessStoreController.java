package com.me.mdm.api.controller.apps;

import com.me.mdm.server.apps.api.model.AppListModel;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import com.me.mdm.server.apps.businessstore.model.android.EnterpriseAppToPlaystoreAppConversionModel;
import javax.ws.rs.GET;
import java.util.List;
import java.util.Arrays;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import com.me.mdm.api.model.BaseAPIModel;
import com.me.mdm.server.apps.businessstore.model.android.AndroidStoreAppsSyncModel;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.apps.businessstore.service.AndroidBusinessStoreService;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("apps/account/pfw")
public class AndroidBusinessStoreController extends BaseController
{
    private AndroidBusinessStoreService bService;
    private AppFacade appFacade;
    
    public AndroidBusinessStoreController() {
        this.bService = new AndroidBusinessStoreService();
        this.appFacade = new AppFacade();
    }
    
    @GET
    @Path("/{businessstore_id}/failure")
    public AndroidStoreAppsSyncModel getPfwAppsSyncFailureDetails(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final String businessStoreID) {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(baseAPIModel.getUserId(), baseAPIModel.getCustomerId());
        this.appFacade.validateBusinessStoreIDs(Arrays.asList(businessStoreID), baseAPIModel.getCustomerId());
        return (AndroidStoreAppsSyncModel)this.bService.getBusinessStoreAppsFailureDetails(Long.valueOf(businessStoreID), 2, baseAPIModel);
    }
    
    @POST
    @Path("/{businessstore_id}/syncasplaystoreapp")
    public Response convertEnterpriseAppToPlaystoreApp(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final String businessStoreID, final EnterpriseAppToPlaystoreAppConversionModel enterpriseAppToPlaystoreAppConversionModel) {
        enterpriseAppToPlaystoreAppConversionModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(enterpriseAppToPlaystoreAppConversionModel.getUserId(), enterpriseAppToPlaystoreAppConversionModel.getCustomerId());
        this.appFacade.validateBusinessStoreIDs(Arrays.asList(businessStoreID), enterpriseAppToPlaystoreAppConversionModel.getCustomerId());
        this.bService.convertEnterpriseAppToPlaystoreApp(Long.valueOf(businessStoreID), 2, enterpriseAppToPlaystoreAppConversionModel);
        return Response.status(202).build();
    }
    
    @POST
    @Path("/{businessstore_id}/apps")
    public Response addOrUpdateBusinessStoreApps(@Context final ContainerRequestContext requestContext, @PathParam("businessstore_id") final String businessStoreID, final AppListModel applist) {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        MDMCustomerInfoUtil.getInstance().validateCustomerForUserId(baseAPIModel.getUserId(), baseAPIModel.getCustomerId());
        this.appFacade.validateBusinessStoreIDs(Arrays.asList(businessStoreID), baseAPIModel.getCustomerId());
        this.bService.addOrUpdateBusinessStoreApp(baseAPIModel.getUserId(), Long.valueOf(businessStoreID), baseAPIModel.getCustomerId(), applist);
        return Response.accepted().build();
    }
}
