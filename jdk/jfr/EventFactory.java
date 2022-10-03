package jdk.jfr;

import java.util.Iterator;
import jdk.jfr.internal.MetadataRepository;
import jdk.jfr.internal.EventClassBuilder;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import jdk.jfr.internal.Type;
import java.util.HashSet;
import jdk.jfr.internal.Utils;
import jdk.jfr.internal.JVMSupport;
import java.util.Objects;
import java.lang.reflect.Constructor;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.lang.invoke.MethodHandle;
import jdk.Exported;

@Exported
public final class EventFactory
{
    private static final long REGISTERED_ID;
    private final Class<? extends Event> eventClass;
    private final MethodHandle constructorHandle;
    private final List<AnnotationElement> sanitizedAnnotation;
    private final List<ValueDescriptor> sanitizedFields;
    
    private EventFactory(final Class<? extends Event> eventClass, final List<AnnotationElement> sanitizedAnnotation, final List<ValueDescriptor> sanitizedFields) throws IllegalAccessException, NoSuchMethodException, SecurityException {
        this.constructorHandle = MethodHandles.lookup().unreflectConstructor(eventClass.getConstructor((Class<?>[])new Class[0]));
        this.eventClass = eventClass;
        this.sanitizedAnnotation = sanitizedAnnotation;
        this.sanitizedFields = sanitizedFields;
    }
    
    public static EventFactory create(final List<AnnotationElement> list, final List<ValueDescriptor> list2) {
        Objects.requireNonNull(list2);
        Objects.requireNonNull(list);
        JVMSupport.ensureWithInternalError();
        Utils.checkRegisterPermission();
        final List<AnnotationElement> sanitizeNullFreeList = Utils.sanitizeNullFreeList(list, AnnotationElement.class);
        final List<ValueDescriptor> sanitizeNullFreeList2 = Utils.sanitizeNullFreeList(list2, ValueDescriptor.class);
        final HashSet set = new HashSet();
        for (final ValueDescriptor valueDescriptor : sanitizeNullFreeList2) {
            final String name = valueDescriptor.getName();
            if (valueDescriptor.isArray()) {
                throw new IllegalArgumentException("Array types are not allowed for fields");
            }
            if (!Type.isValidJavaFieldType(valueDescriptor.getTypeName())) {
                throw new IllegalArgumentException(valueDescriptor.getTypeName() + " is not a valid type for an event field");
            }
            if (!Type.isValidJavaIdentifier(valueDescriptor.getName())) {
                throw new IllegalArgumentException(name + " is not a valid name for an event field");
            }
            if (set.contains(name)) {
                throw new IllegalArgumentException("Name of fields must be unique. Found two instances of " + name);
            }
            set.add(name);
        }
        boolean b = true;
        final ArrayList list3 = new ArrayList();
        for (final AnnotationElement annotationElement : sanitizeNullFreeList) {
            final long typeId = annotationElement.getTypeId();
            if (annotationElement.isInBoot()) {
                if (typeId == EventFactory.REGISTERED_ID) {
                    if (!Boolean.FALSE.equals(annotationElement.getValue("value"))) {
                        continue;
                    }
                    b = false;
                }
                else {
                    list3.add(annotationElement);
                }
            }
        }
        list3.add(new AnnotationElement((Class<? extends Annotation>)Registered.class, false));
        final Class<? extends Event> build = new EventClassBuilder(list3, sanitizeNullFreeList2).build();
        if (b) {
            MetadataRepository.getInstance().register(build, sanitizeNullFreeList, sanitizeNullFreeList2);
        }
        try {
            return new EventFactory(build, sanitizeNullFreeList, sanitizeNullFreeList2);
        }
        catch (final IllegalAccessException ex) {
            throw new IllegalAccessError("Could not accees constructor of generated event handler, " + ex.getMessage());
        }
        catch (final NoSuchMethodException ex2) {
            throw new InternalError("Could not find constructor in generated event handler, " + ex2.getMessage());
        }
    }
    
    public Event newEvent() {
        try {
            return this.constructorHandle.invoke();
        }
        catch (final Throwable t) {
            throw new InstantiationError("Could not instantaite dynamically generated event class " + this.eventClass.getName() + ". " + t.getMessage());
        }
    }
    
    public EventType getEventType() {
        return EventType.getEventType(this.eventClass);
    }
    
    public void register() {
        MetadataRepository.getInstance().register(this.eventClass, this.sanitizedAnnotation, this.sanitizedFields);
    }
    
    public void unregister() {
        MetadataRepository.getInstance().unregister(this.eventClass);
    }
    
    static {
        REGISTERED_ID = Type.getTypeId(Registered.class);
    }
}
