package com.adventnet.sym.webclient.mdm.enroll;

import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.Collections;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.Map;
import java.util.logging.Logger;

public class SAFEDeviceList
{
    private Logger logger;
    static SAFEDeviceList sAFEDeviceList;
    private static Map safeListPropsMap;
    public static final int DEVICEABOVE4_2 = 2;
    public static final int DEVICEBELOW4_2 = 1;
    public static final int DEVICEUNKNOWN = 0;
    
    private SAFEDeviceList() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static SAFEDeviceList getInstance() {
        if (SAFEDeviceList.sAFEDeviceList == null) {
            final Properties safeListProps = new Properties();
            SAFEDeviceList.sAFEDeviceList = new SAFEDeviceList();
            try {
                final String serverHome = System.getProperty("server.home");
                String safeFilePath = ApiFactoryProvider.getFileAccessAPI().getCanonicalPath(serverHome);
                safeFilePath = safeFilePath + File.separator + "conf" + File.separator + "safedevicelist.properties";
                safeListProps.load(new FileInputStream(safeFilePath));
                SAFEDeviceList.safeListPropsMap = Collections.unmodifiableMap((Map<?, ?>)safeListProps);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        return SAFEDeviceList.sAFEDeviceList;
    }
    
    public Map getSafeDeviceListProp() {
        return SAFEDeviceList.safeListPropsMap;
    }
    
    public boolean isSAFEDevice(final String userAgent) {
        boolean status = false;
        if (SAFEDeviceList.safeListPropsMap.containsKey(userAgent)) {
            status = true;
        }
        return status;
    }
    
    public int isSAFEAbove4_2(final String userAgent) {
        final int MAJOR_INDEX = 1;
        final Pattern VERSIONNUMBER = Pattern.compile("((\\d+)((\\.\\d+)+)?)");
        String version = "";
        final List<Pattern> patterns = new ArrayList<Pattern>();
        patterns.add(Pattern.compile("Android\\s?((\\d+)((\\.\\d+)+)?(\\-(\\w|\\d)+)?);"));
        try {
            for (final Pattern pattern : patterns) {
                final Matcher m = pattern.matcher(userAgent);
                if (m.find()) {
                    version = m.group(1);
                }
            }
            final String[] versionArray = version.split("\\.");
            if (Integer.parseInt(versionArray[0]) > 4 || (Integer.parseInt(versionArray[0]) == 4 && Integer.parseInt(versionArray[1]) >= 2)) {
                return 2;
            }
            return 1;
        }
        catch (final Exception e) {
            this.logger.log(Level.FINE, "SAFEDeviceList: Exception Occured during version check {0}", userAgent);
            return 0;
        }
    }
    
    static {
        SAFEDeviceList.sAFEDeviceList = null;
        SAFEDeviceList.safeListPropsMap = null;
    }
}
