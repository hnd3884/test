package com.me.mdm.onpremise.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class FOSSettingsDownloadAPIRequestHandler extends ApiRequestHandler
{
    FOSSettingsFacade fosSettingsFacade;
    
    public FOSSettingsDownloadAPIRequestHandler() {
        this.fosSettingsFacade = new FOSSettingsFacade();
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.fosSettingsFacade.downloadConfigurationfile(apiRequest);
            return null;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
