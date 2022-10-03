package com.zoho.security.eventfwimpl;

import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.iam.security.InfoFields;
import com.adventnet.iam.security.SecurityFilterProperties;
import java.util.List;
import com.zoho.security.eventfw.config.EventFWConstants;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityUtil;
import com.zoho.security.eventfw.CalleeInfo;
import java.util.Map;
import java.util.logging.Logger;
import com.zoho.security.eventfw.logImpl.LogImplProvider;

public class ZSecAccessLogsCustomFieldsImplProvider extends LogImplProvider
{
    public static final String LOG_CUSTOM_FIELDS = "_custom_fields";
    public static final Logger LOGGER;
    
    public void doLog(final Map<String, Object> eventObject, final CalleeInfo calleeInfo) {
        super.debug((Map)eventObject, calleeInfo);
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        if (request == null) {
            ZSecAccessLogsCustomFieldsImplProvider.LOGGER.log(Level.WARNING, "Unable to Push RequestInfo - Current Request is Null");
            return;
        }
        Map<Object, Object> cf_map = null;
        final boolean isLimitFields = "com.adventnet.iam.security.SecurityFilter".equalsIgnoreCase(calleeInfo.getMonitoringClassName());
        final Object cf_obj = request.getAttribute("_custom_fields");
        if (cf_obj == null) {
            cf_map = new HashMap<Object, Object>();
            request.setAttribute("_custom_fields", (Object)cf_map);
        }
        else {
            cf_map = (Map)cf_obj;
        }
        if (eventObject.containsKey(EventFWConstants.KEY.DATA.name())) {
            final Map<String, Object> map = eventObject.get(EventFWConstants.KEY.DATA.name()).get(0);
            for (final Object customFieldLogObj : map.values()) {
                if (customFieldLogObj != null && customFieldLogObj instanceof Map) {
                    final Map<Object, Object> customFieldMap = (Map<Object, Object>)customFieldLogObj;
                    if (isLimitFields && !SecurityFilterProperties.getInstance(request).isDevelopmentMode()) {
                        for (final InfoFields.ACCESSLOGFIELDS fields : InfoFields.ACCESSLOGFIELDS.values()) {
                            final String fieldValue = fields.getValue();
                            if (customFieldMap.containsKey(fieldValue)) {
                                cf_map.put(fields.getLogFieldValue(), customFieldMap.get(fieldValue));
                            }
                        }
                    }
                    else {
                        cf_map.putAll(customFieldMap);
                    }
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ZSecAccessLogsCustomFieldsImplProvider.class.getName());
    }
}
