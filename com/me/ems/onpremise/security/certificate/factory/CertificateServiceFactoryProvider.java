package com.me.ems.onpremise.security.certificate.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class CertificateServiceFactoryProvider
{
    static Logger logger;
    private static CertificateService certificateService;
    
    public static CertificateService getSSLCertificationService() {
        try {
            if (CertificateServiceFactoryProvider.certificateService == null) {
                if (SyMUtil.isProbeServer()) {
                    CertificateServiceFactoryProvider.certificateService = (CertificateService)Class.forName("com.me.ems.onpremise.security.certificate.summeryserver.probe.api.v1.service.PSCertificateServiceImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    CertificateServiceFactoryProvider.certificateService = (CertificateService)Class.forName("com.me.ems.onpremise.security.certificate.summeryserver.summary.api.v1.service.SSCertificateServiceImpl").newInstance();
                }
                else {
                    CertificateServiceFactoryProvider.certificateService = (CertificateService)Class.forName("com.me.ems.onpremise.security.certificate.api.v1.service.CertificateServiceImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            CertificateServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting CertificateServiceObject", e);
        }
        return CertificateServiceFactoryProvider.certificateService;
    }
    
    static {
        CertificateServiceFactoryProvider.logger = Logger.getLogger(CertificateServiceFactoryProvider.class.getName());
        CertificateServiceFactoryProvider.certificateService = null;
    }
}
