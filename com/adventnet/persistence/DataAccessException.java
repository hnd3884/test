package com.adventnet.persistence;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataAccessException extends Exception
{
    public static final int UNDEFINED_ERROR_CODE = -9999;
    private int errorCode;
    private String tableName;
    private String[] columnNames;
    private int[] updateCounts;
    private ArrayList errRows;
    private String errorString;
    
    public DataAccessException() {
        this.errorCode = -9999;
        this.tableName = null;
        this.columnNames = null;
        this.updateCounts = null;
        this.errRows = null;
        this.errorString = null;
    }
    
    public DataAccessException(final String message) {
        super(message);
        this.errorCode = -9999;
        this.tableName = null;
        this.columnNames = null;
        this.updateCounts = null;
        this.errRows = null;
        this.errorString = null;
    }
    
    public DataAccessException(final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = -9999;
        this.tableName = null;
        this.columnNames = null;
        this.updateCounts = null;
        this.errRows = null;
        this.errorString = null;
        this.init(cause);
    }
    
    public DataAccessException(final Throwable cause) {
        super(cause);
        this.errorCode = -9999;
        this.tableName = null;
        this.columnNames = null;
        this.updateCounts = null;
        this.errRows = null;
        this.errorString = null;
        this.init(cause);
    }
    
    private void init(final Throwable cause) {
        if (cause instanceof SQLException) {
            final SQLException sqle = (SQLException)cause;
            this.setErrorCode(sqle.getErrorCode());
        }
    }
    
    public int[] getUpdateCounts() {
        return this.updateCounts;
    }
    
    public void setUpdateCounts(final int[] updateCount) {
        this.updateCounts = updateCount;
    }
    
    public List getErrRows() {
        return this.errRows;
    }
    
    public String getErrRowsAsString() {
        final StringBuilder buff = new StringBuilder();
        if (this.errRows != null) {
            for (int size = this.errRows.size(), j = 0; j < size; ++j) {
                final Row temp = this.errRows.get(j);
                buff.append(" ");
                buff.append(temp);
                buff.append("\n");
            }
        }
        return buff.toString();
    }
    
    public void addErrRow(final Row row) {
        if (row == null) {
            return;
        }
        if (this.errRows == null) {
            this.errRows = new ArrayList();
        }
        this.errRows.add(row);
    }
    
    public void setErrRows(final Iterator rows) {
        if (rows == null || this.updateCounts == null) {
            return;
        }
        for (int i = 0; rows.hasNext() && i < this.updateCounts.length; ++i) {
            final Row r1 = rows.next();
            if (this.updateCounts[i] == -3) {
                this.addErrRow(r1);
            }
        }
    }
    
    @Override
    public Throwable initCause(final Throwable cause) {
        this.init(cause);
        return super.initCause(cause);
    }
    
    void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
        if (this.errorCode != -9999) {
            final Map<Object, ErrorCodes.AdventNetErrorCode> map = ErrorCodes.getErrorCodeMap("AdventNetErrorCode");
            if (map != null) {
                final ErrorCodes.AdventNetErrorCode ec = map.get(this.errorCode);
                if (ec != null) {
                    this.errorString = ec.errorString;
                }
            }
        }
    }
    
    public String getErrorString() {
        return this.errorString;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    void setColumnNames(final String[] columnNames) {
        this.columnNames = columnNames;
    }
    
    public String[] getColumnNames() {
        return this.columnNames;
    }
    
    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (this.tableName != null) {
            message = "[" + this.tableName + "] " + message;
        }
        return message;
    }
    
    @Override
    public void printStackTrace() {
        Throwable cause = this.getCause();
        if (cause != null && cause instanceof SQLException) {
            cause.printStackTrace();
            cause = ((SQLException)cause).getNextException();
            if (cause != null) {
                cause.printStackTrace();
            }
        }
        else {
            super.printStackTrace();
        }
    }
}
