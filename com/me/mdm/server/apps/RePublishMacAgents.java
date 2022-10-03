package com.me.mdm.server.apps;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Map;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import org.json.JSONObject;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class RePublishMacAgents implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public RePublishMacAgents() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void executeTask(final Properties props) {
        try {
            final Long startTime = System.currentTimeMillis();
            this.logger.log(Level.INFO, "Republish MAC agent task initiated at {0}", new Object[] { DateTimeUtil.longdateToString((long)startTime) });
            final List collectionList = this.rePublishMacAgents();
            this.deleteNotProperlyFormedAppConfigurationCommands();
            this.reAssociateAppConfigurationCommandForDevice(collectionList);
            this.updateSystemParamkey();
            final Long endTime = System.currentTimeMillis();
            this.logger.log(Level.INFO, "Republish MAC agent task initiated at {1}", new Object[] { DateTimeUtil.longdateToString((long)endTime) });
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in RePublishMacAgents task", ex);
        }
    }
    
    private List<Long> rePublishMacAgents() throws Exception {
        this.logger.log(Level.INFO, "******** Inside rePublishMacAgents() ********* ");
        final List<Long> republishedCollectionList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
        selectQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 1));
        final Criteria appIdentifierCriteria = new Criteria(new Column("MdAppDetails", "IDENTIFIER"), (Object)new String[] { "com.manageengine.ems", "com.manageengine.mdm.mac" }, 8);
        final Criteria malFormedCommandUUIDCriteria = new Criteria(new Column("MdCommands", "COMMAND_UUID"), (Object)new String[] { null, "InstallProfile;InstallProfile" }, 8);
        final Criteria commandTypeCriteria = new Criteria(new Column("MdCommands", "COMMAND_TYPE"), (Object)new String[] { null, "ApplicationConfiguration" }, 8);
        selectQuery.setCriteria(appIdentifierCriteria.and(malFormedCommandUUIDCriteria).and(commandTypeCriteria));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("MdAppToCollection", "APP_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "LAST_MODIFIED_BY"));
        selectQuery.setDistinct(true);
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (ds.next()) {
            final JSONObject jsonObject = new JSONObject();
            final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
            republishedCollectionList.add(collectionId);
            jsonObject.put("PROFILE_ID", ds.getValue("PROFILE_ID"));
            jsonObject.put("COLLECTION_ID", (Object)collectionId);
            jsonObject.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
            jsonObject.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
            jsonObject.put("APP_CONFIG", (Object)Boolean.TRUE);
            jsonObject.put("HAS_APP_CONFIGURATION", true);
            jsonObject.put("APP_ID", ds.getValue("APP_ID"));
            jsonObject.put("LAST_MODIFIED_BY", ds.getValue("LAST_MODIFIED_BY"));
            ProfileConfigHandler.publishProfile(jsonObject);
        }
        this.logger.log(Level.INFO, "Following MAC agents collections are republished {0}", new Object[] { republishedCollectionList });
        return republishedCollectionList;
    }
    
    private void deleteNotProperlyFormedAppConfigurationCommands() throws Exception {
        final Criteria commandTypeCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_TYPE"), (Object)"ApplicationConfiguration", 0);
        final Criteria commandUUIDCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)"InstallProfile;InstallProfile", 0);
        DataAccess.delete("MdCommands", commandTypeCriteria.and(commandUUIDCriteria));
        this.logger.log(Level.INFO, "Successfully Deleted Not properly formed MAC app configuration commands");
    }
    
    private void reAssociateAppConfigurationCommandForDevice(final List collectionList) throws Exception {
        this.logger.log(Level.INFO, "Re associating configuration command for the collections {0}", new Object[] { collectionList });
        if (!collectionList.isEmpty()) {
            final Map collectionToResourceListMap = new HashMap();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
            selectQuery.setCriteria(collectionCriteria);
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("RecentProfileForResource");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long collectionId = (Long)row.get("COLLECTION_ID");
                    final Long resourceId = (Long)row.get("RESOURCE_ID");
                    List resourceList = collectionToResourceListMap.get(collectionId);
                    if (resourceList == null) {
                        resourceList = new ArrayList();
                    }
                    resourceList.add(resourceId);
                    collectionToResourceListMap.put(collectionId, resourceList);
                }
            }
            this.logger.log(Level.INFO, "Collection to resource map for re association configuration command is {0}", new Object[] { collectionToResourceListMap });
            if (!collectionToResourceListMap.isEmpty()) {
                final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("MdCollectionCommand"));
                selectQuery2.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
                final Criteria commandTypeCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_TYPE"), (Object)"ApplicationConfiguration", 0);
                final Criteria commandCollectionCriteria = new Criteria(Column.getColumn("MdCollectionCommand", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
                selectQuery2.setCriteria(commandTypeCriteria.and(commandCollectionCriteria));
                selectQuery2.addSelectColumn(Column.getColumn("MdCollectionCommand", "*"));
                final DataObject mdCollectionCommandDO = DataAccess.get(selectQuery2);
                collectionToResourceListMap.forEach((a, b) -> {
                    try {
                        final Long collectionId2 = (Long)a;
                        final List resourceList2 = (List)b;
                        final Row commandRow = dataObject2.getRow("MdCollectionCommand", new Criteria(Column.getColumn("MdCollectionCommand", "COLLECTION_ID"), (Object)collectionId2, 0));
                        final Long commandId = (Long)commandRow.get("COMMAND_ID");
                        DeviceCommandRepository.getInstance().assignCommandToDevices(Arrays.asList(commandId), resourceList2);
                        this.logger.log(Level.INFO, "Successfully associated mac app configuration command {0} for resource {1}", new Object[] { a, b });
                    }
                    catch (final Exception ex) {
                        this.logger.log(Level.SEVERE, "Exception while re associating mac app configuration command {0} for the resource {1}", new Object[] { a, b });
                    }
                });
            }
        }
    }
    
    private void updateSystemParamkey() {
        MDMUtil.updateSyMParameter("RePublishMacAgents", "false");
    }
}
