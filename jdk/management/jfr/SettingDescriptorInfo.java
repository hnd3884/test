package jdk.management.jfr;

import jdk.management.jfr.internal.FlightRecorderMXBeanProvider;
import java.util.concurrent.Callable;
import javax.management.openmbean.CompositeData;
import jdk.jfr.SettingDescriptor;

public final class SettingDescriptorInfo
{
    private final String name;
    private final String label;
    private final String description;
    private final String typeName;
    private final String contentType;
    private final String defaultValue;
    
    SettingDescriptorInfo(final SettingDescriptor settingDescriptor) {
        this.name = settingDescriptor.getName();
        this.label = settingDescriptor.getLabel();
        this.description = settingDescriptor.getDescription();
        this.typeName = settingDescriptor.getTypeName();
        this.contentType = settingDescriptor.getContentType();
        this.defaultValue = settingDescriptor.getDefaultValue();
    }
    
    private SettingDescriptorInfo(final CompositeData compositeData) {
        this.name = (String)compositeData.get("name");
        this.label = (String)compositeData.get("label");
        this.description = (String)compositeData.get("description");
        this.typeName = (String)compositeData.get("typeName");
        this.defaultValue = (String)compositeData.get("defaultValue");
        this.contentType = (String)compositeData.get("contentType");
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public static SettingDescriptorInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        return new SettingDescriptorInfo(compositeData);
    }
    
    @Override
    public String toString() {
        final Stringifier stringifier = new Stringifier();
        stringifier.add("name", this.name);
        stringifier.add("label", this.label);
        stringifier.add("description", this.description);
        stringifier.add("typeName", this.typeName);
        stringifier.add("contentType", this.contentType);
        stringifier.add("defaultValue", this.defaultValue);
        return stringifier.toString();
    }
    
    static {
        FlightRecorderMXBeanProvider.setFlightRecorderMXBeanFactory(new Callable<FlightRecorderMXBean>() {
            @Override
            public FlightRecorderMXBean call() throws Exception {
                return new FlightRecorderMXBeanImpl();
            }
        });
    }
}
