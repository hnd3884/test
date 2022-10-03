package com.me.ems.onpremise.summaryserver.probe.probeadministration.api.v1.controller;

import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.response.APIResponse;
import javax.security.auth.login.LoginException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.ems.framework.uac.api.v1.model.AuthUser;
import com.me.ems.onpremise.uac.api.v1.service.OPUserService;
import com.me.ems.onpremise.summaryserver.probe.probeadministration.api.v1.service.ProbeAuthenticationService;
import javax.ws.rs.Path;

@Path("")
public class ProbeAuthenticationController
{
    private ProbeAuthenticationService probeService;
    private OPUserService opUserService;
    
    public ProbeAuthenticationController() {
        this.probeService = new ProbeAuthenticationService();
        this.opUserService = OPUserService.getInstance();
    }
    
    @PUT
    @Path("probe/regenerateProbeAuthKey")
    @Produces({ "application/probeAuthKey.v1+json" })
    @Consumes({ "application/authentication.v1+json" })
    public Object validateAndRegenerateProbeAuthKey(final AuthUser authUser) {
        final String userName = authUser.getUserName();
        String password = authUser.getEncodedPassword();
        final String domainName = authUser.getDomainName();
        final String authType = authUser.getAuthType();
        password = ((password != null && !password.trim().isEmpty()) ? SyMUtil.decodeAsUTF16LE(password) : null);
        try {
            final User userDetails = this.opUserService.validateAndAuthenticateUser(userName, password, domainName, authType);
            if (userDetails != null) {
                return this.probeService.regenerateProbeAuthKey();
            }
            throw new LoginException(I18N.getMsg("ems.rest.userdetails.not.found", new Object[0]));
        }
        catch (final Exception ex) {
            return APIResponse.errorResponse("USER0003");
        }
    }
    
    @PUT
    @Path("probe/updateSummaryServerAuthKey")
    @Consumes({ "application/summaryServerAuthKey.v1+json" })
    @Produces({ "application/updationStatus.v1+json" })
    public HashMap updateSummaryServerAuthKey(final Map ssAuthKeyDetails) throws Exception {
        final String ssAuthKey = ssAuthKeyDetails.get("summaryServerAuthKey");
        if (ssAuthKey == null) {
            throw new APIException("PRBE9500303");
        }
        final HashMap updatedDetails = this.probeService.updateSummaryServerAuthKey(ssAuthKey);
        return updatedDetails;
    }
    
    @GET
    @Path("probe/checkSummaryServerConnectivity")
    @Produces({ "application/summaryServerStatus.v1+json" })
    public HashMap checkSummaryServerConnectivity() throws Exception {
        return this.probeService.checkSummaryServerConnectivity();
    }
    
    @GET
    @Path("probe/getApiKeyDetails")
    @Produces({ "application/apiKeyDetails.v1+json" })
    public HashMap getApiKeyDetails() {
        return this.probeService.getApiKeyDetails();
    }
}
