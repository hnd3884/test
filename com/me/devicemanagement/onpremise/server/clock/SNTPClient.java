package com.me.devicemanagement.onpremise.server.clock;

import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import java.util.logging.Level;
import org.apache.commons.net.ntp.TimeStamp;
import java.net.InetAddress;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SNTPClient
{
    private static Logger LOG;
    
    public static JSONObject getInternetTime(final String ntpClientHostAddress) {
        final NTPUDPClient client = new NTPUDPClient();
        try {
            client.setDefaultTimeout(SNTPConstants.DEFAULT_TIMEOUT);
            client.open();
            final InetAddress inetNTPClientHostAddress = InetAddress.getByName(ntpClientHostAddress);
            final TimeInfo info = client.getTime(inetNTPClientHostAddress);
            final NtpV3Packet message = info.getMessage();
            final long destTime = info.getReturnTime();
            final TimeStamp xmitNtpTime = message.getTransmitTimeStamp();
            final TimeStamp destNtpTime = TimeStamp.getNtpTime(destTime);
            info.computeDetails();
            final Long offsetValue = info.getOffset();
            final JSONObject timeJSON = new JSONObject();
            timeJSON.put(SNTPConstants.INTERNET_TIME_LONG, xmitNtpTime.getTime());
            timeJSON.put(SNTPConstants.LOCAL_TIME_LONG, destNtpTime.getTime());
            timeJSON.put(SNTPConstants.NTP_HOST, (Object)ntpClientHostAddress);
            timeJSON.put(SNTPConstants.TIME_DIFFERENCE_LONG, (Object)offsetValue);
            return timeJSON;
        }
        catch (final Exception ex) {
            SNTPClient.LOG.log(Level.SEVERE, "HARMLESS: Exception for host: {0} : {1}", new Object[] { ntpClientHostAddress, ex.getMessage() });
        }
        finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
    
    static {
        SNTPClient.LOG = Logger.getLogger(SNTPClient.class.getName());
    }
}
