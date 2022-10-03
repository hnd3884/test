package javax.sql.rowset.spi;

import java.sql.SQLException;
import java.io.Writer;
import javax.sql.rowset.WebRowSet;
import javax.sql.RowSetWriter;

public interface XmlWriter extends RowSetWriter
{
    void writeXML(final WebRowSet p0, final Writer p1) throws SQLException;
}
