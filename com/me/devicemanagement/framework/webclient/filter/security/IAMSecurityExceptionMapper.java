package com.me.devicemanagement.framework.webclient.filter.security;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.api.util.APIRequest;
import com.me.devicemanagement.framework.webclient.api.util.APIUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.iam.security.IAMSecurityException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class IAMSecurityExceptionMapper extends HttpServlet
{
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final IAMSecurityException securityException = (IAMSecurityException)request.getAttribute(IAMSecurityException.class.getName());
        final String errorCode = (securityException != null) ? securityException.getErrorCode() : null;
        if (securityException != null && errorCode != null) {
            final String reqURI = (String)request.getAttribute("javax.servlet.forward.request_uri");
            if (reqURI != null && reqURI.startsWith("/api") && (errorCode.equals("AUTHENTICATION_FAILED") || errorCode.equals("NOT_AUTHENTICATED") || errorCode.equals("UNAUTHORISED") || errorCode.equals("WRITE_OPERATION_NOT_ALLOWED"))) {
                this.writeLegacyAPIErrorResponse(securityException, request, response);
            }
            else {
                final IAMExceptionHandler exceptionHandler = new IAMExceptionHandler();
                exceptionHandler.writeAPIErrorResponse(securityException, request, response);
            }
        }
    }
    
    private void writeLegacyAPIErrorResponse(final IAMSecurityException securityException, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final String reqURI = (String)request.getAttribute("javax.servlet.forward.request_uri");
            final String errorCode = securityException.getErrorCode();
            final APIUtil apiUtil = APIUtil.getInstance();
            final String[] path = reqURI.split("/");
            final String version = path[2];
            final String entity = path[4];
            if (errorCode.equals("NOT_AUTHENTICATED") || errorCode.equals("AUTHENTICATION_FAILED")) {
                apiUtil.setErrorDetails("10002", "Invalid or expired token");
            }
            else if (errorCode.equals("UNAUTHORISED") || errorCode.equals("WRITE_OPERATION_NOT_ALLOWED")) {
                apiUtil.setErrorDetails("1010", "User is not authorized to access this API");
            }
            apiUtil.setOutput(apiUtil.constructMessageResponse(null, null, null, entity, version));
            response.setStatus(200);
            apiUtil.writeOutputResponse(null, apiUtil.getOutput(), response);
        }
        catch (final Exception e) {
            final Logger logger = Logger.getLogger("DCAPILogger");
            logger.log(Level.SEVERE, "Error while writing legacy API response", e);
        }
    }
}
