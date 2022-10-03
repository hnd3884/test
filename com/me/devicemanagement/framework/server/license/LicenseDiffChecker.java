package com.me.devicemanagement.framework.server.license;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Arrays;
import java.util.Set;
import java.util.HashMap;
import org.json.JSONObject;

public class LicenseDiffChecker
{
    private static LicenseDiffChecker licenseDiffChecker;
    private static final String DC_PRODUCT_CODE = "DCEE";
    private static final String PATCH_PRODUCT_CODE = "PMP";
    private static final String VULNERABILITY_PRODUCT_CODE = "VMP";
    private static final String RAP_PRODUCT_CODE = "RAP";
    private static final String SOM = "SOM";
    private static final String PATCH = "Patch";
    private static final String TOOLS = "Tools";
    private static final String MOBILE_DEVICES = "MobileDevices";
    private static final String VULNERABILITY = "Vulnerability";
    private static final String END_POINT_MGMT = "EndpointManagement";
    private static final String OSDEPLOYER = "OSDeployer";
    private static final String BITLOCKER = "BitLocker";
    private static final String DCM = "DeviceControl";
    private static final String ACP = "ApplicationControl";
    private JSONObject changeSet;
    
    private LicenseDiffChecker() {
        this.changeSet = null;
        this.changeSet = this.getLicenseDiff();
    }
    
    public static LicenseDiffChecker getInstance() {
        if (LicenseDiffChecker.licenseDiffChecker == null) {
            LicenseDiffChecker.licenseDiffChecker = new LicenseDiffChecker();
        }
        return LicenseDiffChecker.licenseDiffChecker;
    }
    
    public void clearLicenseChangeSet() {
        LicenseDiffChecker.licenseDiffChecker = null;
    }
    
    public JSONObject getLicenseDiff() {
        if (this.changeSet == null) {
            final License oldLicenseObject = License.getOldLicenseObject();
            final License newLicenseObject = License.getNewLicenseObject();
            Boolean isModulesEnabled = false;
            Boolean isModulesDisabled = false;
            final HashMap changeSetMap = new HashMap();
            final HashMap diff = new HashMap();
            if (!oldLicenseObject.getProductName().equalsIgnoreCase(newLicenseObject.getProductName()) || !oldLicenseObject.getProductCode().equalsIgnoreCase(newLicenseObject.getProductCode())) {
                changeSetMap.put("isProductChanged", true);
            }
            else {
                changeSetMap.put("isProductChanged", false);
            }
            final HashMap productCodeMap = new HashMap();
            productCodeMap.put("old", oldLicenseObject.getProductCode());
            productCodeMap.put("new", newLicenseObject.getProductCode());
            diff.put("productCode", productCodeMap);
            if (!oldLicenseObject.getLicenseType().equalsIgnoreCase(newLicenseObject.getLicenseType())) {
                changeSetMap.put("isLicenseTypeChanged", true);
            }
            else {
                changeSetMap.put("isLicenseTypeChanged", false);
            }
            final HashMap licenseTypeMap = new HashMap();
            licenseTypeMap.put("old", oldLicenseObject.getLicenseType());
            licenseTypeMap.put("new", newLicenseObject.getLicenseType());
            diff.put("licenseType", licenseTypeMap);
            if (!oldLicenseObject.getUserName().equalsIgnoreCase(newLicenseObject.getUserName())) {
                changeSetMap.put("isuserNameChanged", true);
            }
            else {
                changeSetMap.put("isuserNameChanged", false);
            }
            final HashMap userNameMap = new HashMap();
            userNameMap.put("old", oldLicenseObject.getUserName());
            userNameMap.put("new", newLicenseObject.getUserName());
            diff.put("userName", userNameMap);
            if (!oldLicenseObject.getCompanyName().equalsIgnoreCase(newLicenseObject.getCompanyName())) {
                changeSetMap.put("isCompanyNameChanged", true);
            }
            else {
                changeSetMap.put("isCompanyNameChanged", false);
            }
            final HashMap companyNameMap = new HashMap();
            companyNameMap.put("old", oldLicenseObject.getCompanyName());
            companyNameMap.put("new", newLicenseObject.getCompanyName());
            diff.put("companyName", companyNameMap);
            if (!oldLicenseObject.getProductCategoryString().equalsIgnoreCase(newLicenseObject.getProductCategoryString())) {
                changeSetMap.put("isProductCategoryChanged", true);
            }
            else {
                changeSetMap.put("isProductCategoryChanged", false);
            }
            final HashMap jsonObject3 = new HashMap();
            jsonObject3.put("old", oldLicenseObject.getProductCategoryString());
            jsonObject3.put("new", newLicenseObject.getProductCategoryString());
            diff.put("productCategory", jsonObject3);
            if (!oldLicenseObject.getLicenseVersion().equalsIgnoreCase(newLicenseObject.getLicenseVersion())) {
                changeSetMap.put("isLicenseVersionChanged", true);
            }
            else {
                changeSetMap.put("isLicenseVersionChanged", false);
            }
            final HashMap licenseVersionMap = new HashMap();
            licenseVersionMap.put("old", oldLicenseObject.getLicenseVersion());
            licenseVersionMap.put("new", newLicenseObject.getLicenseVersion());
            diff.put("isLicenseVersion", licenseVersionMap);
            if (!oldLicenseObject.getEmsLicenseversion().equalsIgnoreCase(newLicenseObject.getEmsLicenseversion())) {
                changeSetMap.put("isLicenseVersionChanged", true);
            }
            else {
                changeSetMap.put("isLicenseVersionChanged", false);
            }
            final HashMap emsLicenseVersionMap = new HashMap();
            emsLicenseVersionMap.put("old", oldLicenseObject.getEmsLicenseversion());
            emsLicenseVersionMap.put("new", newLicenseObject.getEmsLicenseversion());
            diff.put("isLicenseVersion", emsLicenseVersionMap);
            changeSetMap.put("ModulesPropertiesAvailableNow", Arrays.asList(License.getNewLicenseObject().getModulePropertiesMap().keySet()));
            final HashMap moduleMap = new HashMap();
            final HashMap modulePropertyMap = new HashMap();
            for (final Object module : oldLicenseObject.getModulePropertiesMap().keySet()) {
                final HashMap propertyMap = new HashMap();
                final Properties oldProperties = oldLicenseObject.getModulePropertiesMap().get(module.toString());
                int i = 0;
                final Properties newProperties = newLicenseObject.getModulePropertiesMap().get(module.toString());
                for (final Object key : ((Hashtable<Object, V>)oldProperties).keySet()) {
                    if (oldProperties != null && newProperties != null) {
                        if (oldProperties.getProperty(key.toString()) != null && !oldProperties.getProperty(key.toString()).equals(newProperties.getProperty(key.toString()))) {
                            final HashMap diff2 = new HashMap();
                            diff2.put("Old", oldProperties.getProperty(key.toString()));
                            diff2.put("New", newProperties.getProperty(key.toString()));
                            propertyMap.put(key.toString(), diff2);
                            i = 1;
                        }
                        if (i == 0) {
                            moduleMap.put(String.valueOf(module), false);
                        }
                        else {
                            moduleMap.put(String.valueOf(module), true);
                            modulePropertyMap.put(module.toString(), propertyMap);
                        }
                    }
                }
                diff.put("ModulePropertyDiff", modulePropertyMap);
                changeSetMap.put("isModulepropertiesChanged", moduleMap);
            }
            final ArrayList oldModules = new ArrayList();
            final ArrayList newModules = new ArrayList();
            final ArrayList disabledList = new ArrayList();
            final ArrayList enabledList = new ArrayList();
            oldModules.addAll(getEnabledModules(oldLicenseObject));
            newModules.addAll(getEnabledModules(newLicenseObject));
            final Iterator iterator = oldModules.iterator();
            while (iterator.hasNext()) {
                final String str = String.valueOf(iterator.next());
                if (!newModules.contains(str)) {
                    isModulesDisabled = true;
                    disabledList.add(str);
                }
            }
            final Iterator iterator2 = newModules.iterator();
            while (iterator2.hasNext()) {
                final String str2 = String.valueOf(iterator2.next());
                if (!oldModules.contains(str2)) {
                    isModulesEnabled = true;
                    enabledList.add(str2);
                }
            }
            diff.put("newlyEnabledModules", enabledList);
            diff.put("disabledModules", disabledList);
            changeSetMap.put("isModulesDisabled", isModulesDisabled);
            changeSetMap.put("isModulesEnabled", isModulesEnabled);
            changeSetMap.put("diff", diff);
            this.changeSet = new JSONObject((Map)changeSetMap);
        }
        return this.changeSet;
    }
    
    public static ArrayList getEnabledModules(final License license) {
        final ArrayList enabledModules = new ArrayList();
        if (!license.getEmsLicenseversion().equals("11") && EMSProductUtil.isEMSFlowSupportedForCurrentProduct()) {
            if ("PMP".equals(license.getProductCode())) {
                enabledModules.add("SOM");
                enabledModules.add("Patch");
            }
            else if ("VMP".equals(license.getProductCode())) {
                if (!license.getProductType().equals("Professional")) {
                    enabledModules.add("Patch");
                }
                enabledModules.add("SOM");
                enabledModules.add("Vulnerability");
            }
            else if ("RAP".equals(license.getProductCode())) {
                enabledModules.add("SOM");
                enabledModules.add("Tools");
            }
            else if ("DCEE".equals(license.getProductCode())) {
                enabledModules.add("SOM");
                enabledModules.add("MobileDevices");
                enabledModules.add("OSDeployer");
                enabledModules.add("Patch");
                enabledModules.add("Tools");
            }
        }
        else {
            final Iterator iterator = Arrays.asList(license.getModulePropertiesMap().keySet().toArray()).iterator();
            while (iterator.hasNext()) {
                final String str = String.valueOf(iterator.next());
                if (license.getModulePropertiesMap().containsKey(str)) {
                    final Properties properties = license.getModulePropertiesMap().get(str);
                    if (properties == null) {
                        continue;
                    }
                    enabledModules.add(str);
                }
            }
        }
        if (CustomerInfoUtil.isBLMEnabled() && !enabledModules.contains("BitLocker")) {
            enabledModules.add("BitLocker");
        }
        if (CustomerInfoUtil.isDCPAddonEnabled() && !enabledModules.contains("DeviceControl")) {
            enabledModules.add("DeviceControl");
        }
        if (CustomerInfoUtil.isACPAddonEnabled() && !enabledModules.contains("ApplicationControl")) {
            enabledModules.add("ApplicationControl");
        }
        if (CustomerInfoUtil.isVulnerabilityEnabled() && !enabledModules.contains("Vulnerability")) {
            enabledModules.add("Vulnerability");
        }
        if (enabledModules.contains("EndpointManagement")) {
            enabledModules.add("SOM");
            enabledModules.add("OSDeployer");
            enabledModules.add("MobileDevices");
        }
        return enabledModules;
    }
    
    public static ArrayList getEnabledModulesForCurrentLicense() {
        return getEnabledModules(License.getNewLicenseObject());
    }
    
    static {
        LicenseDiffChecker.licenseDiffChecker = null;
    }
}
