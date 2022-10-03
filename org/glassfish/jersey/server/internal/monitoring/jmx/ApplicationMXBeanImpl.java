package org.glassfish.jersey.server.internal.monitoring.jmx;

import org.glassfish.jersey.server.ResourceConfig;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.HashMap;
import java.util.HashSet;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;
import java.util.Set;
import java.util.Date;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ApplicationMXBean;

public class ApplicationMXBeanImpl implements ApplicationMXBean
{
    private final String applicationName;
    private final String applicationClass;
    private final Map<String, String> configurationProperties;
    private final Date startTime;
    private final Set<String> providers;
    private final Set<String> registeredClasses;
    private final Set<String> registeredInstances;
    
    public ApplicationMXBeanImpl(final ApplicationInfo applicationInfo, final MBeanExposer mBeanExposer, final String parentName) {
        this.providers = new HashSet<String>();
        this.registeredClasses = new HashSet<String>();
        this.registeredInstances = new HashSet<String>();
        for (final Class<?> provider : applicationInfo.getProviders()) {
            this.providers.add(provider.getName());
        }
        for (final Class<?> registeredClass : applicationInfo.getRegisteredClasses()) {
            this.registeredClasses.add(registeredClass.toString());
        }
        for (final Object registeredInstance : applicationInfo.getRegisteredInstances()) {
            this.registeredInstances.add(registeredInstance.getClass().getName());
        }
        final ResourceConfig resourceConfig = applicationInfo.getResourceConfig();
        this.applicationName = resourceConfig.getApplicationName();
        this.applicationClass = resourceConfig.getApplication().getClass().getName();
        this.configurationProperties = new HashMap<String, String>();
        for (final Map.Entry<String, Object> entry : resourceConfig.getProperties().entrySet()) {
            final Object value = entry.getValue();
            String stringValue;
            try {
                stringValue = ((value == null) ? "[null]" : value.toString());
            }
            catch (final Exception e) {
                stringValue = LocalizationMessages.PROPERTY_VALUE_TOSTRING_THROWS_EXCEPTION(e.getClass().getName(), e.getMessage());
            }
            this.configurationProperties.put(entry.getKey(), stringValue);
        }
        this.startTime = new Date(applicationInfo.getStartTime().getTime());
        mBeanExposer.registerMBean(this, parentName + ",global=Configuration");
    }
    
    @Override
    public String getApplicationName() {
        return this.applicationName;
    }
    
    @Override
    public String getApplicationClass() {
        return this.applicationClass;
    }
    
    @Override
    public Map<String, String> getProperties() {
        return this.configurationProperties;
    }
    
    @Override
    public Date getStartTime() {
        return this.startTime;
    }
    
    @Override
    public Set<String> getRegisteredClasses() {
        return this.registeredClasses;
    }
    
    @Override
    public Set<String> getRegisteredInstances() {
        return this.registeredInstances;
    }
    
    @Override
    public Set<String> getProviderClasses() {
        return this.providers;
    }
}
