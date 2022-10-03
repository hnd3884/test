package com.me.mdm.server.updates.osupdates.task;

import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class OSUpdateStandardLicenseProfileRemovalTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    public void executeTask(final Properties props) {
        try {
            final String isNeedToExecute = MDMUtil.getSyMParameter("OSUpdateStandardLicenseRemoval");
            if (!MDMStringUtils.isEmpty(isNeedToExecute) && Boolean.valueOf(isNeedToExecute)) {
                OSUpdateStandardLicenseProfileRemovalTask.LOGGER.log(Level.INFO, "Handling OSUpdate removal in scheduler");
                this.handleLicenseChangeForOSUpdate(true);
                MDMUtil.updateSyMParameter("OSUpdateStandardLicenseRemoval", "false");
            }
        }
        catch (final Exception e) {
            OSUpdateStandardLicenseProfileRemovalTask.LOGGER.log(Level.SEVERE, "Exception in License Change of existing customer", e);
        }
    }
    
    public void handleLicenseChangeForOSUpdate(final boolean notifyDevice) throws Exception {
        this.handleLicenseChangeForOSUpdate(notifyDevice, MDMUtil.getInstance().getCurrentlyLoggedOnUserID());
    }
    
    public void handleLicenseChangeForOSUpdate(final boolean notifyDevice, final Long userId) throws Exception {
        try {
            final OSUpdatePolicyHandler osUpdatePolicyHandler = new OSUpdatePolicyHandler();
            final List<Long> profileIds = osUpdatePolicyHandler.getOSUpdatePolicyId(new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0));
            if (!profileIds.isEmpty()) {
                OSUpdateStandardLicenseProfileRemovalTask.LOGGER.log(Level.INFO, "Handling License Change for OSupdate Profile:{0}", profileIds);
                for (final Long profileId : profileIds) {
                    final List deviceProfileId = new ArrayList();
                    deviceProfileId.add(profileId);
                    final List resourceIds = osUpdatePolicyHandler.getManagedDevicesAssignedForProfiles(deviceProfileId);
                    final List groupIds = osUpdatePolicyHandler.getManagedGroupsAssignedForProfiles(deviceProfileId);
                    final JSONObject msgHeaderJSON = new JSONObject();
                    msgHeaderJSON.put("REMOVE_EVENT_LOG", true);
                    msgHeaderJSON.put("USER_ID", (Object)userId);
                    final JSONObject resourceJSON = new JSONObject();
                    resourceJSON.put("PROFILE_IDS", (Object)new JSONArray((Collection)deviceProfileId));
                    resourceJSON.put("DEVICE_IDS", (Object)new JSONArray((Collection)resourceIds));
                    resourceJSON.put("GROUP_IDS", (Object)new JSONArray((Collection)groupIds));
                    if (!notifyDevice) {
                        resourceJSON.put("NOTIFY_DEVICE", false);
                    }
                    osUpdatePolicyHandler.removeDistributedOSUpdatePolicy(msgHeaderJSON, resourceJSON);
                }
            }
        }
        catch (final DataAccessException e) {
            OSUpdateStandardLicenseProfileRemovalTask.LOGGER.log(Level.SEVERE, "Exception in osupdate license removal", (Throwable)e);
            throw e;
        }
        catch (final QueryConstructionException e2) {
            OSUpdateStandardLicenseProfileRemovalTask.LOGGER.log(Level.SEVERE, "Exception in osupdate license removal", (Throwable)e2);
            throw e2;
        }
        catch (final JSONException e3) {
            OSUpdateStandardLicenseProfileRemovalTask.LOGGER.log(Level.SEVERE, "Exception in osupdate license removal", (Throwable)e3);
            throw e3;
        }
        catch (final Exception e4) {
            OSUpdateStandardLicenseProfileRemovalTask.LOGGER.log(Level.SEVERE, "Exception in osupdate license removal", e4);
            throw e4;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
