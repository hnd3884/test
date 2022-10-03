package org.apache.commons.net.ntp;

import java.net.DatagramPacket;

public interface NtpV3Packet
{
    public static final int NTP_PORT = 123;
    public static final int LI_NO_WARNING = 0;
    public static final int LI_LAST_MINUTE_HAS_61_SECONDS = 1;
    public static final int LI_LAST_MINUTE_HAS_59_SECONDS = 2;
    public static final int LI_ALARM_CONDITION = 3;
    public static final int MODE_RESERVED = 0;
    public static final int MODE_SYMMETRIC_ACTIVE = 1;
    public static final int MODE_SYMMETRIC_PASSIVE = 2;
    public static final int MODE_CLIENT = 3;
    public static final int MODE_SERVER = 4;
    public static final int MODE_BROADCAST = 5;
    public static final int MODE_CONTROL_MESSAGE = 6;
    public static final int MODE_PRIVATE = 7;
    public static final int NTP_MINPOLL = 4;
    public static final int NTP_MAXPOLL = 14;
    public static final int NTP_MINCLOCK = 1;
    public static final int NTP_MAXCLOCK = 10;
    public static final int VERSION_3 = 3;
    public static final int VERSION_4 = 4;
    public static final String TYPE_NTP = "NTP";
    public static final String TYPE_ICMP = "ICMP";
    public static final String TYPE_TIME = "TIME";
    public static final String TYPE_DAYTIME = "DAYTIME";
    
    DatagramPacket getDatagramPacket();
    
    void setDatagramPacket(final DatagramPacket p0);
    
    int getLeapIndicator();
    
    void setLeapIndicator(final int p0);
    
    int getMode();
    
    String getModeName();
    
    void setMode(final int p0);
    
    int getPoll();
    
    void setPoll(final int p0);
    
    int getPrecision();
    
    void setPrecision(final int p0);
    
    int getRootDelay();
    
    void setRootDelay(final int p0);
    
    double getRootDelayInMillisDouble();
    
    int getRootDispersion();
    
    void setRootDispersion(final int p0);
    
    long getRootDispersionInMillis();
    
    double getRootDispersionInMillisDouble();
    
    int getVersion();
    
    void setVersion(final int p0);
    
    int getStratum();
    
    void setStratum(final int p0);
    
    String getReferenceIdString();
    
    int getReferenceId();
    
    void setReferenceId(final int p0);
    
    TimeStamp getTransmitTimeStamp();
    
    TimeStamp getReferenceTimeStamp();
    
    TimeStamp getOriginateTimeStamp();
    
    TimeStamp getReceiveTimeStamp();
    
    void setTransmitTime(final TimeStamp p0);
    
    void setReferenceTime(final TimeStamp p0);
    
    void setOriginateTimeStamp(final TimeStamp p0);
    
    void setReceiveTimeStamp(final TimeStamp p0);
    
    String getType();
}
