package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.me.mdm.server.apps.MacAppPermissionHandler;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.UUID;
import com.adventnet.sym.server.mdm.ios.payload.MacPPPCPolicyPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import java.util.HashMap;
import com.dd.plist.NSDictionary;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DO2MacPPPCPolicyPayload implements DO2Payload
{
    private Logger logger;
    private Map<String, List<NSDictionary>> permissions;
    private Map<String, List<NSDictionary>> legacyPermissions;
    private static final String BUNDLEID = "bundleID";
    private static final String PATH = "path";
    private static final String ALLOW_STANDARD_USER_TO_SET_SYSTEM_SERVICE = "AllowStandardUserToSetSystemService";
    public static final String PAYLOAD_IDENTIFIER = "PPPC-payload";
    public static final String PAYLOAD_LEGACY_IDENTIFIER = "PPPC-legacy-payload";
    private static final String ALLOW = "Allow";
    private static final String DENY = "Deny";
    
    public DO2MacPPPCPolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.permissions = new HashMap<String, List<NSDictionary>>();
        this.legacyPermissions = new HashMap<String, List<NSDictionary>>();
    }
    
    private void addToPermissionMap(final Map<String, List<NSDictionary>> permissionMap, final String key, final NSDictionary dict) throws Exception {
        if (permissionMap.containsKey(key)) {
            final List<NSDictionary> array = permissionMap.get(key);
            array.add(dict);
        }
        else {
            final List<NSDictionary> array = new ArrayList<NSDictionary>();
            array.add(dict);
            permissionMap.put(key, array);
        }
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        try {
            final Iterator<Row> policyIterator = dataObject.getRows("MacPPPCPolicy");
            while (policyIterator.hasNext()) {
                final Row row = policyIterator.next();
                final Long permissionConfig = (Long)row.get("APP_PERMISSION_CONFIG_ID");
                this.addAppPermissions(dataObject, permissionConfig);
            }
            final MacPPPCPolicyPayload[] payloadArray = new MacPPPCPolicyPayload[2];
            final MacPPPCPolicyPayload payload = new MacPPPCPolicyPayload(1, "MDM", UUID.randomUUID().toString() + "." + "PPPC-payload", "Privacy Preference Policy Control");
            final MacPPPCPolicyPayload legacyPayload = new MacPPPCPolicyPayload(1, "MDM", UUID.randomUUID().toString() + "." + "PPPC-legacy-payload", "Privacy Preference Policy Control");
            this.setPayloadDetails(payload, this.permissions);
            this.setPayloadDetails(legacyPayload, this.legacyPermissions);
            payloadArray[0] = payload;
            payloadArray[1] = legacyPayload;
            return payloadArray;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to convert dataObject to payload", e);
            return null;
        }
    }
    
    private void setPayloadDetails(final MacPPPCPolicyPayload payload, final Map<String, List<NSDictionary>> permissionMap) throws Exception {
        for (final Map.Entry pair : permissionMap.entrySet()) {
            payload.addToServiceDict(pair.getKey(), PlistWrapper.convertListToNSArray(pair.getValue()));
        }
        payload.setPermissionForApp("Services", payload.getServiceDict());
    }
    
    private void addAppPermissions(final DataObject dataObject, final Long permissionConfigID) throws Exception {
        String codeRequirement = null;
        String identifierType = null;
        String identifier = null;
        String bundleID = null;
        String installationPath = null;
        final Row invRow = dataObject.getRow("InvAppGroupToPermission", new Criteria(Column.getColumn("InvAppGroupToPermission", "APP_PERMISSION_CONFIG_ID"), (Object)permissionConfigID, 0));
        final Long appGroupID = (Long)invRow.get("APP_GROUP_ID");
        bundleID = (String)dataObject.getValue("INV_APPGROUPDETAILS_ALIAS", "IDENTIFIER", new Criteria(Column.getColumn("INV_APPGROUPDETAILS_ALIAS", "APP_GROUP_ID"), (Object)appGroupID, 0));
        final Row macPropertyRow = dataObject.getRow("INV_MACAPPPROPERTIES_ALIAS", new Criteria(Column.getColumn("INV_MACAPPPROPERTIES_ALIAS", "APP_GROUP_ID"), (Object)appGroupID, 0));
        codeRequirement = (String)macPropertyRow.get("CODE_REQUIREMENT");
        installationPath = (String)macPropertyRow.get("INSTALLATION_PATH");
        final Iterator<Row> iterator = dataObject.getRows("AppPermissionConfigDetails", new Criteria(Column.getColumn("AppPermissionConfigDetails", "APP_PERMISSION_CONFIG_ID"), (Object)permissionConfigID, 0));
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Integer grantValue = (Integer)row.get("APP_PERMISSION_GRANT_STATE");
            if (grantValue.equals(2)) {
                continue;
            }
            final Long permissionDtlsID = (Long)row.get("APP_PERMISSION_CONFIG_DTLS_ID");
            final Row macPermissionRow = dataObject.getRow("MacAppPermissionProps", new Criteria(Column.getColumn("MacAppPermissionProps", "APP_PERMISSION_CONFIG_DTLS_ID"), (Object)permissionDtlsID, 0));
            final Integer identifierTypeValue = (Integer)macPermissionRow.get("IDENTIFIER_TYPE");
            identifierType = ((identifierTypeValue == 1) ? "bundleID" : "path");
            identifier = this.getIdentifier(identifierTypeValue, installationPath, bundleID);
            final Long permissionPolicyID = (Long)row.get("APP_PERMISSION_CONFIG_DTLS_ID");
            final Boolean staticCode = (Boolean)dataObject.getValue("MacAppPermissionProps", "STATIC_CODE_VALIDATION", new Criteria(Column.getColumn("MacAppPermissionProps", "APP_PERMISSION_CONFIG_DTLS_ID"), (Object)permissionPolicyID, 0));
            final Long permissionGroupID = (Long)row.get("APP_PERMISSION_GROUP_ID");
            final String permissionType = (String)dataObject.getValue("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME", new Criteria(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_ID"), (Object)permissionGroupID, 0));
            final NSDictionary dict = new NSDictionary();
            dict.put("Identifier", (Object)identifier);
            dict.put("IdentifierType", (Object)identifierType);
            dict.put("CodeRequirement", (Object)codeRequirement);
            dict.put("StaticCode", (Object)staticCode);
            if (permissionType.equalsIgnoreCase("apple.macos.permission-group.AppleEvents")) {
                final Row appleEventRow = dataObject.getRow("AppleEventPreference", new Criteria(Column.getColumn("AppleEventPreference", "APP_PERMISSION_CONFIG_DTLS_ID"), (Object)permissionDtlsID, 0));
                final Long receiverAppGroupID = (Long)appleEventRow.get("RECEIVER_APP_GROUP_ID");
                final Integer receiverIDTypeValue = (Integer)appleEventRow.get("RECEIVER_ID_TYPE");
                final String receiverIDType = (identifierTypeValue == 1) ? "bundleID" : "path";
                final String receiverBundleID = (String)dataObject.getValue("RECEIVER_APPGROUPDETAILS_ALIAS", "IDENTIFIER", new Criteria(Column.getColumn("RECEIVER_APPGROUPDETAILS_ALIAS", "APP_GROUP_ID"), (Object)receiverAppGroupID, 0));
                final Row appPropertyRow = dataObject.getRow("RECEIVER_MACAPPPROPERTIES_ALIAS", new Criteria(Column.getColumn("RECEIVER_MACAPPPROPERTIES_ALIAS", "APP_GROUP_ID"), (Object)receiverAppGroupID, 0));
                final String receiverInstallationPath = (String)appPropertyRow.get("INSTALLATION_PATH");
                final String reciverCodeReqmt = (String)appPropertyRow.get("CODE_REQUIREMENT");
                final String receiverID = this.getIdentifier(receiverIDTypeValue, receiverInstallationPath, receiverBundleID);
                dict.put("AEReceiverIdentifierType", (Object)receiverIDType);
                dict.put("AEReceiverIdentifier", (Object)receiverID);
                dict.put("AEReceiverCodeRequirement", (Object)reciverCodeReqmt);
            }
            final NSDictionary depricatedPermissionDict = (NSDictionary)DMSecurityUtil.parsePropertyList(dict.toXMLPropertyList().getBytes());
            dict.put("Authorization", (Object)this.getAuthorizationValue(grantValue));
            this.addToPermissionMap(this.permissions, permissionType, dict);
            if (grantValue == 4) {
                continue;
            }
            depricatedPermissionDict.put("Allowed", (Object)this.getAllowedStatus(grantValue));
            this.addToPermissionMap(this.legacyPermissions, permissionType, depricatedPermissionDict);
        }
    }
    
    private String getIdentifier(final Integer type, final String path, final String bundle) {
        return (type != MacAppPermissionHandler.IDENTIFIER_TYPE_PATH) ? bundle : path;
    }
    
    private String getAuthorizationValue(final Integer grantState) {
        switch (grantState) {
            case 1: {
                return "Allow";
            }
            case 3: {
                return "Deny";
            }
            case 4: {
                return "AllowStandardUserToSetSystemService";
            }
            default: {
                return null;
            }
        }
    }
    
    private Boolean getAllowedStatus(final Integer grantState) {
        switch (grantState) {
            case 1: {
                return Boolean.TRUE;
            }
            default: {
                return Boolean.FALSE;
            }
        }
    }
}
