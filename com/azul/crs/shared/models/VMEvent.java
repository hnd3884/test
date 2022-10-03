package com.azul.crs.shared.models;

import java.util.Objects;
import java.util.UUID;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VMEvent<T> extends Payload
{
    private String vmId;
    private String eventId;
    private Type eventType;
    private Long eventTime;
    private T eventPayload;
    
    public String getVmId() {
        return this.vmId;
    }
    
    public String getEventId() {
        return this.eventId;
    }
    
    public Type getEventType() {
        return this.eventType;
    }
    
    public Long getEventTime() {
        return this.eventTime;
    }
    
    public T getEventPayload() {
        return this.eventPayload;
    }
    
    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }
    
    public void setEventId(final String eventId) {
        this.eventId = eventId;
    }
    
    public void setEventTime(final Long eventTime) {
        this.eventTime = eventTime;
    }
    
    public void setEventType(final Type eventType) {
        if (this.eventPayload != null && !eventType.payloadClass().isAssignableFrom(this.eventPayload.getClass())) {
            throw new IllegalArgumentException("Event type inconsistent with event payload type");
        }
        this.eventType = eventType;
    }
    
    public void setEventPayload(final T eventPayload) {
        if (this.eventType != null && !this.eventType.payloadClass().isAssignableFrom(eventPayload.getClass())) {
            if (eventPayload instanceof Map) {
                try {
                    this.eventPayload = Payload.objectMapper().convertValue(eventPayload, (Class<T>)this.eventType.payloadClass());
                    return;
                }
                catch (final IllegalArgumentException ex) {}
            }
            throw new IllegalArgumentException("Event payload type inconsistent with event type ");
        }
        this.eventPayload = eventPayload;
    }
    
    public VMEvent<T> vmId(final String vmId) {
        this.setVmId(vmId);
        return this;
    }
    
    public VMEvent<T> eventId(final String eventId) {
        this.setEventId(eventId);
        return this;
    }
    
    public VMEvent<T> randomEventId() {
        this.setEventId(UUID.randomUUID().toString());
        return this;
    }
    
    public VMEvent<T> eventType(final Type eventType) {
        this.setEventType(eventType);
        return this;
    }
    
    public VMEvent<T> eventType(final String eventType) {
        this.setEventType(Type.valueOf(eventType));
        return this;
    }
    
    public VMEvent<T> eventTime(final Long eventTime) {
        this.setEventTime(eventTime);
        return this;
    }
    
    public VMEvent<T> eventPayload(final T eventPayload) {
        this.setEventPayload(eventPayload);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final VMEvent<?> vmEvent = (VMEvent<?>)o;
        return Objects.equals(this.vmId, vmEvent.vmId) && Objects.equals(this.eventId, vmEvent.eventId) && this.eventType == vmEvent.eventType && Objects.equals(this.eventTime, vmEvent.eventTime) && Objects.equals(this.eventPayload, vmEvent.eventPayload);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.vmId, this.eventId, this.eventType, this.eventTime, this.eventPayload);
    }
    
    @Override
    public String toString() {
        return "vmId=" + this.vmId + ", eventId=" + this.eventId + ", eventType=" + this.eventType + ", eventTime=" + this.eventTime + ", eventPayload=" + this.eventPayload;
    }
    
    public enum Type
    {
        VM_CREATE((Class)VMInstance.class), 
        VM_PATCH((Class)VMInstance.class), 
        VM_ARTIFACT_CREATE((Class)VMArtifact.class), 
        VM_ARTIFACT_PATCH((Class)VMArtifact.class), 
        VM_ARTIFACT_DATA((Class)Map.class), 
        VM_HEARTBEAT((Class)Void.class), 
        VM_SHUTDOWN((Class)Void.class), 
        VM_CLASS_LOADED((Class)Map.class), 
        VM_METHOD_FIRST_CALLED((Class)Map.class), 
        VM_PERFORMANCE_METRICS((Class)Map.class);
        
        private final Class payloadClass;
        
        private Type(final Class payloadClass) {
            this.payloadClass = payloadClass;
        }
        
        public Class payloadClass() {
            return this.payloadClass;
        }
        
        public static Type eventType(final String type) {
            return (type != null) ? valueOf(type.toUpperCase()) : null;
        }
    }
}
