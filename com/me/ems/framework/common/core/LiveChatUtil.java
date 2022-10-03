package com.me.ems.framework.common.core;

import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import org.json.JSONObject;
import java.util.logging.Logger;

public class LiveChatUtil
{
    private static final LiveChatUtil LIVE_CHAT_UTIL;
    private static Logger logger;
    private JSONObject liveChatConf;
    private Map<String, LiveChatHandler> instanceMap;
    
    private LiveChatUtil() {
        try {
            final String filePath = SyMUtil.getInstallationDir().concat(File.separator).concat(File.separator).concat("conf").concat(File.separator).concat("livechat-conf.json");
            this.liveChatConf = JsonUtils.loadJsonFile(new File(filePath));
        }
        catch (final Exception ex) {
            LiveChatUtil.logger.log(Level.SEVERE, "Exception while loading SalesIQ handlers", ex);
        }
    }
    
    public static LiveChatUtil getInstance() {
        return LiveChatUtil.LIVE_CHAT_UTIL;
    }
    
    public Map<String, Object> getLiveChatData() throws Exception {
        final Map<String, Object> salesIQDetails = new HashMap<String, Object>(6);
        if (CustomerInfoUtil.isSAS()) {
            salesIQDetails.put("enableLiveChat", Boolean.TRUE);
        }
        else {
            String enableLiveChat = SyMUtil.getSyMParameter("ENABLE_LIVE_CHAT");
            enableLiveChat = ((enableLiveChat == null) ? "true" : enableLiveChat.toLowerCase());
            final boolean enableChat = Boolean.parseBoolean(enableLiveChat);
            salesIQDetails.put("enableLiveChat", enableChat);
            if (!enableChat) {
                return salesIQDetails;
            }
        }
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        final LiveChatHandler productSpecificHandler = this.getProductSpecificHandler(productCode);
        if (productSpecificHandler != null) {
            productSpecificHandler.getProductSpecificLiveChatData(salesIQDetails);
            salesIQDetails.put("locale", I18N.getLocale().toString());
        }
        else {
            salesIQDetails.put("isErrorOccurred", Boolean.TRUE);
        }
        return salesIQDetails;
    }
    
    private LiveChatHandler getProductSpecificHandler(final String productCode) throws Exception {
        try {
            if (this.instanceMap == null) {
                this.instanceMap = new HashMap<String, LiveChatHandler>(3);
            }
            LiveChatHandler productSpecificInstance = this.instanceMap.get(productCode);
            if (productSpecificInstance == null) {
                final String className = this.liveChatConf.getString(productCode);
                productSpecificInstance = (LiveChatHandler)Class.forName(className).newInstance();
                this.instanceMap.put(productCode, productSpecificInstance);
            }
            return productSpecificInstance;
        }
        catch (final NullPointerException exception) {
            LiveChatUtil.logger.log(Level.SEVERE, "salesiq-conf json maybe empty or null", exception);
            return null;
        }
    }
    
    static {
        LIVE_CHAT_UTIL = new LiveChatUtil();
        LiveChatUtil.logger = Logger.getLogger(LiveChatUtil.class.getName());
    }
}
