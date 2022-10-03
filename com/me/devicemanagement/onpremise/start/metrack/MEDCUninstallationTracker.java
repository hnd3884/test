package com.me.devicemanagement.onpremise.start.metrack;

import java.util.Hashtable;
import com.me.tools.zcutil.ZCUtil;
import sun.misc.BASE64Decoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.onpremise.start.metrack.util.DeviceMgmtEvaluatorUtil;
import java.util.Properties;
import com.me.tools.zcutil.METrack;
import com.adventnet.tools.prevalent.Wield;
import java.io.File;

public class MEDCUninstallationTracker
{
    private static String baseDir;
    private static boolean isStartup;
    
    public static void main(final String[] args) {
        try {
            MEDCUninstallationTracker.baseDir = args[0];
            final Properties proxyProps = getProxyProperties();
            final String confDir = MEDCUninstallationTracker.baseDir + File.separator + "conf";
            final String licDir = MEDCUninstallationTracker.baseDir + File.separator + "lib";
            final METrack meTrack = new METrack(confDir, MEDCUninstallationTracker.baseDir, "mickeylite", (String)null, licDir, true, proxyProps, Wield.getInstance().getProductName());
            addBaseForm();
            meTrack.start();
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equalsIgnoreCase("startup")) {
                    MEDCUninstallationTracker.isStartup = true;
                    break;
                }
            }
            if (MEDCUninstallationTracker.isStartup) {
                postStartupFailureData();
            }
            else {
                postUninstallData(proxyProps);
            }
        }
        catch (final Exception e) {
            e.getMessage();
        }
    }
    
    private static void postUninstallData(final Properties proxyProps) {
        try {
            final Properties updatedProps = getUninstallProps();
            String response = METrack.updateRecord(updatedProps, "inputform", proxyProps);
            final Properties postInstallProps = getPostInstallProps();
            response = METrack.updateRecord(postInstallProps, "inputcommon", proxyProps);
            final Properties somProperties = getSoMTrackingSummary();
            response = METrack.updateRecord(somProperties, "inputsom", proxyProps);
            final DeviceMgmtEvaluatorUtil dcProductEvaluatorUtil = (DeviceMgmtEvaluatorUtil)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_EVALUATOR_UTIL")).getDeclaredConstructor(String.class).newInstance(MEDCUninstallationTracker.baseDir);
            final Properties trackerProperties = dcProductEvaluatorUtil.getModuleTrackerProperties(dcProductEvaluatorUtil.getMETrackKeyAndModule());
            response = METrack.updateRecord(trackerProperties, "inputdcpageclicks", proxyProps);
        }
        catch (final Exception e) {
            e.getMessage();
        }
    }
    
    private static void postStartupFailureData() throws Exception {
        final Properties updatedProps = getStartupDetails();
        final String response = METrack.updateRecord(updatedProps, "inputform", (Properties)null);
    }
    
    private static Properties getUninstallProps() {
        final Properties properties = new Properties();
        properties.setProperty("Uninstallation_Time", String.valueOf(System.currentTimeMillis()));
        properties.putAll(getStartupDetails());
        return properties;
    }
    
    public static Properties getStartupDetails() {
        final String confFilePath = MEDCUninstallationTracker.baseDir + File.separator + "conf" + File.separator + "METracking" + File.separator + "startupinfo.conf";
        final JSONObject startupObj = new JSONObject();
        final JSONObject loginObj = new JSONObject();
        final Properties startupProps = readProperties(confFilePath);
        final Enumeration e = startupProps.propertyNames();
        try {
            while (e.hasMoreElements()) {
                final String keyName = e.nextElement();
                if (keyName.startsWith("LA_")) {
                    loginObj.put(keyName, (Object)startupProps.getProperty(keyName));
                }
                else {
                    startupObj.put(keyName, (Object)startupProps.getProperty(keyName));
                }
            }
        }
        catch (final JSONException ex) {
            ex.getMessage();
        }
        final Properties failureProps = new Properties();
        ((Hashtable<String, String>)failureProps).put("Startup_Details", startupObj.toString());
        ((Hashtable<String, String>)failureProps).put("Login_Details", loginObj.toString());
        return failureProps;
    }
    
    private static Properties getProxyProperties() {
        Properties proxyProps = null;
        String confFileName = null;
        FileInputStream fis = null;
        try {
            confFileName = MEDCUninstallationTracker.baseDir + File.separator + "conf" + File.separator + "medc.conf";
            final File file = new File(confFileName);
            if (file.exists()) {
                final Properties props = new Properties();
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
                if (((Hashtable<K, Object>)props).get("directConnection").equals("1")) {
                    proxyProps = new Properties();
                    ((Hashtable<String, Object>)proxyProps).put("host", ((Hashtable<K, Object>)props).get("host"));
                    ((Hashtable<String, Object>)proxyProps).put("port", ((Hashtable<K, Object>)props).get("port"));
                    ((Hashtable<String, Object>)proxyProps).put("username", ((Hashtable<K, Object>)props).get("username"));
                    ((Hashtable<String, String>)proxyProps).put("password", decode(((Hashtable<K, Object>)props).get("password").toString()));
                }
            }
        }
        catch (final IOException e) {
            e.getMessage();
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e) {
                e.getMessage();
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e2) {
                e2.getMessage();
            }
        }
        return proxyProps;
    }
    
    private static String decode(String str) {
        final BASE64Decoder decoder = new BASE64Decoder();
        try {
            str = new String(decoder.decodeBuffer(str));
        }
        catch (final IOException e) {
            e.getMessage();
        }
        return str;
    }
    
    private static Properties getPostInstallProps() {
        final Properties properties = new Properties();
        final String didConfFile = MEDCUninstallationTracker.baseDir + File.separator + "DID.conf";
        String didValue = new String("");
        didValue = readProperties(didConfFile).getProperty("DID") + "";
        String buildNumber = new String("");
        buildNumber = readProperties(MEDCUninstallationTracker.baseDir + File.separator + "conf" + File.separator + "product.conf").getProperty("buildnumber") + "";
        properties.setProperty("Build_Number", buildNumber);
        properties.setProperty("DID_Num", didValue);
        String confFileName = MEDCUninstallationTracker.baseDir + File.separator + "conf" + File.separator + "server_info.props";
        final String serverOs = readProperties(confFileName).getProperty("server.os") + "";
        confFileName = MEDCUninstallationTracker.baseDir + File.separator + "conf" + File.separator + "install.conf";
        final String installTime = readProperties(confFileName).getProperty("it") + "";
        properties.setProperty("OS_Name", serverOs);
        properties.setProperty("Installation_Timestamp", installTime);
        return properties;
    }
    
    private static Properties readProperties(final String fileName) {
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            final File didFile = new File(fileName);
            if (didFile.isFile()) {
                ism = new FileInputStream(didFile);
                props.load(ism);
            }
        }
        catch (final Exception ex) {}
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    private static Properties getSoMTrackingSummary() {
        final JSONObject jsonObj = new JSONObject();
        final Properties somResult = new Properties();
        final String confFileName = MEDCUninstallationTracker.baseDir + File.separator + "conf" + File.separator + "install.conf";
        final Properties somTrackProps = readProperties(confFileName);
        final String somSummary = somTrackProps.getProperty("som");
        try {
            jsonObj.put("NWDomain", 0);
            jsonObj.put("NWADC", 0);
            jsonObj.put("NWWGC", 0);
            jsonObj.put("NWDomainError", 0);
            jsonObj.put("NWDomainType", (Object)"Empty");
            if (somSummary != null && somSummary.trim().length() > 0) {
                final String[] summaryArray = somSummary.split("\\|");
                if (summaryArray != null && summaryArray.length > 3) {
                    for (int i = 0; i < summaryArray.length; ++i) {
                        final String[] summaryParam = summaryArray[i].split("-", 2);
                        if (summaryParam.length > 1) {
                            if (summaryParam[0].contains("env")) {
                                jsonObj.put("NWDomainType", (Object)summaryParam[1]);
                            }
                            else if (summaryParam[0].contains("adc")) {
                                jsonObj.put("NWADC", (Object)summaryParam[1]);
                            }
                            else if (summaryParam[0].contains("wgc")) {
                                jsonObj.put("NWWGC", (Object)summaryParam[1]);
                            }
                            else if (summaryParam[0].contains("dd")) {
                                jsonObj.put("NWDomain", (Object)summaryParam[1]);
                            }
                            else if (summaryParam[0].contains("sErr")) {
                                jsonObj.put("NWDomainError", (Object)summaryParam[1]);
                            }
                        }
                    }
                }
            }
        }
        catch (final JSONException ex) {}
        somResult.setProperty("Domain_Details", jsonObj.toString());
        somResult.setProperty("SoM_String", somSummary + "");
        return somResult;
    }
    
    private static boolean isCreatorPropFileExist() {
        final String probFilePath = MEDCUninstallationTracker.baseDir + File.separator + "conf" + File.separator + "ZohoCreator.properties";
        final File propFile = new File(probFilePath);
        return propFile.exists();
    }
    
    private static void addBaseForm() {
        if (!isCreatorPropFileExist()) {
            final ZCUtil zcu = new ZCUtil();
            final Properties prop = zcu.getCustomerDetails();
            final Properties confProp = zcu.getConfValue();
            final Properties cusprops = zcu.getNewCustomerDetails(prop);
            final String response = zcu.addRecord(confProp.getProperty("appname"), confProp.getProperty("dataform"), cusprops, (Properties)null).toString();
            final String addResult = zcu.getTagTextNode(response, "status");
            if (addResult != null && addResult.equalsIgnoreCase("Success")) {
                final Properties result = zcu.getDataProp(zcu.getDocument(response));
                zcu.storeCreatorRowId(result.getProperty("ID"));
                zcu.storeProperties(prop, METrack.getConfDir() + File.separator + "cdet.properties");
            }
            else {
                zcu.storeCreatorRowId((String)null);
            }
        }
    }
    
    static {
        MEDCUninstallationTracker.baseDir = "";
        MEDCUninstallationTracker.isStartup = false;
    }
}
