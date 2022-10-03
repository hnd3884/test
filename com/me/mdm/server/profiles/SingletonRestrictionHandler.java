package com.me.mdm.server.profiles;

import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import org.json.JSONException;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.metracker.MEMDMTrackerUtil;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Logger;

public abstract class SingletonRestrictionHandler
{
    private static final Logger LOGGER;
    protected String request_type;
    
    public SingletonRestrictionHandler() {
        this.request_type = "SingletonRestriction";
    }
    
    public Long addSingletonRestrictionCommand() {
        Long commandId = DeviceCommandRepository.getInstance().getCommandID("SingletonRestriction");
        try {
            if (commandId == null) {
                commandId = DeviceCommandRepository.getInstance().addCommandWithPriority("SingletonRestriction", 40);
            }
        }
        catch (final Exception ex) {
            SingletonRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception while adding singleton command to the device", ex);
        }
        return commandId;
    }
    
    public Long addSingletonRestrictionCommand(final Long collectionId) {
        final List metaDataList = new ArrayList();
        final String commandUUID = "SingletonRestriction;Collection=" + collectionId.toString();
        Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
        if (commandId == null) {
            final Properties installProperties = new Properties();
            installProperties.setProperty("commandUUID", commandUUID);
            installProperties.setProperty("commandType", "SingletonRestriction");
            installProperties.setProperty("dynamicVariable", "false");
            metaDataList.add(installProperties);
            DeviceCommandRepository.getInstance().addCollectionCommand(collectionId, metaDataList);
            commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
        }
        return commandId;
    }
    
    public Long addRemoveSingletonRestrictionCommand() {
        Long commandId = DeviceCommandRepository.getInstance().getCommandID("RemoveSingletonRestriction");
        try {
            if (commandId == null) {
                commandId = DeviceCommandRepository.getInstance().addCommandWithPriority("SingletonRestriction", 40);
            }
        }
        catch (final Exception ex) {
            SingletonRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception while adding singleton command to the device", ex);
        }
        return commandId;
    }
    
    public Long addRemoveSingletonRestrictionCommand(final Long collectionId) {
        final List metaDataList = new ArrayList();
        final String commandUUID = "RemoveSingletonRestriction;Collection=" + collectionId.toString();
        Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
        if (commandId == null) {
            final Properties installProperties = new Properties();
            installProperties.setProperty("commandUUID", commandUUID);
            installProperties.setProperty("commandType", "RemoveSingletonRestriction");
            installProperties.setProperty("dynamicVariable", "false");
            metaDataList.add(installProperties);
            DeviceCommandRepository.getInstance().addCollectionCommand(collectionId, metaDataList);
            commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
        }
        return commandId;
    }
    
    public JSONObject prepareRestrictionSeqCmd(final int order) {
        final JSONObject restrictionObject = new JSONObject();
        try {
            restrictionObject.put("cmd_id", (Object)this.addSingletonRestrictionCommand());
            restrictionObject.put("handler", (Object)this.getSeqHandler());
            restrictionObject.put("order", order);
        }
        catch (final Exception ex) {
            SingletonRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception while preparing seq singleton restriction command", ex);
        }
        return restrictionObject;
    }
    
    public JSONObject prepareRestrictionSeqCmd(final int order, final Long collectionId) {
        final JSONObject restrictionObject = new JSONObject();
        try {
            restrictionObject.put("cmd_id", (Object)this.addSingletonRestrictionCommand(collectionId));
            restrictionObject.put("handler", (Object)this.getSeqHandler());
            restrictionObject.put("order", order);
        }
        catch (final Exception ex) {
            SingletonRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception while preparing seq singleton restriction collection command", ex);
        }
        return restrictionObject;
    }
    
    public JSONObject prepareRemoveRestrictionSeqCmd(final int order) {
        final JSONObject restrictionObject = new JSONObject();
        try {
            restrictionObject.put("cmd_id", (Object)this.addRemoveSingletonRestrictionCommand());
            restrictionObject.put("handler", (Object)this.getRemoveSeqHandler());
            restrictionObject.put("order", order);
        }
        catch (final Exception ex) {
            SingletonRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception while preparing seq singleton restriction command", ex);
        }
        return restrictionObject;
    }
    
    public JSONObject prepareRemoveRestrictionSeqCmd(final int order, final Long collectionId) {
        final JSONObject restrictionObject = new JSONObject();
        try {
            restrictionObject.put("cmd_id", (Object)this.addRemoveSingletonRestrictionCommand(collectionId));
            restrictionObject.put("handler", (Object)this.getRemoveSeqHandler());
            restrictionObject.put("order", order);
        }
        catch (final Exception ex) {
            SingletonRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception while preparing remove seq singleton restriction collection command", ex);
        }
        return restrictionObject;
    }
    
    public JSONObject getSingletonRestrictionConfigured(final Long resourceId) {
        return this.getSingletonRestrictionConfigured(resourceId, null, false);
    }
    
    public JSONObject getSingletonRestrictionConfigured(final Long resourceId, final Long collectionId, final boolean isRemoval) {
        Connection conn = null;
        DataSet ds = null;
        final JSONObject restrictionObject = new JSONObject();
        try {
            final String tableName = this.getRestrictionTable();
            final String deviceRestTableName = this.getDeviceRestrictionTable();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("ConfigDataItem", tableName, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", deviceRestTableName, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria appliedProfile = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria singletonRest = new Criteria(new Column("MdCommands", "COMMAND_TYPE"), (Object)this.request_type, 0);
            final Criteria criteria = resourceCriteria.and(appliedProfile).and(singletonRest);
            selectQuery.setCriteria(criteria);
            for (final String restrictionName : this.getRestrictionHash().keySet()) {
                final Boolean restrictionValue = this.getRestrictionHash().get(restrictionName);
                String deviceRestrictionName = this.getMappedRestrictionHash().get(restrictionName);
                if (MDMStringUtils.isEmpty(deviceRestrictionName)) {
                    deviceRestrictionName = restrictionName;
                }
                final String deviceRestrictionValue = this.getDeviceRestrictionHash().get(deviceRestrictionName);
                final Criteria restrictionCriteria = new Criteria(new Column(tableName, restrictionName), (Object)restrictionValue, 0);
                final Criteria succededCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)6, 0);
                Criteria strictRest = restrictionCriteria.and(succededCriteria);
                if (collectionId != null) {
                    Criteria collectionIdCriteria = null;
                    if (isRemoval) {
                        collectionIdCriteria = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 1);
                    }
                    else {
                        collectionIdCriteria = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
                    }
                    final Criteria collectionStatus = new Criteria(new Column("CollnToResources", "STATUS"), (Object)new Integer[] { 18, 3, 12, 16 }, 8).and(collectionIdCriteria);
                    strictRest = strictRest.or(restrictionCriteria.and(collectionStatus));
                }
                final Criteria deviceStatus = new Criteria(new Column(deviceRestTableName, deviceRestrictionName), (Object)deviceRestrictionValue, 0);
                if (!isRemoval) {
                    strictRest = strictRest.or(restrictionCriteria.and(deviceStatus));
                }
                final CaseExpression caseExpression = new CaseExpression(restrictionName);
                caseExpression.addWhen(strictRest, (Object)new Column(tableName, restrictionName));
                selectQuery.addSelectColumn(MEMDMTrackerUtil.getIntegerCountOfCaseExpression(caseExpression));
            }
            conn = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery((Query)selectQuery, conn);
            while (ds.next()) {
                for (final String columnName : this.getRestrictionHash().keySet()) {
                    final Integer value = (Integer)ds.getValue(columnName);
                    if (value > 0) {
                        restrictionObject.put(columnName, this.getRestrictionHash().get(columnName));
                    }
                }
            }
            SingletonRestrictionHandler.LOGGER.log(Level.INFO, "Strict restriction in the device.{0}", new Object[] { restrictionObject });
        }
        catch (final SQLException | QueryConstructionException | JSONException e) {
            SingletonRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception while getting the singleton restriction", e);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return restrictionObject;
    }
    
    protected abstract String getRestrictionTable();
    
    protected abstract String getDeviceRestrictionTable();
    
    protected abstract HashMap getRestrictionHash();
    
    protected abstract HashMap getDeviceRestrictionHash();
    
    protected abstract String getSeqHandler();
    
    protected abstract String getRemoveSeqHandler();
    
    protected abstract HashMap<String, String> getMappedRestrictionHash();
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
