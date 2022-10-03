package java.sql;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import java.io.Reader;
import java.io.OutputStream;
import java.io.InputStream;

public interface SQLXML
{
    void free() throws SQLException;
    
    InputStream getBinaryStream() throws SQLException;
    
    OutputStream setBinaryStream() throws SQLException;
    
    Reader getCharacterStream() throws SQLException;
    
    Writer setCharacterStream() throws SQLException;
    
    String getString() throws SQLException;
    
    void setString(final String p0) throws SQLException;
    
     <T extends Source> T getSource(final Class<T> p0) throws SQLException;
    
     <T extends Result> T setResult(final Class<T> p0) throws SQLException;
}
