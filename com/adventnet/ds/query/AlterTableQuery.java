package com.adventnet.ds.query;

import java.util.Properties;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.AllowedValues;
import java.util.List;

public interface AlterTableQuery
{
    public static final int ADD_COLUMN = 1;
    public static final int MODIFY_COLUMN = 2;
    public static final int DELETE_COLUMN = 3;
    public static final int ADD_UNIQUE_KEY = 4;
    public static final int DELETE_UNIQUE_KEY = 5;
    public static final int ADD_FOREIGN_KEY = 6;
    public static final int DELETE_FOREIGN_KEY = 7;
    @Deprecated
    public static final int DELETE_PRIMARY_KEY = 8;
    @Deprecated
    public static final int ADD_PRIMARY_KEY = 9;
    public static final int ADD_INDEX = 10;
    public static final int DELETE_INDEX = 11;
    public static final int RENAME_COLUMN = 12;
    public static final int RENAME_TABLE = 13;
    public static final int MODIFY_FOREIGN_KEY = 14;
    public static final int MODIFY_UNIQUE_KEY = 15;
    public static final int MODIFY_INDEX = 16;
    public static final int MODIFY_PRIMARY_KEY = 17;
    public static final int MODIFY_TABLE_ATTRIBUTES = 18;
    public static final int ADD_DYNAMIC_COLUMN = 19;
    public static final int DELETE_DYNAMIC_COLUMN = 20;
    public static final int MODIFY_DYNAMIC_COLUMN = 21;
    public static final int RENAME_DYNAMIC_COLUMN = 22;
    
    @Deprecated
    int getOperationType();
    
    String getTableName();
    
    @Deprecated
    String getColumnName();
    
    @Deprecated
    String getNewColumnName();
    
    @Deprecated
    String getDescription();
    
    @Deprecated
    String getDataType();
    
    @Deprecated
    Object getDefaultValue();
    
    @Deprecated
    int getMaxLength();
    
    @Deprecated
    boolean isNullable();
    
    @Deprecated
    boolean isUnique();
    
    boolean isValid();
    
    @Deprecated
    String getConstraintName();
    
    @Deprecated
    int getFKConstraint();
    
    @Deprecated
    List<String> getFKLocalColumns();
    
    @Deprecated
    List<String> getFKReferenceColumns();
    
    @Deprecated
    String getFKMasterTableName();
    
    @Deprecated
    List<String> getUniqueCols();
    
    @Deprecated
    boolean isBidirectional();
    
    @Deprecated
    List<String> getIndexColumns();
    
    @Deprecated
    List<String> getPKColumns();
    
    @Deprecated
    AllowedValues getAllowedValues();
    
    @Deprecated
    String getDisplayName();
    
    @Deprecated
    UniqueValueGeneration getUniqueValueGeneration();
    
    @Deprecated
    void setPKColumns(final List<String> p0);
    
    @Deprecated
    void setIndexColumns(final List<String> p0);
    
    @Deprecated
    void setColumnName(final String p0);
    
    @Deprecated
    void setNewColumnName(final String p0);
    
    @Deprecated
    void setDescription(final String p0);
    
    @Deprecated
    void setMaxLength(final int p0);
    
    @Deprecated
    void setDataType(final String p0);
    
    @Deprecated
    void setNullable(final boolean p0);
    
    @Deprecated
    void setAllowedValues(final AllowedValues p0);
    
    @Deprecated
    void setDisplayName(final String p0);
    
    @Deprecated
    void setUniqueValueGeneration(final UniqueValueGeneration p0);
    
    @Deprecated
    void setUnique(final boolean p0);
    
    @Deprecated
    void setDefaultValue(final Object p0);
    
    @Deprecated
    void setUniqueCols(final List<String> p0);
    
    @Deprecated
    void setConstraintName(final String p0);
    
    @Deprecated
    void setFKConstraint(final int p0);
    
    @Deprecated
    void setFKLocalColumns(final List<String> p0);
    
    @Deprecated
    void setFKReferenceColumns(final List<String> p0);
    
    @Deprecated
    void setFKMasterTableName(final String p0);
    
    @Deprecated
    void setBidirectional(final boolean p0);
    
    void validate() throws QueryConstructionException;
    
    void setIsExecutable(final boolean p0);
    
    boolean isExecutable();
    
    @Deprecated
    int getPrecision();
    
    @Deprecated
    void setPrecision(final int p0);
    
    void addColumn(final ColumnDefinition p0) throws QueryConstructionException;
    
    void addColumn(final ColumnDefinition p0, final String p1) throws QueryConstructionException;
    
    void renameColumn(final String p0, final String p1) throws QueryConstructionException;
    
    void removeColumn(final String p0) throws QueryConstructionException;
    
    void removeColumn(final String p0, final boolean p1) throws QueryConstructionException;
    
    void modifyColumn(final String p0, final ColumnDefinition p1) throws QueryConstructionException;
    
    void modifyColumn(final String p0, final ColumnDefinition p1, final String p2) throws QueryConstructionException;
    
    void modifyPrimaryKey(final String p0, final PrimaryKeyDefinition p1) throws QueryConstructionException;
    
    void modifyForeignKey(final ForeignKeyDefinition p0);
    
    void modifyUniqueKey(final UniqueKeyDefinition p0);
    
    void modifyIndex(final IndexDefinition p0);
    
    void addUniqueKey(final UniqueKeyDefinition p0) throws QueryConstructionException;
    
    void removeUniqueKey(final String p0) throws QueryConstructionException;
    
    void addForeignKey(final ForeignKeyDefinition p0) throws QueryConstructionException;
    
    void removeForeignKey(final String p0) throws QueryConstructionException;
    
    void addIndex(final IndexDefinition p0) throws QueryConstructionException;
    
    void dropIndex(final String p0) throws QueryConstructionException;
    
    void renameTable(final String p0) throws QueryConstructionException;
    
    List<AlterOperation> getAlterOperations();
    
    void setRevertFlag(final boolean p0);
    
    boolean isRevert();
    
    void setTableDefinition(final TableDefinition p0);
    
    TableDefinition getTableDefinition();
    
    void modifyTableAttributes(final Properties p0);
    
    void addPrimaryKey(final PrimaryKeyDefinition p0) throws QueryConstructionException;
    
    void removePrimaryKey(final String p0) throws QueryConstructionException;
    
    void addDynamicColumn(final ColumnDefinition p0) throws QueryConstructionException;
    
    void modifyDynamicColumn(final String p0, final ColumnDefinition p1) throws QueryConstructionException;
    
    void renameDynamicColumn(final String p0, final String p1) throws QueryConstructionException;
    
    void removeDynamicColumn(final String p0) throws QueryConstructionException;
    
    void removeDynamicColumn(final String p0, final boolean p1) throws QueryConstructionException;
}
