package org.apache.commons.net.ntp;

public final class NtpUtils
{
    public static String getHostAddress(final int address) {
        return (address >>> 24 & 0xFF) + "." + (address >>> 16 & 0xFF) + "." + (address >>> 8 & 0xFF) + "." + (address >>> 0 & 0xFF);
    }
    
    public static String getRefAddress(final NtpV3Packet packet) {
        final int address = (packet == null) ? 0 : packet.getReferenceId();
        return getHostAddress(address);
    }
    
    public static String getReferenceClock(final NtpV3Packet message) {
        if (message == null) {
            return "";
        }
        final int refId = message.getReferenceId();
        if (refId == 0) {
            return "";
        }
        final StringBuilder buf = new StringBuilder(4);
        for (int shiftBits = 24; shiftBits >= 0; shiftBits -= 8) {
            final char c = (char)(refId >>> shiftBits & 0xFF);
            if (c == '\0') {
                break;
            }
            if (!Character.isLetterOrDigit(c)) {
                return "";
            }
            buf.append(c);
        }
        return buf.toString();
    }
    
    public static String getModeName(final int mode) {
        switch (mode) {
            case 0: {
                return "Reserved";
            }
            case 1: {
                return "Symmetric Active";
            }
            case 2: {
                return "Symmetric Passive";
            }
            case 3: {
                return "Client";
            }
            case 4: {
                return "Server";
            }
            case 5: {
                return "Broadcast";
            }
            case 6: {
                return "Control";
            }
            case 7: {
                return "Private";
            }
            default: {
                return "Unknown";
            }
        }
    }
}
