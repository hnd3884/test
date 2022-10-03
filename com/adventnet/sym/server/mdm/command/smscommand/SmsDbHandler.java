package com.adventnet.sym.server.mdm.command.smscommand;

import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Table;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmsDbHandler
{
    static Logger logger;
    
    private void generateKeys(final Long customerId) {
        final SmsKeysHandler keyGenerator = new SmsKeysHandler();
        try {
            keyGenerator.generateKeyPair();
            final String publicKey = keyGenerator.getPublicKey();
            final String privateKey = keyGenerator.getPrivateKey();
            SmsDbHandler.logger.log(Level.INFO, "The public and private key are generated");
            this.writeKeyInDB(privateKey, publicKey, customerId);
        }
        catch (final NoSuchAlgorithmException e) {
            SmsDbHandler.logger.log(Level.WARNING, "RSA Algorithm not supported : {0}\n", e.toString());
        }
    }
    
    public String getPublicKey(final Long customerId) {
        final Table smsTable = new Table("SMSCommandKey");
        final Column publicKey = new Column("SMSCommandKey", "PUBLIC_KEY");
        final Column privateKey = new Column("SMSCommandKey", "PRIVATE_KEY");
        final Column primaryKey = new Column("SMSCommandKey", "SMSCOMMAND_ID");
        final Column customerInfo = new Column("SMSCommandKey", "CUSTOMER_ID");
        final SelectQuery publicKeyQuery = (SelectQuery)new SelectQueryImpl(smsTable);
        publicKeyQuery.addSelectColumn(publicKey);
        publicKeyQuery.addSelectColumn(privateKey);
        publicKeyQuery.addSelectColumn(primaryKey);
        publicKeyQuery.addSelectColumn(customerInfo);
        final Criteria customerCriteria = new Criteria(Column.getColumn("SMSCommandKey", "CUSTOMER_ID"), (Object)customerId, 0);
        publicKeyQuery.setCriteria(customerCriteria);
        try {
            final DataObject publicKeyObject = MDMUtil.getPersistence().get(publicKeyQuery);
            final Object columnData = publicKeyObject.getFirstRow("SMSCommandKey").get("PUBLIC_KEY");
            SmsDbHandler.logger.log(Level.INFO, "The public key is : {0}", columnData);
            return (String)columnData;
        }
        catch (final DataAccessException e) {
            SmsDbHandler.logger.log(Level.WARNING, "Data not present in DB {0}", e.getMessage());
            return null;
        }
    }
    
    private String getPrivateKey(final Long customerId) {
        final Table smsTable = new Table("SMSCommandKey");
        final Column publicKey = new Column("SMSCommandKey", "PUBLIC_KEY");
        final Column privateKey = new Column("SMSCommandKey", "PRIVATE_KEY");
        final Column primaryKey = new Column("SMSCommandKey", "SMSCOMMAND_ID");
        final Column customerInfo = new Column("SMSCommandKey", "CUSTOMER_ID");
        final SelectQuery privateKeyQuery = (SelectQuery)new SelectQueryImpl(smsTable);
        privateKeyQuery.addSelectColumn(publicKey);
        privateKeyQuery.addSelectColumn(privateKey);
        privateKeyQuery.addSelectColumn(primaryKey);
        privateKeyQuery.addSelectColumn(customerInfo);
        final Criteria customerCriteria = new Criteria(Column.getColumn("SMSCommandKey", "CUSTOMER_ID"), (Object)customerId, 0);
        privateKeyQuery.setCriteria(customerCriteria);
        try {
            final DataObject privateKeyObject = MDMUtil.getPersistence().get(privateKeyQuery);
            final Object columnData = privateKeyObject.getFirstRow("SMSCommandKey").get("PRIVATE_KEY");
            return (String)columnData;
        }
        catch (final DataAccessException e) {
            SmsDbHandler.logger.log(Level.WARNING, "Data not present in DB {0}", e.getMessage());
            return null;
        }
    }
    
    private SmsKeysHandler setKeys() {
        final String publicKey = this.getPublicKey(new Long(2L));
        final String privateKey = this.getPrivateKey(new Long(2L));
        try {
            final SmsKeysHandler keyGen = new SmsKeysHandler().setPublicKey(publicKey).setPrivateKey(privateKey);
            return keyGen;
        }
        catch (final Exception e) {
            SmsDbHandler.logger.log(Level.WARNING, "Exception occured when setting the keys");
            SmsDbHandler.logger.log(Level.WARNING, e.toString());
            return null;
        }
    }
    
    public boolean isSmsCommandKeyInDB(final Long customerId) {
        final Table smsTable = new Table("SMSCommandKey");
        final Column publicKey = new Column("SMSCommandKey", "PUBLIC_KEY");
        final Column privateKey = new Column("SMSCommandKey", "PRIVATE_KEY");
        final Column primaryKey = new Column("SMSCommandKey", "SMSCOMMAND_ID");
        final Column customerDetails = new Column("SMSCommandKey", "CUSTOMER_ID");
        final SelectQuery fetchKeyQuery = (SelectQuery)new SelectQueryImpl(smsTable);
        fetchKeyQuery.addSelectColumn(publicKey);
        fetchKeyQuery.addSelectColumn(privateKey);
        fetchKeyQuery.addSelectColumn(primaryKey);
        fetchKeyQuery.addSelectColumn(customerDetails);
        final Criteria customerCriteria = new Criteria(Column.getColumn("SMSCommandKey", "CUSTOMER_ID"), (Object)customerId, 0);
        fetchKeyQuery.setCriteria(customerCriteria);
        try {
            final DataObject keyObject = MDMUtil.getPersistence().get(fetchKeyQuery);
            final Object columnData = keyObject.getFirstRow("SMSCommandKey").get("PUBLIC_KEY");
            SmsDbHandler.logger.log(Level.INFO, "The public key is : {0}", columnData);
            return columnData != null;
        }
        catch (final DataAccessException e) {
            SmsDbHandler.logger.log(Level.WARNING, "Data not present in DB {0}", e.getMessage());
            return false;
        }
    }
    
    private void writeKeyInDB(final String privateKey, final String publicKey, final Long customerID) {
        final Row keyRow = new Row("SMSCommandKey");
        keyRow.set("PRIVATE_KEY", (Object)privateKey);
        keyRow.set("PUBLIC_KEY", (Object)publicKey);
        keyRow.set("CUSTOMER_ID", (Object)customerID);
        try {
            final DataObject keyObject = MDMUtil.getPersistence().get("SMSCommandKey", (Criteria)null);
            keyObject.addRow(keyRow);
            MDMUtil.getPersistence().update(keyObject);
        }
        catch (final DataAccessException e) {
            SmsDbHandler.logger.log(Level.SEVERE, "Database access exception : {0}\n{1}", new Object[] { e.toString(), e.getMessage() });
        }
    }
    
    public void generateAndPublishKeys(final Long customerId) {
        this.generateKeys(customerId);
        this.publishKeys(customerId);
    }
    
    private void publishKeys(final Long customerId) {
        SmsDbHandler.logger.log(Level.INFO, "New key generated push the key to enrolled devices");
        final ArrayList managedAndroidDevicesList = ManagedDeviceHandler.getInstance().getAndroidManagedDevicesForCustomer(customerId);
        final List notificationDevicesList = new ArrayList();
        for (final Object resourceID : managedAndroidDevicesList) {
            SmsDbHandler.logger.log(Level.INFO, resourceID.toString());
            try {
                final Row row = DBUtil.getRowFromDB("ManagedDevice", "RESOURCE_ID", resourceID);
                if (row != null) {
                    final Long currentVersionCode = (Long)row.get("AGENT_VERSION_CODE");
                    if (currentVersionCode % 10000L <= 267L) {
                        continue;
                    }
                    SmsDbHandler.logger.log(Level.INFO, "Device is found to have a version greater than 267");
                    DeviceCommandRepository.getInstance().addSmsPublicKeyDistributorCommand((Long)resourceID);
                    notificationDevicesList.add(resourceID);
                }
                else {
                    SmsDbHandler.logger.log(Level.INFO, "No devices enrolled");
                }
            }
            catch (final Exception e) {
                SmsDbHandler.logger.log(Level.INFO, "Error fetching row from table : {0}", e.toString());
            }
        }
        try {
            NotificationHandler.getInstance().SendNotification(notificationDevicesList, 2);
        }
        catch (final Exception exp) {
            SmsDbHandler.logger.log(Level.SEVERE, "Exception in pushing commands to devices : {0}", exp.getMessage());
        }
    }
    
    static {
        SmsDbHandler.logger = Logger.getLogger(SmsDbHandler.class.getName());
    }
}
