package com.me.ems.framework.common.api.response;

import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.common.api.utils.APIErrorUtil;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

public class APIResponse
{
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_MSG = "errorMsg";
    private static final String REFERENCE_URI = "referenceURI";
    private static Logger logger;
    
    private static Response errorResponse(final Response.Status httpStatus, final String productErrorCode, final String productErrorMsg, final String... errorMsgArgs) {
        final Map<String, Object> errorResponse = errorResponseConstruction(productErrorCode, productErrorMsg, errorMsgArgs);
        return Response.status(httpStatus).entity((Object)errorResponse).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    private static Response errorResponse(final Response.Status httpStatus, final Long productErrorCode, final String productErrorMsg, final String... errorMsgArgs) {
        return errorResponse(httpStatus, String.valueOf(productErrorCode), productErrorMsg, errorMsgArgs);
    }
    
    public static Response errorResponse(final String productErrorCode) {
        final Response.Status status = getErrorResponseStatus(productErrorCode);
        final String i18NKey = getI18NErrorMessage(productErrorCode);
        return errorResponse(status, productErrorCode, i18NKey, new String[0]);
    }
    
    public static Response errorResponse(final String productErrorCode, String message, final String... errorMsgArgs) {
        if (message == null || message.isEmpty()) {
            message = getI18NErrorMessage(productErrorCode);
        }
        final Map<String, Object> errorResponse = errorResponseConstruction(productErrorCode, message, errorMsgArgs);
        final Response.Status status = getErrorResponseStatus(productErrorCode);
        return Response.status(status).entity((Object)errorResponse).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    public static Response.Status getErrorResponseStatus(final String productErrorCode) {
        return Response.Status.fromStatusCode((int)APIErrorUtil.httpStatusMap.get(productErrorCode));
    }
    
    public static String getI18NErrorMessage(final String productErrorCode) {
        return APIErrorUtil.errorCodeMap.get(productErrorCode);
    }
    
    public static String getReferenceURI(final String productErrorCode) {
        return APIErrorUtil.referenceURIMap.get(productErrorCode);
    }
    
    public static Response missingParamErrorResponse(final String missingParam) {
        return errorResponse("GENERIC0003", null, missingParam);
    }
    
    public static Response missingHeaderErrorResponse(final String missingHeader) {
        return errorResponse("GENERIC0004", null, missingHeader);
    }
    
    public static Response errorResponse(final APIException apiException) {
        final String referenceURI = apiException.getReferenceURI();
        if (referenceURI != null && !referenceURI.isEmpty()) {
            return errorResponseWithReferenceURI(apiException.getHttpStatus(), apiException.getErrorCode(), apiException.getErrorMsg(), referenceURI, new String[0]);
        }
        return errorResponse(apiException.getHttpStatus(), apiException.getErrorCode(), apiException.getErrorMsg(), new String[0]);
    }
    
    private static Response errorResponseWithReferenceURI(final Response.Status httpStatus, final String productErrorCode, final String productErrorMsg, final String referenceURI, final String... errorMsgArgs) {
        final Map<String, Object> errorResponse = errorResponseConstruction(productErrorCode, productErrorMsg, errorMsgArgs);
        errorResponse.put("referenceURI", referenceURI);
        return Response.status(httpStatus).entity((Object)errorResponse).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    public static Response errorResponse(final Response errorResponse) {
        final int status = errorResponse.getStatus();
        final Response.Status errorStatus = Response.Status.fromStatusCode(status);
        return errorResponse(errorStatus, new Long(status), errorStatus.getReasonPhrase(), new String[0]);
    }
    
    public static Map<String, Object> errorResponseConstruction(final String productErrorCode, final String... errorMsgArgs) {
        final String i18NKey = getI18NErrorMessage(productErrorCode);
        return errorResponseConstruction(productErrorCode, i18NKey, errorMsgArgs);
    }
    
    private static Map<String, Object> errorResponseConstruction(final String productErrorCode, String productErrorMsg, final String... errorMsgArgs) {
        final HashMap<String, Object> errorResponse = new HashMap<String, Object>();
        errorResponse.put("errorCode", productErrorCode);
        try {
            productErrorMsg = I18N.getMsg(productErrorMsg, (Object[])errorMsgArgs);
        }
        catch (final Exception ex) {
            APIResponse.logger.log(Level.SEVERE, "Exception while translating the Key: " + productErrorMsg + " with args: " + errorMsgArgs, ex);
        }
        errorResponse.put("errorMsg", productErrorMsg);
        return errorResponse;
    }
    
    static {
        APIResponse.logger = Logger.getLogger(APIResponse.class.getName());
    }
    
    public enum Status implements Response.StatusType
    {
        MULTI_STATUS(207, "Multi Status");
        
        private final int code;
        private final String reason;
        private final Response.Status.Family family;
        
        private Status(final int statusCode, final String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            this.family = Response.Status.Family.familyOf(statusCode);
        }
        
        public Response.Status.Family getFamily() {
            return this.family;
        }
        
        public int getStatusCode() {
            return this.code;
        }
        
        public String getReasonPhrase() {
            return this.toString();
        }
        
        @Override
        public String toString() {
            return this.reason;
        }
    }
}
