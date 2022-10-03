package com.me.mdm.api.controller;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import com.me.mdm.server.enrollment.api.model.LicenseResolveModel;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.enrollment.api.service.MDMLicenseService;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("license")
public class MDMLicenseController extends BaseController
{
    MDMLicenseService mdmLicenseService;
    
    public MDMLicenseController() {
        this.mdmLicenseService = new MDMLicenseService();
    }
    
    @POST
    @Path("resolve_device_shortfall")
    public Response resolveShortfall(@Context final ContainerRequestContext requestContext, final LicenseResolveModel licenseResolveModel) throws Exception {
        licenseResolveModel.setCustomerUserDetails(requestContext);
        this.mdmLicenseService.resolveMDMLicenseCount(licenseResolveModel);
        return Response.status(202).build();
    }
}
