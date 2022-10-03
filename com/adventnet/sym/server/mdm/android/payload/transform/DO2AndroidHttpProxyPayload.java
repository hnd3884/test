package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidHttpProxyPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidHttpProxyPayload implements DO2AndroidPayload
{
    public Logger logger;
    
    public DO2AndroidHttpProxyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidHttpProxyPayload proxyPayload = null;
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("GlobalHttpProxyPolicy");
                while (iterator.hasNext()) {
                    proxyPayload = new AndroidHttpProxyPayload("1.0", "com.mdm.mobiledevice.GlobalProxy", "Global Proxy");
                    final Row row = iterator.next();
                    final int proxyType = (int)row.get("PROXY_TYPE");
                    if (proxyType == 1) {
                        proxyPayload.setProxyType("Manual");
                        proxyPayload.setServerURL((String)row.get("PROXY_SERVER"));
                        proxyPayload.setServerPort((int)row.get("PROXY_SERVER_PORT"));
                        String byPassUrl = (String)row.get("BYPASS_PROXY_URL");
                        if (byPassUrl == null) {
                            continue;
                        }
                        byPassUrl = byPassUrl.replaceAll(" ", "");
                        if (byPassUrl.equals("")) {
                            continue;
                        }
                        final String[] urls = byPassUrl.split(",");
                        final JSONArray urlArray = new JSONArray();
                        for (final String url : urls) {
                            urlArray.put((Object)url);
                        }
                        proxyPayload.setByPassUrl(urlArray);
                    }
                    else {
                        proxyPayload.setProxyType("Automatic");
                        proxyPayload.setPACUrl((String)row.get("PROXY_PAC_URL"));
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while generate Http Proxy payload", ex);
        }
        return proxyPayload;
    }
}
