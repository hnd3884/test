package com.me.devicemanagement.framework.server.resource;

import com.adventnet.persistence.DataAccess;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.Hashtable;
import java.util.logging.Logger;

public class ResourceDataProvider
{
    protected static final int READ_OPERATION = 4;
    protected static final int WRITE_OPERATION = 2;
    protected static Logger logger;
    protected static String sourceClass;
    private static Hashtable resourceTypes;
    private static DataObject resourcePersonalityCache;
    
    public static DataObject getNamespaces() throws SyMException {
        final String sourceMethod = "getNamespaces";
        final Column resTypeCol = Column.getColumn("Resource", "RESOURCE_TYPE");
        final Criteria criteria = new Criteria(resTypeCol, (Object)new Integer(100), 0);
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get("Resource", criteria);
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving namespaces details from DB: ", (Throwable)ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public static int getResourceType(final Long resourceId) throws SyMException {
        final String sourceMethod = "getResourceType";
        int resType = 111;
        try {
            final Row resRow = new Row("Resource");
            resRow.set("RESOURCE_ID", (Object)resourceId);
            final DataObject resultDO = SyMUtil.getPersistence().get("Resource", resRow);
            if (!resultDO.isEmpty()) {
                final Integer res = (Integer)resultDO.getFirstValue("Resource", "RESOURCE_TYPE");
                if (res != null) {
                    resType = res;
                }
            }
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving namespaces details from DB: ", (Throwable)ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resType;
    }
    
    public static Criteria getDomainCriteria(final String domainName) {
        final Column dnCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
        final Criteria criteria = new Criteria(dnCol, (Object)domainName, 0, false);
        return criteria;
    }
    
    public static DataObject getResourceDetails(final Long resourceId) throws SyMException {
        final String sourceMethod = "getResourceDetails";
        DataObject resultDO = null;
        try {
            final int resType = getResourceType(resourceId);
            final String personality = getPersonalityName(resType, 4);
            final Column resIdCol = Column.getColumn("Resource", "RESOURCE_ID");
            final Criteria cri = new Criteria(resIdCol, (Object)resourceId, 0);
            resultDO = SyMUtil.getPersistence().getForPersonality(personality, cri);
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB for resourceId: " + resourceId, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public static DataObject getResourceDetails(final int resourceType, final Criteria criteria) throws SyMException {
        final String sourceMethod = "getResourceDetails";
        DataObject resultDO = null;
        try {
            final String personality = getPersonalityName(resourceType, 4);
            resultDO = SyMUtil.getPersistence().getForPersonality(personality, criteria);
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB for resourceType: " + resourceType + "\t with criteria: " + criteria, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public static DataObject getResourceDetails(final int resourceType, final String domainName, Criteria criteria) throws SyMException {
        final String sourceMethod = "getResourceDetails";
        if (domainName != null) {
            final Criteria domainCri = getDomainCriteria(domainName);
            if (criteria != null) {
                criteria = criteria.and(domainCri);
            }
            else {
                criteria = domainCri;
            }
        }
        return getResourceDetails(resourceType, criteria);
    }
    
    public static DataObject getResourceDetails(final String name, final String domainName, final int resourceType) throws SyMException {
        final String sourceMethod = "getResourceDetails";
        final Column nameCol = Column.getColumn("Resource", "NAME");
        final Criteria nameCri = new Criteria(nameCol, (Object)name, 0, false);
        return getResourceDetails(resourceType, domainName, nameCri);
    }
    
    public static DataObject getResourceDetailsForWrite(final int resourceType, final Criteria criteria) throws SyMException {
        final String sourceMethod = "getResourceDetailsForWrite";
        DataObject resultDO = null;
        try {
            final String personality = getPersonalityName(resourceType, 2);
            resultDO = SyMUtil.getPersistence().getForPersonality(personality, criteria);
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB for resourceType: " + resourceType + "\t with criteria: " + criteria, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public static String getResourceName(final Long resourceId) throws SyMException {
        final String sourceMethod = "getResourceName";
        String resName = null;
        try {
            final Column resIdCol = Column.getColumn("Resource", "RESOURCE_ID");
            final Criteria cri = new Criteria(resIdCol, (Object)resourceId, 0);
            final DataObject resultDO = SyMUtil.getPersistence().get("Resource", cri);
            if (!resultDO.isEmpty()) {
                resName = (String)resultDO.getFirstValue("Resource", "NAME");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB for resourceId: " + resourceId, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resName;
    }
    
    public boolean checkIfTableEmpty(final String tableName, final Criteria criteria) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
            selectQuery.setRange(new Range(0, 1));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(criteria);
            final DataObject resultDO = SyMUtil.getPersistence().get(selectQuery);
            if (resultDO.isEmpty()) {
                return true;
            }
        }
        catch (final Exception ex) {
            ResourceDataProvider.logger.log(Level.SEVERE, "Caught exception while checking table if empty or not: ", ex);
        }
        return false;
    }
    
    public static Long getResourceIdFromDB(final String name, final String domainNetBIOSName, final Integer resourceType, final Long customerID, final String uniqueValue) throws SyMException {
        Row resRow = new Row("Resource");
        resRow.set("NAME", (Object)name);
        resRow.set("DOMAIN_NETBIOS_NAME", (Object)domainNetBIOSName);
        resRow.set("RESOURCE_TYPE", (Object)resourceType);
        if (customerID != null) {
            resRow.set("CUSTOMER_ID", (Object)customerID);
        }
        if (uniqueValue != null) {
            resRow.set("UNIQUE_VALUE", (Object)uniqueValue);
        }
        resRow = getResourceRowFromDB(resRow);
        if (resRow != null) {
            return (Long)resRow.get("RESOURCE_ID");
        }
        return null;
    }
    
    public static Row getResourceRowFromDB(Row resRow) throws SyMException {
        final String sourceMethod = "getResourceRowFromDB";
        try {
            final DataObject resultDO = getResourceDOFromDB(resRow);
            if (!resultDO.isEmpty()) {
                resRow = resultDO.getRow("Resource");
                return resRow;
            }
            return null;
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving Resource details from database.", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving Resource details from database.", ex2);
            throw new SyMException(1001, ex2);
        }
    }
    
    public static DataObject getResourceDOFromDB(final Row resRow) throws SyMException {
        final String sourceMethod = "getResourceDOFromDB";
        try {
            final Column nameCol = Column.getColumn("Resource", "NAME");
            Criteria criteria = new Criteria(nameCol, resRow.get("NAME"), 0, false);
            final Column dnCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
            final Criteria cri2 = new Criteria(dnCol, resRow.get("DOMAIN_NETBIOS_NAME"), 0, false);
            criteria = criteria.and(cri2);
            final Column typeCol = Column.getColumn("Resource", "RESOURCE_TYPE");
            final Criteria cri3 = new Criteria(typeCol, resRow.get("RESOURCE_TYPE"), 0);
            criteria = criteria.and(cri3);
            if (resRow.get("CUSTOMER_ID") != null) {
                final Column custCol = Column.getColumn("Resource", "CUSTOMER_ID");
                final Criteria cri4 = new Criteria(custCol, resRow.get("CUSTOMER_ID"), 0);
                criteria = criteria.and(cri4);
            }
            if (resRow.get("UNIQUE_VALUE") != null && SoMADUtil.getInstance().getUniqueIDStatus((Long)resRow.get("CUSTOMER_ID"))) {
                final Column custCol = Column.getColumn("Resource", "UNIQUE_VALUE");
                Criteria cri5 = new Criteria(custCol, resRow.get("UNIQUE_VALUE"), 0, false);
                cri5 = cri5.or(new Criteria(custCol, (Object)"--", 0, false));
                criteria = criteria.and(cri5);
            }
            final DataObject resultDO = SyMUtil.getPersistence().get("Resource", criteria);
            return resultDO;
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving Resource details from database.", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving Resource details from database.", ex2);
            throw new SyMException(1001, ex2);
        }
    }
    
    public static DataObject getResourceFromDBForWrite(final Row resRow) throws SyMException {
        return getResourceFromDB(resRow, 2);
    }
    
    public static DataObject getResourceFromDBForRead(final Row resRow) throws SyMException {
        return getResourceFromDB(resRow, 4);
    }
    
    private static DataObject getResourceFromDB(final Row resRow, final int operationType) throws SyMException {
        final String sourceMethod = "getResourceFromDB";
        DataObject resultDO = null;
        final int resourceType = (int)resRow.get("RESOURCE_TYPE");
        String persName = null;
        try {
            persName = getPersonalityName(resourceType, operationType);
            if (persName == null) {
                SyMLogger.warning(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Unable to retrieve resource details from DB. Personality is null for given resource type: " + resourceType);
                throw new SyMException(1002, "Personality is null for given resource type: " + resourceType, null);
            }
            final Column col1 = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
            Criteria crit1 = new Criteria(col1, resRow.get("DOMAIN_NETBIOS_NAME"), 0, false);
            final Column col2 = Column.getColumn("Resource", "NAME");
            final Criteria crit2 = new Criteria(col2, resRow.get("NAME"), 2, false);
            final Column col3 = Column.getColumn("Resource", "RESOURCE_TYPE");
            final Criteria crit3 = new Criteria(col3, (Object)new Integer(resourceType), 0);
            crit1 = crit1.and(crit2.and(crit3));
            if (resRow.get("UNIQUE_VALUE") != null && SoMADUtil.getInstance().getUniqueIDStatus((Long)resRow.get("CUSTOMER_ID"))) {
                Criteria cri5 = new Criteria(new Column("Resource", "UNIQUE_VALUE"), resRow.get("UNIQUE_VALUE"), 0, false);
                cri5 = cri5.or(new Criteria(new Column("Resource", "UNIQUE_VALUE"), (Object)"--", 0, false));
                crit1 = crit1.and(cri5);
            }
            resultDO = SyMUtil.getPersistence().getForPersonality(persName, crit1);
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB using personality: " + persName, (Throwable)ex);
            throw new SyMException(1002, "Caught exception while retrieving resource details from DB using personality: " + persName, ex.fillInStackTrace());
        }
        catch (final Exception ex2) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB using personality: " + persName, ex2);
            throw new SyMException(1002, "Caught exception while retrieving resource details from DB using personality: " + persName, ex2.fillInStackTrace());
        }
        return resultDO;
    }
    
    public static DataObject getResourceFromDBForWrite(final Criteria criteria, final String domainNetBIOSName, final int resourceType) throws SyMException {
        return getResourceFromDB(criteria, domainNetBIOSName, resourceType, 2);
    }
    
    public static DataObject getResourceFromDBForRead(final Criteria criteria, final String domainNetBIOSName, final int resourceType) throws SyMException {
        return getResourceFromDB(criteria, domainNetBIOSName, resourceType, 4);
    }
    
    private static DataObject getResourceFromDB(final Criteria criteria, final String domainNetBIOSName, final int resourceType, final int operationType) throws SyMException {
        final String sourceMethod = "getResourceFromDB";
        DataObject resultDO = null;
        String persName = null;
        try {
            persName = getPersonalityName(resourceType, operationType);
            if (persName == null) {
                SyMLogger.warning(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Unable to retrieve resource details from DB. Personality is null for given resource type: " + resourceType);
                throw new SyMException(1002, "Personality is null for given resource type: " + resourceType, null);
            }
            final Column resTypeCol = Column.getColumn("Resource", "RESOURCE_TYPE");
            Criteria cri = new Criteria(resTypeCol, (Object)new Integer(resourceType), 0);
            if (domainNetBIOSName != null) {
                final Column dnbCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
                final Criteria dnbCri = new Criteria(dnbCol, (Object)domainNetBIOSName, 0, false);
                cri = cri.and(dnbCri);
            }
            if (criteria != null) {
                cri = cri.and(criteria);
            }
            resultDO = SyMUtil.getPersistence().getForPersonality(persName, cri);
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB using personality: " + persName, (Throwable)ex);
            throw new SyMException(1002, "Caught exception while retrieving resource details from DB using personality: " + persName, ex.fillInStackTrace());
        }
        catch (final Exception ex2) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB using personality: " + persName, ex2);
            throw new SyMException(1002, "Caught exception while retrieving resource details from DB using personality: " + persName, ex2.fillInStackTrace());
        }
        return resultDO;
    }
    
    private static String getPersonalityName(final int resourceType, final int operationType) throws DataAccessException {
        final String sourceMethod = "getPersonalityName";
        String persName = null;
        final Column resTypeCol = Column.getColumn("ResourcePersonality", "RESOURCE_TYPE");
        Criteria cri = new Criteria(resTypeCol, (Object)new Integer(resourceType), 0);
        final Column oprTypeCol = Column.getColumn("ResourcePersonality", "OPERATION_TYPE");
        final Criteria oprTypeCri = new Criteria(oprTypeCol, (Object)new Integer(operationType), 0);
        cri = cri.and(oprTypeCri);
        final Row resultRow = getResourcePersonalityCachedDO().getRow("ResourcePersonality", cri);
        if (resultRow != null) {
            persName = (String)resultRow.get("PERSONALITY_NAME");
        }
        SyMLogger.debug(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Personality Name retrieved for resource type: " + resourceType + "\t with operation type: " + operationType + " is " + persName);
        return persName;
    }
    
    public static String getPersonalityNameForRead(final int resourceType) throws Exception {
        return getPersonalityName(resourceType, 4);
    }
    
    public static String getPersonalityNameForWrite(final int resourceType) throws Exception {
        return getPersonalityName(resourceType, 2);
    }
    
    public static String getResourceTypeName(final int resourceType) {
        if (ResourceDataProvider.resourceTypes == null || ResourceDataProvider.resourceTypes.isEmpty()) {
            loadResourceTypes();
            return ResourceDataProvider.resourceTypes.get(new Integer(resourceType));
        }
        return String.valueOf(resourceType);
    }
    
    private static void loadResourceTypes() {
        final String sourceMethod = "loadResourceTypes";
        try {
            ResourceDataProvider.resourceTypes = new Hashtable();
            final DataObject resTypesDO = SyMUtil.getPersistence().get("ResourceType", (Criteria)null);
            final Iterator rows = resTypesDO.getRows("ResourceType");
            while (rows.hasNext()) {
                final Row row = rows.next();
                ResourceDataProvider.resourceTypes.put(row.get("RESOURCE_TYPE_ID"), row.get("DISPLAY_LABEL"));
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while loading resource types with display labels in a hash.", ex);
        }
    }
    
    public static Row getResourceRowAlone(final Long resourceId) throws SyMException {
        final String sourceMethod = "getResourceRowAlone";
        Row resRow = new Row("Resource");
        resRow.set("RESOURCE_ID", (Object)resourceId);
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get("Resource", resRow);
            resRow = resultDO.getRow("Resource");
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB for resource id: " + resourceId, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resRow;
    }
    
    public static DataObject getResourceRowsAlone(final String domainName, final int resourceType) throws SyMException {
        final String sourceMethod = "getResourceRowsAlone";
        final Column resTypeCol = Column.getColumn("Resource", "RESOURCE_TYPE");
        Criteria criteria = new Criteria(resTypeCol, (Object)new Integer(resourceType), 0);
        if (domainName != null) {
            final Column dnColCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
            final Criteria dnNameCri = new Criteria(dnColCol, (Object)domainName, 0, false);
            criteria = criteria.and(dnNameCri);
        }
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get("Resource", criteria);
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB for domainName: " + domainName + " resourceType: " + resourceType, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public static DataObject getResourceRowsAlone(final String name, final String domainName, final Long customerID, final String uniqueValue) throws SyMException {
        final String sourceMethod = "getResourceRowsAlone";
        final Column resTypeCol = Column.getColumn("Resource", "CUSTOMER_ID");
        Criteria criteria = new Criteria(resTypeCol, (Object)customerID, 0);
        if (domainName != null) {
            final Column dnColCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME");
            final Criteria dnNameCri = new Criteria(dnColCol, (Object)domainName, 0, false);
            criteria = criteria.and(dnNameCri);
        }
        if (name != null) {
            final Column nameColCol = Column.getColumn("Resource", "NAME");
            final Criteria dnNameCri = new Criteria(nameColCol, (Object)name, 0, false);
            criteria = criteria.and(dnNameCri);
        }
        if (uniqueValue != null && !uniqueValue.equals("--") && SoMADUtil.getInstance().getUniqueIDStatus(customerID)) {
            Criteria uniqueCriteria = new Criteria(Column.getColumn("Resource", "UNIQUE_VALUE"), (Object)uniqueValue, 0, false);
            uniqueCriteria = uniqueCriteria.or(new Criteria(Column.getColumn("Resource", "UNIQUE_VALUE"), (Object)"--", 0, false));
            criteria = criteria.and(uniqueCriteria);
        }
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get("Resource", criteria);
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, sourceMethod, "Caught exception while retrieving resource details from DB for domainName: " + domainName + " customerID: " + customerID, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public static DataObject getResourceDO(final Criteria criteria) throws SyMException {
        DataObject resultDO = null;
        final SelectQuery resQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final List<Column> selectCols = new LinkedList<Column>();
        selectCols.add(Column.getColumn("Resource", "RESOURCE_ID"));
        selectCols.add(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        selectCols.add(Column.getColumn("Resource", "NAME"));
        selectCols.add(Column.getColumn("Resource", "UNIQUE_VALUE"));
        resQuery.addSelectColumns((List)selectCols);
        resQuery.setCriteria(criteria);
        try {
            resultDO = SyMUtil.getPersistence().get(resQuery);
        }
        catch (final Exception ex) {
            SyMLogger.error(ResourceDataProvider.logger, ResourceDataProvider.sourceClass, "getResourceDO", "Caught exception while retrieving resource DO from DB for criteria: " + criteria, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return resultDO;
    }
    
    public static DataObject getResourcePersonalityCachedDO() throws DataAccessException {
        if (ResourceDataProvider.resourcePersonalityCache == null) {
            ResourceDataProvider.resourcePersonalityCache = DataAccess.get("ResourcePersonality", (Criteria)null);
        }
        return ResourceDataProvider.resourcePersonalityCache;
    }
    
    static {
        ResourceDataProvider.logger = Logger.getLogger("ADLogger");
        ResourceDataProvider.sourceClass = "ResourceDataProvider";
        ResourceDataProvider.resourceTypes = null;
        ResourceDataProvider.resourcePersonalityCache = null;
    }
}
