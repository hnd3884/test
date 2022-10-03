package com.me.devicemanagement.onpremise.server.license;

import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Iterator;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.license.FreeEditionHandler;
import com.adventnet.tools.prevalent.DataClass;
import java.util.logging.Level;
import com.adventnet.tools.prevalent.InputFileParser;
import java.io.File;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Properties;
import java.util.Hashtable;
import com.adventnet.tools.prevalent.Wield;
import java.util.Map;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseAPI;

public abstract class LicenseImpl implements LicenseAPI
{
    private static Logger logger;
    Map moduleProMap;
    protected Wield w;
    private String emsLicenseVersion;
    
    public LicenseImpl() {
        this.moduleProMap = new Hashtable();
        this.w = null;
        this.w = this.getWield();
    }
    
    public Wield getWield() {
        return this.w = Wield.getInstance();
    }
    
    @Deprecated
    public String getLicenseVersion() {
        return this.w.getLicenseTypeString();
    }
    
    public String getMinimumSupportedBuild() {
        String minimumSupportedBuild = null;
        final Properties metaProps = this.getModuleProperties("Metadata");
        if (metaProps != null) {
            minimumSupportedBuild = metaProps.getProperty("MinimumBuild");
        }
        return minimumSupportedBuild;
    }
    
    public String getUserName() {
        return this.w.getUserName();
    }
    
    public long getEvaluationDays() {
        return this.w.getEvaluationDays();
    }
    
    public String getProductCategoryString() {
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        if (!version.equalsIgnoreCase("11")) {
            String category = this.getWield().getProductCategoryString();
            if (category.equalsIgnoreCase("Enterprise")) {
                final String type = LicenseProvider.getInstance().getLicenseVersion();
                category = (type.equals("Vulnerability") ? category : type);
            }
            return category;
        }
        return this.w.getLicenseTypeString();
    }
    
    public String getEMSLicenseVersion() {
        if (this.emsLicenseVersion != null) {
            return this.emsLicenseVersion;
        }
        String version = "11";
        try {
            final String productHomeDir = System.getProperty("server.home");
            final String filePath = productHomeDir + File.separator + "lib" + File.separator + "AdventNetLicense.xml";
            final InputFileParser parser = new InputFileParser(filePath);
            final DataClass data = parser.getDataClass();
            version = data.getDetails("ID").getProductVersion();
            this.emsLicenseVersion = version;
        }
        catch (final Exception e) {
            this.emsLicenseVersion = null;
            LicenseImpl.logger.log(Level.SEVERE, "Exception while getting EMS License Version", e);
        }
        return version;
    }
    
    public String getCompanyName() {
        return this.w.getCompanyName();
    }
    
    public String getProductExpiryDate() {
        return this.w.getEvaluationExpiryDate();
    }
    
    public String getProductName() {
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        if (!version.equalsIgnoreCase("11")) {
            return this.w.getProductName();
        }
        return this.w.getProductCategoryString();
    }
    
    @Deprecated
    public String getProductType() {
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        if (!version.equalsIgnoreCase("11")) {
            return this.w.getProductCategoryString();
        }
        return this.w.getLicenseTypeString();
    }
    
    public String getLicenseType() {
        return this.w.getUserType();
    }
    
    public Properties getModuleProperties(final String moduleName) {
        Properties moduleProperties = null;
        if (Wield.getInstance().isModulePresent(moduleName)) {
            moduleProperties = Wield.getInstance().getModuleProperties(moduleName);
            this.moduleProMap.put(moduleName, moduleProperties);
        }
        return moduleProperties;
    }
    
    public String getNoOfTechnicians() {
        final Properties numOfTechnicians = LicenseProvider.getInstance().getModuleProperties("Technicians");
        final String propertyKey = "NumberOfTechnicians";
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType != null && licenseType.equalsIgnoreCase("F")) {
            return String.valueOf(FreeEditionHandler.getInstance().getFreeEditionUsers());
        }
        if (numOfTechnicians == null) {
            return null;
        }
        final String numberOfTechnicians = numOfTechnicians.getProperty(propertyKey);
        return numberOfTechnicians;
    }
    
    public String getProductAMSExpiryDate() {
        String amsExpireDate = null;
        final Properties amsProps = LicenseProvider.getInstance().getModuleProperties("AMS");
        if (amsProps != null) {
            amsExpireDate = amsProps.getProperty("Expiry");
        }
        return amsExpireDate;
    }
    
    public String getPrimaryContact() {
        String primaryContact = "";
        final Properties licensDetailsProps = LicenseProvider.getInstance().getModuleProperties("LicenseDetails");
        if (licensDetailsProps != null) {
            primaryContact = licensDetailsProps.getProperty("PrimaryContact");
        }
        return primaryContact;
    }
    
    public String getLicenseUserType() {
        String licenseusertype = "";
        final Properties licensDetailsProps = LicenseProvider.getInstance().getModuleProperties("LicenseDetails");
        if (licensDetailsProps != null) {
            licenseusertype = licensDetailsProps.getProperty("licenseusertype");
        }
        return licenseusertype;
    }
    
    public void setmoduleProMap() {
        final ArrayList moduleArray = Wield.getInstance().getAllModules();
        for (final Object moduleName : moduleArray) {
            final Properties moduleProperties = Wield.getInstance().getModuleProperties(moduleName.toString());
            this.moduleProMap.put(moduleName, moduleProperties);
        }
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        if (!version.equalsIgnoreCase("11")) {
            final Object mObj = this.moduleProMap.get("Multilangpack");
            if (mObj == null) {
                final Properties multiLangProps = new Properties();
                multiLangProps.setProperty("Multi-lang-Pack", Boolean.toString(LicenseProvider.getInstance().isLanguagePackEnabled()));
                this.moduleProMap.put("Multilangpack", multiLangProps);
                LicenseImpl.logger.log(Level.FINE, " Properties obtained from License is " + this.moduleProMap);
            }
        }
    }
    
    public Boolean isLanguagePackEnabled() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equalsIgnoreCase("T") || licenseType.equalsIgnoreCase("F")) {
            return Boolean.TRUE;
        }
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        final String propertyKey = "Multi-lang-Pack";
        Properties addOnsProperties = null;
        if (!version.equalsIgnoreCase("11")) {
            addOnsProperties = LicenseProvider.getInstance().getModuleProperties("Multilangpack");
        }
        else {
            addOnsProperties = LicenseProvider.getInstance().getModuleProperties("AddOnModules");
        }
        if (addOnsProperties == null) {
            return Boolean.FALSE;
        }
        final Boolean isMultiPackEnabled = Boolean.parseBoolean(addOnsProperties.getProperty(propertyKey));
        return isMultiPackEnabled;
    }
    
    public Boolean isFosEnabled() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equalsIgnoreCase("T")) {
            return Boolean.TRUE;
        }
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        Properties addOnsProperties = null;
        if (!version.equalsIgnoreCase("11")) {
            addOnsProperties = LicenseProvider.getInstance().getModuleProperties("FailOverService");
        }
        else {
            addOnsProperties = LicenseProvider.getInstance().getModuleProperties("AddOnModules");
        }
        if (addOnsProperties == null) {
            return Boolean.FALSE;
        }
        final Boolean isFosEnabled = Boolean.parseBoolean(addOnsProperties.getProperty("FOSEnabled"));
        return isFosEnabled;
    }
    
    public Boolean isFwsEnabled() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equalsIgnoreCase("T")) {
            return Boolean.TRUE;
        }
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        Properties addOnsProperties = null;
        if (!version.equalsIgnoreCase("11")) {
            addOnsProperties = LicenseProvider.getInstance().getModuleProperties("ForwardingServer");
        }
        else {
            addOnsProperties = LicenseProvider.getInstance().getModuleProperties("AddOnModules");
        }
        if (addOnsProperties == null) {
            return Boolean.FALSE;
        }
        final Boolean isFwsEnabled = Boolean.parseBoolean(addOnsProperties.getProperty("FwsEnabled"));
        return isFwsEnabled;
    }
    
    public Map getmoduleProMap() {
        if (this.moduleProMap.isEmpty()) {
            this.setmoduleProMap();
        }
        return this.moduleProMap;
    }
    
    public boolean isUserLimitReached() {
        boolean userLimt = false;
        int technicianCount = 1;
        final int userCount = DMUserHandler.getUsersCountWithLogin();
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final String noOftechnician = LicenseProvider.getInstance().getNoOfTechnicians();
        if (noOftechnician != null && !noOftechnician.equalsIgnoreCase("unlimited")) {
            technicianCount = Integer.parseInt(noOftechnician);
        }
        if ((noOftechnician == null || !noOftechnician.equalsIgnoreCase("unlimited")) && userCount >= technicianCount && !licenseType.equals("T")) {
            userLimt = true;
        }
        return userLimt;
    }
    
    public String getStoreURL(final String remoteUserIP) {
        return ProductUrlLoader.getInstance().getValue("store_url");
    }
    
    public int getComplimentaryDevicesCount() {
        return 0;
    }
    
    public int getUsageCount(final String moduleName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Boolean isEndpointServiceEnabled() {
        return false;
    }
    
    public Integer getPurchasedStorage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Long getUsedStorage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static {
        LicenseImpl.logger = Logger.getLogger(LicenseAPI.class.getName());
    }
}
