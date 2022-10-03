package com.me.ems.onpremise.uac.summaryserver.revamp.summary;

import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SummaryDefaultUsersDataUtil
{
    private static final Logger LOGGER;
    private static SummaryDefaultUsersDataUtil summaryDefaultUsersDataUtil;
    
    public static SummaryDefaultUsersDataUtil getInstance() {
        if (SummaryDefaultUsersDataUtil.summaryDefaultUsersDataUtil == null) {
            SummaryDefaultUsersDataUtil.summaryDefaultUsersDataUtil = new SummaryDefaultUsersDataUtil();
        }
        return SummaryDefaultUsersDataUtil.summaryDefaultUsersDataUtil;
    }
    
    public JSONObject getSSDefaultUsersAndRoleUVHValues() {
        final JSONObject defaultUserAndRoleIds = new JSONObject();
        if (SyMUtil.isSummaryServer()) {
            try {
                SummaryDefaultUsersDataUtil.LOGGER.log(Level.INFO, "Fetching UVH values of Default Users");
                final JSONObject userData = this.getUVHValues("users");
                this.addDefaultUserStatus(userData);
                final JSONObject roleData = this.getUVHValues("roles");
                defaultUserAndRoleIds.put("users", (Object)userData);
                defaultUserAndRoleIds.put("roles", (Object)roleData);
            }
            catch (final Exception e) {
                SummaryDefaultUsersDataUtil.LOGGER.log(Level.SEVERE, "Exception Occurred while fetching Default SS User UVH data", e);
            }
        }
        return defaultUserAndRoleIds;
    }
    
    public JSONObject getUVHValues(final String property) {
        final JSONObject uvhData = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UVHValues"));
            selectQuery.addSelectColumn(new Column("UVHValues", "*"));
            Criteria criteria = null;
            if (property.equals("users")) {
                final String[] tableNames = { "AaaUser", "AaaLogin" };
                criteria = new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), (Object)tableNames, 8);
            }
            else if (property.equals("roles")) {
                criteria = new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), (Object)"UMRole", 0);
            }
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("UVHValues");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String pattern = (row.get("PATTERN") + "").replace(":", "-");
                    final Object genValues = row.get("GENVALUES");
                    uvhData.put(pattern, (Object)(genValues + ""));
                }
            }
        }
        catch (final DataAccessException e) {
            SummaryDefaultUsersDataUtil.LOGGER.log(Level.SEVERE, "Exception Occurred while retrieving UVH pattern Value in Summary Server ", (Throwable)e);
        }
        return uvhData;
    }
    
    public void addDefaultUserStatus(final JSONObject userData) throws DataAccessException {
        final SelectQuery statusQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUserStatus"));
        statusQuery.addSelectColumn(new Column("AaaUserStatus", "*"));
        statusQuery.addJoin(new Join("AaaUserStatus", "UVHValues", new String[] { "USER_ID" }, new String[] { "GENVALUES" }, 2));
        statusQuery.setCriteria(new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), (Object)"AaaUser", 0));
        final DataObject defaultUserStatus = DataAccess.get(statusQuery);
        if (!defaultUserStatus.isEmpty()) {
            final Iterator<Row> iterator = defaultUserStatus.getRows("AaaUserStatus");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                userData.put(String.valueOf(row.get("USER_ID")), row.get("STATUS"));
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger("probeActionsLogger");
        SummaryDefaultUsersDataUtil.summaryDefaultUsersDataUtil = null;
    }
}
