package com.me.mdm.doc;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.mdm.server.doc.DocViewApi;

public class DocOpViewUrl implements DocViewApi
{
    public static final Logger DOC_LOGGER;
    
    public String getDocViewUrl(final int docType, final long docSize, final String mimeType, final String filePath) {
        String url = "--";
        final HashMap hm = new HashMap();
        hm.put("IS_SERVER", false);
        hm.put("IS_AUTHTOKEN", true);
        hm.put("path", filePath);
        try {
            url = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        }
        catch (final Exception e) {
            DocOpViewUrl.DOC_LOGGER.log(Level.SEVERE, "Exception in getDocViewUrl", e);
        }
        return url;
    }
    
    static {
        DOC_LOGGER = Logger.getLogger("MDMDocLogger");
    }
}
