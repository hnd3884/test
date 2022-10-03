package com.me.ems.onpremise.security.certificate.factory;

import java.util.Map;
import com.me.ems.onpremise.security.certificate.api.Exception.CertificateAPIException;
import com.me.ems.onpremise.security.certificate.api.model.ImportCertificateResponse;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.onpremise.security.certificate.api.model.CertificateFormBean;
import com.me.ems.onpremise.security.certificate.api.model.Certificate;

public interface CertificateService
{
    Certificate getCertificateDetails();
    
    ImportCertificateResponse importCertificate(final CertificateFormBean p0, final SecurityContext p1) throws CertificateAPIException;
    
    default Map getProbeCertificateDetailsList() {
        return null;
    }
}
