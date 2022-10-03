package com.me.ems.framework.common.api.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import com.me.ems.framework.common.api.response.APIResponse;
import javax.ws.rs.core.Response;

public class APIException extends Exception
{
    private Response.Status httpStatus;
    private String errorCode;
    private String errorMsg;
    private String referenceURI;
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_MSG = "errorMsg";
    private static final String REFERENCE_URI = "referenceURI";
    
    public APIException(final Throwable cause) {
        if (cause instanceof APIException) {
            final APIException thrownException = (APIException)cause;
            this.httpStatus = thrownException.httpStatus;
            this.errorCode = thrownException.errorCode;
            this.errorMsg = thrownException.errorMsg;
            this.referenceURI = thrownException.referenceURI;
        }
    }
    
    public Response.Status getHttpStatus() {
        return this.httpStatus;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public String getErrorMsg() {
        return this.errorMsg;
    }
    
    public APIException(final Throwable cause, final String errorCode, final String errorMsg, final String... msgArgs) {
        this(errorCode, errorMsg, msgArgs);
    }
    
    public APIException(final String errorCode, final String errorMsg, final String... msgArgs) {
        this(APIResponse.getErrorResponseStatus(errorCode), errorCode, errorMsg, msgArgs);
    }
    
    public APIException(final Response.Status status, final String errorCode, String errorMsg) {
        if (errorMsg == null || errorMsg.isEmpty()) {
            errorMsg = APIResponse.getI18NErrorMessage(errorCode);
        }
        this.httpStatus = status;
        this.errorCode = errorCode;
        try {
            this.errorMsg = I18N.getMsg(errorMsg, new Object[0]);
        }
        catch (final Exception ex) {
            this.errorMsg = errorMsg;
        }
    }
    
    public APIException(final Response.Status status, final String errorCode, String errorMsg, final String... msgArgs) {
        if (errorMsg == null || errorMsg.isEmpty()) {
            errorMsg = APIResponse.getI18NErrorMessage(errorCode);
        }
        this.httpStatus = status;
        this.errorCode = errorCode;
        final String referenceUri = APIResponse.getReferenceURI(errorCode);
        if (referenceUri != null && !referenceUri.isEmpty()) {
            this.referenceURI = referenceUri;
        }
        try {
            this.errorMsg = I18N.getMsg(errorMsg, (Object[])msgArgs);
        }
        catch (final Exception ex) {
            this.errorMsg = errorMsg;
        }
    }
    
    public static APIException noDataAvailable() {
        return new APIException("GENERIC0001");
    }
    
    public APIException(final String errorCode) {
        this(APIResponse.getErrorResponseStatus(errorCode), errorCode, APIResponse.getI18NErrorMessage(errorCode));
    }
    
    public JSONObject toJSONObject() {
        try {
            final JSONObject errorJSON = new JSONObject();
            errorJSON.put("errorCode", (Object)this.errorCode);
            errorJSON.put("errorMsg", (Object)this.errorMsg);
            if (this.referenceURI != null && !this.referenceURI.isEmpty()) {
                errorJSON.put("referenceURI", (Object)this.errorMsg);
            }
            return errorJSON;
        }
        catch (final Exception e) {
            Logger.getLogger(APIException.class.getName()).log(Level.SEVERE, "Error in APIError", e);
            return null;
        }
    }
    
    public static Response.Status getErrorResponseStatus(final String productErrorCode) {
        return Response.Status.fromStatusCode((int)APIErrorUtil.httpStatusMap.get(productErrorCode));
    }
    
    public static String getI18NErrorMessage(final String productErrorCode) {
        return APIErrorUtil.errorCodeMap.get(productErrorCode);
    }
    
    public String getReferenceURI() {
        return this.referenceURI;
    }
    
    public APIException(final String errorCode, final boolean isReferenceURIAvailable, final String referenceURI) {
        this.httpStatus = APIResponse.getErrorResponseStatus(errorCode);
        this.errorCode = errorCode;
        this.errorMsg = APIResponse.getI18NErrorMessage(errorCode);
        if (isReferenceURIAvailable) {
            this.referenceURI = referenceURI;
        }
    }
}
