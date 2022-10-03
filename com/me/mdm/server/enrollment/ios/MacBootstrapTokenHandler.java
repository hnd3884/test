package com.me.mdm.server.enrollment.ios;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public class MacBootstrapTokenHandler
{
    public Logger checkinLogger;
    private static MacBootstrapTokenHandler macBootstrapTokenHandler;
    
    private MacBootstrapTokenHandler() {
        this.checkinLogger = Logger.getLogger("MDMCheckinLogger");
    }
    
    public static MacBootstrapTokenHandler getInstance() {
        if (MacBootstrapTokenHandler.macBootstrapTokenHandler == null) {
            MacBootstrapTokenHandler.macBootstrapTokenHandler = new MacBootstrapTokenHandler();
        }
        return MacBootstrapTokenHandler.macBootstrapTokenHandler;
    }
    
    public void addOrUpdateMacBootstrapToken(final Long resourceID, final Long customerID, final String udid, final Map bootstrapData) throws Exception {
        this.checkinLogger.log(Level.INFO, "addOrUpdateMacBootstrapToken() :- for resourceID={0}, customerID={1}, udid={2}", new Object[] { resourceID, customerID, udid });
        try {
            final DataObject dataObject = this.getMacBootstrapTokenDataObject(resourceID);
            if (dataObject.isEmpty()) {
                this.checkinLogger.log(Level.INFO, "addOrUpdateMacBootstrapToken() :- adding new row");
                final Row row = new Row("MacBootstrapToken");
                row.set("RESOURCE_ID", (Object)resourceID);
                final DataObject bootstrapTempDo = this.getMacBootstrapTokenTempDataObject(udid, customerID);
                if (!bootstrapTempDo.isEmpty()) {
                    final Row bootstrapTokenTempRow = bootstrapTempDo.getRow("MacBootstrapTokenTemp");
                    final String bootstrapToken = (String)bootstrapTokenTempRow.get("TOKEN");
                    this.checkinLogger.log(Level.INFO, "addOrUpdateMacBootstrapToken() :- overRiding the value & token is null={1}, token is empty={1}", new Object[] { bootstrapToken == null, bootstrapToken != null && bootstrapToken.isEmpty() });
                    bootstrapData.put("TOKEN", bootstrapToken);
                    bootstrapData.put("TOKEN_ADDED_AT", bootstrapTokenTempRow.get("TOKEN_ADDED_AT"));
                }
                bootstrapData.forEach((key, value) -> row2.set((String)key, value));
                dataObject.addRow(row);
                MDMUtil.getPersistence().add(dataObject);
                bootstrapTempDo.deleteRows("MacBootstrapTokenTemp", (Criteria)null);
                MDMUtil.getPersistence().update(bootstrapTempDo);
                this.checkinLogger.log(Level.INFO, "addOrUpdateMacBootstrapToken() : tempTable row has been deleted");
            }
            else {
                this.checkinLogger.log(Level.INFO, "addOrUpdateMacBootstrapToken() :- updating the row");
                final Row row = dataObject.getFirstRow("MacBootstrapToken");
                bootstrapData.forEach((key, value) -> row3.set((String)key, value));
                dataObject.updateRow(row);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception e) {
            this.checkinLogger.log(Level.SEVERE, "Exception in addOrUpdateMacBootstrapToken() :- ", e);
            throw e;
        }
    }
    
    public String getMacBootstrapToken(final String udid, final Long customerID) throws Exception {
        this.checkinLogger.log(Level.INFO, "getMacBootstrapToken() :- for udid={0}, customerID={1}", new Object[] { udid, customerID });
        try {
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid, customerID);
            if (resourceID != null) {
                final DataObject dataObject = this.getMacBootstrapTokenDataObject(resourceID);
                if (!dataObject.isEmpty()) {
                    final Row row = dataObject.getFirstRow("MacBootstrapToken");
                    return (String)row.get("TOKEN");
                }
            }
            return this.getMacBootstrapTokenFromTempTable(udid, customerID);
        }
        catch (final Exception e) {
            this.checkinLogger.log(Level.SEVERE, "Exception in addOrUpdateMacBootstrapToken() :- ", e);
            throw e;
        }
    }
    
    public DataObject getMacBootstrapTokenDataObject(final Long resourceID) throws Exception {
        this.checkinLogger.log(Level.INFO, " Inside getMacBootstrapTokenDataObject() :- for resourceID={0}", resourceID);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MacBootstrapToken"));
        final Criteria criteria = new Criteria(Column.getColumn("MacBootstrapToken", "RESOURCE_ID"), (Object)resourceID, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("MacBootstrapToken", "*"));
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    private DataObject getMacBootstrapTokenTempDataObject(final String udid, final Long customerID) throws Exception {
        this.checkinLogger.log(Level.INFO, " Inside getMacBootstrapTokenTempDataObject() :- for udid={0}, customerID={1}", new Object[] { udid, customerID });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MacBootstrapTokenTemp"));
        final Criteria customerCrit = new Criteria(Column.getColumn("MacBootstrapTokenTemp", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria udidCrit = new Criteria(Column.getColumn("MacBootstrapTokenTemp", "UDID"), (Object)udid, 0);
        selectQuery.setCriteria(customerCrit.and(udidCrit));
        selectQuery.addSelectColumn(Column.getColumn("MacBootstrapTokenTemp", "*"));
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public void addOrUpdateMacBootstrapTokenTemp(final String udid, final String bootstrapToken, final Long customerID, final Long reqTime) throws Exception {
        this.checkinLogger.log(Level.INFO, "addOrUpdateMacBootstrapTokenTemp() :- for udid={0}, customerID={1}, reqTime={2}", new Object[] { udid, customerID, reqTime });
        try {
            final DataObject dataObject = this.getMacBootstrapTokenTempDataObject(udid, customerID);
            if (dataObject.isEmpty()) {
                this.checkinLogger.log(Level.INFO, "addOrUpdateMacBootstrapTokenTemp() :- Insertion in bootstraptokenTemp table");
                final Row bootstrapTempRow = new Row("MacBootstrapTokenTemp");
                bootstrapTempRow.set("UDID", (Object)udid);
                bootstrapTempRow.set("TOKEN", (Object)bootstrapToken);
                bootstrapTempRow.set("CUSTOMER_ID", (Object)customerID);
                bootstrapTempRow.set("TOKEN_ADDED_AT", (Object)reqTime);
                dataObject.addRow(bootstrapTempRow);
                MDMUtil.getPersistence().add(dataObject);
            }
            else {
                this.checkinLogger.log(Level.WARNING, "addOrUpdateMacBootstrapTokenTemp() :- Modification in bootstraptokenTemp table");
                final Row bootstrapTempRow = dataObject.getRow("MacBootstrapTokenTemp");
                bootstrapTempRow.set("TOKEN", (Object)bootstrapToken);
                bootstrapTempRow.set("TOKEN_ADDED_AT", (Object)reqTime);
                dataObject.updateRow(bootstrapTempRow);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception ex) {
            this.checkinLogger.log(Level.SEVERE, "Exception in addOrUpdateMacBootstrapTokenTemp() :- ", ex);
            throw ex;
        }
    }
    
    private String getMacBootstrapTokenFromTempTable(final String udid, final Long customerID) throws Exception {
        this.checkinLogger.log(Level.WARNING, "getMacBootstrapTokenFromTempTable() :- for udid={0}, customerID={1}", new Object[] { udid, customerID });
        try {
            String bootstrapToken = null;
            final DataObject bootstrapTempDo = this.getMacBootstrapTokenTempDataObject(udid, customerID);
            if (!bootstrapTempDo.isEmpty()) {
                final Row bootstrapTokenTempRow = bootstrapTempDo.getRow("MacBootstrapTokenTemp");
                bootstrapToken = (String)bootstrapTokenTempRow.get("TOKEN");
            }
            return bootstrapToken;
        }
        catch (final Exception ex) {
            this.checkinLogger.log(Level.SEVERE, "Exception in getMacBootstrapTokenFromTempTable() :- ", ex);
            throw ex;
        }
    }
    
    static {
        MacBootstrapTokenHandler.macBootstrapTokenHandler = null;
    }
}
