package com.sun.rowset.providers;

import java.io.ObjectInputStream;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.RowSetReader;
import java.sql.SQLException;
import javax.sql.RowSetWriter;
import java.io.IOException;
import com.sun.rowset.JdbcRowSetResourceBundle;
import com.sun.rowset.internal.CachedRowSetWriter;
import com.sun.rowset.internal.CachedRowSetReader;
import java.io.Serializable;
import javax.sql.rowset.spi.SyncProvider;

public final class RIOptimisticProvider extends SyncProvider implements Serializable
{
    private CachedRowSetReader reader;
    private CachedRowSetWriter writer;
    private String providerID;
    private String vendorName;
    private String versionNumber;
    private JdbcRowSetResourceBundle resBundle;
    static final long serialVersionUID = -3143367176751761936L;
    
    public RIOptimisticProvider() {
        this.providerID = "com.sun.rowset.providers.RIOptimisticProvider";
        this.vendorName = "Oracle Corporation";
        this.versionNumber = "1.0";
        this.providerID = this.getClass().getName();
        this.reader = new CachedRowSetReader();
        this.writer = new CachedRowSetWriter();
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public String getProviderID() {
        return this.providerID;
    }
    
    @Override
    public RowSetWriter getRowSetWriter() {
        try {
            this.writer.setReader(this.reader);
        }
        catch (final SQLException ex) {}
        return this.writer;
    }
    
    @Override
    public RowSetReader getRowSetReader() {
        return this.reader;
    }
    
    @Override
    public int getProviderGrade() {
        return 2;
    }
    
    @Override
    public void setDataSourceLock(final int n) throws SyncProviderException {
        if (n != 1) {
            throw new SyncProviderException(this.resBundle.handleGetObject("riop.locking").toString());
        }
    }
    
    @Override
    public int getDataSourceLock() throws SyncProviderException {
        return 1;
    }
    
    @Override
    public int supportsUpdatableView() {
        return 6;
    }
    
    @Override
    public String getVersion() {
        return this.versionNumber;
    }
    
    @Override
    public String getVendor() {
        return this.vendorName;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
