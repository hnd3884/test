package javax.sql.rowset.spi;

import java.sql.SQLException;
import java.io.Reader;
import javax.sql.rowset.WebRowSet;
import javax.sql.RowSetReader;

public interface XmlReader extends RowSetReader
{
    void readXML(final WebRowSet p0, final Reader p1) throws SQLException;
}
