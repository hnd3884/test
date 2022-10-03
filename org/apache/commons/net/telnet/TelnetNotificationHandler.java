package org.apache.commons.net.telnet;

public interface TelnetNotificationHandler
{
    public static final int RECEIVED_DO = 1;
    public static final int RECEIVED_DONT = 2;
    public static final int RECEIVED_WILL = 3;
    public static final int RECEIVED_WONT = 4;
    public static final int RECEIVED_COMMAND = 5;
    
    void receivedNegotiation(final int p0, final int p1);
}
