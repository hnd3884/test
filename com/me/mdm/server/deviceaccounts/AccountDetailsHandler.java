package com.me.mdm.server.deviceaccounts;

import com.me.mdm.server.util.Hash.PasswordHash;
import com.me.mdm.server.util.Hash.PasswordHashHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.persistence.DataObject;
import com.me.mdm.api.paging.PagingUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class AccountDetailsHandler
{
    private Logger logger;
    
    public AccountDetailsHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    private SelectQuery addAccountColumns(final SelectQuery query) {
        query.addSelectColumn(Column.getColumn("MdComputerAccount", "ACCOUNT_ID"));
        query.addSelectColumn(Column.getColumn("MdComputerAccount", "ACCOUNT_NAME"));
        query.addSelectColumn(Column.getColumn("MdComputerAccount", "ACCOUNT_DESCRIPTION"));
        query.addSelectColumn(Column.getColumn("MdComputerAccount", "ACCOUNT_TYPE"));
        query.addSelectColumn(Column.getColumn("MdComputerAccount", "SHORT_NAME"));
        query.addSelectColumn(Column.getColumn("MdComputerAccount", "FULL_NAME"));
        query.addSelectColumn(Column.getColumn("MdComputerAccount", "HASH_ALGORITHM"));
        return query;
    }
    
    private SelectQuery addAccountConfigColumn(SelectQuery query) {
        query.addSelectColumn(Column.getColumn("MdMacAccountConfigSettings", "ACCOUNT_CONFIG_ID"));
        query.addSelectColumn(Column.getColumn("MdMacAccountConfigSettings", "SKIP_ACC_CREATION"));
        query.addSelectColumn(Column.getColumn("MdMacAccountConfigSettings", "SET_REGULAR_ACCOUNT"));
        query.addSelectColumn(Column.getColumn("MdMacAccountToConfig", "ACCOUNT_CONFIG_ID"));
        query.addSelectColumn(Column.getColumn("MdMacAccountToConfig", "ACCOUNT_ID"));
        query.addSelectColumn(Column.getColumn("MdMacAccountToConfig", "HIDDEN"));
        query = this.addAccountColumns(query);
        return query;
    }
    
    public JSONObject getAllAccounts(final JSONObject request) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(request);
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
            final DataObject dataObject = this.getAccountDO(customerID, -1L, pagingUtil);
            final JSONObject accounts = new JSONObject();
            accounts.put("Accounts", (Object)new JSONArray((Collection)DBUtil.getListfromDataObject(dataObject, "MdComputerAccount")));
            final SelectQuery cQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdComputerAccount"));
            final Criteria countCriteria = new Criteria(Column.getColumn("MdComputerAccount", "CUSTOMER_ID"), (Object)customerID, 0);
            cQuery.addSelectColumn(Column.getColumn("MdComputerAccount", "ACCOUNT_ID").distinct().count());
            cQuery.setCriteria(countCriteria);
            final int count = DBUtil.getRecordCount(cQuery);
            if (count != 0) {
                accounts.put("paging", (Object)pagingUtil.getPagingJSON(count));
            }
            return accounts;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting account details", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private DataObject getAccountDO(final Long customerID, final Long accountID, final PagingUtil pagingUtil) throws Exception {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdComputerAccount"));
        if (pagingUtil != null) {
            query.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
        }
        Criteria criteria = new Criteria(Column.getColumn("MdComputerAccount", "CUSTOMER_ID"), (Object)customerID, 0);
        if (accountID != -1L) {
            criteria = criteria.and(new Criteria(Column.getColumn("MdComputerAccount", "ACCOUNT_ID"), (Object)accountID, 0));
        }
        query = this.addAccountColumns(query);
        query.setCriteria(criteria);
        return MDMUtil.getPersistence().get(query);
    }
    
    private DataObject getMacAccountConfigDO(final Long customerID, final Long accountConfigID) throws Exception {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdMacAccountConfigSettings"));
        query.addJoin(new Join("MdMacAccountConfigSettings", "MdMacAccountToConfig", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 2));
        query.addJoin(new Join("MdMacAccountToConfig", "MdComputerAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("MdComputerAccount", "CUSTOMER_ID"), (Object)customerID, 0);
        if (accountConfigID != -1L) {
            criteria = criteria.and(new Criteria(Column.getColumn("MdMacAccountConfigSettings", "ACCOUNT_CONFIG_ID"), (Object)accountConfigID, 0));
        }
        query = this.addAccountConfigColumn(query);
        query.setCriteria(criteria);
        return MDMUtil.getPersistence().get(query);
    }
    
    public JSONObject getAccountDetails(final Long customerID, final Long accountID) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getAccountDO(customerID, accountID, null);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final Row row = dataObject.getFirstRow("MdComputerAccount");
            final JSONObject response = new JSONObject();
            response.put("ACCOUNT_ID", (Object)accountID);
            response.put("ACCOUNT_NAME", (Object)row.get("ACCOUNT_NAME"));
            response.put("ACCOUNT_DESCRIPTION", (Object)row.get("ACCOUNT_DESCRIPTION"));
            response.put("ACCOUNT_TYPE", (Object)row.get("ACCOUNT_TYPE"));
            response.put("SHORT_NAME", (Object)row.get("SHORT_NAME"));
            response.put("FULL_NAME", (Object)row.get("FULL_NAME"));
            response.put("HASH_ALGORITHM", (Object)row.get("HASH_ALGORITHM"));
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting account details", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private Map getAccountMap(final DataObject dataObject) throws Exception {
        final HashMap<Long, List> configMap = new HashMap<Long, List>();
        final Iterator<Row> iterator = dataObject.getRows("MdMacAccountToConfig");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long accountConfigID = (Long)row.get("ACCOUNT_CONFIG_ID");
            final Long accountID = (Long)row.get("ACCOUNT_ID");
            final Boolean hidden = (Boolean)row.get("HIDDEN");
            List accountList;
            if (!configMap.containsKey(accountConfigID)) {
                accountList = new ArrayList();
            }
            else {
                accountList = configMap.get(accountConfigID);
            }
            final JSONObject json = new JSONObject();
            json.put("ACCOUNT_ID".toLowerCase(), (Object)accountID);
            json.put("HIDDEN".toLowerCase(), (Object)hidden);
            accountList.add(json);
            configMap.put(accountConfigID, accountList);
        }
        return configMap;
    }
    
    public JSONArray getAllMacAccountConfiguration(final Long customerID) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getMacAccountConfigDO(customerID, -1L);
            final Map accountMap = this.getAccountMap(dataObject);
            final JSONArray configArray = new JSONArray();
            final Iterator<Row> iterator = dataObject.getRows("MdMacAccountConfigSettings");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long accountConfigID = (Long)row.get("ACCOUNT_CONFIG_ID");
                final Boolean skipAccountCreation = (Boolean)row.get("SKIP_ACC_CREATION");
                final Boolean setRegularAccount = (Boolean)row.get("SET_REGULAR_ACCOUNT");
                final List accountList = accountMap.get(accountConfigID);
                final JSONObject accountConfig = new JSONObject();
                accountConfig.put("ACCOUNT_CONFIG_ID", (Object)accountConfigID);
                accountConfig.put("SKIP_ACC_CREATION", (Object)skipAccountCreation);
                accountConfig.put("SET_REGULAR_ACCOUNT", (Object)setRegularAccount);
                accountConfig.put("ACCOUNTS", (Object)new JSONArray((Collection)accountList));
                configArray.put((Object)accountConfig);
            }
            return configArray;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting Mac account configuration details", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getMacAccountConfiguration(final Long customerID, final Long accountConfigID) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getMacAccountConfigDO(customerID, accountConfigID);
            final JSONArray accountArray = new JSONArray();
            final Iterator<Row> iterator = dataObject.getRows("MdMacAccountToConfig");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long accountID = (Long)row.get("ACCOUNT_ID");
                final Boolean hidden = (Boolean)row.get("HIDDEN");
                final JSONObject json = new JSONObject();
                json.put("ACCOUNT_ID", (Object)accountID);
                json.put("HIDDEN", (Object)hidden);
                accountArray.put((Object)json);
            }
            final Row configRow = dataObject.getFirstRow("MdMacAccountConfigSettings");
            final Boolean skipAccCreation = (Boolean)configRow.get("SKIP_ACC_CREATION");
            final Boolean setRegularAccount = (Boolean)configRow.get("SET_REGULAR_ACCOUNT");
            final JSONObject response = new JSONObject();
            response.put("ACCOUNT_CONFIG_ID", (Object)accountConfigID);
            response.put("SKIP_ACC_CREATION", (Object)skipAccCreation);
            response.put("SET_REGULAR_ACCOUNT", (Object)setRegularAccount);
            response.put("ACCOUNTS", (Object)accountArray);
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting Mac account configuration details", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addOrUpdateMacAccountConfig(final JSONObject request) throws APIHTTPException {
        try {
            final Long customerID = JSONUtil.optLongForUVH(request, "CUSTOMER_ID", Long.valueOf(-1L));
            Long accountConfigID = JSONUtil.optLongForUVH(request, "ACCOUNT_CONFIG_ID", Long.valueOf(-2L));
            if (accountConfigID == -1L) {
                accountConfigID = -2L;
            }
            final DataObject dataObject = this.getMacAccountConfigDO(customerID, accountConfigID);
            if (dataObject.isEmpty()) {
                Row accountConfigRow = new Row("MdMacAccountConfigSettings");
                accountConfigRow.set("SKIP_ACC_CREATION", (Object)request.getBoolean("SKIP_ACC_CREATION".toLowerCase()));
                accountConfigRow.set("SET_REGULAR_ACCOUNT", (Object)request.getBoolean("SET_REGULAR_ACCOUNT".toLowerCase()));
                final JSONArray accounntArray = request.getJSONArray("accounts");
                dataObject.addRow(accountConfigRow);
                if (accounntArray.length() == 0) {
                    throw new APIHTTPException("COM0009", new Object[0]);
                }
                for (int i = 0; i < accounntArray.length(); ++i) {
                    final JSONObject accountDetails = accounntArray.getJSONObject(i);
                    final Row accountMapRow = new Row("MdMacAccountToConfig");
                    accountMapRow.set("ACCOUNT_CONFIG_ID", accountConfigRow.get("ACCOUNT_CONFIG_ID"));
                    accountMapRow.set("ACCOUNT_ID", (Object)JSONUtil.optLong(accountDetails, "ACCOUNT_ID".toLowerCase(), null));
                    accountMapRow.set("HIDDEN", (Object)accountDetails.getBoolean("HIDDEN".toLowerCase()));
                    dataObject.addRow(accountMapRow);
                }
                MDMUtil.getPersistence().add(dataObject);
                accountConfigRow = dataObject.getRow("MdMacAccountConfigSettings");
                accountConfigID = (Long)accountConfigRow.get("ACCOUNT_CONFIG_ID");
            }
            else {
                final Row accountConfigRow = dataObject.getFirstRow("MdMacAccountConfigSettings");
                if (request.has("SKIP_ACC_CREATION".toLowerCase())) {
                    accountConfigRow.set("SKIP_ACC_CREATION", (Object)request.getBoolean("SKIP_ACC_CREATION".toLowerCase()));
                }
                if (request.has("SET_REGULAR_ACCOUNT".toLowerCase())) {
                    accountConfigRow.set("SET_REGULAR_ACCOUNT", (Object)request.getBoolean("SET_REGULAR_ACCOUNT".toLowerCase()));
                }
                if (request.has("accounts")) {
                    final JSONArray accountArray = request.getJSONArray("accounts");
                    if (accountArray.length() == 0) {
                        throw new APIHTTPException("COM0009", new Object[0]);
                    }
                    Iterator<Row> iterator = dataObject.getRows("MdMacAccountToConfig");
                    final List<Row> accountMapRowList = new ArrayList<Row>();
                    while (iterator.hasNext()) {
                        final Row accountMapRow = iterator.next();
                        accountMapRowList.add(accountMapRow);
                    }
                    iterator = accountMapRowList.iterator();
                    while (iterator.hasNext()) {
                        final Row accountMapRow = iterator.next();
                        dataObject.deleteRow(accountMapRow);
                    }
                    for (int j = 0; j < accountArray.length(); ++j) {
                        final JSONObject accountDetails2 = accountArray.getJSONObject(j);
                        final Row accountMapRow2 = new Row("MdMacAccountToConfig");
                        accountMapRow2.set("ACCOUNT_CONFIG_ID", accountConfigRow.get("ACCOUNT_CONFIG_ID"));
                        accountMapRow2.set("ACCOUNT_ID", (Object)JSONUtil.optLong(accountDetails2, "ACCOUNT_ID".toLowerCase(), null));
                        accountMapRow2.set("HIDDEN", (Object)accountDetails2.getBoolean("HIDDEN".toLowerCase()));
                        dataObject.addRow(accountMapRow2);
                    }
                }
                dataObject.updateRow(accountConfigRow);
                MDMUtil.getPersistence().update(dataObject);
            }
            Long userID = null;
            if (request.has("ADDED_USER".toLowerCase())) {
                userID = request.getLong("ADDED_USER".toLowerCase());
            }
            this.publishMacAccountConfiguration(accountConfigID, customerID, userID);
            return this.getMacAccountConfiguration(customerID, accountConfigID);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in add-uodate Mac account configuration", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public SelectQuery getCollectionIDAccountConfigQuery() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
        query.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join("ConfigDataItem", "MdMacAccountConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        return query;
    }
    
    public Long getAccountConfigIDForCollectionID(final Long collectionID) {
        try {
            final SelectQuery query = this.getCollectionIDAccountConfigQuery();
            query.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"));
            query.addSelectColumn(Column.getColumn("CfgDataToCollection", "CONFIG_DATA_ID"));
            query.addSelectColumn(Column.getColumn("MdMacAccountConfigPolicy", "ACCOUNT_CONFIG_ID"));
            query.addSelectColumn(Column.getColumn("MdMacAccountConfigPolicy", "CONFIG_DATA_ITEM_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Row row = dataObject.getFirstRow("MdMacAccountConfigPolicy");
            return (Long)row.get("ACCOUNT_CONFIG_ID");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in fetching associated accountConfigID of collectionID", e);
            return null;
        }
    }
    
    public Long getCollectionIDForAccountConfig(final Long accountConfigID) {
        try {
            final SelectQuery query = this.getCollectionIDAccountConfigQuery();
            query.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"));
            query.addSelectColumn(Column.getColumn("CfgDataToCollection", "CONFIG_DATA_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("MdMacAccountConfigPolicy", "ACCOUNT_CONFIG_ID"), (Object)accountConfigID, 0);
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (dataObject.isEmpty()) {
                return null;
            }
            final Row row = dataObject.getFirstRow("CfgDataToCollection");
            return (Long)row.get("COLLECTION_ID");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to fetching associated collectionID of account Configuration, returning null.", e);
            return null;
        }
    }
    
    private DataObject getAccountConfigForResourceDO(final Long resourceID) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MacAccountConfigToResource"));
        query.addSelectColumn(Column.getColumn("MacAccountConfigToResource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MacAccountConfigToResource", "ACCOUNT_CONFIG_ID"));
        final Criteria criteria = new Criteria(Column.getColumn("MacAccountConfigToResource", "RESOURCE_ID"), (Object)resourceID, 0);
        query.setCriteria(criteria);
        return MDMUtil.getPersistence().get(query);
    }
    
    public void addOrUpdateAccountConfigToResource(final Long accountConfig, final Long resourceID, final Integer status) {
        try {
            final DataObject dataObject = this.getAccountConfigForResourceDO(resourceID);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MacAccountConfigToResource");
                if (accountConfig != null) {
                    row.set("ACCOUNT_CONFIG_ID", (Object)accountConfig);
                }
                row.set("STATUS", (Object)status);
                dataObject.updateRow(row);
                MDMUtil.getPersistence().update(dataObject);
            }
            else {
                final Row row = new Row("MacAccountConfigToResource");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("ACCOUNT_CONFIG_ID", (Object)accountConfig);
                row.set("STATUS", (Object)status);
                dataObject.addRow(row);
                MDMUtil.getPersistence().add(dataObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in mapping account configuration to resource", e);
        }
    }
    
    public Long getAccountConfigForResource(final Long resourceID) {
        try {
            final DataObject dataObject = this.getAccountConfigForResourceDO(resourceID);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MacAccountConfigToResource");
                return (Long)row.get("ACCOUNT_CONFIG_ID");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getting Account configuration for resourceID", e);
        }
        return null;
    }
    
    private void publishMacAccountConfiguration(final Long accountConfigID, final Long customerID, final Long userId) throws APIHTTPException {
        try {
            final Long collectionID = this.getCollectionIDForAccountConfig(accountConfigID);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("LAST_MODIFIED_BY", (Object)userId);
            jsonObject.put("PROFILE_NAME", (Object)("AccountConfig:" + String.valueOf(accountConfigID)));
            jsonObject.put("PROFILE_TYPE", 7);
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"Account Configuration profile");
            jsonObject.put("CREATED_BY", (Object)userId);
            jsonObject.put("PROFILE_TYPE", 6);
            jsonObject.put("PLATFORM_TYPE", 6);
            jsonObject.put("ACCOUNT_CONFIG_ID", (Object)accountConfigID);
            jsonObject.put("CONFIG_ID", 752);
            jsonObject.put("CURRENT_CONFIG", (Object)"macaccountconfiguration");
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("APP_CONFIG", (Object)Boolean.FALSE);
            final JSONObject configJSON = new JSONObject();
            configJSON.put("ACCOUNT_CONFIG_ID", (Object)accountConfigID);
            configJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            configJSON.put("CONFIG_ID", 752);
            configJSON.put("CONFIG_NAME", (Object)"MAC_ACCOUNT_CONFIGURATION");
            configJSON.put("TABLE_NAME", (Object)"MdMacAccountConfigPolicy");
            jsonObject.put("macaccountconfiguration", (Object)configJSON);
            if (collectionID == null) {
                ProfileConfigHandler.addProfileCollection(jsonObject);
                ProfileConfigHandler.addOrModifyConfiguration(jsonObject);
            }
            else {
                final ProfileHandler handler = new ProfileHandler();
                jsonObject.put("PROFILE_ID", (Object)handler.getProfileIDFromCollectionID(collectionID));
                jsonObject.put("COLLECTION_ID", (Object)collectionID);
            }
            ProfileConfigHandler.publishProfile(jsonObject);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in add-update Mac account configuration", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteMacAccountConfig(final Long customerID, final Long accountConfigID) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getMacAccountConfigDO(customerID, accountConfigID);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdMacAccountToConfig");
                dataObject.deleteRow(row);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in delete Mac account configuration", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addOrUpdateAccount(final JSONObject request) throws APIHTTPException {
        try {
            final Long customerID = JSONUtil.optLongForUVH(request, "CUSTOMER_ID", Long.valueOf(-1L));
            Long accountID = JSONUtil.optLongForUVH(request, "ACCOUNT_ID", Long.valueOf(-2L));
            if (accountID == -1L) {
                accountID = -2L;
            }
            final DataObject dataObject = this.getAccountDO(customerID, accountID, null);
            if (dataObject.isEmpty()) {
                Row accountRow = new Row("MdComputerAccount");
                accountRow.set("ACCOUNT_NAME", (Object)request.optString("ACCOUNT_NAME".toLowerCase(), String.valueOf(request.get("FULL_NAME".toLowerCase()))));
                accountRow.set("ACCOUNT_DESCRIPTION", (Object)request.optString("ACCOUNT_DESCRIPTION".toLowerCase(), (String)null));
                accountRow.set("ACCOUNT_TYPE", (Object)request.getInt("ACCOUNT_TYPE".toLowerCase()));
                accountRow.set("CUSTOMER_ID", (Object)JSONUtil.optLongForUVH(request, "CUSTOMER_ID", Long.valueOf(-1L)));
                accountRow.set("SHORT_NAME", (Object)String.valueOf(request.get("SHORT_NAME".toLowerCase())).toLowerCase());
                accountRow.set("FULL_NAME", (Object)String.valueOf(request.get("FULL_NAME".toLowerCase())));
                if (!MDMStringUtils.isEmpty(request.optString("password", (String)null))) {
                    final PasswordHash passwordHash = PasswordHashHandler.getInstance().getHashAlgorithm(request.getInt("HASH_ALGORITHM".toLowerCase()));
                    final byte[] salt = PasswordHashHandler.getInstance().getSaltForHashAlgorithm(request.getInt("HASH_ALGORITHM".toLowerCase()));
                    accountRow.set("PASSWORD_HASH", (Object)passwordHash.getDigest(String.valueOf(request.get("password")), salt, PasswordHashHandler.getInstance().getHashIterations(request.getInt("HASH_ALGORITHM".toLowerCase()))));
                    accountRow.set("HASH_ALGORITHM", (Object)request.getInt("HASH_ALGORITHM".toLowerCase()));
                }
                else {
                    accountRow.set("PASSWORD_HASH", (Object)"--");
                    accountRow.set("HASH_ALGORITHM", (Object)(-1));
                }
                dataObject.addRow(accountRow);
                MDMUtil.getPersistence().add(dataObject);
                accountRow = dataObject.getRow("MdComputerAccount");
                accountID = (Long)accountRow.get("ACCOUNT_ID");
            }
            else {
                final Row accountRow = dataObject.getFirstRow("MdComputerAccount");
                if (request.has("ACCOUNT_NAME".toLowerCase())) {
                    accountRow.set("ACCOUNT_NAME", (Object)String.valueOf(request.get("ACCOUNT_NAME".toLowerCase())));
                }
                if (request.has("ACCOUNT_DESCRIPTION".toLowerCase())) {
                    accountRow.set("ACCOUNT_DESCRIPTION", (Object)String.valueOf(request.get("ACCOUNT_DESCRIPTION".toLowerCase())));
                }
                if (request.has("ACCOUNT_TYPE".toLowerCase())) {
                    accountRow.set("ACCOUNT_TYPE", (Object)request.getInt("ACCOUNT_TYPE".toLowerCase()));
                }
                if (request.has("SHORT_NAME".toLowerCase())) {
                    accountRow.set("SHORT_NAME", (Object)String.valueOf(request.get("SHORT_NAME".toLowerCase())).toLowerCase());
                }
                if (request.has("FULL_NAME".toLowerCase())) {
                    accountRow.set("FULL_NAME", (Object)String.valueOf(request.get("FULL_NAME".toLowerCase())));
                }
                int hashTechnique;
                if (request.has("HASH_ALGORITHM".toLowerCase())) {
                    hashTechnique = request.getInt("HASH_ALGORITHM".toLowerCase());
                    accountRow.set("HASH_ALGORITHM", (Object)hashTechnique);
                }
                else {
                    hashTechnique = (int)accountRow.get("HASH_ALGORITHM");
                }
                if (!MDMStringUtils.isEmpty(request.optString("password", (String)null))) {
                    final PasswordHash passwordHash2 = PasswordHashHandler.getInstance().getHashAlgorithm(hashTechnique);
                    final byte[] salt2 = PasswordHashHandler.getInstance().getSaltForHashAlgorithm(hashTechnique);
                    accountRow.set("PASSWORD_HASH", (Object)passwordHash2.getDigest(String.valueOf(request.get("password")), salt2, PasswordHashHandler.getInstance().getHashIterations(hashTechnique)));
                }
                dataObject.updateRow(accountRow);
                MDMUtil.getPersistence().update(dataObject);
            }
            return this.getAccountDetails(customerID, accountID);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in add-update account", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteComputerAccount(final Long customerID, final Long accountID) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getAccountDO(customerID, accountID, null);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdComputerAccount");
                dataObject.deleteRow(row);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in deleting the account", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
