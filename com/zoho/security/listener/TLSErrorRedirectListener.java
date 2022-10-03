package com.zoho.security.listener;

import com.zoho.security.util.SASConfigProviderUtil;
import com.zoho.conf.ConfigurationListener;

public class TLSErrorRedirectListener implements ConfigurationListener
{
    public void valueChanged(final String key, final String value) {
        switch (key) {
            case "zsec.tls.error.response": {
                SASConfigProviderUtil.updatedErrorResponse = SASConfigProviderUtil.getErrorResponseFromServer(value);
                break;
            }
            case "zsec.tls.error.excludedomains": {
                SASConfigProviderUtil.updatedExcludeDomains = SASConfigProviderUtil.getExcludedDomainsAsList(value);
                break;
            }
        }
    }
}
