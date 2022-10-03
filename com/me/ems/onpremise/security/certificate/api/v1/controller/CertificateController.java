package com.me.ems.onpremise.security.certificate.api.v1.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.ems.onpremise.security.certificate.api.Exception.CertificateAPIException;
import com.me.ems.onpremise.security.certificate.api.model.ImportCertificateResponse;
import com.me.ems.onpremise.security.certificate.api.model.CertificateFormBean;
import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.AllowEntityFilter;
import javax.ws.rs.GET;
import com.me.ems.onpremise.security.certificate.api.model.Certificate;
import com.me.ems.onpremise.security.certificate.factory.CertificateServiceFactoryProvider;
import com.me.ems.onpremise.security.certificate.factory.CertificateService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.Path;

@Path("certificate")
public class CertificateController
{
    @Context
    private SecurityContext securityContext;
    CertificateService certificateService;
    
    public CertificateController() {
        this.certificateService = CertificateServiceFactoryProvider.getSSLCertificationService();
    }
    
    @GET
    @AllowEntityFilter
    @Produces({ "application/certificateDetails.v1+json" })
    public Certificate getCertificateDetails() {
        return this.certificateService.getCertificateDetails();
    }
    
    @POST
    @AllowEntityFilter
    @Consumes({ "application/importCertificateData.v1+json" })
    @Produces({ "application/importCertificateResponse.v1+json" })
    public ImportCertificateResponse importCertificate(final CertificateFormBean certificateFormBean) throws CertificateAPIException {
        return this.certificateService.importCertificate(certificateFormBean, this.securityContext);
    }
}
