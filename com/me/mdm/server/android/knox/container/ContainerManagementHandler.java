package com.me.mdm.server.android.knox.container;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.android.knox.enroll.KnoxActivationManager;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public class ContainerManagementHandler
{
    private static ContainerManagementHandler handler;
    private static final Logger LOGGER;
    
    public static ContainerManagementHandler getInstance() {
        return ContainerManagementHandler.handler;
    }
    
    public void processMessage(final HashMap<String, String> hmap) throws JSONException, Exception {
        final HashMap<String, String> msg = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject((String)hmap.get("Message")));
        this.processKnoxUpdateMessage(hmap);
    }
    
    private void processKnoxUpdateMessage(final HashMap<String, String> hmap) throws Exception {
        final String UUID = hmap.get("UDID");
        final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(UUID);
        final HashMap<String, String> msg = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject((String)hmap.get("Message")));
        final Integer knoxStatus = Integer.parseInt(msg.get("KnoxStatus"));
        final Integer containerState = Integer.parseInt(msg.get("ContainerState"));
        final String remarks = msg.get("Remarks");
        final Integer oldStatus = (Integer)DBUtil.getValueFromDB("ManagedKNOXContainer", "RESOURCE_ID", (Object)resourceId, "CONTAINER_STATUS");
        try {
            KnoxUtil.getInstance().updateStatus(resourceId, knoxStatus, remarks, containerState);
        }
        catch (final DataAccessException ex) {
            ContainerManagementHandler.LOGGER.log(Level.SEVERE, null, (Throwable)ex);
        }
        if (oldStatus != null && oldStatus == 20000 && knoxStatus == 20001) {
            this.associateProfilesToKnoxContainer(resourceId);
            DeviceInvCommandHandler.getInstance().scanContainer(resourceId);
            DeviceCommandRepository.getInstance().addSystemAppContainerCommand(resourceId);
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
        if (knoxStatus == 20002) {
            KnoxActivationManager.getInstance().removeAssociatedLicense(resourceId);
            AppsUtil.getInstance().handleAppsForContainerRemoved(resourceId);
            KnoxUtil.getInstance().clearAssociatedProfileForContainer(resourceId, "mdm.android.knox.profile.noContainer");
            KnoxUtil.getInstance().clearBlacklistedAppInContainer(resourceId);
            KnoxUtil.getInstance().clearInstalledAppInContainer(resourceId);
        }
    }
    
    private void associateProfilesToKnoxContainer(final Long resourceId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria profileScopeCriteria = new Criteria(new Column("Profile", "SCOPE"), (Object)1, 0);
        final Criteria profileTypeCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)1, 0);
        final Criteria profileMarkedForDeleteCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(resourceCriteria.and(profileScopeCriteria).and(profileTypeCriteria).and(profileMarkedForDeleteCriteria));
        sQuery.addJoin(profileJoin);
        final DataObject dO = SyMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final List commandList = new ArrayList();
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            final Iterator iter = dO.getRows("RecentProfileForResource");
            while (iter.hasNext()) {
                final Row row = iter.next();
                commandList.add(DBUtil.getValueFromDB("MdCollectionCommand", "COLLECTION_ID", (Object)row.get("COLLECTION_ID"), "COMMAND_ID"));
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, (Long)row.get("COLLECTION_ID"), 18);
            }
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
    }
    
    public String concatRemarksWithType(final String remarks, final int resultType) {
        if (resultType == 9101 || resultType == 9103) {
            return remarks + ".association";
        }
        return remarks + ".deassociation";
    }
    
    static {
        ContainerManagementHandler.handler = new ContainerManagementHandler();
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
