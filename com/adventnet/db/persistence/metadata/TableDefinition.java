package com.adventnet.db.persistence.metadata;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Collections;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;

public class TableDefinition implements Serializable, Cloneable
{
    private static final long serialVersionUID = 5097156068917996073L;
    private boolean template;
    private List<String> columnNames;
    private List<String> dynamicColumnNames;
    private List<String> encryptedColumnNames;
    private List<String> physicalColumnNames;
    private List<ColumnDefinition> columnList;
    private HashMap<String, ColumnDefinition> columnsMap;
    private PrimaryKeyDefinition primaryKey;
    private List<ForeignKeyDefinition> foreignKeyList;
    private HashMap<String, ForeignKeyDefinition> fkMap;
    private List<UniqueKeyDefinition> uniqueKeyList;
    private List<IndexDefinition> indexList;
    private String tableName;
    private String displayName;
    private boolean hasBDFK;
    private boolean bdfkImpact;
    private Long tableID;
    private String dcType;
    private List<ColumnDefinition> dynamicColumnList;
    int tableState;
    private boolean isValidated;
    private int[] uvhColumnIndices;
    private long modifiedTime;
    private String template_instance_pattern;
    boolean isSystem;
    boolean createTable;
    private String dirtyWriteCheckColumnNamesStr;
    private List dirtyWriteCheckColNamesExcludingPK;
    private List dirtyWriteCheckColumnNames;
    private String moduleName;
    private String description;
    private int[] keyIndices;
    private long metaDigest;
    private boolean isParticipatedInArchival;
    
    public void setTemplateInstancePatternName(final String name) {
        this.template_instance_pattern = name;
    }
    
    public String getTemplateInstancePatternName() {
        return this.template_instance_pattern;
    }
    
    public void setTableID(final Long id) {
        this.tableID = id;
    }
    
    @Deprecated
    public Long getTableID() {
        return this.tableID;
    }
    
    public Object clone() throws CloneNotSupportedException {
        if (!this.foreignKeyList.isEmpty() && !this.isTemplate() && !this.isParticipatedInArchival()) {
            throw new CloneNotSupportedException("Has FK and not a template. Hence clone Not supported.");
        }
        final TableDefinition copy = (TableDefinition)super.clone();
        copy.columnList = new ArrayList<ColumnDefinition>();
        copy.columnNames = new ArrayList<String>();
        copy.columnsMap = new HashMap<String, ColumnDefinition>();
        copy.dynamicColumnList = new ArrayList<ColumnDefinition>();
        copy.dynamicColumnNames = new ArrayList<String>();
        copy.physicalColumnNames = new ArrayList<String>();
        copy.dcType = this.dcType;
        for (final ColumnDefinition colDefn : this.columnList) {
            final ColumnDefinition _colDefn = (ColumnDefinition)colDefn.clone();
            copy.columnList.add(_colDefn);
            copy.columnNames.add(_colDefn.getColumnName());
            copy.columnsMap.put(_colDefn.getColumnName(), _colDefn);
            if (_colDefn.isDynamic()) {
                copy.dynamicColumnList.add(_colDefn);
                copy.dynamicColumnNames.add(_colDefn.getColumnName());
                copy.physicalColumnNames.add(_colDefn.getPhysicalColumn());
            }
        }
        if (this.encryptedColumnNames != null) {
            copy.encryptedColumnNames = new ArrayList<String>(this.encryptedColumnNames);
        }
        if (this.uvhColumnIndices != null) {
            copy.uvhColumnIndices = new int[this.uvhColumnIndices.length];
            System.arraycopy(this.uvhColumnIndices, 0, copy.uvhColumnIndices, 0, this.uvhColumnIndices.length);
        }
        copy.primaryKey = (PrimaryKeyDefinition)this.primaryKey.clone();
        copy.keyIndices = new int[this.keyIndices.length];
        System.arraycopy(this.keyIndices, 0, copy.keyIndices, 0, this.keyIndices.length);
        if (this.indexList != null) {
            copy.indexList = new ArrayList<IndexDefinition>();
            for (final IndexDefinition indexDefn : this.indexList) {
                copy.indexList.add((IndexDefinition)indexDefn.clone());
            }
        }
        if (this.uniqueKeyList != null) {
            copy.uniqueKeyList = new ArrayList<UniqueKeyDefinition>();
            for (final UniqueKeyDefinition ukDefn : this.uniqueKeyList) {
                copy.uniqueKeyList.add((UniqueKeyDefinition)ukDefn.clone());
            }
        }
        if (this.foreignKeyList != null && this.foreignKeyList.size() > 0) {
            copy.foreignKeyList = new ArrayList<ForeignKeyDefinition>();
            for (final ForeignKeyDefinition fkDef : this.foreignKeyList) {
                copy.foreignKeyList.add((ForeignKeyDefinition)fkDef.clone());
            }
        }
        return copy;
    }
    
    public Object cloneWithoutFK() throws CloneNotSupportedException {
        final TableDefinition copy = (TableDefinition)super.clone();
        copy.columnList = new ArrayList<ColumnDefinition>();
        copy.columnNames = new ArrayList<String>();
        copy.columnsMap = new HashMap<String, ColumnDefinition>();
        copy.dynamicColumnList = new ArrayList<ColumnDefinition>();
        copy.dynamicColumnNames = new ArrayList<String>();
        copy.physicalColumnNames = new ArrayList<String>();
        copy.dcType = this.dcType;
        for (final ColumnDefinition colDefn : this.columnList) {
            final ColumnDefinition _colDefn = (ColumnDefinition)colDefn.clone();
            copy.columnList.add(_colDefn);
            copy.columnNames.add(_colDefn.getColumnName());
            copy.columnsMap.put(_colDefn.getColumnName(), _colDefn);
            if (_colDefn.isDynamic()) {
                copy.dynamicColumnList.add(_colDefn);
                copy.dynamicColumnNames.add(_colDefn.getColumnName());
                copy.physicalColumnNames.add(_colDefn.getPhysicalColumn());
            }
        }
        if (this.encryptedColumnNames != null) {
            copy.encryptedColumnNames = new ArrayList<String>(this.encryptedColumnNames);
        }
        copy.primaryKey = (PrimaryKeyDefinition)this.primaryKey.clone();
        copy.keyIndices = new int[this.keyIndices.length];
        System.arraycopy(this.keyIndices, 0, copy.keyIndices, 0, this.keyIndices.length);
        if (this.indexList != null) {
            copy.indexList = new ArrayList<IndexDefinition>();
            for (final IndexDefinition indexDefn : this.indexList) {
                copy.indexList.add((IndexDefinition)indexDefn.clone());
            }
        }
        if (this.uniqueKeyList != null) {
            copy.uniqueKeyList = new ArrayList<UniqueKeyDefinition>();
            for (final UniqueKeyDefinition ukDefn : this.uniqueKeyList) {
                copy.uniqueKeyList.add((UniqueKeyDefinition)ukDefn.clone());
            }
        }
        if (this.foreignKeyList != null && this.foreignKeyList.size() > 0) {
            copy.foreignKeyList = new ArrayList<ForeignKeyDefinition>();
            for (final ForeignKeyDefinition fkDef : this.foreignKeyList) {
                copy.removeForeignKey(fkDef.getName());
            }
        }
        return copy;
    }
    
    public long getModifiedTime() {
        return this.modifiedTime;
    }
    
    public void setModifiedTime(final long newModTime) {
        this.modifiedTime = newModTime;
    }
    
    public TableDefinition(final boolean isSystem) {
        this(isSystem, true);
    }
    
    public TableDefinition(final boolean isSystem, final boolean createTable) {
        this(isSystem, createTable, null);
    }
    
    public TableDefinition(final boolean isSystem, final boolean createTable, final String dirtyWriteCheckColNamesValue) {
        this.columnNames = null;
        this.dynamicColumnNames = null;
        this.encryptedColumnNames = null;
        this.physicalColumnNames = new ArrayList<String>();
        this.columnList = null;
        this.columnsMap = new HashMap<String, ColumnDefinition>();
        this.foreignKeyList = new ArrayList<ForeignKeyDefinition>();
        this.fkMap = new HashMap<String, ForeignKeyDefinition>();
        this.uniqueKeyList = null;
        this.indexList = null;
        this.hasBDFK = false;
        this.bdfkImpact = false;
        this.tableID = null;
        this.dcType = null;
        this.dynamicColumnList = null;
        this.tableState = 0;
        this.isValidated = false;
        this.uvhColumnIndices = null;
        this.modifiedTime = System.currentTimeMillis();
        this.template_instance_pattern = null;
        this.isSystem = false;
        this.createTable = true;
        this.dirtyWriteCheckColumnNamesStr = null;
        this.keyIndices = null;
        this.metaDigest = 0L;
        this.isParticipatedInArchival = false;
        this.isSystem = isSystem;
        this.createTable = createTable;
        this.dirtyWriteCheckColumnNamesStr = dirtyWriteCheckColNamesValue;
        if (this.dirtyWriteCheckColumnNamesStr != null && !"*".equals(this.dirtyWriteCheckColumnNamesStr)) {
            this.dirtyWriteCheckColNamesExcludingPK = new ArrayList(Arrays.asList(this.dirtyWriteCheckColumnNamesStr.split(",")));
            this.dirtyWriteCheckColumnNames = new ArrayList(this.dirtyWriteCheckColNamesExcludingPK);
        }
    }
    
    public boolean isDirtyWriteCheckColumnsDefined() {
        return this.dirtyWriteCheckColumnNamesStr != null;
    }
    
    public List<String> getPKExcludingDirtyWriteCheckColumnNames() {
        if (this.dirtyWriteCheckColumnNamesStr == null) {
            return null;
        }
        if (this.dirtyWriteCheckColNamesExcludingPK == null) {
            this.fillDirtyWriteCheckColumnNames();
        }
        return this.dirtyWriteCheckColNamesExcludingPK;
    }
    
    public List<String> getDirtyWriteCheckColumnNames() {
        if (this.dirtyWriteCheckColumnNamesStr != null && "*".equals(this.dirtyWriteCheckColumnNamesStr)) {
            return this.columnNames;
        }
        return this.dirtyWriteCheckColumnNames;
    }
    
    private void fillDirtyWriteCheckColumnNames() {
        List<String> colsExcludingPK = null;
        if ("*".equals(this.dirtyWriteCheckColumnNamesStr)) {
            colsExcludingPK = new ArrayList<String>(this.columnNames);
        }
        else {
            colsExcludingPK = new ArrayList<String>(this.dirtyWriteCheckColNamesExcludingPK);
        }
        colsExcludingPK.removeAll(this.primaryKey.getColumnList());
        this.dirtyWriteCheckColNamesExcludingPK = colsExcludingPK;
    }
    
    private void cleanDirtyWriteColumnNames() {
        if (this.dirtyWriteCheckColumnNamesStr != null && this.dirtyWriteCheckColumnNamesStr.equals("*")) {
            this.dirtyWriteCheckColNamesExcludingPK = null;
        }
    }
    
    public String getModuleName() {
        return this.moduleName;
    }
    
    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }
    
    public TableDefinition() {
        this.columnNames = null;
        this.dynamicColumnNames = null;
        this.encryptedColumnNames = null;
        this.physicalColumnNames = new ArrayList<String>();
        this.columnList = null;
        this.columnsMap = new HashMap<String, ColumnDefinition>();
        this.foreignKeyList = new ArrayList<ForeignKeyDefinition>();
        this.fkMap = new HashMap<String, ForeignKeyDefinition>();
        this.uniqueKeyList = null;
        this.indexList = null;
        this.hasBDFK = false;
        this.bdfkImpact = false;
        this.tableID = null;
        this.dcType = null;
        this.dynamicColumnList = null;
        this.tableState = 0;
        this.isValidated = false;
        this.uvhColumnIndices = null;
        this.modifiedTime = System.currentTimeMillis();
        this.template_instance_pattern = null;
        this.isSystem = false;
        this.createTable = true;
        this.dirtyWriteCheckColumnNamesStr = null;
        this.keyIndices = null;
        this.metaDigest = 0L;
        this.isParticipatedInArchival = false;
    }
    
    public boolean isTemplate() {
        return this.template;
    }
    
    public void setTemplate(final boolean template) {
        this.template = template;
    }
    
    public boolean isSystem() {
        return this.isSystem;
    }
    
    public void setCreateTable(final boolean createTable) {
        this.createTable = createTable;
    }
    
    public boolean creatable() {
        return this.createTable;
    }
    
    public int getState() {
        return this.tableState;
    }
    
    public boolean isValidated() {
        return this.isValidated;
    }
    
    public String getDisplayName() {
        if (this.displayName == null) {
            this.displayName = this.tableName;
        }
        return this.displayName;
    }
    
    public void setDisplayName(final String v) {
        this.displayName = v;
        ++this.tableState;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String v) {
        this.description = v;
        ++this.tableState;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
        ++this.tableState;
        this.isValidated = false;
    }
    
    public PrimaryKeyDefinition getPrimaryKey() {
        return this.primaryKey;
    }
    
    public void setPrimaryKey(final PrimaryKeyDefinition primaryKey) {
        (this.primaryKey = primaryKey).setTableName(this.tableName);
        ++this.tableState;
        this.isValidated = false;
        this.initKeyIndices();
        for (final ColumnDefinition cd : this.columnList) {
            cd.setKey(primaryKey.getColumnList().contains(cd.getColumnName()));
        }
        if (this.dirtyWriteCheckColumnNamesStr != null) {
            this.fillDirtyWriteCheckColumnNames();
        }
    }
    
    private void initKeyIndices() {
        if (this.primaryKey != null) {
            final List pkColumns = this.primaryKey.getColumnList();
            final int keySize = pkColumns.size();
            this.keyIndices = new int[keySize];
            for (int i = 0; i < keySize; ++i) {
                final String columnName = pkColumns.get(i);
                this.keyIndices[i] = this.columnNames.indexOf(columnName) + 1;
            }
        }
    }
    
    public List getForeignKeyList() {
        return (this.foreignKeyList.size() > 0) ? this.foreignKeyList : Collections.EMPTY_LIST;
    }
    
    public void addForeignKey(final ForeignKeyDefinition fkd) {
        final String fkName = fkd.getName();
        fkd.setSlaveTableName(this.tableName);
        if (this.foreignKeyList == null) {
            this.foreignKeyList = new ArrayList<ForeignKeyDefinition>(1);
        }
        if (fkd.isBidirectional()) {
            this.hasBDFK = true;
        }
        this.foreignKeyList.add(fkd);
        this.fkMap.put(fkName, fkd);
        ++this.tableState;
        this.isValidated = false;
    }
    
    public void removeForeignKey(final String foreignKeyName) {
        final ForeignKeyDefinition removedFK = this.fkMap.remove(foreignKeyName);
        if (removedFK != null) {
            this.foreignKeyList.remove(removedFK);
            ++this.tableState;
            this.isValidated = false;
            this.hasBDFK = false;
            for (final ForeignKeyDefinition fkDef : this.foreignKeyList) {
                if (fkDef.isBidirectional()) {
                    this.hasBDFK = true;
                    break;
                }
            }
        }
    }
    
    public boolean isUnique(final List<String> columnNames) {
        if (this.uniqueKeyList != null && columnNames != null) {
            for (final UniqueKeyDefinition uk : this.getUniqueKeys()) {
                if (uk.getColumns().size() == columnNames.size()) {
                    boolean isUnique = true;
                    for (int index = 0; index < uk.getColumns().size(); ++index) {
                        if (!uk.getColumns().get(index).equals(columnNames.get(index))) {
                            isUnique = false;
                            break;
                        }
                    }
                    if (isUnique) {
                        return true;
                    }
                    continue;
                }
            }
        }
        return false;
    }
    
    public List getColumnList() {
        return this.columnList;
    }
    
    public List<String> getColumnNames() {
        return this.columnNames;
    }
    
    public List<String> getEncryptedColumnNames() {
        return this.encryptedColumnNames;
    }
    
    public int getColumnIndex(final String columnName) {
        if (columnName == null) {
            throw new NullPointerException("The column name specified to identify the column index in the table " + this.tableName + " is null");
        }
        return this.getColumnIndex(columnName, false);
    }
    
    public int getColumnIndex(final String columnName, final boolean ignoreCase) {
        if (columnName == null) {
            throw new NullPointerException("The column name specified to identify the column index in the table " + this.tableName + " is null");
        }
        if (ignoreCase) {
            for (final ColumnDefinition cd : this.columnList) {
                if (cd.getColumnName().equalsIgnoreCase(columnName)) {
                    return cd.index();
                }
            }
        }
        else {
            final ColumnDefinition cd2 = this.columnsMap.get(columnName);
            if (cd2 != null) {
                return cd2.index();
            }
        }
        return -1;
    }
    
    public void renameTableName(final String newTableName) {
        final String oldTableName = this.tableName;
        ++this.tableState;
        this.tableName = newTableName;
        if (this.displayName.equals(oldTableName) && null != PersistenceInitializer.getConfigurationValue("changeDisplayName") && PersistenceInitializer.getConfigurationValue("changeDisplayName").equalsIgnoreCase("yes")) {
            this.displayName = newTableName;
        }
        for (final ColumnDefinition c : this.columnList) {
            c.setTableName(newTableName);
        }
        this.primaryKey.setTableName(newTableName);
        if (this.foreignKeyList != null) {
            for (final ForeignKeyDefinition fk : this.foreignKeyList) {
                fk.setSlaveTableName(newTableName);
            }
        }
    }
    
    public void renameColumn(final String oldColumnName, final String newColumnName) {
        final ColumnDefinition colDef = this.getColumnDefinitionByName(oldColumnName);
        int index = -1;
        if (colDef.isEncryptedColumn()) {
            index = this.encryptedColumnNames.indexOf(oldColumnName);
            this.encryptedColumnNames.set(index, newColumnName);
        }
        index = this.columnList.indexOf(colDef);
        colDef.setColumnName(newColumnName);
        index = this.columnNames.indexOf(oldColumnName);
        this.columnNames.set(index, newColumnName);
        this.columnsMap.put(newColumnName, colDef);
        this.columnsMap.remove(oldColumnName);
        if (colDef.getDisplayName().equals(oldColumnName) && null != PersistenceInitializer.getConfigurationValue("changeDisplayName") && PersistenceInitializer.getConfigurationValue("changeDisplayName").equalsIgnoreCase("yes")) {
            colDef.setDisplayName(newColumnName);
        }
        if (colDef.isDynamic()) {
            final int idx = this.dynamicColumnNames.indexOf(oldColumnName);
            this.dynamicColumnNames.set(idx, newColumnName);
        }
        this.primaryKey.renameColumn(oldColumnName, newColumnName);
        if (this.dirtyWriteCheckColumnNamesStr != null && !this.dirtyWriteCheckColumnNamesStr.equals("*")) {
            final List<String> collist = new ArrayList<String>(this.dirtyWriteCheckColNamesExcludingPK);
            collist.set(collist.indexOf(oldColumnName), newColumnName);
            this.dirtyWriteCheckColNamesExcludingPK = collist;
        }
        else {
            this.cleanDirtyWriteColumnNames();
        }
        for (final ForeignKeyDefinition fk : this.foreignKeyList) {
            fk.renameColumn(oldColumnName, newColumnName, true);
        }
        if (this.uniqueKeyList != null) {
            for (final UniqueKeyDefinition uk : this.uniqueKeyList) {
                uk.renameColumn(oldColumnName, newColumnName);
            }
        }
        if (this.indexList != null) {
            for (final IndexDefinition idx2 : this.indexList) {
                idx2.renameColumn(oldColumnName, newColumnName);
            }
        }
        ++this.tableState;
        this.isValidated = false;
    }
    
    public void addColumnDefinition(final ColumnDefinition cd) {
        if (cd != null) {
            final String columnName = cd.getColumnName();
            final Long columnID = cd.getColumnID();
            cd.setTableName(this.tableName);
            if (this.columnList == null) {
                this.columnList = new ArrayList<ColumnDefinition>(1);
                this.encryptedColumnNames = new ArrayList<String>(1);
                this.columnNames = new ArrayList<String>(1);
            }
            if (cd.isDynamic()) {
                if (this.dynamicColumnList == null) {
                    this.dynamicColumnList = new ArrayList<ColumnDefinition>();
                    this.dynamicColumnNames = new ArrayList<String>();
                }
                this.dynamicColumnList.add(cd);
                this.dynamicColumnNames.add(columnName);
                if (cd.getPhysicalColumn() != null) {
                    this.addPhysicalColumn(cd.getPhysicalColumn());
                }
            }
            if (cd.isEncryptedColumn()) {
                this.encryptedColumnNames.add(columnName);
            }
            this.columnList.add(cd);
            cd.setIndex(this.columnList.size());
            this.columnNames.add(columnName);
            this.columnsMap.put(columnName, cd);
            ++this.tableState;
            this.isValidated = false;
            this.initKeyIndices();
            if (cd.getUniqueValueGeneration() != null) {
                this.updateUVHColumnIndices();
            }
            this.cleanDirtyWriteColumnNames();
        }
    }
    
    private void updateUVHColumnIndices() {
        final List<Integer> uvhColumnsIndexes = new ArrayList<Integer>();
        for (final ColumnDefinition cd : this.columnList) {
            if (cd.getUniqueValueGeneration() != null) {
                uvhColumnsIndexes.add(cd.index());
            }
        }
        if (!uvhColumnsIndexes.isEmpty()) {
            final int[] newArray = new int[uvhColumnsIndexes.size()];
            for (int i = 0; i < uvhColumnsIndexes.size(); ++i) {
                newArray[i] = uvhColumnsIndexes.get(i);
            }
            this.uvhColumnIndices = newArray;
        }
        else {
            this.uvhColumnIndices = null;
        }
    }
    
    public int[] getUVHColumnIndices() {
        return this.uvhColumnIndices;
    }
    
    public void modifyColumnDefinition(final ColumnDefinition modifiedColumnDef) throws MetaDataException {
        final ColumnDefinition columnDefinition = this.columnsMap.get(modifiedColumnDef.getColumnName());
        final String existingColumnDataType = columnDefinition.getDataType();
        final String modifiedColumnDataType = modifiedColumnDef.getDataType();
        final boolean isDataTypeChanged = !modifiedColumnDataType.equals(existingColumnDataType);
        if (isDataTypeChanged) {
            if (this.encryptedColumnNames.contains(modifiedColumnDef.getColumnName()) && !modifiedColumnDef.isEncryptedColumn()) {
                this.encryptedColumnNames.remove(modifiedColumnDef.getColumnName());
            }
            else if (!this.encryptedColumnNames.contains(modifiedColumnDef.getColumnName()) && modifiedColumnDef.isEncryptedColumn()) {
                this.encryptedColumnNames.add(modifiedColumnDef.getColumnName());
            }
        }
        columnDefinition.setDataType(modifiedColumnDataType);
        columnDefinition.setDefaultValue(modifiedColumnDef.getDefaultValue());
        columnDefinition.setMaxLength(modifiedColumnDef.getMaxLength());
        columnDefinition.setNullable(modifiedColumnDef.isNullable());
        columnDefinition.setAllowedValues(modifiedColumnDef.getAllowedValues());
        columnDefinition.setUniqueValueGeneration(modifiedColumnDef.getUniqueValueGeneration());
        columnDefinition.setDescription(modifiedColumnDef.getDescription());
        columnDefinition.setDisplayName(modifiedColumnDef.getDisplayName());
        columnDefinition.setPrecision(modifiedColumnDef.getPrecision());
        columnDefinition.setUnique(modifiedColumnDef.isUnique());
    }
    
    public ColumnDefinition removeColumnDefinition(final String columnName) {
        ColumnDefinition coldef = null;
        if (this.columnNames.contains(columnName)) {
            final int index = this.columnNames.indexOf(columnName);
            coldef = this.columnList.remove(index);
            this.columnNames.remove(columnName);
            this.columnsMap.remove(columnName);
            if (coldef.isDynamic()) {
                final int idx = this.dynamicColumnNames.indexOf(columnName);
                this.dynamicColumnList.remove(idx);
                this.dynamicColumnNames.remove(columnName);
                final String phyCol = coldef.getPhysicalColumn();
                if (phyCol != null) {
                    boolean removePhycol = true;
                    for (final ColumnDefinition dcColDef : this.dynamicColumnList) {
                        if (dcColDef.getPhysicalColumn() != null && dcColDef.getPhysicalColumn().equals(phyCol)) {
                            removePhycol = false;
                        }
                    }
                    if (removePhycol) {
                        this.physicalColumnNames.remove(phyCol);
                    }
                }
                if (this.dynamicColumnList.isEmpty()) {
                    this.dynamicColumnList = null;
                    this.dynamicColumnNames = null;
                }
            }
            for (int i = index; i < this.columnList.size(); ++i) {
                this.columnList.get(i).setIndex(i + 1);
            }
            this.initKeyIndices();
            ++this.tableState;
            this.isValidated = false;
            if (coldef.getUniqueValueGeneration() != null) {
                this.updateUVHColumnIndices();
            }
        }
        if (this.dirtyWriteCheckColumnNamesStr != null && !this.dirtyWriteCheckColumnNamesStr.equals("*")) {
            final List<String> collist = new ArrayList<String>(this.dirtyWriteCheckColNamesExcludingPK);
            collist.remove(columnName);
            this.dirtyWriteCheckColNamesExcludingPK = collist;
        }
        else {
            this.cleanDirtyWriteColumnNames();
        }
        return coldef;
    }
    
    public ColumnDefinition getColumnDefinitionByName(final String name) {
        return this.columnsMap.get(name);
    }
    
    public String getDefinedColumnName(final String columnName) {
        final int colIdx = this.getColumnIndex(columnName, true);
        return (colIdx > 0) ? this.getColumnNames().get(colIdx - 1) : null;
    }
    
    public ForeignKeyDefinition getForeignKeyDefinitionByName(final String name) {
        return this.fkMap.get(name);
    }
    
    public void addUniqueKey(final UniqueKeyDefinition ukd) {
        if (this.uniqueKeyList == null) {
            this.uniqueKeyList = new ArrayList<UniqueKeyDefinition>();
        }
        this.uniqueKeyList.add(ukd);
        ++this.tableState;
        this.isValidated = false;
    }
    
    public UniqueKeyDefinition removeUniqueKey(final String uniqueKeyName) {
        if (this.uniqueKeyList != null && this.uniqueKeyList.size() > 0) {
            for (final UniqueKeyDefinition ukDef : this.uniqueKeyList) {
                if (ukDef.getName().equals(uniqueKeyName)) {
                    ++this.tableState;
                    this.isValidated = false;
                    return this.uniqueKeyList.remove(this.uniqueKeyList.indexOf(ukDef));
                }
            }
        }
        return null;
    }
    
    public List<UniqueKeyDefinition> getUniqueKeys() {
        return this.uniqueKeyList;
    }
    
    public UniqueKeyDefinition getUniqueKeyDefinitionByName(final String uniqueKeyName) {
        if (uniqueKeyName != null && this.uniqueKeyList != null) {
            for (final UniqueKeyDefinition ukDef : this.uniqueKeyList) {
                if (ukDef.getName().equals(uniqueKeyName)) {
                    return ukDef;
                }
            }
        }
        return null;
    }
    
    public void addIndex(final IndexDefinition id) {
        if (this.indexList == null) {
            this.indexList = new ArrayList<IndexDefinition>();
        }
        this.indexList.add(id);
        ++this.tableState;
        this.isValidated = false;
    }
    
    public IndexDefinition removeIndex(final String indexName) {
        if (this.indexList != null && this.indexList.size() > 0) {
            for (final IndexDefinition iDef : this.indexList) {
                if (iDef.getName().equals(indexName)) {
                    ++this.tableState;
                    this.isValidated = false;
                    return this.indexList.remove(this.indexList.indexOf(iDef));
                }
            }
        }
        return null;
    }
    
    public List<IndexDefinition> getIndexes() {
        return this.indexList;
    }
    
    public IndexDefinition getIndexDefinitionByName(final String indexName) {
        if (indexName != null && this.indexList != null) {
            for (final IndexDefinition iDef : this.indexList) {
                if (iDef.getName().equals(indexName)) {
                    return iDef;
                }
            }
        }
        return null;
    }
    
    public int[] getKeyIndices() {
        if (this.keyIndices == null) {
            this.initKeyIndices();
        }
        return this.keyIndices;
    }
    
    public String getColumnType(final String columnName) {
        final int index = this.getColumnIndex(columnName);
        return this.getColumnType(index);
    }
    
    public String getColumnType(final int columnIndex) {
        final ColumnDefinition cd = this.columnList.get(columnIndex - 1);
        return cd.getDataType();
    }
    
    public int getSQLType(final String columnName) {
        final int index = this.getColumnIndex(columnName);
        return this.getSQLType(index);
    }
    
    public int getSQLType(final int columnIndex) {
        return this.columnList.get(columnIndex - 1).getSQLType();
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("\n<TableDefinition>");
        buffer.append("\n\t<table-name>" + this.tableName + "</table-name>");
        buffer.append("\n\t<template>" + this.template + "</template>");
        buffer.append("\n\t<display-name>" + this.displayName + "</display-name>");
        buffer.append("\n\t<isSystem>" + this.isSystem + "</isSystem>");
        buffer.append("\n\t<createTable>" + this.createTable + "</createTable>");
        if (this.dcType != null) {
            buffer.append("\n\t<dc-type>" + this.dcType + "</dc-type>");
        }
        buffer.append("\n\t<columns>" + this.columnList + "\n\t</columns>");
        buffer.append("\n\t<primary-key>" + this.primaryKey + "</primary-key>");
        buffer.append("\n\t<foreign-key-List>" + this.foreignKeyList + "\n\t</foreign-key-List>");
        buffer.append("\n\t<unique-key-list>" + this.uniqueKeyList + "\n\t</unique-key-list>");
        buffer.append("\n</TableDefinition>");
        return buffer.toString();
    }
    
    void setValidate(final boolean validate) throws MetaDataException {
        this.isValidated = validate;
    }
    
    public boolean hasBDFK() {
        return this.hasBDFK;
    }
    
    public void setBDFKImpact(final boolean bdfkImpact) {
        this.bdfkImpact = bdfkImpact;
    }
    
    public boolean hasBDFKImpact() {
        return this.bdfkImpact;
    }
    
    @Deprecated
    public String name() {
        return this.tableName;
    }
    
    @Deprecated
    public int columnCount() {
        return this.columnList.size();
    }
    
    public boolean hasColumn(final String columnName) {
        return this.getColumnDefinitionByName(columnName) != null;
    }
    
    public long metaDigest() {
        try {
            if (this.metaDigest == 0L) {
                if (PersistenceInitializer.onSAS()) {
                    this.metaDigest = this.tableName.hashCode() + this.columnNames.size();
                    for (final ColumnDefinition cd : this.columnList) {
                        this.metaDigest += cd.getColumnName().hashCode() + cd.getSQLType() + cd.index();
                    }
                }
                else {
                    this.metaDigest = this.tableName.hashCode();
                    for (final ColumnDefinition column : this.columnList) {
                        this.metaDigest += column.metaDigest();
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return this.metaDigest;
    }
    
    public void resetMetaDigest() {
        this.metaDigest = 0L;
        for (final ColumnDefinition column : this.columnList) {
            column.resetMetaDigest();
        }
    }
    
    public boolean isParticipatedInArchival() {
        return this.isParticipatedInArchival;
    }
    
    public void setArchivalTable(final boolean isParticipated) {
        this.isParticipatedInArchival = isParticipated;
    }
    
    public String getDynamicColumnType() {
        return this.dcType;
    }
    
    public void setDynamicColumnType(final String dcType) {
        this.dcType = dcType;
    }
    
    public List<String> getDynamicColumnNames() {
        return this.dynamicColumnNames;
    }
    
    public List<ColumnDefinition> getDynamicColumnList() {
        return this.dynamicColumnList;
    }
    
    public void addPhysicalColumn(final String columnName) {
        if (!this.physicalColumnNames.contains(columnName)) {
            this.physicalColumnNames.add(columnName);
        }
    }
    
    public void removePhysicalColumn(final String columnName) {
        if (this.physicalColumnNames.contains(columnName)) {
            this.physicalColumnNames.remove(columnName);
        }
    }
    
    public List<String> getPhysicalColumns() {
        return this.physicalColumnNames;
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("tablename", (Object)this.tableName);
        jsonObject.put("template", this.template);
        jsonObject.put("displayname", (Object)this.displayName);
        jsonObject.put("issystem", this.isSystem);
        jsonObject.put("createtable", this.createTable);
        jsonObject.put("dctype", (Object)this.dcType);
        JSONArray jsonArray = new JSONArray();
        for (final ColumnDefinition cd : this.columnList) {
            jsonArray.put((Object)cd.toJSON());
        }
        jsonObject.put("columns", (Object)jsonArray);
        if (this.primaryKey != null) {
            jsonObject.put("primarykey", (Object)this.primaryKey.toJSON());
        }
        if (this.foreignKeyList != null) {
            jsonArray = new JSONArray();
            for (final ForeignKeyDefinition fk : this.foreignKeyList) {
                jsonArray.put((Object)fk.toJSON());
            }
            jsonObject.put("foreignkeys", (Object)jsonArray);
        }
        if (this.uniqueKeyList != null) {
            jsonArray = new JSONArray();
            for (final UniqueKeyDefinition uk : this.uniqueKeyList) {
                jsonArray.put((Object)uk.toJSON());
            }
            jsonObject.put("uniquekeys", (Object)jsonArray);
        }
        if (this.indexList != null) {
            jsonArray = new JSONArray();
            for (final IndexDefinition ik : this.indexList) {
                jsonArray.put((Object)ik.toJSON());
            }
            jsonObject.put("indexes", (Object)jsonArray);
        }
        jsonObject.put("description", (Object)this.description);
        return jsonObject;
    }
}
