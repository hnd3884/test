package com.adventnet.persistence.migration;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import java.util.Properties;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.zoho.dddiff.AddedElement;
import java.util.Collection;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.ElementTransformer;
import org.w3c.dom.Element;
import com.zoho.dddiff.DeletedElement;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.zoho.dddiff.ModifiedElement;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.AlterTableQuery;
import com.zoho.dddiff.DataDictionaryDiff;
import java.util.List;
import java.util.logging.Logger;

public class DDLChanges
{
    private static final Logger OUT;
    private List[] installQueries;
    private List[] revertQueries;
    private DataDictionaryDiff ddDiff;
    private boolean ignoreMaxSizeReduction;
    private DDLOperationType operationType;
    
    DDLChanges(final DataDictionaryDiff diff) {
        this.installQueries = new List[17];
        this.revertQueries = new List[17];
        this.ddDiff = null;
        this.ignoreMaxSizeReduction = false;
        this.operationType = DDLOperationType.INSTALL;
        this.ddDiff = diff;
    }
    
    void ignoreMaxSizeReduction() {
        this.ignoreMaxSizeReduction = true;
    }
    
    void setOperationType(final DDLOperationType operationType) {
        this.operationType = operationType;
    }
    
    List[] getQueries(final DDLOperationType type) {
        return (type == DDLOperationType.INSTALL) ? this.installQueries : this.revertQueries;
    }
    
    public List<AlterTableQuery> getDroppedForeignKeys(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.DROP_FK.ordinal()];
    }
    
    public List<AlterTableQuery> getDroppedUniqueKeys(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.DROP_UK.ordinal()];
    }
    
    public List<AlterTableQuery> getDroppedPrimaryKeys(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.DROP_PK.ordinal()];
    }
    
    public List<AlterTableQuery> getDroppedIndexes(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.DROP_IDX.ordinal()];
    }
    
    public List<AlterTableQuery> getAddedColumns(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.ADD_COLUMN.ordinal()];
    }
    
    public List<AlterTableQuery> getModifiedColumns(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.MODIFY_COLUMN.ordinal()];
    }
    
    public List<AlterTableQuery> getModifiedPrimaryKeys(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.MODIFY_PK.ordinal()];
    }
    
    public List<AlterTableQuery> getAddedIndexes(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.ADD_IDX.ordinal()];
    }
    
    public List<AlterTableQuery> getAddedPrimaryKeys(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.ADD_PK.ordinal()];
    }
    
    public List<AlterTableQuery> getAddedUniqueKeys(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.ADD_UK.ordinal()];
    }
    
    public List<TableDefinition> getCreatedTables(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.CREATE_TABLE.ordinal()];
    }
    
    public List<AlterTableQuery> getModifiedForeignKeys(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.MODIFY_FK.ordinal()];
    }
    
    public List<AlterTableQuery> getAddedForeignKeys(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.ADD_FK.ordinal()];
    }
    
    public List<String> getDroppedTableNames(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.DROP_TABLE.ordinal()];
    }
    
    public List<AlterTableQuery> getDroppedColumns(final DDLOperationType operation) {
        return this.getQueries(operation)[DDLOperation.DROP_COLUMN.ordinal()];
    }
    
    private void initArray(final List[] listArray) {
        listArray[DDLOperation.MODIFY_DD.ordinal()] = new ArrayList();
        listArray[DDLOperation.DROP_FK.ordinal()] = new ArrayList();
        listArray[DDLOperation.DROP_UK.ordinal()] = new ArrayList();
        listArray[DDLOperation.DROP_PK.ordinal()] = new ArrayList();
        listArray[DDLOperation.DROP_IDX.ordinal()] = new ArrayList();
        listArray[DDLOperation.ADD_COLUMN.ordinal()] = new ArrayList();
        listArray[DDLOperation.MODIFY_COLUMN.ordinal()] = new ArrayList();
        listArray[DDLOperation.MODIFY_PK.ordinal()] = new ArrayList();
        listArray[DDLOperation.ADD_IDX.ordinal()] = new ArrayList();
        listArray[DDLOperation.ADD_PK.ordinal()] = new ArrayList();
        listArray[DDLOperation.ADD_UK.ordinal()] = new ArrayList();
        listArray[DDLOperation.CREATE_TABLE.ordinal()] = new ArrayList();
        listArray[DDLOperation.MODIFY_TABLE.ordinal()] = new ArrayList();
        listArray[DDLOperation.MODIFY_FK.ordinal()] = new ArrayList();
        listArray[DDLOperation.ADD_FK.ordinal()] = new ArrayList();
        listArray[DDLOperation.DROP_TABLE.ordinal()] = new ArrayList();
        listArray[DDLOperation.DROP_COLUMN.ordinal()] = new ArrayList();
    }
    
    void generateDDLQueries() {
        this.initArray(this.installQueries);
        this.initArray(this.revertQueries);
        final List<DeletedElement> droppedFKs = this.ddDiff.getDroppedForeignKeys();
        final List<AddedElement> addedFKs = this.ddDiff.getNewForeignKeys();
        final List<ModifiedElement> modifiedFK = this.ddDiff.getModifiedForeignKeys();
        final List<DeletedElement> droppedUKs = this.ddDiff.getDroppedUniquesKeys();
        final List<AddedElement> addedUKs = this.ddDiff.getNewUniqueKeys();
        final List<ModifiedElement> modifiedUKs = this.ddDiff.getModifiedUniqueKeys();
        final List<DeletedElement> droppedIDXs = this.ddDiff.getDroppedIndexes();
        final List<AddedElement> addedIdxs = this.ddDiff.getNewIndexes();
        final List<ModifiedElement> modifiedIdxs = this.ddDiff.getModifiedIndexes();
        final List<ModifiedElement> modifiedPKs = this.ddDiff.getModifiedPrimaryKeys();
        final List<AddedElement> addedColumns = this.ddDiff.getNewColumns();
        final List<DeletedElement> droppedColumns = this.ddDiff.getDroppedColumns();
        final List<ModifiedElement> modifiedColumns = this.ddDiff.getModifiedColumns();
        final List<AddedElement> addedTables = this.ddDiff.getNewTables();
        final List<DeletedElement> droppedTables = this.ddDiff.getDroppedTables();
        final List<ModifiedElement> modifiedDDs = this.ddDiff.getModifiedDDs();
        final List<ModifiedElement> modifiedTables = this.ddDiff.getModifiedTables();
        if (!modifiedDDs.isEmpty()) {
            for (final ModifiedElement modify : modifiedDDs) {
                final String ddName = modify.getDDName();
                final UpdateQuery uq = new UpdateQueryImpl("SB_Applications");
                final UpdateQuery uq2 = new UpdateQueryImpl("SB_Applications");
                final Criteria cri = new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), ddName, 0);
                uq.setCriteria(cri);
                uq2.setCriteria(cri);
                final List<String> changedAttributes = modify.getChangedAttributes();
                for (int i = 0; i < changedAttributes.size(); ++i) {
                    final String attributeName = changedAttributes.get(i);
                    if (attributeName.equals("description")) {
                        final String oldDesc = this.getDescriptionForDD(modify.getOldElementForChangedAttribute(attributeName));
                        final String newDesc = this.getDescriptionForDD(modify.getNewElementForChangedAttribute(attributeName));
                        uq.setUpdateColumn("APPL_DESC", newDesc);
                        uq2.setUpdateColumn("APPL_DESC", oldDesc);
                    }
                    else if (attributeName.equals("template-meta-handler")) {
                        final String oldHandler = modify.getOldElementForChangedAttribute(attributeName).getAttribute("template-meta-handler");
                        final String newHandler = modify.getNewElementForChangedAttribute(attributeName).getAttribute("template-meta-handler");
                        uq.setUpdateColumn("TEMPLATE_META_HANDLER", newHandler.isEmpty() ? null : newHandler);
                        uq2.setUpdateColumn("TEMPLATE_META_HANDLER", oldHandler.isEmpty() ? null : oldHandler);
                    }
                    else if (attributeName.equals("dc-type")) {
                        final String oldDcType = modify.getOldElementForChangedAttribute(attributeName).getAttribute("dc-type");
                        final String newDcType = modify.getNewElementForChangedAttribute(attributeName).getAttribute("dc-type");
                        uq.setUpdateColumn("DC_TYPE", newDcType.isEmpty() ? null : newDcType);
                        uq2.setUpdateColumn("DC_TYPE", oldDcType.isEmpty() ? null : oldDcType);
                        if (oldDcType != null && !oldDcType.equals("nodc")) {
                            if (newDcType != null) {
                                if (!newDcType.equals("nodc")) {
                                    continue;
                                }
                            }
                            try {
                                final SelectQuery sq = new SelectQueryImpl(Table.getTable("TableDetails"));
                                sq.addSelectColumn(Column.getColumn(null, "*"));
                                final Criteria jnCri = new Criteria(Column.getColumn("TableDetails", "APPL_ID"), Column.getColumn("SB_Applications", "APPL_ID"), 0);
                                sq.addJoin(new Join("TableDetails", "SB_Applications", jnCri, 1));
                                sq.setCriteria(cri.and(new Criteria(Column.getColumn("TableDetails", "DC_TYPE"), null, 0)));
                                final DataObject dobj = DataAccess.get(sq);
                                final Iterator tableEntries = dobj.getRows("TableDetails");
                                while (tableEntries.hasNext()) {
                                    final Row tableEntry = tableEntries.next();
                                    final String tableName = (String)tableEntry.get("TABLE_NAME");
                                    final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                                    if (td.getDynamicColumnNames() != null && !td.getDynamicColumnNames().isEmpty()) {
                                        boolean isTableLevelDcTypeChangeRequired = true;
                                        if (!modifiedTables.isEmpty()) {
                                            for (final ModifiedElement modifyTable : modifiedTables) {
                                                final String tName = modifyTable.getTableName();
                                                if (tName.equals(td.getTableName()) && modifyTable.getChangedAttributes().contains("dc-type")) {
                                                    DDLChanges.OUT.log(Level.INFO, "Not setting dc-type as \"" + newDcType + "\" for table \"" + tName + "\", due to \"" + ddName + "\" module's dc-type change.Because, table level dc-type got modified.");
                                                    isTableLevelDcTypeChangeRequired = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!isTableLevelDcTypeChangeRequired) {
                                            continue;
                                        }
                                        DDLChanges.OUT.log(Level.INFO, "Going to set dc-type as \"" + newDcType + "\" for table \"" + td.getTableName() + "\", as \"" + ddName + "\" module's dc-type is changed.");
                                        final AlterTableQuery atq = this.getATQForModifyTablesDCType(td.getTableName(), newDcType);
                                        final AlterTableQuery revertAtq = this.getATQForModifyTablesDCType(td.getTableName(), oldDcType);
                                        if (this.operationType == DDLOperationType.INSTALL) {
                                            this.installQueries[DDLOperation.MODIFY_TABLE.ordinal()].add(0, atq);
                                            this.revertQueries[DDLOperation.MODIFY_TABLE.ordinal()].add(0, revertAtq);
                                        }
                                        else {
                                            this.installQueries[DDLOperation.MODIFY_TABLE.ordinal()].add(0, atq);
                                            this.revertQueries[DDLOperation.MODIFY_TABLE.ordinal()].add(0, revertAtq);
                                        }
                                    }
                                }
                            }
                            catch (final Exception e) {
                                throw new IllegalArgumentException("Exception Occured while using \"MetaDataUtil.getDataDictionary(ddName)\" method.", e);
                            }
                        }
                    }
                }
                this.installQueries[DDLOperation.MODIFY_DD.ordinal()].add(uq);
                this.revertQueries[DDLOperation.MODIFY_DD.ordinal()].add(uq2);
            }
        }
        if (!droppedFKs.isEmpty()) {
            for (final DeletedElement drop : droppedFKs) {
                final String tableName2 = drop.getTableName();
                final AlterTableQuery aq = this.getATQforDropForeignKey(tableName2, drop.getElement());
                final AlterTableQuery aq2 = this.getATQforAddForeignKey(tableName2, drop.getElement());
                this.installQueries[DDLOperation.DROP_FK.ordinal()].add(aq);
                this.revertQueries[DDLOperation.ADD_FK.ordinal()].add(aq2);
            }
        }
        if (!droppedUKs.isEmpty()) {
            for (final DeletedElement drop : droppedUKs) {
                final String tableName2 = drop.getTableName();
                final TableDefinition oldTd = ElementTransformer.getTableDefinition((Element)drop.getElement().getParentNode().getParentNode());
                final UniqueKeyDefinition oldUK = ElementTransformer.getUniqueKeyDefinition(drop.getElement());
                if (this.isUK_duplicate_of_PK_orAnotherUK_of_Table(oldTd, oldUK, false)) {
                    DDLChanges.OUT.log(Level.INFO, "Ignoring the delete Unique-key \"{0}\" query, as entry for UK is not there in meta-data cache(since earlier it is duplicate of either Primary-key or another Unique-key of table \"{1}\").", new String[] { oldUK.getName(), tableName2 });
                }
                else {
                    final AlterTableQuery aq3 = this.getATQforDropUniqueKey(tableName2, oldUK);
                    this.installQueries[DDLOperation.DROP_UK.ordinal()].add(aq3);
                    final AlterTableQuery aq4 = this.getATQforAddUniqueKey(tableName2, oldUK);
                    this.revertQueries[DDLOperation.ADD_UK.ordinal()].add(aq4);
                }
            }
        }
        if (!droppedIDXs.isEmpty()) {
            for (final DeletedElement drop : droppedIDXs) {
                final String tableName2 = drop.getTableName();
                TableDefinition td2 = null;
                try {
                    td2 = MetaDataUtil.getTableDefinitionByName(tableName2);
                }
                catch (final Exception e2) {
                    throw new IllegalArgumentException("Exception got while getting TableDefinition from MetaData", e2);
                }
                if (td2 == null) {
                    throw new IllegalArgumentException("TableDefinition for table '" + tableName2 + "', is missing in meta-data cache, while constructing delete_index query for '" + drop.getElement().getAttribute("name") + "'.");
                }
                final IndexDefinition id = ElementTransformer.getIndexDefinition(drop.getElement(), td2);
                if (td2.getIndexDefinitionByName(id.getName()) == null) {
                    DDLChanges.OUT.log(Level.INFO, "Ignoring the drop index \"{0}\" query, since the index is not created.", new String[] { id.getName() });
                }
                else {
                    final AlterTableQuery aq3 = this.getATQforDropIndex(tableName2, id.getName());
                    this.installQueries[DDLOperation.DROP_IDX.ordinal()].add(aq3);
                    final AlterTableQuery aq4 = this.getATQforAddIndex(tableName2, id);
                    this.revertQueries[DDLOperation.ADD_IDX.ordinal()].add(aq4);
                }
            }
        }
        if (!droppedTables.isEmpty()) {
            for (final DeletedElement drop : droppedTables) {
                final TableDefinition td3 = ElementTransformer.getTableDefinition(drop.getElement());
                final String tableName3 = td3.getTableName();
                for (final ColumnDefinition cd : td3.getColumnList()) {
                    try {
                        final String maxValueInCustomUtil = MetaDataUtil.getAttribute(tableName3, cd.getColumnName(), "maxsize");
                    }
                    catch (final MetaDataException e3) {
                        e3.printStackTrace();
                        throw new IllegalArgumentException("Exception occurred while getting custom attribute value of " + this.getKey(tableName3, cd.getColumnName(), "maxsize") + " from metadata");
                    }
                    String maxValueInCustomUtil;
                    final Integer oldExtendedMaxVal = ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getOldExtendedMaxValue(tableName3, cd.getColumnName()) : ((maxValueInCustomUtil != null) ? Integer.valueOf(Integer.parseInt(maxValueInCustomUtil)) : null);
                    if (oldExtendedMaxVal != null) {
                        cd.setMaxLength(oldExtendedMaxVal);
                    }
                    String oldExtendedDefaultVal;
                    try {
                        oldExtendedDefaultVal = (ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getOldExtendedDefaultValue(tableName3, cd.getColumnName()) : MetaDataUtil.getAttribute(tableName3, cd.getColumnName(), "defaultvalue"));
                    }
                    catch (final MetaDataException e4) {
                        e4.printStackTrace();
                        throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName3, cd.getColumnName(), "defaultvalue") + " from metadata");
                    }
                    if (oldExtendedDefaultVal != null) {
                        try {
                            cd.setDefaultValue(oldExtendedDefaultVal);
                        }
                        catch (final MetaDataException e5) {
                            e5.printStackTrace();
                        }
                    }
                }
                final List<ForeignKeyDefinition> fks = new ArrayList<ForeignKeyDefinition>(td3.getForeignKeyList());
                for (final ForeignKeyDefinition fk : fks) {
                    final String fkName = fk.getName();
                    this.installQueries[DDLOperation.DROP_FK.ordinal()].add(this.getATQforDropForeignKey(tableName3, fkName));
                    this.revertQueries[DDLOperation.ADD_FK.ordinal()].add(this.getATQforAddForeignKey(tableName3, fk));
                    td3.removeForeignKey(fkName);
                }
                this.installQueries[DDLOperation.DROP_TABLE.ordinal()].add(tableName3);
                this.revertQueries[DDLOperation.CREATE_TABLE.ordinal()].add(td3);
            }
        }
        if (!addedColumns.isEmpty()) {
            for (final AddedElement add : addedColumns) {
                final String tableName2 = add.getTableName();
                final ColumnDefinition col = ElementTransformer.getColumnDefinition(add.getElement());
                boolean needToModify = false;
                try {
                    final String maxValueInCustomUtil2 = MetaDataUtil.getAttribute(tableName2, col.getColumnName(), "maxsize");
                }
                catch (final MetaDataException e6) {
                    e6.printStackTrace();
                    throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName2, col.getColumnName(), "maxsize") + " from metadata");
                }
                String maxValueInCustomUtil2;
                final Integer extendedMaxVal = ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getNewExtendedMaxValue(tableName2, col.getColumnName()) : ((maxValueInCustomUtil2 != null) ? Integer.valueOf(Integer.parseInt(maxValueInCustomUtil2)) : null);
                if (extendedMaxVal != null) {
                    col.setMaxLength(extendedMaxVal);
                }
                String extendedDefVal;
                try {
                    extendedDefVal = (ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getNewExtendedDefaultValue(tableName2, col.getColumnName()) : MetaDataUtil.getAttribute(tableName2, col.getColumnName(), "defaultvalue"));
                }
                catch (final MetaDataException e7) {
                    e7.printStackTrace();
                    throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName2, col.getColumnName(), "defaultvalue") + " from metadata");
                }
                if (extendedDefVal != null) {
                    try {
                        col.setDefaultValue(extendedDefVal);
                    }
                    catch (final MetaDataException e8) {
                        e8.printStackTrace();
                    }
                }
                if (!col.isNullable() && col.getDefaultValue() == null) {
                    col.setNullable(true);
                    needToModify = true;
                }
                if (col.isUnique()) {
                    col.setUnique(false);
                    final AlterTableQuery atq2 = this.getATQforAddUniqueKey(tableName2, col);
                    this.installQueries[DDLOperation.ADD_UK.ordinal()].add(atq2);
                }
                final AlterTableQuery aq5 = this.getATQforAddColumn(tableName2, col);
                this.installQueries[DDLOperation.ADD_COLUMN.ordinal()].add(aq5);
                if (needToModify) {
                    final List<String> dummyList = new ArrayList<String>();
                    dummyList.add("nullable");
                    final ColumnDefinition cd2 = ElementTransformer.getColumnDefinition(add.getElement());
                    if (extendedMaxVal != null) {
                        cd2.setMaxLength(extendedMaxVal);
                    }
                    if (extendedDefVal != null) {
                        try {
                            cd2.setDefaultValue(extendedDefVal);
                        }
                        catch (final MetaDataException e9) {
                            e9.printStackTrace();
                        }
                    }
                    cd2.setUnique(false);
                    final AlterTableQuery atq3 = this.getATQforModifyColumn(tableName2, cd2, cd2, dummyList);
                    this.installQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(atq3);
                    final AlterTableQuery atq4 = null;
                    this.revertQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(atq4);
                }
                final AlterTableQuery aq6 = this.getATQforDropColumn(tableName2, add.getElement());
                this.revertQueries[DDLOperation.DROP_COLUMN.ordinal()].add(aq6);
            }
        }
        if (!modifiedFK.isEmpty()) {
            for (final ModifiedElement modify : modifiedFK) {
                final ForeignKeyDefinition oldFK = ElementTransformer.getForeignKeyDefinition(modify.getOldElement());
                final ForeignKeyDefinition newFK = ElementTransformer.getForeignKeyDefinition(modify.getNewElement());
                if (modify.getChangedAttributes().contains("isbidirectional") && oldFK.isBidirectional() == newFK.isBidirectional()) {
                    modify.getChangedAttributes().remove("isbidirectional");
                }
                if (!modify.getChangedAttributes().isEmpty()) {
                    DDLChanges.OUT.log(Level.INFO, "processing MODIFY_FK query for constraint {0}.{1} modified in {2}", new Object[] { modify.getTableName(), oldFK.getName(), modify.getChangedAttributes() });
                    if (modify.getChangedAttributes().contains("fk-local-column") || modify.getChangedAttributes().contains("fk-reference-column") || modify.getChangedAttributes().contains("reference-table-name")) {
                        final String tableName4 = modify.getTableName();
                        this.installQueries[DDLOperation.DROP_FK.ordinal()].add(this.getATQforDropForeignKey(tableName4, modify.getOldElement()));
                        this.installQueries[DDLOperation.ADD_FK.ordinal()].add(this.getATQforAddForeignKey(tableName4, modify.getNewElement()));
                        this.revertQueries[DDLOperation.ADD_FK.ordinal()].add(this.getATQforAddForeignKey(tableName4, modify.getOldElement()));
                        this.revertQueries[DDLOperation.DROP_FK.ordinal()].add(this.getATQforDropForeignKey(tableName4, modify.getNewElement()));
                    }
                    else {
                        final AlterTableQuery[] aqs = this.getATQforModifyForeignKey(modify);
                        this.installQueries[DDLOperation.MODIFY_FK.ordinal()].add(aqs[0]);
                        this.revertQueries[DDLOperation.MODIFY_FK.ordinal()].add(aqs[1]);
                    }
                }
            }
        }
        if (!modifiedColumns.isEmpty()) {
            for (final ModifiedElement modify : modifiedColumns) {
                final String tableName2 = modify.getTableName();
                final ColumnDefinition oldCD = ElementTransformer.getColumnDefinition(modify.getOldElement());
                final ColumnDefinition newCD = ElementTransformer.getColumnDefinition(modify.getNewElement());
                String key = tableName2 + "." + newCD.getColumnName() + ".maxsize";
                try {
                    final String maxValueInCustomUtil = MetaDataUtil.getAttribute(tableName2, newCD.getColumnName(), "maxsize");
                }
                catch (final MetaDataException e3) {
                    e3.printStackTrace();
                    throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName2, newCD.getColumnName(), "maxsize") + " from metadata");
                }
                String maxValueInCustomUtil;
                final Integer newExtendedMaxVal = ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getNewExtendedMaxValue(tableName2, newCD.getColumnName()) : ((maxValueInCustomUtil != null) ? Integer.valueOf(Integer.parseInt(maxValueInCustomUtil)) : null);
                final Integer oldExtendedMaxVal2 = ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getOldExtendedMaxValue(tableName2, newCD.getColumnName()) : ((maxValueInCustomUtil != null) ? Integer.valueOf(Integer.parseInt(maxValueInCustomUtil)) : null);
                if (oldExtendedMaxVal2 != null) {
                    oldCD.setMaxLength(oldExtendedMaxVal2);
                }
                if (newExtendedMaxVal != null) {
                    newCD.setMaxLength(newExtendedMaxVal);
                }
                if (this.operationType == DDLOperationType.INSTALL && ExtendedDDAggregator.isExtendedDDModified() && oldExtendedMaxVal2 != null && newExtendedMaxVal == null) {
                    ExtendedDDAggregator.setColsModifiedInDD("maxsize", key);
                }
                key = tableName2 + "." + newCD.getColumnName() + ".defaultvalue";
                String newExtendedDefVal;
                try {
                    newExtendedDefVal = (ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getNewExtendedDefaultValue(tableName2, newCD.getColumnName()) : MetaDataUtil.getAttribute(tableName2, newCD.getColumnName(), "defaultvalue"));
                }
                catch (final MetaDataException e10) {
                    e10.printStackTrace();
                    throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName2, newCD.getColumnName(), "defaultvalue") + " from metadata");
                }
                if (newExtendedDefVal != null) {
                    try {
                        newCD.setDefaultValue(newExtendedDefVal);
                    }
                    catch (final MetaDataException ex2) {}
                }
                String oldExtendedDefVal;
                try {
                    oldExtendedDefVal = (ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getOldExtendedDefaultValue(tableName2, newCD.getColumnName()) : MetaDataUtil.getAttribute(tableName2, newCD.getColumnName(), "defaultvalue"));
                }
                catch (final MetaDataException e11) {
                    e11.printStackTrace();
                    throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName2, newCD.getColumnName(), "defaultvalue") + " from metadata");
                }
                if (oldExtendedDefVal != null) {
                    try {
                        oldCD.setDefaultValue(oldExtendedDefVal);
                    }
                    catch (final MetaDataException ex3) {}
                }
                if (this.operationType == DDLOperationType.INSTALL && ExtendedDDAggregator.isExtendedDDModified() && oldExtendedDefVal != null && newExtendedDefVal == null) {
                    ExtendedDDAggregator.setColsModifiedInDD("defaultvalue", key);
                }
                if (modify.getChangedAttributes().contains("nullable") && oldCD.isNullable() == newCD.isNullable()) {
                    modify.getChangedAttributes().remove("nullable");
                }
                if (modify.getChangedAttributes().contains("max-size") && oldCD.getMaxLength() == newCD.getMaxLength()) {
                    modify.getChangedAttributes().remove("max-size");
                }
                if (modify.getChangedAttributes().contains("precision") && oldCD.getPrecision() == newCD.getPrecision()) {
                    modify.getChangedAttributes().remove("precision");
                }
                if (modify.getChangedAttributes().contains("unique")) {
                    if (!oldCD.isUnique() && newCD.isUnique()) {
                        for (final AddedElement ae : addedUKs) {
                            final UniqueKeyDefinition ukDef = ElementTransformer.getUniqueKeyDefinition(ae.getElement());
                            if (ukDef.getColumns().contains(oldCD.getColumnName()) && ukDef.getColumns().size() == 1 && ae.getTableName().equals(tableName2)) {
                                modify.getChangedAttributes().remove("unique");
                                break;
                            }
                        }
                    }
                    else if (oldCD.isUnique() && !newCD.isUnique()) {
                        AddedElement removeElement = null;
                        for (final AddedElement ae2 : addedUKs) {
                            final UniqueKeyDefinition ukDef2 = ElementTransformer.getUniqueKeyDefinition(ae2.getElement());
                            if (ukDef2.getColumns().contains(oldCD.getColumnName()) && ukDef2.getColumns().size() == 1 && ae2.getTableName().equals(tableName2)) {
                                modify.getChangedAttributes().remove("unique");
                                removeElement = ae2;
                                break;
                            }
                        }
                        if (removeElement != null) {
                            addedUKs.remove(removeElement);
                        }
                    }
                }
                if (modify.getChangedAttributes().contains("unique")) {
                    if (oldCD.isUnique() && !newCD.isUnique()) {
                        final UniqueKeyDefinition ukDef3 = this.getMickeyUniqueKeyForColumn(tableName2, newCD.getColumnName(), modify.getOldElement());
                        if (ukDef3 != null) {
                            final AlterTableQuery atq5 = this.getATQforDropUniqueKey(tableName2, ukDef3);
                            this.installQueries[DDLOperation.DROP_UK.ordinal()].add(atq5);
                            final AlterTableQuery atq6 = this.getATQforAddUniqueKey(tableName2, ukDef3);
                            this.revertQueries[DDLOperation.ADD_UK.ordinal()].add(atq6);
                        }
                        else {
                            DDLChanges.OUT.info("No Unique-key details were found in meta-data for \"" + tableName2 + "\".\"" + newCD.getColumnName() + "\" column.");
                        }
                    }
                    else if (!oldCD.isUnique() && newCD.isUnique()) {
                        final TableDefinition newTd = ElementTransformer.getTableDefinition((Element)modify.getNewElement().getParentNode().getParentNode());
                        final UniqueKeyDefinition newUK = new UniqueKeyDefinition();
                        newUK.addColumn(newCD.getColumnName());
                        newUK.setName("Temp_UK_Temp");
                        if (this.isUK_duplicate_of_PK_orAnotherUK_of_Table(newTd, newUK, true)) {
                            DDLChanges.OUT.info("As the column \"" + tableName2 + "\".\"" + newCD.getColumnName() + "\" alone participates in Primary-key or another Uniuq-key of table, not creating new Unique-key for change in unique attribute.");
                        }
                        else {
                            final AlterTableQuery atq7 = this.getATQforAddUniqueKey(tableName2, newCD);
                            this.installQueries[DDLOperation.ADD_UK.ordinal()].add(atq7);
                        }
                    }
                    oldCD.setUnique(false);
                    newCD.setUnique(false);
                    modify.getChangedAttributes().remove("unique");
                }
                if (!modify.getChangedAttributes().isEmpty()) {
                    TableDefinition td4 = null;
                    final String columnName = oldCD.getColumnName();
                    try {
                        DDLChanges.OUT.log(Level.INFO, "Getting TableDefintion for table " + tableName2 + ", to get column's dependent foreign-keys to drop and add while modifying column " + columnName + " and to set isKey() value in modified columnDefinition.");
                        td4 = MetaDataUtil.getTableDefinitionByName(tableName2);
                        if (td4 == null) {
                            throw new IllegalArgumentException("TableDefinition for table '" + tableName2 + "', is missing in meta-data cache, while constructing modify_column query for '" + columnName + "'.");
                        }
                        final ColumnDefinition cacheCD = td4.getColumnDefinitionByName(columnName);
                        if (cacheCD == null) {
                            throw new IllegalArgumentException("ColumnDefinition for column '" + tableName2 + "'.'" + columnName + "', is missing in meta-data cache, while constructing modify_column query.");
                        }
                        if (cacheCD.isKey()) {
                            oldCD.setNullable(false);
                        }
                        final TableDefinition newTd2 = ElementTransformer.getTableDefinition((Element)modify.getNewElement().getParentNode().getParentNode());
                        if (newTd2.getColumnDefinitionByName(columnName).isKey()) {
                            newCD.setNullable(false);
                        }
                    }
                    catch (final MetaDataException mde) {
                        throw new IllegalArgumentException("Exception Occured while using \"MetaDataUtil.getTableDefinitionByName(tableName)\" method.", mde);
                    }
                    DDLChanges.OUT.log(Level.INFO, "processing MODIFY_COLUMN query for column {0}.{1} modified in {2}", new Object[] { tableName2, oldCD.getColumnName(), modify.getChangedAttributes() });
                    final AlterTableQuery aq7 = this.getATQforModifyColumn(tableName2, oldCD, newCD, modify.getChangedAttributes());
                    this.installQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(aq7);
                    final AlterTableQuery aq8 = this.getATQforModifyColumn(tableName2, newCD, oldCD, modify.getChangedAttributes());
                    this.revertQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(aq8);
                    if (!modify.getChangedAttributes().contains("data-type") && (oldCD.getDataType().equals("CHAR") || oldCD.getDataType().equals("NCHAR") || oldCD.getDataType().equals("DECIMAL")) && modify.getChangedAttributes().contains("max-size") && this.ignoreMaxSizeReduction) {
                        if (this.operationType == DDLOperationType.INSTALL) {
                            final AlterOperation ao = aq8.getAlterOperations().get(0);
                            ao.ignoreMaxSizeReduction();
                        }
                        else {
                            final AlterOperation ao = aq7.getAlterOperations().get(0);
                            ao.ignoreMaxSizeReduction();
                        }
                    }
                    if (!aq7.isExecutable()) {
                        continue;
                    }
                    try {
                        final List<ForeignKeyDefinition> referringFKs = MetaDataUtil.getReferringForeignKeyDefinitions(tableName2, columnName);
                        for (final ForeignKeyDefinition fkDef : referringFKs) {
                            this.dropAndreCreateFK(fkDef);
                        }
                    }
                    catch (final MetaDataException e12) {
                        throw new IllegalArgumentException("Exception Occured while using \"MetaDataUtil.getReferringForeignKeyDefinitions(tableName, columnName)\" method.", e12);
                    }
                    final List<ForeignKeyDefinition> fks2 = td4.getForeignKeyList();
                    for (final ForeignKeyDefinition fk2 : fks2) {
                        if (fk2.childColumnNames().contains(columnName)) {
                            this.dropAndreCreateFK(fk2);
                        }
                    }
                }
            }
        }
        if (!modifiedIdxs.isEmpty()) {
            for (final ModifiedElement modify : modifiedIdxs) {
                boolean needToDrop = true;
                boolean needToAdd = true;
                final String tableName4 = modify.getTableName();
                TableDefinition td5 = null;
                try {
                    td5 = MetaDataUtil.getTableDefinitionByName(tableName4);
                }
                catch (final Exception e13) {
                    throw new IllegalArgumentException("Exception got while getting TableDefinition from MetaData", e13);
                }
                if (td5 == null) {
                    throw new IllegalArgumentException("TableDefinition for table '" + tableName4 + "', is missing in meta-data cache, while constructing modify_index query for '" + modify.getOldElement().getAttribute("name") + "'.");
                }
                final IndexDefinition oldIdx = ElementTransformer.getIndexDefinition(modify.getOldElement(), td5);
                final TableDefinition newTd3 = ElementTransformer.getTableDefinition((Element)modify.getNewElement().getParentNode().getParentNode());
                final IndexDefinition newIdx = ElementTransformer.getIndexDefinition(modify.getNewElement(), newTd3);
                if (!RelationalAPI.getInstance().getDBAdapter().isIndexModified(oldIdx, newIdx, modify.getChangedAttributes())) {
                    continue;
                }
                if (td5.getIndexDefinitionByName(oldIdx.getName()) == null) {
                    DDLChanges.OUT.log(Level.INFO, "Ignoring the drop index \"{0}\" query, since the index is not created.", new String[] { oldIdx.getName() });
                    needToDrop = false;
                }
                if (this.isIndex_Duplicate_Of_OtherConstraint(newTd3, newIdx)) {
                    needToAdd = false;
                }
                if (needToAdd) {
                    final AlterTableQuery aq9 = this.getATQforAddIndex(tableName4, newIdx);
                    this.installQueries[DDLOperation.ADD_IDX.ordinal()].add(aq9);
                    final AlterTableQuery aq10 = this.getATQforDropIndex(tableName4, newIdx.getName());
                    this.revertQueries[DDLOperation.DROP_IDX.ordinal()].add(aq10);
                }
                if (!needToDrop) {
                    continue;
                }
                final AlterTableQuery aq9 = this.getATQforDropIndex(tableName4, oldIdx.getName());
                this.installQueries[DDLOperation.DROP_IDX.ordinal()].add(aq9);
                final AlterTableQuery aq10 = this.getATQforAddIndex(tableName4, oldIdx);
                this.revertQueries[DDLOperation.ADD_IDX.ordinal()].add(aq10);
            }
        }
        if (!modifiedPKs.isEmpty()) {
            for (final ModifiedElement modify : modifiedPKs) {
                final String tableName2 = modify.getTableName();
                if (modify.getChangedAttributes().contains("primary-key-column")) {
                    final PrimaryKeyDefinition oldPK = ElementTransformer.getPrimaryKeyDefinition(modify.getOldElement());
                    final PrimaryKeyDefinition newPK = ElementTransformer.getPrimaryKeyDefinition(modify.getNewElement());
                    final AlterTableQuery aq11 = this.getATQforDropPrimaryKey(tableName2, oldPK.getName());
                    final AlterTableQuery aq4 = this.getATQforAddPrimaryKey(tableName2, newPK);
                    this.installQueries[DDLOperation.DROP_PK.ordinal()].add(aq11);
                    this.installQueries[DDLOperation.ADD_PK.ordinal()].add(aq4);
                    final AlterTableQuery aq12 = this.getATQforDropPrimaryKey(tableName2, newPK.getName());
                    final AlterTableQuery aq13 = this.getATQforAddPrimaryKey(tableName2, oldPK);
                    this.revertQueries[DDLOperation.DROP_PK.ordinal()].add(aq12);
                    this.revertQueries[DDLOperation.ADD_PK.ordinal()].add(aq13);
                    final List<String> oldPKColumns = oldPK.getColumnList();
                    final List<String> newPKColumns = newPK.getColumnList();
                    for (final String columnName : oldPKColumns) {
                        if (!newPKColumns.contains(columnName)) {
                            final TableDefinition newTd4 = ElementTransformer.getTableDefinition((Element)modify.getNewElement().getParentNode());
                            ColumnDefinition cd3 = newTd4.getColumnDefinitionByName(columnName);
                            final TableDefinition oldTd2 = ElementTransformer.getTableDefinition((Element)modify.getOldElement().getParentNode());
                            final ColumnDefinition oldColDef = oldTd2.getColumnDefinitionByName(columnName);
                            if (cd3 == null) {
                                cd3 = oldColDef;
                            }
                            cd3.setUnique(false);
                            oldColDef.setUnique(false);
                            if (!cd3.isNullable()) {
                                continue;
                            }
                            boolean isColumnAlreadyAddedForModification = false;
                            final List<AlterTableQuery> modifiedColumnsATQList = this.getModifiedColumns(DDLOperationType.INSTALL);
                            for (final AlterTableQuery atq8 : modifiedColumnsATQList) {
                                final ColumnDefinition colDef = (ColumnDefinition)atq8.getAlterOperations().get(0).getAlterObject();
                                if (atq8.getTableName().equals(tableName2) && colDef.getColumnName().equals(columnName)) {
                                    isColumnAlreadyAddedForModification = true;
                                    break;
                                }
                            }
                            if (isColumnAlreadyAddedForModification) {
                                continue;
                            }
                            DDLChanges.OUT.log(Level.INFO, "processing MODIFY_COLUMN query for column {0}.{1} to drop not-null constraint, as column removed from PK.", new Object[] { tableName2, columnName });
                            final List<String> modifiedAttributes = new ArrayList<String>();
                            modifiedAttributes.add("nullable");
                            AlterTableQuery atq9 = null;
                            if (newTd4.getColumnDefinitionByName(columnName) != null) {
                                atq9 = this.getATQforModifyColumn(tableName2, oldColDef, cd3, modifiedAttributes);
                            }
                            this.installQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(atq9);
                            oldColDef.setNullable(false);
                            final AlterTableQuery atq10 = this.getATQforModifyColumn(tableName2, cd3, oldColDef, modifiedAttributes);
                            this.revertQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(atq10);
                        }
                    }
                    final Element oldTableElement = (Element)modify.getOldElement().getParentNode();
                    final Element newTableElement = (Element)modify.getNewElement().getParentNode();
                    final List<UniqueKeyDefinition> oldUKsDefinedInXML = this.getUniqueKeysOfTable(oldTableElement);
                    final List<UniqueKeyDefinition> newUKsDefinedInXML = this.getUniqueKeysOfTable(newTableElement);
                    TableDefinition tdInCache;
                    try {
                        tdInCache = MetaDataUtil.getTableDefinitionByName(tableName2);
                        if (tdInCache == null) {
                            throw new IllegalArgumentException("TableDefinition for table '" + tableName2 + "', is missing in meta-data cache, while checking duplicate UK.");
                        }
                    }
                    catch (final MetaDataException ex) {
                        throw new IllegalArgumentException("Exception Occured while using \"MetaDataUtil.getTableDefinitionByName(tableName)\" method.", ex);
                    }
                    for (final UniqueKeyDefinition oldxmlUK : oldUKsDefinedInXML) {
                        if (oldxmlUK.getColumns().equals(oldPKColumns)) {
                            boolean breakTheLoop = false;
                            for (final UniqueKeyDefinition newxmlUK : newUKsDefinedInXML) {
                                if (oldxmlUK.getName().equals(newxmlUK.getName()) && oldxmlUK.getColumns().equals(newxmlUK.getColumns())) {
                                    final UniqueKeyDefinition ukInCache = tdInCache.getUniqueKeyDefinitionByName(oldxmlUK.getName());
                                    if (ukInCache == null) {
                                        final AlterTableQuery atq11 = this.getATQforAddUniqueKey(tableName2, oldxmlUK);
                                        this.installQueries[DDLOperation.ADD_UK.ordinal()].add(atq11);
                                        final AlterTableQuery atq12 = this.getATQforDropUniqueKey(tableName2, oldxmlUK);
                                        this.revertQueries[DDLOperation.DROP_UK.ordinal()].add(atq12);
                                    }
                                    breakTheLoop = true;
                                    break;
                                }
                            }
                            if (breakTheLoop) {
                                break;
                            }
                            continue;
                        }
                    }
                    for (final UniqueKeyDefinition newXmlUK : newUKsDefinedInXML) {
                        if (newXmlUK.getColumns().equals(newPKColumns)) {
                            for (final UniqueKeyDefinition oldXmlUK : oldUKsDefinedInXML) {
                                if (newXmlUK.getName().equals(oldXmlUK.getName()) && newXmlUK.getColumns().equals(oldXmlUK.getColumns())) {
                                    final UniqueKeyDefinition ukInCache2 = tdInCache.getUniqueKeyDefinitionByName(newXmlUK.getName());
                                    if (ukInCache2 == null) {
                                        break;
                                    }
                                    if (!ukInCache2.getColumns().equals(newXmlUK.getColumns())) {
                                        DDLChanges.OUT.log(Level.SEVERE, "UK column(s) list for \"{0}\" constraint varies between meta-data cache [{1}] and the one defined in xml [{2}].", new Object[] { newXmlUK.getName(), ukInCache2.getColumns(), newXmlUK.getColumns() });
                                        break;
                                    }
                                    final AlterTableQuery atq13 = this.getATQforDropUniqueKey(tableName2, newXmlUK);
                                    this.installQueries[DDLOperation.DROP_UK.ordinal()].add(atq13);
                                    final AlterTableQuery atq14 = this.getATQforAddUniqueKey(tableName2, newXmlUK);
                                    this.revertQueries[DDLOperation.ADD_UK.ordinal()].add(atq14);
                                    break;
                                }
                            }
                        }
                    }
                    for (final String columnName2 : newPKColumns) {
                        if (!oldPKColumns.contains(columnName2)) {
                            final TableDefinition newTd5 = ElementTransformer.getTableDefinition((Element)modify.getNewElement().getParentNode());
                            final ColumnDefinition cd4 = newTd5.getColumnDefinitionByName(columnName2);
                            if (!cd4.isNullable()) {
                                continue;
                            }
                            boolean isColumnAlreadyAddedForModification2 = false;
                            final List<AlterTableQuery> modifiedColumnsATQList2 = this.getModifiedColumns(DDLOperationType.INSTALL);
                            for (final AlterTableQuery atq15 : modifiedColumnsATQList2) {
                                final ColumnDefinition colDef2 = (ColumnDefinition)atq15.getAlterOperations().get(0).getAlterObject();
                                if (atq15.getTableName().equals(tableName2) && colDef2.getColumnName().equals(columnName2)) {
                                    isColumnAlreadyAddedForModification2 = true;
                                    break;
                                }
                            }
                            if (isColumnAlreadyAddedForModification2) {
                                continue;
                            }
                            DDLChanges.OUT.log(Level.INFO, "processing MODIFY_COLUMN query for column {0}.{1} to add not-null constraint, as column added to PK.", new Object[] { tableName2, columnName2 });
                            final List<String> modifiedAttributes2 = new ArrayList<String>();
                            modifiedAttributes2.add("nullable");
                            final TableDefinition oldTd3 = ElementTransformer.getTableDefinition((Element)modify.getOldElement().getParentNode());
                            ColumnDefinition oldColDef2 = oldTd3.getColumnDefinitionByName(columnName2);
                            if (oldColDef2 == null) {
                                oldColDef2 = cd4;
                            }
                            cd4.setUnique(false);
                            oldColDef2.setUnique(false);
                            cd4.setNullable(false);
                            final AlterTableQuery atq16 = this.getATQforModifyColumn(tableName2, oldColDef2, cd4, modifiedAttributes2);
                            this.installQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(atq16);
                            AlterTableQuery atq17 = null;
                            if (oldTd3.getColumnDefinitionByName(columnName2) != null) {
                                atq17 = this.getATQforModifyColumn(tableName2, cd4, oldColDef2, modifiedAttributes2);
                            }
                            this.revertQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(atq17);
                        }
                    }
                }
                else {
                    final AlterTableQuery aq = this.getATQforModifyPrimaryKey(tableName2, modify.getOldElement(), modify.getNewElement());
                    this.installQueries[DDLOperation.MODIFY_PK.ordinal()].add(aq);
                    final AlterTableQuery aq2 = this.getATQforModifyPrimaryKey(tableName2, modify.getNewElement(), modify.getOldElement());
                    this.revertQueries[DDLOperation.MODIFY_PK.ordinal()].add(aq2);
                }
            }
        }
        if (!modifiedUKs.isEmpty()) {
            for (final ModifiedElement modify : modifiedUKs) {
                final String tableName2 = modify.getTableName();
                final TableDefinition oldTd = ElementTransformer.getTableDefinition((Element)modify.getOldElement().getParentNode().getParentNode());
                final UniqueKeyDefinition oldUK = ElementTransformer.getUniqueKeyDefinition(modify.getOldElement());
                if (this.isUK_duplicate_of_PK_orAnotherUK_of_Table(oldTd, oldUK, false)) {
                    DDLChanges.OUT.log(Level.INFO, "Ignoring the delete Unique-key \"{0}\" query for change in unique-column list, as entry for UK is not there in meta-data cache(since earlier it is duplicate of either Primary-key or another Unique-key of table \"{1}\").", new String[] { oldUK.getName(), tableName2 });
                }
                else {
                    final AlterTableQuery atq18 = this.getATQforDropUniqueKey(tableName2, oldUK);
                    this.installQueries[DDLOperation.DROP_UK.ordinal()].add(atq18);
                    final AlterTableQuery atq19 = this.getATQforAddUniqueKey(tableName2, oldUK);
                    this.revertQueries[DDLOperation.ADD_UK.ordinal()].add(atq19);
                }
                final TableDefinition newTd6 = ElementTransformer.getTableDefinition((Element)modify.getNewElement().getParentNode().getParentNode());
                final UniqueKeyDefinition newUK2 = ElementTransformer.getUniqueKeyDefinition(modify.getNewElement());
                if (this.isUK_duplicate_of_PK_orAnotherUK_of_Table(newTd6, newUK2, true)) {
                    DDLChanges.OUT.log(Level.INFO, "Ignoring the add Unique-key \"{0}\" query for change in unique-column list, since it is duplicate of either Primary-key or another Unique-key of table \"{1}\".", new String[] { newUK2.getName(), tableName2 });
                }
                else {
                    final AlterTableQuery atq20 = this.getATQforAddUniqueKey(tableName2, newUK2);
                    this.installQueries[DDLOperation.ADD_UK.ordinal()].add(atq20);
                    final AlterTableQuery atq21 = this.getATQforDropUniqueKey(tableName2, newUK2);
                    this.revertQueries[DDLOperation.DROP_UK.ordinal()].add(atq21);
                }
            }
        }
        if (!addedIdxs.isEmpty()) {
            for (final AddedElement add : addedIdxs) {
                final String tableName2 = add.getTableName();
                final TableDefinition td2 = ElementTransformer.getTableDefinition((Element)add.getElement().getParentNode().getParentNode());
                final IndexDefinition idx = ElementTransformer.getIndexDefinition(add.getElement(), td2);
                if (this.isIndex_Duplicate_Of_OtherConstraint(td2, idx)) {
                    DDLChanges.OUT.log(Level.INFO, "Ignoring the add index \"{0}\" query, since other constraint is defined for same set of column.", new String[] { idx.getName() });
                }
                else {
                    final AlterTableQuery aq3 = this.getATQforAddIndex(tableName2, idx);
                    this.installQueries[DDLOperation.ADD_IDX.ordinal()].add(aq3);
                    final AlterTableQuery aq4 = this.getATQforDropIndex(tableName2, idx.getName());
                    this.revertQueries[DDLOperation.DROP_IDX.ordinal()].add(aq4);
                }
            }
        }
        if (!addedUKs.isEmpty()) {
            for (final AddedElement add : addedUKs) {
                final String tableName2 = add.getTableName();
                final TableDefinition newTd7 = ElementTransformer.getTableDefinition((Element)add.getElement().getParentNode().getParentNode());
                final UniqueKeyDefinition newUK3 = ElementTransformer.getUniqueKeyDefinition(add.getElement());
                if (this.isUK_duplicate_of_PK_orAnotherUK_of_Table(newTd7, newUK3, true)) {
                    DDLChanges.OUT.log(Level.INFO, "Ignoring the add Unique-key \"{0}\" query, since it is duplicate of either Primary-key or another Unique-key of table \"{1}\".", new String[] { newUK3.getName(), tableName2 });
                }
                else {
                    final AlterTableQuery aq3 = this.getATQforAddUniqueKey(tableName2, newUK3);
                    this.installQueries[DDLOperation.ADD_UK.ordinal()].add(aq3);
                    final AlterTableQuery aq4 = this.getATQforDropUniqueKey(tableName2, newUK3);
                    this.revertQueries[DDLOperation.DROP_UK.ordinal()].add(aq4);
                }
            }
        }
        if (!addedTables.isEmpty()) {
            for (final AddedElement add : addedTables) {
                final TableDefinition td3 = ElementTransformer.getTableDefinition(add.getElement());
                final String tableName3 = td3.getTableName();
                for (final ColumnDefinition cd : td3.getColumnList()) {
                    try {
                        final String maxValueInCustomUtil = MetaDataUtil.getAttribute(tableName3, cd.getColumnName(), "maxsize");
                    }
                    catch (final MetaDataException e14) {
                        e14.printStackTrace();
                        throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName3, cd.getColumnName(), "maxsize") + " from metadata");
                    }
                    String maxValueInCustomUtil;
                    final Integer extendedMaxVal2 = ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getNewExtendedMaxValue(tableName3, cd.getColumnName()) : ((maxValueInCustomUtil != null) ? Integer.valueOf(Integer.parseInt(maxValueInCustomUtil)) : null);
                    if (extendedMaxVal2 != null) {
                        cd.setMaxLength(extendedMaxVal2);
                    }
                    String extendedDefVal2;
                    try {
                        extendedDefVal2 = (ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getNewExtendedDefaultValue(tableName3, cd.getColumnName()) : MetaDataUtil.getAttribute(tableName3, cd.getColumnName(), "defaultvalue"));
                    }
                    catch (final MetaDataException e4) {
                        e4.printStackTrace();
                        throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName3, cd.getColumnName(), "defaultvalue") + " from metadata");
                    }
                    if (extendedDefVal2 != null) {
                        try {
                            cd.setDefaultValue(extendedDefVal2);
                        }
                        catch (final MetaDataException e5) {
                            e5.printStackTrace();
                        }
                    }
                }
                final List<ForeignKeyDefinition> fks = new ArrayList<ForeignKeyDefinition>(td3.getForeignKeyList());
                for (final ForeignKeyDefinition fk : fks) {
                    final String fkName = fk.getName();
                    this.installQueries[DDLOperation.ADD_FK.ordinal()].add(this.getATQforAddForeignKey(tableName3, fk));
                    this.revertQueries[DDLOperation.DROP_FK.ordinal()].add(this.getATQforDropForeignKey(tableName3, fkName));
                    td3.removeForeignKey(fkName);
                }
                this.installQueries[DDLOperation.CREATE_TABLE.ordinal()].add(td3);
                this.revertQueries[DDLOperation.DROP_TABLE.ordinal()].add(tableName3);
            }
        }
        if (!modifiedTables.isEmpty()) {
            for (final ModifiedElement modify : modifiedTables) {
                final String tableName2 = modify.getTableName();
                final AlterTableQuery aq = this.getATQforModifyTable(tableName2, modify.getNewElement(), modify.getChangedAttributes());
                final AlterTableQuery aq2 = this.getATQforModifyTable(tableName2, modify.getOldElement(), modify.getChangedAttributes());
                this.installQueries[DDLOperation.MODIFY_TABLE.ordinal()].add(0, aq);
                this.revertQueries[DDLOperation.MODIFY_TABLE.ordinal()].add(0, aq2);
            }
        }
        if (!addedFKs.isEmpty()) {
            for (final AddedElement add : addedFKs) {
                final String tableName2 = add.getTableName();
                this.installQueries[DDLOperation.ADD_FK.ordinal()].add(this.getATQforAddForeignKey(tableName2, add.getElement()));
                this.revertQueries[DDLOperation.DROP_FK.ordinal()].add(this.getATQforDropForeignKey(tableName2, add.getElement()));
            }
        }
        if (!droppedColumns.isEmpty()) {
            for (final DeletedElement drop : droppedColumns) {
                final String tableName2 = drop.getTableName();
                final ColumnDefinition cd5 = ElementTransformer.getColumnDefinition(drop.getElement());
                if (cd5.isUnique()) {
                    cd5.setUnique(false);
                }
                final AlterTableQuery aq14 = this.getATQforDropColumn(tableName2, drop.getElement());
                this.installQueries[DDLOperation.DROP_COLUMN.ordinal()].add(aq14);
                boolean needtoModify = false;
                try {
                    final String maxValueInCustomUtil = MetaDataUtil.getAttribute(tableName2, cd5.getColumnName(), "maxsize");
                }
                catch (final MetaDataException e3) {
                    e3.printStackTrace();
                    throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName2, cd5.getColumnName(), "maxsize") + " from metadata");
                }
                String maxValueInCustomUtil;
                final Integer oldExtendedMaxVal = ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getOldExtendedMaxValue(tableName2, cd5.getColumnName()) : ((maxValueInCustomUtil != null) ? Integer.valueOf(Integer.parseInt(maxValueInCustomUtil)) : null);
                if (oldExtendedMaxVal != null) {
                    cd5.setMaxLength(oldExtendedMaxVal);
                }
                String extendedDefVal2;
                try {
                    extendedDefVal2 = (ExtendedDDAggregator.isExtendedDDModified() ? ExtendedDDAggregator.getOldExtendedDefaultValue(tableName2, cd5.getColumnName()) : MetaDataUtil.getAttribute(tableName2, cd5.getColumnName(), "defaultvalue"));
                }
                catch (final MetaDataException e4) {
                    e4.printStackTrace();
                    throw new IllegalArgumentException("Exception occurred while getting custom attribute value " + this.getKey(tableName2, cd5.getColumnName(), "defaultvalue") + " from metadata");
                }
                if (extendedDefVal2 != null) {
                    try {
                        cd5.setDefaultValue(extendedDefVal2);
                    }
                    catch (final MetaDataException e5) {
                        e5.printStackTrace();
                    }
                }
                if (!cd5.isNullable() && cd5.getDefaultValue() == null) {
                    cd5.setNullable(true);
                    needtoModify = true;
                }
                final AlterTableQuery aq6 = this.getATQforAddColumn(tableName2, cd5);
                this.revertQueries[DDLOperation.ADD_COLUMN.ordinal()].add(aq6);
                if (needtoModify) {
                    final List dummyList2 = new ArrayList();
                    dummyList2.add("nullable");
                    final ColumnDefinition colDef3 = ElementTransformer.getColumnDefinition(drop.getElement());
                    if (oldExtendedMaxVal != null) {
                        colDef3.setMaxLength(oldExtendedMaxVal);
                    }
                    if (extendedDefVal2 != null) {
                        try {
                            colDef3.setDefaultValue(extendedDefVal2);
                        }
                        catch (final MetaDataException e15) {
                            e15.printStackTrace();
                        }
                    }
                    colDef3.setUnique(false);
                    final AlterTableQuery atq22 = this.getATQforModifyColumn(tableName2, colDef3, colDef3, dummyList2);
                    this.revertQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(atq22);
                    final AlterTableQuery atq6 = null;
                    this.installQueries[DDLOperation.MODIFY_COLUMN.ordinal()].add(atq6);
                }
                try {
                    DDLChanges.OUT.log(Level.INFO, "Getting TableDefintion for table {0}, to get Unique-key name to drop while dropping unique column {1}.", new String[] { tableName2, (String)aq14.getAlterOperations().get(0).getAlterObject() });
                    final TableDefinition td6 = MetaDataUtil.getTableDefinitionByName(tableName2);
                    if (td6 == null) {
                        throw new IllegalArgumentException("TableDefinition for table '" + tableName2 + "', is missing in meta-data cache, while constructing delete_column query for '" + cd5.getColumnName() + "'.");
                    }
                    final List<UniqueKeyDefinition> uks = td6.getUniqueKeys();
                    if (uks == null) {
                        continue;
                    }
                    for (final UniqueKeyDefinition uk : uks) {
                        final List<String> ukColumns = uk.getColumns();
                        if (ukColumns.size() == 1 && ukColumns.contains(aq14.getAlterOperations().get(0).getAlterObject())) {
                            boolean alreadyMarked = false;
                            for (final AlterTableQuery query : this.installQueries[DDLOperation.DROP_UK.ordinal()]) {
                                if (((String)query.getAlterOperations().get(0).getAlterObject()).equals(uk.getName())) {
                                    alreadyMarked = true;
                                    break;
                                }
                            }
                            if (alreadyMarked) {
                                continue;
                            }
                            final AlterTableQuery atq23 = new AlterTableQueryImpl(tableName2);
                            final AlterTableQuery atq24 = new AlterTableQueryImpl(tableName2);
                            try {
                                atq23.removeUniqueKey(uk.getName());
                                atq24.addUniqueKey(uk);
                            }
                            catch (final QueryConstructionException e16) {
                                throw new IllegalArgumentException(e16);
                            }
                            this.installQueries[DDLOperation.DROP_UK.ordinal()].add(atq23);
                            this.revertQueries[DDLOperation.ADD_UK.ordinal()].add(atq24);
                        }
                    }
                }
                catch (final MetaDataException e17) {
                    throw new IllegalArgumentException("Exception Occured while using \"MetaDataUtil.getTableDefinitionByName(tableName)\" method.", e17);
                }
            }
        }
        try {
            final List<AlterTableQuery> dropPKs = this.getDroppedPrimaryKeys(DDLOperationType.INSTALL);
            final List<AlterTableQuery> newUKs = this.getAddedUniqueKeys(DDLOperationType.INSTALL);
            for (final AlterTableQuery dropPK : dropPKs) {
                final TableDefinition td7 = MetaDataUtil.getTableDefinitionByName(dropPK.getTableName());
                if (td7 == null) {
                    throw new IllegalArgumentException("TableDefinition for table '" + dropPK.getTableName() + "', is missing in meta-data cache, while checking PK to UK movement check.");
                }
                final PrimaryKeyDefinition oldPKDef = td7.getPrimaryKey();
                boolean isMarkedforNewUK = false;
                for (final AlterTableQuery addUK : newUKs) {
                    if (addUK.getTableName().equals(dropPK.getTableName()) && ((UniqueKeyDefinition)addUK.getAlterOperations().get(0).getAlterObject()).getColumns().equals(oldPKDef.getColumnList())) {
                        isMarkedforNewUK = true;
                        break;
                    }
                }
                if (!isMarkedforNewUK) {
                    continue;
                }
                final List<ForeignKeyDefinition> refFKs = MetaDataUtil.getReferringForeignKeyDefinitions(dropPK.getTableName());
                if (refFKs == null) {
                    continue;
                }
                for (final ForeignKeyDefinition fk3 : refFKs) {
                    if (oldPKDef.getColumnList().equals(fk3.getFkRefColumns())) {
                        final String fkName2 = fk3.getName();
                        boolean alreadyMarked2 = false;
                        final List<AlterTableQuery> dropFKs = this.getDroppedForeignKeys(DDLOperationType.INSTALL);
                        for (final AlterTableQuery atq25 : dropFKs) {
                            if (((String)atq25.getAlterOperations().get(0).getAlterObject()).equals(fkName2)) {
                                alreadyMarked2 = true;
                                break;
                            }
                        }
                        if (alreadyMarked2) {
                            continue;
                        }
                        final AlterTableQuery addFK = this.getATQforAddForeignKey(fk3.getSlaveTableName(), fk3);
                        final AlterTableQuery dropFK = this.getATQforDropForeignKey(fk3.getSlaveTableName(), fkName2);
                        this.installQueries[DDLOperation.DROP_FK.ordinal()].add(dropFK);
                        this.revertQueries[DDLOperation.ADD_FK.ordinal()].add(addFK);
                        this.installQueries[DDLOperation.ADD_FK.ordinal()].add(addFK);
                        this.revertQueries[DDLOperation.DROP_FK.ordinal()].add(dropFK);
                    }
                }
            }
            final List<AlterTableQuery> dropUKs = this.getDroppedUniqueKeys(DDLOperationType.INSTALL);
            final List<AlterTableQuery> newPKs = this.getAddedPrimaryKeys(DDLOperationType.INSTALL);
            for (final AlterTableQuery dropUK : dropUKs) {
                final TableDefinition td8 = MetaDataUtil.getTableDefinitionByName(dropUK.getTableName());
                if (td8 == null) {
                    throw new IllegalArgumentException("TableDefinition for table '" + dropUK.getTableName() + "', is missing in meta-data cache, while checking PK to UK movement check.");
                }
                final UniqueKeyDefinition oldUKDef = td8.getUniqueKeyDefinitionByName((String)dropUK.getAlterOperations().get(0).getAlterObject());
                boolean isMarkedForNewPK = false;
                for (final AlterTableQuery addPK : newPKs) {
                    if (addPK.getTableName().equals(dropUK.getTableName()) && ((PrimaryKeyDefinition)addPK.getAlterOperations().get(0).getAlterObject()).getColumnList().equals(oldUKDef.getColumns())) {
                        isMarkedForNewPK = true;
                        break;
                    }
                }
                if (!isMarkedForNewPK) {
                    continue;
                }
                final List<ForeignKeyDefinition> refFKs2 = MetaDataUtil.getReferringForeignKeyDefinitions(dropUK.getTableName());
                if (refFKs2 == null) {
                    continue;
                }
                for (final ForeignKeyDefinition fk4 : refFKs2) {
                    if (oldUKDef.getColumns().equals(fk4.getFkRefColumns())) {
                        final String fkName3 = fk4.getName();
                        boolean alreadyMarked3 = false;
                        final List<AlterTableQuery> dropFKs2 = this.getDroppedForeignKeys(DDLOperationType.INSTALL);
                        for (final AlterTableQuery atq23 : dropFKs2) {
                            if (((String)atq23.getAlterOperations().get(0).getAlterObject()).equals(fkName3)) {
                                alreadyMarked3 = true;
                                break;
                            }
                        }
                        if (alreadyMarked3) {
                            continue;
                        }
                        final AlterTableQuery addFK2 = this.getATQforAddForeignKey(fk4.getSlaveTableName(), fk4);
                        final AlterTableQuery dropFK2 = this.getATQforDropForeignKey(fk4.getSlaveTableName(), fkName3);
                        this.installQueries[DDLOperation.DROP_FK.ordinal()].add(dropFK2);
                        this.revertQueries[DDLOperation.ADD_FK.ordinal()].add(addFK2);
                        this.installQueries[DDLOperation.ADD_FK.ordinal()].add(addFK2);
                        this.revertQueries[DDLOperation.DROP_FK.ordinal()].add(dropFK2);
                    }
                }
            }
        }
        catch (final MetaDataException e18) {
            throw new IllegalArgumentException("Exception Occured while using \"MetaDataUtil.getReferringForeignKeyDefinitions(tableName)\" method.", e18);
        }
    }
    
    private void dropAndreCreateFK(final ForeignKeyDefinition fk) {
        final String fkName = fk.getName();
        final String tableName = fk.getSlaveTableName();
        boolean isAlreadyMarkedForDrop = false;
        for (final AlterTableQuery atq : this.installQueries[DDLOperation.DROP_FK.ordinal()]) {
            if (((String)atq.getAlterOperations().get(0).getAlterObject()).equals(fkName)) {
                isAlreadyMarkedForDrop = true;
                break;
            }
        }
        if (!isAlreadyMarkedForDrop) {
            int modifyFKQueryIndex = -1;
            for (int j = 0; j < this.installQueries[DDLOperation.MODIFY_FK.ordinal()].size(); ++j) {
                final AlterTableQuery atq2 = this.installQueries[DDLOperation.MODIFY_FK.ordinal()].get(j);
                if (((ForeignKeyDefinition)atq2.getAlterOperations().get(0).getAlterObject()).getName().equals(fkName)) {
                    modifyFKQueryIndex = j;
                }
            }
            if (modifyFKQueryIndex != -1) {
                DDLChanges.OUT.log(Level.INFO, "Modified FK {0}.{1} is constructed as DROP and ADD, as the column present in this FK got modifed.", new String[] { tableName, fkName });
                final AlterTableQuery modifyFK_install_atq = this.installQueries[DDLOperation.MODIFY_FK.ordinal()].remove(modifyFKQueryIndex);
                final ForeignKeyDefinition newFK = (ForeignKeyDefinition)modifyFK_install_atq.getAlterOperations().get(0).getAlterObject();
                final AlterTableQuery modifyFK_revert_atq = this.revertQueries[DDLOperation.MODIFY_FK.ordinal()].remove(modifyFKQueryIndex);
                final ForeignKeyDefinition oldFK = (ForeignKeyDefinition)modifyFK_revert_atq.getAlterOperations().get(0).getAlterObject();
                this.installQueries[DDLOperation.DROP_FK.ordinal()].add(this.getATQforDropForeignKey(fk.getSlaveTableName(), fkName));
                this.revertQueries[DDLOperation.ADD_FK.ordinal()].add(this.getATQforAddForeignKey(fk.getSlaveTableName(), oldFK));
                this.installQueries[DDLOperation.ADD_FK.ordinal()].add(this.getATQforAddForeignKey(fk.getSlaveTableName(), newFK));
                this.revertQueries[DDLOperation.DROP_FK.ordinal()].add(this.getATQforDropForeignKey(fk.getSlaveTableName(), fkName));
            }
            else {
                final AlterTableQuery addFK = this.getATQforAddForeignKey(tableName, fk);
                final AlterTableQuery dropFK = this.getATQforDropForeignKey(tableName, fkName);
                this.installQueries[DDLOperation.DROP_FK.ordinal()].add(dropFK);
                this.revertQueries[DDLOperation.ADD_FK.ordinal()].add(addFK);
                this.installQueries[DDLOperation.ADD_FK.ordinal()].add(addFK);
                this.revertQueries[DDLOperation.DROP_FK.ordinal()].add(dropFK);
            }
        }
    }
    
    private boolean isUK_duplicate_of_PK_orAnotherUK_of_Table(final TableDefinition td, final UniqueKeyDefinition uk, final boolean checkForAddition) {
        final PrimaryKeyDefinition pkDef = td.getPrimaryKey();
        if (pkDef.getColumnList().equals(uk.getColumns())) {
            return true;
        }
        try {
            final TableDefinition tdInCache = MetaDataUtil.getTableDefinitionByName(td.getTableName());
            if (tdInCache == null) {
                throw new IllegalArgumentException("TableDefinition for table '" + td.getTableName() + "', is missing in meta-data cache, while checking if '" + uk.getName() + "' is duplicating some othe unique-key in table.");
            }
            final List<UniqueKeyDefinition> uniqueKeys = td.getUniqueKeys();
            if (uniqueKeys != null) {
                for (final UniqueKeyDefinition ukDef : uniqueKeys) {
                    if (!ukDef.getName().equals(uk.getName()) && ukDef.getColumns().equals(uk.getColumns())) {
                        if (checkForAddition) {
                            boolean duplicatingExistingUK = false;
                            final List<UniqueKeyDefinition> uniqueKeysInCache = tdInCache.getUniqueKeys();
                            if (uniqueKeysInCache != null) {
                                for (final UniqueKeyDefinition ukInCache : uniqueKeysInCache) {
                                    if (ukInCache.getName().equals(ukDef.getName()) && ukInCache.getColumns().equals(ukDef.getColumns())) {
                                        duplicatingExistingUK = true;
                                        break;
                                    }
                                }
                            }
                            if (duplicatingExistingUK) {
                                return true;
                            }
                            final List<AlterTableQuery> addedUniqueKeys = this.getAddedUniqueKeys(DDLOperationType.INSTALL);
                            for (final AlterTableQuery addUK : addedUniqueKeys) {
                                if (addUK.getTableName().equals(td.getTableName()) && ((UniqueKeyDefinition)addUK.getAlterOperations().get(0).getAlterObject()).getColumns().equals(uk.getColumns())) {
                                    return true;
                                }
                            }
                        }
                        else {
                            if (tdInCache.getUniqueKeyDefinitionByName(uk.getName()) == null) {
                                return true;
                            }
                            continue;
                        }
                    }
                }
            }
        }
        catch (final MetaDataException e) {
            throw new IllegalArgumentException("Exception Occured while using \"MetaDataUtil.getTableDefinitionByName(tableName)\" method.", e);
        }
        return false;
    }
    
    private boolean isIndex_Duplicate_Of_OtherConstraint(final TableDefinition td, final IndexDefinition idx) {
        final List<String> idxColumns = idx.getColumns();
        final PrimaryKeyDefinition pkDef = td.getPrimaryKey();
        if (pkDef.getColumnList().equals(idxColumns)) {
            DDLChanges.OUT.log(Level.WARNING, "Ignoring IndexDefinition {0} of table {1}, as it redefinies the primary-key {2} of table.", new String[] { idx.getName(), td.getTableName(), pkDef.getName() });
            return true;
        }
        final List<UniqueKeyDefinition> uks = td.getUniqueKeys();
        if (uks != null) {
            for (final UniqueKeyDefinition ukDef : uks) {
                if (ukDef.getColumns().equals(idxColumns)) {
                    DDLChanges.OUT.log(Level.WARNING, "Ignoring IndexDefinition {0} of table {1}, as it redefinies the unique-key {2} of table.", new String[] { idx.getName(), td.getTableName(), ukDef.getName() });
                    return true;
                }
            }
        }
        final List<ForeignKeyDefinition> fks = td.getForeignKeyList();
        for (final ForeignKeyDefinition fk : fks) {
            if (fk.getFkColumns().equals(idxColumns)) {
                DDLChanges.OUT.log(Level.WARNING, "Ignoring IndexDefinition {0} of table {1}, as it redefinies the foreign-key {2} of table.", new String[] { idx.getName(), td.getTableName(), fk.getName() });
                return true;
            }
        }
        final List<IndexDefinition> idxs = td.getIndexes();
        if (idxs != null) {
            for (final IndexDefinition id : idxs) {
                if (id.getColumns().equals(idxColumns) && !id.getName().equals(idx.getName())) {
                    DDLChanges.OUT.log(Level.WARNING, "Ignoring IndexDefinition {0} of table {1}, as it redefinies the index {2} of table.", new String[] { idx.getName(), td.getTableName(), id.getName() });
                    return true;
                }
            }
        }
        return false;
    }
    
    private AlterTableQuery getATQforAddColumn(final String tableName, final ColumnDefinition columnDefinition) {
        final AlterTableQuery aq = new AlterTableQueryImpl(tableName);
        try {
            aq.addColumn(columnDefinition);
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return aq;
    }
    
    private AlterTableQuery getATQforDropColumn(final String tableName, final Element columnElement) {
        final AlterTableQuery aq = new AlterTableQueryImpl(tableName);
        final ColumnDefinition columnDefinition = ElementTransformer.getColumnDefinition(columnElement);
        try {
            aq.removeColumn(columnDefinition.getColumnName());
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return aq;
    }
    
    private AlterTableQuery getATQforModifyColumn(final String tableName, final ColumnDefinition oldColumnDefinition, final ColumnDefinition newColumnDefinition, final List<String> attributeNames) {
        final AlterTableQuery aq = new AlterTableQueryImpl(tableName);
        newColumnDefinition.setTableName(tableName);
        try {
            aq.modifyColumn(newColumnDefinition.getColumnName(), newColumnDefinition);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
        final boolean isExecutable = RelationalAPI.getInstance().getDBAdapter().isColumnModified(oldColumnDefinition, newColumnDefinition, attributeNames);
        aq.setIsExecutable(isExecutable);
        return aq;
    }
    
    private AlterTableQuery getATQforDropPrimaryKey(final String tableName, final String pkName) {
        final AlterTableQuery atq = new AlterTableQueryImpl(tableName, 8);
        atq.setConstraintName(pkName);
        return atq;
    }
    
    private AlterTableQuery getATQforAddPrimaryKey(final String tableName, final PrimaryKeyDefinition pkDef) {
        final AlterTableQuery atq = new AlterTableQueryImpl(tableName, 9);
        atq.setConstraintName(pkDef.getName());
        atq.setPKColumns(pkDef.getColumnList());
        return atq;
    }
    
    private AlterTableQuery getATQforModifyPrimaryKey(final String tableName, final Element oldPKElement, final Element newPKElement) {
        final AlterTableQuery atq = new AlterTableQueryImpl(tableName);
        final PrimaryKeyDefinition oldPK = ElementTransformer.getPrimaryKeyDefinition(oldPKElement);
        final PrimaryKeyDefinition newPK = ElementTransformer.getPrimaryKeyDefinition(newPKElement);
        try {
            atq.modifyPrimaryKey(oldPK.getName(), newPK);
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return atq;
    }
    
    private AlterTableQuery getATQforAddForeignKey(final String tableName, final Element foreignKeyElement) {
        final ForeignKeyDefinition fkDef = ElementTransformer.getForeignKeyDefinition(foreignKeyElement);
        return this.getATQforAddForeignKey(tableName, fkDef);
    }
    
    private AlterTableQuery getATQforAddForeignKey(final String tableName, final ForeignKeyDefinition fkDef) {
        final AlterTableQuery aq = new AlterTableQueryImpl(tableName);
        try {
            aq.addForeignKey(fkDef);
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return aq;
    }
    
    private AlterTableQuery getATQforDropForeignKey(final String tableName, final Element foreignKeyElement) {
        final ForeignKeyDefinition fkDef = ElementTransformer.getForeignKeyDefinition(foreignKeyElement);
        return this.getATQforDropForeignKey(tableName, fkDef.getName());
    }
    
    private AlterTableQuery getATQforDropForeignKey(final String tableName, final String fkName) {
        final AlterTableQuery aq = new AlterTableQueryImpl(tableName);
        try {
            aq.removeForeignKey(fkName);
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return aq;
    }
    
    private AlterTableQuery[] getATQforModifyForeignKey(final ModifiedElement modify) {
        final String tableName = modify.getTableName();
        final AlterTableQuery aq = new AlterTableQueryImpl(tableName);
        final AlterTableQuery aq2 = new AlterTableQueryImpl(tableName);
        final ForeignKeyDefinition oldFK = ElementTransformer.getForeignKeyDefinition(modify.getOldElement());
        final ForeignKeyDefinition newFK = ElementTransformer.getForeignKeyDefinition(modify.getNewElement());
        aq.modifyForeignKey(newFK);
        aq2.modifyForeignKey(oldFK);
        final List<String> attributes = modify.getChangedAttributes();
        if (!attributes.contains("fk-constraints")) {
            aq.setIsExecutable(false);
            aq2.setIsExecutable(false);
        }
        return new AlterTableQuery[] { aq, aq2 };
    }
    
    private AlterTableQuery getATQforAddUniqueKey(final String tableName, final ColumnDefinition cd) {
        final UniqueKeyDefinition ukDef = new UniqueKeyDefinition();
        ukDef.addColumn(cd.getColumnName());
        return this.getATQforAddUniqueKey(tableName, ukDef);
    }
    
    private AlterTableQuery getATQforAddUniqueKey(final String tableName, final UniqueKeyDefinition ukd) {
        final AlterTableQuery atq = new AlterTableQueryImpl(tableName);
        try {
            atq.addUniqueKey(ukd);
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return atq;
    }
    
    private UniqueKeyDefinition getMickeyUniqueKeyForColumn(final String tableName, final String columnName, final Element columnElement) {
        UniqueKeyDefinition ukDef = null;
        try {
            final Element tableElement = (Element)columnElement.getParentNode().getParentNode();
            final TableDefinition tableDefinition = ElementTransformer.getTableDefinition(tableElement);
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            if (td == null) {
                throw new IllegalArgumentException("TableDefinition for table '" + tableName + "', is missing in meta-data cache, while constructing drop_uk query for unique attribute change in '" + columnName + "' column.");
            }
            final List<UniqueKeyDefinition> uks = td.getUniqueKeys();
            final List<String> definedUKNames = new ArrayList<String>();
            if (tableDefinition.getUniqueKeys() != null) {
                for (final UniqueKeyDefinition uk : tableDefinition.getUniqueKeys()) {
                    if (!uk.isAutoGenerated()) {
                        definedUKNames.add(uk.getName());
                    }
                }
            }
            if (uks != null) {
                for (final UniqueKeyDefinition uk : uks) {
                    if (uk.getColumns().size() == 1 && uk.getColumns().contains(columnName) && !definedUKNames.contains(uk.getName())) {
                        ukDef = uk;
                    }
                }
            }
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
        return ukDef;
    }
    
    private AlterTableQuery getATQforDropUniqueKey(final String tableName, final UniqueKeyDefinition ukd) {
        final AlterTableQuery atq = new AlterTableQueryImpl(tableName);
        try {
            atq.removeUniqueKey(ukd.getName());
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return atq;
    }
    
    private AlterTableQuery getATQforAddIndex(final String tableName, final IndexDefinition idxDef) {
        final AlterTableQuery aq = new AlterTableQueryImpl(tableName);
        try {
            aq.addIndex(idxDef);
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return aq;
    }
    
    private AlterTableQuery getATQforDropIndex(final String tableName, final String idxName) {
        final AlterTableQuery aq = new AlterTableQueryImpl(tableName);
        try {
            aq.dropIndex(idxName);
        }
        catch (final QueryConstructionException e) {
            throw new IllegalArgumentException(e);
        }
        return aq;
    }
    
    private AlterTableQuery getATQforModifyTable(final String tableName, final Element newTableElement, final List<String> modifiedAttributes) {
        final TableDefinition newTd = ElementTransformer.getTableDefinition(newTableElement);
        final Properties tableProp = new Properties();
        for (final String attribute : modifiedAttributes) {
            if (attribute.equals("createtable")) {
                tableProp.setProperty(attribute, newTd.creatable() ? "true" : "false");
            }
            else if (attribute.equals("display-name")) {
                tableProp.setProperty(attribute, newTd.getDisplayName());
            }
            else if (attribute.equals("description")) {
                tableProp.setProperty(attribute, (newTd.getDescription() == null) ? "" : newTd.getDescription());
            }
            else if (attribute.equals("ddName")) {
                tableProp.setProperty("modulename", newTd.getModuleName());
            }
            else {
                if (!attribute.equals("dc-type")) {
                    continue;
                }
                tableProp.setProperty("dc-type", (newTd.getDynamicColumnType() == null) ? "" : newTd.getDynamicColumnType());
            }
        }
        AlterTableQuery atq = null;
        if (!tableProp.isEmpty()) {
            atq = new AlterTableQueryImpl(tableName);
            atq.modifyTableAttributes(tableProp);
            if (!modifiedAttributes.contains("createtable") && !modifiedAttributes.contains("dc-type")) {
                atq.setIsExecutable(false);
            }
        }
        return atq;
    }
    
    private AlterTableQuery getATQForModifyTablesDCType(final String tableName, final String newDCType) {
        final Properties tableProp = new Properties();
        tableProp.setProperty("dc-type", (newDCType == null) ? "" : newDCType);
        final AlterTableQuery atq = new AlterTableQueryImpl(tableName);
        atq.modifyTableAttributes(tableProp);
        return atq;
    }
    
    private String getDescriptionForDD(final Element ddElement) {
        String description = null;
        final NodeList nodes = ddElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)node;
                if (element.getTagName().equals("description")) {
                    final NodeList innerNodes = element.getChildNodes();
                    for (int j = 0; j < innerNodes.getLength(); ++j) {
                        final Node innerNode = innerNodes.item(j);
                        if (innerNode.getNodeType() == 3) {
                            description = ((Text)innerNode).getData();
                        }
                    }
                }
            }
        }
        return description;
    }
    
    private List<UniqueKeyDefinition> getUniqueKeysOfTable(final Element tableElement) {
        final List<UniqueKeyDefinition> uks = new ArrayList<UniqueKeyDefinition>();
        final NodeList childNodes = tableElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node node = childNodes.item(i);
            if (node.getNodeType() == 1) {
                final Element childElement = (Element)node;
                if (childElement.getTagName().equals("unique-keys")) {
                    final NodeList ukNodes = childElement.getChildNodes();
                    for (int j = 0; j < ukNodes.getLength(); ++j) {
                        final Node ukNode = ukNodes.item(j);
                        if (ukNode.getNodeType() == 1) {
                            final Element ukElement = (Element)ukNode;
                            if (ukElement.getTagName().equals("unique-key")) {
                                final UniqueKeyDefinition ukDef = ElementTransformer.getUniqueKeyDefinition(ukElement);
                                uks.add(ukDef);
                            }
                        }
                    }
                    break;
                }
            }
        }
        return uks;
    }
    
    private String getKey(final String tableName, final String columnName, final String attributeName) {
        String key = null;
        if (columnName != null) {
            key = tableName + "." + columnName + "." + attributeName;
        }
        else {
            key = tableName + "." + attributeName;
        }
        return key;
    }
    
    static {
        OUT = Logger.getLogger(DDLChanges.class.getName());
    }
    
    enum DDLOperation
    {
        MODIFY_DD, 
        DROP_FK, 
        DROP_UK, 
        DROP_PK, 
        DROP_IDX, 
        CREATE_TABLE, 
        ADD_COLUMN, 
        MODIFY_COLUMN, 
        ADD_PK, 
        ADD_UK, 
        ADD_IDX, 
        MODIFY_PK, 
        MODIFY_FK, 
        ADD_FK, 
        DROP_TABLE, 
        DROP_COLUMN, 
        MODIFY_TABLE;
    }
    
    public enum DDLOperationType
    {
        INSTALL, 
        REVERT;
    }
}
