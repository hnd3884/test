package com.me.ems.onpremise.support.api.v1.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import javax.ws.rs.QueryParam;
import com.me.ems.onpremise.support.factory.SupportServiceFactoryProvider;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.onpremise.support.factory.SupportFileService;
import javax.ws.rs.Path;

@Path("supportFile")
public class SupportFileController
{
    private SupportFileService supportFileService;
    @Context
    private SecurityContext securityContext;
    @HeaderParam("X-Customer")
    private String customerIdStr;
    
    public SupportFileController() {
        this.supportFileService = SupportServiceFactoryProvider.getSupportFileService();
    }
    
    @GET
    @Produces({ "application/supportFileDetails.v1+json" })
    public Map getSupportFileDetails(@QueryParam("supportAction") final String supportAction, @QueryParam("disableServerCheck") final String disableServerCheck) throws APIException {
        return this.supportFileService.getSupportFileDetails(supportAction, disableServerCheck);
    }
    
    @GET
    @Path("processStatus")
    @Produces({ "application/processStatusDetails.v1+json" })
    public Map getProcessStatus() throws APIException {
        return this.supportFileService.getProcessStatus();
    }
    
    @GET
    @Path("download")
    @Produces({ "application/supportFile.v1+json" })
    public Response downloadSupportFile() throws APIException {
        return this.supportFileService.downloadSupportFile();
    }
    
    @POST
    @Consumes({ "application/supportFileDetails.v1+json" })
    @Produces({ "application/supportFileResponse.v1+json" })
    public Map supportFileCreation(@Context final HttpServletRequest request, final Map supportFileDetails) throws APIException {
        supportFileDetails.put("customerId", this.supportFileService.validateCustomer(this.customerIdStr, supportFileDetails));
        supportFileDetails.put("userAgent", request.getHeader("User-Agent"));
        final User user = (User)this.securityContext.getUserPrincipal();
        supportFileDetails.put("userID", user.getUserID());
        supportFileDetails.put("loginID", user.getLoginID());
        supportFileDetails.put("isAdminUser", this.securityContext.isUserInRole("Common_Write"));
        this.supportFileService.validateSupportFileData(supportFileDetails);
        final JSONObject logData = new JSONObject();
        logData.put((Object)"DOMAIN_NAME", (Object)user.getDomainName());
        logData.put((Object)"AUTH_TYPE", (Object)user.getAuthType());
        logData.put((Object)"LOGIN_ID", (Object)user.getLoginID());
        logData.put((Object)"IS_ADMIN_USER", (Object)this.securityContext.isUserInRole("Common_Write"));
        logData.put((Object)"AGENT_LOG_UPLOAD", supportFileDetails.get("agentLogUpload"));
        logData.put((Object)"DS_LOG_UPLOAD", supportFileDetails.get("dsLogUpload"));
        logData.put((Object)"SERVER_LOG_UPLOAD", supportFileDetails.get("serverLogUpload"));
        logData.put((Object)"USER_MESSAGE", supportFileDetails.get("userMessage"));
        try {
            final Map map = this.supportFileService.supportFileCreation(supportFileDetails);
            logData.put((Object)"REMARK", (Object)"SUCCESS");
            return map;
        }
        finally {
            SecurityOneLineLogger.log("Server", "Create_support_file", logData, Level.INFO);
        }
    }
    
    @POST
    @Path("dbLock")
    @Consumes({ "application/supportFileDetails.v1+json" })
    @Produces({ "application/supportFileResponse.v1+json" })
    public Map supportFileCreationForDBLock(@Context final HttpServletRequest request, final Map supportFileDetails) throws APIException {
        supportFileDetails.put("customerId", this.supportFileService.validateCustomer(this.customerIdStr, supportFileDetails));
        supportFileDetails.put("userAgent", request.getHeader("User-Agent"));
        return this.supportFileService.supportFileCreationForDBLock(supportFileDetails);
    }
    
    @DELETE
    public Response cancelSupportFileCreation() throws APIException {
        this.supportFileService.cancelSupportFileCreation();
        return Response.ok().build();
    }
}
