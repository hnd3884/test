package com.me.mdm.api.controller;

import javax.ws.rs.core.Response;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import com.me.mdm.api.model.BaseAPIModel;
import com.me.mdm.server.certificate.api.model.SupervisionIdentityModel;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.certificate.api.service.SupervisionIdentityService;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("supervisionIdentity")
public class SupervisionIdentityController extends BaseController
{
    SupervisionIdentityService supervisionIdentityService;
    
    public SupervisionIdentityController() {
        this.supervisionIdentityService = new SupervisionIdentityService();
    }
    
    @GET
    public SupervisionIdentityModel getSupervisionIdentityInfo(@Context final ContainerRequestContext requestContext) throws Exception {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        return this.supervisionIdentityService.getSupervisionIdentityInfo(baseAPIModel.getCustomerId());
    }
    
    @GET
    @Path("/{cert_id}/password")
    public SupervisionIdentityModel getSupervisionIdentityCertPassword(@Context final ContainerRequestContext requestContext, @PathParam("cert_id") final Long certificateID) throws Exception {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        final SupervisionIdentityModel supervisionIdentityModel = new SupervisionIdentityModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        supervisionIdentityModel.setCertificateID(certificateID);
        return this.supervisionIdentityService.getSupervisionIdentityCertPassword(baseAPIModel.getCustomerId(), baseAPIModel.getUserName(), supervisionIdentityModel);
    }
    
    @POST
    @Path("/{cert_id}/download")
    public SupervisionIdentityModel downloadSupervisionIdentityCert(@Context final ContainerRequestContext requestContext, final SupervisionIdentityModel supervisionIdentityModel, @PathParam("cert_id") final Long certificateID) throws Exception {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        supervisionIdentityModel.setCertificateID(certificateID);
        return this.supervisionIdentityService.downloadSupervisionIdentityCert(baseAPIModel.getCustomerId(), baseAPIModel.getUserName(), supervisionIdentityModel);
    }
    
    @POST
    @Path("/regenerate")
    public Response regenerateSupervisionIdentityCert(@Context final ContainerRequestContext requestContext, final SupervisionIdentityModel supervisionIdentityModel) throws Exception {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        this.supervisionIdentityService.regenerateSupervisionIdentityCert(baseAPIModel.getCustomerId(), baseAPIModel.getUserName(), supervisionIdentityModel);
        return Response.status(202).build();
    }
}
