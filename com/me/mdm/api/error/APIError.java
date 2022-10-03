package com.me.mdm.api.error;

import java.util.logging.Level;
import java.util.Locale;
import org.json.JSONObject;
import java.util.logging.Logger;

public class APIError
{
    Logger logger;
    private String errorString;
    private String localizedErrorString;
    private String errorCode;
    private String i18nKey;
    private JSONObject customErrorParams;
    private int httpStatus;
    
    public int getHttpStatus() {
        return this.httpStatus;
    }
    
    public void setHttpStatus(final int httpStatus) {
        this.httpStatus = httpStatus;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getI18nKey() {
        return this.i18nKey;
    }
    
    public void setI18nKey(final String i18nKey) {
        this.i18nKey = i18nKey;
    }
    
    public void setErrorMsg(final Locale locale) throws Exception {
        this.errorString = APIErrorUtil.getInstance().getEnglishErrorString(this.i18nKey, new Object[0]);
        this.localizedErrorString = APIErrorUtil.getInstance().getLocalizedErrorString(this.i18nKey, locale, new Object[0]);
    }
    
    public APIError() {
        this.logger = Logger.getLogger(APIError.class.getName());
    }
    
    public APIError(final String errorCode) {
        this.logger = Logger.getLogger(APIError.class.getName());
        this.errorCode = errorCode;
        this.i18nKey = APIErrorUtil.getInstance().getErrorI18nKey(errorCode);
        this.errorString = APIErrorUtil.getInstance().getEnglishErrorString(this.i18nKey, new Object[0]);
        this.localizedErrorString = APIErrorUtil.getInstance().getLocalizedErrorString(this.i18nKey, null, new Object[0]);
        this.httpStatus = APIErrorUtil.getInstance().getHttpStatusCode(errorCode);
    }
    
    public APIError(final String errorCode, final Object... args) {
        this.logger = Logger.getLogger(APIError.class.getName());
        this.errorCode = errorCode;
        this.i18nKey = APIErrorUtil.getInstance().getErrorI18nKey(errorCode);
        this.errorString = APIErrorUtil.getInstance().getEnglishErrorString(this.i18nKey, args);
        this.localizedErrorString = APIErrorUtil.getInstance().getLocalizedErrorString(this.i18nKey, null, args);
        this.httpStatus = APIErrorUtil.getInstance().getHttpStatusCode(errorCode);
    }
    
    public APIError(final JSONObject customErrorParams, final String errorCode, final Object... args) {
        this(errorCode, args);
        this.customErrorParams = customErrorParams;
    }
    
    public JSONObject toJSONObject() {
        try {
            final JSONObject errorJSON = new JSONObject();
            errorJSON.put("error_code", (Object)this.errorCode);
            errorJSON.put("error_description", (Object)this.errorString);
            errorJSON.put("localized_error_description", (Object)this.localizedErrorString);
            if (this.customErrorParams != null && this.customErrorParams.length() > 0) {
                errorJSON.put("custom_error_data", (Object)this.customErrorParams);
            }
            return errorJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error in APIError", e);
            return null;
        }
    }
}
