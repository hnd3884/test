package com.me.devicemanagement.onpremise.server.metrack.ondemand;

import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.Vector;
import java.util.Hashtable;
import java.util.logging.Logger;

public class ONDemandDataCollectorBean
{
    private static Logger logger;
    private static String sourceClass;
    private Hashtable<String, Vector<Properties>> defaultFormRecords;
    private Hashtable<String, Properties> nonDefaultFormRecords;
    private String zcFailedRequests;
    
    protected ONDemandDataCollectorBean() {
        this.defaultFormRecords = new Hashtable<String, Vector<Properties>>();
        this.nonDefaultFormRecords = new Hashtable<String, Properties>();
        this.zcFailedRequests = "";
        if (!this.defaultFormRecords.isEmpty()) {
            this.defaultFormRecords = new Hashtable<String, Vector<Properties>>();
        }
        if (!this.nonDefaultFormRecords.isEmpty()) {
            this.nonDefaultFormRecords = new Hashtable<String, Properties>();
        }
        if (!"".equalsIgnoreCase(this.zcFailedRequests)) {
            this.zcFailedRequests = "";
        }
    }
    
    protected void setDefaultFormRecords(final String zcFormName, final Properties defaultFormRecord) {
        try {
            Vector vector = null;
            if (this.defaultFormRecords.containsKey(zcFormName)) {
                vector = this.defaultFormRecords.get(zcFormName);
            }
            else {
                vector = new Vector();
            }
            vector.add(defaultFormRecord);
            this.defaultFormRecords.put(zcFormName, vector);
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollectorBean.logger, ONDemandDataCollectorBean.sourceClass, "setDefaultFormRecords", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public Hashtable<String, Vector<Properties>> getDefaultFormRecords() {
        return this.defaultFormRecords;
    }
    
    protected void setNonDefaultFormRecords(final String zcFormName, final String zcColumnName, final String value) {
        try {
            Properties properties = null;
            if (this.nonDefaultFormRecords.containsKey(zcFormName)) {
                properties = this.nonDefaultFormRecords.get(zcFormName);
            }
            else {
                properties = new Properties();
            }
            ((Hashtable<String, String>)properties).put(zcColumnName, value);
            this.nonDefaultFormRecords.put(zcFormName, properties);
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollectorBean.logger, ONDemandDataCollectorBean.sourceClass, "setNonDefaultFormRecords", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public Hashtable<String, Properties> getNonDefaultFormRecords() {
        return this.nonDefaultFormRecords;
    }
    
    protected void setZCFailedRequests(final String zcFailedRequests) {
        this.zcFailedRequests = zcFailedRequests;
    }
    
    public String getZCFailedRequests() {
        return this.zcFailedRequests;
    }
    
    static {
        ONDemandDataCollectorBean.logger = Logger.getLogger("METrackLog");
        ONDemandDataCollectorBean.sourceClass = "ONDemandDataCollectorBean";
    }
}
