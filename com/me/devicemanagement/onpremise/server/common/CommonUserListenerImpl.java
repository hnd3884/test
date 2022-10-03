package com.me.devicemanagement.onpremise.server.common;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.UserEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.AbstractUserListener;

public class CommonUserListenerImpl extends AbstractUserListener
{
    private static Logger logger;
    
    public void userModified(final UserEvent userEvent) {
        CommonUserListenerImpl.logger.log(Level.INFO, "User is modified, so going to update the cache for time zone and locale and argument is..." + userEvent);
        final Long userID = userEvent.userID;
        final String timezoneKey = userID + "_" + "USERTIMEZONEID";
        final String localeKey = userID + "_" + "USERLOCALE";
        final String dateKey = userID + "_" + "DATEFORMAT";
        final String timeKey = userID + "_" + "TIMEFORMAT";
        CommonUserListenerImpl.logger.log(Level.INFO, "Dropping timezone related cache with keys " + timezoneKey + " , " + localeKey + " , " + dateKey + " and " + timeKey);
        ApiFactoryProvider.getCacheAccessAPI().removeCache(timezoneKey, 3);
        ApiFactoryProvider.getCacheAccessAPI().removeCache(localeKey, 3);
        ApiFactoryProvider.getCacheAccessAPI().removeCache(dateKey, 3);
        ApiFactoryProvider.getCacheAccessAPI().removeCache(timeKey, 3);
    }
    
    static {
        CommonUserListenerImpl.logger = Logger.getLogger(CommonUserListenerImpl.class.getName());
    }
}
