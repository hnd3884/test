package com.me.mdm.server.resource;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.resource.ResourceDataProvider;

public class MDMResourceDataProvider extends ResourceDataProvider
{
    public static DataObject getResourceDOFromDBForMDMDevice(final Row resRow, final String sUDID) throws SyMException {
        final String sourceMethod = "getResourceDOFromDBForMDMDevice";
        final Properties props = new Properties();
        ((Hashtable<String, Object>)props).put("NAME", resRow.get("NAME"));
        ((Hashtable<String, Object>)props).put("DOMAIN_NETBIOS_NAME", resRow.get("DOMAIN_NETBIOS_NAME"));
        ((Hashtable<String, Object>)props).put("RESOURCE_TYPE", resRow.get("RESOURCE_TYPE"));
        ((Hashtable<String, Object>)props).put("CUSTOMER_ID", resRow.get("CUSTOMER_ID"));
        try {
            return getResourceDOFromDBForMDMDevice(props, sUDID);
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(MDMResourceDataProvider.logger, MDMResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving Resource details from database.", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            SyMLogger.error(MDMResourceDataProvider.logger, MDMResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving Resource details from database.", (Throwable)ex2);
            throw new SyMException(1001, (Throwable)ex2);
        }
    }
    
    public static DataObject getResourceDOFromDBForMDMDevice(final Properties props, final String sUDID) throws Exception {
        final Column nameCol = Column.getColumn("Resource", "NAME");
        Criteria criteria = new Criteria(nameCol, ((Hashtable<K, Object>)props).get("NAME"), 0, false);
        final Column dnCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
        final Criteria cri2 = new Criteria(dnCol, ((Hashtable<K, Object>)props).get("DOMAIN_NETBIOS_NAME"), 0, false);
        criteria = criteria.and(cri2);
        final Column typeCol = Column.getColumn("Resource", "RESOURCE_TYPE");
        final Criteria cri3 = new Criteria(typeCol, ((Hashtable<K, Object>)props).get("RESOURCE_TYPE"), 0);
        criteria = criteria.and(cri3);
        if (props.get("CUSTOMER_ID") != null) {
            final Column custCol = Column.getColumn("Resource", "CUSTOMER_ID");
            final Criteria cri4 = new Criteria(custCol, ((Hashtable<K, Object>)props).get("CUSTOMER_ID"), 0);
            criteria = criteria.and(cri4);
        }
        if (sUDID != null) {
            final Column udidCol = Column.getColumn("ManagedDevice", "UDID");
            final Criteria udidCri = new Criteria(udidCol, (Object)sUDID, 0);
            criteria = criteria.and(udidCri);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        final Join managedDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(managedDeviceJoin);
        selectQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        selectQuery.setCriteria(criteria);
        final DataObject resultDO = MDMUtil.getPersistence().get(selectQuery);
        return resultDO;
    }
    
    public static HashMap getResourceNames(final List resourceIds) throws SyMException {
        final String sourceMethod = "getResourceName";
        final HashMap resourceIdVsName = new HashMap();
        try {
            final Column resIdCol = Column.getColumn("Resource", "RESOURCE_ID");
            final Criteria cri = new Criteria(resIdCol, (Object)resourceIds.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addSelectColumn(resIdCol);
            selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            selectQuery.setCriteria(cri);
            final DataObject resultDO = SyMUtil.getPersistence().get(selectQuery);
            if (!resultDO.isEmpty()) {
                final Iterator<Row> rows = resultDO.getRows("Resource");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    resourceIdVsName.put(row.get("RESOURCE_ID"), row.get("NAME"));
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataProvider.logger, MDMResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB ", (Throwable)ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resourceIdVsName;
    }
    
    public static List<String> getResourceNamesList(final List<Long> managedDeviceIDs) {
        final List<String> resourceNamesList = new ArrayList<String>();
        try {
            final Column resIdCol = Column.getColumn("Resource", "RESOURCE_ID");
            final Criteria cri = new Criteria(resIdCol, (Object)managedDeviceIDs.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addSelectColumn(resIdCol);
            selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            selectQuery.setCriteria(cri);
            final DataObject resultDO = SyMUtil.getPersistence().get(selectQuery);
            if (!resultDO.isEmpty()) {
                final Iterator itr = resultDO.get("Resource", "NAME");
                while (itr.hasNext()) {
                    resourceNamesList.add(itr.next().toString());
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMResourceDataProvider.logger, MDMResourceDataProvider.sourceClass, "getResourceNames", "Exception in MSP device migration.. ", (Throwable)ex);
        }
        return resourceNamesList;
    }
}
