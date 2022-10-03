package com.me.devicemanagement.framework.webclient.common;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class GettingStartedUtil
{
    private static Logger logger;
    
    public static boolean isGettingStartedClosed(final HttpServletRequest request, final String parameter) {
        boolean close = false;
        String closeStr = request.getParameter("userclose");
        closeStr = ((closeStr == null) ? request.getParameter("close") : closeStr);
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (closeStr != null) {
                close = true;
                SyMUtil.updateUserParameter(userID, parameter, String.valueOf(close));
            }
            else {
                final String closeDB = SyMUtil.getUserParameter(userID, parameter);
                if (closeDB != null) {
                    close = Boolean.valueOf(closeDB);
                }
            }
        }
        catch (final Exception exp) {
            GettingStartedUtil.logger.log(Level.WARNING, "Exception in  isGettingStartedClosed  : ", exp);
        }
        return close;
    }
    
    static {
        GettingStartedUtil.logger = Logger.getLogger(GettingStartedUtil.class.getName());
    }
}
