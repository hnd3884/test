package org.openjsse.legacy8ujsse.sun.security.ssl;

import sun.net.util.IPAddressUtil;
import javax.net.ssl.SNIHostName;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javax.net.ssl.SNIServerName;
import java.util.List;

final class Utilities
{
    static List<SNIServerName> addToSNIServerNameList(final List<SNIServerName> serverNames, final String hostname) {
        final SNIHostName sniHostName = rawToSNIHostName(hostname);
        if (sniHostName == null) {
            return serverNames;
        }
        final int size = serverNames.size();
        final List<SNIServerName> sniList = (size != 0) ? new ArrayList<SNIServerName>(serverNames) : new ArrayList<SNIServerName>(1);
        boolean reset = false;
        for (int i = 0; i < size; ++i) {
            final SNIServerName serverName = sniList.get(i);
            if (serverName.getType() == 0) {
                sniList.set(i, sniHostName);
                if (Debug.isOn("ssl")) {
                    System.out.println(Thread.currentThread().getName() + ", the previous server name in SNI (" + serverName + ") was replaced with (" + sniHostName + ")");
                }
                reset = true;
                break;
            }
        }
        if (!reset) {
            sniList.add(sniHostName);
        }
        return Collections.unmodifiableList((List<? extends SNIServerName>)sniList);
    }
    
    private static SNIHostName rawToSNIHostName(final String hostname) {
        SNIHostName sniHostName = null;
        if (hostname != null && hostname.indexOf(46) > 0 && !hostname.endsWith(".") && !IPAddressUtil.isIPv4LiteralAddress(hostname) && !IPAddressUtil.isIPv6LiteralAddress(hostname)) {
            try {
                sniHostName = new SNIHostName(hostname);
            }
            catch (final IllegalArgumentException iae) {
                if (Debug.isOn("ssl")) {
                    System.out.println(Thread.currentThread().getName() + ", \"" + hostname + "\" is not a legal HostName for  server name indication");
                }
            }
        }
        return sniHostName;
    }
}
