package jdk.jfr.internal;

import java.util.Set;
import jdk.jfr.events.ActiveSettingEvent;
import jdk.jfr.internal.settings.PeriodSetting;
import jdk.jfr.internal.settings.CutoffSetting;
import jdk.jfr.internal.settings.StackTraceSetting;
import jdk.jfr.internal.settings.ThresholdSetting;
import jdk.jfr.internal.settings.EnabledSetting;
import java.util.Collections;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jdk.jfr.Name;
import jdk.jfr.SettingControl;
import jdk.jfr.SettingDefinition;
import java.lang.reflect.Modifier;
import jdk.jfr.Event;
import java.util.Iterator;
import jdk.jfr.StackTrace;
import jdk.jfr.Enabled;
import jdk.jfr.Period;
import java.lang.annotation.Annotation;
import jdk.jfr.AnnotationElement;
import jdk.jfr.Threshold;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public final class EventControl
{
    static final String FIELD_SETTING_PREFIX = "setting";
    private static final Type TYPE_ENABLED;
    private static final Type TYPE_THRESHOLD;
    private static final Type TYPE_STACK_TRACE;
    private static final Type TYPE_PERIOD;
    private static final Type TYPE_CUTOFF;
    private final List<EventInstrumentation.SettingInfo> settingInfos;
    private final Map<String, Control> eventControls;
    private final PlatformEventType type;
    private final String idName;
    
    EventControl(final PlatformEventType type) {
        this.settingInfos = new ArrayList<EventInstrumentation.SettingInfo>();
        (this.eventControls = new HashMap<String, Control>(5)).put("enabled", defineEnabled(type));
        if (type.hasDuration()) {
            this.eventControls.put("threshold", defineThreshold(type));
        }
        if (type.hasStackTrace()) {
            this.eventControls.put("stackTrace", defineStackTrace(type));
        }
        if (type.hasPeriod()) {
            this.eventControls.put("period", definePeriod(type));
        }
        if (type.hasCutoff()) {
            this.eventControls.put("cutoff", defineCutoff(type));
        }
        final ArrayList annotations = new ArrayList((Collection<? extends E>)type.getAnnotationElements());
        remove(type, annotations, Threshold.class);
        remove(type, annotations, Period.class);
        remove(type, annotations, (Class<? extends Annotation>)Enabled.class);
        remove(type, annotations, (Class<? extends Annotation>)StackTrace.class);
        remove(type, annotations, Cutoff.class);
        annotations.trimToSize();
        type.setAnnotations(annotations);
        this.type = type;
        this.idName = String.valueOf(type.getId());
    }
    
    static void remove(final PlatformEventType platformEventType, final List<AnnotationElement> list, final Class<? extends Annotation> clazz) {
        final long typeId = Type.getTypeId(clazz);
        for (final AnnotationElement annotationElement : platformEventType.getAnnotationElements()) {
            if (annotationElement.getTypeId() == typeId && annotationElement.getTypeName().equals(clazz.getName())) {
                list.remove(annotationElement);
            }
        }
    }
    
    EventControl(final PlatformEventType platformEventType, final Class<? extends Event> clazz) {
        this(platformEventType);
        this.defineSettings(clazz);
    }
    
    private void defineSettings(Class<?> superclass) {
        int n = 1;
        while (superclass != null) {
            for (final Method method : superclass.getDeclaredMethods()) {
                final boolean private1 = Modifier.isPrivate(method.getModifiers());
                if (method.getReturnType() == Boolean.TYPE && method.getParameterCount() == 1 && (!private1 || n != 0) && method.getDeclaredAnnotation(SettingDefinition.class) != null) {
                    final Class<?> type = method.getParameters()[0].getType();
                    if (!Modifier.isAbstract(type.getModifiers()) && SettingControl.class.isAssignableFrom(type)) {
                        String s = method.getName();
                        final Name name = method.getAnnotation(Name.class);
                        if (name != null) {
                            s = name.value();
                        }
                        if (!this.eventControls.containsKey(s)) {
                            this.defineSetting((Class<? extends SettingControl>)type, method, this.type, s);
                        }
                    }
                }
            }
            superclass = superclass.getSuperclass();
            n = 0;
        }
    }
    
    private void defineSetting(final Class<? extends SettingControl> clazz, final Method method, final PlatformEventType platformEventType, final String s) {
        try {
            final int size = this.settingInfos.size();
            final EventInstrumentation.SettingInfo settingInfo = new EventInstrumentation.SettingInfo("setting" + size, size);
            settingInfo.settingControl = this.instantiateSettingControl(clazz);
            final SettingControl settingControl = settingInfo.settingControl;
            settingControl.setDefault();
            final String valueSafe = settingControl.getValueSafe();
            if (valueSafe != null) {
                final Type type = TypeLibrary.createType(clazz);
                final ArrayList list = new ArrayList();
                final Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
                for (int length = declaredAnnotations.length, i = 0; i < length; ++i) {
                    final AnnotationElement annotation = TypeLibrary.createAnnotation(declaredAnnotations[i]);
                    if (annotation != null) {
                        list.add(annotation);
                    }
                }
                list.trimToSize();
                this.eventControls.put(s, settingInfo.settingControl);
                platformEventType.add(PrivateAccess.getInstance().newSettingDescriptor(type, s, valueSafe, list));
                this.settingInfos.add(settingInfo);
            }
        }
        catch (final InstantiationException ex) {
            throw new InstantiationError("Could not instantiate setting " + clazz.getName() + " for event " + platformEventType.getLogName() + ". " + ex.getMessage());
        }
        catch (final IllegalAccessException ex2) {
            throw new IllegalAccessError("Could not access setting " + clazz.getName() + " for event " + platformEventType.getLogName() + ". " + ex2.getMessage());
        }
    }
    
    private SettingControl instantiateSettingControl(final Class<? extends SettingControl> clazz) throws IllegalAccessException, InstantiationException {
        SecuritySupport.makeVisibleToJFR(clazz);
        Constructor<?> accessible;
        try {
            accessible = clazz.getDeclaredConstructors()[0];
        }
        catch (final Exception ex) {
            throw (Error)new InternalError("Could not get constructor for " + clazz.getName()).initCause(ex);
        }
        SecuritySupport.setAccessible(accessible);
        try {
            return (SettingControl)accessible.newInstance(new Object[0]);
        }
        catch (final IllegalArgumentException | InvocationTargetException ex2) {
            throw new InternalError("Could not instantiate setting for class " + clazz.getName());
        }
    }
    
    private static Control defineEnabled(final PlatformEventType platformEventType) {
        final Enabled enabled = platformEventType.getAnnotation((Class<? extends Annotation>)Enabled.class);
        String string = platformEventType.isJVM() ? "false" : "true";
        if (enabled != null) {
            string = Boolean.toString(enabled.value());
        }
        platformEventType.add(PrivateAccess.getInstance().newSettingDescriptor(EventControl.TYPE_ENABLED, "enabled", string, Collections.emptyList()));
        return new EnabledSetting(platformEventType, string);
    }
    
    private static Control defineThreshold(final PlatformEventType platformEventType) {
        final Threshold threshold = platformEventType.getAnnotation(Threshold.class);
        String value = "0 ns";
        if (threshold != null) {
            value = threshold.value();
        }
        platformEventType.add(PrivateAccess.getInstance().newSettingDescriptor(EventControl.TYPE_THRESHOLD, "threshold", value, Collections.emptyList()));
        return new ThresholdSetting(platformEventType, value);
    }
    
    private static Control defineStackTrace(final PlatformEventType platformEventType) {
        final StackTrace stackTrace = platformEventType.getAnnotation((Class<? extends Annotation>)StackTrace.class);
        String string = "true";
        if (stackTrace != null) {
            string = Boolean.toString(stackTrace.value());
        }
        platformEventType.add(PrivateAccess.getInstance().newSettingDescriptor(EventControl.TYPE_STACK_TRACE, "stackTrace", string, Collections.emptyList()));
        return new StackTraceSetting(platformEventType, string);
    }
    
    private static Control defineCutoff(final PlatformEventType platformEventType) {
        final Cutoff cutoff = platformEventType.getAnnotation(Cutoff.class);
        String value = "infinity";
        if (cutoff != null) {
            value = cutoff.value();
        }
        platformEventType.add(PrivateAccess.getInstance().newSettingDescriptor(EventControl.TYPE_CUTOFF, "cutoff", value, Collections.emptyList()));
        return new CutoffSetting(platformEventType, value);
    }
    
    private static Control definePeriod(final PlatformEventType platformEventType) {
        final Period period = platformEventType.getAnnotation(Period.class);
        String value = "everyChunk";
        if (period != null) {
            value = period.value();
        }
        platformEventType.add(PrivateAccess.getInstance().newSettingDescriptor(EventControl.TYPE_PERIOD, "period", value, Collections.emptyList()));
        return new PeriodSetting(platformEventType, value);
    }
    
    void disable() {
        for (final Control control : this.eventControls.values()) {
            if (control instanceof EnabledSetting) {
                control.setValueSafe("false");
            }
        }
    }
    
    void writeActiveSettingEvent() {
        if (!this.type.isRegistered()) {
            return;
        }
        for (final Map.Entry entry : this.eventControls.entrySet()) {
            final Control control = (Control)entry.getValue();
            if (Utils.isSettingVisible(control, this.type.hasEventHook())) {
                String value = control.getLastValue();
                if (value == null) {
                    value = control.getDefaultValue();
                }
                final ActiveSettingEvent activeSettingEvent = new ActiveSettingEvent();
                activeSettingEvent.id = this.type.getId();
                activeSettingEvent.name = (String)entry.getKey();
                activeSettingEvent.value = value;
                activeSettingEvent.commit();
            }
        }
    }
    
    public Set<Map.Entry<String, Control>> getEntries() {
        return this.eventControls.entrySet();
    }
    
    public PlatformEventType getEventType() {
        return this.type;
    }
    
    public String getSettingsId() {
        return this.idName;
    }
    
    public List<EventInstrumentation.SettingInfo> getSettingInfos() {
        return this.settingInfos;
    }
    
    static {
        TYPE_ENABLED = TypeLibrary.createType(EnabledSetting.class);
        TYPE_THRESHOLD = TypeLibrary.createType(ThresholdSetting.class);
        TYPE_STACK_TRACE = TypeLibrary.createType(StackTraceSetting.class);
        TYPE_PERIOD = TypeLibrary.createType(PeriodSetting.class);
        TYPE_CUTOFF = TypeLibrary.createType(CutoffSetting.class);
    }
}
