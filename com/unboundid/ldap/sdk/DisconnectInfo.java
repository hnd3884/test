package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class DisconnectInfo
{
    private final AtomicBoolean handlerNotified;
    private final DisconnectType type;
    private final int port;
    private final LDAPConnection connection;
    private final String host;
    private final String message;
    private final Throwable cause;
    
    DisconnectInfo(final LDAPConnection connection, final DisconnectType type, final String message, final Throwable cause) {
        Validator.ensureNotNull(connection);
        Validator.ensureNotNull(type);
        this.connection = connection;
        this.type = type;
        this.message = message;
        this.cause = cause;
        this.handlerNotified = new AtomicBoolean(false);
        this.host = connection.getConnectedAddress();
        this.port = connection.getConnectedPort();
    }
    
    DisconnectType getType() {
        return this.type;
    }
    
    String getMessage() {
        return this.message;
    }
    
    Throwable getCause() {
        return this.cause;
    }
    
    void notifyDisconnectHandler() {
        final boolean alreadyNotified = this.handlerNotified.getAndSet(true);
        if (alreadyNotified) {
            return;
        }
        final ServerSet serverSet = this.connection.getServerSet();
        if (serverSet != null) {
            serverSet.handleConnectionClosed(this.connection, this.host, this.port, this.type, this.message, this.cause);
        }
        final DisconnectHandler handler = this.connection.getConnectionOptions().getDisconnectHandler();
        if (handler != null) {
            handler.handleDisconnect(this.connection, this.host, this.port, this.type, this.message, this.cause);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    void toString(final StringBuilder buffer) {
        buffer.append("DisconnectInfo(type=");
        buffer.append(this.type.name());
        if (this.message != null) {
            buffer.append(", message='");
            buffer.append(this.message);
            buffer.append('\'');
        }
        if (this.cause != null) {
            buffer.append(", cause=");
            buffer.append(StaticUtils.getExceptionMessage(this.cause));
        }
        buffer.append(')');
    }
}
