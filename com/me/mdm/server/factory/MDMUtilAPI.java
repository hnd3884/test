package com.me.mdm.server.factory;

import java.util.List;
import java.time.ZoneId;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.me.mdm.core.enrollment.WindowsLaptopEnrollmentHandler;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public interface MDMUtilAPI
{
    public static final Logger LOGGER = Logger.getLogger(MDMUtilAPI.class.getName());
    
    String getSelfEnrollURL(final String p0);
    
    String getAgentAppDownloadBaseUrl();
    
    String getAuthTokenString(final String p0, final boolean p1);
    
    String getHttpsServerBaseUrl() throws Exception;
    
    String getHttpServerBaseUrl() throws Exception;
    
    boolean useProxyForApns(final String p0);
    
    boolean isFeatureAllowedForUser(final String p0);
    
    Process exec(final String p0);
    
    Process exec(final String[] p0);
    
    Criteria getManagedDeviceCountCriteriaForLicenseCheck();
    
    Criteria getUemManagedDeviceCountCriteriaForLicenseCheck();
    
    String getWindowsAzureADDiscoverURL(final JSONObject p0) throws Exception;
    
    String getExtractionFilePath(final String p0);
    
    void checkLicenseStateAndUpdateManagedDeviceStatus(final JSONObject p0) throws Exception;
    
    Integer getWindowsLaptopEnrollmentUnassignedCount(final JSONObject p0, final WindowsLaptopEnrollmentHandler p1) throws Exception;
    
    String getAgentDownloadUrl(final Integer p0, final Integer p1);
    
    JSONObject getComputerDeviceMappingTable(final JSONObject p0) throws Exception;
    
    String replaceProductSpecificDynamicValues(final String p0, final HashMap p1);
    
    String getOrgName(final Long p0);
    
    @Deprecated
    SelectQuery deepCloneQuery(final SelectQuery p0);
    
    int getCurrentBuildNumber();
    
    boolean isClearedDetailsForFreeEdition(final String p0);
    
    JSONObject getLicenseMessages(final JSONObject p0);
    
    JSONObject getUserAssignmentRules(final JSONObject p0) throws Exception;
    
    JSONObject postDeviceUserDetails(final JSONObject p0);
    
    JSONObject addAutoUserAssignRule(final JSONObject p0);
    
    boolean isModernMgmtCapable();
    
    String getLicenseType();
    
    String getServerURLOnTomcatPortForClientAuthSetup() throws Exception;
    
    String getKeyToolPath() throws Exception;
    
    String getCustomerDataParentPath();
    
    String getCustomerDataBasePath(final String p0);
    
    ZoneId getZoneForCreatingSchedule();
    
    Criteria getLicenseResolveCriteria(final List p0);
    
    List<Integer> getAllowedPurpose(final String p0);
    
    Boolean showEndpointCentralLicenseMessageBox(final String p0);
}
