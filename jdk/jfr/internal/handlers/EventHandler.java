package jdk.jfr.internal.handlers;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import jdk.jfr.internal.JVM;
import jdk.jfr.internal.StringPool;
import jdk.jfr.internal.PrivateAccess;
import jdk.jfr.internal.EventControl;
import jdk.jfr.EventType;
import jdk.jfr.internal.PlatformEventType;

public abstract class EventHandler
{
    protected final PlatformEventType platformEventType;
    private final EventType eventType;
    private final EventControl eventControl;
    
    protected EventHandler(final boolean registered, final EventType eventType, final EventControl eventControl) {
        if (System.getSecurityManager() != null && EventHandler.class.getClassLoader() != this.getClass().getClassLoader()) {
            throw new SecurityException("Illegal subclass");
        }
        this.eventType = eventType;
        this.platformEventType = PrivateAccess.getInstance().getPlatformEventType(eventType);
        this.eventControl = eventControl;
        this.platformEventType.setRegistered(registered);
    }
    
    protected final StringPool createStringFieldWriter() {
        return new StringPool();
    }
    
    public final boolean shouldCommit(final long n) {
        return this.isEnabled() && n >= this.platformEventType.getThresholdTicks();
    }
    
    public final boolean isEnabled() {
        return this.platformEventType.isCommitable();
    }
    
    public final EventType getEventType() {
        return this.eventType;
    }
    
    public final PlatformEventType getPlatformEventType() {
        return this.platformEventType;
    }
    
    public final EventControl getEventControl() {
        return this.eventControl;
    }
    
    public static long timestamp() {
        return JVM.counterTime();
    }
    
    public static long duration(final long n) {
        if (n == 0L) {
            return 0L;
        }
        return timestamp() - n;
    }
    
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    private final void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        throw new IOException("Object cannot be serialized");
    }
    
    private final void readObject(final ObjectInputStream objectInputStream) throws IOException {
        throw new IOException("Class cannot be deserialized");
    }
    
    public boolean isRegistered() {
        return this.platformEventType.isRegistered();
    }
    
    public boolean setRegistered(final boolean registered) {
        return this.platformEventType.setRegistered(registered);
    }
}
