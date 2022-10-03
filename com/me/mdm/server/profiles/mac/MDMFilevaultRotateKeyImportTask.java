package com.me.mdm.server.profiles.mac;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class MDMFilevaultRotateKeyImportTask extends CSVTask
{
    Logger logger;
    
    public MDMFilevaultRotateKeyImportTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    protected void performOperation(final JSONObject json) throws Exception {
        this.logger.log(Level.INFO, "Entering MDMFilevaultRotateKeyImportTask..");
        this.copyFilevaultImportDetails(this.customerID);
        this.logger.log(Level.INFO, "Exiting MDMFilevaultRotateKeyImportTask..");
    }
    
    private void copyFilevaultImportDetails(final Long customerID) {
        final HashMap<String, String> serialRotationKeyMap = new HashMap<String, String>();
        final SelectQuery fvPRK = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMFileVaultRotateKeyImportInfoCSVTemp"));
        fvPRK.addSelectColumn(new Column("MDMFileVaultRotateKeyImportInfoCSVTemp", "*"));
        fvPRK.setCriteria(new Criteria(new Column("MDMFileVaultRotateKeyImportInfoCSVTemp", "CUSTOMER_ID"), (Object)customerID, 0));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(fvPRK);
            if (dataObject.containsTable("MDMFileVaultRotateKeyImportInfoCSVTemp")) {
                final Iterator itr = dataObject.getRows("MDMFileVaultRotateKeyImportInfoCSVTemp");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    serialRotationKeyMap.put((String)row.get("SERIAL_NUMBER"), (String)row.get("PERSONAL_RECOVERY_KEY"));
                }
                dataObject.deleteRows("MDMFileVaultRotateKeyImportInfoCSVTemp", (Criteria)null);
                this.logger.log(Level.INFO, "FilevaultLog: Going to copy FV PRK for Serial Nos:{0}", serialRotationKeyMap.keySet());
                MDMFilevaultPersonalRecoveryKeyImport.addOrUpdateFilevaultKeyImport(customerID, 1, serialRotationKeyMap);
            }
            MDMUtil.getPersistence().update(dataObject);
            CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getStatusLabel("MacFilevaultKeyImport"), String.valueOf(serialRotationKeyMap.size()), (long)customerID);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "FilevaultLog Exception in copyFilevaultImportDetails", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "FilevaultLog Exception in copyFilevaultImportDetails", e2);
        }
    }
}
