package com.me.ems.onpremise.security.certificate.summeryserver.summary.api.v1.controller;

import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.AllowEntityFilter;
import javax.ws.rs.GET;
import java.util.Map;
import com.me.ems.onpremise.security.certificate.factory.CertificateServiceFactoryProvider;
import com.me.ems.onpremise.security.certificate.factory.CertificateService;
import javax.ws.rs.Path;

@Path("certificate")
public class SSCertificateController
{
    CertificateService certificateService;
    
    public SSCertificateController() {
        this.certificateService = CertificateServiceFactoryProvider.getSSLCertificationService();
    }
    
    @GET
    @Path("probeConfigStatus")
    @AllowEntityFilter
    @Produces({ "application/probeCertificateDetails.v1+json" })
    public Map getCertificateDetails() {
        return this.certificateService.getProbeCertificateDetailsList();
    }
}
