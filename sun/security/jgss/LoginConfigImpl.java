package sun.security.jgss;

import sun.security.action.GetPropertyAction;
import java.util.Map;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.ietf.jgss.Oid;
import sun.security.util.Debug;
import javax.security.auth.login.Configuration;

public class LoginConfigImpl extends Configuration
{
    private final Configuration config;
    private final GSSCaller caller;
    private final String mechName;
    private static final Debug debug;
    public static final boolean HTTP_USE_GLOBAL_CREDS;
    
    public LoginConfigImpl(final GSSCaller caller, final Oid oid) {
        this.caller = caller;
        if (oid.equals(GSSUtil.GSS_KRB5_MECH_OID)) {
            this.mechName = "krb5";
            this.config = AccessController.doPrivileged((PrivilegedAction<Configuration>)new PrivilegedAction<Configuration>() {
                @Override
                public Configuration run() {
                    return Configuration.getConfiguration();
                }
            });
            return;
        }
        throw new IllegalArgumentException(oid.toString() + " not supported");
    }
    
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(final String s) {
        AppConfigurationEntry[] array = null;
        if ("OTHER".equalsIgnoreCase(s)) {
            return null;
        }
        String[] array2 = null;
        if ("krb5".equals(this.mechName)) {
            if (this.caller == GSSCaller.CALLER_INITIATE) {
                array2 = new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.security.jgss.initiate" };
            }
            else if (this.caller == GSSCaller.CALLER_ACCEPT) {
                array2 = new String[] { "com.sun.security.jgss.krb5.accept", "com.sun.security.jgss.accept" };
            }
            else if (this.caller == GSSCaller.CALLER_SSL_CLIENT) {
                array2 = new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.net.ssl.client" };
            }
            else if (this.caller == GSSCaller.CALLER_SSL_SERVER) {
                array2 = new String[] { "com.sun.security.jgss.krb5.accept", "com.sun.net.ssl.server" };
            }
            else if (this.caller instanceof HttpCaller) {
                array2 = new String[] { "com.sun.security.jgss.krb5.initiate" };
            }
            else if (this.caller == GSSCaller.CALLER_UNKNOWN) {
                throw new AssertionError((Object)"caller not defined");
            }
            for (final String s2 : array2) {
                array = this.config.getAppConfigurationEntry(s2);
                if (LoginConfigImpl.debug != null) {
                    LoginConfigImpl.debug.println("Trying " + s2 + ((array == null) ? ": does not exist." : ": Found!"));
                }
                if (array != null) {
                    break;
                }
            }
            if (array == null) {
                if (LoginConfigImpl.debug != null) {
                    LoginConfigImpl.debug.println("Cannot read JGSS entry, use default values instead.");
                }
                array = this.getDefaultConfigurationEntry();
            }
            return array;
        }
        throw new IllegalArgumentException(this.mechName + " not supported");
    }
    
    private AppConfigurationEntry[] getDefaultConfigurationEntry() {
        final HashMap hashMap = new HashMap(2);
        if (this.mechName == null || this.mechName.equals("krb5")) {
            if (isServerSide(this.caller)) {
                hashMap.put("useKeyTab", "true");
                hashMap.put("storeKey", "true");
                hashMap.put("doNotPrompt", "true");
                hashMap.put("principal", "*");
                hashMap.put("isInitiator", "false");
            }
            else {
                if (this.caller instanceof HttpCaller && !LoginConfigImpl.HTTP_USE_GLOBAL_CREDS) {
                    hashMap.put("useTicketCache", "false");
                }
                else {
                    hashMap.put("useTicketCache", "true");
                }
                hashMap.put("doNotPrompt", "false");
            }
            return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, hashMap) };
        }
        return null;
    }
    
    private static boolean isServerSide(final GSSCaller gssCaller) {
        return GSSCaller.CALLER_ACCEPT == gssCaller || GSSCaller.CALLER_SSL_SERVER == gssCaller;
    }
    
    static {
        debug = Debug.getInstance("gssloginconfig", "\t[GSS LoginConfigImpl]");
        HTTP_USE_GLOBAL_CREDS = !"false".equalsIgnoreCase(GetPropertyAction.privilegedGetProperty("http.use.global.creds"));
    }
}
