package com.me.mdm.files.serve;

import java.util.concurrent.TimeUnit;
import com.me.mdm.core.auth.APIKey;
import java.io.OutputStream;
import java.util.Map;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import com.me.mdm.files.FileFacade;
import org.apache.commons.lang3.time.DurationFormatUtils;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.List;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.factory.FileDownloadRegulatorAPI;
import com.adventnet.iam.security.SecurityUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.AsyncContext;
import org.json.JSONObject;
import com.me.mdm.agent.servlets.DeviceRequestServlet;

class AsyncContextAuthorizer extends DeviceRequestServlet
{
    Long loginId;
    String module;
    Long customerID;
    Long requestedAt;
    String deviceUDID;
    String fileIDhint;
    String deviceToken;
    JSONObject downloadDetails;
    private AsyncContext asyncContext;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HashMap requestMap;
    private Boolean isServeFromAppServer;
    private boolean authorized;
    private boolean authenticated;
    private String internalRedirectPath;
    private static final long TIME_OUT;
    
    AsyncContextAuthorizer initalize() throws Exception {
        this.authorized = false;
        this.authenticated = false;
        this.asyncContext.setTimeout(AsyncContextAuthorizer.TIME_OUT);
        this.request = (HttpServletRequest)this.asyncContext.getRequest();
        this.response = (HttpServletResponse)this.asyncContext.getResponse();
        this.requestMap = this.getParameterValueMap(this.request);
        this.deviceUDID = this.request.getParameter("udid");
        this.deviceToken = this.request.getParameter("encapiKey");
        final String authToken = this.request.getHeader("authorization");
        if ((SyMUtil.isStringEmpty(this.deviceUDID) || SyMUtil.isStringEmpty(this.deviceToken)) && this.loginId == null) {
            if (authToken == null || (this.loginId = FileDownloadRegulator.getLoginIDForValidAuthToken(authToken, this.request.getHeader("host"))) == null) {
                this.complete();
                return null;
            }
            this.markAuthenticated();
        }
        else if (SyMUtil.isStringEmpty(this.deviceUDID) && SyMUtil.isStringEmpty(this.deviceToken) && this.loginId != null) {
            this.markAuthenticated();
        }
        final FileDownloadRegulatorAPI fileDownloadRegulatorAPI = FileDownloadRegulator.getFileDownloadRegulatorModuleImpl(SecurityUtil.getRequestPath(this.request));
        this.validateFileExtensionBasedOnModule(fileDownloadRegulatorAPI);
        this.module = fileDownloadRegulatorAPI.getModule(SecurityUtil.getRequestPath(this.request));
        this.customerID = fileDownloadRegulatorAPI.getCustomerID(SecurityUtil.getRequestPath(this.request));
        this.fileIDhint = fileDownloadRegulatorAPI.getFileIDhint(SecurityUtil.getRequestPath(this.request));
        this.downloadDetails = fileDownloadRegulatorAPI.getDownloadRequestDetails(this.request);
        this.internalRedirectPath = fileDownloadRegulatorAPI.getInternalRedirectPath(this.request);
        this.isServeFromAppServer = fileDownloadRegulatorAPI.isServeFromAppServer(this.request);
        return this;
    }
    
    AsyncContextAuthorizer(final Long loginId, final AsyncContext asyncContext, final Long requestedAt) {
        this.loginId = loginId;
        this.requestedAt = requestedAt;
        this.asyncContext = asyncContext;
    }
    
    void complete() {
        if (!this.authenticated) {
            this.complete(403);
        }
        else if (!this.authorized) {
            this.complete(401);
        }
        else if (!this.isServeFromAppServer) {
            this.complete(200);
        }
    }
    
    void complete(final int httpStatus) {
        this.response.setStatus(httpStatus);
        this.asyncContext.complete();
    }
    
    private void markAuthenticated() {
        this.authenticated = true;
    }
    
    void authenticate(final String deviceUDID, final String deviceToken) {
        if (deviceUDID.equals(this.deviceUDID) && deviceToken.equals(this.deviceToken)) {
            this.markAuthenticated();
        }
    }
    
    private void markAuthorized(final String mimeType) {
        this.authorized = true;
        SecurityUtil.setCurrentRequest(this.request);
        if (!MDMUtil.isStringEmpty(mimeType)) {
            this.response.setHeader("Content-Type", mimeType);
            this.response.setHeader("Content-Disposition", "inline");
        }
        if (!this.isServeFromAppServer) {
            if ("apache".equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
                this.response.setHeader("X-Sendfile", this.internalRedirectPath);
            }
            else if ("nginx".equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
                this.response.setHeader("X-Accel-Redirect", this.internalRedirectPath);
            }
        }
        else {
            this.serveFileAsynchronously();
        }
    }
    
    void authorize(final List<Long> approvedLoginIds, final String mimeType) {
        if (this.loginId != null && approvedLoginIds != null && approvedLoginIds.contains(this.loginId)) {
            this.markAuthorized(mimeType);
        }
    }
    
    void authorize(final String deviceUDID, final String fileIDhint) {
        if (deviceUDID.equals(this.deviceUDID) && fileIDhint.equals(this.fileIDhint)) {
            this.markAuthorized(null);
        }
    }
    
    boolean isAuthenticated() {
        return this.authenticated;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.deviceUDID == null) {
            sb.append("LOGIN_ID");
            sb.append(" ");
            sb.append(this.loginId);
        }
        else {
            sb.append("UDID");
            sb.append(" ");
            sb.append(this.deviceUDID);
        }
        sb.append(" FILE_ID ");
        sb.append(this.fileIDhint);
        sb.append(" authenticated ");
        sb.append(this.authenticated);
        sb.append(" authorized ");
        sb.append(this.authorized);
        sb.append(" requestAt ");
        sb.append(DateTimeUtil.longdateToString((long)this.requestedAt, "EEE, d MMM yyyy hh:mm:ss.SSS zzz"));
        sb.append(" duration ");
        sb.append(DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - this.requestedAt));
        return sb.toString();
    }
    
    private void serveFileAsynchronously() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/me/mdm/files/serve/AsyncContextAuthorizer.asyncContext:Ljavax/servlet/AsyncContext;
        //     4: aload_0         /* this */
        //     5: invokedynamic   BootstrapMethod #0, run:(Lcom/me/mdm/files/serve/AsyncContextAuthorizer;)Ljava/lang/Runnable;
        //    10: invokeinterface javax/servlet/AsyncContext.start:(Ljava/lang/Runnable;)V
        //    15: return         
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:252)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:185)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.nameVariables(AstMethodBodyBuilder.java:1482)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.populateVariables(AstMethodBodyBuilder.java:1411)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void validateFileExtensionBasedOnModule(final FileDownloadRegulatorAPI fileDownloadRegulatorApi) {
        final String extension = FileFacade.getInstance().getFileExtension(SecurityUtil.getRequestPath(this.request));
        if (!fileDownloadRegulatorApi.isValidFileExtension(extension)) {
            SyMLogger.log("FileServletLog", Level.SEVERE, "Trying to access unauthorised files {0}", (Object[])new String[] { this.request.getRequestURI() });
            this.complete(403);
        }
    }
    
    static {
        TIME_OUT = TimeUnit.SECONDS.toMillis(30L);
    }
}
