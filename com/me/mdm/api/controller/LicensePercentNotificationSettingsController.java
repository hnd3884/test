package com.me.mdm.api.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import com.me.mdm.server.enrollment.api.model.LicensePercentNotificationUpdateModel;
import javax.ws.rs.GET;
import com.me.mdm.api.model.BaseAPIModel;
import com.me.mdm.server.enrollment.api.model.LicensePercentNotificationSettingsModel;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.enrollment.api.service.LicenseNotificationSettingsService;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("license_notification_settings")
public class LicensePercentNotificationSettingsController extends BaseController
{
    private static Logger logger;
    private LicenseNotificationSettingsService licenseNotificationSettingsService;
    
    public LicensePercentNotificationSettingsController() {
        this.licenseNotificationSettingsService = new LicenseNotificationSettingsService();
    }
    
    @GET
    public LicensePercentNotificationSettingsModel getSettings(@Context final ContainerRequestContext requestContext) throws Exception {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        return this.licenseNotificationSettingsService.getLicensePercentNotificationSettings(baseAPIModel);
    }
    
    @PUT
    public Response updateSettings(@Context final ContainerRequestContext requestContext, final LicensePercentNotificationUpdateModel licensePercentNotificationUpdate) throws Exception {
        licensePercentNotificationUpdate.setCustomerUserDetails(requestContext);
        this.licenseNotificationSettingsService.updateLicensePercentNotificationSettings(licensePercentNotificationUpdate);
        return Response.status(202).build();
    }
    
    @DELETE
    public Response deleteSettings(@Context final ContainerRequestContext requestContext) throws Exception {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        this.licenseNotificationSettingsService.deleteLicensePercentNotificationSettings(baseAPIModel);
        return Response.status(202).build();
    }
    
    static {
        LicensePercentNotificationSettingsController.logger = Logger.getLogger("MDMEnrollment");
    }
}
