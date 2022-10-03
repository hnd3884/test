package com.me.devicemanagement.onpremise.server.authentication;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class IntegrationServiceUtil
{
    private Logger logger;
    public static final int SUCCESS = 100;
    public static final int UNKNOWN_SERVICE = 102;
    public static final int INVALID_INPUT = 103;
    public static final int RESOURCE_EXISTS = 105;
    public static final int INVALID_STATUS = 106;
    public static final String LOGGED_IN_USER = "logged_in_user";
    public static final String STATUS = "status";
    public static final String DESCRIPTION = "description";
    public static final String STATUS_ID = "status_id";
    public static final String SUCCESS_STATUS = "success";
    public static final String FAILURE_STATUS = "failure";
    public static final int SERVICE_BEING_USED = 104;
    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_DISABLED = 2;
    
    public IntegrationServiceUtil() {
        this.logger = Logger.getLogger(IntegrationServiceUtil.class.getSimpleName());
    }
    
    public static IntegrationServiceUtil getNewInstance() {
        return new IntegrationServiceUtil();
    }
    
    public JSONObject addOrUpdateIntegrationService(final JSONObject properties) throws Exception {
        try {
            if (!properties.has("SERVICE_ID")) {
                return this.createIntegrationService(properties);
            }
            return this.modifyIntegrationService(properties);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error occurred in addOrUpdateIntegrationService() function", e);
            return null;
        }
    }
    
    public JSONObject createIntegrationService(final JSONObject properties) {
        final JSONObject result = new JSONObject();
        try {
            final Long userId = Long.valueOf(String.valueOf(properties.get("logged_in_user")));
            final String serviceName = String.valueOf(properties.get("NAME"));
            final DataObject existingDO = DataAccess.get("IntegrationService", new Criteria(Column.getColumn("IntegrationService", "NAME"), (Object)serviceName, 0, false));
            if (existingDO.isEmpty()) {
                final Row row = new Row("IntegrationService");
                row.set("NAME", (Object)serviceName);
                row.set("CREATED_BY", (Object)userId);
                row.set("CREATION_TIME", (Object)System.currentTimeMillis());
                row.set("MODIFIED_BY", (Object)userId);
                row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                final DataObject apiKeyDO = DataAccess.constructDataObject();
                apiKeyDO.addRow(row);
                final DataObject resultDO = DataAccess.update(apiKeyDO);
                result.put("status", (Object)"success");
                result.put("status_id", 100);
                result.put("NAME", resultDO.getFirstRow("IntegrationService").get("NAME"));
                result.put("SERVICE_ID", resultDO.getFirstRow("IntegrationService").get("SERVICE_ID"));
            }
            else {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Integration Service name already exists");
                result.put("status_id", 103);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error occurred in createIntegrationService()", e);
        }
        return result;
    }
    
    public JSONObject modifyIntegrationService(final JSONObject properties) {
        final JSONObject result = new JSONObject();
        try {
            final Long userId = Long.valueOf(String.valueOf(properties.get("logged_in_user")));
            final String serviceName = String.valueOf(properties.get("NAME"));
            final Long integrationServiceId = Long.valueOf(String.valueOf(properties.get("SERVICE_ID")));
            final int status = properties.optInt("STATUS", 1);
            if (status != 1 && status != 2) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Invalid status cannot be updated");
                result.put("status_id", 106);
                return result;
            }
            final DataObject existingDO = DataAccess.get("IntegrationService", new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)integrationServiceId, 0));
            if (existingDO.isEmpty()) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Integration Service does not exist");
                result.put("status_id", 103);
                return result;
            }
            final DataObject tempDO = DataAccess.get("IntegrationService", new Criteria(Column.getColumn("IntegrationService", "NAME"), (Object)serviceName, 0).and(new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)integrationServiceId, 1)));
            if (!tempDO.isEmpty()) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Integration Service name already exists");
                result.put("status_id", 105);
                return result;
            }
            final Row row = existingDO.getFirstRow("IntegrationService");
            row.set("NAME", (Object)serviceName);
            row.set("STATUS", (Object)status);
            row.set("MODIFIED_BY", (Object)userId);
            row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
            existingDO.updateRow(row);
            final DataObject resultDO = DataAccess.update(existingDO);
            result.put("status", (Object)"success");
            result.put("status_id", 100);
            result.put("NAME", resultDO.getFirstRow("IntegrationService").get("NAME"));
            result.put("SERVICE_ID", resultDO.getFirstRow("IntegrationService").get("SERVICE_ID"));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error occurred in modifyIntegrationService()", e);
        }
        return result;
    }
    
    public int deleteIntegrationService(final JSONObject properties) throws Exception {
        final Long serviceId = Long.valueOf(String.valueOf(properties.get("SERVICE_ID")));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IntegrationService"));
        selectQuery.addJoin(new Join("IntegrationService", "APIKeyInfo", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "SERVICE_ID").count());
        selectQuery.setCriteria(new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)serviceId, 0));
        final int count = DBUtil.getRecordCount(selectQuery);
        final DataObject existingDO = DataAccess.get("IntegrationService", new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)serviceId, 0));
        if (existingDO.isEmpty()) {
            return 102;
        }
        if (count == 0) {
            SyMUtil.getPersistence().delete(existingDO.getFirstRow("IntegrationService"));
            return 100;
        }
        return 104;
    }
    
    public boolean isValidIntegrationServiceId(final Long integrationServiceId) throws DataAccessException {
        final DataObject existingDO = DataAccess.get("IntegrationService", new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)integrationServiceId, 0).and(new Criteria(Column.getColumn("IntegrationService", "STATUS"), (Object)1, 0)));
        return !existingDO.isEmpty();
    }
    
    public String getIntegrationServiceName(final Long integrationServiceId) {
        String serviceName = null;
        try {
            serviceName = (String)DBUtil.getValueFromDB("IntegrationService", "SERVICE_ID", (Object)integrationServiceId, "NAME");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting Service Name", e);
        }
        return serviceName;
    }
}
