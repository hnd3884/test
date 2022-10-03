package com.microsoft.sqlserver.jdbc;

import java.util.Map;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public class JaasConfiguration extends Configuration
{
    private final Configuration delegate;
    private AppConfigurationEntry[] defaultValue;
    
    private static AppConfigurationEntry[] generateDefaultConfiguration() {
        if (Util.isIBM()) {
            final Map<String, String> confDetailsWithoutPassword = new HashMap<String, String>();
            confDetailsWithoutPassword.put("useDefaultCcache", "true");
            final Map<String, String> confDetailsWithPassword = new HashMap<String, String>();
            final String ibmLoginModule = "com.ibm.security.auth.module.Krb5LoginModule";
            return new AppConfigurationEntry[] { new AppConfigurationEntry("com.ibm.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT, confDetailsWithoutPassword), new AppConfigurationEntry("com.ibm.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT, confDetailsWithPassword) };
        }
        final Map<String, String> confDetails = new HashMap<String, String>();
        confDetails.put("useTicketCache", "true");
        return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, confDetails) };
    }
    
    JaasConfiguration(final Configuration delegate) {
        this.delegate = delegate;
        this.defaultValue = generateDefaultConfiguration();
    }
    
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(final String name) {
        final AppConfigurationEntry[] conf = (AppConfigurationEntry[])((this.delegate == null) ? null : this.delegate.getAppConfigurationEntry(name));
        if (conf == null && name.equals(SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue())) {
            return this.defaultValue;
        }
        return conf;
    }
    
    @Override
    public void refresh() {
        if (null != this.delegate) {
            this.delegate.refresh();
        }
    }
}
