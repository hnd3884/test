package com.me.mdm.api.error;

import java.util.ResourceBundle;
import java.text.MessageFormat;
import com.adventnet.i18n.I18N;
import java.util.Locale;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.core.xmlparser.XmlBeanUtil;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

public class APIErrorUtil
{
    Logger logger;
    private HashMap<String, String> errorCodeMap;
    private HashMap<String, Integer> httpStatusMap;
    private static APIErrorUtil instance;
    
    private APIErrorUtil() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.errorCodeMap = null;
        this.httpStatusMap = null;
        final File file = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDM" + File.separator + "api" + File.separator + "api-error-constants.xml");
        this.parseAndAddErrorsFromXML(file);
    }
    
    public void parseAndAddErrorsFromXML(final File errorConstantsFile) {
        try {
            final String fileContent = FileUtils.readFileToString(errorConstantsFile, "UTF-8");
            final JSONObject xmlBeanUtilJSON = new JSONObject();
            APIErrorCodes apiErrorConstant = new APIErrorCodes();
            xmlBeanUtilJSON.put("BEAN_OBJECT", (Object)apiErrorConstant);
            final XmlBeanUtil<APIErrorCodes> xmlBeanUtil = new XmlBeanUtil<APIErrorCodes>(xmlBeanUtilJSON);
            apiErrorConstant = xmlBeanUtil.xmlStringToBean(fileContent);
            if (this.errorCodeMap == null) {
                this.errorCodeMap = new HashMap<String, String>();
            }
            if (this.httpStatusMap == null) {
                this.httpStatusMap = new HashMap<String, Integer>();
            }
            for (final APIErrorCodes.ErrorCode error : apiErrorConstant.getErrorCodes()) {
                this.errorCodeMap.put(error.getName(), error.getI18nkey());
                this.httpStatusMap.put(error.getName(), error.getHttpstatus());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while parsing XML", e);
        }
    }
    
    public static APIErrorUtil getInstance() {
        if (APIErrorUtil.instance == null) {
            APIErrorUtil.instance = new APIErrorUtil();
        }
        return APIErrorUtil.instance;
    }
    
    public String getEnglishErrorString(final String errorMessageKey, final Object... args) {
        try {
            final Locale locale = new Locale("en", "US");
            final ResourceBundle bundle = I18N.getResourceBundleFromCache("ApplicationResources", locale);
            final String val = bundle.getString(errorMessageKey);
            return MessageFormat.format(val, args);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while Retrieving I18N message", e);
            return errorMessageKey;
        }
        finally {
            I18N.resetRequestLocale();
        }
    }
    
    public String getLocalizedErrorString(final String errorMessageKey, final Locale locale, final Object... args) {
        try {
            if (locale != null) {
                final ResourceBundle bundle = I18N.getResourceBundleFromCache("ApplicationResources", locale);
                final String val = bundle.getString(errorMessageKey);
                return MessageFormat.format(val, args);
            }
            return I18N.getMsg(errorMessageKey, args);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while Retrieving I18N message", e);
            return errorMessageKey;
        }
        finally {
            I18N.resetRequestLocale();
        }
    }
    
    public String getErrorI18nKey(final String errorCode) {
        return this.errorCodeMap.get(errorCode);
    }
    
    public int getHttpStatusCode(final String errorCode) {
        return this.httpStatusMap.get(errorCode);
    }
    
    static {
        APIErrorUtil.instance = null;
    }
}
