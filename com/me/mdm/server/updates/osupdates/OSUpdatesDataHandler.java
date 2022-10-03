package com.me.mdm.server.updates.osupdates;

import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.updates.ManagedUpdatesDataHandler;

public class OSUpdatesDataHandler extends ManagedUpdatesDataHandler
{
    private final Logger logger;
    private static final Logger CONFIGLOGGER;
    
    public OSUpdatesDataHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    protected Long addNewOSUpdate(final JSONObject dataJson, final ExtendedOSDetailsDataHandler extnDataHandler) throws Exception {
        final Long updateID = this.addNewManagedUpdate(dataJson);
        final DataObject dO = MDMUtil.getPersistence().constructDataObject();
        final Row osUpdatesRow = new Row("OSUpdates");
        osUpdatesRow.set("UPDATE_ID", (Object)updateID);
        osUpdatesRow.set("VERSION", (Object)String.valueOf(dataJson.get("VERSION")));
        osUpdatesRow.set("DOWNLOAD_SIZE", (Object)dataJson.optLong("DOWNLOAD_SIZE", -1L));
        osUpdatesRow.set("INSTALL_SIZE", (Object)dataJson.optLong("INSTALL_SIZE", -1L));
        osUpdatesRow.set("RESTART_REQUIRED", (Object)dataJson.optBoolean("RESTART_REQUIRED", true));
        dO.addRow(osUpdatesRow);
        if (extnDataHandler != null) {
            final Row extnRow = extnDataHandler.getExtnOSDetailsNewRow(dataJson);
            extnRow.set("UPDATE_ID", (Object)updateID);
            dO.addRow(extnRow);
        }
        MDMUtil.getPersistence().update(dO);
        this.logger.log(Level.INFO, "New entries in ManagedOSUpdate and OSUpdates added. ID: {0}", new Object[] { updateID });
        return updateID;
    }
    
    public DataObject getOSUpdateDetails(final Long osUpdateID, final ExtendedOSDetailsDataHandler extnHandler) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedUpdates"));
        final Join osdetailsJoin = new Join("ManagedUpdates", "OSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
        selectQuery.addJoin(osdetailsJoin);
        if (extnHandler != null) {
            selectQuery.addJoin(extnHandler.getOSUpdateDetailsExtnJoin());
        }
        final Criteria criteria = new Criteria(Column.getColumn("ManagedUpdates", "UPDATE_ID"), (Object)osUpdateID, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public boolean isOSUpdatePolicyApplicableForResource(final Long resourceId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
            selectQuery.addJoin(new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "OSUpdatePolicy", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria osPolicyCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)3, 0);
            final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria markForDelete = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            selectQuery.setCriteria(osPolicyCriteria.and(resourceCriteria).and(markForDelete));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                OSUpdatesDataHandler.CONFIGLOGGER.log(Level.INFO, "iOS update policy applicable");
                return true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while checking os update policy for resource", ex);
        }
        return false;
    }
    
    public List<Long> getOSUpdateCommandForResourceOnCriteria(final List resourceList, final Criteria criteria) {
        final List<Long> commandIdList = new ArrayList<Long>();
        DMDataSetWrapper dataSet = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            selectQuery.addJoin(new Join("MdCommands", "MdCommandsToDevice", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            final Criteria resourceCriteria = new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria OSUpdateCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)3, 0);
            final Criteria commandStatus = new Criteria(new Column("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS"), (Object)12, 0);
            Criteria finalCriteria = resourceCriteria.and(OSUpdateCriteria).and(commandStatus);
            if (criteria != null) {
                finalCriteria = finalCriteria.and(criteria);
            }
            selectQuery.setCriteria(finalCriteria);
            selectQuery.addSelectColumn(new Column("MdCommandsToDevice", "COMMAND_ID"));
            final List columnNames = new ArrayList();
            columnNames.add(new Column("MdCommandsToDevice", "COMMAND_ID"));
            final GroupByClause groupByClause = new GroupByClause(columnNames);
            selectQuery.setGroupByClause(groupByClause);
            dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                final Long commandId = (Long)dataSet.getValue("COMMAND_ID");
                commandIdList.add(commandId);
            }
            OSUpdatesDataHandler.CONFIGLOGGER.log(Level.INFO, "OSUpdate command for resource:Command List-{0}", new Object[] { commandIdList });
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting osupdate command for resource", ex);
        }
        return commandIdList;
    }
    
    static {
        CONFIGLOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
