package com.me.mdm.server.adep.mac;

import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.me.mdm.server.deviceaccounts.AccountDetailsHandler;
import com.adventnet.ds.query.Join;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class AccountConfiguration
{
    public static Logger logger;
    private static AccountConfiguration accountHandler;
    
    public static AccountConfiguration getInstance() {
        if (AccountConfiguration.accountHandler == null) {
            AccountConfiguration.accountHandler = new AccountConfiguration();
        }
        return AccountConfiguration.accountHandler;
    }
    
    private DataObject getDEPAccountConfigDO(final Long depTemplateID) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AccountConfigToDEPEnroll"));
            query.setCriteria(new Criteria(Column.getColumn("AccountConfigToDEPEnroll", "TEMPLATE_ID"), (Object)depTemplateID, 0));
            query.addSelectColumn(Column.getColumn("AccountConfigToDEPEnroll", "TEMPLATE_ID"));
            query.addSelectColumn(Column.getColumn("AccountConfigToDEPEnroll", "ACCOUNT_CONFIG_ID"));
            return MDMUtil.getPersistence().get(query);
        }
        catch (final Exception e) {
            AccountConfiguration.logger.log(Level.SEVERE, "Exception in fetching AccountConfiguration details for DEP Token", e);
            return null;
        }
    }
    
    private void updateRow(final DataObject dataObject, final Row row) throws Exception {
        dataObject.updateRow(row);
        MDMUtil.getPersistence().update(dataObject);
    }
    
    private void addRow(final DataObject dataObject, final Row row) throws Exception {
        dataObject.addRow(row);
        MDMUtil.getPersistence().add(dataObject);
    }
    
    public void addOrModifyAccounntConfigurationToDEPEnrollTemplate(final Long accountConfigID, final Long depEnrollmentTemplate) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getDEPAccountConfigDO(depEnrollmentTemplate);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AccountConfigToDEPEnroll");
                row.set("ACCOUNT_CONFIG_ID", (Object)accountConfigID);
                this.updateRow(dataObject, row);
            }
            else {
                final Row row = new Row("AccountConfigToDEPEnroll");
                row.set("TEMPLATE_ID", (Object)depEnrollmentTemplate);
                row.set("ACCOUNT_CONFIG_ID", (Object)accountConfigID);
                this.addRow(dataObject, row);
            }
        }
        catch (final Exception e) {
            AccountConfiguration.logger.log(Level.SEVERE, "Exception in adding or modifying Account Configuration DEP Mapping", e);
            throw new APIHTTPException("COM0008", new Object[0]);
        }
    }
    
    public Boolean isAccountConfigEnabledForDEP(final Long depTokenID) {
        try {
            final SelectQuery query = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
            final Criteria criteria = new Criteria(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)depTokenID, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("DEPEnrollmentTemplate", "TEMPLATE_ID"));
            query.addSelectColumn(Column.getColumn("DEPEnrollmentTemplate", "ENABLE_AWAIT_CONFIG"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("DEPEnrollmentTemplate");
                return (Boolean)row.get("ENABLE_AWAIT_CONFIG");
            }
        }
        catch (final Exception e) {
            AccountConfiguration.logger.log(Level.SEVERE, "Exception in finding whether account configuration enabled", e);
        }
        AccountConfiguration.logger.log(Level.WARNING, "Missing Enrollment template configuration for DEP Token. Returning account configuration as disabled");
        return Boolean.FALSE;
    }
    
    public void deleteAccountConfigurationToDEPEnrollTemplate(final Long depEnrollmentTemplate) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getDEPAccountConfigDO(depEnrollmentTemplate);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AccountConfigToDEPEnroll");
                dataObject.deleteRow(row);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception e) {
            AccountConfiguration.logger.log(Level.SEVERE, "Exception in adding or modifying Account Configuration DEP Mapping", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Long checkAndRemoveAcountConfiguration(final Long accountConfigID, final Long customerID) {
        try {
            final SelectQuery query = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
            query.addJoin(new Join("DEPEnrollmentTemplate", "AccountConfigToDEPEnroll", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
            query.addJoin(new Join("AccountConfigToDEPEnroll", "MdMacAccountConfigSettings", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 2));
            query.addJoin(new Join("MdMacAccountConfigSettings", "MdMacAccountToConfig", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 2));
            query.addJoin(new Join("MdMacAccountConfigSettings", "MacAccountConfigToResource", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 1));
            Criteria criteria = new Criteria(Column.getColumn("AccountConfigToDEPEnroll", "ACCOUNT_CONFIG_ID"), (Object)accountConfigID, 0);
            criteria = criteria.or(new Criteria(Column.getColumn("MacAccountConfigToResource", "ACCOUNT_CONFIG_ID"), (Object)accountConfigID, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("AccountConfigToDEPEnroll", "TEMPLATE_ID"));
            query.addSelectColumn(Column.getColumn("AccountConfigToDEPEnroll", "ACCOUNT_CONFIG_ID"));
            query.addSelectColumn(Column.getColumn("MdMacAccountToConfig", "ACCOUNT_CONFIG_ID"));
            query.addSelectColumn(Column.getColumn("MdMacAccountToConfig", "ACCOUNT_ID"));
            query.addSelectColumn(Column.getColumn("MacAccountConfigToResource", "RESOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final int templateSize = dataObject.size("AccountConfigToDEPEnroll");
            final int resourceSize = dataObject.size("MacAccountConfigToResource");
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            final JSONObject accountConfigJSON = handler.getMacAccountConfiguration(customerID, accountConfigID);
            final JSONArray acountArray = accountConfigJSON.getJSONArray("ACCOUNTS");
            final List<Long> accountIDs = new ArrayList<Long>();
            for (int i = 0; i < acountArray.length(); ++i) {
                accountIDs.add(acountArray.getJSONObject(i).getLong("ACCOUNT_ID"));
            }
            if (templateSize > 1 && resourceSize > 0) {
                return (Long)this.duplicateAccountID(accountIDs.get(0)).get("ACCOUNT_ID");
            }
            handler.deleteMacAccountConfig(customerID, accountConfigID);
            return (Long)DBUtil.getRowFromDB("MdComputerAccount", "ACCOUNT_ID", (Object)accountIDs.get(0)).get("ACCOUNT_ID");
        }
        catch (final Exception e) {
            AccountConfiguration.logger.log(Level.SEVERE, "Exception in removing account configuration");
            return null;
        }
    }
    
    private Row duplicateAccountID(final Long accountID) throws Exception {
        final Row row = DBUtil.getRowFromDB("MdComputerAccount", "ACCOUNT_ID", (Object)accountID);
        final Row newRow = MDMDBUtil.cloneRow(row, new String[] { "ACCOUNT_ID" });
        final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
        dataObject.addRow(newRow);
        MDMUtil.getPersistence().add(dataObject);
        return newRow;
    }
    
    public Long getAccountConfigIDForDEPEnrollTemplate(final Long depEnrollmentTemplate) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getDEPAccountConfigDO(depEnrollmentTemplate);
            if (dataObject.isEmpty()) {
                return null;
            }
            final Row row = dataObject.getFirstRow("AccountConfigToDEPEnroll");
            return (Long)row.get("ACCOUNT_CONFIG_ID");
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            AccountConfiguration.logger.log(Level.WARNING, "Failed to fetch account Config for DEP Token", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public DataObject getAccountConfigIDToResourceIDDO(final Long resourceID) {
        try {
            final List<Integer> macModelArray = new ArrayList<Integer>();
            macModelArray.add(3);
            macModelArray.add(4);
            final SelectQuery query = DEPAdminEnrollmentHandler.getManagedResourceToDEPTokenQuery();
            query.addJoin(new Join("DEPEnrollmentTemplate", "AccountConfigToDEPEnroll", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
            query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            Criteria criteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)macModelArray.toArray(), 8);
            criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("AccountConfigToDEPEnroll", "TEMPLATE_ID"));
            query.addSelectColumn(Column.getColumn("AccountConfigToDEPEnroll", "ACCOUNT_CONFIG_ID"));
            query.addSelectColumn(Column.getColumn("DEPEnrollmentTemplate", "ENABLE_AWAIT_CONFIG"));
            query.addSelectColumn(Column.getColumn("DEPEnrollmentTemplate", "TEMPLATE_ID"));
            return MDMUtil.getPersistence().get(query);
        }
        catch (final Exception e) {
            AccountConfiguration.logger.log(Level.WARNING, "Failed to fetch actual account Config ID for the given resource", e);
            return null;
        }
    }
    
    public Long getAccountConfigIDForResourceID(final Long resourceID) {
        try {
            final DataObject dataObject = this.getAccountConfigIDToResourceIDDO(resourceID);
            if (dataObject.isEmpty()) {
                return null;
            }
            final Row row = dataObject.getFirstRow("AccountConfigToDEPEnroll");
            return (Long)row.get("ACCOUNT_CONFIG_ID");
        }
        catch (final Exception e) {
            AccountConfiguration.logger.log(Level.WARNING, "Failed to fetch actual account Config ID for the given resource", e);
            return null;
        }
    }
    
    public Long getEnrollmentTemplateIDFromDEPTokenID(final Long depTokenID) {
        try {
            final SelectQuery query = DEPAdminEnrollmentHandler.getManagedResourceToDEPTokenQuery();
            final Criteria criteria = new Criteria(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)depTokenID, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("DEPEnrollmentTemplate", "TEMPLATE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("DEPEnrollmentTemplate");
                return (Long)row.get("TEMPLATE_ID");
            }
        }
        catch (final Exception e) {
            AccountConfiguration.logger.log(Level.SEVERE, "Failed to get Enrollment template ID for DEP token ID", e);
        }
        return null;
    }
    
    static {
        AccountConfiguration.logger = Logger.getLogger("MDMEnrollment");
        AccountConfiguration.accountHandler = null;
    }
}
