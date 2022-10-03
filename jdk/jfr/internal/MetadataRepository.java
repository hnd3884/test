package jdk.jfr.internal;

import java.util.HashSet;
import java.io.IOException;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.AnnotationElement;
import java.util.Collections;
import jdk.jfr.Event;
import java.util.Collection;
import jdk.jfr.internal.handlers.EventHandler;
import java.util.Iterator;
import jdk.jfr.Period;
import jdk.jfr.StackTrace;
import jdk.jfr.Threshold;
import java.util.ArrayList;
import jdk.jfr.EventType;
import java.util.List;

public final class MetadataRepository
{
    private static final JVM jvm;
    private static final MetadataRepository instace;
    private final List<EventType> nativeEventTypes;
    private final List<EventControl> nativeControls;
    private final TypeLibrary typeLibrary;
    private final SettingsManager settingsManager;
    private boolean staleMetadata;
    private boolean unregistered;
    private long lastUnloaded;
    
    public MetadataRepository() {
        this.nativeEventTypes = new ArrayList<EventType>(100);
        this.nativeControls = new ArrayList<EventControl>(100);
        this.typeLibrary = TypeLibrary.getInstance();
        this.settingsManager = new SettingsManager();
        this.staleMetadata = true;
        this.lastUnloaded = -1L;
        this.initializeJVMEventTypes();
    }
    
    private void initializeJVMEventTypes() {
        final ArrayList list = new ArrayList();
        for (final Type type : this.typeLibrary.getTypes()) {
            if (type instanceof PlatformEventType) {
                final PlatformEventType platformEventType = (PlatformEventType)type;
                final EventType eventType = PrivateAccess.getInstance().newEventType(platformEventType);
                platformEventType.setHasDuration(eventType.getAnnotation(Threshold.class) != null);
                platformEventType.setHasStackTrace(eventType.getAnnotation(StackTrace.class) != null);
                platformEventType.setHasCutoff(eventType.getAnnotation(Cutoff.class) != null);
                platformEventType.setHasPeriod(eventType.getAnnotation(Period.class) != null);
                if (platformEventType.hasPeriod()) {
                    platformEventType.setEventHook(true);
                    if (!"jdk.ExecutionSample".equals(type.getName())) {
                        list.add(new RequestEngine.RequestHook(platformEventType));
                    }
                }
                this.nativeControls.add(new EventControl(platformEventType));
                this.nativeEventTypes.add(eventType);
            }
        }
        RequestEngine.addHooks(list);
    }
    
    public static MetadataRepository getInstance() {
        return MetadataRepository.instace;
    }
    
    public synchronized List<EventType> getRegisteredEventTypes() {
        final List<EventHandler> eventHandlers = getEventHandlers();
        final ArrayList list = new ArrayList(eventHandlers.size() + this.nativeEventTypes.size());
        for (final EventHandler eventHandler : eventHandlers) {
            if (eventHandler.isRegistered()) {
                list.add((Object)eventHandler.getEventType());
            }
        }
        list.addAll((Collection)this.nativeEventTypes);
        return (List<EventType>)list;
    }
    
    public synchronized EventType getEventType(final Class<? extends Event> clazz) {
        final EventHandler handler = this.getHandler(clazz);
        if (handler != null && handler.isRegistered()) {
            return handler.getEventType();
        }
        throw new IllegalStateException("Event class " + clazz.getName() + " is not registered");
    }
    
    public synchronized void unregister(final Class<? extends Event> clazz) {
        Utils.checkRegisterPermission();
        final EventHandler handler = this.getHandler(clazz);
        if (handler != null) {
            handler.setRegistered(false);
        }
    }
    
    public synchronized EventType register(final Class<? extends Event> clazz) {
        return this.register(clazz, Collections.emptyList(), Collections.emptyList());
    }
    
    public synchronized EventType register(final Class<? extends Event> clazz, final List<AnnotationElement> list, final List<ValueDescriptor> list2) {
        Utils.checkRegisterPermission();
        EventHandler eventHandler = this.getHandler(clazz);
        if (eventHandler == null) {
            eventHandler = this.makeHandler(clazz, list, list2);
        }
        eventHandler.setRegistered(true);
        this.typeLibrary.addType(eventHandler.getPlatformEventType());
        if (MetadataRepository.jvm.isRecording()) {
            this.storeDescriptorInJVM();
            this.settingsManager.setEventControl(eventHandler.getEventControl());
            this.settingsManager.updateRetransform(Collections.singletonList(clazz));
        }
        else {
            this.setStaleMetadata();
        }
        return eventHandler.getEventType();
    }
    
    private EventHandler getHandler(final Class<? extends Event> clazz) {
        Utils.ensureValidEventSubclass(clazz);
        SecuritySupport.makeVisibleToJFR(clazz);
        Utils.ensureInitialized(clazz);
        return Utils.getHandler(clazz);
    }
    
    private EventHandler makeHandler(final Class<? extends Event> clazz, final List<AnnotationElement> list, final List<ValueDescriptor> list2) throws InternalError {
        SecuritySupport.addHandlerExport(clazz);
        final PlatformEventType platformEventType = (PlatformEventType)TypeLibrary.createType(clazz, list, list2);
        final EventType eventType = PrivateAccess.getInstance().newEventType(platformEventType);
        final EventControl eventControl = new EventControl(platformEventType, clazz);
        Class<? extends EventHandler> clazz2;
        try {
            clazz2 = Class.forName(EventHandlerCreator.makeEventHandlerName(eventType.getId()), false, Event.class.getClassLoader()).asSubclass(EventHandler.class);
            platformEventType.setInstrumented();
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.DEBUG, "Found existing event handler for " + eventType.getName());
        }
        catch (final ClassNotFoundException ex) {
            clazz2 = new EventHandlerCreator(eventType.getId(), eventControl.getSettingInfos(), eventType, clazz).makeEventHandlerClass();
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.DEBUG, "Created event handler for " + eventType.getName());
        }
        final EventHandler instantiateEventHandler = EventHandlerCreator.instantiateEventHandler(clazz2, true, eventType, eventControl);
        Utils.setHandler(clazz, instantiateEventHandler);
        return instantiateEventHandler;
    }
    
    public synchronized void setSettings(final List<Map<String, String>> settings) {
        this.settingsManager.setSettings(settings);
    }
    
    synchronized void disableEvents() {
        final Iterator<EventControl> iterator = this.getEventControls().iterator();
        while (iterator.hasNext()) {
            iterator.next().disable();
        }
    }
    
    public synchronized List<EventControl> getEventControls() {
        final ArrayList list = new ArrayList();
        list.addAll(this.nativeControls);
        final Iterator<EventHandler> iterator = getEventHandlers().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getEventControl());
        }
        return list;
    }
    
    private void storeDescriptorInJVM() throws InternalError {
        MetadataRepository.jvm.storeMetadataDescriptor(this.getBinaryRepresentation());
        this.staleMetadata = false;
    }
    
    private static List<EventHandler> getEventHandlers() {
        final List<Class<? extends Event>> allEventClasses = MetadataRepository.jvm.getAllEventClasses();
        final ArrayList list = new ArrayList(allEventClasses.size());
        final Iterator iterator = allEventClasses.iterator();
        while (iterator.hasNext()) {
            final EventHandler handler = Utils.getHandler((Class<? extends Event>)iterator.next());
            if (handler != null) {
                list.add((Object)handler);
            }
        }
        return (List<EventHandler>)list;
    }
    
    private byte[] getBinaryRepresentation() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(40000);
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            final List<Type> types = this.typeLibrary.getTypes();
            Collections.sort((List<Comparable>)types);
            MetadataDescriptor.write(types, dataOutputStream);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException ex) {
            throw new InternalError(ex);
        }
    }
    
    synchronized boolean isEnabled(final String s) {
        return this.settingsManager.isEnabled(s);
    }
    
    synchronized void setStaleMetadata() {
        this.staleMetadata = true;
    }
    
    synchronized void setOutput(final String output) {
        MetadataRepository.jvm.setOutput(output);
        this.unregisterUnloaded();
        if (this.unregistered) {
            this.staleMetadata = this.typeLibrary.clearUnregistered();
            this.unregistered = false;
        }
        if (this.staleMetadata) {
            this.storeDescriptorInJVM();
        }
    }
    
    private void unregisterUnloaded() {
        final long unloadedEventClassCount = MetadataRepository.jvm.getUnloadedEventClassCount();
        if (this.lastUnloaded != unloadedEventClassCount) {
            this.lastUnloaded = unloadedEventClassCount;
            final List<Class<? extends Event>> allEventClasses = MetadataRepository.jvm.getAllEventClasses();
            final HashSet set = new HashSet<Long>(allEventClasses.size());
            final Iterator iterator = allEventClasses.iterator();
            while (iterator.hasNext()) {
                set.add(Type.getTypeId((Class<?>)iterator.next()));
            }
            for (final Type type : this.typeLibrary.getTypes()) {
                if (type instanceof PlatformEventType && !set.contains(type.getId())) {
                    final PlatformEventType platformEventType = (PlatformEventType)type;
                    if (platformEventType.isJVM()) {
                        continue;
                    }
                    platformEventType.setRegistered(false);
                }
            }
        }
    }
    
    public synchronized void setUnregistered() {
        this.unregistered = true;
    }
    
    static {
        jvm = JVM.getJVM();
        instace = new MetadataRepository();
    }
}
