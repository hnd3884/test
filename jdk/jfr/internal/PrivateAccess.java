package jdk.jfr.internal;

import jdk.jfr.SettingDescriptor;
import jdk.jfr.Recording;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.AnnotationElement;
import java.util.List;
import jdk.jfr.EventType;
import jdk.jfr.Configuration;
import java.util.Map;
import jdk.jfr.FlightRecorderPermission;

public abstract class PrivateAccess
{
    private static volatile PrivateAccess instance;
    
    public static PrivateAccess getInstance() {
        if (PrivateAccess.instance == null) {
            new FlightRecorderPermission("registerEvent");
        }
        return PrivateAccess.instance;
    }
    
    public static void setPrivateAccess(final PrivateAccess instance) {
        PrivateAccess.instance = instance;
    }
    
    public abstract Type getType(final Object p0);
    
    public abstract Configuration newConfiguration(final String p0, final String p1, final String p2, final String p3, final Map<String, String> p4, final String p5);
    
    public abstract EventType newEventType(final PlatformEventType p0);
    
    public abstract AnnotationElement newAnnotation(final Type p0, final List<Object> p1, final boolean p2);
    
    public abstract ValueDescriptor newValueDescriptor(final String p0, final Type p1, final List<AnnotationElement> p2, final int p3, final boolean p4, final String p5);
    
    public abstract PlatformRecording getPlatformRecording(final Recording p0);
    
    public abstract PlatformEventType getPlatformEventType(final EventType p0);
    
    public abstract boolean isConstantPool(final ValueDescriptor p0);
    
    public abstract String getFieldName(final ValueDescriptor p0);
    
    public abstract ValueDescriptor newValueDescriptor(final Class<?> p0, final String p1);
    
    public abstract SettingDescriptor newSettingDescriptor(final Type p0, final String p1, final String p2, final List<AnnotationElement> p3);
    
    public abstract void setAnnotations(final ValueDescriptor p0, final List<AnnotationElement> p1);
    
    public abstract void setAnnotations(final SettingDescriptor p0, final List<AnnotationElement> p1);
    
    public abstract boolean isUnsigned(final ValueDescriptor p0);
    
    public abstract PlatformRecorder getPlatformRecorder();
}
