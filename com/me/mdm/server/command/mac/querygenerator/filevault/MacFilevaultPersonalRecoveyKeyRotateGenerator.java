package com.me.mdm.server.command.mac.querygenerator.filevault;

import com.adventnet.persistence.Row;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import java.io.File;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.security.cert.X509Certificate;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.dd.plist.NSDictionary;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFilevaultUtils;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class MacFilevaultPersonalRecoveyKeyRotateGenerator implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.FINE, "Entering MacFilevaultPersonalRecoveyKeyRotateGenerator resourceID[{0}],UDID[{1}]", new Object[] { resourceID, strUDID });
        final String commandPayload = this.createFilevaultPersonalKeyRotate(resourceID, strUDID);
        MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.FINE, "Inside MacFilevaultPersonalRecoveyKeyRotateGenerator resourceID[{0}],UDID[{1}]", new Object[] { resourceID, strUDID });
        return commandPayload;
    }
    
    private String createFilevaultPersonalKeyRotate(final Long resourceID, final String deviceUDID) throws Exception {
        final JSONObject existingEncryptionInfo = this.getFilevaultPRKPasswordForRotation(resourceID);
        if (existingEncryptionInfo != null) {
            final NSDictionary dict = MDMFilevaultUtils.getFileVaultPersonalRecoveryCommandXMLForResID(resourceID);
            String xmlContent = dict.toXMLPropertyList();
            xmlContent = DynamicVariableHandler.replaceDynamicVariable(xmlContent, "%filevault_personal_recovery_key%", existingEncryptionInfo.getString("PERSONAL_RECOVERY_KEY"));
            MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.INFO, "{0} Command has been created successfully - resourceID[{1}],UDID[{2}]", new Object[] { "MacFileVaultPersonalKeyRotate", resourceID, deviceUDID });
            return xmlContent;
        }
        MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.SEVERE, "{0} Unable to create Filevault Rotate Command - resourceID[{1}],UDID[{2}]", new Object[] { "MacFileVaultPersonalKeyRotate", resourceID, deviceUDID });
        return null;
    }
    
    public static SelectQuery getFilevaultImportQuery(final List<Long> resourceIDs) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "MDMDeviceFileVaultInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "DeviceToEncrytptionSettingsRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("DeviceToEncrytptionSettingsRel", "MDMFileVaultPersonalKeyConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 2));
        final Criteria customerIDCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("MDMFileVaultRotateKeyImportInfo", "CUSTOMER_ID"), 0);
        final Criteria serialNoCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)Column.getColumn("MDMFileVaultRotateKeyImportInfo", "DEVICE_IDENTIFIER"), 0);
        sQuery.addJoin(new Join("MdDeviceInfo", "MDMFileVaultRotateKeyImportInfo", customerIDCriteria.and(serialNoCriteria), 1));
        sQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8));
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        return sQuery;
    }
    
    private JSONObject getFilevaultPRKPasswordForRotation(final Long resourceID) throws Exception {
        final List<Long> resIDs = new ArrayList<Long>();
        resIDs.add(resourceID);
        final SelectQuery sQuery = getFilevaultImportQuery(resIDs);
        final DataObject doB = MDMUtil.getPersistence().get(sQuery);
        if (doB.isEmpty() || !doB.containsTable("MDMDeviceFileVaultInfo")) {
            MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.WARNING, "[Filevault]DOB is Empty in getFilevaultPRKPasswordForRotation for resourceID:{0}", resourceID);
            return null;
        }
        String currentRecoveryKey = null;
        final Long personalRecoveryCertificateID = (Long)doB.getFirstRow("MDMFileVaultPersonalKeyConfiguration").get("RECOVERY_ENCRYPT_CERT_ID");
        MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.INFO, "[Filevault]CertificateID Mapped for  resourceID: {0} CertificateID : {1}", new Object[] { resourceID, personalRecoveryCertificateID });
        if (doB.containsTable("MDMFileVaultRotateKeyImportInfo")) {
            final String importedKey = (String)doB.getFirstRow("MDMFileVaultRotateKeyImportInfo").get("PERSONAL_RECOVERY_KEY");
            final String errorRemarks = (String)doB.getFirstRow("MDMFileVaultRotateKeyImportInfo").get("ERROR_REMARKS");
            if (MDMStringUtils.isEmpty(errorRemarks)) {
                if (!MDMStringUtils.isEmpty(importedKey)) {
                    currentRecoveryKey = importedKey;
                }
                else {
                    MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.WARNING, "[Filevault] Not taking Imported FV key as its Empty set resourceID:{0}", resourceID);
                }
            }
            else {
                MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.WARNING, "[Filevault] Not taking Imported FV key as it has Error set resourceID:{0}", resourceID);
            }
        }
        if (currentRecoveryKey == null) {
            currentRecoveryKey = (String)doB.getFirstRow("MDMDeviceFileVaultInfo").get("PERSONAL_RECOVERY_KEY");
        }
        if (MDMStringUtils.isEmpty(currentRecoveryKey)) {
            MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.SEVERE, "[Filevault]Previous Recovery Key is Empty for resourceID:{0}", resourceID);
            return null;
        }
        final JSONObject responseObject = new JSONObject();
        responseObject.put("RECOVERY_ENCRYPT_CERT_ID", (Object)personalRecoveryCertificateID);
        responseObject.put("PERSONAL_RECOVERY_KEY", (Object)currentRecoveryKey);
        return responseObject;
    }
    
    public static X509Certificate getPersonalRecoveryKeyCertificate(final Long certificateID) throws Exception {
        final Criteria certificateIDCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certificateID, 0);
        final DataObject certificatesDO = ProfileCertificateUtil.getCertificateDO(certificateIDCriteria);
        if (certificatesDO.containsTable("CredentialCertificateInfo")) {
            final Row credentialRow = certificatesDO.getFirstRow("CredentialCertificateInfo");
            final Long customerID = (Long)credentialRow.get("CUSTOMER_ID");
            final String certificateName = (String)credentialRow.get("CERTIFICATE_FILE_NAME");
            final String cerFolder = MDMUtil.getCredentialCertificateFolder(customerID);
            final String certPath = cerFolder + File.separator + certificateName;
            final String password = (String)credentialRow.get("CERTIFICATE_PASSWORD");
            final X509Certificate certificate = CredentialsMgmtAction.readCertificateFromPKCS12(certPath, password);
            if (certificate != null) {
                return certificate;
            }
        }
        MacFilevaultPersonalRecoveyKeyRotateGenerator.LOGGER.log(Level.WARNING, "[Filevault] Unable to get Certificate Object for Filevault Rotate Key CertificateID:{0}", certificateID);
        throw new Exception("Unable to get Certificate Object for Filevault Rotate Key CertificateID:\"+certificateID");
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
