package com.me.mdm.webclient.formbean;

import com.me.mdm.server.config.PayloadProperty;
import org.json.JSONException;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMSSOPolicyFormBean extends MDMDefaultFormBean
{
    public static Logger logger;
    private static final String KEY_ALLOWED_APPS = "ALLOWED_APPS";
    private static final String KEY_URL_DETAILS = "URL_DETAILS";
    private static final String KEY_URL = "URL";
    private static final String KEY_GROUP_DISPLAY_NAME = "GROUP_DISPLAY_NAME";
    private static final String KEY_IDENTIFIER = "IDENTIFIER";
    
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
                this.modifyDetails(dataObject, dynaForm, "SSOKerberosAccount");
                final Long certificateId = dynaForm.optLong("CLIENT_CERT_ID");
                if (certificateId != null && certificateId != -1L) {
                    this.modifyDetails(dataObject, dynaForm, "SSOToCertificateRel");
                }
                else {
                    dataObject.deleteRows("SSOToCertificateRel", (Criteria)null);
                }
                final String jsonString = String.valueOf(dynaForm.get("URL_DETAILS"));
                final JSONArray jsonObj = MDMStringUtils.isEmpty(jsonString) ? null : new JSONArray(jsonString);
                this.modifyURLDetails(jsonObj, dataObject);
                final String appJsonString = String.valueOf(dynaForm.get("ALLOWED_APPS"));
                final JSONArray appJsonObj = MDMStringUtils.isEmpty(appJsonString) ? null : new JSONArray(appJsonString);
                this.modifyAppDetails(appJsonObj, dataObject);
            }
        }
        catch (final Exception e) {
            MDMSSOPolicyFormBean.logger.log(Level.SEVERE, "Exception in SSO Payload", e);
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Object configDataItemId = configDOFromDB.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        final Object clonedConfigDataItemId = cloneConfigDO.getValue("SSOAccountPolicy", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        MDMSSOPolicyFormBean.logger.log(Level.INFO, " configDOFromDB {0}", configDOFromDB);
        final Iterator iterator = configDOFromDB.getRows("ManagedWebDomainURLDetails", new Criteria(new Column("SSODomains", "CONFIG_DATA_ITEM_ID"), configDataItemId, 0));
        final List<Long> urlidList = new ArrayList<Long>();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long urlId = (Long)row.get("URL_DETAILS_ID");
            urlidList.add(urlId);
        }
        final Long[] urlArray = new Long[urlidList.size()];
        urlidList.toArray(urlArray);
        final Iterator it = configDOFromDB.getRows("ManagedWebDomainURLDetails", new Criteria(new Column("ManagedWebDomainURLDetails", "URL_DETAILS_ID"), (Object)urlArray, 8));
        while (it.hasNext()) {
            final Row urlDetailsRow = it.next();
            final Row clonedRow = new Row("ManagedWebDomainURLDetails");
            final Object newPolicyId = this.cloneRow(urlDetailsRow, clonedRow, "URL_DETAILS_ID");
            final Row ssoRow = new Row("SSODomains");
            ssoRow.set("URL_DETAILS_ID", newPolicyId);
            ssoRow.set("CONFIG_DATA_ITEM_ID", clonedConfigDataItemId);
            cloneConfigDO.addRow(clonedRow);
            cloneConfigDO.addRow(ssoRow);
        }
        MDMSSOPolicyFormBean.logger.log(Level.INFO, " cloneConfigDO {0}", cloneConfigDO);
    }
    
    private void deleteExistingURLDetails(final DataObject dataObject, final Object configDataItem) {
        try {
            final Object[] urlarray = this.getSpecificColumnValue(dataObject, "SSODomains", "URL_DETAILS_ID", configDataItem);
            dataObject.deleteRows("ManagedWebDomainURLDetails", new Criteria(new Column("ManagedWebDomainURLDetails", "URL_DETAILS_ID"), (Object)urlarray, 8));
            dataObject.deleteRows("SSODomains", (Criteria)null);
        }
        catch (final Exception e) {
            MDMSSOPolicyFormBean.logger.log(Level.WARNING, "Exception occured in deleting Existing URL in SSO", e);
        }
    }
    
    private void modifyURLDetails(final JSONArray jsonArray, final DataObject dataObject) throws JSONException, DataAccessException {
        MDMSSOPolicyFormBean.logger.log(Level.INFO, "SSO URL JSONArry :", jsonArray);
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configId instanceof UniqueValueHolder)) {
            this.deleteExistingURLDetails(dataObject, configId);
        }
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject json = (JSONObject)jsonArray.get(i);
            final String url = (String)json.get("URL");
            if (!MDMStringUtils.isEmpty(url)) {
                final Row urlrow = new Row("ManagedWebDomainURLDetails");
                urlrow.set("URL", (Object)url);
                final Row restrictionRow = new Row("SSODomains");
                restrictionRow.set("URL_DETAILS_ID", urlrow.get("URL_DETAILS_ID"));
                restrictionRow.set("CONFIG_DATA_ITEM_ID", configId);
                dataObject.addRow(urlrow);
                dataObject.addRow(restrictionRow);
            }
        }
    }
    
    private void deleteExistingAppDetails(final DataObject dataObject) throws DataAccessException {
        dataObject.deleteRows("SSOApps", (Criteria)null);
    }
    
    private void modifyAppDetails(final JSONArray appJsonArray, final DataObject dataObject) throws DataAccessException, JSONException {
        MDMSSOPolicyFormBean.logger.log(Level.INFO, "SSO App Details JSONArray :", appJsonArray);
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configId instanceof UniqueValueHolder)) {
            this.deleteExistingAppDetails(dataObject);
        }
        if (appJsonArray != null) {
            for (int i = 0; i < appJsonArray.length(); ++i) {
                final JSONObject appJson = new JSONObject(appJsonArray.get(i).toString());
                final String identifier = appJson.optString("IDENTIFIER");
                final String name = appJson.optString("GROUP_DISPLAY_NAME");
                dataObject.addRow(this.createAppRow(configId, identifier, name));
            }
        }
    }
    
    private Row createAppRow(final Object configId, final String identifier, final String name) {
        final Row appRow = new Row("SSOApps");
        appRow.set("CONFIG_DATA_ITEM_ID", configId);
        appRow.set("APP_IDENTIFIER", (Object)identifier);
        appRow.set("APP_NAME", (Object)name);
        return appRow;
    }
    
    private String getAppDetails(final DataObject dataObject) throws Exception {
        final JSONArray appArray = new JSONArray();
        final Iterator iterator = dataObject.getRows("SSOApps");
        while (iterator.hasNext()) {
            final JSONObject appDetails = new JSONObject();
            final Row row = iterator.next();
            appDetails.put("IDENTIFIER", (Object)row.get("APP_IDENTIFIER"));
            appDetails.put("GROUP_DISPLAY_NAME", (Object)row.get("APP_NAME"));
            appArray.put((Object)appDetails);
        }
        return appArray.toString();
    }
    
    private String getURLDetails(final DataObject dataObject) throws Exception {
        final JSONArray urlDetailsArray = new JSONArray();
        final Iterator it = dataObject.getRows("ManagedWebDomainURLDetails");
        while (it.hasNext()) {
            final JSONObject urlDetails = new JSONObject();
            final Row urlDetailsRow = it.next();
            urlDetails.put("URL", urlDetailsRow.get("URL"));
            urlDetailsArray.put((Object)urlDetails);
        }
        return urlDetailsArray.toString();
    }
    
    private void modifyDetails(final DataObject dataObject, final JSONObject dynaForm, final String tableName) throws Exception {
        boolean isAdded = false;
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        final Long configDataItemID = dynaForm.optLong("CONFIG_DATA_ITEM_ID");
        Row payloadRow = null;
        if (configDataItemID == null || configDataItemID <= 0L || !dataObject.containsTable(tableName)) {
            payloadRow = new Row(tableName);
            payloadRow.set("CONFIG_DATA_ITEM_ID", configId);
            isAdded = true;
        }
        else {
            final Criteria criteria = new Criteria(Column.getColumn(tableName, "CONFIG_DATA_ITEM_ID"), configId, 0);
            payloadRow = dataObject.getRow(tableName, criteria);
        }
        final PayloadProperty payloadProperty = new PayloadProperty();
        final List columns = payloadRow.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            payloadProperty.name = columns.get(i);
            if (!payloadProperty.name.equals("CONFIG_DATA_ITEM_ID")) {
                payloadProperty.value = dynaForm.get(payloadProperty.name);
                payloadRow.set(payloadProperty.name, payloadProperty.value);
            }
        }
        if (isAdded) {
            dataObject.addRow(payloadRow);
        }
        else {
            dataObject.updateRow(payloadRow);
        }
    }
    
    static {
        MDMSSOPolicyFormBean.logger = Logger.getLogger("MDMConfigLogger");
    }
}
