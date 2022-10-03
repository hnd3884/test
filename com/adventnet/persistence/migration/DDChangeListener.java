package com.adventnet.persistence.migration;

import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.AlterTableQuery;

public interface DDChangeListener
{
    boolean preInvokeForAlterTable(final AlterTableQuery p0) throws Exception;
    
    void postInvokeForAlterTable(final AlterTableQuery p0) throws Exception;
    
    boolean preInvokeForCreateTable(final TableDefinition p0) throws Exception;
    
    void postInvokeForCreateTable(final TableDefinition p0) throws Exception;
    
    boolean preInvokeForDropTable(final TableDefinition p0) throws Exception;
    
    void postInvokeForDropTable(final TableDefinition p0) throws Exception;
    
    MigrationType getMigrationType();
    
    void setMigrationType(final MigrationType p0);
    
    void handleExceptionForAlterTable(final AlterTableQuery p0, final Exception p1) throws Exception;
    
    void handleExceptionForCreateTable(final TableDefinition p0, final Exception p1) throws Exception;
    
    void handleExceptionForDropTable(final TableDefinition p0, final Exception p1) throws Exception;
    
    public enum MigrationType
    {
        INSTALL, 
        INSTALL_FAILURE, 
        UNINSTALL, 
        UNINSTALL_FAILURE;
    }
}
