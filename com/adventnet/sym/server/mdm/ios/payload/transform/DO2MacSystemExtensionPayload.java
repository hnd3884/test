package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.List;
import java.util.Iterator;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSArray;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.MacSystemExtensionPayload;
import com.me.mdm.server.profiles.config.MacSystemExtensionConfigHandler;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;

public class DO2MacSystemExtensionPayload implements DO2Payload
{
    private Logger logger;
    private Map<String, Set<Integer>> teamPermissionMap;
    private Map<String, Set<String>> teamKernelMap;
    private Set<String> teamIDs;
    public static final String PAYLOAD_LEGACY_IDENTIFIER = "com.manageengine.kernel.profile";
    public static final String PAYLOAD_IDENTIFIER = "com.manageengine.system.extension.profile";
    
    public DO2MacSystemExtensionPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.teamPermissionMap = new HashMap<String, Set<Integer>>();
        this.teamKernelMap = new HashMap<String, Set<String>>();
        this.teamIDs = new HashSet<String>();
    }
    
    private void addPermissionForTeam(final String teamID, final Integer permission) {
        if (this.teamPermissionMap.containsKey(teamID)) {
            final Set permissions = this.teamPermissionMap.get(teamID);
            permissions.add(permission);
        }
        else {
            final Set permissions = new HashSet();
            permissions.add(permission);
            this.teamPermissionMap.put(teamID, permissions);
        }
    }
    
    private void addKernelForTeam(final String teamID, final String kernel) {
        if (this.teamKernelMap.containsKey(teamID)) {
            final Set kernels = this.teamKernelMap.get(teamID);
            kernels.add(kernel);
        }
        else {
            final Set kernels = new HashSet();
            kernels.add(kernel);
            this.teamKernelMap.put(teamID, kernels);
        }
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        try {
            Integer payloadNumber = 0;
            Boolean isKernelExtnPayloadAvailable = Boolean.FALSE;
            Boolean isSystemExtnPayloadAvailable = Boolean.FALSE;
            final Row configRow = dataObject.getFirstRow("MacSystemExtnConfig");
            final Boolean allowUserOverrides = (Boolean)configRow.get("ALLOW_USER_OVERRIDE");
            this.extractTeamIdsAndKernels(dataObject, MacSystemExtensionConfigHandler.KERNEL_EXT_PERMISSION_PAYLOAD_TYPE);
            NSArray teamArray = this.getTeamsIDArray();
            NSDictionary kernelDict = this.getKernelDictionary();
            final MacSystemExtensionPayload kernelPayload = new MacSystemExtensionPayload(1, "MDM", "com.manageengine.kernel.profile", "Kernel Extension", "com.apple.syspolicy.kernel-extension-policy");
            if (!kernelDict.isEmpty()) {
                isKernelExtnPayloadAvailable = Boolean.TRUE;
                kernelPayload.setAllowedKernelExtensions(kernelDict);
            }
            if (teamArray != null) {
                isKernelExtnPayloadAvailable = Boolean.TRUE;
                kernelPayload.setAllowedTeamIdentifiers(teamArray);
            }
            kernelPayload.setAllowSystemOverrides(allowUserOverrides);
            this.teamPermissionMap = new HashMap<String, Set<Integer>>();
            this.teamKernelMap = new HashMap<String, Set<String>>();
            this.teamIDs = new HashSet<String>();
            final MacSystemExtensionPayload systemExtensionPayload = new MacSystemExtensionPayload(1, "MDM", "com.manageengine.system.extension.profile", "System Extension", "com.apple.system-extension-policy");
            this.extractTeamIdsAndKernels(dataObject, MacSystemExtensionConfigHandler.SYSTEM_EXT_PERMISSION_PAYLOAD_TYPE);
            final NSDictionary permissionDict = this.getPermissionDict();
            teamArray = this.getTeamsIDArray();
            kernelDict = this.getKernelDictionary();
            if (!kernelDict.isEmpty()) {
                isSystemExtnPayloadAvailable = Boolean.TRUE;
                systemExtensionPayload.setAllowedSystemExtensions(kernelDict);
            }
            if (teamArray != null) {
                isSystemExtnPayloadAvailable = Boolean.TRUE;
                systemExtensionPayload.setAllowedTeamIdentifiers(teamArray);
            }
            systemExtensionPayload.setAllowSystemOverrides(allowUserOverrides);
            if (!permissionDict.isEmpty()) {
                systemExtensionPayload.setAllowedSystemExtensionsType(permissionDict);
            }
            if (isKernelExtnPayloadAvailable) {
                ++payloadNumber;
            }
            if (isSystemExtnPayloadAvailable) {
                ++payloadNumber;
            }
            final MacSystemExtensionPayload[] payloadArray = new MacSystemExtensionPayload[(int)payloadNumber];
            int payloadCount = 0;
            if (isKernelExtnPayloadAvailable) {
                payloadArray[payloadCount++] = kernelPayload;
            }
            if (isSystemExtnPayloadAvailable) {
                payloadArray[payloadCount++] = systemExtensionPayload;
            }
            return payloadArray;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to convert dataObject to payload", e);
            return null;
        }
    }
    
    private NSDictionary getPermissionDict() {
        final NSDictionary permissionDict = new NSDictionary();
        for (final Map.Entry pair : this.teamPermissionMap.entrySet()) {
            final String teamID = pair.getKey();
            final Set<Integer> permissions = pair.getValue();
            final NSArray permissionArray = this.getPermissionArray(permissions);
            final Boolean isPermissionNull = permissionArray == null;
            if (!isPermissionNull) {
                permissionDict.put(teamID, (NSObject)permissionArray);
            }
        }
        return permissionDict;
    }
    
    private NSDictionary getKernelDictionary() {
        final NSDictionary kernelDict = new NSDictionary();
        final int kernelCount = this.teamKernelMap.size();
        if (kernelCount > 0) {
            for (final Map.Entry pair : this.teamKernelMap.entrySet()) {
                final String teamID = pair.getKey();
                if (this.teamIDs.contains(teamID)) {
                    continue;
                }
                final Set<String> kernels = pair.getValue();
                final int noKernels = kernels.size();
                int kCount = 0;
                final Iterator<String> kernelIt = kernels.iterator();
                final NSArray kernelArray = new NSArray(noKernels);
                while (kernelIt.hasNext()) {
                    kernelArray.setValue(kCount++, (Object)kernelIt.next());
                }
                kernelDict.put(teamID, (NSObject)kernelArray);
            }
        }
        return kernelDict;
    }
    
    private NSArray getTeamsIDArray() {
        NSArray teamArray = null;
        final int teamCount = this.teamIDs.size();
        if (teamCount > 0) {
            teamArray = new NSArray(teamCount);
            int count = 0;
            for (final String teamID : this.teamIDs) {
                teamArray.setValue(count++, (Object)teamID);
            }
        }
        return teamArray;
    }
    
    private void extractTeamIdsAndKernels(final DataObject dataObject, final Integer payloadTypeReqd) throws Exception {
        final Iterator<Row> preferences = dataObject.getRows("MacSystemExtnPreference");
        while (preferences.hasNext()) {
            final Row preferenceRow = preferences.next();
            final Integer allowedExtns = (Integer)preferenceRow.get("ALLOWED_EXTENSIONS");
            final Integer payloadType = allowedExtns.equals(MacSystemExtensionConfigHandler.KERNEL_EXT_PERMISSION_PAYLOAD_TYPE) ? MacSystemExtensionConfigHandler.KERNEL_EXT_PERMISSION_PAYLOAD_TYPE : MacSystemExtensionConfigHandler.SYSTEM_EXT_PERMISSION_PAYLOAD_TYPE;
            if (payloadType != payloadTypeReqd) {
                continue;
            }
            final Long provID = (Long)preferenceRow.get("PROV_ID");
            final String teamID = this.getTeamIdentifier(dataObject, provID);
            final String kernelExtn = this.getKernelExtension(dataObject, provID);
            final Integer extnWhiteListType = (Integer)preferenceRow.get("EXTENSION_WHITELIST_TYPE");
            this.addPermissionForTeam(teamID, allowedExtns);
            if (extnWhiteListType.equals(MacSystemExtensionConfigHandler.KERNEL_EXT_WHITELIST_TYPE)) {
                this.addKernelForTeam(teamID, kernelExtn);
            }
            else {
                this.teamIDs.add(teamID);
            }
        }
        this.removeKernelsOfWhitelistedTeams();
    }
    
    private void removeKernelsOfWhitelistedTeams() {
        final Iterator iterator = this.teamKernelMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry pair = iterator.next();
            final String teamID = pair.getKey();
            if (this.teamIDs.contains(teamID)) {
                iterator.remove();
            }
        }
    }
    
    private NSArray getPermissionArray(final Set<Integer> permissions) {
        final Iterator<Integer> iterator = permissions.iterator();
        int finalPermission = 0;
        while (iterator.hasNext()) {
            final int permission = iterator.next();
            finalPermission |= permission;
        }
        if (finalPermission == 0) {
            return null;
        }
        final List<String> permissionNames = MacSystemExtensionConfigHandler.getSystemExtensionTypes(finalPermission);
        final int size = permissionNames.size();
        final NSArray array = new NSArray(size);
        int count = 0;
        for (final String permission2 : permissionNames) {
            array.setValue(count++, (Object)permission2);
        }
        return array;
    }
    
    private String getTeamIdentifier(final DataObject dataObject, final Long provID) throws Exception {
        return (String)MDMDBUtil.getFirstRow(dataObject, "AppleProvProfilesExtn", new Object[][] { { "PROV_ID", provID } }).get("TEAM_ID");
    }
    
    private String getKernelExtension(final DataObject dataObject, final Long provID) throws Exception {
        final String provAppID = (String)MDMDBUtil.getFirstRow(dataObject, "AppleProvProfilesExtn", new Object[][] { { "PROV_ID", provID } }).get("PROV_APP_ID");
        final String teamID = this.getTeamIdentifier(dataObject, provID);
        return provAppID.replaceFirst(teamID + ".", "");
    }
}
