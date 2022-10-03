package org.jscep.client.inspect;

import java.util.WeakHashMap;
import java.security.cert.CertStore;
import java.util.Map;

public class HarmonyCertStoreInspectorFactory implements CertStoreInspectorFactory
{
    private static final Map<CertStore, CertStoreInspector> INSTANCES;
    
    @Override
    public CertStoreInspector getInstance(final CertStore store) {
        CertStoreInspector instance = HarmonyCertStoreInspectorFactory.INSTANCES.get(store);
        if (instance != null) {
            return instance;
        }
        instance = new HarmonyCertStoreInspector(store);
        HarmonyCertStoreInspectorFactory.INSTANCES.put(store, instance);
        return instance;
    }
    
    static {
        INSTANCES = new WeakHashMap<CertStore, CertStoreInspector>();
    }
}
