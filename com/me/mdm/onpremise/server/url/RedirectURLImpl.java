package com.me.mdm.onpremise.server.url;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.urlredirection.DMURLRedirection;
import com.me.mdm.server.factory.RedirectURLAPI;

public class RedirectURLImpl implements RedirectURLAPI
{
    public String getURL(final String key) {
        String url = null;
        try {
            url = DMURLRedirection.getURL(key);
        }
        catch (final Exception ex) {
            Logger.getLogger(RedirectURLImpl.class.getName()).log(Level.INFO, "Exception while get the redirecturl : {0}", ex);
        }
        return url;
    }
}
