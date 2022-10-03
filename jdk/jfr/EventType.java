package jdk.jfr;

import jdk.jfr.internal.Type;
import java.util.Arrays;
import jdk.jfr.internal.MetadataRepository;
import jdk.jfr.internal.JVMSupport;
import jdk.jfr.internal.Utils;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import jdk.jfr.internal.PlatformEventType;
import jdk.Exported;

@Exported
public final class EventType
{
    private final PlatformEventType platformEventType;
    private final List<String> UNCATEGORIZED;
    private Map<String, ValueDescriptor> cache;
    
    EventType(final PlatformEventType platformEventType) {
        this.UNCATEGORIZED = Collections.singletonList("Uncategorized");
        this.platformEventType = platformEventType;
    }
    
    public List<ValueDescriptor> getFields() {
        return this.platformEventType.getFields();
    }
    
    public ValueDescriptor getField(final String s) {
        Objects.requireNonNull(s);
        if (this.cache == null) {
            final List<ValueDescriptor> fields = this.getFields();
            final LinkedHashMap cache = new LinkedHashMap(fields.size());
            for (final ValueDescriptor valueDescriptor : fields) {
                cache.put((Object)valueDescriptor.getName(), (Object)valueDescriptor);
            }
            this.cache = (Map<String, ValueDescriptor>)cache;
        }
        return this.cache.get(s);
    }
    
    public String getName() {
        return this.platformEventType.getName();
    }
    
    public String getLabel() {
        return this.platformEventType.getLabel();
    }
    
    public long getId() {
        return this.platformEventType.getId();
    }
    
    public List<AnnotationElement> getAnnotationElements() {
        return this.platformEventType.getAnnotationElements();
    }
    
    public boolean isEnabled() {
        return this.platformEventType.isEnabled();
    }
    
    public String getDescription() {
        return this.platformEventType.getDescription();
    }
    
    public <A extends Annotation> A getAnnotation(final Class<A> clazz) {
        Objects.requireNonNull(clazz);
        return this.platformEventType.getAnnotation(clazz);
    }
    
    public static EventType getEventType(final Class<? extends Event> clazz) {
        Objects.requireNonNull(clazz);
        Utils.ensureValidEventSubclass(clazz);
        JVMSupport.ensureWithInternalError();
        return MetadataRepository.getInstance().getEventType(clazz);
    }
    
    public List<SettingDescriptor> getSettingDescriptors() {
        return Collections.unmodifiableList((List<? extends SettingDescriptor>)this.platformEventType.getSettings());
    }
    
    public List<String> getCategoryNames() {
        final Category category = this.platformEventType.getAnnotation((Class<? extends Annotation>)Category.class);
        if (category == null) {
            return this.UNCATEGORIZED;
        }
        return (List<String>)Collections.unmodifiableList((List<?>)Arrays.asList((T[])category.value()));
    }
    
    Type getType() {
        return this.platformEventType;
    }
    
    PlatformEventType getPlatformEventType() {
        return this.platformEventType;
    }
}
