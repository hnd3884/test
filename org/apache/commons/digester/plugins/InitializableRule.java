package org.apache.commons.digester.plugins;

public interface InitializableRule
{
    void postRegisterInit(final String p0) throws PluginConfigurationException;
}
