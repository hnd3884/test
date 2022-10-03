package com.sun.rowset.providers;

import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.spi.SyncProviderException;
import java.sql.SQLException;
import java.io.IOException;
import javax.sql.rowset.spi.XmlWriter;
import javax.sql.rowset.spi.XmlReader;
import com.sun.rowset.JdbcRowSetResourceBundle;
import javax.sql.rowset.spi.SyncProvider;

public final class RIXMLProvider extends SyncProvider
{
    private String providerID;
    private String vendorName;
    private String versionNumber;
    private JdbcRowSetResourceBundle resBundle;
    private XmlReader xmlReader;
    private XmlWriter xmlWriter;
    
    public RIXMLProvider() {
        this.providerID = "com.sun.rowset.providers.RIXMLProvider";
        this.vendorName = "Oracle Corporation";
        this.versionNumber = "1.0";
        this.providerID = this.getClass().getName();
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
    
    public void setXmlReader(final XmlReader xmlReader) throws SQLException {
        this.xmlReader = xmlReader;
    }
    
    public void setXmlWriter(final XmlWriter xmlWriter) throws SQLException {
        this.xmlWriter = xmlWriter;
    }
    
    public XmlReader getXmlReader() throws SQLException {
        return this.xmlReader;
    }
    
    public XmlWriter getXmlWriter() throws SQLException {
        return this.xmlWriter;
    }
    
    @Override
    public int getProviderGrade() {
        return 1;
    }
    
    @Override
    public int supportsUpdatableView() {
        return 6;
    }
    
    @Override
    public int getDataSourceLock() throws SyncProviderException {
        return 1;
    }
    
    @Override
    public void setDataSourceLock(final int n) throws SyncProviderException {
        throw new UnsupportedOperationException(this.resBundle.handleGetObject("rixml.unsupp").toString());
    }
    
    @Override
    public RowSetWriter getRowSetWriter() {
        return null;
    }
    
    @Override
    public RowSetReader getRowSetReader() {
        return null;
    }
    
    @Override
    public String getVersion() {
        return this.versionNumber;
    }
    
    @Override
    public String getVendor() {
        return this.vendorName;
    }
}
