package com.me.mdm.server.inv;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class AppleServiceProgramsHandler
{
    public static final String IPHONE_CSV_FILENAME = "iPhone7_Program_Eligible_Devices";
    public static final String KEY_PROGRAM_ID = "program_id";
    public static final int IPHONE_7_PROGRAM_ID = 1;
    private static final Logger LOGGER;
    
    public StringBuilder getIPhone7ProgramCSVStringBuilder(final Long customerID) {
        final StringBuilder sBuilder = new StringBuilder();
        RelationalAPI relAPI = null;
        Connection connection = null;
        DataSet dataSet = null;
        try {
            final SelectQuery selectQuery = this.getiPhone7CoreQuery(customerID);
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "DISPLAY_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            selectQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "PHONE_NUMBER"));
            selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
            final String sQuery = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            AppleServiceProgramsHandler.LOGGER.log(Level.INFO, "getIPhone7ProgramCSVStringBuilder() Query = {0}", sQuery);
            relAPI = RelationalAPI.getInstance();
            connection = relAPI.getConnection();
            dataSet = relAPI.executeQuery((Query)selectQuery, connection);
            sBuilder.append("DeviceName");
            sBuilder.append(",");
            sBuilder.append("UserDisplayName");
            sBuilder.append(",");
            sBuilder.append("UserEmail");
            sBuilder.append(",");
            sBuilder.append("OSVersion");
            sBuilder.append(",");
            sBuilder.append("SerialNumber");
            sBuilder.append(",");
            sBuilder.append("PhoneNumber");
            sBuilder.append(",");
            sBuilder.append("DeviceModelNumber");
            sBuilder.append(",");
            sBuilder.append("IMEI");
            sBuilder.append("\n");
            while (dataSet.next()) {
                sBuilder.append((dataSet.getValue(1) == null) ? "--" : ((String)dataSet.getValue(1)));
                for (int columnIndex = 2; columnIndex < 9; ++columnIndex) {
                    sBuilder.append(",");
                    sBuilder.append((dataSet.getValue(columnIndex) == null) ? "--" : ((String)dataSet.getValue(columnIndex)));
                }
                sBuilder.append("\n");
            }
        }
        catch (final Exception ex) {
            AppleServiceProgramsHandler.LOGGER.log(Level.WARNING, "Exception occurred while exportLocationHistoryData", ex);
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception e) {
                AppleServiceProgramsHandler.LOGGER.log(Level.WARNING, "Exception occoured in close dataset....", e);
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e) {
                AppleServiceProgramsHandler.LOGGER.log(Level.WARNING, "Exception occoured in close connection....", e);
            }
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception e2) {
                AppleServiceProgramsHandler.LOGGER.log(Level.WARNING, "Exception occoured in close dataset....", e2);
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e2) {
                AppleServiceProgramsHandler.LOGGER.log(Level.WARNING, "Exception occoured in close connection....", e2);
            }
        }
        return sBuilder;
    }
    
    public StringBuilder getEligibleDevicesCSVStringBuilder(final JSONObject data) {
        StringBuilder sBuilder = new StringBuilder();
        try {
            final String programID = data.get("program_id").toString();
            final int pID = Integer.parseInt(programID);
            switch (pID) {
                case 1: {
                    sBuilder = this.getIPhone7ProgramCSVStringBuilder(data.getLong("CUSTOMER_ID"));
                    break;
                }
            }
        }
        catch (final Exception e) {
            AppleServiceProgramsHandler.LOGGER.log(Level.WARNING, "Exception occurred while exportLocationHistoryData", e);
        }
        return sBuilder;
    }
    
    public int getiPhone7ProgramEligibleDevicesCount(final Long customerID) {
        int count = 0;
        DMDataSetWrapper dataSet = null;
        try {
            final SelectQuery selectQuery = this.getiPhone7CoreQuery(customerID);
            final Column countColumn = new Column("Resource", "CUSTOMER_ID").count();
            countColumn.setColumnAlias("DevicesCount");
            final List groupByColumns = new ArrayList();
            groupByColumns.add(new Column("Resource", "CUSTOMER_ID"));
            final GroupByClause grouping = new GroupByClause(groupByColumns);
            selectQuery.addSelectColumn(countColumn);
            selectQuery.setGroupByClause(grouping);
            final String sQuery = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            AppleServiceProgramsHandler.LOGGER.log(Level.INFO, "getIPhone7ProgramCSVStringBuilder() Query = {0}", sQuery);
            dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                count = (int)dataSet.getValue("DevicesCount");
            }
        }
        catch (final Exception ex) {
            AppleServiceProgramsHandler.LOGGER.log(Level.WARNING, "Exception occurred while getiPhone7ProgramEligibleDevicesCount ", ex);
        }
        return count;
    }
    
    private SelectQuery getiPhone7CoreQuery(final Long customerID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdSIMInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria pNameC = new Criteria(Column.getColumn("MdModelInfo", "PRODUCT_NAME"), (Object)"iPhone9,1", 2);
        final Criteria chCrit = new Criteria(Column.getColumn("MdModelInfo", "MODEL"), (Object)"?????CH*", 2);
        final Criteria jCrit = new Criteria(Column.getColumn("MdModelInfo", "MODEL"), (Object)"?????J*", 2);
        final Criteria llCrit = new Criteria(Column.getColumn("MdModelInfo", "MODEL"), (Object)"?????LL*", 2);
        final Criteria zpCrit = new Criteria(Column.getColumn("MdModelInfo", "MODEL"), (Object)"?????ZP*", 2);
        final Criteria customerC = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria fullCrit = chCrit.or(jCrit).or(llCrit).or(zpCrit).and(pNameC).and(customerC);
        selectQuery.setCriteria(fullCrit);
        return selectQuery;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
