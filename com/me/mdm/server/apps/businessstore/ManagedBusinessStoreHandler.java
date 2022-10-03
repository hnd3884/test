package com.me.mdm.server.apps.businessstore;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.resource.MDMResourceDataPopulator;
import java.util.Properties;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class ManagedBusinessStoreHandler
{
    public static final Integer BS_SERVICE_VPP;
    public static final Integer BS_SERVICE_AFW;
    public static final Integer BS_SERVICE_WBS;
    private static Logger logger;
    
    public static Long addOrUpdateManagedStore(final JSONObject jsonData) throws DataAccessException, JSONException, SyMException {
        try {
            final Long customerId = jsonData.getLong("CUSTOMER_ID");
            final Integer serviceType = jsonData.getInt("BS_SERVICE_TYPE");
            final String bsIdentifier = String.valueOf(jsonData.get("BUSINESSSTORE_IDENTIFICATION"));
            final Long userId = jsonData.getLong("BUSINESSSTORE_ADDED_BY");
            String bsIdentifierForResource = null;
            if (bsIdentifier.trim().length() > 47) {
                bsIdentifierForResource = bsIdentifier.substring(0, 45).trim();
            }
            else {
                bsIdentifierForResource = bsIdentifier;
            }
            final Properties resourceProp = new Properties();
            ((Hashtable<String, Long>)resourceProp).put("CUSTOMER_ID", customerId);
            ((Hashtable<String, String>)resourceProp).put("NAME", bsIdentifier);
            ((Hashtable<String, String>)resourceProp).put("DOMAIN_NETBIOS_NAME", bsIdentifierForResource + getServiceTypeSuffix(serviceType));
            ((Hashtable<String, String>)resourceProp).put("RESOURCE_TYPE", String.valueOf(1201));
            final DataObject resourceDO = MDMResourceDataPopulator.addOrUpdateMDMResource(resourceProp);
            final Long resourceId = (Long)resourceDO.getFirstValue("Resource", "RESOURCE_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedBusinessStore"));
            selectQuery.addJoin(new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria resourceCriteria = new Criteria(new Column("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)resourceId, 0);
            selectQuery.addSelectColumn(new Column("ManagedBusinessStore", "*"));
            selectQuery.setCriteria(resourceCriteria);
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            Boolean isAdded = true;
            Row row;
            if (dO.isEmpty()) {
                row = new Row("ManagedBusinessStore");
                row.set("BUSINESSSTORE_ID", (Object)resourceId);
                row.set("BS_SERVICE_TYPE", (Object)serviceType);
            }
            else {
                row = dO.getFirstRow("ManagedBusinessStore");
                isAdded = false;
            }
            row.set("BUSINESSSTORE_IDENTIFICATION", (Object)bsIdentifier);
            row.set("BUSINESSSTORE_ADDED_BY", (Object)userId);
            if (isAdded) {
                dO.addRow(row);
                MDMUtil.getPersistence().add(dO);
            }
            else {
                dO.updateRow(row);
                MDMUtil.getPersistence().update(dO);
            }
            ManagedBusinessStoreHandler.logger.log(Level.INFO, "Managed Business Store {0} of type {1} for customer Id {2} Added Successfully :: BUSINESSSTORE_ID = {3}", new Object[] { bsIdentifier, serviceType, customerId, resourceId });
            return resourceId;
        }
        catch (final DataAccessException e) {
            ManagedBusinessStoreHandler.logger.log(Level.WARNING, "Unable to add Business Store Users", (Throwable)e);
            throw e;
        }
    }
    
    public static JSONObject getBusinessStoreDetails(final Long customerID, final Integer serveiceType) throws DataAccessException, JSONException {
        final Table table = new Table("ManagedBusinessStore");
        final Join resJoin = new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        selectQuery.addJoin(resJoin);
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_IDENTIFICATION"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        final Criteria serviceCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)serveiceType, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(serviceCriteria.and(customerCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONObject response = new JSONObject();
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("ManagedBusinessStore");
            response.put("BUSINESSSTORE_ID", row.get("BUSINESSSTORE_ID"));
            response.put("BUSINESSSTORE_IDENTIFICATION", row.get("BUSINESSSTORE_IDENTIFICATION"));
        }
        else {
            response.put("Error", (Object)"No rows retrived");
        }
        return response;
    }
    
    private static String getServiceTypeSuffix(final Integer serviceType) {
        String suffix = "";
        if (serviceType.equals(ManagedBusinessStoreHandler.BS_SERVICE_AFW)) {
            suffix = "_pfw";
        }
        else if (serviceType.equals(ManagedBusinessStoreHandler.BS_SERVICE_WBS)) {
            suffix = "_wbs";
        }
        else if (serviceType.equals(ManagedBusinessStoreHandler.BS_SERVICE_VPP)) {
            suffix = "_vpp";
        }
        return suffix;
    }
    
    public static int getServiceTypeBasedOnPlatform(final int platformType) {
        int serviceType = -1;
        if (platformType == 2) {
            serviceType = ManagedBusinessStoreHandler.BS_SERVICE_AFW;
        }
        else if (platformType == 3) {
            serviceType = ManagedBusinessStoreHandler.BS_SERVICE_WBS;
        }
        else if (platformType == 1) {
            serviceType = ManagedBusinessStoreHandler.BS_SERVICE_VPP;
        }
        return serviceType;
    }
    
    static {
        BS_SERVICE_VPP = 101;
        BS_SERVICE_AFW = 201;
        BS_SERVICE_WBS = 301;
        ManagedBusinessStoreHandler.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
