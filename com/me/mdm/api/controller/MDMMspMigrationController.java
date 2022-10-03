package com.me.mdm.api.controller;

import javax.ws.rs.POST;
import com.me.mdm.server.device.api.service.MspDeviceMigrationService;
import javax.ws.rs.core.Response;
import com.me.mdm.server.device.api.model.MspDeviceMigrationModel;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("internal/customer_migration")
public class MDMMspMigrationController extends BaseController
{
    @POST
    @Path("/device")
    public Response migrateDevice(@Context final ContainerRequestContext requestContext, final MspDeviceMigrationModel mspDeviceMigrationModel) throws Exception {
        mspDeviceMigrationModel.setCustomerUserDetails(requestContext);
        final MspDeviceMigrationService mspDeviceMigrationService = new MspDeviceMigrationService();
        mspDeviceMigrationService.migrateDevice(mspDeviceMigrationModel);
        return Response.status(202).build();
    }
}
