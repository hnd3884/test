package com.me.mdm.server.seqcommands.android;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Properties;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.BaseSeqCmdResponseHandler;

public class AndroidSeqCmdResponseHandler extends BaseSeqCmdResponseHandler
{
    public Logger logger;
    
    public AndroidSeqCmdResponseHandler() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    @Override
    public JSONObject processLater(final JSONObject params) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("taskClass", (Object)"com.me.mdm.server.seqcommands.android.task.ProcessSequentialCommandScheduler");
        responseJSON.put("timeOffset", 10000);
        return responseJSON;
    }
    
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        final String CommandUUID = params.optString("commandUUID");
        if (CommandUUID.contains("InstallProfile")) {
            try {
                this.distributePendingAppsForKiosk(params);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, null, e);
            }
        }
        return super.onSuccess(params);
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand sequentialSubCommand) {
        boolean isCmdReq = true;
        String profileName = null;
        try {
            if (DeviceCommandRepository.getInstance().getCommandID("SyncAppCatalog").equals(commandID)) {
                final ArrayList<Long> resourceList = new ArrayList<Long>();
                resourceList.add(resourceID);
                AppsUtil.getInstance().addOrUpdateAppCatalogSync(resourceList);
                return true;
            }
            final DataObject dataObject = AndroidSeqCmdUtil.getInstance().getPreData(resourceID, commandID);
            final Row profileRow = dataObject.getRow("Profile");
            if (profileRow == null) {
                isCmdReq = false;
            }
            else {
                final Row appRow = dataObject.getRow("MdPackageToAppGroup");
                if (appRow != null) {
                    final Boolean isMovedToTrash = (Boolean)profileRow.get("IS_MOVED_TO_TRASH");
                    if (isMovedToTrash) {
                        isCmdReq = false;
                    }
                    final int appType = (int)appRow.get("PACKAGE_TYPE");
                    if (appType == 2) {
                        if (ManagedDeviceHandler.getInstance().isProfileOwner(resourceID)) {
                            isCmdReq = false;
                        }
                        final Row collnRow = dataObject.getRow("RecentProfileForResource");
                        if (collnRow != null) {
                            final boolean status = (boolean)collnRow.get("MARKED_FOR_DELETE");
                            if (!status) {
                                isCmdReq = false;
                            }
                        }
                    }
                    else {
                        this.logger.log(Level.INFO, "Playstore app. dont sendcommand");
                        isCmdReq = false;
                    }
                }
                profileName = (String)profileRow.get("PROFILE_NAME");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "[Seq] [Init , Pre] error is pre : issuing command without checking :{0}cmd Id :{1}- Exception {2}", new Object[] { resourceID, commandID, e });
        }
        this.logger.log(Level.INFO, "[Seq] [Init , Pre] Returning seq command preprocessing result {0} for command {1} and resource {2} : profile name {3}", new Object[] { isCmdReq, commandID, resourceID, profileName });
        return isCmdReq;
    }
    
    private void distributePendingAppsForKiosk(final JSONObject params) throws Exception {
        final JSONObject currentParams = params.optJSONObject("CommandLevelParams");
        final Long resourceID = params.optLong("resourceID");
        Boolean isPendingAppsFilled = false;
        final HashMap profileColMap = new HashMap();
        if (currentParams != null) {
            final JSONArray pendingCollnIDs = currentParams.optJSONArray("pendingCollnIDs");
            if (pendingCollnIDs != null) {
                isPendingAppsFilled = true;
                this.logger.log(Level.INFO, "Distributing Playstore apps for kiosk from command params");
                for (int i = 0; i < pendingCollnIDs.length(); ++i) {
                    try {
                        final Long collectionID = pendingCollnIDs.getLong(i);
                        if (collectionID != -1L) {
                            final Map hashMap = new ProfileHandler().getProfileInfoFromCollectionID(collectionID);
                            final Long profileID = hashMap.get("PROFILE_ID");
                            profileColMap.put(profileID, collectionID);
                        }
                    }
                    catch (final JSONException ex) {
                        this.logger.log(Level.SEVERE, null, (Throwable)ex);
                    }
                }
            }
        }
        if (!isPendingAppsFilled) {
            try {
                this.logger.log(Level.INFO, "Old Kiosk flow. So detecting Playstore apps to be distributed");
                final Long collectionId = SeqCmdUtils.getInstance().getBaseCollectionIDForResource(resourceID);
                final SelectQuery KioskAppsSelect = AndroidSeqCmdUtil.getInstance().getKioskAppsSelectQuery(collectionId);
                final DataObject dataObject1 = DataAccess.get(KioskAppsSelect);
                final Iterator it = dataObject1.getRows("AndroidKioskPolicyApps");
                while (it.hasNext()) {
                    final Row row1 = it.next();
                    final Long appgrpid = (Long)row1.get("APP_GROUP_ID");
                    final Long collectionID2 = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appgrpid);
                    final ArrayList collectionList = new ArrayList();
                    collectionList.add(collectionID2);
                    final List collectionCmdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallApplication");
                    final Long cmdID = collectionCmdList.isEmpty() ? null : collectionCmdList.get(0);
                    if (cmdID != null) {
                        final int appType = AppsUtil.getInstance().getAppPackageTypeFromCollectionId(collectionID2);
                        if (appType != 2) {
                            final Map hashMap2 = new ProfileHandler().getProfileInfoFromCollectionID(collectionID2);
                            final Long profileID2 = hashMap2.get("PROFILE_ID");
                            profileColMap.put(profileID2, collectionID2);
                        }
                        else {
                            this.logger.log(Level.INFO, "Enterprise app : {0}: would be distributed already. So not adding now", collectionID2);
                        }
                    }
                    else {
                        this.logger.log(Level.INFO, "App not Managed : {0}: not distriubting now", collectionID2);
                    }
                }
                final SelectQuery KioskHiddenAppsSelect = AndroidSeqCmdUtil.getInstance().getKioskHiddenAppsSelectQuery(collectionId);
                final DataObject kioskHiddenApps = DataAccess.get(KioskHiddenAppsSelect);
                final Iterator iterator = kioskHiddenApps.getRows("AndroidKioskPolicyBackgroundApps");
                while (iterator.hasNext()) {
                    final Row row2 = iterator.next();
                    final Long appgrpid2 = (Long)row2.get("APP_GROUP_ID");
                    final Long collectionID3 = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appgrpid2);
                    final ArrayList collectionList2 = new ArrayList();
                    collectionList2.add(collectionID3);
                    final List collectionCmdList2 = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList2, "InstallApplication");
                    final Long cmdID2 = collectionCmdList2.isEmpty() ? null : collectionCmdList2.get(0);
                    if (cmdID2 != null) {
                        final int appType2 = AppsUtil.getInstance().getAppPackageTypeFromCollectionId(collectionID3);
                        if (appType2 != 2) {
                            final Map hashMap3 = new ProfileHandler().getProfileInfoFromCollectionID(collectionID3);
                            final Long profileID3 = hashMap3.get("PROFILE_ID");
                            profileColMap.put(profileID3, collectionID3);
                        }
                        else {
                            this.logger.log(Level.INFO, "Enterprise app : {0}: would be distributed already. So not adding now", collectionID3);
                        }
                    }
                    else {
                        this.logger.log(Level.INFO, "App not Managed : {0}: not distriubting now", collectionID3);
                    }
                }
            }
            catch (final DataAccessException ex2) {
                this.logger.log(Level.SEVERE, null, (Throwable)ex2);
            }
            catch (final Exception ex3) {
                this.logger.log(Level.SEVERE, null, ex3);
            }
        }
        if (!profileColMap.isEmpty()) {
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.logger.log(Level.INFO, "Distributing pending apps for Kiosk - profileColMap{0}", profileColMap);
            final JSONObject seqParams = SeqCmdDBUtil.getInstance().getParams(resourceID);
            final JSONObject initialParams = seqParams.optJSONObject("initialParams");
            final Long seqCmdID = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resourceID).SequentialCommandID;
            this.logger.log(Level.INFO, "Distributing pending collection for Kiosk seqCmdID{0}", seqCmdID);
            final Long associatedUser = SeqCmdUtils.getInstance().getAssociatedUserFromParams(initialParams, seqCmdID);
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("loggedOnUser", associatedUser);
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", true);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileColMap);
            ((Hashtable<String, String>)properties).put("commandName", "InstallApplication");
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, List>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Integer>)properties).put("platformtype", 2);
            ((Hashtable<String, Long>)properties).put("customerId", CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID));
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
    }
}
