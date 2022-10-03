package com.me.devicemanagement.framework.server.csv;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public abstract class CSVTask implements SchedulerExecutionInterface
{
    protected Long customerID;
    protected Long userID;
    protected Long operationID;
    private static Logger logger;
    
    private void persistTaskInputs(final JSONObject json, final Long userID, final Long customerID, final Long operationID) throws Exception {
        this.clearTaskInputs(customerID, operationID);
        final DataObject dobj = (DataObject)new WritableDataObject();
        final Row instance = new Row("CSVInprogressOperation");
        instance.set("CUSTOMER_ID", (Object)customerID);
        instance.set("OPERATION_ID", (Object)operationID);
        dobj.addRow(instance);
        final Set<Map.Entry> entrySet = json.entrySet();
        Iterator<Map.Entry> i = (Iterator<Map.Entry>)entrySet.iterator();
        i = (Iterator<Map.Entry>)entrySet.iterator();
        while (i.hasNext()) {
            final Map.Entry element = i.next();
            final String key = element.getKey();
            final String value = String.valueOf(element.getValue());
            final Row inputParams = new Row("CSVInputParams");
            inputParams.set("INSTANCE_ID", instance.get("INSTANCE_ID"));
            inputParams.set("PARAMETER_NAME", (Object)key);
            inputParams.set("PARAMETER_VALUE", (Object)value);
            dobj.addRow(inputParams);
        }
        final Row inputParams2 = new Row("CSVInputParams");
        inputParams2.set("INSTANCE_ID", instance.get("INSTANCE_ID"));
        inputParams2.set("PARAMETER_NAME", (Object)"userID");
        inputParams2.set("PARAMETER_VALUE", (Object)String.valueOf(userID));
        dobj.addRow(inputParams2);
        SyMUtil.getPersistence().add(dobj);
    }
    
    private void clearTaskInputs(final Long customerID, final Long operationID) throws Exception {
        final Row r = new Row("CSVInprogressOperation");
        r.set("OPERATION_ID", (Object)operationID);
        r.set("CUSTOMER_ID", (Object)customerID);
        SyMUtil.getPersistence().delete(r);
    }
    
    @Override
    public void executeTask(final Properties props) {
        try {
            final JSONObject json = this.getInputs(props);
            this.initialize(props);
            this.persistTaskInputs(json, this.userID, this.customerID, this.operationID);
            this.performOperation(json);
            this.clearTaskInputs(this.customerID, this.operationID);
        }
        catch (final Exception e) {
            CSVTask.logger.log(Level.SEVERE, "Exception occured while excuting Task..{0}", e);
        }
    }
    
    private void initialize(final Properties taskProps) throws Exception {
        this.operationID = Long.parseLong(((Hashtable<K, String>)taskProps).get("operationID"));
        this.customerID = Long.parseLong(((Hashtable<K, String>)taskProps).get("customerID"));
        this.userID = Long.parseLong(((Hashtable<K, String>)taskProps).get("userID"));
    }
    
    protected JSONObject getInputs(final Properties taskProps) throws Exception {
        return new JSONObject();
    }
    
    protected abstract void performOperation(final JSONObject p0) throws Exception;
    
    static {
        CSVTask.logger = Logger.getLogger("MDMLogger");
    }
}
