package com.adventnet.db.adapter;

import java.util.List;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.ds.query.AlterTableQuery;
import java.sql.Connection;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.UpdateQuery;
import java.sql.SQLException;
import java.util.Map;
import java.sql.PreparedStatement;

public interface DCAdapter
{
    void setValue(final PreparedStatement p0, final int p1, final Map<String, Integer> p2, final Map<String, Object> p3) throws SQLException;
    
    UpdateQuery getModifiedUpdateQuery(final UpdateQuery p0) throws MetaDataException;
    
    void addDynamicColumn(final Connection p0, final AlterTableQuery p1) throws SQLException;
    
    void deleteDynamicColumn(final Connection p0, final AlterTableQuery p1) throws SQLException;
    
    void validateVersion(final Connection p0);
    
    void modifyDynamicColumn(final Connection p0, final AlterTableQuery p1) throws SQLException;
    
    void renameDynamicColumn(final Connection p0, final AlterTableQuery p1) throws SQLException;
    
    String getDataType(final String p0, final String p1) throws MetaDataException;
    
    void deleteAllDynamicColumns(final Connection p0, final String p1) throws SQLException;
    
    void preAlterTable(final Connection p0, final AlterTableQuery p1) throws SQLException;
    
    void initDBAdapter(final DBAdapter p0);
    
    void loadDynamicColumnDetails(final BulkInsertObject p0, final BulkLoad p1, final List<String> p2, final int[] p3, final int p4) throws MetaDataException;
    
    Object getModifiedObjectForDynamicColumn(final Object p0, final String p1, final Object p2) throws Exception;
}
