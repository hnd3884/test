package com.me.mdm.api.command.schedule;

import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScheduledCollectionToResourceHandler
{
    private static Logger logger;
    private static ScheduledCollectionToResourceHandler scheduledCollectionToResourceHandler;
    
    public static ScheduledCollectionToResourceHandler getInstance() {
        if (ScheduledCollectionToResourceHandler.scheduledCollectionToResourceHandler == null) {
            ScheduledCollectionToResourceHandler.scheduledCollectionToResourceHandler = new ScheduledCollectionToResourceHandler();
        }
        return ScheduledCollectionToResourceHandler.scheduledCollectionToResourceHandler;
    }
    
    public List getCommandRespositoryTypeForCollection(final Long collectionID) {
        List commandRepositoryTypes = new ArrayList();
        try {
            ScheduledCollectionToResourceHandler.logger.log(Level.INFO, "getting commandRepositoryType for the given collectionID{0}", collectionID);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCollectionToResource"));
            final Column resourceIDColumn = new Column("ScheduledCollectionToResource", "RESOURCE_ID");
            final Column collectionIDColumn = new Column("ScheduledCollectionToResource", "COLLECTION_ID");
            final Column commandRepositoryTypeColumn = new Column("ScheduledCollectionToResource", "COMMAND_REPOSITORY_TYPE");
            final Criteria c = new Criteria(collectionIDColumn, (Object)collectionID, 0);
            sq.addSelectColumn(collectionIDColumn);
            sq.addSelectColumn(resourceIDColumn);
            sq.addSelectColumn(commandRepositoryTypeColumn);
            sq.setCriteria(c);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator iter = dataObject.getRows("ScheduledCollectionToResource");
            commandRepositoryTypes = DBUtil.getColumnValuesAsList(iter, "COMMAND_REPOSITORY_TYPE");
        }
        catch (final Exception e) {
            ScheduledCollectionToResourceHandler.logger.log(Level.SEVERE, "Exception while trying to getCommandRespositoryTypeForCollection:{0}", collectionID);
            ScheduledCollectionToResourceHandler.logger.log(Level.SEVERE, "Exception in getCommandRespositoryTypeForCollection", e);
        }
        return commandRepositoryTypes;
    }
    
    public List getResourcesForCollectionAndCommandRepositoryType(final Long collectionID, final Long commandRepositoryType) {
        List resourceList = new ArrayList();
        try {
            ScheduledCollectionToResourceHandler.logger.log(Level.INFO, "Fetching Resources for collection:{0}", collectionID);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCollectionToResource"));
            final Column resourceIDColumn = new Column("ScheduledCollectionToResource", "RESOURCE_ID");
            final Column collectionIDColumn = new Column("ScheduledCollectionToResource", "COLLECTION_ID");
            final Column commandRepositoryTypeColumn = new Column("ScheduledCollectionToResource", "COMMAND_REPOSITORY_TYPE");
            final Criteria c = new Criteria(collectionIDColumn, (Object)collectionID, 0);
            final Criteria commandRepositoryTypeCriteria = new Criteria(commandRepositoryTypeColumn, (Object)commandRepositoryType, 0);
            sq.addSelectColumn(collectionIDColumn);
            sq.addSelectColumn(resourceIDColumn);
            sq.addSelectColumn(commandRepositoryTypeColumn);
            sq.setCriteria(c.and(commandRepositoryTypeCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator iter = dataObject.getRows("ScheduledCollectionToResource");
            resourceList = DBUtil.getColumnValuesAsList(iter, "RESOURCE_ID");
            ScheduledCollectionToResourceHandler.logger.log(Level.INFO, "Resources for collectionID:{0} is {1}", new Object[] { collectionID, resourceList });
        }
        catch (final Exception e) {
            ScheduledCollectionToResourceHandler.logger.log(Level.SEVERE, "Exception in getResourcesForCollection,Fetching the resource for collectionID{0} has failed because{1}", new Object[] { collectionID, e });
        }
        return ScheduleCommandService.getUniqueListItems((ArrayList)resourceList);
    }
    
    public List getResourcesForCollections(final List collectionIDs) {
        List resourceList = new ArrayList();
        try {
            ScheduledCollectionToResourceHandler.logger.log(Level.INFO, "Fetching Resources for collection:{0}", collectionIDs);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCollectionToResource"));
            final Column resourceIDColumn = new Column("ScheduledCollectionToResource", "RESOURCE_ID");
            final Column collectionIDColumn = new Column("ScheduledCollectionToResource", "COLLECTION_ID");
            final Column commandRepositoryTypeColumn = new Column("ScheduledCollectionToResource", "COMMAND_REPOSITORY_TYPE");
            final Criteria c = new Criteria(collectionIDColumn, (Object)collectionIDs.toArray(), 8);
            sq.addSelectColumn(collectionIDColumn);
            sq.addSelectColumn(resourceIDColumn);
            sq.addSelectColumn(commandRepositoryTypeColumn);
            sq.setCriteria(c);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator iter = dataObject.getRows("ScheduledCollectionToResource");
            resourceList = DBUtil.getColumnValuesAsList(iter, "RESOURCE_ID");
            ScheduledCollectionToResourceHandler.logger.log(Level.INFO, "Resources for collectionID:{0} is {1}", new Object[] { collectionIDs, resourceList });
        }
        catch (final Exception e) {
            ScheduledCollectionToResourceHandler.logger.log(Level.SEVERE, "Exception in getResourcesForCollection,Fetching the resource for collectionID{0} has failed because{1}", new Object[] { collectionIDs, e });
        }
        return ScheduleCommandService.getUniqueListItems((ArrayList)resourceList);
    }
    
    public void deleteScheduledCollectionToResource(final List collectionIDs, final List resourceIDs) {
        try {
            ScheduledCollectionToResourceHandler.logger.log(Level.INFO, "deleting in ScheduledCommandToResource for collectionIds{0} and resourceIDs{1}", new Object[] { collectionIDs, resourceIDs });
            final Criteria c = new Criteria(new Column("ScheduledCollectionToResource", "COLLECTION_ID"), (Object)collectionIDs.toArray(), 8);
            final Criteria resourceCriteria = new Criteria(new Column("ScheduledCollectionToResource", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            DataAccess.delete("ScheduledCollectionToResource", c.and(resourceCriteria));
        }
        catch (final Exception e) {
            ScheduledCollectionToResourceHandler.logger.log(Level.SEVERE, "Error occured while trying to delete for Resources{1} and collection:{0}", new Object[] { collectionIDs, resourceIDs });
            ScheduledCollectionToResourceHandler.logger.log(Level.SEVERE, "Exception occurred in deleteScheduledCollectionToResource()", e);
        }
    }
    
    public void addCollectionToResource(final Long resourceID, final Long collectionID, final Integer commandRepositoryType) {
        try {
            ScheduledCollectionToResourceHandler.logger.log(Level.INFO, "Adding resource{0} for the collectionID{1}", new Object[] { resourceID, collectionID });
            final Row r = new Row("ScheduledCollectionToResource");
            r.set("RESOURCE_ID", (Object)resourceID);
            r.set("COLLECTION_ID", (Object)collectionID);
            r.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
            r.set("STATUS", (Object)1);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(r);
            MDMUtil.getPersistence().add(dataObject);
        }
        catch (final Exception e) {
            ScheduledCollectionToResourceHandler.logger.log(Level.SEVERE, "Exception in addCollectionToResource", e);
        }
    }
    
    static {
        ScheduledCollectionToResourceHandler.logger = Logger.getLogger("ActionsLogger");
        ScheduledCollectionToResourceHandler.scheduledCollectionToResourceHandler = null;
    }
}
