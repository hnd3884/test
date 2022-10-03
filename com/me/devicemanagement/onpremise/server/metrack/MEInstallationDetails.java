package com.me.devicemanagement.onpremise.server.metrack;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import com.me.devicemanagement.onpremise.winaccess.WmiAccessProvider;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.general.CountryProvider;
import java.util.Locale;
import java.util.Set;
import sun.net.dns.ResolverConfiguration;
import java.util.Collection;
import java.util.HashSet;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class MEInstallationDetails
{
    private Logger logger;
    private Properties installationDetails;
    
    public MEInstallationDetails() {
        this.logger = Logger.getLogger("METrackLog");
        this.installationDetails = new Properties();
    }
    
    public Properties getInstallationDetails() {
        try {
            if (this.installationDetails.isEmpty()) {
                this.setDID();
                this.setOSDetails();
                this.setRamDetails();
                this.setServerHWDetails();
                this.setIsValidInfo();
                this.setCountryInfo();
                this.setMETrackDetails();
            }
            return this.installationDetails;
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in getInstallationDetails : ", e);
            return new Properties();
        }
    }
    
    private void setMETrackDetails() {
        final String userType = this.getUserType(LicenseProvider.getInstance().getLicenseType());
        if (userType != null) {
            this.installationDetails.setProperty("User_Type", userType);
        }
        if (LicenseProvider.getInstance().getLicenseVersion() != null) {
            this.installationDetails.setProperty("License_Type", LicenseProvider.getInstance().getLicenseVersion());
        }
        if (LicenseProvider.getInstance().getProductCategoryString() != null) {
            this.installationDetails.setProperty("License_Category", LicenseProvider.getInstance().getProductCategoryString());
        }
        if (LicenseProvider.getInstance().getProductName() != null) {
            this.installationDetails.setProperty("Product", LicenseProvider.getInstance().getProductName());
        }
        if (!userType.equals("Free") && LicenseProvider.getInstance().getProductExpiryDate() != null) {
            if (!"never".equalsIgnoreCase(LicenseProvider.getInstance().getProductExpiryDate())) {
                final String[] expiryArr = LicenseProvider.getInstance().getProductExpiryDate().split(" ");
                final String eDate = expiryArr[0] + "-" + this.appendZero(Integer.parseInt(expiryArr[1])) + "-" + this.appendZero(Integer.parseInt(expiryArr[2])) + " 00:00:00";
                this.installationDetails.setProperty("expirytime", String.valueOf(this.dateInLong(eDate, "yyyy-MM-dd HH:mm:ss")));
            }
            this.installationDetails.setProperty("Expiry_Date", LicenseProvider.getInstance().getProductExpiryDate());
        }
        if (userType.equals("Registered")) {
            final Properties licenseModuleProps = LicenseProvider.getInstance().getModuleProperties("LicenseDetails");
            if (licenseModuleProps != null) {
                if (licenseModuleProps.getProperty("LicenseID") != null) {
                    this.installationDetails.setProperty("License_ID", "true");
                }
                if (licenseModuleProps.getProperty("CustomerID") != null) {
                    this.installationDetails.setProperty("Customer_ID", "true");
                }
                if (licenseModuleProps.getProperty("licenseusertype") != null) {
                    this.installationDetails.setProperty("User_Type", licenseModuleProps.getProperty("licenseusertype"));
                }
                if (licenseModuleProps.getProperty("licenseusertype") != null) {
                    this.installationDetails.setProperty("User_Type", licenseModuleProps.getProperty("licenseusertype"));
                }
            }
        }
    }
    
    public long dateInLong(final String st, final String format) {
        long dateInLong = 0L;
        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            final Date mailDate = formatter.parse(st);
            dateInLong = mailDate.getTime();
        }
        catch (final Exception var7) {
            var7.printStackTrace();
        }
        return dateInLong;
    }
    
    private String appendZero(final int num) {
        return (num < 10) ? ("0" + num) : Integer.toString(num);
    }
    
    private String getUserType(final String uType) {
        if (uType.equals("T")) {
            return "Trial";
        }
        if (uType.equals("R")) {
            return "Registered";
        }
        return uType.equals("F") ? "Free" : "";
    }
    
    public void setIsValidInfo() {
        try {
            boolean isValid = true;
            String remarks = "V";
            try {
                final File productionSetupFile = new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "productionsetup.lock");
                if (productionSetupFile.exists()) {
                    final Properties productionProps = FileAccessUtil.readProperties(System.getProperty("server.home") + File.separator + "bin" + File.separator + "productionsetup.lock");
                    if (productionProps.containsKey("production") && Boolean.parseBoolean(productionProps.getProperty("production")) == Boolean.FALSE) {
                        isValid = false;
                        remarks = "N_V_WPack";
                    }
                }
                else {
                    final Properties properties = FileAccessUtil.readProperties(METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metrack_config.properties");
                    final String[] notValidDNSList = properties.containsKey("NotValidDNSList") ? ((Hashtable<K, Object>)properties).get("NotValidDNSList").toString().trim().toLowerCase().split(",") : "csez.zohocorpin.com,tsi.zohocorpin.com,ru.zohocorpin.com".trim().toLowerCase().split(",");
                    this.logger.log(Level.INFO, "notValidDNSList : " + Arrays.toString(notValidDNSList));
                    Set<String> dnsSuffixSet;
                    if (properties.containsKey("UseOldIsValidMethod") && Boolean.parseBoolean(properties.getProperty("UseOldIsValidMethod").toLowerCase())) {
                        dnsSuffixSet = new HashSet<String>(Arrays.asList(WinAccessProvider.getInstance().getDNSSuffix()));
                        this.installationDetails.setProperty("IsValidMethod", String.valueOf(1));
                    }
                    else {
                        dnsSuffixSet = new HashSet<String>(ResolverConfiguration.open().searchlist());
                        this.installationDetails.setProperty("IsValidMethod", String.valueOf(2));
                    }
                    dnsSuffixSet.add(WinAccessProvider.getInstance().getCurrentDomainName());
                    final String[] dnsSuffixes = new String[dnsSuffixSet.size()];
                    dnsSuffixSet.toArray(dnsSuffixes);
                    boolean isReasonableDNSSuffixes = false;
                    this.logger.log(Level.INFO, "Received dnsSuffix from SyMNative : " + Arrays.asList(dnsSuffixes));
                    for (final String dns : dnsSuffixes) {
                        final String dnsVal = String.valueOf(dns).trim().toLowerCase();
                        if (dnsVal.length() > 0) {
                            isReasonableDNSSuffixes = true;
                            for (final String notValidDNS : notValidDNSList) {
                                if (dnsVal.contains(notValidDNS)) {
                                    isValid = false;
                                    remarks = "N_V";
                                }
                            }
                        }
                    }
                    remarks = (isReasonableDNSSuffixes ? remarks : "UNR_DNS_S");
                }
            }
            catch (final Exception e) {
                remarks = "E";
                this.logger.log(Level.WARNING, "Exception occurred while finding isValidCustomer info : ", e);
            }
            this.installationDetails.setProperty("IS_Valid_Customer", String.valueOf(isValid));
            this.installationDetails.setProperty("IS_Valid_Remarks", remarks);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception occurred while setIsValidInfo in Metrack MEInstallationDetails props : ", e2);
        }
    }
    
    public void setCountryInfo() {
        try {
            this.installationDetails.setProperty("OS_Country", Locale.getDefault().getDisplayCountry());
            this.installationDetails.setProperty("Usr_Personalize_Country", CountryProvider.getInstance().countryCodeFromDefaultTimeZoneID());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred : ", e);
        }
    }
    
    public void setDID() {
        final String rawDID = String.valueOf(SyMUtil.getRawDID());
        final String xmlForbiddenCharsPattern = "[^a-z0-9:_-]";
        final String rawDIDCleaned = rawDID.replaceAll(xmlForbiddenCharsPattern, "\\$");
        this.logger.info("DID to be posted to METrack: " + rawDIDCleaned);
        this.installationDetails.setProperty("did", rawDIDCleaned);
        this.installationDetails.setProperty("DID_Status", String.valueOf(SyMUtil.getDIDValue()));
    }
    
    public void setOSDetails() {
        final JSONObject osDetailsJson = new JSONObject();
        try {
            final Hashtable osDetails = WmiAccessProvider.getInstance().getOSDetails();
            if (osDetails != null && !osDetails.isEmpty()) {
                osDetailsJson.put("OS_NAME", (Object)((osDetails.get("OS_NAME") != null) ? osDetails.get("OS_NAME") : "Unable to fetch"));
                osDetailsJson.put("OS_VERSION", (Object)((osDetails.get("OS_VERSION") != null) ? osDetails.get("OS_VERSION") : "Unable to fetch"));
                osDetailsJson.put("SP_VERSION", (Object)((osDetails.get("SP_VERSION") != null) ? osDetails.get("SP_VERSION") : "Unable to fetch"));
                this.logger.log(Level.FINE, "OS details" + osDetails);
                this.installationDetails.setProperty("OS", osDetailsJson.toString());
            }
            else {
                this.logger.log(Level.INFO, "Unable to fetch OS details from native");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting OS Name" + e);
        }
    }
    
    public void setRamDetails() {
        try {
            final String ramDetail = WmiAccessProvider.getInstance().getRAMDetails();
            if (ramDetail != null && !ramDetail.isEmpty()) {
                this.installationDetails.setProperty("Ram", ramDetail);
            }
            else {
                this.logger.log(Level.INFO, "Unable to fetch RAM details from native");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting RAM Memory" + e);
        }
    }
    
    private void setServerHWDetails() {
        this.logger.log(Level.INFO, "setServerDetails", "into set server details");
        final JSONObject serverDetails = new JSONObject();
        try {
            final String installHWType = SyMUtil.getServerParameter("SYSTEM_HW_TYPE");
            this.logger.log(Level.INFO, "setServerDetails", "install hardware type " + installHWType);
            final Properties productProps = this.readPropertiesFromConf("product.conf");
            String buildType = "normal";
            final boolean vmBuild = Boolean.valueOf(((Hashtable<K, String>)productProps).get("vmbuild"));
            this.logger.log(Level.INFO, "setServerDetails", "vmBuild " + vmBuild);
            if (vmBuild) {
                buildType = "vmBuild";
            }
            if (installHWType != null) {
                serverDetails.put("install_HW_Type", (Object)installHWType);
            }
            serverDetails.put("build_type", (Object)buildType);
            serverDetails.put("CPUPhysicalCoreCount", (Object)this.cpuPhysicalCoreCount());
            this.installationDetails.setProperty("DCServer_Details", serverDetails.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Unable to set MDMP server details: ", e);
        }
    }
    
    public String cpuPhysicalCoreCount() {
        BufferedReader in = null;
        int numberOfCores = 0;
        try {
            final String[] command = { "wmic", "cpu", "get", "NumberOfCores,NumberOfLogicalProcessors/Format:List" };
            final ProcessBuilder builder = new ProcessBuilder(command);
            final Process child = builder.start();
            child.getOutputStream().close();
            in = new BufferedReader(new InputStreamReader(child.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("NumberOfCores")) {
                    numberOfCores += Integer.parseInt(line.substring(14));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception getting CPU count ", e);
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e2) {
                this.logger.log(Level.SEVERE, "Exception finally block ", e2);
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e3) {
                this.logger.log(Level.SEVERE, "Exception finally block ", e3);
            }
        }
        return String.valueOf(numberOfCores);
    }
    
    private Properties readPropertiesFromConf(final String fileName) {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + "conf" + File.separator + fileName;
            return FileAccessUtil.readProperties(fname);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
