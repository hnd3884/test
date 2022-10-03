package javax.sql.rowset;

import java.sql.SQLException;

public interface Joinable
{
    void setMatchColumn(final int p0) throws SQLException;
    
    void setMatchColumn(final int[] p0) throws SQLException;
    
    void setMatchColumn(final String p0) throws SQLException;
    
    void setMatchColumn(final String[] p0) throws SQLException;
    
    int[] getMatchColumnIndexes() throws SQLException;
    
    String[] getMatchColumnNames() throws SQLException;
    
    void unsetMatchColumn(final int p0) throws SQLException;
    
    void unsetMatchColumn(final int[] p0) throws SQLException;
    
    void unsetMatchColumn(final String p0) throws SQLException;
    
    void unsetMatchColumn(final String[] p0) throws SQLException;
}
