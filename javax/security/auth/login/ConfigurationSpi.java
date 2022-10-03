package javax.security.auth.login;

public abstract class ConfigurationSpi
{
    protected abstract AppConfigurationEntry[] engineGetAppConfigurationEntry(final String p0);
    
    protected void engineRefresh() {
    }
}
