package com.me.idps.mdm.sync;

import java.util.Hashtable;
import java.util.Iterator;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.List;
import com.me.idps.core.util.DirectoryQueryutil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import java.util.Properties;
import com.me.idps.core.util.DirectoryGroupOnConfig;
import com.me.idps.core.sync.events.DirectoryEventsUtil;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.adventnet.ds.query.SelectQuery;
import java.sql.Connection;
import com.me.idps.core.util.DirectoryAttributeConstants;
import java.util.HashMap;
import com.me.mdm.directory.sync.mdm.MDMDirectoryProductImpl;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;

class DirectoryGrouper
{
    private static final String BLOCK = "MDMDirectoryDataPersistor";
    private static DirectoryGrouper directoryGrouper;
    
    static DirectoryGrouper getInstance() {
        if (DirectoryGrouper.directoryGrouper == null) {
            DirectoryGrouper.directoryGrouper = new DirectoryGrouper();
        }
        return DirectoryGrouper.directoryGrouper;
    }
    
    Criteria getCGcri() {
        final MDMDirectoryProductImpl mdmIDPSimpl = MDMIdpsUtil.getMDMdirProdImpl();
        final int cgType = mdmIDPSimpl.getDirectoryCGType();
        final int cgCategory = mdmIDPSimpl.getDirectoryCGCategory();
        return new Criteria(Column.getColumn("CustomGroup", "IS_EDITABLE"), (Object)false, 0).and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)cgType, 0)).and(new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)cgCategory, 0));
    }
    
    private HashMap<String, Column> getCGrelColMap(final Long groupOnAttr) {
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        final Column dirResRelCol = new Column("DirObjArrLngVal", "VALUE_RESOURCE_ID");
        final Column dirObjAttrValCol = new Column("DirObjArrLngVal", "RESOURCE_ID");
        if (DirectoryAttributeConstants.isGroupOnOfType(groupOnAttr)) {
            dirResRelCol.setColumnAlias("group.RES_ID");
            dirObjAttrValCol.setColumnAlias("member.RES_ID");
            colMap.put("GROUP_RESOURCE_ID", dirResRelCol);
            colMap.put("MEMBER_RESOURCE_ID", dirObjAttrValCol);
        }
        else {
            dirResRelCol.setColumnAlias("member.RES_ID");
            dirObjAttrValCol.setColumnAlias("group.RES_ID");
            colMap.put("MEMBER_RESOURCE_ID", dirResRelCol);
            colMap.put("GROUP_RESOURCE_ID", dirObjAttrValCol);
        }
        return colMap;
    }
    
    private Long persistCGRelListenerEvents(final Connection connection, final Long dmDomainID, final Long collationID, final SelectQuery selectQuery, final HashMap<String, Column> colMap, final IdpEventConstants eventType) throws Exception {
        final Column cgCol = colMap.get("GROUP_RESOURCE_ID");
        final Column memberCol = colMap.get("MEMBER_RESOURCE_ID");
        final Column timeStampCol = (Column)memberCol.clone();
        timeStampCol.setColumnAlias("DIRECTORYEVENTDETAILS.EVENT_TIME_STAMP");
        final Long dirEventID = DirectoryEventsUtil.getInstance().populateEvents(connection, dmDomainID, collationID, eventType, selectQuery, cgCol, timeStampCol, memberCol);
        return dirEventID;
    }
    
    private Criteria getRelTypeCriteria(final DirectoryGroupOnConfig directoryGroupOnConfig, final boolean groupType) {
        final boolean isAttrOfType = DirectoryAttributeConstants.isGroupOnOfType(directoryGroupOnConfig.groupOnAttr);
        final boolean parity = isAttrOfType == groupType;
        return new Criteria(Column.getColumn("DirObjArrLngVal", parity ? "VALUE_DIR_RESOURCE_TYPE" : "DIR_RESOURCE_TYPE"), groupType ? directoryGroupOnConfig.groupResType : directoryGroupOnConfig.getMemberTypesInArray(), groupType ? 0 : 8);
    }
    
    private Properties getDeleteCGRelQuery(final Long dmDomainID, final HashMap<String, Column> colMap, final DirectoryGroupOnConfig directoryGroupOnConfig) {
        final boolean isAttrOfType = DirectoryAttributeConstants.isGroupOnOfType(directoryGroupOnConfig.groupOnAttr);
        final Join cgJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join dirJoin = new Join("CustomGroup", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join domainJoin = new Join("DirResRel", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2);
        final Join cgDirRelJoin = new Join("CustomGroupMemberRel", "DirObjArrLngVal", new String[] { "GROUP_RESOURCE_ID", "MEMBER_RESOURCE_ID" }, new String[] { isAttrOfType ? "VALUE_RESOURCE_ID" : "RESOURCE_ID", isAttrOfType ? "RESOURCE_ID" : "VALUE_RESOURCE_ID" }, 1);
        final Criteria criteria = this.getCGcri().and(new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"), (Object)directoryGroupOnConfig.groupResType, 0)).and(new Criteria(Column.getColumn("DirObjArrLngVal", "RESOURCE_ID"), (Object)null, 0).or(new Criteria(Column.getColumn("DirObjArrLngVal", "VALUE_RESOURCE_ID"), (Object)null, 0)));
        final Column cgRelGcol = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID", colMap.get("GROUP_RESOURCE_ID").getColumnAlias());
        final Column cgRelMcol = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID", colMap.get("MEMBER_RESOURCE_ID").getColumnAlias());
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        selectQuery.addJoin(cgJoin);
        selectQuery.addJoin(dirJoin);
        selectQuery.addJoin(domainJoin);
        selectQuery.addJoin(cgDirRelJoin);
        selectQuery.setCriteria(criteria);
        colMap.put("GROUP_RESOURCE_ID", cgRelGcol);
        colMap.put("MEMBER_RESOURCE_ID", cgRelMcol);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("CustomGroupMemberRel");
        deleteQuery.addJoin(cgJoin);
        deleteQuery.addJoin(dirJoin);
        deleteQuery.addJoin(domainJoin);
        deleteQuery.addJoin(cgDirRelJoin);
        deleteQuery.setCriteria(criteria);
        final Properties props = new Properties();
        ((Hashtable<String, SelectQuery>)props).put(SelectQuery.class.getSimpleName(), selectQuery);
        ((Hashtable<String, DeleteQuery>)props).put(DeleteQuery.class.getSimpleName(), deleteQuery);
        return props;
    }
    
    private SelectQuery getInsertCGrelSelectQuery(final Long dmDomainID, final DirectoryGroupOnConfig directoryGroupOnConfig) {
        final boolean isAttrOfType = DirectoryAttributeConstants.isGroupOnOfType(directoryGroupOnConfig.groupOnAttr);
        final SelectQuery dirRelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
        dirRelQuery.addJoin(new Join("DMDomain", "DirObjArrLngVal", new String[] { "DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 2));
        dirRelQuery.addJoin(new Join("DirObjArrLngVal", "CustomGroup", new String[] { isAttrOfType ? "VALUE_RESOURCE_ID" : "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        dirRelQuery.addJoin(new Join("DirObjArrLngVal", "CustomGroupMemberRel", new String[] { isAttrOfType ? "VALUE_RESOURCE_ID" : "RESOURCE_ID", isAttrOfType ? "RESOURCE_ID" : "VALUE_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID", "MEMBER_RESOURCE_ID" }, 1));
        dirRelQuery.setCriteria(this.getCGcri().and(this.getRelTypeCriteria(directoryGroupOnConfig, true)).and(this.getRelTypeCriteria(directoryGroupOnConfig, false)).and(new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirObjArrLngVal", "ATTR_ID"), (Object)directoryGroupOnConfig.groupOnAttr, 0)).and(new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 0).or(new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)null, 0))));
        return dirRelQuery;
    }
    
    private void deleteCGrelAndInvokeListeners(final Connection connection, final Long dmDomainID, final Long collationID, final HashMap<String, Column> colMap, final DirectoryGroupOnConfig directoryGroupOnConfig) throws Exception {
        final Properties props = this.getDeleteCGRelQuery(dmDomainID, colMap, directoryGroupOnConfig);
        IDPSlogger.DBO.log(Level.INFO, "deleting from cg rel");
        final Long dirEventID = this.persistCGRelListenerEvents(connection, dmDomainID, collationID, ((Hashtable<K, SelectQuery>)props).get(SelectQuery.class.getSimpleName()), colMap, IdpEventConstants.MEMBER_REMOVED_EVENT);
        IDPSlogger.DBO.log(Level.INFO, "finished getting delete cg rel events data");
        final int totalDeletedRows = DirectoryQueryutil.getInstance().executeDeleteQuery(connection, ((Hashtable<K, DeleteQuery>)props).get(DeleteQuery.class.getSimpleName()), false);
        IDPSlogger.DBO.log(Level.INFO, "finished removing from cg rel");
        DirectoryQueryutil.getInstance().incrementDbOpsMetric(dmDomainID, collationID, "MDMDirectoryDataPersistor", 3, totalDeletedRows);
        DirectoryEventsUtil.getInstance().markEventSucceeded(connection, dmDomainID, dirEventID);
    }
    
    private void insertCGrelAndGetListenerEvents(final Connection connection, final Long dmDomainID, final Long collationID, final HashMap<String, Column> colMap, final DirectoryGroupOnConfig directoryGroupOnConfig) throws Exception {
        final SelectQuery selectQuery = this.getInsertCGrelSelectQuery(dmDomainID, directoryGroupOnConfig);
        IDPSlogger.DBO.log(Level.INFO, "inserting into cg rel");
        final Long dirEventID = this.persistCGRelListenerEvents(connection, dmDomainID, collationID, selectQuery, colMap, IdpEventConstants.MEMBER_ADDED_EVENT);
        IDPSlogger.DBO.log(Level.INFO, "finished getting add cg rel events data");
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, selectQuery, "CustomGroupMemberRel", colMap, null, "MDMDirectoryDataPersistor", null, false);
        IDPSlogger.DBO.log(Level.INFO, "finished inserting into cg rel");
        DirectoryEventsUtil.getInstance().markEventSucceeded(connection, dmDomainID, dirEventID);
    }
    
    void insertAndDelCGrel(final Connection connection, final Long dmDomainID, final Long collationID, final Integer dmDomainClient, final List<Integer> objectsToBeSynced) throws Exception {
        final List<DirectoryGroupOnConfig> groupOnDetails = IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClient).getGroupOnProps(objectsToBeSynced);
        for (final DirectoryGroupOnConfig directoryGroupOnConfig : groupOnDetails) {
            this.insertCGrelAndGetListenerEvents(connection, dmDomainID, collationID, this.getCGrelColMap(directoryGroupOnConfig.groupOnAttr), directoryGroupOnConfig);
            this.deleteCGrelAndInvokeListeners(connection, dmDomainID, collationID, this.getCGrelColMap(directoryGroupOnConfig.groupOnAttr), directoryGroupOnConfig);
        }
    }
    
    static {
        DirectoryGrouper.directoryGrouper = null;
    }
}
