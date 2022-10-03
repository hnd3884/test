package com.me.ems.onpremise.security.securegatewayserver.api.v1.service;

import com.me.ems.onpremise.security.securegatewayserver.api.v1.model.SecureGatewayServerSyncData;
import java.text.ParseException;
import com.me.ems.onpremise.security.securegatewayserver.core.SecureGatewayServerUtils;
import java.util.logging.Level;
import com.me.ems.onpremise.security.securegatewayserver.core.SecureGatewayServerLicenseUtils;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.ems.onpremise.security.securegatewayserver.api.v1.model.SecureGatewayServerLicence;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

public class SecureGatewayServerService
{
    private Logger logger;
    
    public SecureGatewayServerService() {
        this.logger = Logger.getLogger("SecurityLogger");
    }
    
    public Response sgsLicenceStatus() {
        final SecureGatewayServerLicence licenseResponse = new SecureGatewayServerLicence();
        final String isSGSLicenseValid = SyMUtil.getSyMParameter("fs_license_valid");
        try {
            if (!LicenseProvider.getInstance().isFwsEnabled() && (isSGSLicenseValid == null || !isSGSLicenseValid.equalsIgnoreCase("true")) && (!SecureGatewayServerLicenseUtils.isSGSTrialFlagEnabled() || SecureGatewayServerLicenseUtils.getSGSTrialExpiryPeriod() <= 0L)) {
                licenseResponse.setIsValid(false);
            }
            else {
                licenseResponse.setIsValid(true);
            }
        }
        catch (final ParseException parseException) {
            this.logger.log(Level.SEVERE, "Parse exception while getting SGS licence valid status. ", parseException);
            Response.serverError().entity((Object)SecureGatewayServerUtils.getExceptionMap(parseException)).build();
        }
        this.logger.info("The Secure Gateway Server Licence status " + licenseResponse);
        return Response.ok().entity((Object)licenseResponse).build();
    }
    
    public Response sgsSyncData() {
        final SecureGatewayServerSyncData secureGatewayServerSyncData = new SecureGatewayServerSyncData();
        try {
            secureGatewayServerSyncData.setEnableUI(SecureGatewayServerUtils.getUIAccessEnableData());
            secureGatewayServerSyncData.setSecureGatewayServerSecurityConfiguration(SecureGatewayServerUtils.getSecurityConfigurations());
            secureGatewayServerSyncData.setProxyFileData(SecureGatewayServerUtils.getProxyFile());
            secureGatewayServerSyncData.setSecureGatewayServerCertificate(SecureGatewayServerUtils.getCertificateFromServer());
            return Response.ok().entity((Object)secureGatewayServerSyncData).build();
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception occured while getting sync data . ", exception);
            return Response.serverError().entity((Object)SecureGatewayServerUtils.getExceptionMap(exception)).build();
        }
    }
    
    public Response getCertificateForSecureGatewayServer() {
        try {
            return Response.ok().entity((Object)SecureGatewayServerUtils.getCertificateFromServer()).build();
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception while getting certificate from server\n ", exception);
            return Response.serverError().entity((Object)SecureGatewayServerUtils.getExceptionMap(exception)).build();
        }
    }
}
