package com.adventnet.sym.webclient.mdm.config;

import java.util.Arrays;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.security.passcode.MDMManagedPasswordHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.internal.UniqueValueHolder;
import java.util.List;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.common.customdata.CustomDataHandler;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.server.profiles.config.WebContentConfigHandler;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class WebContentFilterPolicyFormBean extends MDMDefaultFormBean
{
    public static Logger logger;
    
    @Override
    public DataObject getDataObject(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        this.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        return dataObject;
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            super.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
            for (final JSONObject dynaForm : dynaActionForm) {
                if (dynaForm.get("TABLE_NAME").equals("AndroidEFRPPolicy")) {
                    final String jsonString = String.valueOf(dynaForm.get("EFRP_DETAILS"));
                    final JSONArray jsonObj = new JSONArray(jsonString);
                    this.modifyEFRPAccDetails(jsonObj, dataObject, this.getSubTableName((int)dynaForm.get("CONFIG_ID")));
                }
                else {
                    final int filterType = dynaForm.optInt("FILTER_TYPE", (int)WebContentConfigHandler.BUILTIN_WCF);
                    if (WebContentConfigHandler.BUILTIN_WCF.equals(filterType)) {
                        final String jsonString2 = String.valueOf(dynaForm.get("URL_DETAILS"));
                        final JSONArray jsonObj2 = new JSONArray(jsonString2);
                        if (!dynaForm.has("is_url_change") || String.valueOf(dynaForm.get("is_url_change")) != "false") {
                            this.modifyURLDetails(jsonObj2, dataObject, this.getSubTableName((int)dynaForm.get("CONFIG_ID")));
                        }
                        if (dynaForm.has("PERMITTED_URLS")) {
                            final JSONArray permittedURL = dynaForm.getJSONArray("PERMITTED_URLS");
                            final JSONArray permilledURLDetailsArray = new JSONArray();
                            for (int i = 0; i < permittedURL.length(); ++i) {
                                final JSONObject permittedURLJSON = new JSONObject();
                                permittedURLJSON.put("URL", permittedURL.get(i));
                                permilledURLDetailsArray.put((Object)permittedURLJSON);
                            }
                            this.modifyURLDetails(permilledURLDetailsArray, dataObject, "AppleWCFPermittedURL");
                        }
                    }
                    else {
                        this.addOrModifyAppleWCF(dynaForm, dataObject);
                        final Object policyID = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
                        if (dynaForm.has("CUSTOM_DATA")) {
                            new CustomDataHandler().addCustomData(dynaForm.getJSONArray("CUSTOM_DATA"), policyID, dataObject);
                        }
                        else {
                            new CustomDataHandler().deleteCustomData(dataObject, policyID);
                        }
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    private void deleteExistingURLDetails(final DataObject dataObject, final String subtableName) {
        try {
            final Iterator<Row> iterator = dataObject.getRows(subtableName);
            final List list = MDMDBUtil.getColumnValuesAsList((Iterator)iterator, "URL_DETAILS_ID");
            final Criteria criteria = new Criteria(Column.getColumn("URLDetails", "URL_DETAILS_ID"), (Object)list.toArray(), 8);
            dataObject.deleteRows("URLDetails", criteria);
        }
        catch (final Exception e) {
            WebContentFilterPolicyFormBean.logger.log(Level.WARNING, "Exception occured in deleteExistingWebContentFilter OnDemandRules....", e);
        }
    }
    
    private void deleteExistingEFRPDetails(final DataObject dataObject, final String subtableName) {
        try {
            dataObject.deleteRows("EFRPAccDetails", (Criteria)null);
            dataObject.deleteRows(subtableName, (Criteria)null);
        }
        catch (final Exception e) {
            WebContentFilterPolicyFormBean.logger.log(Level.WARNING, "Exception occured in deleteExistingEFRPRules....", e);
        }
    }
    
    private void modifyEFRPAccDetails(final JSONArray jsonArray, final DataObject dataObject, final String subtableName) throws JSONException, DataAccessException {
        WebContentFilterPolicyFormBean.logger.log(Level.INFO, "jsonObj {0}", jsonArray);
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configId instanceof UniqueValueHolder)) {
            this.deleteExistingEFRPDetails(dataObject, subtableName);
        }
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject json = (JSONObject)jsonArray.get(i);
            final String userId = (String)json.get("EMAIL_USER_ID");
            if (userId != "") {
                final Row efrpAccRow = new Row("EFRPAccDetails");
                efrpAccRow.set("EMAIL_USER_ID", (Object)userId);
                efrpAccRow.set("EMAIL_ID", (Object)json.optString("EMAIL_ID", "--"));
                final Row policyRow = new Row(subtableName);
                policyRow.set("EFRP_ACC_ID", efrpAccRow.get("EFRP_ACC_ID"));
                policyRow.set("CONFIG_DATA_ITEM_ID", configId);
                dataObject.addRow(efrpAccRow);
                dataObject.addRow(policyRow);
            }
        }
    }
    
    private void addOrModifyAppleWCF(final JSONObject jsonObject, final DataObject dataObject) throws Exception {
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        final Long appleWCFPasswordID = MDMManagedPasswordHandler.getMDMManagedPasswordID(jsonObject.optString("password"), customerId, userID);
        MDMDBUtil.updateRow(dataObject, "AppleWCFConfig", new Object[][] { { "CONFIG_DATA_ITEM_ID", configId }, { "FILTER_BROWSER", jsonObject.getBoolean("FILTER_BROWSER") }, { "FILTER_SOCKET", jsonObject.getBoolean("FILTER_SOCKET") }, { "ORGANIZATION", jsonObject.optString("ORGANIZATION") }, { "PAYLOAD_CERTIFICATE_UUID", jsonObject.optString("PAYLOAD_CERTIFICATE_UUID") }, { "PLUGIN_BUNDLE_ID", jsonObject.optString("PLUGIN_BUNDLE_ID") }, { "SERVER_ADDRESS", jsonObject.optString("SERVER_ADDRESS") }, { "USER_DEF_NAME", jsonObject.optString("USER_DEF_NAME") }, { "USER_NAME", jsonObject.optString("USER_NAME") }, { "CERTIFICATE_ID", jsonObject.has("CERTIFICATE_ID") ? Long.valueOf(jsonObject.getLong("CERTIFICATE_ID")) : null }, { "PASSWORD", "" }, { "PASSWORD_ID", appleWCFPasswordID } });
        if (jsonObject.has("FILTER_PACKET") || jsonObject.has("FILTER_DATA_BUNDLE_ID")) {
            MDMDBUtil.updateRow(dataObject, "MacWCFKext", new Object[][] { { "CONFIG_DATA_ITEM_ID", configId }, { "FILTER_DATA_BUNDLE_ID", jsonObject.optString("FILTER_DATA_BUNDLE_ID") }, { "FILTER_DATA_CODE_REQ", MDMStringUtils.getDecodedString(jsonObject.optString("FILTER_DATA_CODE_REQ")) }, { "FILTER_GRADE", jsonObject.optInt("FILTER_GRADE") }, { "FILTER_PACKET", jsonObject.optBoolean("FILTER_PACKET") }, { "FILTER_PACKET_BUNDLE_ID", jsonObject.optString("FILTER_PACKET_BUNDLE_ID") }, { "FILTER_PACKET_CODE_REQ", MDMStringUtils.getDecodedString(jsonObject.optString("FILTER_PACKET_CODE_REQ")) } });
        }
    }
    
    private void modifyURLDetails(final JSONArray jsonArray, final DataObject dataObject, final String subtableName) throws JSONException, DataAccessException {
        WebContentFilterPolicyFormBean.logger.log(Level.INFO, "jsonObj {0}", jsonArray);
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configId instanceof UniqueValueHolder)) {
            this.deleteExistingURLDetails(dataObject, subtableName);
        }
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject json = (JSONObject)jsonArray.get(i);
            final String url = (String)json.get("URL");
            if (url != "") {
                final Row urlrow = new Row("URLDetails");
                urlrow.set("URL", (Object)url);
                urlrow.set("BOOKMARK_TITILE", (Object)json.optString("BOOKMARK_TITILE", "--"));
                urlrow.set("BOOKMARK_PATH", (Object)json.optString("BOOKMARK_PATH", "--"));
                final Row restrictionRow = new Row(subtableName);
                restrictionRow.set("URL_DETAILS_ID", urlrow.get("URL_DETAILS_ID"));
                restrictionRow.set("CONFIG_DATA_ITEM_ID", configId);
                dataObject.addRow(urlrow);
                dataObject.addRow(restrictionRow);
            }
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Object configDataItemId = cloneConfigDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        final Object oldConfigDataItemId = configDOFromDB.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        WebContentFilterPolicyFormBean.logger.log(Level.INFO, " configDOFromDB {0}", configDOFromDB);
        if (configID == 565) {
            final Iterator it = configDOFromDB.getRows("EFRPAccDetails");
            while (it.hasNext()) {
                final Row efrpAccRow = it.next();
                final Row clonedRow = new Row("EFRPAccDetails");
                final Object newPolicyId = this.cloneRow(efrpAccRow, clonedRow, "EFRP_ACC_ID");
                final Row policyRow = new Row(this.getSubTableName(configID));
                policyRow.set("EFRP_ACC_ID", newPolicyId);
                policyRow.set("CONFIG_DATA_ITEM_ID", configDataItemId);
                cloneConfigDO.addRow(clonedRow);
                cloneConfigDO.addRow(policyRow);
            }
        }
        else if (configID == 188) {
            final Iterator it = configDOFromDB.getRows("URLDetails");
            while (it.hasNext()) {
                final Row urlDetailsRow = it.next();
                final Row clonedRow = new Row("URLDetails");
                final Object newPolicyId = this.cloneRow(urlDetailsRow, clonedRow, "URL_DETAILS_ID");
                final Boolean isPermittedURLDetails = configDOFromDB.getRow("AppleWCFPermittedURL", new Criteria(Column.getColumn("AppleWCFPermittedURL", "URL_DETAILS_ID"), urlDetailsRow.get("URL_DETAILS_ID"), 0)) != null;
                final Row restrictionRow = isPermittedURLDetails ? new Row("AppleWCFPermittedURL") : new Row("URLRestrictionDetails");
                restrictionRow.set("URL_DETAILS_ID", newPolicyId);
                restrictionRow.set("CONFIG_DATA_ITEM_ID", configDataItemId);
                cloneConfigDO.addRow(clonedRow);
                cloneConfigDO.addRow(restrictionRow);
            }
            this.cloneAppleSpecificRows(configDOFromDB, cloneConfigDO, configDataItemId);
            new CustomDataHandler();
            if (CustomDataHandler.hasCustomConfigData(configDOFromDB)) {
                final JSONArray customData = new JSONArray((Collection)new CustomDataHandler().getCustomData((Long)oldConfigDataItemId, configDOFromDB));
                new CustomDataHandler().addCustomData(customData, configDataItemId, cloneConfigDO);
            }
        }
        else if (configID == 758) {
            this.cloneAppleSpecificRows(configDOFromDB, cloneConfigDO, configDataItemId);
            new CustomDataHandler();
            if (CustomDataHandler.hasCustomConfigData(configDOFromDB)) {
                final JSONArray customData2 = new JSONArray((Collection)new CustomDataHandler().getCustomData((Long)oldConfigDataItemId, configDOFromDB));
                new CustomDataHandler().addCustomData(customData2, configDataItemId, cloneConfigDO);
            }
        }
        else {
            final Iterator it = configDOFromDB.getRows("URLDetails");
            while (it.hasNext()) {
                final Row urlDetailsRow = it.next();
                final Row clonedRow = new Row("URLDetails");
                final Object newPolicyId = this.cloneRow(urlDetailsRow, clonedRow, "URL_DETAILS_ID");
                final Row restrictionRow2 = new Row(this.getSubTableName(configID));
                restrictionRow2.set("URL_DETAILS_ID", newPolicyId);
                restrictionRow2.set("CONFIG_DATA_ITEM_ID", configDataItemId);
                cloneConfigDO.addRow(clonedRow);
                cloneConfigDO.addRow(restrictionRow2);
            }
        }
        WebContentFilterPolicyFormBean.logger.log(Level.INFO, " cloneConfigDO {0}", cloneConfigDO);
    }
    
    private void cloneAppleSpecificRows(final DataObject dataObject, final DataObject cloneDO, final Object configDataItemID) throws DataAccessException {
        final List<String> cloneTables = Arrays.asList("AppleWCFConfig", "MacWCFKext");
        for (final String table : cloneTables) {
            if (dataObject.containsTable(table)) {
                final Iterator<Row> iterator = dataObject.getRows(table);
                while (iterator.hasNext()) {
                    final Row oldRow = iterator.next();
                    final Row newRow = MDMDBUtil.cloneRow(oldRow, new String[] { "CONFIG_DATA_ITEM_ID" });
                    newRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
                    cloneDO.addRow(newRow);
                }
            }
        }
    }
    
    private String getSubTableName(final int configId) {
        String subTableName = "URLRestrictionDetails";
        if (configId == 707 || configId == 709 || configId == 713) {
            subTableName = "CfgDataItemToUrl";
        }
        else if (configId == 565) {
            subTableName = "AndroidEFRPPolicy";
        }
        return subTableName;
    }
    
    static {
        WebContentFilterPolicyFormBean.logger = Logger.getLogger("MDMConfigLogger");
    }
}
