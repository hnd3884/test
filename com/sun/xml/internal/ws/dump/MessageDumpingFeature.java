package com.sun.xml.internal.ws.dump;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Queue;
import java.util.logging.Level;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public final class MessageDumpingFeature extends WebServiceFeature
{
    public static final String ID = "com.sun.xml.internal.ws.messagedump.MessageDumpingFeature";
    private static final Level DEFAULT_MSG_LOG_LEVEL;
    private final Queue<String> messageQueue;
    private final AtomicBoolean messageLoggingStatus;
    private final String messageLoggingRoot;
    private final Level messageLoggingLevel;
    
    public MessageDumpingFeature() {
        this(null, null, true);
    }
    
    public MessageDumpingFeature(final String msgLogRoot, final Level msgLogLevel, final boolean storeMessages) {
        this.messageQueue = (storeMessages ? new ConcurrentLinkedQueue<String>() : null);
        this.messageLoggingStatus = new AtomicBoolean(true);
        this.messageLoggingRoot = ((msgLogRoot != null && msgLogRoot.length() > 0) ? msgLogRoot : "com.sun.xml.internal.ws.messagedump");
        this.messageLoggingLevel = ((msgLogLevel != null) ? msgLogLevel : MessageDumpingFeature.DEFAULT_MSG_LOG_LEVEL);
        super.enabled = true;
    }
    
    public MessageDumpingFeature(final boolean enabled) {
        this();
        super.enabled = enabled;
    }
    
    @FeatureConstructor({ "enabled", "messageLoggingRoot", "messageLoggingLevel", "storeMessages" })
    public MessageDumpingFeature(final boolean enabled, final String msgLogRoot, final String msgLogLevel, final boolean storeMessages) {
        this(msgLogRoot, Level.parse(msgLogLevel), storeMessages);
        super.enabled = enabled;
    }
    
    @ManagedAttribute
    @Override
    public String getID() {
        return "com.sun.xml.internal.ws.messagedump.MessageDumpingFeature";
    }
    
    public String nextMessage() {
        return (this.messageQueue != null) ? this.messageQueue.poll() : null;
    }
    
    public void enableMessageLogging() {
        this.messageLoggingStatus.set(true);
    }
    
    public void disableMessageLogging() {
        this.messageLoggingStatus.set(false);
    }
    
    @ManagedAttribute
    public boolean getMessageLoggingStatus() {
        return this.messageLoggingStatus.get();
    }
    
    @ManagedAttribute
    public String getMessageLoggingRoot() {
        return this.messageLoggingRoot;
    }
    
    @ManagedAttribute
    public Level getMessageLoggingLevel() {
        return this.messageLoggingLevel;
    }
    
    boolean offerMessage(final String message) {
        return this.messageQueue != null && this.messageQueue.offer(message);
    }
    
    static {
        DEFAULT_MSG_LOG_LEVEL = Level.FINE;
    }
}
