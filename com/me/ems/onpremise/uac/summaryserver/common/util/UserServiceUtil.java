package com.me.ems.onpremise.uac.summaryserver.common.util;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.onpremise.uac.core.CoreUserUtil;
import java.util.HashMap;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Arrays;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.Set;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.List;
import org.json.JSONObject;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import java.util.logging.Logger;

public class UserServiceUtil
{
    protected static Logger LOGGER;
    
    public static List<Long> processProbeScope(final UserDetails userDetails, final JSONObject userObject, final List<String> roleNameList) {
        final int probeScopeType = userDetails.getProbeScopeType();
        String[] probeList = new String[0];
        if (probeScopeType == 1) {
            roleNameList.add("All_Managed_Probes");
            probeList = getAllProbeIds();
            UserServiceUtil.LOGGER.log(Level.INFO, "PROBE SCOPE : 'All Probe'");
        }
        else if (probeScopeType == 2) {
            probeList = userDetails.getProbeIDs().toArray(new String[0]);
            UserServiceUtil.LOGGER.log(Level.INFO, "PROBE SCOPE : 'Specific Probe'");
        }
        UserServiceUtil.LOGGER.log(Level.INFO, "PROBE LIST : {0}", getListFromProbeArray(probeList));
        userObject.put("probeScope", userDetails.getProbeScopeType());
        userObject.put("probeList", (Object)probeList);
        return getListFromProbeArray(probeList);
    }
    
    public static List<Long> processProbeScopeForPS(final UserDetails userDetails, final JSONObject userObject, final List<String> roleNameList) {
        final int probeScopeType = userDetails.getProbeScopeType();
        final String[] probeList = new String[0];
        if (probeScopeType == 1) {
            roleNameList.add("All_Managed_Probes");
        }
        userObject.put("probeScope", userDetails.getProbeScopeType());
        userObject.put("probeList", (Object)probeList);
        return getListFromProbeArray(probeList);
    }
    
    public static void processComputerScope(final UserDetails userDetails, final JSONObject userObject, final List<String> roleNameList) {
        final int computerScopeType = userDetails.getComputerScopeType();
        List<Map<String, Object>> staticUniqueGroupList = new ArrayList<Map<String, Object>>();
        String[] remoteOfficeList = new String[0];
        if (computerScopeType == 1) {
            staticUniqueGroupList = userDetails.getProbeStaticCustomGroups();
            UserServiceUtil.LOGGER.log(Level.INFO, "COMPUTER SCOPE : 'Static Unique Group'");
            UserServiceUtil.LOGGER.log(Level.INFO, "Static Unique Groups : {0}", staticUniqueGroupList);
        }
        else if (computerScopeType == 2) {
            remoteOfficeList = userDetails.getRemoteOfficeGroups().toArray(new String[0]);
            UserServiceUtil.LOGGER.log(Level.INFO, "COMPUTER SCOPE : 'Remote Office'");
            UserServiceUtil.LOGGER.log(Level.INFO, "Remote Offices : {0}", userDetails.getRemoteOfficeGroups());
        }
        else {
            roleNameList.add("All_Managed_Computer");
            UserServiceUtil.LOGGER.log(Level.INFO, "COMPUTER SCOPE : 'All Computers'");
        }
        userObject.put("scope", computerScopeType);
        userObject.put("Static_Custom_Groups", (Collection)staticUniqueGroupList);
        userObject.put("roList", (Object)remoteOfficeList);
    }
    
    public static void processDeviceScope(final UserDetails userDetails, final JSONObject userObject, final List<String> roleNameList) {
        final int deviceScopeType = userDetails.getDeviceScopeType();
        List<Map<String, Object>> mdmDeviceGroupList = new ArrayList<Map<String, Object>>();
        if (deviceScopeType == 1) {
            mdmDeviceGroupList = userDetails.getProbeMobileDeviceGroups();
            UserServiceUtil.LOGGER.log(Level.INFO, "DEVICE SCOPE : 'Device Group'");
            UserServiceUtil.LOGGER.log(Level.INFO, "Device Groups : {0}", mdmDeviceGroupList);
        }
        else {
            roleNameList.add("All_Managed_Mobile_Devices");
            UserServiceUtil.LOGGER.log(Level.INFO, "DEVICE SCOPE : 'All Devices'");
        }
        userObject.put("mdmScope", deviceScopeType);
        userObject.put("Mdm_Device_Groups", (Collection)mdmDeviceGroupList);
    }
    
    public static String[] getAllProbeIds() {
        final Set<Long> probeIds = ProbeUtil.getInstance().getAllProbeDetails().keySet();
        final String[] probeList = new String[probeIds.size()];
        int index = 0;
        for (final Long id : probeIds) {
            probeList[index++] = String.valueOf(id);
        }
        return probeList;
    }
    
    public static List<Long> getListFromProbeArray(final String[] probeArray) {
        final List<Long> probeList = new ArrayList<Long>();
        for (final String id : probeArray) {
            final Long probeID = Long.parseLong(id);
            probeList.add(probeID);
        }
        return probeList;
    }
    
    public static List<String> getCombinedScopeList(final JSONArray staticCustomGroups, final JSONArray mdmDeviceGroups, final String[] roList) {
        final List<String> combinedScopeList = new ArrayList<String>();
        if (staticCustomGroups != null && staticCustomGroups.length() > 0) {
            for (int index = 0; index < staticCustomGroups.length(); ++index) {
                final JSONObject jsonObject = staticCustomGroups.getJSONObject(index);
                combinedScopeList.add(Integer.toString(jsonObject.getInt("groupId")));
            }
        }
        if (mdmDeviceGroups != null && mdmDeviceGroups.length() > 0) {
            for (int index = 0; index < mdmDeviceGroups.length(); ++index) {
                final JSONObject jsonObject = mdmDeviceGroups.getJSONObject(index);
                combinedScopeList.add(Integer.toString(jsonObject.getInt("group_id")));
            }
        }
        if (roList != null && roList.length > 0) {
            combinedScopeList.addAll(Arrays.asList(roList));
        }
        return combinedScopeList;
    }
    
    public static void updateSpiceWorkDetails(final UserDetails userDetails, final Long loginId) {
        if (!SyMUtil.getSyMParameter("isSpiceworksEnabled").equalsIgnoreCase("enabled") || userDetails.getSpiceUser() != null) {}
    }
    
    public static void validateADUser(final UserDetails userDetails) throws APIException {
        final boolean isLocalUser = userDetails.getAuthType().equals("localAuthentication");
        final String userName = userDetails.getUserName();
        final String domainName = userDetails.getDomainName();
        if (!isLocalUser) {
            final HashMap<String, Object> resultMap = UserManagementUtil.checkIfUserIsValid(userName, domainName);
            if (Boolean.FALSE.equals(resultMap.get("isValidUser"))) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "user not found");
            }
            UserServiceUtil.LOGGER.log(Level.INFO, "AD User Validated Successfully");
        }
    }
    
    public static void processMappingData(final UserDetails userDetails, final JSONObject userObject, final Long loginID, final String roleID) throws Exception {
        String[] newMappedArray = new String[0];
        try {
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            List<String> oldMappedList;
            if (CustomerInfoUtil.getInstance().isMSP()) {
                oldMappedList = CoreUserUtil.getCustomerMappingDetailsInString(loginID);
                if (userDetails.getCustomerIDs() != null && !userDetails.getCustomerIDs().isEmpty()) {
                    newMappedArray = userDetails.getCustomerIDs().toArray(new String[0]);
                }
                else if (DMUserHandler.isDefaultAdministratorRole(Long.valueOf(roleID))) {
                    newMappedArray = DMUserHandler.getCustomerIdListForRole(roleID, (String)null).split(",");
                }
            }
            else {
                oldMappedList = ApiFactoryProvider.getUserManagementAPIHandler().getMappingDetailsInString(loginID);
                if (userDetails.getStaticComputerGroups() != null && !userDetails.getStaticComputerGroups().isEmpty()) {
                    newMappedArray = userDetails.getStaticComputerGroups().toArray(new String[0]);
                }
                else if (userDetails.getRemoteOfficeGroups() != null && !userDetails.getRemoteOfficeGroups().isEmpty()) {
                    newMappedArray = userDetails.getRemoteOfficeGroups().toArray(new String[0]);
                }
            }
            final String[] oldMappedArray = oldMappedList.toArray(new String[0]);
            userObject.put("oldMappedList", (Object)oldMappedArray);
            userObject.put("newMappedList", (Object)newMappedArray);
        }
        catch (final Exception exception) {
            UserServiceUtil.LOGGER.log(Level.SEVERE, "Exception while Customer Id list for the Role");
            throw exception;
        }
    }
    
    public static void validateScopeOfSDPUser(final UserDetails userDetails) throws APIException {
        final int probeScopeType = userDetails.getProbeScopeType();
        final int computerScopeType = userDetails.getComputerScopeType();
        final int deviceScopeType = userDetails.getDeviceScopeType();
        if (probeScopeType != 1 || computerScopeType != 0 || deviceScopeType != 0) {
            throw new APIException(Response.Status.BAD_REQUEST, "IAM0006", "INVALID DATA");
        }
    }
    
    public static boolean isUserNewToProbe(final List<Long> oldProbesList, final List<Long> newProbesList) {
        for (final Long probeId : newProbesList) {
            if (!oldProbesList.contains(probeId)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSDPUser(final Long loginId) throws DataAccessException {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("DCAaaLogin", "LOGIN_ID"), (Object)loginId, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("DCAaaLogin", criteria);
            return dataObject != null && !dataObject.isEmpty();
        }
        catch (final DataAccessException exception) {
            UserServiceUtil.LOGGER.log(Level.SEVERE, "Exception while checking if the User is an SDP User.");
            throw exception;
        }
    }
    
    static {
        UserServiceUtil.LOGGER = Logger.getLogger("UserManagementLogger");
    }
}
