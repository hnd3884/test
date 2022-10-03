package com.me.mdm.server.apps.android.afw;

import org.json.JSONObject;
import com.google.api.client.googleapis.json.GoogleJsonError;
import java.util.logging.Level;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.auth.oauth2.TokenResponseException;
import java.util.logging.Logger;

public class GoogleAPIErrorHandler
{
    private static Logger logger;
    public static final String TIME_DATE_MISMATCH = "Invalid JWT";
    public static final String PRODUCT_NOT_COMPATIBLE = "product is not compatible";
    public static final String ENTERPRISE_NOT_EXISTS = "No enterprise was found";
    public static final String PROXY_CERTIFICATE_ERROR = "SunCertPathBuilderException";
    public static final String API_ERROR_CODE = "apiErrorCode";
    public static final String INTERNAL_ERROR_CODE = "internalErrorCode";
    public static final String ERROR_MESSAGE = "errorMsg";
    
    public static String getResponseErrorKey(final Exception error) {
        String errorKey = null;
        try {
            if (error instanceof TokenResponseException) {
                final String errorDescription = ((TokenResponseException)error).getDetails().getErrorDescription();
                if (errorDescription.contains("Invalid JWT") && errorDescription.contains("timeframe")) {
                    errorKey = "mdm.appmgmt.afw.syncfailed.invalid_time_date";
                }
                else {
                    errorKey = errorDescription;
                }
            }
            else if (error instanceof GoogleJsonResponseException) {
                final GoogleJsonError googleJsonError = ((GoogleJsonResponseException)error).getDetails();
                final String errorDescription = errorKey = getErrorMessage(googleJsonError.getMessage());
            }
            else if (error.toString().contains("SunCertPathBuilderException")) {
                errorKey = "mdm.appmgmt.afw.syncfailed.proxy_cert_error";
            }
            else {
                errorKey = error.toString();
            }
        }
        catch (final Exception e) {
            GoogleAPIErrorHandler.logger.log(Level.SEVERE, "Exception in getting GOOGLE API ERROR message", e);
        }
        return errorKey;
    }
    
    @Deprecated
    public static String getErrorMessage(final String errorMessage) {
        String errorKey = errorMessage;
        try {
            if (errorMessage.contains("product is not compatible")) {
                errorKey = "mdm.appmgmt.afw.product_not_compatible_with_device";
            }
            else if (errorMessage.contains("No enterprise was found")) {
                errorKey = "mdm.appmgmt.afw.enterprise_not_found";
            }
        }
        catch (final Exception ex) {
            GoogleAPIErrorHandler.logger.log(Level.INFO, "Exception in getting GoogleJsonError message", ex);
        }
        return errorKey;
    }
    
    public static JSONObject getErrorResponseJSON(final GoogleJsonResponseException ex) {
        final JSONObject errorResponseJSON = new JSONObject();
        try {
            final String googleErrorMessage = ex.getDetails().getMessage();
            String apiErrorCode = null;
            final int internalErrorCode = -1;
            String errorMessage = googleErrorMessage;
            if (googleErrorMessage.contains("No enterprise was found")) {
                apiErrorCode = "APP0026";
                errorMessage = "mdm.appmgmt.afw.enterprise_not_found";
            }
            else if (googleErrorMessage.contains("product is not compatible")) {
                errorMessage = "mdm.appmgmt.afw.product_not_compatible_with_device";
            }
            errorResponseJSON.put("apiErrorCode", (Object)apiErrorCode);
            errorResponseJSON.put("internalErrorCode", internalErrorCode);
            errorResponseJSON.put("errorMsg", (Object)errorMessage);
        }
        catch (final Exception e) {
            GoogleAPIErrorHandler.logger.log(Level.INFO, "Exception while getting error response", (Throwable)ex);
        }
        GoogleAPIErrorHandler.logger.log(Level.INFO, "getErrorResponseJSON : {0}", errorResponseJSON);
        return errorResponseJSON;
    }
    
    static {
        GoogleAPIErrorHandler.logger = Logger.getLogger("MDMLogger");
    }
}
