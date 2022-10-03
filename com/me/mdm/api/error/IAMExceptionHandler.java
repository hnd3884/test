package com.me.mdm.api.error;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.easmanagement.EASMgmt;
import com.me.idps.core.IDPSlogger;
import com.me.mdm.server.doc.DocMgmt;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Logger;

public class IAMExceptionHandler
{
    protected static final Logger LOGGER;
    public static final String URL_CONFIGURATION_FAIL_AFTER_REDIRECTION = "URL_CONFIGURATION_FAIL_AFTER_REDIRECTION";
    
    private void logRequest(final IAMSecurityException exception, final String error, final String errorCode) {
        final String uri = exception.getUri();
        if (!SyMUtil.isStringEmpty(uri)) {
            Logger logger = null;
            if (uri.contains("docs")) {
                logger = DocMgmt.logger;
            }
            else if (uri.contains("directory")) {
                logger = IDPSlogger.ERR;
            }
            else if (uri.contains("cea")) {
                logger = EASMgmt.logger;
            }
            if (logger != null) {
                logger.log(Level.INFO, "URI : {0}", new String[] { exception.getUri() });
                logger.log(Level.INFO, "IAM Exception : {0}", new String[] { error });
                logger.log(Level.INFO, "API Error Code : {0}", new String[] { errorCode });
                logger.log(Level.INFO, "Cause : paramName:{0},paramvalue:{1}", new String[] { exception.getParameterName(), exception.getParameterValue() });
            }
        }
    }
    
    public void writeAPIErrorResponse(final IAMSecurityException exception, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Logger logger = Logger.getLogger("MDMErrorPageLogger");
        final String error = exception.getErrorCode();
        final String uri = request.getParameter("uri");
        APIError apiError;
        if (error.equals("URL_CONFIGURATION_FAIL_AFTER_REDIRECTION")) {
            apiError = new APIError("COM0016");
        }
        else if (error.equalsIgnoreCase("UNAUTHORISED") || error.equalsIgnoreCase("BLOCK_LISTED_IP") || error.equalsIgnoreCase("INVALID_OAUTHTOKEN") || error.equalsIgnoreCase("INVALID_TICKET") || error.equalsIgnoreCase("OAUTHTOKEN_EXPIRED") || error.equalsIgnoreCase("INVALID_OAUTHSCOPE")) {
            apiError = new APIError("COM0013");
        }
        else if ("NOT_AUTHENTICATED".equalsIgnoreCase(error)) {
            apiError = new APIError("COM0028");
        }
        else if ("URL_RULE_NOT_CONFIGURED".equalsIgnoreCase(error) || error.equalsIgnoreCase("INTERNAL_IP_ACCESS_ONLY") || error.equalsIgnoreCase("INVALID_URL") || error.equalsIgnoreCase("INVALID_METHOD")) {
            apiError = new APIError("COM0001");
        }
        else if ("LESS_THAN_MIN_OCCURANCE".equalsIgnoreCase(error) || "ARRAY_SIZE_OUT_OF_RANGE".equalsIgnoreCase(error) || "MORE_THAN_MAX_LENGTH".equalsIgnoreCase(error) || "LESS_THAN_MIN_LENGTH".equalsIgnoreCase(error)) {
            apiError = new APIError("COM0014");
        }
        else if ("EXTRA_PARAM_FOUND".equalsIgnoreCase(error)) {
            apiError = new APIError("COM0024");
        }
        else if ("INVALID_METHOD".equalsIgnoreCase(error)) {
            apiError = new APIError("COM0001");
        }
        else if ("PATTERN_NOT_MATCHED".equalsIgnoreCase(error)) {
            apiError = new APIError("COM0005", new Object[] { (exception.getEmbedParameterName() != null) ? exception.getEmbedParameterName() : exception.getParameterName() });
        }
        else {
            apiError = new APIError("COM0015", new Object[] { "Security Exception occurred - " + error });
        }
        logger.log(Level.INFO, "IAM Exception : {0}", error);
        logger.log(Level.INFO, "API Error Code : {0}", apiError.getErrorCode());
        this.logRequest(exception, error, apiError.getErrorCode());
        response.setStatus(apiError.getHttpStatus(), uri);
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(apiError.toJSONObject().toString());
    }
    
    static {
        LOGGER = Logger.getLogger(IAMExceptionHandler.class.getName());
    }
}
