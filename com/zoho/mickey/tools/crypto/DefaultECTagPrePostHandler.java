package com.zoho.mickey.tools.crypto;

import java.util.List;
import com.adventnet.persistence.ConfigurationParser;
import java.util.ArrayList;
import com.zoho.framework.utils.crypto.CryptoUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public class DefaultECTagPrePostHandler implements ECTagPrePostHandler
{
    static Logger out;
    
    @Override
    public void preHandle() throws Exception {
    }
    
    @Override
    public void postHandle(final boolean isSuccess) throws Exception {
        if (isSuccess) {
            final HashMap<String, String> h = new HashMap<String, String>();
            h.put("ECTag", CryptoUtil.encrypt(ECTagModifierUtil.getNewKey()));
            ConfigurationParser.writeExtendedPersistenceConfFile((HashMap)h, (HashMap)null, (HashMap)null, (List)new ArrayList());
            DefaultECTagPrePostHandler.out.info("New ECTag has been updated in customer-config.xml");
        }
    }
    
    static {
        DefaultECTagPrePostHandler.out = Logger.getLogger(DefaultECTagPrePostHandler.class.getName());
    }
}
