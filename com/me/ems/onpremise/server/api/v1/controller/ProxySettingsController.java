package com.me.ems.onpremise.server.api.v1.controller;

import javax.ws.rs.QueryParam;
import javax.ws.rs.PUT;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import com.me.ems.onpremise.server.api.v1.service.ProxySettingsService;
import javax.ws.rs.Path;

@Path("proxy")
public class ProxySettingsController
{
    private ProxySettingsService proxySettings;
    
    public ProxySettingsController() {
        this.proxySettings = new ProxySettingsService();
    }
    
    @GET
    @Path("settings")
    @Produces({ "application/proxySettingsDetails.v1+json" })
    public Map loadProxy() throws APIException {
        return this.proxySettings.loadProxy();
    }
    
    @POST
    @Path("validate")
    @Produces({ "application/proxyValidationStatus.v1+json" })
    @Consumes({ "application/proxyDetails.v1+json" })
    public Response validateProxy(final Map proxyDetails) throws APIException {
        return this.proxySettings.validateProxy(proxyDetails);
    }
    
    @PUT
    @Path("settings")
    @Produces({ "application/proxySettingStatus.v1+json" })
    @Consumes({ "application/proxySettingsDetails.v1+json" })
    public Response setProxy(final Map proxyDetails, @Context final ContainerRequestContext requestContext) throws APIException {
        final User user = (User)requestContext.getSecurityContext().getUserPrincipal();
        final JSONObject logData = new JSONObject();
        logData.put((Object)"DOMAIN_NAME", (Object)user.getDomainName());
        logData.put((Object)"AUTH_TYPE", (Object)user.getAuthType());
        logData.put((Object)"PROXY_TYPE", proxyDetails.get("proxyType"));
        logData.put((Object)"PROXY_SCRIPT", proxyDetails.get("proxyScript"));
        logData.put((Object)"PROXY_PORT", proxyDetails.get("proxyPort"));
        logData.put((Object)"PROXY_HOST", proxyDetails.get("proxyHost"));
        logData.put((Object)"IS_PASSWORD_MODIFIED", proxyDetails.get("isPasswordModified"));
        logData.put((Object)"USER_NAME", proxyDetails.get("userName"));
        try {
            final Response response = this.proxySettings.addProxyConfig(proxyDetails, user.getName());
            logData.put((Object)"REMARK", (Object)"SUCCESS");
            return response;
        }
        catch (final APIException ex) {
            logData.put((Object)"REMARK", (Object)"FAILURE");
            throw ex;
        }
        finally {
            SecurityOneLineLogger.log("Server", "Save_proxySettings", logData, Level.INFO);
        }
    }
    
    @GET
    @Path("domain")
    @Produces({ "application/proxyDomainStatus.v1+json" })
    public Map getDomainValidationStatus() throws APIException {
        return this.proxySettings.getDomainValidationStatus();
    }
    
    @GET
    @Path("isProxyDefined")
    @Produces({ "application/proxyDomainValues.v1+json" })
    public Map getProxyDefinedStatus() {
        return this.proxySettings.getProxyDefinedStatus();
    }
    
    @GET
    @Path("validateDomains")
    @Produces({ "application/domainValidationStatus.v1+json" })
    public Map validateDomains(@QueryParam("urlType") final String url) {
        return this.proxySettings.validateDomains(url);
    }
}
