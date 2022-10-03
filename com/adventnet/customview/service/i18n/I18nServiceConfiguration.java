package com.adventnet.customview.service.i18n;

import java.util.ResourceBundle;
import com.adventnet.customview.service.ServiceConfiguration;

public class I18nServiceConfiguration implements ServiceConfiguration
{
    private String serviceName;
    private transient ResourceBundle rb;
    
    public I18nServiceConfiguration(final ResourceBundle rb) {
        this.serviceName = "DS_I18N_SERVICE";
        this.rb = null;
        this.rb = rb;
    }
    
    @Override
    public String getServiceName() {
        return this.serviceName;
    }
    
    public ResourceBundle getResourceBundle() {
        return this.rb;
    }
    
    public void setResourceBundle(final ResourceBundle resBundle) {
        this.rb = resBundle;
    }
}
