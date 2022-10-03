package sun.security.krb5.internal.ccache;

import sun.security.krb5.RealmException;
import sun.security.krb5.internal.Krb5;
import java.util.Iterator;
import java.util.List;
import sun.security.krb5.internal.LoginOptions;
import sun.security.krb5.KrbException;
import java.io.IOException;
import sun.security.krb5.PrincipalName;

public abstract class CredentialsCache
{
    static CredentialsCache singleton;
    static String cacheName;
    private static boolean DEBUG;
    
    public static CredentialsCache getInstance(final PrincipalName principalName) {
        return FileCredentialsCache.acquireInstance(principalName, null);
    }
    
    public static CredentialsCache getInstance(final String s) {
        if (s.length() >= 5 && s.substring(0, 5).equalsIgnoreCase("FILE:")) {
            return FileCredentialsCache.acquireInstance(null, s.substring(5));
        }
        return FileCredentialsCache.acquireInstance(null, s);
    }
    
    public static CredentialsCache getInstance(final PrincipalName principalName, final String s) {
        if (s != null && s.length() >= 5 && s.regionMatches(true, 0, "FILE:", 0, 5)) {
            return FileCredentialsCache.acquireInstance(principalName, s.substring(5));
        }
        return FileCredentialsCache.acquireInstance(principalName, s);
    }
    
    public static CredentialsCache getInstance() {
        return FileCredentialsCache.acquireInstance();
    }
    
    public static CredentialsCache create(final PrincipalName principalName, String substring) {
        if (substring == null) {
            throw new RuntimeException("cache name error");
        }
        if (substring.length() >= 5 && substring.regionMatches(true, 0, "FILE:", 0, 5)) {
            substring = substring.substring(5);
            return FileCredentialsCache.New(principalName, substring);
        }
        return FileCredentialsCache.New(principalName, substring);
    }
    
    public static CredentialsCache create(final PrincipalName principalName) {
        return FileCredentialsCache.New(principalName);
    }
    
    public static String cacheName() {
        return CredentialsCache.cacheName;
    }
    
    public abstract PrincipalName getPrimaryPrincipal();
    
    public abstract void update(final Credentials p0);
    
    public abstract void save() throws IOException, KrbException;
    
    public abstract Credentials[] getCredsList();
    
    public abstract Credentials getDefaultCreds();
    
    public abstract sun.security.krb5.Credentials getInitialCreds();
    
    public abstract Credentials getCreds(final PrincipalName p0);
    
    public abstract Credentials getCreds(final LoginOptions p0, final PrincipalName p1);
    
    public abstract void addConfigEntry(final ConfigEntry p0);
    
    public abstract List<ConfigEntry> getConfigEntries();
    
    public ConfigEntry getConfigEntry(final String s) {
        final List<ConfigEntry> configEntries = this.getConfigEntries();
        if (configEntries != null) {
            for (final ConfigEntry configEntry : configEntries) {
                if (configEntry.getName().equals(s)) {
                    return configEntry;
                }
            }
        }
        return null;
    }
    
    static {
        CredentialsCache.singleton = null;
        CredentialsCache.DEBUG = Krb5.DEBUG;
    }
    
    public static class ConfigEntry
    {
        private final String name;
        private final PrincipalName princ;
        private final byte[] data;
        
        public ConfigEntry(final String name, final PrincipalName princ, final byte[] data) {
            this.name = name;
            this.princ = princ;
            this.data = data;
        }
        
        public String getName() {
            return this.name;
        }
        
        public PrincipalName getPrinc() {
            return this.princ;
        }
        
        public byte[] getData() {
            return this.data;
        }
        
        @Override
        public String toString() {
            return this.name + ((this.princ != null) ? ("." + this.princ) : "") + ": " + new String(this.data);
        }
        
        public PrincipalName getSName() {
            try {
                return new PrincipalName("krb5_ccache_conf_data/" + this.name + ((this.princ != null) ? ("/" + this.princ) : "") + "@X-CACHECONF:");
            }
            catch (final RealmException ex) {
                throw new AssertionError((Object)ex);
            }
        }
    }
}
