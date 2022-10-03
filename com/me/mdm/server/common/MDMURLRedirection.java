package com.me.mdm.server.common;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.urlredirection.DMURLRedirection;

public class MDMURLRedirection extends DMURLRedirection
{
    public static String getURL(final String key) {
        final String url = MDMApiFactoryProvider.getRedirectURLAPI().getURL(key);
        return url;
    }
}
