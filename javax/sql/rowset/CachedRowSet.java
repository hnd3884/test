package javax.sql.rowset;

import javax.sql.RowSetEvent;
import java.sql.Savepoint;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.spi.SyncProvider;
import java.util.Collection;
import javax.sql.rowset.spi.SyncProviderException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.sql.RowSet;

public interface CachedRowSet extends RowSet, Joinable
{
    @Deprecated
    public static final boolean COMMIT_ON_ACCEPT_CHANGES = true;
    
    void populate(final ResultSet p0) throws SQLException;
    
    void execute(final Connection p0) throws SQLException;
    
    void acceptChanges() throws SyncProviderException;
    
    void acceptChanges(final Connection p0) throws SyncProviderException;
    
    void restoreOriginal() throws SQLException;
    
    void release() throws SQLException;
    
    void undoDelete() throws SQLException;
    
    void undoInsert() throws SQLException;
    
    void undoUpdate() throws SQLException;
    
    boolean columnUpdated(final int p0) throws SQLException;
    
    boolean columnUpdated(final String p0) throws SQLException;
    
    Collection<?> toCollection() throws SQLException;
    
    Collection<?> toCollection(final int p0) throws SQLException;
    
    Collection<?> toCollection(final String p0) throws SQLException;
    
    SyncProvider getSyncProvider() throws SQLException;
    
    void setSyncProvider(final String p0) throws SQLException;
    
    int size();
    
    void setMetaData(final RowSetMetaData p0) throws SQLException;
    
    ResultSet getOriginal() throws SQLException;
    
    ResultSet getOriginalRow() throws SQLException;
    
    void setOriginalRow() throws SQLException;
    
    String getTableName() throws SQLException;
    
    void setTableName(final String p0) throws SQLException;
    
    int[] getKeyColumns() throws SQLException;
    
    void setKeyColumns(final int[] p0) throws SQLException;
    
    RowSet createShared() throws SQLException;
    
    CachedRowSet createCopy() throws SQLException;
    
    CachedRowSet createCopySchema() throws SQLException;
    
    CachedRowSet createCopyNoConstraints() throws SQLException;
    
    RowSetWarning getRowSetWarnings() throws SQLException;
    
    boolean getShowDeleted() throws SQLException;
    
    void setShowDeleted(final boolean p0) throws SQLException;
    
    void commit() throws SQLException;
    
    void rollback() throws SQLException;
    
    void rollback(final Savepoint p0) throws SQLException;
    
    void rowSetPopulated(final RowSetEvent p0, final int p1) throws SQLException;
    
    void populate(final ResultSet p0, final int p1) throws SQLException;
    
    void setPageSize(final int p0) throws SQLException;
    
    int getPageSize();
    
    boolean nextPage() throws SQLException;
    
    boolean previousPage() throws SQLException;
}
