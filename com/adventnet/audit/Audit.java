package com.adventnet.audit;

import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import java.util.logging.Level;
import com.adventnet.audit.util.AuditUtil;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import java.util.Hashtable;
import java.util.logging.Logger;

public class Audit
{
    private static String className;
    private static Logger logger;
    private String moduleName;
    private static Hashtable auditInstanceHT;
    private DataObject moduleConfigDO;
    private HashMap auditBufferMap;
    private HashMap notifyCriteriaMap;
    
    protected Audit(final String modname) throws AuditException {
        this.moduleName = modname;
        this.moduleConfigDO = AuditUtil.getAuditConfiguration(this.moduleName);
        Audit.logger.log(Level.FINEST, "Audit moduleconfiguration obtained for modulename : {0} is {1}", new Object[] { this.moduleName, this.moduleConfigDO });
        this.auditBufferMap = this.getInitializedAuditBufferMap();
        this.notifyCriteriaMap = new HashMap();
    }
    
    public static Audit getInstance(final String modulename) throws AuditException {
        Audit.logger.log(Level.FINEST, "Audit.getInstance called for moduleName : {0}", modulename);
        final String htkey = (modulename == null) ? "DEFAULTCONFIG" : modulename;
        Audit al = null;
        if (Audit.auditInstanceHT == null) {
            Audit.logger.log(Level.FINEST, "AuditInstance Hashtable is null. initializing it");
            Audit.auditInstanceHT = new Hashtable();
        }
        final DataObject moduleConfig = AuditUtil.getAuditConfiguration(modulename);
        if (moduleConfig.isEmpty()) {
            Audit.logger.log(Level.FINEST, "Auditconfiguration does not exist for module : {0}. Hence setting default audit configuration");
            al = Audit.auditInstanceHT.get("DEFAULTCONFIG");
            if (al == null) {
                Audit.logger.log(Level.FINEST, "Default Configuration does not exist. Initializing a default configuration");
                al = new Audit(null);
                Audit.auditInstanceHT.put("DEFAULTCONFIG", al);
            }
        }
        else {
            al = Audit.auditInstanceHT.get(htkey);
            if (al == null) {
                Audit.logger.log(Level.FINEST, "Audit instance does not exist for the key {0}, creating a new instantce", htkey);
                al = new Audit(modulename);
                Audit.auditInstanceHT.put(htkey, al);
            }
            else {
                Audit.logger.log(Level.FINEST, "Audit instance obtained from instance hashtable is {0}", al);
            }
        }
        return al;
    }
    
    public void createAuditRecord(DataObject auditRecordDO, final Hashtable nonTransPropHT) throws AuditException {
        Audit.logger.log(Level.FINEST, "createAuditRecord called with dataobject : {0} and properties hashtable : {1}", new Object[] { auditRecordDO, nonTransPropHT });
        try {
            if (auditRecordDO == null || auditRecordDO.isEmpty()) {
                Audit.logger.log(Level.FINEST, "auditRecord dataobject is empty or null. Hence ignored");
                return;
            }
            if (this.isAuditDisabled()) {
                Audit.logger.log(Level.FINEST, "Audit is disabled for the module. Hence ignored");
                return;
            }
            auditRecordDO = this.getFilteredRecordsBasedOnEnableCriteria(auditRecordDO, nonTransPropHT);
            if (auditRecordDO == null) {
                return;
            }
            auditRecordDO = this.fillupSeverity(auditRecordDO, nonTransPropHT);
            auditRecordDO = this.getCustomizedRecord(auditRecordDO, nonTransPropHT);
            auditRecordDO = this.getFilteredRecordsBasedOnCurrentLevel(auditRecordDO);
            final String recordType = (String)auditRecordDO.getFirstValue("AuditRecord", "RECORDTYPE");
            final int bufferSize = this.getBufferSizeFromConfig(recordType);
            if (bufferSize > 0) {
                this.addRecordToBuffer(auditRecordDO, recordType);
                Audit.logger.log(Level.FINEST, "AuditRecord added to buffer");
            }
            else {
                DataAccess.add(auditRecordDO);
                Audit.logger.log(Level.FINEST, "Audit record added to the data store");
            }
        }
        catch (final Exception e) {
            Audit.logger.log(Level.SEVERE, "Exception while creating Audit record", e);
            throw new AuditException(e.getMessage(), e);
        }
    }
    
    public DataObject getModuleConfiguration() throws AuditException {
        return this.moduleConfigDO = AuditUtil.getAuditConfiguration(this.moduleName);
    }
    
    public boolean isAuditDisabled() {
        try {
            final Row acRow = this.moduleConfigDO.getFirstRow("AuditConfig");
            final Boolean disable = (Boolean)acRow.get("DISABLEALLAUDIT");
            return disable;
        }
        catch (final DataAccessException dae) {
            Audit.logger.log(Level.SEVERE, "Exception while getting isAuditDisabled : ", (Throwable)dae);
            return false;
        }
    }
    
    public String getCurrentLevel() throws Exception {
        final String currLevel = (String)this.moduleConfigDO.getFirstValue("AuditConfig", "CURRENTLEVEL");
        return currLevel;
    }
    
    public void saveBuffer() {
        final Iterator iterator = this.auditBufferMap.keySet().iterator();
        if (iterator == null) {
            Audit.logger.log(Level.FINEST, "Iterator obtained from hashmap is null. Returning");
            return;
        }
        AuditBuffer buffer = null;
        String tablename = null;
        while (iterator.hasNext()) {
            tablename = iterator.next();
            Audit.logger.log(Level.FINEST, "flushing buffer for table : {0}", tablename);
            buffer = this.auditBufferMap.get(tablename);
            if (buffer != null) {
                buffer.saveRecords();
                Audit.logger.log(Level.FINEST, "Buffer flushed");
            }
        }
        Audit.logger.exiting(Audit.className, "saveBuffer");
    }
    
    public void close() {
        Audit.logger.entering(Audit.className, "close");
        this.saveBuffer();
        Audit.logger.exiting(Audit.className, "close");
    }
    
    public void modifyConfiguration(final DataObject updatedDO) throws AuditException {
        try {
            Audit.logger.log(Level.FINEST, "moduleConfigurationDO before modifying : {0}", this.moduleConfigDO);
            Audit.logger.log(Level.FINEST, "module configuration DO to be updated : {0}", updatedDO);
            this.moduleConfigDO = DataAccess.update(updatedDO);
            Audit.logger.log(Level.FINEST, "moduleConfigurationDO after updating the database : {0}", this.moduleConfigDO);
            this.refreshInstance();
        }
        catch (final Exception e) {
            Audit.logger.log(Level.SEVERE, "Exception while updating module configuration : ", e);
            throw new AuditException("Unable to update module configuration for module " + this.moduleName, e);
        }
    }
    
    public void setBufferSize(final String tablename, final int buffersize) throws AuditException {
        try {
            final DataObject dobj = this.getModuleConfiguration();
            final Object id = dobj.getFirstValue("AuditConfig", "ID");
            final Row oarRow = new Row("AuditTableConfig");
            oarRow.set("AUDITCONFIG_ID", id);
            oarRow.set("AUDITTABLENAME", (Object)tablename);
            final Row reqRow = dobj.getFirstRow("AuditTableConfig", oarRow);
            reqRow.set("BUFFERSIZE", (Object)new Integer(buffersize));
            dobj.updateRow(reqRow);
            this.modifyConfiguration(dobj);
        }
        catch (final Exception e) {
            Audit.logger.log(Level.SEVERE, "Exception while setting buffer size for table : {0} : {1}", new Object[] { tablename, e });
            throw new AuditException("Exception while setting buffer size ", e);
        }
    }
    
    private DataObject getFilteredRecordsBasedOnCurrentLevel(final DataObject recordDO) {
        try {
            final String currentLevel = this.getCurrentLevel();
            if (currentLevel.equals("FINE_GRAINED")) {
                return recordDO;
            }
            Audit.logger.log(Level.FINEST, "current level is not FINE_GRAINED - deleting additional records before persisting to the db");
            if (recordDO.containsTable("AuditUserProperty")) {
                final Criteria criteria = new Criteria(Column.getColumn("AuditUserProperty", "PROPERTYNAME"), (Object)"*", 2);
                recordDO.deleteRows("AuditUserProperty", criteria);
            }
            if (recordDO.containsTable("AuditResultProperty")) {
                final Criteria criteria = new Criteria(Column.getColumn("AuditResultProperty", "PROPERTYNAME"), (Object)"*", 2);
                recordDO.deleteRows("AuditResultProperty", criteria);
            }
            if (recordDO.containsTable("AuditResourceProp")) {
                final Criteria criteria = new Criteria(Column.getColumn("AuditResourceProp", "PROPERTYNAME"), (Object)"*", 2);
                recordDO.deleteRows("AuditResourceProp", criteria);
            }
            if (recordDO.containsTable("AuditOperProperty")) {
                final Criteria criteria = new Criteria(Column.getColumn("AuditOperProperty", "PROPERTYNAME"), (Object)"*", 2);
                recordDO.deleteRows("AuditOperProperty", criteria);
            }
        }
        catch (final Exception e) {
            Audit.logger.log(Level.SEVERE, "Exception while filtering audit records based on current level : ", e);
        }
        return recordDO;
    }
    
    private DataObject getFilteredRecordsBasedOnEnableCriteria(final DataObject auditRecord, final Hashtable propertiesHT) {
        return auditRecord;
    }
    
    private void refreshInstance() {
        try {
            final Iterator iterator = this.auditBufferMap.keySet().iterator();
            if (iterator == null) {
                Audit.logger.log(Level.FINEST, "Iterator obtained from hashmap in refresh instance is null. Returning");
                return;
            }
            AuditBuffer buffer = null;
            String tablename = null;
            while (iterator.hasNext()) {
                tablename = iterator.next();
                buffer = this.auditBufferMap.get(tablename);
                if (buffer != null) {
                    buffer.setBufferSize(this.getBufferSizeFromConfig(tablename));
                    Audit.logger.log(Level.FINEST, "Buffer flushed");
                }
            }
        }
        catch (final Exception e) {
            Audit.logger.log(Level.FINEST, "Exception while refreshing audit instance", e);
        }
    }
    
    private DataObject fillupSeverity(final DataObject dobj, final Hashtable properties) {
        try {
            final boolean containsOAR = dobj.containsTable("OperationAuditRecord");
            if (!containsOAR) {
                return dobj;
            }
            final Iterator iterator = dobj.getRows("AuditRecord");
            if (iterator == null) {
                return dobj;
            }
            final List tableList = dobj.getTableNames();
            Row auditRow = null;
            DataObject tempDO = null;
            Row oarRow = null;
            while (iterator.hasNext()) {
                auditRow = iterator.next();
                tempDO = dobj.getDataObject(tableList, auditRow);
                if (tempDO.containsTable("OperationAuditRecord")) {
                    oarRow = tempDO.getFirstRow("OperationAuditRecord");
                    oarRow.set("SEVERITY", (Object)this.getSeverity(tempDO, properties));
                    dobj.updateRow(oarRow);
                }
            }
        }
        catch (final Exception e) {
            Audit.logger.log(Level.SEVERE, "Exception while filling severity for the record : ", e);
        }
        return dobj;
    }
    
    private String getSeverity(final DataObject recDO, final Hashtable propHT) {
        String toRet;
        try {
            final String severity = (String)recDO.getFirstValue("OperationAuditRecord", "SEVERITY");
            if (severity == null) {
                toRet = "Nil";
            }
            else {
                toRet = severity;
            }
        }
        catch (final DataAccessException dae) {
            toRet = "Error";
            Audit.logger.log(Level.SEVERE, "DataAccessException thrown when calculating severity ", (Throwable)dae);
        }
        return toRet;
    }
    
    private DataObject getCustomizedRecord(final DataObject orgDO, final Hashtable propertiesHT) {
        DataObject customizedDO = null;
        try {
            final String customizerName = (String)this.moduleConfigDO.getFirstValue("AuditConfig", "CUSTOMPROVIDER");
            if (customizerName == null || customizerName.equals("")) {
                Audit.logger.log(Level.FINEST, "Customizer does not exist for the module. Hence returing the original DO");
                return orgDO;
            }
            final Iterator arItr = orgDO.getRows("AuditRecord");
            if (arItr == null) {
                Audit.logger.log(Level.FINEST, "dataobject passed for getting customized record does not contain auditrecord table. iterator obtained is null");
                return orgDO;
            }
            customizedDO = DataAccess.constructDataObject();
            Audit.logger.log(Level.FINEST, "Trying to get CustomProvider class instance : {0}", customizerName);
            final CustomAuditProvider provider = (CustomAuditProvider)this.getClass().getClassLoader().loadClass(customizerName).newInstance();
            Audit.logger.log(Level.FINEST, "CustomProvider class instance obtained is : {0}", provider.toString());
            Row arRow = null;
            DataObject tempDO = null;
            DataObject temp_customizedDO = null;
            final List tablenames = orgDO.getTableNames();
            while (arItr.hasNext()) {
                arRow = arItr.next();
                tempDO = orgDO.getDataObject(tablenames, arRow);
                Audit.logger.log(Level.FINEST, "AuditRecord before Customization {0}", tempDO);
                temp_customizedDO = provider.getCustomRecords(tempDO, propertiesHT);
                Audit.logger.log(Level.FINEST, "AuditRecord after Customization {0}", temp_customizedDO);
                if (!temp_customizedDO.isEmpty()) {
                    customizedDO.merge(temp_customizedDO);
                }
            }
        }
        catch (final DataAccessException dae) {
            Audit.logger.log(Level.WARNING, "Exception when trying to get Customizer name : ", (Throwable)dae);
            customizedDO = orgDO;
        }
        catch (final ClassNotFoundException e) {
            Audit.logger.log(Level.WARNING, "Custom provider class name given is not found in this VM.", e);
            customizedDO = orgDO;
        }
        catch (final InstantiationException ine) {
            customizedDO = orgDO;
            Audit.logger.log(Level.WARNING, "Custom provider class name given is not found in this VM.", ine);
        }
        catch (final IllegalAccessException ile) {
            customizedDO = orgDO;
            Audit.logger.log(Level.WARNING, "Custom provider class name given is not found in this VM.", ile);
        }
        catch (final Exception e2) {
            Audit.logger.log(Level.SEVERE, "Exception while getting custom records. returned uncustomized do ", e2);
            return orgDO;
        }
        return customizedDO;
    }
    
    private void addRecordToBuffer(final DataObject recordDO, final String auditTableName) throws AuditException {
        try {
            Audit.logger.entering(Audit.className, "addRecordToBuffer");
            final AuditBuffer auditBuffer = this.auditBufferMap.get(auditTableName);
            if (auditBuffer == null) {
                Audit.logger.log(Level.SEVERE, "Audit buffer obtained for tablename : {0} is null ", auditTableName);
                throw new AuditException("AuditBuffer is null for table :" + auditTableName);
            }
            final Iterator itr = recordDO.getRows("AuditRecord");
            if (itr == null) {
                Audit.logger.log(Level.FINEST, "iterator obtained from recordDO for auditrecord table is null. Returning");
                return;
            }
            Row arRow = null;
            final List tablenames = recordDO.getTableNames();
            while (itr.hasNext()) {
                arRow = itr.next();
                auditBuffer.addRecord(recordDO.getDataObject(tablenames, arRow));
            }
            Audit.logger.log(Level.FINEST, "Added recordDO to buffer");
        }
        catch (final Exception e) {
            Audit.logger.log(Level.SEVERE, "Exception while saving records to buffer ", e);
            throw new AuditException("Error while saving records to buffer", e);
        }
    }
    
    private int getBufferSizeFromConfig(final String tablename) {
        int size = 0;
        try {
            final Object auditConfigId = this.moduleConfigDO.getFirstValue("AuditConfig", "ID");
            final Row row = new Row("AuditTableConfig");
            row.set("AUDITCONFIG_ID", auditConfigId);
            row.set("AUDITTABLENAME", (Object)tablename);
            final Row atcRow = this.moduleConfigDO.getFirstRow("AuditTableConfig", row);
            size = (int)atcRow.get("BUFFERSIZE");
        }
        catch (final Exception e) {
            Audit.logger.log(Level.FINEST, "Exception while getting buffer size from moduleconif DO for table {0} : {1}", new Object[] { tablename, e });
        }
        return size;
    }
    
    private HashMap getInitializedAuditBufferMap() throws AuditException {
        final HashMap hMap = new HashMap();
        Iterator atcIterator = null;
        try {
            atcIterator = this.moduleConfigDO.getRows("AuditTableConfig");
        }
        catch (final DataAccessException dae) {
            Audit.logger.log(Level.SEVERE, "Exception while fetching audit table configuration for module " + this.moduleName + " : ", (Throwable)dae);
            throw new AuditException("Exception while fetching audit table configuration for module", (Exception)dae);
        }
        if (atcIterator == null) {
            Audit.logger.log(Level.WARNING, "Iterator obtained for table AuditTableConfig for module : {0} is null ", this.moduleName);
            return hMap;
        }
        Row atcRow = null;
        String auditTableName = null;
        int bufferSize = 0;
        int batchSize = 0;
        while (atcIterator.hasNext()) {
            AuditBuffer auditBuffer = null;
            atcRow = atcIterator.next();
            auditTableName = (String)atcRow.get("AUDITTABLENAME");
            bufferSize = (int)atcRow.get("BUFFERSIZE");
            batchSize = (int)atcRow.get("BATCHSIZE");
            auditBuffer = new AuditBuffer(auditTableName, bufferSize, batchSize);
            hMap.put(auditTableName, auditBuffer);
        }
        Audit.logger.log(Level.FINEST, "auditBuffer hashmap initialized for module : {0} is = {1}", new Object[] { this.moduleName, hMap });
        return hMap;
    }
    
    protected void finazlize() throws Throwable {
        this.saveBuffer();
    }
    
    @Override
    public String toString() {
        return "[AuditInstance for: " + this.moduleName + "]";
    }
    
    static {
        Audit.className = Audit.class.getName();
        Audit.logger = Logger.getLogger(Audit.className);
    }
}
