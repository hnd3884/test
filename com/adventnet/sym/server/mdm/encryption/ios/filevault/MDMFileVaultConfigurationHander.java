package com.adventnet.sym.server.mdm.encryption.ios.filevault;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import org.json.JSONArray;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.encryption.MDMPKCS12CertificateHandler;
import com.adventnet.persistence.DataObject;
import javax.transaction.SystemException;
import javax.transaction.NotSupportedException;
import org.json.JSONException;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.encryption.MDMEncryptionSettingsHandler;

public class MDMFileVaultConfigurationHander extends MDMEncryptionSettingsHandler
{
    public static Logger logger;
    
    public static JSONObject addOrUpdateMDMFileVaultSettings(final JSONObject fileVaultSettings) {
        final JSONObject responseObject = new JSONObject();
        try {
            try {
                MDMUtil.getUserTransaction().begin();
                MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: DB TransAction begins to insert values into MDMENCRYPTIONSETTINGS,MDMFILEVAULTPERSONALKEYCONFIGURATION,MDMFILEVAULTINSTITUTIONCONFIGURATION ");
                try {
                    fileVaultSettings.put("ENCRYPTION_SETTINGS_TYPE", 1);
                    MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Going to add Entry in MDMEncryptionSettings... ");
                    final JSONObject encrytipnJSON = MDMEncryptionSettingsHandler.addOrUpdateEncryptionSetting(fileVaultSettings);
                    MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data Added in MDMEncryptionSettings... ");
                    if (encrytipnJSON != null) {
                        final Long encrypID = fileVaultSettings.getLong("ENCRYPTION_SETTINGS_ID");
                        Row fileVaultRow = DBUtil.getRowFromDB("MDMFileVaultSettings", "ENCRYPTION_SETTINGS_ID", (Object)encrypID);
                        final DataObject encryptDO = (DataObject)new WritableDataObject();
                        boolean isNew = false;
                        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Going to add/update Entry in MDMFileFaultSettings... ");
                        if (fileVaultRow == null) {
                            fileVaultRow = new Row("MDMFileVaultSettings");
                            isNew = true;
                            fileVaultRow.set("ENCRYPTION_SETTINGS_ID", (Object)encrypID);
                            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Going to add new row in MDMFileVaultSettings with encryptionID{0}", encrypID);
                        }
                        fileVaultRow.set("ASK_ENABLE_DURING_LOGOUT", (Object)fileVaultSettings.optBoolean("ASK_ENABLE_DURING_LOGOUT", false));
                        fileVaultRow.set("COPY_RECOVERY_KEY_TO_MDM", (Object)fileVaultSettings.optBoolean("COPY_RECOVERY_KEY_TO_MDM", true));
                        fileVaultRow.set("FORCE_ENCRYPTION_UNTIL_LOGOUT", (Object)fileVaultSettings.optBoolean("FORCE_ENCRYPTION_UNTIL_LOGOUT", false));
                        fileVaultRow.set("MAXIMUM_ATTEMPTS_TO_FORCE", (Object)fileVaultSettings.optInt("MAXIMUM_ATTEMPTS_TO_FORCE", -1));
                        fileVaultRow.set("MESSAGE_TO_USER", fileVaultSettings.get("MESSAGE_TO_USER"));
                        fileVaultRow.set("RECOVERY_KEY_TYPE", fileVaultSettings.get("RECOVERY_KEY_TYPE"));
                        if (isNew) {
                            encryptDO.addRow(fileVaultRow);
                            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data added in MDMFileVaultSettings{0}", fileVaultRow.getAsJSON());
                        }
                        else {
                            encryptDO.updateBlindly(fileVaultRow);
                            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data Updated in MDMFileVaultSettings{0}", fileVaultRow.getAsJSON());
                        }
                        MDMUtil.getPersistence().update(encryptDO);
                        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data successfully added/modified in MDMFilevaultSettings...");
                        if (fileVaultSettings.getInt("RECOVERY_KEY_TYPE") == 1 || fileVaultSettings.getInt("RECOVERY_KEY_TYPE") == 3) {
                            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: MDMFilevaultSettings requires data to be added in child table MDMFILEVAULTPERSONALKEYCONFIGURATION as Recovery mode is set to Personal/Combined..");
                            addOrUpdatePersonalKeySettings(encrytipnJSON);
                        }
                        if (fileVaultSettings.getInt("RECOVERY_KEY_TYPE") == 2 || fileVaultSettings.getInt("RECOVERY_KEY_TYPE") == 3) {
                            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: MDMFilevaultSettings requires data to be added in child table MDMFILEVAULTINSTITUTIONCONFIGURATION as Recovery mode is set to Institutional/Combined..");
                            addOrUpdateInstitutionalRecoveryKeySettings(encrytipnJSON);
                        }
                    }
                    MDMUtil.getUserTransaction().commit();
                    MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data added in All Tables related to FileVault configurations , going to Commit transaction....");
                }
                catch (final Exception ex) {
                    try {
                        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog:Exception while adding FileVault configuration...Going to rollback transaction..");
                        MDMUtil.getUserTransaction().rollback();
                        responseObject.put("Status", (Object)"Error");
                        responseObject.put("ErrorMessage", (Object)ex.getMessage());
                        if (ex.getMessage() != null && ex.getMessage().contains("encryption_settings_name_already_exists")) {
                            responseObject.put("ErrorDescription", (Object)I18N.getMsg("mdm.profile.fv.encryption_name_already_exisits", new Object[0]));
                        }
                        MDMFileVaultConfigurationHander.logger.log(Level.SEVERE, "FileVaultLog:Exception in addOrUpdateMDMFileVaultSettings() ", ex);
                        return responseObject;
                    }
                    catch (final JSONException ex2) {
                        MDMFileVaultConfigurationHander.logger.log(Level.SEVERE, "FileVaultLog:Exception in addOrUpdateMDMFileVaultSettings() ", (Throwable)ex2);
                    }
                    catch (final Exception ex3) {
                        MDMFileVaultConfigurationHander.logger.log(Level.SEVERE, "FileVaultLog:Exception in addOrUpdateMDMFileVaultSettings() ", ex3);
                    }
                }
            }
            catch (final NotSupportedException ex4) {
                MDMFileVaultConfigurationHander.logger.log(Level.SEVERE, "FileVaultLog:Exception in addOrUpdateMDMFileVaultSettings() ", (Throwable)ex4);
            }
            catch (final SystemException ex5) {
                MDMFileVaultConfigurationHander.logger.log(Level.SEVERE, "FileVaultLog:Exception in addOrUpdateMDMFileVaultSettings() ", (Throwable)ex5);
            }
            responseObject.put("Status", (Object)"Acknowledged");
            responseObject.put("ResponseData", (Object)fileVaultSettings);
        }
        catch (final JSONException ex6) {
            MDMFileVaultConfigurationHander.logger.log(Level.SEVERE, "FileVaultLog:Exception in addOrUpdateMDMFileVaultSettings() ", (Throwable)ex6);
        }
        return responseObject;
    }
    
    private static JSONObject addOrUpdatePersonalKeySettings(final JSONObject obj) throws JSONException, Exception {
        final Long encrypID = obj.getLong("ENCRYPTION_SETTINGS_ID");
        final Long customerID = obj.getLong("CUSTOMER_ID");
        Long certificateID = null;
        Row personalKeyRow = DBUtil.getRowFromDB("MDMFileVaultPersonalKeyConfiguration", "ENCRYPTION_SETTINGS_ID", (Object)encrypID);
        final DataObject personalKeyDO = (DataObject)new WritableDataObject();
        boolean isAdd = false;
        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Going to add/modify data in MDMFILEVAULTPERSONALKEYCONFIGURATION ...");
        if (personalKeyRow == null) {
            isAdd = true;
            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Going to create new Identitiy Certificate to read PersonalKeyCMS blob...");
            certificateID = MDMPKCS12CertificateHandler.getInstance().addNewFileVaultPersonalRecoveryKeyToCertificates(customerID);
            personalKeyRow = new Row("MDMFileVaultPersonalKeyConfiguration");
            obj.put("RECOVERY_ENCRYPT_CERT_ID", (Object)certificateID);
            personalKeyRow.set("RECOVERY_ENCRYPT_CERT_ID", (Object)certificateID);
            personalKeyRow.set("ENCRYPTION_SETTINGS_ID", (Object)encrypID);
            personalKeyRow.set("RECOVERY_KEY_IDENTIFIER", (Object)obj.optString("RECOVERY_KEY_IDENTIFIER", String.valueOf(certificateID)));
        }
        personalKeyRow.set("SHOW_RECOVERY_KEY", obj.get("SHOW_RECOVERY_KEY"));
        if (isAdd) {
            personalKeyDO.addRow(personalKeyRow);
            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data added in MDMFILEVAULTPERSONALKEYCONFIGURATION{0}", personalKeyRow.getAsJSON());
        }
        else {
            personalKeyDO.updateBlindly(personalKeyRow);
            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data updated in MDMFILEVAULTPERSONALKEYCONFIGURATION{0}", personalKeyRow.getAsJSON());
        }
        MDMUtil.getPersistence().update(personalKeyDO);
        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data sucessfully added/modified in MDMFILEVAULTPERSONALKEYCONFIGURATION");
        return obj;
    }
    
    private static JSONObject addOrUpdateInstitutionalRecoveryKeySettings(final JSONObject obj) throws JSONException, Exception {
        final Long encrypID = obj.getLong("ENCRYPTION_SETTINGS_ID");
        Row institutionRRow = DBUtil.getRowFromDB("MDMFileVaultInstitutionConfiguration", "ENCRYPTION_SETTINGS_ID", (Object)encrypID);
        final DataObject institutionalRecoveryDO = (DataObject)new WritableDataObject();
        boolean isAdd = false;
        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Going to add/modify data in MDMFILEVAULTINSTITUTIONCONFIGURATION ...");
        if (institutionRRow == null) {
            isAdd = true;
            institutionRRow = new Row("MDMFileVaultInstitutionConfiguration");
            institutionRRow.set("ENCRYPTION_SETTINGS_ID", (Object)encrypID);
        }
        institutionRRow.set("INSTITUTION_ENCRYPTION_CERT", obj.get("INSTITUTION_ENCRYPTION_CERT"));
        if (isAdd) {
            institutionalRecoveryDO.addRow(institutionRRow);
            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data added in MDMFILEVAULTINSTITUTIONCONFIGURATION{0}", institutionRRow.getAsJSON());
        }
        else {
            institutionalRecoveryDO.updateBlindly(institutionRRow);
            MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data updated in MDMFILEVAULTINSTITUTIONCONFIGURATION{0}", institutionRRow.getAsJSON());
        }
        MDMUtil.getPersistence().update(institutionalRecoveryDO);
        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Data sucessfully added/modified in MDMFILEVAULTINSTITUTIONCONFIGURATION");
        return obj;
    }
    
    public static JSONObject getFileVaultDetails(final Long mdmEncrptionSettingsID, final Criteria criteria) throws DataAccessException, JSONException {
        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Inside getFileVaultDetails() for mdmEncryptionsID {0}", mdmEncrptionSettingsID);
        Criteria cri = new Criteria(new Column("MDMEncryptionSettings", "ENCRYPTION_SETTINGS_ID"), (Object)mdmEncrptionSettingsID, 0);
        if (criteria != null) {
            cri = cri.and(criteria);
        }
        return getFileVaultDetails(cri);
    }
    
    public static JSONObject getFileVaultDetails(final String settingsName) throws DataAccessException, JSONException {
        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Inside getFileVaultDetails() for mdmencryptinosettingsname {0}", settingsName);
        final Criteria cri = new Criteria(new Column("MDMEncryptionSettings", "SETTINGS_NAME"), (Object)settingsName, 0);
        return getFileVaultDetails(cri);
    }
    
    private static JSONObject getFileVaultDetails(final Criteria cri) throws DataAccessException, JSONException {
        JSONObject encryptionDetailsJSON = null;
        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Inside getFileVaultDetails() for criteria {0}", cri);
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMEncryptionSettings"));
        sq.addJoin(new Join("MDMEncryptionSettings", "MDMFileVaultSettings", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 2));
        sq.addJoin(new Join("MDMFileVaultSettings", "MDMFileVaultPersonalKeyConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 1));
        sq.addJoin(new Join("MDMFileVaultSettings", "MDMFileVaultInstitutionConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 1));
        sq.addSelectColumn(new Column((String)null, "*"));
        if (cri != null) {
            sq.setCriteria(cri);
        }
        final DataObject resultDo = MDMUtil.getPersistence().get(sq);
        if (resultDo != null) {
            encryptionDetailsJSON = new JSONObject();
            final JSONArray settingsArr = new JSONArray();
            final Iterator ite = resultDo.getRows("MDMEncryptionSettings");
            while (ite.hasNext()) {
                final Row encrytionRow = ite.next();
                final JSONObject rowJSON = new JSONObject();
                rowJSON.put("MDMEncryptionSettings", (Object)MDMDBUtil.rowToJSON(encrytionRow));
                final Long settingsID = (Long)encrytionRow.get("ENCRYPTION_SETTINGS_ID");
                final Criteria pkFVCri = new Criteria(new Column("MDMFileVaultSettings", "ENCRYPTION_SETTINGS_ID"), (Object)settingsID, 0);
                final Row fileVaultRow = resultDo.getRow("MDMFileVaultSettings", pkFVCri);
                if (fileVaultRow != null) {
                    rowJSON.put("MDMFileVaultSettings", (Object)MDMDBUtil.rowToJSON(fileVaultRow));
                }
                final Criteria pkFVPCri = new Criteria(new Column("MDMFileVaultPersonalKeyConfiguration", "ENCRYPTION_SETTINGS_ID"), (Object)settingsID, 0);
                final Row fileVaultPersonalRow = resultDo.getRow("MDMFileVaultPersonalKeyConfiguration", pkFVPCri);
                if (fileVaultPersonalRow != null) {
                    rowJSON.put("MDMFileVaultPersonalKeyConfiguration", (Object)MDMDBUtil.rowToJSON(fileVaultPersonalRow));
                }
                final Criteria pkFVICri = new Criteria(new Column("MDMFileVaultInstitutionConfiguration", "ENCRYPTION_SETTINGS_ID"), (Object)settingsID, 0);
                final Row fileVaultInstRow = resultDo.getRow("MDMFileVaultInstitutionConfiguration", pkFVICri);
                if (fileVaultInstRow != null) {
                    rowJSON.put("MDMFileVaultInstitutionConfiguration", (Object)MDMDBUtil.rowToJSON(fileVaultInstRow));
                }
                settingsArr.put((Object)rowJSON);
            }
            encryptionDetailsJSON.put("ResponseData", (Object)settingsArr);
        }
        MDMFileVaultConfigurationHander.logger.log(Level.INFO, "FileVaultLog: Response getFileVaultDetails() :  {0}", encryptionDetailsJSON);
        return encryptionDetailsJSON;
    }
    
    static {
        MDMFileVaultConfigurationHander.logger = Logger.getLogger("MDMConfigLogger");
    }
}
