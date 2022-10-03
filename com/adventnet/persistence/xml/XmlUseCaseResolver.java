package com.adventnet.persistence.xml;

import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.logging.Logger;
import java.util.List;

class XmlUseCaseResolver
{
    private List tableNames;
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    private boolean swap_parent_child;
    
    XmlUseCaseResolver(final List tableNames, final boolean swap_parent_child) {
        this.tableNames = tableNames;
        this.swap_parent_child = swap_parent_child;
    }
    
    ParentChildrenMap resolveUseCase(final ParentChildrenMap pcm) throws MetaDataException {
        final String tableName = pcm.getElementName();
        final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
        final List fkList = tableDef.getForeignKeyList();
        if (fkList == null) {
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "{0} has no foreign key.", tableName);
            pcm.setUseCaseType(1);
            return pcm;
        }
        final int size = fkList.size();
        if (size != 1) {
            return this.handleMultipleFKs(tableDef, fkList, pcm);
        }
        XmlUseCaseResolver.LOGGER.log(Level.FINEST, "{0} has one foreign key.", tableName);
        final ForeignKeyDefinition fkDef = fkList.get(0);
        if (!this.tableNames.contains(fkDef.getMasterTableName())) {
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "{0}'s parent does not present in the specified table list:{1}", new Object[] { tableName, this.tableNames });
            pcm.setUseCaseType(1);
            return pcm;
        }
        return this.handleSingleFK(tableDef, fkDef, pcm);
    }
    
    ParentChildrenMap handleSingleFK(final TableDefinition tableDef, final ForeignKeyDefinition fkDef, final ParentChildrenMap pcm) throws MetaDataException {
        final String tableName = tableDef.getTableName();
        if (fkDef.getMasterTableName().equals(tableName)) {
            pcm.setUseCaseType(1);
            return pcm;
        }
        final PrimaryKeyDefinition pkDef = tableDef.getPrimaryKey();
        final List pkColumns = pkDef.getColumnList();
        final List fkColumns = new ArrayList();
        final List fkColumnDefs = fkDef.getForeignKeyColumns();
        for (final ForeignKeyColumnDefinition fkColumnDef : fkColumnDefs) {
            fkColumns.add(fkColumnDef.getLocalColumnDefinition().getColumnName());
        }
        if (pkColumns.equals(fkColumns)) {
            if (fkDef.isBidirectional()) {
                XmlUseCaseResolver.LOGGER.log(Level.FINEST, "{0} is Single FK - Complete PK, BDFK.", tableName);
                if (this.hasMoreThanOneChild(fkDef.getMasterTableName()) || !this.swap_parent_child) {
                    pcm.setUseCaseType(2);
                }
                else {
                    pcm.setUseCaseType(3);
                }
                pcm.addParentElementName(fkDef.getMasterTableName());
                pcm.setMasterTableName(fkDef.getMasterTableName());
                pcm.setBdfk(true);
                return pcm;
            }
            pcm.setUseCaseType(2);
            pcm.addParentElementName(fkDef.getMasterTableName());
            pcm.setMasterTableName(fkDef.getMasterTableName());
            return pcm;
        }
        else {
            if (this.hasPartialPKAsFK(pkColumns, fkColumns)) {
                return this.groupElements(tableName, fkDef.getMasterTableName(), pcm);
            }
            pcm.setSingleFKNonPK(true);
            if (this.isUniqueFK(fkColumnDefs)) {
                pcm.setUseCaseType(2);
                pcm.addParentElementName(fkDef.getMasterTableName());
                pcm.setMasterTableName(fkDef.getMasterTableName());
                return pcm;
            }
            return this.groupElements(tableName, fkDef.getMasterTableName(), pcm);
        }
    }
    
    boolean hasPartialPKAsFK(final List pkColumns, final List fkColumns) {
        for (final String fkColumnName : fkColumns) {
            if (!pkColumns.contains(fkColumnName)) {
                return false;
            }
        }
        return true;
    }
    
    ParentChildrenMap groupElements(final String tableName, final String parentTableName, final ParentChildrenMap pcm) {
        String suffix = "List";
        if (tableName.endsWith(suffix)) {
            suffix = "s";
        }
        final String groupingTagName = tableName + suffix;
        pcm.setUseCaseType(2);
        pcm.addParentElementName(parentTableName);
        pcm.setMasterTableName(parentTableName);
        return pcm;
    }
    
    boolean isUniqueFK(final List fkColumnDefs) throws MetaDataException {
        if (fkColumnDefs.size() != 1) {
            return false;
        }
        final ForeignKeyColumnDefinition fkColumnDef = fkColumnDefs.get(0);
        final ColumnDefinition colDef = fkColumnDef.getLocalColumnDefinition();
        return colDef.isUnique();
    }
    
    ParentChildrenMap handleMultipleFKs(final TableDefinition tableDef, final List fkList, final ParentChildrenMap pcm) throws MetaDataException {
        final String tableName = tableDef.getTableName();
        final PrimaryKeyDefinition pkDef = tableDef.getPrimaryKey();
        final List pkColumns = pkDef.getColumnList();
        final Iterator fkDefIterator = fkList.iterator();
        boolean isMultipleFK_CompletePK = false;
        boolean isMultipleFK_PartialPK = false;
        boolean isBDFK = false;
        String masterTableName = null;
        while (fkDefIterator.hasNext()) {
            final ForeignKeyDefinition fkDef = fkDefIterator.next();
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "fkDef.getMasterTableName():{0}", fkDef.getMasterTableName());
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "start of loop masterTableName:{0}", masterTableName);
            if (fkDef.getMasterTableName().equals(tableName)) {
                continue;
            }
            final List fkColumns = new ArrayList();
            final List fkColumnDefs = fkDef.getForeignKeyColumns();
            for (final ForeignKeyColumnDefinition fkColumnDef : fkColumnDefs) {
                fkColumns.add(fkColumnDef.getLocalColumnDefinition().getColumnName());
            }
            if (!isMultipleFK_CompletePK) {
                isMultipleFK_CompletePK = pkColumns.equals(fkColumns);
                isBDFK = fkDef.isBidirectional();
                if (isMultipleFK_CompletePK) {
                    masterTableName = fkDef.getMasterTableName();
                }
            }
            else if (pkColumns.equals(fkColumns)) {
                isMultipleFK_CompletePK = false;
                masterTableName = null;
                break;
            }
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "PKColumns:{0}", pkColumns);
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "FKColumns:{0}", fkColumns);
            if (!isMultipleFK_PartialPK) {
                isMultipleFK_PartialPK = this.hasPartialPKAsFK(pkColumns, fkColumns);
                XmlUseCaseResolver.LOGGER.log(Level.FINEST, "isMultipleFK_PartialPK:{0}", isMultipleFK_PartialPK);
                if (!isMultipleFK_PartialPK) {
                    continue;
                }
                masterTableName = fkDef.getMasterTableName();
            }
            else {
                if (this.hasPartialPKAsFK(pkColumns, fkColumns)) {
                    break;
                }
                continue;
            }
        }
        if (isMultipleFK_CompletePK) {
            if (!this.tableNames.contains(masterTableName)) {
                pcm.setUseCaseType(1);
                return pcm;
            }
            if (isBDFK) {
                XmlUseCaseResolver.LOGGER.log(Level.FINEST, "{0} is Multiple FK - Complete PK, BDFK", tableName);
                if (this.hasMoreThanOneChild(masterTableName) || !this.swap_parent_child) {
                    pcm.setUseCaseType(2);
                }
                else {
                    pcm.setUseCaseType(3);
                }
                pcm.addParentElementName(masterTableName);
                pcm.setMasterTableName(masterTableName);
                pcm.setBdfk(true);
                return pcm;
            }
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "{0} is Multiple FK - Complete PK, non BDFK", tableName);
            pcm.setUseCaseType(2);
            pcm.addParentElementName(masterTableName);
            pcm.setMasterTableName(masterTableName);
            return pcm;
        }
        else {
            if (!isMultipleFK_PartialPK) {
                pcm.setUseCaseType(1);
                return pcm;
            }
            if (!this.tableNames.contains(masterTableName)) {
                pcm.setUseCaseType(1);
                return pcm;
            }
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "{0} is Multiple FK - Partial PK", tableName);
            XmlUseCaseResolver.LOGGER.log(Level.FINEST, "masterTableName:{0}", masterTableName);
            return this.groupElements(tableName, masterTableName, pcm);
        }
    }
    
    private boolean hasMoreThanOneChild(final String tableName) {
        try {
            final List fkDefs = MetaDataUtil.getReferringForeignKeyDefinitions(tableName);
            final Iterator fkIterator = fkDefs.iterator();
            int count = 0;
            while (fkIterator.hasNext()) {
                final ForeignKeyDefinition fkDef = fkIterator.next();
                if (!fkDef.isBidirectional()) {
                    continue;
                }
                final String childTableName = fkDef.getSlaveTableName();
                if (!this.tableNames.contains(childTableName)) {
                    continue;
                }
                ++count;
            }
            return count > 1;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    
    static {
        CLASS_NAME = XmlUseCaseResolver.class.getName();
        LOGGER = Logger.getLogger(XmlUseCaseResolver.CLASS_NAME);
    }
}
