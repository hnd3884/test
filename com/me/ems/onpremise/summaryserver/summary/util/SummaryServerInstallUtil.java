package com.me.ems.onpremise.summaryserver.summary.util;

import java.util.Hashtable;
import java.net.HttpURLConnection;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import java.net.URL;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SummaryServerInstallUtil
{
    public static Logger logger;
    private static final int CONNECTION_FAILED = 300001;
    
    public static void main(final String[] args) {
        SummaryServerInstallUtil.logger.log(Level.INFO, "length 0f args " + args.length);
        if (args.length != 4) {
            System.exit(1);
        }
        SummaryServerInstallUtil.logger.log(Level.INFO, "proxy port - " + args[0]);
        SummaryServerInstallUtil.logger.log(Level.INFO, "proxy host - " + args[1]);
        SummaryServerInstallUtil.logger.log(Level.INFO, "proxy user - " + args[2]);
        SummaryServerInstallUtil.logger.log(Level.INFO, "proxy pass - " + args[3]);
        final Properties proxyProps = new Properties();
        ((Hashtable<String, String>)proxyProps).put("proxyHost", args[1]);
        ((Hashtable<String, String>)proxyProps).put("proxyPort", args[0]);
        ((Hashtable<String, String>)proxyProps).put("proxyUser", args[2]);
        ((Hashtable<String, String>)proxyProps).put("proxyPass", args[3]);
        if (!establishConnectionUsingProxy(proxyProps)) {
            System.exit(300001);
        }
        ProbeDetailsUtil.storeProxyDetails(proxyProps);
    }
    
    private static boolean establishConnectionUsingProxy(final Properties proxyProps) {
        try {
            final String urlStr = "https://patchdb.manageengine.com/dc-crs/crs-meta-data.xml";
            final URL url = new URL(urlStr);
            final HttpURLConnection connection = HttpsHandlerUtil.getProxyAppliedConnection(url, "true", proxyProps);
            final int respCode = connection.getResponseCode();
            if (respCode == 200) {
                return true;
            }
        }
        catch (final Exception e) {
            SummaryServerInstallUtil.logger.log(Level.SEVERE, "Exception while establishing ss connection using proxy", e);
        }
        return false;
    }
    
    static {
        SummaryServerInstallUtil.logger = Logger.getLogger("probeActionsLogger");
    }
}
