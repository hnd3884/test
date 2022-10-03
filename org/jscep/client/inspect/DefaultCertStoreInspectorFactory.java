package org.jscep.client.inspect;

import java.util.Collections;
import java.util.WeakHashMap;
import java.security.cert.CertStore;
import java.util.Map;

public class DefaultCertStoreInspectorFactory implements CertStoreInspectorFactory
{
    private static final Map<CertStore, CertStoreInspector> INSTANCES;
    
    @Override
    public CertStoreInspector getInstance(final CertStore store) {
        CertStoreInspector instance = DefaultCertStoreInspectorFactory.INSTANCES.get(store);
        if (instance != null) {
            return instance;
        }
        instance = new DefaultCertStoreInspector(store);
        DefaultCertStoreInspectorFactory.INSTANCES.put(store, instance);
        return instance;
    }
    
    static {
        INSTANCES = Collections.synchronizedMap(new WeakHashMap<CertStore, CertStoreInspector>());
    }
}
