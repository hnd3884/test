package javax.sql.rowset;

import java.io.OutputStream;
import java.io.Writer;
import java.sql.ResultSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.io.Reader;

public interface WebRowSet extends CachedRowSet
{
    public static final String PUBLIC_XML_SCHEMA = "--//Oracle Corporation//XSD Schema//EN";
    public static final String SCHEMA_SYSTEM_ID = "http://java.sun.com/xml/ns/jdbc/webrowset.xsd";
    
    void readXml(final Reader p0) throws SQLException;
    
    void readXml(final InputStream p0) throws SQLException, IOException;
    
    void writeXml(final ResultSet p0, final Writer p1) throws SQLException;
    
    void writeXml(final ResultSet p0, final OutputStream p1) throws SQLException, IOException;
    
    void writeXml(final Writer p0) throws SQLException;
    
    void writeXml(final OutputStream p0) throws SQLException, IOException;
}
