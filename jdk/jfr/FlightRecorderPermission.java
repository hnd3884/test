package jdk.jfr;

import jdk.jfr.internal.PlatformRecorder;
import java.util.Collections;
import jdk.jfr.internal.PlatformRecording;
import java.util.List;
import jdk.jfr.internal.PlatformEventType;
import java.util.Map;
import jdk.jfr.internal.Type;
import jdk.jfr.internal.PrivateAccess;
import java.util.Objects;
import jdk.Exported;
import java.security.BasicPermission;

@Exported
public final class FlightRecorderPermission extends BasicPermission
{
    public FlightRecorderPermission(final String s) {
        super(Objects.requireNonNull(s));
        if (!s.equals("accessFlightRecorder") && !s.equals("registerEvent")) {
            throw new IllegalArgumentException("name: " + s);
        }
    }
    
    static {
        PrivateAccess.setPrivateAccess(new InternalAccess());
    }
    
    private static final class InternalAccess extends PrivateAccess
    {
        @Override
        public Type getType(final Object o) {
            if (o instanceof AnnotationElement) {
                return ((AnnotationElement)o).getType();
            }
            if (o instanceof EventType) {
                return ((EventType)o).getType();
            }
            if (o instanceof ValueDescriptor) {
                return ((ValueDescriptor)o).getType();
            }
            if (o instanceof SettingDescriptor) {
                return ((SettingDescriptor)o).getType();
            }
            throw new Error("Unknown type " + o.getClass());
        }
        
        @Override
        public Configuration newConfiguration(final String s, final String s2, final String s3, final String s4, final Map<String, String> map, final String s5) {
            return new Configuration(s, s2, s3, s4, map, s5);
        }
        
        @Override
        public EventType newEventType(final PlatformEventType platformEventType) {
            return new EventType(platformEventType);
        }
        
        @Override
        public AnnotationElement newAnnotation(final Type type, final List<Object> list, final boolean b) {
            return new AnnotationElement(type, list, b);
        }
        
        @Override
        public ValueDescriptor newValueDescriptor(final String s, final Type type, final List<AnnotationElement> list, final int n, final boolean b, final String s2) {
            return new ValueDescriptor(type, s, list, n, b, s2);
        }
        
        @Override
        public PlatformRecording getPlatformRecording(final Recording recording) {
            return recording.getInternal();
        }
        
        @Override
        public PlatformEventType getPlatformEventType(final EventType eventType) {
            return eventType.getPlatformEventType();
        }
        
        @Override
        public boolean isConstantPool(final ValueDescriptor valueDescriptor) {
            return valueDescriptor.isConstantPool();
        }
        
        @Override
        public void setAnnotations(final ValueDescriptor valueDescriptor, final List<AnnotationElement> annotations) {
            valueDescriptor.setAnnotations(annotations);
        }
        
        @Override
        public void setAnnotations(final SettingDescriptor settingDescriptor, final List<AnnotationElement> annotations) {
            settingDescriptor.setAnnotations(annotations);
        }
        
        @Override
        public String getFieldName(final ValueDescriptor valueDescriptor) {
            return valueDescriptor.getJavaFieldName();
        }
        
        @Override
        public ValueDescriptor newValueDescriptor(final Class<?> clazz, final String s) {
            return new ValueDescriptor(clazz, s, Collections.emptyList(), true);
        }
        
        @Override
        public SettingDescriptor newSettingDescriptor(final Type type, final String s, final String s2, final List<AnnotationElement> list) {
            return new SettingDescriptor(type, s, s2, list);
        }
        
        @Override
        public boolean isUnsigned(final ValueDescriptor valueDescriptor) {
            return valueDescriptor.isUnsigned();
        }
        
        @Override
        public PlatformRecorder getPlatformRecorder() {
            return FlightRecorder.getFlightRecorder().getInternal();
        }
    }
}
