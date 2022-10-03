package javapns.notification;

import javapns.notification.exceptions.ErrorResponsePacketReceivedException;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import javapns.devices.Device;

public class PushedNotification
{
    private Payload payload;
    private Device device;
    private ResponsePacket response;
    private int identifier;
    private long expiry;
    private int transmissionAttempts;
    private boolean transmissionCompleted;
    private Exception exception;
    
    protected PushedNotification(final Device device, final Payload payload) {
        this.device = device;
        this.payload = payload;
    }
    
    PushedNotification(final Device device, final Payload payload, final int identifier) {
        this.device = device;
        this.payload = payload;
        this.identifier = identifier;
    }
    
    public PushedNotification(final Device device, final Payload payload, final Exception exception) {
        this.device = device;
        this.payload = payload;
        this.exception = exception;
    }
    
    public static List<PushedNotification> findSuccessfulNotifications(final List<PushedNotification> notifications) {
        final List<PushedNotification> filteredList = new Vector<PushedNotification>();
        for (final PushedNotification notification : notifications) {
            if (notification.isSuccessful()) {
                filteredList.add(notification);
            }
        }
        return filteredList;
    }
    
    public static List<PushedNotification> findFailedNotifications(final List<PushedNotification> notifications) {
        final List<PushedNotification> filteredList = new Vector<PushedNotification>();
        for (final PushedNotification notification : notifications) {
            if (!notification.isSuccessful()) {
                filteredList.add(notification);
            }
        }
        return filteredList;
    }
    
    public Payload getPayload() {
        return this.payload;
    }
    
    protected void setPayload(final Payload payload) {
        this.payload = payload;
    }
    
    public Device getDevice() {
        return this.device;
    }
    
    protected void setDevice(final Device device) {
        this.device = device;
    }
    
    public int getIdentifier() {
        return this.identifier;
    }
    
    void setIdentifier(final int identifier) {
        this.identifier = identifier;
    }
    
    public long getExpiry() {
        return this.expiry;
    }
    
    void setExpiry(final long expiry) {
        this.expiry = expiry;
    }
    
    void addTransmissionAttempt() {
        ++this.transmissionAttempts;
    }
    
    public int getTransmissionAttempts() {
        return this.transmissionAttempts;
    }
    
    void setTransmissionAttempts(final int transmissionAttempts) {
        this.transmissionAttempts = transmissionAttempts;
    }
    
    public String getLatestTransmissionAttempt() {
        if (this.transmissionAttempts == 0) {
            return "no attempt yet";
        }
        switch (this.transmissionAttempts) {
            case 1: {
                return "first attempt";
            }
            case 2: {
                return "second attempt";
            }
            case 3: {
                return "third attempt";
            }
            case 4: {
                return "fourth attempt";
            }
            default: {
                return "attempt #" + this.transmissionAttempts;
            }
        }
    }
    
    public boolean isTransmissionCompleted() {
        return this.transmissionCompleted;
    }
    
    void setTransmissionCompleted(final boolean completed) {
        this.transmissionCompleted = completed;
    }
    
    public ResponsePacket getResponse() {
        return this.response;
    }
    
    void setResponse(final ResponsePacket response) {
        this.response = response;
        if (response != null && this.exception == null) {
            this.exception = new ErrorResponsePacketReceivedException(response);
        }
    }
    
    public boolean isSuccessful() {
        return this.transmissionCompleted && (this.response == null || !this.response.isValidErrorMessage());
    }
    
    @Override
    public String toString() {
        final StringBuilder msg = new StringBuilder();
        msg.append("[").append(this.identifier).append("]");
        msg.append(this.transmissionCompleted ? (" transmitted " + this.payload + " on " + this.getLatestTransmissionAttempt()) : " not transmitted");
        msg.append(" to token ").append(this.device.getToken().substring(0, 5)).append("..").append(this.device.getToken().substring(59, 64));
        if (this.response != null) {
            msg.append("  ").append(this.response.getMessage());
        }
        if (this.exception != null) {
            msg.append("  ").append(this.exception);
        }
        return msg.toString();
    }
    
    public Exception getException() {
        return this.exception;
    }
    
    void setException(final Exception exception) {
        this.exception = exception;
    }
}
