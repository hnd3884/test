package com.theorem.radius3;

import java.lang.reflect.Field;
import java.util.Hashtable;

public class PacketType
{
    public static final int Response = 0;
    public static final int Request = 1;
    public static final int Access_Request = 1;
    public static final int Access_Accept = 2;
    public static final int Access_Reject = 3;
    public static final int Access_Challenge = 11;
    public static final int Access_BadPacket = 0;
    public static final int Accounting_Request = 4;
    public static final int Accounting_Response = 5;
    public static final int Accounting_Status = 6;
    public static final int Interim_Accounting = 6;
    public static final int Password_Request = 7;
    public static final int Password_Ack = 8;
    public static final int Password_Reject = 9;
    public static final int Accounting_Message = 10;
    public static final int Status_Server = 12;
    public static final int Status_Client = 13;
    public static final int Resource_Free_Request = 21;
    public static final int Resource_Free_Response = 22;
    public static final int Resource_Query_Request = 23;
    public static final int Resource_Query_Response = 24;
    public static final int Alternate_Resource_Reclaim_Request = 25;
    public static final int NAS_Reboot_Request = 26;
    public static final int NAS_Reboot_Response = 27;
    public static final int Next_Passcode = 29;
    public static final int New_Pin = 30;
    public static final int Terminate_Session = 31;
    public static final int Password_Expired = 32;
    public static final int Event_Request = 33;
    public static final int Event_Response = 34;
    public static final int Disconnect_Request = 40;
    public static final int Disconnect_ACK = 41;
    public static final int Disconnect_NAK = 42;
    public static final int CoA_Request = 43;
    public static final int CoA_ACK = 44;
    public static final int CoA_NAK = 45;
    public static final int IP_Address_Allocate = 50;
    public static final int IP_Address_Release = 51;
    protected int a;
    protected int b;
    protected int c;
    protected int d;
    private boolean e;
    private boolean f;
    private static Hashtable g;
    
    public final int getPacketType() {
        return this.a;
    }
    
    public final String getName(final int n) {
        if (PacketType.g == null) {
            this.a();
        }
        String s;
        if ((s = PacketType.g.get(new Integer(n))) == null) {
            s = "Unknown";
        }
        return s + "(" + n + ")";
    }
    
    private final synchronized void a() {
        if (PacketType.g != null) {
            return;
        }
        PacketType.g = new Hashtable();
        try {
            final Field[] fields = this.getClass().getFields();
            for (int i = 0; i < fields.length; ++i) {
                try {
                    final Field field = fields[i];
                    final String name = field.getName();
                    if (name.indexOf(95) >= 0) {
                        PacketType.g.put(new Integer(field.getInt(null)), name.replace('_', '-'));
                    }
                }
                catch (final Exception ex) {}
            }
        }
        catch (final Exception ex2) {
            System.out.println(ex2);
        }
    }
    
    public final String toString() {
        final StringBuffer sb = new StringBuffer("PacketType: ");
        sb.append(this.getName(this.a)).append(",");
        sb.append("Direction=");
        if (this.c == 0) {
            sb.append("Response");
        }
        else {
            sb.append("Request");
        }
        sb.append(",");
        sb.append("ProxyAttr=").append(AttributeName.lookup(this.b)).append(",");
        sb.append("Port=");
        if (this.d == 0) {
            sb.append("Authentication");
        }
        else if (this.d == 1) {
            sb.append("Accounting");
        }
        else if (this.d == 2) {
            sb.append("DMCOA");
        }
        else {
            sb.append("Unknown");
        }
        sb.append(",");
        sb.append("Type=");
        if (this.e) {
            sb.append("Extended");
        }
        else if (this.f) {
            sb.append("DMCOA");
        }
        else {
            sb.append("RADIUS");
        }
        return sb.toString();
    }
}
