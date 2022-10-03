package com.me.devicemanagement.framework.server.license;

import java.util.List;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class LicenseProvider
{
    private static final Logger LOGGER;
    private static LicenseProvider dcLicenseHandler;
    public static final int DEFAULT_LOGIN_USER_COUNT = 2;
    public static final int DEFAULT_LOGIN_USER_COUNT_ENP = 2;
    public static final int DEFAULT_LOGIN_USER_COUNT_MSP = 2;
    public static final String PRODUCT_TYPE_REGISTERED = "R";
    public static final String PRODUCT_TYPE_TRIAL = "T";
    public static final String TOOLS_LICENSE_PRODUCT = "TOOLSADDON";
    public static final String PRODUCT_TYPE_FREE = "F";
    public static final String TOOLS_LICENSE = "T";
    public static final String STANDARD_LICENSE = "S";
    public static final String PRIMARY_CONTACT = "PrimaryContact";
    public static final String LICENSE_USERTYPE = "licenseusertype";
    public static final String PATCH_EDITION_PRODUCT = "Patch";
    private static LicenseAPI licenseapi;
    private static MDMLicenseAPI mdmlicenseapi;
    private static DCLicenseAPI dclicenseapi;
    private static OSDLicenseAPI osdLicenseAPI;
    private static PMPLicenseAPI pmpLicenseAPI;
    private static FreeEditionCountAPI freeEditionCountAPI;
    public static final String FREE_EDITION_DEFINED = "free_edition_computer_defined";
    public static final String TOOLS_STANDARD_LICENSE = "Tools_Standard";
    public static final String TOOLS_PROF_LICENSE = "Tools_Professional";
    
    public static LicenseProvider getInstance() {
        if (LicenseProvider.dcLicenseHandler == null) {
            LicenseProvider.dcLicenseHandler = new LicenseProvider();
        }
        return LicenseProvider.dcLicenseHandler;
    }
    
    public static LicenseAPI getGeneralLicenseAPI() {
        if (LicenseProvider.licenseapi == null) {
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                LicenseProvider.licenseapi = (LicenseAPI)ApiFactoryProvider.getImplClassInstance("DM_LICENSE_API_CLASS");
            }
            else {
                try {
                    if (SyMUtil.isProbeServer()) {
                        LicenseProvider.licenseapi = (LicenseAPI)Class.forName("com.me.dconpremise.server.license.summaryserver.probe.ProbeGeneralLicenseImpl").newInstance();
                    }
                    else {
                        final String licenseHandlerClassName = ProductClassLoader.getSingleImplProductClass("LICENSE_HANDLER_CLASS");
                        if (licenseHandlerClassName != null && licenseHandlerClassName.trim().length() != 0) {
                            LicenseProvider.licenseapi = (LicenseAPI)Class.forName(licenseHandlerClassName).newInstance();
                        }
                    }
                }
                catch (final ClassNotFoundException ce) {
                    LicenseProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for LicenseAPI... ", ce);
                }
                catch (final InstantiationException ie) {
                    LicenseProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for LicenseAPI...", ie);
                }
                catch (final IllegalAccessException ie2) {
                    LicenseProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for LicenseAPI...", ie2);
                }
                catch (final Exception ex) {
                    LicenseProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for LicenseAPI...", ex);
                }
            }
        }
        return LicenseProvider.licenseapi;
    }
    
    public static void reGenerateLicenseHandler() {
        LicenseProvider.licenseapi = null;
        LicenseProvider.dclicenseapi = null;
        LicenseProvider.mdmlicenseapi = null;
        LicenseProvider.osdLicenseAPI = null;
    }
    
    public String getEMSLicenseVersion() {
        return getGeneralLicenseAPI().getEMSLicenseVersion();
    }
    
    public String getCompanyName() {
        return getGeneralLicenseAPI().getCompanyName();
    }
    
    public String getUserName() {
        return getGeneralLicenseAPI().getUserName();
    }
    
    public String getLicenseVersion() {
        return getGeneralLicenseAPI().getLicenseVersion();
    }
    
    public String getMinimumSupportedBuild() {
        return getGeneralLicenseAPI().getMinimumSupportedBuild();
    }
    
    public long getEvaluationDays() {
        return getGeneralLicenseAPI().getEvaluationDays();
    }
    
    public String getProductExpiryDate() {
        return getGeneralLicenseAPI().getProductExpiryDate();
    }
    
    public String getProductName() {
        return getGeneralLicenseAPI().getProductName();
    }
    
    public String getStoreURL(final String remoteUserIP) {
        return getGeneralLicenseAPI().getStoreURL(remoteUserIP);
    }
    
    public String getProductType() {
        try {
            if (ApiFactoryProvider.getMDMDCIntegrationUtilAPI().isModernManagementCapable()) {
                final Properties mdmLicenseProperty = this.getModuleProperties("MobileDevices");
                String mdmLicense = "";
                if (mdmLicenseProperty == null) {
                    return getGeneralLicenseAPI().getProductType();
                }
                mdmLicense = mdmLicenseProperty.getProperty("Edition");
                final String mdmCount = mdmLicenseProperty.getProperty("NumberOfMobileDevices");
                final String dcLicense = getGeneralLicenseAPI().getProductType();
                if (mdmLicense.equalsIgnoreCase("Professional") && !mdmCount.equals("25") && dcLicense.equalsIgnoreCase("Professional")) {
                    return "Enterprise";
                }
            }
        }
        catch (final Exception e) {
            LicenseProvider.LOGGER.log(Level.SEVERE, "Error while fetching license", e);
        }
        return getGeneralLicenseAPI().getProductType();
    }
    
    public String getLicenseType() {
        return getGeneralLicenseAPI().getLicenseType();
    }
    
    public Properties getModuleProperties(final String moduleName) {
        return getGeneralLicenseAPI().getModuleProperties(moduleName);
    }
    
    public String getProductCategoryString() {
        try {
            if (ApiFactoryProvider.getMDMDCIntegrationUtilAPI().isModernManagementCapable()) {
                final Properties mdmLicenseProperty = this.getModuleProperties("MobileDevices");
                String mdmLicense = "";
                if (mdmLicenseProperty == null) {
                    return getGeneralLicenseAPI().getProductCategoryString();
                }
                mdmLicense = mdmLicenseProperty.getProperty("Edition");
                final String mdmCount = mdmLicenseProperty.getProperty("NumberOfMobileDevices");
                final String dcLicense = getGeneralLicenseAPI().getProductCategoryString();
                if (mdmLicense.equalsIgnoreCase("Professional") && !mdmCount.equals("25") && dcLicense.equalsIgnoreCase("Professional")) {
                    return "Enterprise";
                }
            }
        }
        catch (final Exception e) {
            LicenseProvider.LOGGER.log(Level.SEVERE, "Error while fetching license", e);
        }
        return getGeneralLicenseAPI().getProductCategoryString();
    }
    
    public boolean isFreeEditionForwardRequired() {
        return getGeneralLicenseAPI().isFreeEditionForwardRequired();
    }
    
    public boolean isLicenseExpiryMsgRequired() {
        return getGeneralLicenseAPI().isLicenseExpiryMsgRequired();
    }
    
    public void setFreeEditionConfiguredStatus() {
        getGeneralLicenseAPI().setFreeEditionConfiguredStatus();
    }
    
    public boolean isClearedDetailsForFreeEdition(final HttpServletRequest request) {
        return getGeneralLicenseAPI().isClearedDetailsForFreeEdition(request);
    }
    
    public boolean isClearedDetailsForFreeEdition(final Map request) {
        return getGeneralLicenseAPI().isClearedDetailsForFreeEdition(request);
    }
    
    public void checkLicense(final HttpServletRequest request) {
        final String productType = this.getProductCategoryString();
        if (productType != null && (productType.equalsIgnoreCase("professional") || productType.equalsIgnoreCase("Enterprise") || productType.equalsIgnoreCase("UEM") || productType.equalsIgnoreCase("Patch") || productType.equalsIgnoreCase("Tools_Professional") || productType.equalsIgnoreCase("Tools_Standard"))) {
            request.setAttribute("CHECK_LICENSE", (Object)"true");
        }
        else {
            request.setAttribute("CHECK_LICENSE", (Object)"false");
        }
    }
    
    public String getNoOfTechnicians() {
        return getGeneralLicenseAPI().getNoOfTechnicians();
    }
    
    public Integer getPurchasedStorage() {
        return getGeneralLicenseAPI().getPurchasedStorage();
    }
    
    public Long getUsedStorage() {
        return getGeneralLicenseAPI().getUsedStorage();
    }
    
    public String getProductAMSExpiryDate() {
        return getGeneralLicenseAPI().getProductAMSExpiryDate();
    }
    
    public String getPrimaryContact() {
        return getGeneralLicenseAPI().getPrimaryContact();
    }
    
    public String getLicenseUserType() {
        return getGeneralLicenseAPI().getLicenseUserType();
    }
    
    public boolean isUserLimitReached() {
        return getGeneralLicenseAPI().isUserLimitReached();
    }
    
    public int getComplimentaryDevicesCount() {
        return getGeneralLicenseAPI().getComplimentaryDevicesCount();
    }
    
    public FreeEditionCountAPI getFreeEditionCountAPI() {
        if (LicenseProvider.freeEditionCountAPI == null) {
            try {
                Object freeEditionCountInstance = ApiFactoryProvider.getImplClassInstance("FREE_EDITION_COUNT_CLASS");
                if (freeEditionCountInstance == null) {
                    freeEditionCountInstance = Class.forName("com.me.devicemanagement.framework.server.license.FreeEditionCount").newInstance();
                }
                LicenseProvider.freeEditionCountAPI = (FreeEditionCountAPI)freeEditionCountInstance;
            }
            catch (final ClassNotFoundException ce) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for FreeEditionCountAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for FreeEditionCountAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for FreeEditionCountAPI...", ie2);
            }
            catch (final Exception ex) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for FreeEditionCountAPI...", ex);
            }
        }
        return LicenseProvider.freeEditionCountAPI;
    }
    
    public MDMLicenseAPI getMDMLicenseAPI() {
        if (LicenseProvider.mdmlicenseapi == null) {
            try {
                if (SyMUtil.isProbeServer()) {
                    LicenseProvider.mdmlicenseapi = (MDMLicenseAPI)Class.forName("com.me.dconpremise.server.license.summaryserver.probe.ProbeMDMLicenseImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    LicenseProvider.mdmlicenseapi = (MDMLicenseAPI)Class.forName("com.me.dconpremise.server.license.summaryserver.summary.SummaryMDMLicenseImpl").newInstance();
                }
                else {
                    LicenseProvider.mdmlicenseapi = (MDMLicenseAPI)ApiFactoryProvider.getImplClassInstance("MDM_LICENSE_IMPL_CLASS");
                }
            }
            catch (final Exception ex) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for MDMLicenseAPI...", ex);
            }
        }
        return LicenseProvider.mdmlicenseapi;
    }
    
    public boolean isMobileDeviceLicenseLimitExceed(final int addedEnrollmentRequestCount) {
        return this.getMDMLicenseAPI().isMobileDeviceLicenseLimitExceed(addedEnrollmentRequestCount);
    }
    
    public boolean isMobileDeviceLicenseReached(final int addedEnrollmentRequestCount) {
        return this.getMDMLicenseAPI().isMobileDeviceLicenseReached(addedEnrollmentRequestCount);
    }
    
    public String getNoOfMobileDevicesManaged() {
        return this.getMDMLicenseAPI().getNoOfMobileDevicesManaged();
    }
    
    public DCLicenseAPI getDCLicenseAPI() {
        if (LicenseProvider.dclicenseapi == null) {
            try {
                if (SyMUtil.isProbeServer()) {
                    LicenseProvider.dclicenseapi = (DCLicenseAPI)Class.forName("com.me.dconpremise.server.license.summaryserver.probe.ProbeLicenseImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    LicenseProvider.dclicenseapi = (DCLicenseAPI)Class.forName("com.me.dconpremise.server.license.summaryserver.summary.SummaryLicenseImpl").newInstance();
                }
                else {
                    Object dcLicenseImpl = ApiFactoryProvider.getImplClassInstance("DC_LICENSE_CLASS");
                    if (dcLicenseImpl == null) {
                        dcLicenseImpl = Class.forName("com.me.dc.server.license.DCLicenseImpl").newInstance();
                    }
                    LicenseProvider.dclicenseapi = (DCLicenseAPI)dcLicenseImpl;
                }
            }
            catch (final ClassNotFoundException ce) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for DCLicenseAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for DCLicenseAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for DCLicenseAPI...", ie2);
            }
            catch (final Exception ex) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for DCLicenseAPI...", ex);
            }
        }
        return LicenseProvider.dclicenseapi;
    }
    
    public boolean isComputerLicenseLimitExceed(final int managedCompCount) {
        return this.getDCLicenseAPI().isComputerLicenseLimitExceed(managedCompCount);
    }
    
    public String getNoOfComutersManaged() {
        final DCLicenseAPI dcLicenseAPI = this.getDCLicenseAPI();
        if (dcLicenseAPI != null) {
            return dcLicenseAPI.getNoOfComutersManaged();
        }
        return "0";
    }
    
    public boolean isEndpointServiceEnabled() {
        return getGeneralLicenseAPI().isEndpointServiceEnabled();
    }
    
    public String getNoOfWorkstationEndpointLicense() {
        return this.getDCLicenseAPI().getNoOfWorkstationEndpointLicense();
    }
    
    public String getNoOfServerEndpointLicense() {
        return this.getDCLicenseAPI().getNoOfServerEndpointLicense();
    }
    
    public boolean isComputerLicenseReached(final int managedCompCount) {
        return this.getDCLicenseAPI().isComputerLicenseReached(managedCompCount);
    }
    
    public String getNoOfServersLicense() {
        return this.getDCLicenseAPI().getNoOfServersLicense();
    }
    
    public boolean isServerLicenseLimitExceed(final int managedServerCount) {
        return this.getDCLicenseAPI().isServerLicenseLimitExceed(managedServerCount);
    }
    
    public boolean isServerLicenseReached(final int managedServerCount) {
        return this.getDCLicenseAPI().isServerLicenseReached(managedServerCount);
    }
    
    public boolean isTotalMachineLicenseExceed(final int totalMachines) {
        return this.getDCLicenseAPI().isTotalMachinesLimitExceed(totalMachines);
    }
    
    public boolean isTotalMachineLicenseReached(final int totalMachines) {
        return this.getDCLicenseAPI().isTotalMachineLimitReached(totalMachines);
    }
    
    public boolean isServerPropertyPresent() {
        return this.getDCLicenseAPI().isServerPropertyPresent();
    }
    
    public void setmoduleProMap() {
        getGeneralLicenseAPI().setmoduleProMap();
    }
    
    public Map getmoduleProMap() {
        return getGeneralLicenseAPI().getmoduleProMap();
    }
    
    public boolean isLanguagePackEnabled() {
        return getGeneralLicenseAPI().isLanguagePackEnabled();
    }
    
    public Boolean isFosEnabled() {
        return getGeneralLicenseAPI().isFosEnabled();
    }
    
    public Boolean isFwsEnabled() {
        return getGeneralLicenseAPI().isFwsEnabled();
    }
    
    public boolean isLicenseLimitExceed() {
        boolean isComputerLicenseLimitExceed = false;
        boolean isMobileDeviceLicenseLimitExceed = false;
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isDC()) {
            isComputerLicenseLimitExceed = this.getDCLicenseAPI().isDCLicenseLimitExceed();
        }
        if (this.getMDMLicenseAPI() != null) {
            isMobileDeviceLicenseLimitExceed = this.getMDMLicenseAPI().isMDMLicenseLimitExceed();
        }
        return isComputerLicenseLimitExceed || isMobileDeviceLicenseLimitExceed;
    }
    
    public boolean isLicenseReached() {
        boolean isComputerLicenseReached = false;
        boolean isMobileDeviceLicenseReached = false;
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isDC()) {
            isComputerLicenseReached = this.getDCLicenseAPI().isDCLicenseLimitReached();
        }
        if (this.getMDMLicenseAPI() != null) {
            isMobileDeviceLicenseReached = this.getMDMLicenseAPI().isMDMLicenseLimitReached();
        }
        return isComputerLicenseReached || isMobileDeviceLicenseReached;
    }
    
    public int getActiveMobileDevices() {
        return this.getMDMLicenseAPI().getActiveMobileDevices();
    }
    
    public OSDLicenseAPI getOSDLicenseAPI() {
        if (LicenseProvider.osdLicenseAPI == null) {
            try {
                if (SyMUtil.isProbeServer()) {
                    LicenseProvider.osdLicenseAPI = (OSDLicenseAPI)Class.forName("com.me.dconpremise.server.license.summaryserver.probe.ProbeOSDLicenseImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    LicenseProvider.osdLicenseAPI = (OSDLicenseAPI)Class.forName("com.me.dconpremise.server.license.summaryserver.summary.SummaryOSDLicenseImpl").newInstance();
                }
                else {
                    LicenseProvider.osdLicenseAPI = (OSDLicenseAPI)Class.forName("com.me.osd.server.license.OSDLicenseImpl").newInstance();
                }
            }
            catch (final ClassNotFoundException e) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException in getOsdLicenseAPI : ", e);
            }
            catch (final InstantiationException e2) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "InstantiationException in getOsdLicenseAPI : ", e2);
            }
            catch (final IllegalAccessException e3) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException in getOsdLicenseAPI : ", e3);
            }
            catch (final Exception e4) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "Exception in getOsdLicenseAPI : ", e4);
            }
        }
        return LicenseProvider.osdLicenseAPI;
    }
    
    public boolean isOSDLicenseLimitExceed() {
        return this.getOSDLicenseAPI().isDeployedImagesCountExceeded();
    }
    
    public boolean isOSDLicenseReached() {
        return this.getOSDLicenseAPI().isDeployedImagesCountReached();
    }
    
    public String getNoOfOSDeployment() {
        return this.getOSDLicenseAPI().getLicensedDeploymentCount();
    }
    
    public String getNoOfMachinesForDeployment() {
        return this.getOSDLicenseAPI().getLicensedMachineCount();
    }
    
    public String getNoOfImageCreation() {
        return this.getOSDLicenseAPI().getLicensedImagesCount();
    }
    
    public PMPLicenseAPI getPMPLicenseAPI() {
        if (LicenseProvider.pmpLicenseAPI == null) {
            try {
                LicenseProvider.pmpLicenseAPI = (PMPLicenseAPI)Class.forName("com.me.dc.server.license.PMPLicenseImpl").newInstance();
            }
            catch (final ClassNotFoundException e) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException in getPMPLicenseAPI : ", e);
            }
            catch (final InstantiationException e2) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "InstantiationException in getPMPLicenseAPI : ", e2);
            }
            catch (final IllegalAccessException e3) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException in getPMPLicenseAPI : ", e3);
            }
            catch (final Exception e4) {
                LicenseProvider.LOGGER.log(Level.SEVERE, "Exception in getPMPLicenseAPI : ", e4);
            }
        }
        return LicenseProvider.pmpLicenseAPI;
    }
    
    public void rolehandleForLicenseChage(final String productType) {
        try {
            final DataObject dobj = SyMUtil.getPersistence().constructDataObject();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UsersRoleMapping"));
            final Join join2 = new Join("UsersRoleMapping", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            selectQuery.addJoin(join2);
            selectQuery.addSelectColumn(new Column("UsersRoleMapping", "*"));
            selectQuery.addSelectColumn(new Column("AaaAccount", "*"));
            final DataObject dataObj = SyMUtil.getPersistence().get(selectQuery);
            LicenseProvider.LOGGER.log(Level.FINEST, RelationalAPI.getInstance().getSelectSQL((Query)selectQuery) + dataObj);
            final Criteria excludeRolecriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)new String[] { "All_Managed_Computer", "All_Managed_Mobile_Devices", "RESTRICT_USER_TASKS", "All_Managed_NetworkDevices", "All_Managed_Probes" }, 8);
            final DataObject dao = SyMUtil.getPersistence().get("AaaRole", excludeRolecriteria);
            final Iterator iter = dao.getRows("AaaRole");
            final List<Long> excludeRolesFromDelete = new ArrayList<Long>();
            while (iter.hasNext()) {
                final Row row = iter.next();
                excludeRolesFromDelete.add((Long)row.get("ROLE_ID"));
            }
            final Iterator loginMapping = dataObj.getRows("UsersRoleMapping");
            final List<Long> featureRoleIds = DMUserHandler.getEnterpriseRoleIds();
            Criteria featureCriteria = null;
            if (!featureRoleIds.isEmpty()) {
                featureCriteria = new Criteria(new Column("AaaAuthorizedRole", "ROLE_ID"), (Object)featureRoleIds.toArray(new Long[featureRoleIds.size()]), 9);
            }
            while (loginMapping.hasNext()) {
                final Row loginRow = loginMapping.next();
                final String loginID = loginRow.get("LOGIN_ID") + "";
                final String roleID = loginRow.get("UM_ROLE_ID") + "";
                final Criteria c1 = new Criteria(new Column("AaaAccount", "LOGIN_ID"), (Object)loginID, 0);
                final Row aaaAccRow = dataObj.getRow("AaaAccount", c1);
                Criteria deleteCri = new Criteria(new Column("AaaAuthorizedRole", "ACCOUNT_ID"), aaaAccRow.get("ACCOUNT_ID"), 0);
                deleteCri = deleteCri.and(new Criteria(new Column("AaaAuthorizedRole", "ROLE_ID"), (Object)excludeRolesFromDelete.toArray(new Long[excludeRolesFromDelete.size()]), 9));
                if (featureCriteria != null) {
                    deleteCri = deleteCri.and(featureCriteria);
                }
                SyMUtil.getPersistence().delete(deleteCri);
                final List list = DMUserHandler.getRoleList(roleID, productType);
                for (int i = 0; i < list.size(); ++i) {
                    final String id = list.get(i) + "";
                    final Row temp = new Row("AaaAuthorizedRole");
                    temp.set("ACCOUNT_ID", aaaAccRow.get("ACCOUNT_ID"));
                    temp.set("ROLE_ID", (Object)new Long(id));
                    dobj.addRow(temp);
                }
            }
            SyMUtil.getPersistence().update(dobj);
        }
        catch (final Exception ex) {
            LicenseProvider.LOGGER.log(Level.WARNING, "Exception while rolehaldeForLicenseChage method ", ex);
        }
    }
    
    public boolean isProfessionalEdition() {
        final String licenseType = getInstance().getLicenseType();
        final String productType = getInstance().getProductType();
        return ((!productType.equalsIgnoreCase("Enterprise") && !productType.equalsIgnoreCase("UEM") && !productType.equalsIgnoreCase("Tools_Professional")) || !licenseType.equals("R")) && !productType.equalsIgnoreCase("Patch") && !licenseType.equals("F") && !productType.equalsIgnoreCase("TOOLSADDON") && !licenseType.equals("T");
    }
    
    public int getUsageCount(final String moduleName) {
        return getGeneralLicenseAPI().getUsageCount(moduleName);
    }
    
    static {
        LOGGER = Logger.getLogger("LicenseLogger");
        LicenseProvider.dcLicenseHandler = null;
        LicenseProvider.licenseapi = null;
        LicenseProvider.mdmlicenseapi = null;
        LicenseProvider.dclicenseapi = null;
        LicenseProvider.osdLicenseAPI = null;
        LicenseProvider.pmpLicenseAPI = null;
        LicenseProvider.freeEditionCountAPI = null;
    }
}
