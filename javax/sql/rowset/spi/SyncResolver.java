package javax.sql.rowset.spi;

import java.sql.SQLException;
import javax.sql.RowSet;

public interface SyncResolver extends RowSet
{
    public static final int UPDATE_ROW_CONFLICT = 0;
    public static final int DELETE_ROW_CONFLICT = 1;
    public static final int INSERT_ROW_CONFLICT = 2;
    public static final int NO_ROW_CONFLICT = 3;
    
    int getStatus();
    
    Object getConflictValue(final int p0) throws SQLException;
    
    Object getConflictValue(final String p0) throws SQLException;
    
    void setResolvedValue(final int p0, final Object p1) throws SQLException;
    
    void setResolvedValue(final String p0, final Object p1) throws SQLException;
    
    boolean nextConflict() throws SQLException;
    
    boolean previousConflict() throws SQLException;
}
