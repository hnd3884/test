package com.adventnet.sym.server.util;

import java.util.Hashtable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.adventnet.sym.winaccess.WmiAccessProvider;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class SyMUtil extends com.me.devicemanagement.onpremise.server.util.SyMUtil
{
    private static Logger logger;
    
    public static void createMEInstanceFile() throws Exception {
        final DataObject resultDO = DataAccess.get("DCServerInfo", (Criteria)null);
        Long serverInstance = (Long)resultDO.getFirstValue("DCServerInfo", "SERVER_INSTANCE_ID");
        if (serverInstance == null || serverInstance == -1L) {
            serverInstance = System.currentTimeMillis();
        }
        resultDO.set("DCServerInfo", "SERVER_HASH_ID", (Object)serverInstance);
        DataAccess.update(resultDO);
        SyMUtil.logger.log(Level.INFO, "Creating mesui file..");
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String mesuiPath = serverHome + File.separator + "lib" + File.separator + "mesui.dat";
        ApiFactoryProvider.getFileAccessAPI().writeFile(mesuiPath, String.valueOf(serverInstance).getBytes());
        SyMUtil.logger.log(Level.INFO, "Exit creating mesui file..");
    }
    
    public static void addOrUpdateDCServerInfo(final Properties props) {
        try {
            SyMUtil.logger.log(Level.INFO, "##############addOrUpdateDCServerInfo S T A R T############");
            Integer existingPortValue = null;
            final DataObject serverInfoDO = com.me.devicemanagement.framework.server.util.SyMUtil.getPersistence().get("DCServerInfo", (Criteria)null);
            if (serverInfoDO.isEmpty()) {
                final Row serverResourceRow = new Row("DCServerInfo");
                constructDCServerInfoRow(serverResourceRow, props);
                serverInfoDO.addRow(serverResourceRow);
                com.me.devicemanagement.framework.server.util.SyMUtil.getPersistence().add(serverInfoDO);
            }
            else {
                final Row serverResourceRow = serverInfoDO.getRow("DCServerInfo");
                existingPortValue = (Integer)serverResourceRow.get("HTTPS_PORT");
                constructDCServerInfoRow(serverResourceRow, props);
                serverInfoDO.updateRow(serverResourceRow);
                com.me.devicemanagement.framework.server.util.SyMUtil.getPersistence().update(serverInfoDO);
            }
            final String clientDataDirAbsoluteName = DCMetaDataUtil.getInstance().getClientDataDir();
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(clientDataDirAbsoluteName)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(clientDataDirAbsoluteName);
            }
            SyMUtil.logger.log(Level.INFO, "##############addOrUpdateDCServerInfo E N D############");
            if (!(boolean)(boolean)MDMFeatureParamsHandler.getInstance().isFeatureEnabled("IS_NAT_PORT_EDITABLE")) {
                final Integer currentPortValue = ((Hashtable<K, Integer>)props).get("HTTPS_PORT");
                final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                if (currentPortValue != null && existingPortValue != null && !isMSP && existingPortValue != (int)currentPortValue) {
                    final Integer httpsPort = ((Hashtable<K, Integer>)props).get("HTTPS_PORT");
                    NATHandler.updateNATSettingsHttpsPort(httpsPort);
                }
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception occured while adding/updating the DCServerInfo table", e);
        }
    }
    
    public static String getServerOS() throws Exception {
        String osName = System.getProperty("dc.server.osname");
        if (osName != null) {
            return osName;
        }
        osName = System.getProperty("os.name");
        SyMUtil.logger.log(Level.INFO, "osName retrieved from system props: {0}", osName);
        try {
            String osNameTemp = null;
            if (!CustomerInfoUtil.isSAS) {
                osNameTemp = WmiAccessProvider.getInstance().getOSName();
                SyMUtil.logger.log(Level.INFO, "osName retrieved from Native: {0}", osNameTemp);
            }
            if (osNameTemp != null) {
                if (osNameTemp.toLowerCase().indexOf("vista") != -1) {
                    osNameTemp = "Windows Vista";
                }
                osName = osNameTemp;
                System.setProperty("dc.server.osname", osName);
            }
        }
        catch (final Exception ex) {
            SyMUtil.logger.log(Level.WARNING, "Caught exception while getting OS Name using native API.", ex);
        }
        SyMUtil.logger.log(Level.INFO, "osName to return: {0}", osName);
        return osName;
    }
    
    public static String getValueFromSystemLogFile(final String key) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            final File file = new File(System.getProperty("server.home") + File.separator + "logs" + File.separator + "systemlog.txt");
            if (file.exists()) {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.trim().length() > 0 && line.trim().startsWith(key)) {
                        int index = 0;
                        if (line.indexOf(": ") != -1) {
                            index = line.indexOf(": ") + 1;
                        }
                        else {
                            index = line.indexOf("=") + 1;
                        }
                        return line.substring(index);
                    }
                }
            }
        }
        catch (final Exception e) {
            SyMUtil.logger.log(Level.WARNING, "Exception occurred in getValueFromSystemLogFile: ", e);
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (final Exception e) {
                SyMUtil.logger.log(Level.WARNING, "Exception occurred while reading value from SystemLogFile: ", e);
            }
        }
        finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (final Exception e2) {
                SyMUtil.logger.log(Level.WARNING, "Exception occurred while reading value from SystemLogFile: ", e2);
            }
        }
        return "-";
    }
    
    static {
        SyMUtil.logger = Logger.getLogger(SyMUtil.class.getName());
    }
}
