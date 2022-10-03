package com.me.devicemanagement.framework.addons;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.admin.SoMEvent;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import org.json.JSONArray;
import java.io.IOException;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import org.json.JSONException;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.logging.Logger;

public class AddOnHandler
{
    Logger logger;
    private static AddOnHandler api;
    private HashMap handlerClassMap;
    
    public static AddOnHandler getInstance() {
        if (AddOnHandler.api == null) {
            AddOnHandler.api = new AddOnHandler();
        }
        return AddOnHandler.api;
    }
    
    public AddOnHandler() {
        this.logger = Logger.getLogger("SecurityAddonLogger");
        this.handlerClassMap = null;
        this.handlerClassMap = new HashMap();
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AddOns"));
        sq.addSelectColumn(new Column("AddOns", "ADD_ON_NAME"));
        sq.addSelectColumn(new Column("AddOns", "ADD_ON_ID"));
        sq.addSelectColumn(new Column("AddOns", "ADDON_HANDLER_CLASS"));
        try {
            final DataObject dataObject = DataAccess.get(sq);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator itr = dataObject.getRows("AddOns");
                while (itr.hasNext()) {
                    final Row addOnsRow = itr.next();
                    final String className = (String)addOnsRow.get("ADDON_HANDLER_CLASS");
                    final String addOnName = (String)addOnsRow.get("ADD_ON_NAME");
                    try {
                        this.handlerClassMap.put(addOnName, Class.forName(className).newInstance());
                    }
                    catch (final ClassNotFoundException e) {
                        this.logger.log(Level.SEVERE, "the class {0} addon-handler for this addon {1} is not defined.", new Object[] { className, addOnName, e });
                    }
                    catch (final IllegalAccessException e2) {
                        this.logger.log(Level.SEVERE, "the class {0} addon-handler for this addon {1} is not defined.", new Object[] { className, addOnName, e2 });
                    }
                    catch (final InstantiationException e3) {
                        this.logger.log(Level.SEVERE, "the class {0} addon-handler for this addon {1} is not defined.", new Object[] { className, addOnName, e3 });
                    }
                }
            }
        }
        catch (final DataAccessException e4) {
            this.logger.log(Level.SEVERE, "exception occured while getting the class details of the addons.", (Throwable)e4);
        }
    }
    
    public boolean enableOrDisableAddOns(final JSONObject jsonObject) {
        if (!this.getAddOnUpdateRunningStatus()) {
            this.updateAddOnUpdateRunningStatus(true);
            try {
                final String addOnName = jsonObject.getString("add_on_name");
                final int status = jsonObject.getInt("status");
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AddOns"));
                final Criteria cr = new Criteria(new Column("AddOns", "ADD_ON_NAME"), (Object)addOnName, 0);
                sq.setCriteria(cr);
                sq.addSelectColumn(new Column("AddOns", "ADD_ON_ID"));
                sq.addSelectColumn(new Column("AddOns", "ADD_ON_NAME"));
                sq.addSelectColumn(new Column("AddOns", "ADDON_HANDLER_CLASS"));
                final DataObject dO = DataAccess.get(sq);
                final Row addOnsRow = dO.getRow("AddOns");
                if (addOnsRow == null) {
                    this.updateAddOnUpdateRunningStatus(false);
                    return false;
                }
                addOnsRow.set("ADDON_STATUS", (Object)status);
                final String className = (String)addOnsRow.get("ADDON_HANDLER_CLASS");
                dO.updateRow(addOnsRow);
                if (status == 1 || status == 2) {
                    AddOnTrialHandler.getInstance().generateTrialConfFile(addOnName);
                }
                if (!className.isEmpty() && !className.equalsIgnoreCase("--")) {
                    final AddOnsApplicationHandler api = (AddOnsApplicationHandler)Class.forName(className).newInstance();
                    if (!api.addOnStatusUpdated(status, jsonObject)) {
                        this.updateAddOnUpdateRunningStatus(false);
                        return false;
                    }
                }
                DataAccess.update(dO);
                this.updateAddOnUpdateRunningStatus(false);
                return true;
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, " *** Exception while updating AddonStatus table *** ", ex);
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
        }
        return false;
    }
    
    public boolean updateAddOnStatusForCustomers(final JSONObject jsonObject) {
        try {
            if (this.getAddOnUpdateRunningStatus()) {
                jsonObject.put("error", (Object)"Already updating the addon status .. wait till it gets updated");
                return false;
            }
            this.updateAddOnUpdateRunningStatus(true);
            final String addOnName = jsonObject.getString("add_on_name");
            final int status = jsonObject.getInt("status");
            final Long customerId = jsonObject.optLong("customer_id", (long)CustomerInfoUtil.getInstance().getCustomerId());
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AddOns"));
            sq.addJoin(new Join("AddOns", "AddOnStatus", new String[] { "ADD_ON_ID" }, new String[] { "ADD_ON_ID" }, 1));
            final Criteria cr = new Criteria(new Column("AddOns", "ADD_ON_NAME"), (Object)addOnName, 0);
            sq.addSelectColumn(new Column("AddOns", "ADD_ON_ID"));
            sq.addSelectColumn(new Column("AddOns", "ADD_ON_NAME"));
            sq.addSelectColumn(new Column("AddOns", "ADDON_HANDLER_CLASS"));
            sq.addSelectColumn(new Column("AddOns", "META_FILE_PATH"));
            sq.addSelectColumn(new Column("AddOnStatus", "ADD_ON_STATUS_ID"));
            sq.addSelectColumn(new Column("AddOnStatus", "CUSTOMER_ID"));
            sq.addSelectColumn(new Column("AddOnStatus", "STATUS"));
            sq.setCriteria(cr);
            final DataObject dO = DataAccess.get(sq);
            final Row addOnsRow = dO.getRow("AddOns");
            if (addOnsRow == null) {
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            Row addOnStatusRow = dO.getRow("AddOnStatus", new Criteria(new Column("AddOnStatus", "CUSTOMER_ID"), (Object)customerId, 0));
            Row backUpAddOnRow = null;
            if (addOnStatusRow == null) {
                AddOnTrialHandler.getInstance().generateTrialConfFile(addOnName);
                addOnStatusRow = new Row("AddOnStatus");
                addOnStatusRow.set("ADD_ON_ID", addOnsRow.get("ADD_ON_ID"));
                addOnStatusRow.set("CUSTOMER_ID", (Object)customerId);
                dO.addRow(addOnStatusRow);
            }
            else {
                backUpAddOnRow = (Row)addOnStatusRow.clone();
            }
            addOnStatusRow.set("STATUS", (Object)status);
            dO.updateRow(addOnStatusRow);
            final String className = (String)addOnsRow.get("ADDON_HANDLER_CLASS");
            final boolean enabledForSpecifics = status == 2;
            if (className.isEmpty() || className.equalsIgnoreCase("--")) {
                this.logger.log(Level.SEVERE, "the class {0} addon-handler for this addon is not defined.", className);
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            final AddOnsApplicationHandler api = (AddOnsApplicationHandler)Class.forName(className).newInstance();
            if (!api.addOnPreHandling(status, jsonObject)) {
                api.addOnPreHandlingRevert(status, jsonObject);
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            if (enabledForSpecifics && jsonObject.has("resource_ids") && api.getLicensedResourceCountForAddon() < jsonObject.getJSONArray("resource_ids").length()) {
                jsonObject.put("error", (Object)"Trying to manage more computers than Licensed");
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            addOnStatusRow.set("UPDATED_TIME", (Object)new Long(System.currentTimeMillis() / 1000L));
            dO.updateRow(addOnStatusRow);
            DataAccess.update(dO);
            if (!api.addOnPostHandling(status, jsonObject)) {
                api.addOnPostHandlingRevert(status, jsonObject);
                api.addOnPreHandlingRevert(status, jsonObject);
                if (backUpAddOnRow == null) {
                    dO.deleteRow(addOnStatusRow);
                }
                else {
                    dO.updateRow(addOnStatusRow);
                }
                DataAccess.update(dO);
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            this.generateAddonsMeta(customerId);
            this.updateAddOnUpdateRunningStatus(false);
            return true;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, null, (Throwable)e);
            this.updateAddOnUpdateRunningStatus(false);
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, null, (Throwable)e2);
            this.updateAddOnUpdateRunningStatus(false);
        }
        catch (final IllegalAccessException e3) {
            this.logger.log(Level.SEVERE, null, e3);
            this.updateAddOnUpdateRunningStatus(false);
        }
        catch (final InstantiationException e4) {
            this.logger.log(Level.SEVERE, null, e4);
            this.updateAddOnUpdateRunningStatus(false);
        }
        catch (final ClassNotFoundException e5) {
            this.logger.log(Level.SEVERE, null, e5);
            this.updateAddOnUpdateRunningStatus(false);
        }
        catch (final Exception e6) {
            this.logger.log(Level.SEVERE, null, e6);
            this.updateAddOnUpdateRunningStatus(false);
        }
        return false;
    }
    
    public boolean enableOrDisableAddOnsForAllCustomers(JSONObject jsonObject) throws Exception {
        final String skipFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        final String customerIdInThreadLocal = CustomerInfoThreadLocal.getCustomerId();
        final String summaryPageInThreadLocal = CustomerInfoThreadLocal.getSummaryPage();
        CustomerInfoThreadLocal.setSkipCustomerFilter(Boolean.FALSE.toString());
        CustomerInfoThreadLocal.setSummaryPage(Boolean.FALSE.toString());
        boolean status = true;
        final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        final List<Map<Long, String>> customerList = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userId);
        if (customerList != null && customerList.size() > 0) {
            for (final HashMap map : customerList) {
                final long customerId = Long.parseLong("" + map.get("CUSTOMER_ID"));
                CustomerInfoThreadLocal.setCustomerId("" + customerId);
                jsonObject.put("customer_id", customerId);
                final JSONObject cloneJSON = new JSONObject(jsonObject.toString());
                status = (status && getInstance().updateAddOnStatusForCustomers(cloneJSON));
                if (!status) {
                    jsonObject = cloneJSON;
                }
            }
        }
        else {
            jsonObject.put("customer_id", -1L);
            status = this.updateAddOnStatusForMSPWithoutCustomer(jsonObject);
        }
        CustomerInfoThreadLocal.setSkipCustomerFilter(skipFilter);
        CustomerInfoThreadLocal.setCustomerId(customerIdInThreadLocal);
        CustomerInfoThreadLocal.setSummaryPage(summaryPageInThreadLocal);
        return status;
    }
    
    public boolean enableOrDisableAddOnsForSpecificCustomer(final JSONObject jsonObject, final long customerId) throws Exception {
        final String skipFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        final String customerIdInThreadLocal = CustomerInfoThreadLocal.getCustomerId();
        final String summaryPageInThreadLocal = CustomerInfoThreadLocal.getSummaryPage();
        CustomerInfoThreadLocal.setSkipCustomerFilter(Boolean.FALSE.toString());
        CustomerInfoThreadLocal.setCustomerId("" + customerId);
        CustomerInfoThreadLocal.setSummaryPage(Boolean.FALSE.toString());
        jsonObject.put("customer_id", customerId);
        final boolean status = getInstance().updateAddOnStatusForCustomers(jsonObject);
        CustomerInfoThreadLocal.setSkipCustomerFilter(skipFilter);
        CustomerInfoThreadLocal.setCustomerId(customerIdInThreadLocal);
        CustomerInfoThreadLocal.setSummaryPage(summaryPageInThreadLocal);
        return status;
    }
    
    public void updateAddOnUpdateRunningStatus(final boolean status) {
        SyMUtil.updateSyMParameter("ADDON_STATUS_RUNNING", String.valueOf(status));
    }
    
    public boolean getAddOnUpdateRunningStatus() {
        String runningStatus = SyMUtil.getSyMParameter("ADDON_STATUS_RUNNING");
        if (runningStatus == null || runningStatus.isEmpty()) {
            runningStatus = "false";
        }
        return Boolean.parseBoolean(runningStatus);
    }
    
    public void generateAddonsMeta(final Long customerId) throws Exception {
        try {
            final JSONArray addOnDetailsArray = this.getAddOnsDetails(customerId, true);
            final String addOnJSONFile = DCMetaDataUtil.getInstance().getClientDataDir(customerId) + File.separator + AddOnConstants.meta_FILE;
            ApiFactoryProvider.getFileAccessAPI().writeFile(addOnJSONFile, this.getAddOnsStatus(addOnDetailsArray).toString().getBytes());
        }
        catch (final IOException ex) {
            this.logger.log(Level.SEVERE, "error occured while exception writing the meta file", ex);
            throw ex;
        }
    }
    
    public JSONArray getAddOnsDetails() throws Exception {
        final SelectQuery addOnsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AddOns"));
        addOnsQuery.addJoin(new Join("AddOns", "AddOnStatus", new String[] { "ADD_ON_ID" }, new String[] { "ADD_ON_ID" }, 1));
        addOnsQuery.addJoin(new Join("AddOnStatus", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        final CustomerInfoUtil customerInfoUtil = CustomerInfoUtil.getInstance();
        final boolean isMSP = customerInfoUtil.isMSP();
        if (isMSP) {
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userId != null) {
                final SelectQuery addOnsSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LoginUserCustomerMapping"));
                addOnsSubQuery.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"));
                final Criteria userCriteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userId, 0);
                addOnsSubQuery.setCriteria(userCriteria);
                final DerivedTable loginUserCustomerMappingDerivedTable = new DerivedTable("loginUserCustMapping", (Query)addOnsSubQuery);
                addOnsQuery.addJoin(new Join(Table.getTable("CustomerInfo"), (Table)loginUserCustomerMappingDerivedTable, new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            }
        }
        final Long customerId = customerInfoUtil.getCustomerId();
        if (customerId != null && customerId > -1L) {
            final Criteria customerCriteria = new Criteria(new Column("AddOnStatus", "CUSTOMER_ID"), (Object)customerId, 0);
            addOnsQuery.setCriteria((addOnsQuery.getCriteria() != null) ? addOnsQuery.getCriteria().and(customerCriteria) : customerCriteria);
        }
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "ADD_ON_NAME"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "ADD_ON_ID"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "META_FILE_PATH"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "ADDON_STATUS"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "ADDON_HANDLER_CLASS"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOnStatus", "ADD_ON_ID"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOnStatus", "ADD_ON_STATUS_ID"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOnStatus", "UPDATED_TIME"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOnStatus", "STATUS"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOnStatus", "CUSTOMER_ID"));
        final DataObject dO = DataAccess.get(addOnsQuery);
        final Iterator<Row> addOnsIterator = dO.getRows("AddOns");
        final JSONArray jsonArray = new JSONArray();
        while (addOnsIterator.hasNext()) {
            final Row addOnRow = addOnsIterator.next();
            final JSONObject addOnRowJSON = new JSONObject();
            addOnRowJSON.put("add_on_name", addOnRow.get("ADD_ON_NAME"));
            addOnRowJSON.put("add_on_id", (Object)("" + addOnRow.get("ADD_ON_ID")));
            addOnRowJSON.put("add_on_status", (Object)("" + addOnRow.get("ADDON_STATUS")));
            addOnRowJSON.put("meta_file_path", (Object)("" + addOnRow.get("META_FILE_PATH")));
            final Iterator<Row> addOnStatusIterator = dO.getRows("AddOnStatus", addOnRow);
            final JSONArray addOnsArray = new JSONArray();
            while (addOnStatusIterator.hasNext()) {
                final Row addOnStatusRow = addOnStatusIterator.next();
                final JSONObject addOnStatusJSON = new JSONObject();
                addOnStatusJSON.put("updated_time", (Object)("" + addOnStatusRow.get("UPDATED_TIME")));
                addOnStatusJSON.put("addon_customer_status", (Object)("" + addOnStatusRow.get("STATUS")));
                addOnStatusJSON.put("customer_id", (Object)("" + addOnStatusRow.get("CUSTOMER_ID")));
                addOnsArray.put((Object)addOnStatusJSON);
            }
            addOnRowJSON.put("add_on_customer_data", (Object)addOnsArray);
            jsonArray.put((Object)addOnRowJSON);
        }
        return jsonArray;
    }
    
    public JSONArray getAddOnsDetails(final Long customerId, final Boolean includeAllSecurityModules) throws Exception {
        final SelectQuery addOnsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AddOns"));
        addOnsQuery.addJoin(new Join("AddOns", "AddOnStatus", new String[] { "ADD_ON_ID" }, new String[] { "ADD_ON_ID" }, 2));
        addOnsQuery.addJoin(new Join("AddOnStatus", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        if (!includeAllSecurityModules) {
            addOnsQuery.setCriteria(new Criteria(Column.getColumn("AddOns", "ADDON_TYPE"), (Object)0, 0));
        }
        final CustomerInfoUtil customerInfoUtil = CustomerInfoUtil.getInstance();
        final boolean isMSP = customerInfoUtil.isMSP();
        if (customerId != null) {
            final Criteria customerCriteria = new Criteria(new Column("AddOnStatus", "CUSTOMER_ID"), (Object)customerId, 0);
            addOnsQuery.setCriteria((addOnsQuery.getCriteria() != null) ? addOnsQuery.getCriteria().and(customerCriteria) : customerCriteria);
        }
        if (isMSP) {
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userId != null) {
                addOnsQuery.addJoin(new Join("CustomerInfo", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
                final Criteria userCriteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userId, 0);
                addOnsQuery.setCriteria((addOnsQuery.getCriteria() != null) ? addOnsQuery.getCriteria().and(userCriteria) : userCriteria);
            }
        }
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "ADD_ON_NAME"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "ADD_ON_ID"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "META_FILE_PATH"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOnStatus", "UPDATED_TIME"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOns", "ADDON_HANDLER_CLASS"));
        addOnsQuery.addSelectColumn(Column.getColumn("AddOnStatus", "STATUS"));
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        final JSONArray jsonArray = new JSONArray();
        try {
            conn = relationalAPI.getConnection();
            ds = relationalAPI.executeQuery((Query)addOnsQuery, conn);
            while (ds.next()) {
                final JSONObject temp = new JSONObject();
                temp.put("add_on_name", ds.getValue("ADD_ON_NAME"));
                temp.put("add_on_id", ds.getValue("ADD_ON_ID"));
                temp.put("status", (Object)String.valueOf(ds.getValue("STATUS")));
                temp.put("meta_file_path", ds.getValue("META_FILE_PATH"));
                temp.put("updated_time", (Object)String.valueOf(ds.getValue("UPDATED_TIME")));
                temp.put("handler_class", ds.getValue("ADDON_HANDLER_CLASS"));
                jsonArray.put((Object)temp);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSON exception thrown  ", (Throwable)e);
            throw e;
        }
        catch (final QueryConstructionException e2) {
            this.logger.log(Level.SEVERE, "Exception occurred while constructing the query ", (Throwable)e2);
            throw e2;
        }
        catch (final SQLException e3) {
            this.logger.log(Level.SEVERE, "Exception occurred while retrieving  the data for the addon status ", e3);
            throw e3;
        }
        finally {
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException e4) {
                    this.logger.log(Level.SEVERE, " *** Exception while closing DS *** ", e4);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException e4) {
                    this.logger.log(Level.SEVERE, " ***  Exception while closing Connection *** ", e4);
                }
            }
        }
        return jsonArray;
    }
    
    public JSONArray getAddOnsStatus(final Long customerId) throws Exception {
        final JSONArray jsonArray = this.getAddOnsDetails(customerId, true);
        return this.getAddOnsStatus(jsonArray);
    }
    
    public JSONArray getAddOnsStatus(final JSONArray jsonArray) throws Exception {
        final JSONArray ad = new JSONArray();
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject addOnJSON = jsonArray.getJSONObject(i);
            final JSONObject tmp = new JSONObject();
            tmp.put("add_on_name", addOnJSON.get("add_on_name"));
            tmp.put("status", addOnJSON.get("status"));
            tmp.put("meta_file_path", addOnJSON.get("meta_file_path"));
            tmp.put("updated_time", addOnJSON.get("updated_time"));
            ad.put((Object)tmp);
        }
        return ad;
    }
    
    public JSONArray getResourceAddOnStatus(final Long resId) throws Exception {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resId);
        final JSONArray response = this.getAddOnsDetails(customerId, true);
        final JSONArray addOnStatusArray = new JSONArray();
        for (int i = 0; i < response.length(); ++i) {
            final JSONObject addOnStatus = new JSONObject();
            addOnStatus.put("add_on_name", (Object)response.getJSONObject(i).getString("add_on_name"));
            final JSONObject tempRequest = new JSONObject();
            tempRequest.put("resource_id", (Object)resId);
            tempRequest.put("add_on_status", response.getJSONObject(i).getInt("status"));
            tempRequest.put("add_on_name", (Object)response.getJSONObject(i).getString("add_on_name"));
            tempRequest.put("handler", (Object)response.getJSONObject(i).getString("handler_class"));
            addOnStatus.put("status", (Object)String.valueOf(this.getResourceEnabledStatus(tempRequest)));
            addOnStatusArray.put((Object)addOnStatus);
        }
        return addOnStatusArray;
    }
    
    public int getResourceEnabledStatus(final JSONObject jsonObject) throws JSONException, ClassNotFoundException, IllegalAccessException, InstantiationException, QueryConstructionException, SQLException {
        int status = 0;
        final int addOnStatus = jsonObject.getInt("add_on_status");
        if (addOnStatus == 1 || addOnStatus == 2) {
            final AddOnsApplicationHandler api = this.handlerClassMap.get(jsonObject.getString("add_on_name"));
            if (api.isResourceApplicableForAddon(jsonObject.getLong("resource_id"))) {
                status = addOnStatus;
            }
        }
        return status;
    }
    
    public boolean addResourceInfo(final JSONObject jsonObject) throws Exception {
        final JSONArray addOnStatusArray = jsonObject.getJSONArray("add_ons");
        for (int j = 0; j < addOnStatusArray.length(); ++j) {
            final AddOnsApplicationHandler aph = (AddOnsApplicationHandler)Class.forName(addOnStatusArray.getJSONObject(j).getString("handler_class")).newInstance();
            if (aph.getLicensedResourceCountForAddon() < aph.getAddonInstalledCount() + 1) {
                aph.addOnResourceLimitExceedHandling();
                return false;
            }
            aph.addAddonForResource(jsonObject.getLong("resource_id"));
        }
        return true;
    }
    
    public boolean removeResourceInfo(final JSONObject jsonObject) throws Exception {
        final JSONArray addOnStatusArray = jsonObject.getJSONArray("add_ons");
        for (int j = 0; j < addOnStatusArray.length(); ++j) {
            final AddOnsApplicationHandler aph = (AddOnsApplicationHandler)Class.forName(addOnStatusArray.getJSONObject(j).getString("handler_class")).newInstance();
            aph.removeAddonForResource(jsonObject.getLong("resource_id"));
        }
        jsonObject.put("add_ons", (Object)addOnStatusArray);
        return true;
    }
    
    public void regenerateComponentStatus(final SoMEvent[] somEventArr, final boolean isRemoved) {
        try {
            for (int i = 0; i < somEventArr.length; ++i) {
                final JSONObject temp1 = new JSONObject();
                temp1.put("resource_id", (Object)somEventArr[i].resourceID);
                temp1.put("customer_id", (Object)somEventArr[i].customerID);
                temp1.put("add_ons", (Object)this.getAddOnsDetails(somEventArr[i].customerID, false));
                if (isRemoved) {
                    this.removeResourceInfo(temp1);
                }
                else {
                    this.addResourceInfo(temp1);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while regenerating the component status for addons", e);
        }
    }
    
    public void regenerateComponentStatus(final JSONObject jsonObject, final boolean isRemoved) {
        try {
            final Long customerId = (jsonObject.has("customer_id") && jsonObject.get("customer_id") != null) ? Long.parseLong(jsonObject.get("customer_id").toString()) : 0L;
            final JSONArray jsonArray = (jsonObject.has("resources") && jsonObject.get("resources") != null) ? jsonObject.getJSONArray("resources") : new JSONArray();
            if (customerId > 0L) {
                for (int i = 0; i < jsonArray.length(); ++i) {
                    final JSONObject temp1 = new JSONObject();
                    temp1.put("resource_id", jsonArray.get(i));
                    temp1.put("customer_id", (Object)customerId);
                    temp1.put("add_ons", (Object)this.getAddOnsDetails(customerId, false));
                    if (isRemoved) {
                        this.removeResourceInfo(temp1);
                    }
                    else {
                        this.addResourceInfo(temp1);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while regenerating the component status for addons", e);
        }
    }
    
    private void addOnResourceLimitExceedHandling(final String addOnName) throws Exception {
        final UpdateQuery addOnAllToSpecificQuery = (UpdateQuery)new UpdateQueryImpl("AddOnStatus");
        addOnAllToSpecificQuery.addJoin(new Join("AddOnStatus", "AddOns", new String[] { "ADD_ON_ID" }, new String[] { "ADD_ON_ID" }, 2));
        addOnAllToSpecificQuery.setCriteria(new Criteria(new Column("AddOns", "ADD_ON_NAME"), (Object)addOnName, 0));
        addOnAllToSpecificQuery.setUpdateColumn("STATUS", (Object)2);
        addOnAllToSpecificQuery.setUpdateColumn("UPDATED_TIME", (Object)new Long(System.currentTimeMillis() / 1000L));
        DataAccess.update(addOnAllToSpecificQuery);
    }
    
    public JSONArray getApplicableAddons(final String applicationName) throws DataAccessException, JSONException {
        final SelectQuery applicableAddons = (SelectQuery)new SelectQueryImpl(Table.getTable("DMApplication"));
        applicableAddons.addJoin(new Join("DMApplication", "DMApplnAddOnRel", new String[] { "DMAPPLICATION_ID" }, new String[] { "DMAPPLICATION_ID" }, 2));
        applicableAddons.addJoin(new Join("DMApplnAddOnRel", "AddOns", new String[] { "ADD_ON_ID" }, new String[] { "ADD_ON_ID" }, 2));
        applicableAddons.addSelectColumn(new Column("AddOns", "ADD_ON_ID"));
        applicableAddons.addSelectColumn(new Column("AddOns", "ADD_ON_NAME"));
        final DataObject addOnsDO = DataAccess.get(applicableAddons);
        final JSONArray addOnsArray = new JSONArray();
        if (addOnsDO != null && !addOnsDO.isEmpty()) {
            final Iterator iterator = addOnsDO.getRows("AddOns");
            while (iterator.hasNext()) {
                final Row addOnRow = iterator.next();
                final JSONObject addOnJSON = new JSONObject();
                addOnJSON.put("add_on_id", addOnRow.get("ADD_ON_ID"));
                addOnJSON.put("add_on_name", addOnRow.get("ADD_ON_NAME"));
                addOnsArray.put((Object)addOnJSON);
            }
        }
        return addOnsArray;
    }
    
    public boolean updateAddOnStatusForMSPWithoutCustomer(final JSONObject jsonObject) {
        try {
            this.logger.info("AddOnHandler :: updateAddOnStatusForMSPWithoutCustomer invoked");
            if (this.getAddOnUpdateRunningStatus()) {
                jsonObject.put("error", (Object)"Already updating the addon status .. wait till it gets updated");
                return false;
            }
            this.updateAddOnUpdateRunningStatus(true);
            final String addOnName = jsonObject.getString("add_on_name");
            final int status = jsonObject.getInt("status");
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AddOns"));
            sq.addJoin(new Join("AddOns", "AddOnStatus", new String[] { "ADD_ON_ID" }, new String[] { "ADD_ON_ID" }, 1));
            final Criteria cr = new Criteria(new Column("AddOns", "ADD_ON_NAME"), (Object)addOnName, 0);
            sq.addSelectColumn(new Column("AddOns", "ADD_ON_ID"));
            sq.addSelectColumn(new Column("AddOns", "ADD_ON_NAME"));
            sq.addSelectColumn(new Column("AddOns", "ADDON_HANDLER_CLASS"));
            sq.addSelectColumn(new Column("AddOns", "META_FILE_PATH"));
            sq.addSelectColumn(new Column("AddOnStatus", "ADD_ON_STATUS_ID"));
            sq.addSelectColumn(new Column("AddOnStatus", "CUSTOMER_ID"));
            sq.addSelectColumn(new Column("AddOnStatus", "STATUS"));
            sq.setCriteria(cr);
            final DataObject dO = DataAccess.get(sq);
            final Row addOnsRow = dO.getRow("AddOns");
            if (addOnsRow == null) {
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            final String className = (String)addOnsRow.get("ADDON_HANDLER_CLASS");
            if (className.isEmpty() || className.equalsIgnoreCase("--")) {
                this.logger.log(Level.SEVERE, "the class {0} addon-handler for this addon is not defined.", className);
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            final AddOnsApplicationHandler api = (AddOnsApplicationHandler)Class.forName(className).newInstance();
            if (!api.addOnPreHandling(status, jsonObject)) {
                api.addOnPreHandlingRevert(status, jsonObject);
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            if (!api.addOnPostHandling(status, jsonObject)) {
                api.addOnPostHandlingRevert(status, jsonObject);
                api.addOnPreHandlingRevert(status, jsonObject);
                this.updateAddOnUpdateRunningStatus(false);
                return false;
            }
            this.updateAddOnUpdateRunningStatus(false);
            return true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "AddOnHandler :: Exception while processing updateAddOnStatusForMSPWithoutCustomer method", e);
            this.updateAddOnUpdateRunningStatus(false);
            return false;
        }
    }
    
    static {
        AddOnHandler.api = null;
    }
}
