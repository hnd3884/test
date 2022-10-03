package com.me.devicemanagement.onpremise.server.util;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class DemoUtil extends com.me.devicemanagement.framework.server.util.DemoUtil
{
    public boolean isDemoMode() {
        String demoModeFromDB = null;
        boolean isDemoMode = false;
        try {
            final String cache_value = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("IS_DEMO_MODE", 2);
            if (cache_value == null) {
                demoModeFromDB = SyMUtil.getSyMParameter("isDemoMode");
                isDemoMode = (demoModeFromDB != null && Boolean.parseBoolean(demoModeFromDB));
                ApiFactoryProvider.getCacheAccessAPI().putCache("IS_DEMO_MODE", (Object)("" + isDemoMode), 2);
            }
            else {
                isDemoMode = Boolean.parseBoolean(cache_value);
            }
        }
        catch (final Exception e) {
            DemoUtil.logger.log(Level.SEVERE, "Exception while getting demo mode... ", e);
        }
        return isDemoMode;
    }
}
