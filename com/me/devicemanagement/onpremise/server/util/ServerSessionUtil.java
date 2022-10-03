package com.me.devicemanagement.onpremise.server.util;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.common.DMModuleHandler;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

public class ServerSessionUtil
{
    private static Logger logger;
    
    public void setDefaultSessionValues(final HttpSession session) {
        String didValue = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING");
        if (didValue == null) {
            didValue = SyMUtil.getDIDValue();
            ApiFactoryProvider.getCacheAccessAPI().putCache("DID_STRING", (Object)didValue);
        }
        session.setAttribute("DID", (Object)didValue);
        session.setAttribute("isUserLangIsEng", (Object)I18NUtil.isUserLangIsEng());
        if (session.getAttribute("trackCode") == null) {
            session.setAttribute("desktopModuleState", (Object)DMApplicationHandler.getInstance().getDesktopModuleState());
            final String prodUrl = ProductUrlLoader.getInstance().getValue("prodUrl");
            didValue = ((didValue != null) ? didValue : "");
            final String trackCode = ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + didValue;
            final String mdmUrl = ProductUrlLoader.getInstance().getValue("mdmUrl");
            final String dcUrl = ProductUrlLoader.getInstance().getValue("dcUrl");
            session.setAttribute("mdmUrl", (Object)mdmUrl);
            session.setAttribute("dcUrl", (Object)dcUrl);
            session.setAttribute("prodUrl", (Object)prodUrl);
            session.setAttribute("trackCode", (Object)trackCode);
            session.setAttribute("isMDMP", (Object)DMApplicationHandler.isMdmProduct());
            session.setAttribute("isOSDProduct", (Object)DMApplicationHandler.isOSDProduct());
            session.setAttribute("isOSDEnabled", (Object)DMModuleHandler.isOSDEnabled());
            ServerSessionUtil.logger.log(Level.FINE, "Desktop Module State : " + DMApplicationHandler.getInstance().getDesktopModuleState());
            ServerSessionUtil.logger.log(Level.FINE, "MDM Module State : " + DMApplicationHandler.getInstance().getMobileDeviceModuleState());
        }
    }
    
    static {
        ServerSessionUtil.logger = Logger.getLogger(ServerSessionUtil.class.getName());
    }
}
