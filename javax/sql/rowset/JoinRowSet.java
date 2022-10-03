package javax.sql.rowset;

import java.util.Collection;
import javax.sql.RowSet;
import java.sql.SQLException;

public interface JoinRowSet extends WebRowSet
{
    public static final int CROSS_JOIN = 0;
    public static final int INNER_JOIN = 1;
    public static final int LEFT_OUTER_JOIN = 2;
    public static final int RIGHT_OUTER_JOIN = 3;
    public static final int FULL_JOIN = 4;
    
    void addRowSet(final Joinable p0) throws SQLException;
    
    void addRowSet(final RowSet p0, final int p1) throws SQLException;
    
    void addRowSet(final RowSet p0, final String p1) throws SQLException;
    
    void addRowSet(final RowSet[] p0, final int[] p1) throws SQLException;
    
    void addRowSet(final RowSet[] p0, final String[] p1) throws SQLException;
    
    Collection<?> getRowSets() throws SQLException;
    
    String[] getRowSetNames() throws SQLException;
    
    CachedRowSet toCachedRowSet() throws SQLException;
    
    boolean supportsCrossJoin();
    
    boolean supportsInnerJoin();
    
    boolean supportsLeftOuterJoin();
    
    boolean supportsRightOuterJoin();
    
    boolean supportsFullJoin();
    
    void setJoinType(final int p0) throws SQLException;
    
    String getWhereClause() throws SQLException;
    
    int getJoinType() throws SQLException;
}
