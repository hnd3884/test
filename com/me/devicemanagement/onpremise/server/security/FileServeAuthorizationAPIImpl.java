package com.me.devicemanagement.onpremise.server.security;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertificateUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertAuthBean;
import javax.servlet.ServletRequest;
import java.util.Map;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.security.FileServeAuthorizationAPI;

public class FileServeAuthorizationAPIImpl implements FileServeAuthorizationAPI
{
    private static final Logger LOGGER;
    private static Map resourceTableMap;
    private static boolean isRedisCacheSet;
    
    public boolean isDSRequest(final ServletRequest servletRequest) {
        final Object isDSRequest = servletRequest.getAttribute("isDSRequest");
        Boolean flag = Boolean.FALSE;
        if (isDSRequest != null) {
            flag = Boolean.parseBoolean(isDSRequest.toString());
        }
        return flag;
    }
    
    public boolean isValidRequest(final String custID, final String resourceID, final String domainName) {
        try {
            if (ClientCertAuthBean.getInstance().getIsRedisEnabled()) {
                String value = ApiFactoryProvider.getRedisHashMap().get("ResourcesTableFS", resourceID.toString(), 2);
                if (value == null || value.isEmpty()) {
                    this.checkAndUpdateCacheFromDB(resourceID);
                    value = ApiFactoryProvider.getRedisHashMap().get("ResourcesTableFS", resourceID.toString(), 2);
                }
                if (value != null && !value.isEmpty() && value.equalsIgnoreCase(custID + "," + domainName)) {
                    return true;
                }
            }
            else {
                List list = FileServeAuthorizationAPIImpl.resourceTableMap.get(resourceID);
                if (list == null || list.isEmpty()) {
                    this.checkAndUpdateCacheFromDB(resourceID);
                    list = FileServeAuthorizationAPIImpl.resourceTableMap.get(resourceID);
                }
                if (list != null && !list.isEmpty() && list.get(0).toString().equals(custID) && list.get(1).toString().equalsIgnoreCase(domainName)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            FileServeAuthorizationAPIImpl.LOGGER.log(Level.INFO, "Error checking for valid request ", e);
        }
        return false;
    }
    
    private void checkAndUpdateCacheFromDB(final String resourceID) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
        DataObject DO = null;
        DO = this.getResourceDO(criteria);
        if (DO != null && !DO.isEmpty()) {
            final Row row = DO.getFirstRow("Resource");
            final ArrayList<String> list = new ArrayList<String>();
            final String customerID = row.get("CUSTOMER_ID").toString();
            final String domainName = row.get("DOMAIN_NETBIOS_NAME").toString();
            this.updateCache(customerID, resourceID, domainName);
        }
    }
    
    public boolean isFileAuthorizationOn() {
        return ClientCertificateUtil.getInstance().isClientCertAuthEnabledFromWebSettings();
    }
    
    public void initResourceCache() {
        try {
            if (this.isFileAuthorizationOn()) {
                if (this.isRedisEnabled()) {
                    if (!FileServeAuthorizationAPIImpl.isRedisCacheSet) {
                        this.setRedisCache();
                        FileServeAuthorizationAPIImpl.isRedisCacheSet = Boolean.TRUE;
                    }
                }
                else if (FileServeAuthorizationAPIImpl.resourceTableMap == null || FileServeAuthorizationAPIImpl.resourceTableMap.isEmpty()) {
                    this.setResourceHashMap();
                }
            }
        }
        catch (final Exception e) {
            FileServeAuthorizationAPIImpl.LOGGER.log(Level.INFO, "Exception initializing cache", e);
        }
    }
    
    private boolean isRedisEnabled() {
        boolean isRedisEnabled = false;
        try {
            final Properties productSettingsProperties = SyMUtil.getProductSettingsProperties();
            if (productSettingsProperties != null && productSettingsProperties.containsKey("enableRedis")) {
                isRedisEnabled = Boolean.parseBoolean(productSettingsProperties.getProperty("enableRedis").trim());
            }
        }
        catch (final Exception e) {
            FileServeAuthorizationAPIImpl.LOGGER.log(Level.WARNING, "Exception while checking if redis is enabled", e);
        }
        return isRedisEnabled;
    }
    
    public void updateCache(final String custID, final String resourceID, final String domainName) {
        try {
            if (ClientCertAuthBean.getInstance().getIsRedisEnabled()) {
                this.updateRedisCache(custID, resourceID, domainName);
            }
            else {
                this.updateCacheMap(custID, resourceID, domainName);
            }
        }
        catch (final Exception e) {
            FileServeAuthorizationAPIImpl.LOGGER.log(Level.INFO, "Exception updating cache", e);
        }
    }
    
    public void updateCacheAndRemoveOldResID(final String newResourceID, final String oldResourceID, final String custID, final String domainName) {
        try {
            if (ClientCertAuthBean.getInstance().getIsRedisEnabled()) {
                this.updateRedisCache(custID, newResourceID, domainName);
            }
            else {
                FileServeAuthorizationAPIImpl.resourceTableMap.remove(oldResourceID);
                this.updateCacheMap(custID, newResourceID, domainName);
            }
        }
        catch (final Exception e) {
            FileServeAuthorizationAPIImpl.LOGGER.log(Level.INFO, "Exception updating cache", e);
        }
    }
    
    private void updateCacheMap(final String custID, final String resourceID, final String domainName) {
        final ArrayList<String> list = new ArrayList<String>(2);
        list.add(custID);
        list.add(domainName);
        FileServeAuthorizationAPIImpl.resourceTableMap.put(resourceID, list);
    }
    
    private void updateRedisCache(final String custID, final String resourceID, final String domainName) {
        try {
            ApiFactoryProvider.getRedisHashMap().put("ResourcesTableFS", resourceID, custID + "," + domainName, 2);
        }
        catch (final Exception e) {
            FileServeAuthorizationAPIImpl.LOGGER.log(Level.INFO, "Exception updating redis cache ", e);
        }
    }
    
    private void setResourceHashMap() {
        if (FileServeAuthorizationAPIImpl.resourceTableMap == null) {
            FileServeAuthorizationAPIImpl.resourceTableMap = new HashMap();
        }
        try {
            final DataObject DO = this.getResourceDO();
            if (DO != null && !DO.isEmpty()) {
                final Iterator itr = DO.getRows("Resource");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    final String custID = row.get("CUSTOMER_ID").toString();
                    final String resourceID = row.get("RESOURCE_ID").toString();
                    final String domainName = row.get("DOMAIN_NETBIOS_NAME").toString();
                    final ArrayList<String> list = new ArrayList<String>(2);
                    list.add(custID);
                    list.add(domainName);
                    FileServeAuthorizationAPIImpl.resourceTableMap.put(resourceID, list);
                }
            }
        }
        catch (final Exception e) {
            FileServeAuthorizationAPIImpl.LOGGER.log(Level.INFO, "Error storing resource to hashmap", e);
        }
    }
    
    private void setRedisCache() {
        try {
            final DataObject DO = this.getResourceDO();
            if (DO != null && !DO.isEmpty()) {
                final Iterator itr = DO.getRows("Resource");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    final String custID = row.get("CUSTOMER_ID").toString();
                    final String resourceID = row.get("RESOURCE_ID").toString();
                    final String domainName = row.get("DOMAIN_NETBIOS_NAME").toString();
                    ApiFactoryProvider.getRedisHashMap().put("ResourcesTableFS", resourceID, custID + "," + domainName, 2);
                }
            }
        }
        catch (final Exception e) {
            FileServeAuthorizationAPIImpl.LOGGER.log(Level.INFO, "Error retrieving data", e);
        }
    }
    
    private DataObject getResourceDO() throws DataAccessException {
        return this.getResourceDO(null);
    }
    
    private DataObject getResourceDO(final Criteria criteria) throws DataAccessException {
        final String managedComputerTable = "ManagedComputer";
        final String managedComputerResourceId = "RESOURCE_ID";
        final List columns = new ArrayList();
        final Column column1 = new Column("Resource", "RESOURCE_ID");
        final Column column2 = new Column("Resource", "CUSTOMER_ID");
        final Column column3 = new Column("Resource", "DOMAIN_NETBIOS_NAME");
        columns.add(column1);
        columns.add(column2);
        columns.add(column3);
        final Join join = new Join("Resource", managedComputerTable, new String[] { "RESOURCE_ID" }, new String[] { managedComputerResourceId }, 2);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addSelectColumns(columns);
        selectQuery.addJoin(join);
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        return com.me.devicemanagement.onpremise.server.util.SyMUtil.getPersistence().get(selectQuery);
    }
    
    static {
        LOGGER = Logger.getLogger(FileServeAuthorizationAPIImpl.class.getName());
    }
}
