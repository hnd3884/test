package com.me.ems.onpremise.security.securegatewayserver.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import com.me.ems.onpremise.security.securegatewayserver.api.v1.service.SecureGatewayServerService;
import javax.ws.rs.Path;

@Path("secure-gateway-server")
public class SecureGatewayServerController
{
    SecureGatewayServerService secureGatewayServerService;
    
    public SecureGatewayServerController() {
        this.secureGatewayServerService = new SecureGatewayServerService();
    }
    
    @GET
    @Path("license")
    @Produces({ "application/sgsLicence.v1+json" })
    public Response sgsLicenseStatus() {
        return this.secureGatewayServerService.sgsLicenceStatus();
    }
    
    @GET
    @Path("sync-data")
    @Produces({ "application/sgsSyncData.v1+json" })
    public Response sgsSyncData() {
        return this.secureGatewayServerService.sgsSyncData();
    }
    
    @GET
    @Path("certificate")
    @Produces({ "application/sgsCertificate.v1+json" })
    public Response sgsCertificate() {
        return this.secureGatewayServerService.getCertificateForSecureGatewayServer();
    }
}
