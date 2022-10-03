package com.adventnet.sym.server.mdm.command;

import java.util.Hashtable;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.Pattern;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.idps.core.util.DirectoryUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.profiles.LockScreenDataHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;

public class DynamicVariableHandler
{
    private static final MDMStringUtils MDM_UTIL;
    private static Logger logger;
    
    public static String replaceDynamicVariables(final String data, final String deviceUDID) {
        final HashMap managedDeviceInfo = ManagedDeviceHandler.getInstance().getManagedDeviceDetails(deviceUDID);
        final Long resourceId = Long.parseLong(managedDeviceInfo.get("RESOURCE_ID").toString());
        final LockScreenDataHandler lockScreenDataHandler = new LockScreenDataHandler();
        final String groupName = lockScreenDataHandler.getGroupNameForLockScreen(resourceId);
        final String companyname = MDMApiFactoryProvider.getMDMUtilAPI().getOrgName(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId));
        managedDeviceInfo.put("GROUPNAME", groupName);
        managedDeviceInfo.put("ORGANIZATION_NAME", companyname);
        return replaceDynamicVariables(data, deviceUDID, managedDeviceInfo);
    }
    
    public static String replaceDynamicVariables(String data, final String deviceUDID, final HashMap managedDeviceInfo) {
        try {
            Long userResourceID = null;
            if (managedDeviceInfo.get("MANAGED_USER_ID") != null) {
                userResourceID = Long.parseLong(managedDeviceInfo.get("MANAGED_USER_ID").toString());
            }
            final Long resourceID = Long.parseLong(managedDeviceInfo.get("RESOURCE_ID").toString());
            final String userName = managedDeviceInfo.get("NAME");
            String domainName = managedDeviceInfo.get("DOMAIN_NETBIOS_NAME");
            final String email = managedDeviceInfo.get("EMAIL_ADDRESS");
            final String firstName = managedDeviceInfo.get("FIRST_NAME");
            final String middleName = managedDeviceInfo.get("MIDDLE_NAME");
            final String lastName = managedDeviceInfo.get("LAST_NAME");
            final String displayName = managedDeviceInfo.get("DISPLAY_NAME");
            String apnUserName = managedDeviceInfo.get("APN_USER_NAME");
            String apnPassword = managedDeviceInfo.get("APN_PASSWORD");
            final String groupName = managedDeviceInfo.get("GROUPNAME");
            final String organization = managedDeviceInfo.get("ORGANIZATION_NAME");
            final String office = managedDeviceInfo.get("OFFICE");
            apnUserName = ((apnUserName != null) ? apnUserName : "");
            apnPassword = ((apnPassword != null) ? apnPassword : "");
            final String assetTag = managedDeviceInfo.get("ASSET_TAG");
            final String assetOwner = managedDeviceInfo.get("ASSET_OWNER");
            final String imeiNumber = managedDeviceInfo.get("IMEI");
            final String wifiMac = managedDeviceInfo.get("WIFI_MAC");
            final String bluetoothMac = managedDeviceInfo.get("BLUETOOTH_MAC");
            final String ethernetMac = managedDeviceInfo.get("ETHERNET_MACS");
            data = MDMApiFactoryProvider.getMDMUtilAPI().replaceProductSpecificDynamicValues(data, managedDeviceInfo);
            data = escapeMetaCharactersAndReplace(data, "%username%", userName);
            if (data.contains("%upn%")) {
                domainName = null;
                String upn = null;
                try {
                    upn = DirectoryUtil.getInstance().getFirstDirObjAttrValue(userResourceID, Long.valueOf(112L));
                }
                catch (final Exception ex) {
                    DynamicVariableHandler.logger.log(Level.SEVERE, "exception in retrieving upn for {0} ex {1}", new Object[] { String.valueOf(userResourceID), ex });
                }
                upn = ((upn == null) ? userName : upn);
                data = escapeMetaCharactersAndReplace(data, "%upn%", upn);
            }
            data = escapeMetaCharactersAndReplace(data, "%email%", MDMStringUtils.isEmpty(email) ? "" : email);
            data = escapeMetaCharactersAndReplace(data, "%firstname%", MDMStringUtils.isEmpty(firstName) ? "" : firstName);
            data = escapeMetaCharactersAndReplace(data, "%middlename%", MDMStringUtils.isEmpty(middleName) ? "" : middleName);
            data = escapeMetaCharactersAndReplace(data, "%lastname%", MDMStringUtils.isEmpty(lastName) ? "" : lastName);
            data = escapeMetaCharactersAndReplace(data, "%displayname%", MDMStringUtils.isEmpty(displayName) ? "" : displayName);
            if (domainName == null || domainName.equalsIgnoreCase("MDM")) {
                data = replaceDynamicVariable(data, "%domainname%", "");
            }
            else {
                DynamicVariableHandler.logger.log(Level.INFO, "get actual domain name for userResourceId {0} and domain name {1}", new Object[] { userResourceID, domainName });
                String actualDomainName = "";
                if (userResourceID != null) {
                    actualDomainName = DirectoryUtil.getInstance().getDomainName(userResourceID, domainName);
                }
                data = escapeMetaCharactersAndReplace(data, "%domainname%", actualDomainName);
            }
            data = escapeMetaCharactersAndReplace(data, "%devicename%", managedDeviceInfo.get("CUSTOMDEVICENAME"));
            data = escapeMetaCharactersAndReplace(data, "%ServerName%", ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("MDM_SERVER_NAME"));
            data = escapeMetaCharactersAndReplace(data, "%ServerPort%", Integer.toString(((Hashtable<K, Integer>)MDMUtil.getMDMServerInfo()).get("HTTPS_PORT")));
            data = replaceDynamicVariable(data, "%resourceid%", resourceID.toString());
            data = replaceDynamicVariable(data, "%udid%", deviceUDID);
            data = replaceDynamicVariable(data, "%license_pack_enabled%", String.valueOf(LicenseProvider.getInstance().isLanguagePackEnabled()));
            data = replaceDynamicVariable(data, "%erid%", managedDeviceInfo.get("ENROLLMENT_REQUEST_ID").toString());
            data = replaceDynamicVariable(data, "%serialnumber%", managedDeviceInfo.get("SERIAL_NUMBER"));
            data = replaceDynamicVariable(data, "%easid%", managedDeviceInfo.get("EAS_DEVICE_IDENTIFIER"));
            data = replaceDynamicVariable(data, "%imei%", MDMStringUtils.isEmpty(imeiNumber) ? "" : imeiNumber);
            data = replaceDynamicVariable(data, "%asset_tag%", MDMStringUtils.isEmpty(assetTag) ? "" : assetTag);
            data = replaceDynamicVariable(data, "%asset_owner%", MDMStringUtils.isEmpty(assetOwner) ? "" : assetOwner);
            data = replaceDynamicVariable(data, "%group%", MDMStringUtils.isEmpty(groupName) ? "" : groupName);
            data = replaceDynamicVariable(data, "%apn_identifier%", "com.apple.apn.managed");
            data = escapeMetaCharactersAndReplace(data, "%apn_username%", apnUserName);
            data = escapeMetaCharactersAndReplace(data, "%apn_password%", apnPassword);
            data = escapeMetaCharactersAndReplace(data, "%office%", MDMStringUtils.isEmpty(office) ? "" : office);
            data = escapeMetaCharactersAndReplace(data, "%organization_name%", MDMStringUtils.isEmpty(organization) ? "" : organization);
            data = escapeMetaCharactersAndReplace(data, "%wifi_mac%", MDMStringUtils.isEmpty(wifiMac) ? "" : wifiMac);
            data = escapeMetaCharactersAndReplace(data, "%ethernet_mac%", MDMStringUtils.isEmpty(ethernetMac) ? "" : ethernetMac);
            data = escapeMetaCharactersAndReplace(data, "%bluetooth_mac%", MDMStringUtils.isEmpty(bluetoothMac) ? "" : bluetoothMac);
        }
        catch (final Exception e) {
            DynamicVariableHandler.logger.log(Level.SEVERE, "Exception in dynamic variable handler", e);
        }
        return data;
    }
    
    private static String escapeMetaCharactersAndReplace(final String replaceIn, final String replaceWhat, final String replaceWith) {
        return replaceDynamicVariable(replaceIn, replaceWhat, DynamicVariableHandler.MDM_UTIL.escapeMetaCharacters(replaceWith));
    }
    
    public static String replaceDynamicVariable(final String replaceIn, final String replaceWhat, final String replaceWith) {
        return Pattern.compile(replaceWhat, 2).matcher(replaceIn).replaceAll(replaceWith);
    }
    
    private JSONObject getDynamicVariableJSON() {
        final JSONObject dynamicVariable = new JSONObject();
        try {
            dynamicVariable.put("Username", (Object)"%username%");
            dynamicVariable.put("Groups", (Object)"%group%");
            dynamicVariable.put("Organization", (Object)"%organization_name%");
            dynamicVariable.put("Device Serial Number", (Object)"%serialnumber%");
            dynamicVariable.put("Device Name", (Object)"%devicename%");
            dynamicVariable.put("Domain Name", (Object)"%domainname%");
            dynamicVariable.put("Email Address", (Object)"%email%");
            dynamicVariable.put("Server Name", (Object)"%ServerName%");
            dynamicVariable.put("Server Port", (Object)"%ServerPort%");
            dynamicVariable.put("UDID", (Object)"%udid%");
            dynamicVariable.put("UPN", (Object)"%upn%");
            dynamicVariable.put("IMEI Number", (Object)"%imei%");
            dynamicVariable.put("EAS Id", (Object)"%easid%");
            dynamicVariable.put("APN Username", (Object)"%apn_username%");
            dynamicVariable.put("APN Password", (Object)"%apn_password%");
            dynamicVariable.put("First Name", (Object)"%firstname%");
            dynamicVariable.put("Middle Name", (Object)"%middlename%");
            dynamicVariable.put("Last Name", (Object)"%lastname%");
            dynamicVariable.put("Display Name", (Object)"%displayname%");
            dynamicVariable.put("Asset Tag", (Object)"%asset_tag%");
            dynamicVariable.put("Asset Owner", (Object)"%asset_owner%");
            dynamicVariable.put("Office", (Object)"%office%");
            dynamicVariable.put("Bluetooth MAC", (Object)"%bluetooth_mac%");
            dynamicVariable.put("WiFi MAC address", (Object)"%wifi_mac%");
            dynamicVariable.put("Ethernet MAC address", (Object)"%ethernet_mac%");
        }
        catch (final JSONException ex) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in getting dynamic variable", (Throwable)ex);
        }
        return dynamicVariable;
    }
    
    public JSONObject getDynamicVariable() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray dynamicVariableArray = new JSONArray();
        try {
            final JSONObject variableJSON = this.getDynamicVariableJSON();
            final Iterator iterator = variableJSON.keys();
            while (iterator.hasNext()) {
                final String key = iterator.next();
                final JSONObject innerJSON = new JSONObject();
                innerJSON.put("variable_name", (Object)key);
                innerJSON.put("variable_value", variableJSON.get(key));
                dynamicVariableArray.put((Object)innerJSON);
            }
            jsonObject.put("dynamicvariables", (Object)dynamicVariableArray);
        }
        catch (final JSONException e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in getting dynamic variable", (Throwable)e);
            throw e;
        }
        return jsonObject;
    }
    
    public static String replaceDynamicVariable(String data, final HashMap dynamicVariables) {
        for (final String dynamicVariable : dynamicVariables.keySet()) {
            final String value = dynamicVariables.get(dynamicVariable);
            data = escapeMetaCharactersAndReplace(data, dynamicVariable, value);
        }
        return data;
    }
    
    static {
        MDM_UTIL = new MDMStringUtils();
        DynamicVariableHandler.logger = Logger.getLogger("MDMLogger");
    }
}
