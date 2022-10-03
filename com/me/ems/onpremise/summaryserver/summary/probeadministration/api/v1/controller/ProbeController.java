package com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.controller;

import javax.ws.rs.DefaultValue;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.response.APIResponse;
import javax.security.auth.login.LoginException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.ems.framework.uac.api.v1.model.AuthUser;
import javax.ws.rs.PathParam;
import java.util.Map;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import java.util.List;
import com.me.ems.onpremise.uac.api.v1.service.OPUserService;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.ProbeService;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.ProbeDetailsService;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.ProbeNotificationService;
import javax.ws.rs.Path;

@Path("")
public class ProbeController
{
    private ProbeNotificationService probeNotificationServiceService;
    private ProbeDetailsService probeDetailsService;
    private ProbeService probeService;
    private OPUserService opUserService;
    
    public ProbeController() {
        this.probeNotificationServiceService = new ProbeNotificationService();
        this.probeDetailsService = new ProbeDetailsService();
        this.probeService = new ProbeService();
        this.opUserService = OPUserService.getInstance();
    }
    
    @GET
    @Path("allProbeDetails")
    @Produces({ "application/probesList.v1+json" })
    public List<HashMap> getProbeDetails() {
        final ProbeDetailsService probeDetailsService = this.probeDetailsService;
        return ProbeDetailsService.getProbeDetails(null);
    }
    
    @GET
    @Path("probes/isNameUnique")
    @Produces({ "application/nameUniqueResp.v1+json" })
    public Map isProbeNameUnique(@QueryParam("probeName") final String probeName) {
        return this.probeDetailsService.isProbeNameUnique(probeName);
    }
    
    @GET
    @Path("probes/{probeId}")
    @Produces({ "application/probeDetails.v1+json" })
    public HashMap getProbeSpecificDetails(@PathParam("probeId") final Long probeId) {
        return this.probeService.getProbeSpecificDetails(probeId);
    }
    
    @PUT
    @Path("probes/{probeId}/regenerateSummaryServerAuthKey")
    @Produces({ "application/summaryServerAuthKey.v1+json" })
    @Consumes({ "application/authentication.v1+json" })
    public Object validateAndRegenerateSummaryServerAuthKey(@PathParam("probeId") final Long probeId, final AuthUser authUser) {
        final String userName = authUser.getUserName();
        String password = authUser.getEncodedPassword();
        final String domainName = authUser.getDomainName();
        final String authType = authUser.getAuthType();
        password = ((password != null && !password.trim().isEmpty()) ? SyMUtil.decodeAsUTF16LE(password) : null);
        try {
            final User userDetails = this.opUserService.validateAndAuthenticateUser(userName, password, domainName, authType);
            if (userDetails != null) {
                return this.probeService.regenerateSummaryServerAuthKey(probeId);
            }
            throw new LoginException(I18N.getMsg("ems.rest.userdetails.not.found", new Object[0]));
        }
        catch (final Exception ex) {
            return APIResponse.errorResponse("USER0003");
        }
    }
    
    @PUT
    @Path("probes/{probeId}/updateProbeAuthKey")
    @Consumes({ "application/probeAuthKey.v1+json" })
    @Produces({ "application/updationStatus.v1+json" })
    public HashMap updateProbeAuthKey(@PathParam("probeId") final Long probeId, final Map probeAuthKeyDetails) throws Exception {
        final String probeAuthKey = probeAuthKeyDetails.get("probeAuthKey");
        if (probeAuthKey == null) {
            throw new APIException("PRBE9500302");
        }
        final HashMap updatedDetails = this.probeService.updateProbeAuthKey(probeId, probeAuthKey);
        return updatedDetails;
    }
    
    @GET
    @Path("probes/{probeId}/getNotificationSettings")
    @Produces({ "application/probeNotificationSettings.v1+json" })
    public HashMap getProbeNotificationSettings(@PathParam("probeId") final Long probeId) {
        return this.probeNotificationServiceService.getNotificationSettings(probeId);
    }
    
    @PUT
    @Path("probes/{probeId}/updateNotificationSettings")
    @Produces({ "application/probeNotificationSettings.v1+json" })
    @Consumes({ "application/probeNotificationUpdateRequest.v1+json" })
    public HashMap updateProbeNotificationSettings(@PathParam("probeId") final Long probeId, final Map notificationSettings) {
        return this.probeNotificationServiceService.updateNotificationSettings(probeId, notificationSettings);
    }
    
    @GET
    @Path("probes/{probeId}/probeInstallationMeta")
    @Produces({ "application/installationDetails.v1+json" })
    public HashMap getInstallationDetails(@PathParam("probeId") final Long probeId) {
        return this.probeDetailsService.getInstallationDetails(probeId);
    }
    
    @GET
    @Path("isProbeServerAdded")
    @Produces({ "application/probeAddedStatus.v1+json" })
    public HashMap isProbeAdded() throws DataAccessException {
        return this.probeDetailsService.isProbeAdded();
    }
    
    @GET
    @Path("probes/notInstalledProbes")
    @Produces({ "application/notInstalledProbesList.v1+json" })
    public ArrayList getNotInstalledProbes() throws DataAccessException {
        return this.probeDetailsService.getNotInstalledProbes();
    }
    
    @GET
    @Path("probes/{probeId}/checkProbeServerConnectivity")
    @Produces({ "application/probeServerStatus.v1+json" })
    public HashMap checkPSConnectivity(@PathParam("probeId") final Long probeId) {
        return this.probeService.checkPSConnectivity(probeId);
    }
    
    @GET
    @Path("probes/{probeId}/getApiKeyDetails")
    @Produces({ "application/apiKeyDetails.v1+json" })
    public HashMap getApiKeyDetails(@PathParam("probeId") final Long probeId) {
        return this.probeDetailsService.getApiKeyDetails(probeId);
    }
    
    @GET
    @Path("probes/{probeId}/probeServerUrl")
    @Produces({ "application/probeServerUrl.v1+json" })
    public HashMap getProbeServerUrl(@PathParam("probeId") final Long probeId) {
        return this.probeDetailsService.getProbeServerUrl(probeId);
    }
    
    @GET
    @Path("probes/{probeId}/lastSyncTime")
    @Produces({ "application/probeLastSyncTime.v1+json" })
    public HashMap getProbeLastSyncTime(@PathParam("probeId") final Long probeId, @DefaultValue("-1") @QueryParam("moduleId") final Long moduleId) throws Exception {
        return this.probeService.getProbeLastSyncTime(probeId, moduleId);
    }
    
    @GET
    @Path("probes/lastSyncTime")
    @Produces({ "application/allProbesLastSyncTime.v1+json" })
    public HashMap geAllProbesLastSyncTime(@DefaultValue("-1") @QueryParam("moduleId") final Long moduleId) throws Exception {
        return this.probeService.getAllProbesLastSyncTime(moduleId);
    }
}
