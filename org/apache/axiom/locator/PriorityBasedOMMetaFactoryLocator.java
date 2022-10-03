package org.apache.axiom.locator;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import org.apache.axiom.om.OMMetaFactory;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.OMMetaFactoryLocator;

class PriorityBasedOMMetaFactoryLocator implements OMMetaFactoryLocator
{
    private static final Log log;
    private final Map<String, OMMetaFactory> factories;
    
    PriorityBasedOMMetaFactoryLocator() {
        this.factories = new HashMap<String, OMMetaFactory>();
    }
    
    void loadImplementations(final List<Implementation> implementations) {
        final Map<String, Integer> priorityMap = new HashMap<String, Integer>();
        this.factories.clear();
        for (final Implementation implementation : implementations) {
            final Feature[] features = implementation.getFeatures();
            for (int i = 0; i < features.length; ++i) {
                final Feature feature = features[i];
                final String name = feature.getName();
                final int priority = feature.getPriority();
                final Integer highestPriority = priorityMap.get(name);
                if (highestPriority == null || priority > highestPriority) {
                    priorityMap.put(name, priority);
                    this.factories.put(name, implementation.getMetaFactory());
                }
            }
        }
        if (PriorityBasedOMMetaFactoryLocator.log.isDebugEnabled()) {
            final StringBuilder buffer = new StringBuilder("Meta factories:");
            for (final Map.Entry<String, OMMetaFactory> entry : this.factories.entrySet()) {
                buffer.append("\n  ");
                buffer.append(entry.getKey());
                buffer.append(": ");
                buffer.append(entry.getValue().getClass().getName());
            }
            PriorityBasedOMMetaFactoryLocator.log.debug((Object)buffer);
        }
    }
    
    public OMMetaFactory getOMMetaFactory(final String feature) {
        return this.factories.get(feature);
    }
    
    static {
        log = LogFactory.getLog((Class)PriorityBasedOMMetaFactoryLocator.class);
    }
}
