package com.adventnet.sym.server.mdm.encryption;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMEncryptionSettingsHandler
{
    public static Logger logger;
    
    protected static JSONObject addOrUpdateEncryptionSetting(final JSONObject encryptionSettingsJSON) throws Exception {
        try {
            MDMEncryptionSettingsHandler.logger.log(Level.INFO, "FileVaultLog: Request to addOrUpdateEncryptionSetting{0}", encryptionSettingsJSON);
            final String settingsName = String.valueOf(encryptionSettingsJSON.get("SETTINGS_NAME"));
            final Long settingsID = encryptionSettingsJSON.optLong("ENCRYPTION_SETTINGS_ID", -1L);
            Row mdmSettingsRow = null;
            final DataObject addEncrypSettingsDo = (DataObject)new WritableDataObject();
            boolean isAdd = true;
            if (settingsID == -1L) {
                mdmSettingsRow = DBUtil.getRowFromDB("MDMEncryptionSettings", "SETTINGS_NAME", (Object)settingsName);
                if (mdmSettingsRow != null) {
                    throw new Exception("encryption_settings_name_already_exists");
                }
                mdmSettingsRow = new Row("MDMEncryptionSettings");
                mdmSettingsRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                mdmSettingsRow.set("ADDED_USER", (Object)encryptionSettingsJSON.getLong("ADDED_USER"));
                mdmSettingsRow.set("CUSTOMER_ID", (Object)encryptionSettingsJSON.getLong("CUSTOMER_ID"));
                mdmSettingsRow.set("SETTINGS_NAME", (Object)String.valueOf(encryptionSettingsJSON.get("SETTINGS_NAME")));
                mdmSettingsRow.set("ENCRYPTION_SETTINGS_TYPE", (Object)encryptionSettingsJSON.getInt("ENCRYPTION_SETTINGS_TYPE"));
            }
            else {
                mdmSettingsRow = DBUtil.getRowFromDB("MDMEncryptionSettings", "ENCRYPTION_SETTINGS_ID", (Object)settingsID);
                isAdd = false;
            }
            mdmSettingsRow.set("SETTINGS_DESC", (Object)encryptionSettingsJSON.optString("SETTINGS_DESC", ""));
            mdmSettingsRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
            Long LoggedOnUserID = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            if (LoggedOnUserID == null) {
                LoggedOnUserID = encryptionSettingsJSON.optLong("MODIFIED_USER");
            }
            mdmSettingsRow.set("MODIFIED_USER", (Object)LoggedOnUserID);
            if (isAdd) {
                addEncrypSettingsDo.addRow(mdmSettingsRow);
            }
            else {
                addEncrypSettingsDo.updateRow(mdmSettingsRow);
            }
            MDMUtil.getPersistence().update(addEncrypSettingsDo);
            MDMEncryptionSettingsHandler.logger.log(Level.INFO, "FileVaultLog: Data successfully added into MDMENCRYPTIONSETTINGS : {0}", mdmSettingsRow.getAsJSON());
            encryptionSettingsJSON.put("ENCRYPTION_SETTINGS_ID", mdmSettingsRow.get("ENCRYPTION_SETTINGS_ID"));
            return encryptionSettingsJSON;
        }
        catch (final Exception ex) {
            MDMEncryptionSettingsHandler.logger.log(Level.SEVERE, "FileVaultLog: Exception in addOrUpdateEncryptionSetting", ex);
            throw ex;
        }
    }
    
    public static JSONObject getEncrytionDetails(final Long mdmEncrptionSettingsID) throws DataAccessException, JSONException {
        MDMEncryptionSettingsHandler.logger.log(Level.INFO, "FileVaultLog: Response from  getEncrytionDetails() for encryptionID {0}", mdmEncrptionSettingsID);
        final Criteria cri = new Criteria(new Column("MDMEncryptionSettings", "ENCRYPTION_SETTINGS_ID"), (Object)mdmEncrptionSettingsID, 0);
        return getEncrytionDetails(cri);
    }
    
    public static JSONObject getEncrytionDetails(final String settingsName) throws DataAccessException, JSONException {
        MDMEncryptionSettingsHandler.logger.log(Level.INFO, "FileVaultLog: Response from  getEncrytionDetails() for settingsName {0}", settingsName);
        final Criteria cri = new Criteria(new Column("MDMEncryptionSettings", "SETTINGS_NAME"), (Object)settingsName, 0);
        return getEncrytionDetails(cri);
    }
    
    public static JSONObject getEncrytionDetails(final Criteria cri) throws DataAccessException, JSONException {
        MDMEncryptionSettingsHandler.logger.log(Level.INFO, "FileVaultLog: Inside getEncrytionDetails() for criteria {0}", cri);
        JSONObject encryptionDetailsJSON = null;
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMEncryptionSettings"));
        sq.addSelectColumn(new Column("MDMEncryptionSettings", "*"));
        if (cri != null) {
            sq.setCriteria(cri);
        }
        final DataObject resultDo = MDMUtil.getPersistence().get(sq);
        if (resultDo != null) {
            encryptionDetailsJSON = new JSONObject();
            final JSONArray settingsArr = new JSONArray();
            final Iterator ite = resultDo.getRows("MDMEncryptionSettings");
            while (ite.hasNext()) {
                final Row row = ite.next();
                settingsArr.put((Object)row.getAsJSON());
            }
            encryptionDetailsJSON.put("MDMEncryptionSettings", (Object)settingsArr);
        }
        MDMEncryptionSettingsHandler.logger.log(Level.INFO, "FileVaultLog: Response from  getEncrytionDetails() for criteria {0}", encryptionDetailsJSON);
        return encryptionDetailsJSON;
    }
    
    public static Long getEncryptionSettingsIDForConfigDataItem(final Long configDataItemID) {
        try {
            return (Long)DBUtil.getValueFromDB("MacFileVault2Policy", "CONFIG_DATA_ITEM_ID", (Object)configDataItemID, "ENCRYPTION_SETTINGS_ID");
        }
        catch (final Exception ex) {
            MDMEncryptionSettingsHandler.logger.log(Level.SEVERE, "FileVaultLog: Exception in getEncryptionSettingsIDForConfigDataItem", ex);
            return null;
        }
    }
    
    public static boolean deleteEncryptionSettings(final Long encryptionSettingId, final Long customerId) {
        boolean deleteSuccess = false;
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MDMEncryptionSettings");
            final Criteria settingIdCriteria = new Criteria(new Column("MDMEncryptionSettings", "ENCRYPTION_SETTINGS_ID"), (Object)encryptionSettingId, 0);
            final Criteria customerCriteria = new Criteria(new Column("MDMEncryptionSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            deleteQuery.setCriteria(settingIdCriteria.and(customerCriteria));
            MDMUtil.getPersistenceLite().delete(deleteQuery);
            deleteSuccess = true;
        }
        catch (final DataAccessException e) {
            MDMEncryptionSettingsHandler.logger.log(Level.SEVERE, "FileVaultLog: Exception in encryption settings delete", (Throwable)e);
            deleteSuccess = false;
        }
        return deleteSuccess;
    }
    
    public static boolean checkCustomerForEncryptionSetting(final Long encryptionSettingId, final Long customerId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMEncryptionSettings"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria settingIdCriteria = new Criteria(new Column("MDMEncryptionSettings", "ENCRYPTION_SETTINGS_ID"), (Object)encryptionSettingId, 0);
            final Criteria customerCriteria = new Criteria(new Column("MDMEncryptionSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(settingIdCriteria.and(customerCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final DataAccessException e) {
            MDMEncryptionSettingsHandler.logger.log(Level.SEVERE, "FileVaultLog: Exception in encryption setting check for customer", (Throwable)e);
        }
        return false;
    }
    
    static {
        MDMEncryptionSettingsHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
