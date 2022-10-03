package com.sun.security.auth.login;

import javax.security.auth.login.AppConfigurationEntry;
import java.net.URI;
import jdk.Exported;
import javax.security.auth.login.Configuration;

@Exported
public class ConfigFile extends Configuration
{
    private final sun.security.provider.ConfigFile.Spi spi;
    
    public ConfigFile() {
        this.spi = new sun.security.provider.ConfigFile.Spi();
    }
    
    public ConfigFile(final URI uri) {
        this.spi = new sun.security.provider.ConfigFile.Spi(uri);
    }
    
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(final String s) {
        return this.spi.engineGetAppConfigurationEntry(s);
    }
    
    @Override
    public void refresh() {
        this.spi.engineRefresh();
    }
}
