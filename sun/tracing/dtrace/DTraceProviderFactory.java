package sun.tracing.dtrace;

import java.security.Permission;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;

public final class DTraceProviderFactory extends ProviderFactory
{
    @Override
    public <T extends Provider> T createProvider(final Class<T> clazz) {
        final DTraceProvider dTraceProvider = new DTraceProvider(clazz);
        final Provider proxyInstance = dTraceProvider.newProxyInstance();
        dTraceProvider.setProxy(proxyInstance);
        dTraceProvider.init();
        new Activation(dTraceProvider.getModuleName(), new DTraceProvider[] { dTraceProvider });
        return (T)proxyInstance;
    }
    
    public Map<Class<? extends Provider>, Provider> createProviders(final Set<Class<? extends Provider>> set, final String s) {
        final HashMap hashMap = new HashMap();
        final HashSet set2 = new HashSet();
        for (final Class clazz : set) {
            final DTraceProvider dTraceProvider = new DTraceProvider(clazz);
            set2.add(dTraceProvider);
            hashMap.put(clazz, dTraceProvider.newProxyInstance());
        }
        new Activation(s, (DTraceProvider[])set2.toArray(new DTraceProvider[0]));
        return hashMap;
    }
    
    public static boolean isSupported() {
        try {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(new RuntimePermission("com.sun.tracing.dtrace.createProvider"));
            }
            return JVM.isSupported();
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
}
