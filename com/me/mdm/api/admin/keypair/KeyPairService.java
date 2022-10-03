package com.me.mdm.api.admin.keypair;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPair;
import java.util.Base64;
import com.me.mdm.certificate.CryptographyUtil;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.model.BaseAPIModel;
import java.util.logging.Logger;

public class KeyPairService
{
    private final Logger logger;
    
    public KeyPairService() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public KeyPairResponseModel createKeyPair(final BaseAPIModel baseAPIModel, final String featureName) {
        KeyPairResponseModel kpResponse = new KeyPairResponseModel();
        int keyType = 0;
        try {
            if (KeyPairEnumConstants.valueOf(featureName) != null) {
                keyType = KeyPairEnumConstants.valueOf(featureName).getKeyType();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Feature name not found in enum");
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        try {
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("KeyPair")) {
                MDMOneLineLogger.log(Level.INFO, "Creating new key pair as requested for ", "{0} {1}", new String[] { "FEATURE : ", String.valueOf(keyType) });
                kpResponse = this.getKeyPairResponse(keyType, baseAPIModel.getCustomerId());
            }
            else {
                kpResponse = this.getPublicKey(baseAPIModel.getCustomerId(), keyType);
                if (kpResponse != null && kpResponse.getPublicKey() != null && !kpResponse.getPublicKey().isEmpty()) {
                    MDMOneLineLogger.log(Level.INFO, "Returning existing key pair for ", "{0} {1}", new String[] { "FEATURE : ", String.valueOf(keyType) });
                    return kpResponse;
                }
                MDMOneLineLogger.log(Level.INFO, "Creating new RSA key pair for ", "{0} {1}", new String[] { "FEATURE : ", String.valueOf(keyType) });
                kpResponse = this.getKeyPairResponse(keyType, baseAPIModel.getCustomerId());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in GeneratingKeyPair", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return kpResponse;
    }
    
    private KeyPairResponseModel getKeyPairResponse(final int keyType, final Long customerId) throws NoSuchAlgorithmException {
        final KeyPairResponseModel kpResponse = new KeyPairResponseModel();
        final KeyPair kp = CryptographyUtil.createRsaKeyPair(2048);
        final String privateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        final String publicKey = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        this.addKeyPairinDB(privateKey, publicKey, keyType, customerId);
        kpResponse.setPublicKey(publicKey);
        this.logger.log(Level.INFO, "Keypair for {0} is created", keyType);
        MDMOneLineLogger.log(Level.INFO, "Public Key Fetched for ", "{0} {1}", new String[] { "FEATURE : ", String.valueOf(keyType) });
        return kpResponse;
    }
    
    private void addKeyPairinDB(final String privateKey, final String publicKey, final int keyType, final Long customerId) {
        try {
            final DataObject resultDO = this.getDataObject(customerId, keyType);
            if (resultDO.isEmpty()) {
                this.logger.log(Level.INFO, "Created a new row");
                final Row row = new Row("KeyPairSummary");
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("KEY_TYPE", (Object)keyType);
                row.set("PRIVATE_KEY", (Object)privateKey);
                row.set("PUBLIC_KEY", (Object)publicKey);
                row.set("CREATED_AT", (Object)MDMUtil.getCurrentTimeInMillis());
                resultDO.addRow(row);
                MDMUtil.getPersistence().add(resultDO);
            }
            else {
                this.logger.log(Level.INFO, "Updated the existing ROW");
                final Row row = resultDO.getFirstRow("KeyPairSummary");
                row.set("PRIVATE_KEY", (Object)privateKey);
                row.set("PUBLIC_KEY", (Object)publicKey);
                row.set("CREATED_AT", (Object)MDMUtil.getCurrentTimeInMillis());
                resultDO.updateRow(row);
                MDMUtil.getPersistence().update(resultDO);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while update key details {0}", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private KeyPairResponseModel getPublicKey(final Long customerId, final int keyType) {
        final KeyPairResponseModel kp = new KeyPairResponseModel();
        try {
            final DataObject resultDO = this.getDataObject(customerId, keyType);
            if (!resultDO.isEmpty()) {
                MDMOneLineLogger.log(Level.INFO, "Fetching public key for ", "{0} {1}", new String[] { "FEATURE : ", String.valueOf(keyType) });
                final Row row = resultDO.getFirstRow("KeyPairSummary");
                kp.setPublicKey((String)row.get("PUBLIC_KEY"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getPublicKey method", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        MDMOneLineLogger.log(Level.INFO, "Public Key Fetched for ", "{0} {1}", new String[] { "FEATURE : ", String.valueOf(keyType) });
        return kp;
    }
    
    private DataObject getDataObject(final Long customerId, final int keyType) throws Exception {
        final Criteria customerCri = new Criteria(Column.getColumn("KeyPairSummary", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria featureCri = new Criteria(Column.getColumn("KeyPairSummary", "KEY_TYPE"), (Object)keyType, 0);
        final Criteria finalCri = customerCri.and(featureCri);
        return MDMUtil.getPersistence().get("KeyPairSummary", finalCri);
    }
}
