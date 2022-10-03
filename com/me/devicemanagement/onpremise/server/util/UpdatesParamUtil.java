package com.me.devicemanagement.onpremise.server.util;

import com.me.devicemanagement.framework.server.util.DCPluginUtil;
import java.util.HashMap;

public class UpdatesParamUtil extends com.me.devicemanagement.framework.server.util.UpdatesParamUtil
{
    private static UpdatesParamUtil updatesParamUtil;
    
    private UpdatesParamUtil() {
    }
    
    public static UpdatesParamUtil getInstance() {
        return UpdatesParamUtil.updatesParamUtil = ((UpdatesParamUtil.updatesParamUtil == null) ? new UpdatesParamUtil() : UpdatesParamUtil.updatesParamUtil);
    }
    
    public HashMap getUpdateProductMsg() {
        final HashMap details = new HashMap();
        final String updateMsg = getUpdParameter("PRODUCT_UPDATE_MSG");
        if (updateMsg != null) {
            details.put("PRODUCT_UPDATE_MSG", updateMsg);
        }
        if (DCPluginUtil.getInstance().isPlugin()) {
            details.put("UPDATE_DOWNLOAD_URL", "http://www.manageengine.com/products/service-desk/desktop-central-plugin.html?sdp-download");
        }
        else {
            final String updateURL = getUpdParameter("UPDATE_DOWNLOAD_URL");
            if (updateURL != null) {
                details.put("UPDATE_DOWNLOAD_URL", updateURL);
            }
        }
        final String updateTitle = getUpdParameter("PRODUCT_UPDATE_MSG_TITLE");
        if (updateTitle != null) {
            details.put("PRODUCT_UPDATE_MSG_TITLE", updateTitle);
        }
        return details;
    }
    
    static {
        UpdatesParamUtil.updatesParamUtil = null;
    }
}
