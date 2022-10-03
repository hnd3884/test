package com.adventnet.sym.server.mdm.message;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class NatSettingsHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        final Boolean isClose = Boolean.FALSE;
        try {
            final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            final String serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            final Boolean isValidIp = isValidIp(serverIP);
            final int certificateType = ApiFactoryProvider.getServerSettingsAPI().getCertificateType();
            final Boolean isCertificateImported = certificateType != 1 && certificateType != 4;
            if (!isValidIp || isCertificateImported) {
                return Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            NatSettingsHandler.logger.log(Level.SEVERE, "Exception while getting nat settings ", ex);
        }
        return isClose;
    }
    
    private static Boolean isValidIp(final String serverIP) {
        final String ipAddPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        final Pattern ipPattern = Pattern.compile(ipAddPattern);
        final Matcher matcher = ipPattern.matcher(serverIP);
        return matcher.matches();
    }
    
    static {
        NatSettingsHandler.logger = Logger.getLogger("MDMLogger");
    }
}
