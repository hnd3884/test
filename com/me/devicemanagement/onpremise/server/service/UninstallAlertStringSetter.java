package com.me.devicemanagement.onpremise.server.service;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;

public class UninstallAlertStringSetter
{
    public static void main(final String[] args) throws IOException {
        FileInputStream in = null;
        try {
            final String serverDir = args[0];
            final String serverInfoConf = serverDir + File.separator + "conf" + File.separator + "install.conf";
            in = new FileInputStream(serverInfoConf);
            final Properties props = new Properties();
            props.load(in);
            final String mdmSummary = ((Hashtable<K, String>)props).get("mdm");
            final String icCountStr = new UninstallAlertStringSetter().getManagedDeviceCount(mdmSummary);
            if (icCountStr == null) {
                System.exit(0);
            }
            else if (icCountStr != null) {
                final int icCount = Integer.parseInt(icCountStr);
                if (icCount == 0) {
                    System.exit(0);
                }
                else if (icCount > 0) {
                    System.exit(1);
                }
            }
            System.exit(0);
        }
        catch (final Exception ex) {}
        finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    private String getICCount(final String somSummary) {
        String summayString = null;
        final StringTokenizer st = new StringTokenizer(somSummary, "|");
        while (st.hasMoreTokens()) {
            summayString = st.nextToken();
            if (summayString.contains("ic-")) {
                return summayString.substring(3);
            }
        }
        return null;
    }
    
    private String getManagedDeviceCount(final String mdmSummary) {
        String summayString = null;
        final StringTokenizer st = new StringTokenizer(mdmSummary, "|");
        while (st.hasMoreTokens()) {
            summayString = st.nextToken();
            if (summayString.contains("mdc-")) {
                return summayString.substring(4);
            }
        }
        return null;
    }
}
