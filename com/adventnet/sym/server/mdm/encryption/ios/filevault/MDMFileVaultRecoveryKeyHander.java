package com.adventnet.sym.server.mdm.encryption.ios.filevault;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import com.adventnet.sym.server.mdm.encryption.MDMCMSEnvelopedDataParser;
import org.bouncycastle.util.encoders.Base64;
import java.util.logging.Logger;

public class MDMFileVaultRecoveryKeyHander
{
    static Logger logger;
    
    public static String getDecodedFileVaultRecoveryKey(final String base64EncodedCMSBlob, final Long certificatID) {
        final byte[] base64DecodedCMSEnvelopedRaw = Base64.decode(base64EncodedCMSBlob);
        final byte[] deodedParsedData = MDMCMSEnvelopedDataParser.decodeWithMDMCertificate(base64DecodedCMSEnvelopedRaw, certificatID);
        if (deodedParsedData != null) {
            try {
                return IOUtils.toString(deodedParsedData);
            }
            catch (final IOException e) {
                MDMFileVaultRecoveryKeyHander.logger.log(Level.INFO, "FileVaultLog: Exception in getDecodedFileVaultRecoveryKey()", e);
            }
        }
        return null;
    }
    
    public static String getDecodedFileVaultRecoveryKeyForDevice(final String base64EncodedCMSBlob, final Long resourceID) {
        MDMFileVaultRecoveryKeyHander.logger.log(Level.INFO, "FileVaultLog: Inside getDecodedFileVaultRecoveryKeyForDevice() for resource: {0}", new Object[] { resourceID });
        final Long certificateID = getFileVaultPersonalKeyEscrowCert(resourceID);
        if (certificateID == null) {
            MDMFileVaultRecoveryKeyHander.logger.log(Level.SEVERE, "FileVaultLog: File Vault escrow cert is not available for decoding recovery key for resource: {0}", new Object[] { resourceID });
            return null;
        }
        MDMFileVaultRecoveryKeyHander.logger.log(Level.INFO, "FileVaultLog: CMS Certificate to read Personal recovery key : CertificateID: {0} is mapped to resource: {1}", new Object[] { certificateID, resourceID });
        final String decodedString = getDecodedFileVaultRecoveryKey(base64EncodedCMSBlob, certificateID);
        if (MDMStringUtils.isEmpty(decodedString)) {
            MDMFileVaultRecoveryKeyHander.logger.log(Level.SEVERE, "FileVaultLog: Decoded FV Recovery String is empty: {0}", new Object[] { resourceID });
        }
        return decodedString;
    }
    
    public static Long getFileVaultPersonalKeyEscrowCert(final Long resourceID) {
        try {
            final SelectQuery sqlStmt = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMFileVaultPersonalKeyConfiguration"));
            sqlStmt.addJoin(new Join("MDMFileVaultPersonalKeyConfiguration", "DeviceToEncrytptionSettingsRel", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 2));
            sqlStmt.addSelectColumn(new Column("MDMFileVaultPersonalKeyConfiguration", "*"));
            sqlStmt.setCriteria(new Criteria(new Column("DeviceToEncrytptionSettingsRel", "RESOURCE_ID"), (Object)resourceID, 0));
            final DataObject resDO = MDMUtil.getPersistence().get(sqlStmt);
            if (!resDO.isEmpty()) {
                final Row instRow = resDO.getFirstRow("MDMFileVaultPersonalKeyConfiguration");
                return (Long)instRow.get("RECOVERY_ENCRYPT_CERT_ID");
            }
        }
        catch (final Exception ex) {
            MDMFileVaultRecoveryKeyHander.logger.log(Level.SEVERE, "FileVaultLog: Exception in getFileVaultPersonalKeyEscrowCert()", ex);
        }
        return null;
    }
    
    public static Long getFileVaultInstitutionalRecoveryCert(final Long resourceID) {
        try {
            final SelectQuery sqlStmt = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMFileVaultInstitutionConfiguration"));
            sqlStmt.addJoin(new Join("MDMFileVaultInstitutionConfiguration", "DeviceToEncrytptionSettingsRel", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 2));
            sqlStmt.addSelectColumn(new Column("MDMFileVaultInstitutionConfiguration", "*"));
            sqlStmt.setCriteria(new Criteria(new Column("DeviceToEncrytptionSettingsRel", "RESOURCE_ID"), (Object)resourceID, 0));
            final DataObject resDO = MDMUtil.getPersistence().get(sqlStmt);
            final Row instRow = resDO.getFirstRow("MDMFileVaultInstitutionConfiguration");
            return (Long)instRow.get("INSTITUTION_ENCRYPTION_CERT");
        }
        catch (final Exception ex) {
            MDMFileVaultRecoveryKeyHander.logger.log(Level.SEVERE, "FileVaultLog: Exception in getFileVaultInstitutionalRecoveryCert()", ex);
            return null;
        }
    }
    
    public static String getFileVaultRecoveryKey(final Long resourceID) {
        try {
            final String stringFileVaultRecoveryKey = (String)DBUtil.getValueFromDB("MDMDeviceFileVaultInfo", "RESOURCE_ID", (Object)resourceID, "PERSONAL_RECOVERY_KEY");
            return stringFileVaultRecoveryKey;
        }
        catch (final Exception ex) {
            MDMFileVaultRecoveryKeyHander.logger.log(Level.INFO, "FileVaultLog: Exception in getFileVaultRecoveryKey()", ex);
            return null;
        }
    }
    
    static {
        MDMFileVaultRecoveryKeyHander.logger = Logger.getLogger("MDMConfigLogger");
    }
}
