package com.adventnet.audit;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.Vector;
import java.util.logging.Logger;

public class AuditBuffer
{
    private static String className;
    private static Logger logger;
    private int bufferSize;
    private int batchSize;
    private String bufferedTableName;
    private int currentSize;
    private Vector bufferedRecords;
    
    public AuditBuffer(final String tableName, final int buffersize, final int batchsize) {
        this.bufferSize = 10;
        this.batchSize = 5;
        this.bufferedTableName = null;
        this.currentSize = 0;
        this.bufferedRecords = new Vector(this.bufferSize);
        this.bufferedTableName = tableName;
        this.bufferSize = buffersize;
        this.batchSize = batchsize;
        this.bufferedRecords.setSize(this.bufferSize);
    }
    
    public synchronized void addRecord(final DataObject dobj) {
        if (dobj == null) {
            AuditBuffer.logger.log(Level.INFO, "DataObject passed as arg to addRecord is null. Hence ignored");
            return;
        }
        if (this.bufferedRecords == null) {
            this.bufferedRecords = new Vector(this.bufferSize);
        }
        this.bufferedRecords.add(this.currentSize, dobj);
        ++this.currentSize;
        AuditBuffer.logger.log(Level.INFO, "Buffer size = {0}, Current size of buffer = {1}", new Object[] { new Integer(this.bufferSize), new Integer(this.currentSize) });
        if (this.currentSize >= this.bufferSize) {
            this.saveRecords();
            this.currentSize = 0;
        }
        AuditBuffer.logger.exiting(AuditBuffer.className, "addRecord");
    }
    
    public synchronized void saveRecords() {
        AuditBuffer.logger.entering(AuditBuffer.className, "saveRecords");
        if (this.bufferedRecords == null) {
            AuditBuffer.logger.log(Level.INFO, "BufferedRecords vector to save is NULL. Hence ignored");
            return;
        }
        try {
            if (this.batchSize > 0) {
                int lastIndex = 0;
                int recsSize = 0;
                while (lastIndex < this.currentSize) {
                    if (lastIndex + this.batchSize < this.currentSize) {
                        recsSize = this.batchSize;
                    }
                    else {
                        recsSize = this.currentSize - lastIndex;
                    }
                    final DataObject addBatchRec = DataAccess.constructDataObject();
                    DataObject temp = null;
                    for (int i = lastIndex; i < recsSize + lastIndex; ++i) {
                        temp = this.bufferedRecords.elementAt(i);
                        addBatchRec.merge(temp);
                    }
                    try {
                        DataAccess.add(addBatchRec);
                    }
                    catch (final DataAccessException daex) {
                        AuditBuffer.logger.log(Level.SEVERE, "Exception while saving buffered records in batch", (Throwable)daex);
                    }
                    lastIndex += this.batchSize;
                }
            }
            else {
                for (int m = 0; m < this.currentSize; ++m) {
                    try {
                        if (this.bufferedRecords != null) {
                            final DataObject temp2 = this.bufferedRecords.elementAt(m);
                            if (temp2 != null) {
                                DataAccess.add(temp2);
                            }
                        }
                        else {
                            AuditBuffer.logger.log(Level.WARNING, "bufferedRecords is null when trying to save buffered records");
                        }
                    }
                    catch (final DataAccessException daex2) {
                        AuditBuffer.logger.log(Level.SEVERE, "Exception while saving buffered records ", (Throwable)daex2);
                    }
                }
            }
        }
        catch (final Exception dae) {
            AuditBuffer.logger.log(Level.SEVERE, "Exception when saving buffered audit records ", dae);
        }
        this.bufferedRecords.removeAllElements();
        this.currentSize = 0;
        this.bufferedRecords.setSize(this.bufferSize);
        AuditBuffer.logger.exiting(AuditBuffer.className, "saveBuffer");
    }
    
    public void setBufferSize(final int size) {
        if (this.currentSize >= size) {
            this.saveRecords();
            this.currentSize = 0;
        }
        this.bufferSize = size;
        this.bufferedRecords.setSize(this.bufferSize);
        AuditBuffer.logger.log(Level.FINEST, "Buffer size set to : {0}", new Integer(this.bufferSize));
    }
    
    static {
        AuditBuffer.className = AuditBuffer.class.getName();
        AuditBuffer.logger = Logger.getLogger(AuditBuffer.className);
    }
}
