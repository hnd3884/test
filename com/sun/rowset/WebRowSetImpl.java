package com.sun.rowset;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import javax.sql.rowset.spi.SyncFactory;
import java.io.IOException;
import java.util.Hashtable;
import java.sql.SQLException;
import javax.sql.rowset.spi.SyncProvider;
import com.sun.rowset.internal.WebRowSetXmlWriter;
import com.sun.rowset.internal.WebRowSetXmlReader;
import javax.sql.rowset.WebRowSet;

public class WebRowSetImpl extends CachedRowSetImpl implements WebRowSet
{
    private WebRowSetXmlReader xmlReader;
    private WebRowSetXmlWriter xmlWriter;
    private int curPosBfrWrite;
    private SyncProvider provider;
    static final long serialVersionUID = -8771775154092422943L;
    
    public WebRowSetImpl() throws SQLException {
        this.xmlReader = new WebRowSetXmlReader();
        this.xmlWriter = new WebRowSetXmlWriter();
    }
    
    public WebRowSetImpl(final Hashtable hashtable) throws SQLException {
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        if (hashtable == null) {
            throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.nullhash").toString());
        }
        this.provider = SyncFactory.getInstance(hashtable.get("rowset.provider.classname"));
    }
    
    @Override
    public void writeXml(final ResultSet set, final Writer writer) throws SQLException {
        this.populate(set);
        this.curPosBfrWrite = this.getRow();
        this.writeXml(writer);
    }
    
    @Override
    public void writeXml(final Writer writer) throws SQLException {
        if (this.xmlWriter != null) {
            this.curPosBfrWrite = this.getRow();
            this.xmlWriter.writeXML(this, writer);
            return;
        }
        throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
    }
    
    @Override
    public void readXml(final Reader reader) throws SQLException {
        try {
            if (reader == null) {
                throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
            }
            this.xmlReader.readXML(this, reader);
            if (this.curPosBfrWrite == 0) {
                this.beforeFirst();
            }
            else {
                this.absolute(this.curPosBfrWrite);
            }
        }
        catch (final Exception ex) {
            throw new SQLException(ex.getMessage());
        }
    }
    
    @Override
    public void readXml(final InputStream inputStream) throws SQLException, IOException {
        if (inputStream != null) {
            this.xmlReader.readXML(this, inputStream);
            if (this.curPosBfrWrite == 0) {
                this.beforeFirst();
            }
            else {
                this.absolute(this.curPosBfrWrite);
            }
            return;
        }
        throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
    }
    
    @Override
    public void writeXml(final OutputStream outputStream) throws SQLException, IOException {
        if (this.xmlWriter != null) {
            this.curPosBfrWrite = this.getRow();
            this.xmlWriter.writeXML(this, outputStream);
            return;
        }
        throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
    }
    
    @Override
    public void writeXml(final ResultSet set, final OutputStream outputStream) throws SQLException, IOException {
        this.populate(set);
        this.curPosBfrWrite = this.getRow();
        this.writeXml(outputStream);
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
