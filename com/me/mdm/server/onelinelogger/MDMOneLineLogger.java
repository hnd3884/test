package com.me.mdm.server.onelinelogger;

import org.json.simple.JSONObject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;

public class MDMOneLineLogger
{
    private MDMOneLineLogger() {
    }
    
    public static void log(final Level level, final String operation, final String message) {
        if (CustomerInfoUtil.getInstance().isMSP() && !MDMStringUtils.isEmpty(CustomerInfoThreadLocal.getCustomerId())) {
            final String customerIdStr = "CustomerID:" + CustomerInfoThreadLocal.getCustomerId();
            SecurityOneLineLogger.log("MDM", operation, new String[] { customerIdStr, message }, level);
        }
        else {
            SecurityOneLineLogger.log("MDM", operation, message, level);
        }
    }
    
    public static void log(final Level level, final String operation, List<String> messages) {
        if (messages == null) {
            messages = new ArrayList<String>();
        }
        if (CustomerInfoUtil.getInstance().isMSP() && !MDMStringUtils.isEmpty(CustomerInfoThreadLocal.getCustomerId())) {
            messages.add("CustomerID:" + CustomerInfoThreadLocal.getCustomerId());
        }
        SecurityOneLineLogger.log("MDM", operation, (String[])messages.toArray(new String[0]), level);
    }
    
    public static void log(final Level level, final String operation, final String message, final Object[] params) {
        log(level, operation, MessageFormat.format(message, params));
    }
    
    public static void log(final Level level, final String operation, JSONObject message) {
        if (CustomerInfoUtil.getInstance().isMSP() && !MDMStringUtils.isEmpty(CustomerInfoThreadLocal.getCustomerId())) {
            if (message == null) {
                message = new JSONObject();
            }
            message.put((Object)"CustomerID", (Object)CustomerInfoThreadLocal.getCustomerId());
        }
        SecurityOneLineLogger.log("MDM", operation, message, level);
    }
}
