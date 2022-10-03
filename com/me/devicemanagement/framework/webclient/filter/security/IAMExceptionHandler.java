package com.me.devicemanagement.framework.webclient.filter.security;

import java.util.Arrays;
import javax.ws.rs.core.Response;
import com.me.ems.framework.common.api.utils.APIException;
import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.iam.security.IAMSecurityException;

public class IAMExceptionHandler
{
    public void writeAPIErrorResponse(final IAMSecurityException exception, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            final String url = (String)request.getAttribute("javax.servlet.forward.request_uri");
            final String error = exception.getErrorCode();
            final APIException apiError = this.getAPIException(error, exception);
            final Response.Status status = apiError.getHttpStatus();
            response.setStatus(status.getStatusCode());
            response.setHeader("Content-Type", "application/json");
            final JSONObject jsonObject = new JSONObject(apiError.toJSONObject().toString());
            jsonObject.put("url", (Object)url);
            response.getWriter().write(jsonObject.toString());
        }
        catch (final JSONException e) {
            throw new IOException();
        }
    }
    
    public APIException getAPIException(final String error, final IAMSecurityException exception) {
        APIException apiError = null;
        if (error == null || error.isEmpty()) {
            apiError = new APIException("IAM0007", "Security Exception occurred - " + exception, new String[0]);
            return apiError;
        }
        switch (error) {
            case "UNAUTHORISED":
            case "BLOCK_LISTED_IP":
            case "INVALID_OAUTHTOKEN":
            case "INVALID_TICKET":
            case "OAUTHTOKEN_EXPIRED":
            case "INVALID_OAUTHSCOPE": {
                apiError = new APIException("IAM0002");
                break;
            }
            case "URL_FIXED_THROTTLES_LIMIT_EXCEEDED":
            case "URL_LIVE_THROTTLES_LIMIT_EXCEEDED":
            case "URL_ROLLING_THROTTLES_LIMIT_EXCEEDED":
            case "URL_THROTTLES_LIMIT_EXCEEDED": {
                apiError = new APIException("IAM0019", null, new String[] { exception.getUri() });
                break;
            }
            case "INTERNAL_IP_ACCESS_ONLY":
            case "INVALID_URL":
            case "INVALID_METHOD": {
                apiError = new APIException("IAM0004");
                break;
            }
            case "AUTHENTICATION_FAILED":
            case "NOT_AUTHENTICATED": {
                apiError = new APIException("IAM0001");
                break;
            }
            case "URL_RULE_NOT_CONFIGURED": {
                apiError = new APIException("IAM0027", null, new String[] { exception.getUri() });
                break;
            }
            case "LESS_THAN_MIN_OCCURANCE": {
                apiError = new APIException("IAM0021", null, new String[] { this.getParameterName(exception) });
                break;
            }
            case "ARRAY_SIZE_OUT_OF_RANGE": {
                apiError = new APIException("IAM0022");
                break;
            }
            case "MORE_THAN_MAX_LENGTH": {
                apiError = new APIException("IAM0023", null, new String[] { this.getParameterName(exception) });
                break;
            }
            case "LESS_THAN_MIN_LENGTH": {
                apiError = new APIException("IAM0024", null, new String[] { this.getParameterName(exception) });
                break;
            }
            case "EXTRA_PARAM_FOUND": {
                apiError = new APIException("IAM0028", null, new String[] { this.getParameterName(exception) });
                break;
            }
            case "PATTERN_NOT_MATCHED": {
                apiError = new APIException("IAM0025", null, new String[] { this.getParameterName(exception) });
                break;
            }
            case "INVALID_FILE_EXTENSION": {
                apiError = new APIException("IAM0026", null, new String[] { Arrays.toString(exception.getUploadFileRule().getAllowedExtensions()) });
                break;
            }
            case "BROWSER_COOKIES_DISABLED": {
                apiError = new APIException("IAM0008");
                break;
            }
            case "UPLOAD_RULE_NOT_CONFIGURED": {
                apiError = new APIException("IAM0009", null, new String[] { exception.getUri() });
                break;
            }
            case "INVALID_CSRF_TOKEN": {
                apiError = new APIException("IAM0010");
                break;
            }
            case "MORE_THAN_MAX_OCCURANCE": {
                apiError = new APIException("IAM0011", null, new String[] { this.getParameterName(exception) });
                break;
            }
            case "UNMATCHED_FILE_CONTENT_TYPE": {
                apiError = new APIException("IAM0012", null, new String[] { Arrays.toString(exception.getUploadFileRule().getAllowedContentTypesName()) });
                break;
            }
            case "FILE_SIZE_MORE_THAN_ALLOWED_SIZE": {
                apiError = new APIException("IAM0013", null, new String[] { String.valueOf(exception.getUploadFileRule().getMaxSizeInKB()) });
                break;
            }
            case "SERVICE_NOT_CONFIGURED": {
                apiError = new APIException("IAM0014");
                break;
            }
            case "WRITE_OPERATION_NOT_ALLOWED": {
                apiError = new APIException("IAM0015");
                break;
            }
            case "EMPTY_FILE_NOT_ALLOWED": {
                apiError = new APIException("IAM0016");
                break;
            }
            case "JSON_PARSE_ERROR": {
                apiError = new APIException("IAM0017");
                break;
            }
            case "EXTRA_KEY_FOUND_IN_JSON": {
                apiError = new APIException("IAM0018", null, new String[] { this.getParameterName(exception) });
                break;
            }
            case "MODULE_HEADER_NOT_AVAILABLE": {
                apiError = new APIException("IAM0029");
                break;
            }
            case "VIRUS_DETECTED": {
                apiError = new APIException("IAM0030");
                break;
            }
            case "AGENT_UNAUTHORIZED": {
                apiError = new APIException("IAM0032");
                break;
            }
            default: {
                apiError = new APIException("IAM0020", "Security Exception occurred - " + error, new String[0]);
                break;
            }
        }
        return apiError;
    }
    
    private String getParameterName(final IAMSecurityException exception) {
        String parameterName = exception.getParameterName();
        if (parameterName == null || parameterName.equalsIgnoreCase("zoho-inputstream")) {
            final String embedParam = exception.getEmbedParameterName();
            parameterName = ((embedParam == null) ? "" : embedParam);
        }
        return parameterName;
    }
}
