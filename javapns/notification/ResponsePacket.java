package javapns.notification;

public class ResponsePacket
{
    private int command;
    private int status;
    private int identifier;
    
    protected ResponsePacket() {
    }
    
    ResponsePacket(final int command, final int status, final int identifier) {
        this.command = command;
        this.status = status;
        this.identifier = identifier;
    }
    
    void linkToPushedNotification(final PushNotificationManager notificationManager) {
        try {
            final PushedNotification notification = notificationManager.getPushedNotifications().get(this.identifier);
            if (notification != null) {
                notification.setResponse(this);
            }
        }
        catch (final Exception ex) {}
    }
    
    public int getCommand() {
        return this.command;
    }
    
    protected void setCommand(final int command) {
        this.command = command;
    }
    
    private boolean isErrorResponsePacket() {
        return this.command == 8;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    protected void setStatus(final int status) {
        this.status = status;
    }
    
    public boolean isValidErrorMessage() {
        return this.isErrorResponsePacket() && this.status != 0;
    }
    
    public int getIdentifier() {
        return this.identifier;
    }
    
    protected void setIdentifier(final int identifier) {
        this.identifier = identifier;
    }
    
    public String getMessage() {
        if (this.command != 8) {
            return "APNS: Undocumented response command: " + this.command;
        }
        final String prefix = "APNS: [" + this.identifier + "] ";
        if (this.status == 0) {
            return prefix + "No errors encountered";
        }
        if (this.status == 1) {
            return prefix + "Processing error";
        }
        if (this.status == 2) {
            return prefix + "Missing device token";
        }
        if (this.status == 3) {
            return prefix + "Missing topic";
        }
        if (this.status == 4) {
            return prefix + "Missing payload";
        }
        if (this.status == 5) {
            return prefix + "Invalid token size";
        }
        if (this.status == 6) {
            return prefix + "Invalid topic size";
        }
        if (this.status == 7) {
            return prefix + "Invalid payload size";
        }
        if (this.status == 8) {
            return prefix + "Invalid token";
        }
        if (this.status == 255) {
            return prefix + "None (unknown)";
        }
        return prefix + "Undocumented status code: " + this.status;
    }
}
