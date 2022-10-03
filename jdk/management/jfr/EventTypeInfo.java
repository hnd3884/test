package jdk.management.jfr;

import java.util.StringJoiner;
import java.util.Iterator;
import jdk.jfr.SettingDescriptor;
import java.util.Collections;
import java.util.ArrayList;
import javax.management.openmbean.CompositeData;
import jdk.jfr.EventType;
import java.util.List;

public final class EventTypeInfo
{
    private final List<SettingDescriptorInfo> settings;
    private final long id;
    private final String name;
    private final String description;
    private final String label;
    private final List<String> categoryNames;
    
    EventTypeInfo(final EventType eventType) {
        this.settings = creatingSettingDescriptorInfos(eventType);
        this.id = eventType.getId();
        this.name = eventType.getName();
        this.label = eventType.getLabel();
        this.description = eventType.getDescription();
        this.categoryNames = eventType.getCategoryNames();
    }
    
    private EventTypeInfo(final CompositeData compositeData) {
        this.settings = createSettings(compositeData.get("settings"));
        this.id = (long)compositeData.get("id");
        this.name = (String)compositeData.get("name");
        this.label = (String)compositeData.get("label");
        this.description = (String)compositeData.get("description");
        this.categoryNames = createCategoryNames((Object[])compositeData.get("category"));
    }
    
    private static List<String> createCategoryNames(final Object[] array) {
        final ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; ++i) {
            list.add(array[i]);
        }
        return (List<String>)Collections.unmodifiableList((List<?>)list);
    }
    
    private static List<SettingDescriptorInfo> creatingSettingDescriptorInfos(final EventType eventType) {
        final List<SettingDescriptor> settingDescriptors = eventType.getSettingDescriptors();
        final ArrayList list = new ArrayList(settingDescriptors.size());
        final Iterator iterator = settingDescriptors.iterator();
        while (iterator.hasNext()) {
            list.add((Object)new SettingDescriptorInfo((SettingDescriptor)iterator.next()));
        }
        return Collections.unmodifiableList((List<? extends SettingDescriptorInfo>)list);
    }
    
    private static List<SettingDescriptorInfo> createSettings(final Object o) {
        if (o != null && o.getClass().isArray()) {
            final Object[] array = (Object[])o;
            final ArrayList list = new ArrayList(array.length);
            for (final Object o2 : array) {
                if (o2 instanceof CompositeData) {
                    list.add((Object)SettingDescriptorInfo.from((CompositeData)o2));
                }
            }
            return Collections.unmodifiableList((List<? extends SettingDescriptorInfo>)list);
        }
        return Collections.emptyList();
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public List<String> getCategoryNames() {
        return this.categoryNames;
    }
    
    public long getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public List<SettingDescriptorInfo> getSettingDescriptors() {
        return this.settings;
    }
    
    @Override
    public String toString() {
        final Stringifier stringifier = new Stringifier();
        stringifier.add("id", this.id);
        stringifier.add("name", this.name);
        stringifier.add("label", this.label);
        stringifier.add("description", this.description);
        final StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        final Iterator<String> iterator = this.categoryNames.iterator();
        while (iterator.hasNext()) {
            stringJoiner.add(iterator.next());
        }
        stringifier.add("category", stringJoiner.toString());
        return stringifier.toString();
    }
    
    public static EventTypeInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        return new EventTypeInfo(compositeData);
    }
}
