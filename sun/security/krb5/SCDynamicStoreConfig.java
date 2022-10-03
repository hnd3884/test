package sun.security.krb5;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.krb5.internal.Krb5;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;
import java.util.Hashtable;

public class SCDynamicStoreConfig
{
    private static boolean DEBUG;
    
    private static native void installNotificationCallback();
    
    private static native Hashtable<String, Object> getKerberosConfig();
    
    private static Vector<String> unwrapHost(final Collection<Hashtable<String, String>> collection) {
        final Vector vector = new Vector();
        final Iterator<Hashtable<String, String>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            vector.add(((Hashtable<K, Object>)iterator.next()).get("host"));
        }
        return vector;
    }
    
    private static Hashtable<String, Object> convertRealmConfigs(final Hashtable<String, ?> hashtable) {
        final Hashtable hashtable2 = new Hashtable();
        for (final String s : hashtable.keySet()) {
            final Hashtable hashtable3 = (Hashtable)hashtable.get(s);
            final Hashtable<String, Vector<String>> hashtable4 = new Hashtable<String, Vector<String>>();
            final Collection collection = hashtable3.get("kdc");
            if (collection != null) {
                hashtable4.put("kdc", unwrapHost(collection));
            }
            final Collection collection2 = hashtable3.get("kadmin");
            if (collection2 != null) {
                hashtable4.put("admin_server", unwrapHost(collection2));
            }
            hashtable2.put(s, hashtable4);
        }
        return hashtable2;
    }
    
    public static Hashtable<String, Object> getConfig() throws IOException {
        final Hashtable<String, Object> kerberosConfig = getKerberosConfig();
        if (kerberosConfig == null) {
            throw new IOException("Could not load configuration from SCDynamicStore");
        }
        if (SCDynamicStoreConfig.DEBUG) {
            System.out.println("Raw map from JNI: " + kerberosConfig);
        }
        return convertNativeConfig(kerberosConfig);
    }
    
    private static Hashtable<String, Object> convertNativeConfig(final Hashtable<String, Object> hashtable) {
        final Hashtable hashtable2 = hashtable.get("realms");
        if (hashtable2 != null) {
            hashtable.remove("realms");
            hashtable.put("realms", convertRealmConfigs(hashtable2));
        }
        WrapAllStringInVector(hashtable);
        if (SCDynamicStoreConfig.DEBUG) {
            System.out.println("stanzaTable : " + hashtable);
        }
        return hashtable;
    }
    
    private static void WrapAllStringInVector(final Hashtable<String, Object> hashtable) {
        for (final String s : hashtable.keySet()) {
            final Hashtable<String, Object> value = hashtable.get(s);
            if (value instanceof Hashtable) {
                WrapAllStringInVector(value);
            }
            else {
                if (!(value instanceof String)) {
                    continue;
                }
                final Vector<String> vector = new Vector<String>();
                vector.add((String)value);
                hashtable.put(s, (Hashtable)vector);
            }
        }
    }
    
    static {
        SCDynamicStoreConfig.DEBUG = Krb5.DEBUG;
        if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                if (System.getProperty("os.name").contains("OS X")) {
                    System.loadLibrary("osx");
                    return true;
                }
                return false;
            }
        })) {
            installNotificationCallback();
        }
    }
}
