package com.adventnet.db.adapter;

import java.util.logging.Level;
import java.sql.Blob;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;

public class BulkInsertObject
{
    private Object bulkObject;
    private String sql;
    private List<String> colNames;
    private List<String> colTypeNames;
    private List<Integer> colTypes;
    private AtomicBoolean isReadyToWrite;
    private Exception exceptionObj;
    public AtomicBoolean exceptionCaused;
    private static final Logger OUT;
    
    public BulkInsertObject() {
        this.sql = "";
        this.isReadyToWrite = new AtomicBoolean(Boolean.FALSE);
        this.exceptionObj = null;
        this.exceptionCaused = new AtomicBoolean(Boolean.FALSE);
    }
    
    public BulkInsertObject(final Object bulkObject) {
        this.sql = "";
        this.isReadyToWrite = new AtomicBoolean(Boolean.FALSE);
        this.exceptionObj = null;
        this.exceptionCaused = new AtomicBoolean(Boolean.FALSE);
        this.bulkObject = bulkObject;
    }
    
    public void setBulkObject(final Object bulkObject) {
        this.bulkObject = bulkObject;
    }
    
    public Object getBulkObject() {
        return this.bulkObject;
    }
    
    public void setIsReadyToWrite(final boolean isReadyToWrite) {
        this.isReadyToWrite.set(isReadyToWrite);
    }
    
    public boolean isReadyToWrite() {
        return this.isReadyToWrite.get();
    }
    
    public void setColNames(final List<String> colNames) {
        this.colNames = colNames;
    }
    
    public void setColTypeNames(final List<String> colTypeNames) {
        this.colTypeNames = colTypeNames;
    }
    
    public void setColTypes(final List<Integer> colTypes) {
        this.colTypes = colTypes;
    }
    
    public void setSQL(final String sql) {
        this.sql = sql;
    }
    
    public String getSQL() {
        return this.sql;
    }
    
    public List<String> getColNames() {
        return this.colNames;
    }
    
    public List<String> getColTypeNames() {
        return this.colTypeNames;
    }
    
    public List<Integer> getColTypes() {
        return this.colTypes;
    }
    
    public void checkDataType(final int columnPosition, final Object value) throws Exception {
        try {
            Blob blobVal = null;
            byte[] byteVal = null;
            Object validateInput = null;
            if (value instanceof Blob) {
                blobVal = (Blob)value;
            }
            else if (value instanceof byte[]) {
                byteVal = (byte[])value;
            }
            switch (this.colTypes.get(columnPosition - 1)) {
                case 4: {
                    validateInput = ((value instanceof Blob) ? this.parseInt(new String(blobVal.getBytes(1L, (int)blobVal.length()))) : ((value instanceof byte[]) ? this.parseInt(new String(byteVal)) : ((value instanceof String) ? this.parseInt(String.valueOf(value)) : ((Number)value).intValue())));
                    break;
                }
                case -5: {
                    validateInput = ((value instanceof Blob) ? this.parseLong(new String(blobVal.getBytes(1L, (int)blobVal.length()))) : ((value instanceof byte[]) ? this.parseLong(new String(byteVal)) : ((value instanceof String) ? this.parseLong(String.valueOf(value)) : ((Number)value).longValue())));
                    break;
                }
                case 6: {
                    validateInput = ((value instanceof Blob) ? this.parseFloat(new String(blobVal.getBytes(1L, (int)blobVal.length()))) : ((value instanceof byte[]) ? this.parseFloat(new String(byteVal)) : ((value instanceof String) ? this.parseFloat(String.valueOf(value)) : ((Number)value).floatValue())));
                    break;
                }
                case 8: {
                    validateInput = ((value instanceof Blob) ? this.parseDouble(new String(blobVal.getBytes(1L, (int)blobVal.length()))) : ((value instanceof byte[]) ? this.parseDouble(new String(byteVal)) : ((value instanceof String) ? this.parseDouble(String.valueOf(value)) : ((Number)value).doubleValue())));
                    break;
                }
            }
        }
        catch (final Exception e) {
            BulkInsertObject.OUT.log(Level.INFO, "Invalid data set in the column :: [{0}]", columnPosition);
            this.exceptionCaused.getAndSet(true);
            throw e;
        }
    }
    
    private int parseInt(final String str) throws NumberFormatException {
        return Integer.parseInt(String.valueOf(str));
    }
    
    private long parseLong(final String str) throws NumberFormatException {
        return Long.parseLong(str);
    }
    
    private float parseFloat(final String str) throws NumberFormatException {
        return Float.parseFloat(String.valueOf(str));
    }
    
    private double parseDouble(final String str) throws NumberFormatException {
        return Double.parseDouble(String.valueOf(str));
    }
    
    public Exception getError() {
        return this.exceptionObj;
    }
    
    public synchronized void setError(final Exception exceptionObj) {
        if (null == this.exceptionObj) {
            this.exceptionObj = exceptionObj;
        }
    }
    
    public void addColName(final String columnName) {
        if (this.colNames != null) {
            this.colNames.add(columnName);
            return;
        }
        throw new IllegalArgumentException("ColumnNames List not initialized. Call setColNames API first.");
    }
    
    public void addColTypeName(final String dataType) {
        if (this.colTypeNames != null) {
            this.colTypeNames.add(dataType);
            return;
        }
        throw new IllegalArgumentException("ColumnTypeNames List not initialized. Call setColTypeNames API first.");
    }
    
    public void addColType(final int sqlType) {
        if (this.colTypes != null) {
            this.colTypes.add(sqlType);
            return;
        }
        throw new IllegalArgumentException("ColumnTypes List not initialized Call setColTypes API first.");
    }
    
    static {
        OUT = Logger.getLogger(BulkInsertObject.class.getName());
    }
}
