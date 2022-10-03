package com.adventnet.persistence.migration;

import java.io.OutputStream;
import java.io.FileOutputStream;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.AlterTableQueryImpl;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.parser.ParserUtil;
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import com.zoho.conf.tree.ConfTree;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.List;
import java.util.logging.Logger;

class ExtendedDDDiff
{
    private static final Logger OUT;
    private List<AlterTableQuery> installQueries;
    private List<AlterTableQuery> revertQueries;
    private boolean ignoreMaxSizeReduction;
    private DDChangeListener.MigrationType operationType;
    ConfTree deletedMax;
    ConfTree addedMax;
    ConfTree modifiedMax;
    ConfTree modifiedOldMax;
    ConfTree deletedDefault;
    ConfTree addedDefault;
    ConfTree modifiedDefault;
    ConfTree modifiedOldDefault;
    ConfTree oldMaxConf;
    ConfTree newMaxConf;
    ConfTree oldDefaultConf;
    ConfTree newDefaultConf;
    ConfTree oldExtendedDDConfTree;
    ConfTree newExtendedDDConfTree;
    ConfTree deletedOldMaxConf;
    ConfTree deletedOldDefConf;
    String oldExtendedDDPath;
    String newExtendedDDPath;
    private List<String> modifiedMaxSizeOfColsInDD;
    private List<String> modifiedDefValOfColsInDD;
    private List<String> newlyAddedKeys;
    
    ExtendedDDDiff(final ConfTree oldExtendedDDConfTree, final ConfTree newExtendedDDConfTree, final DDChangeListener.MigrationType operationType, final boolean ignoreMaxsize, final String oldExtendedDDPath, final String newExtendedDDPath, final List<String> modifiedMaxSizeOfColsInDD, final List<String> modifiedDefValOfColsInDD) {
        this.installQueries = new ArrayList<AlterTableQuery>();
        this.revertQueries = new ArrayList<AlterTableQuery>();
        this.ignoreMaxSizeReduction = false;
        this.operationType = DDChangeListener.MigrationType.INSTALL;
        this.deletedMax = new ConfTree();
        this.addedMax = new ConfTree();
        this.modifiedMax = new ConfTree();
        this.modifiedOldMax = new ConfTree();
        this.deletedDefault = new ConfTree();
        this.addedDefault = new ConfTree();
        this.modifiedDefault = new ConfTree();
        this.modifiedOldDefault = new ConfTree();
        this.oldMaxConf = null;
        this.newMaxConf = null;
        this.oldDefaultConf = null;
        this.newDefaultConf = null;
        this.oldExtendedDDConfTree = null;
        this.newExtendedDDConfTree = null;
        this.deletedOldMaxConf = null;
        this.deletedOldDefConf = null;
        this.oldExtendedDDPath = null;
        this.newExtendedDDPath = null;
        this.modifiedMaxSizeOfColsInDD = null;
        this.modifiedDefValOfColsInDD = null;
        this.newlyAddedKeys = new ArrayList<String>();
        this.oldExtendedDDConfTree = oldExtendedDDConfTree;
        this.newExtendedDDConfTree = newExtendedDDConfTree;
        this.ignoreMaxSizeReduction = ignoreMaxsize;
        this.operationType = operationType;
        this.oldExtendedDDPath = oldExtendedDDPath;
        this.newExtendedDDPath = newExtendedDDPath;
        this.modifiedMaxSizeOfColsInDD = modifiedMaxSizeOfColsInDD;
        this.modifiedDefValOfColsInDD = modifiedDefValOfColsInDD;
    }
    
    private ConfTree getConfTree(final ConfTree existingConfTree, final String attribute) {
        final ConfTree requiredConfTree = new ConfTree();
        for (final String key : existingConfTree.keySet()) {
            if (key.substring(key.lastIndexOf(".") + 1).equalsIgnoreCase(attribute)) {
                requiredConfTree.put(key, existingConfTree.get(key));
            }
        }
        return requiredConfTree;
    }
    
    public void generateDiff() {
        if (this.oldExtendedDDConfTree != null) {
            this.oldMaxConf = this.getConfTree(this.oldExtendedDDConfTree, "maxsize");
            this.oldDefaultConf = this.getConfTree(this.oldExtendedDDConfTree, "defaultvalue");
        }
        if (this.newExtendedDDConfTree != null) {
            this.newMaxConf = this.getConfTree(this.newExtendedDDConfTree, "maxsize");
            this.newDefaultConf = this.getConfTree(this.newExtendedDDConfTree, "defaultvalue");
        }
        if (this.oldMaxConf != null) {
            if (this.newMaxConf != null) {
                for (final String set : this.oldMaxConf.keySet()) {
                    if (!this.newMaxConf.containsKey(set)) {
                        this.deletedMax.put(set, this.oldMaxConf.get(set));
                    }
                    else {
                        if (this.oldMaxConf.get(set).equals(this.newMaxConf.get(set))) {
                            continue;
                        }
                        this.modifiedMax.put(set, this.newMaxConf.get(set));
                        this.modifiedOldMax.put(set, this.oldMaxConf.get(set));
                    }
                }
                for (final String set : this.newMaxConf.keySet()) {
                    if (!this.oldMaxConf.containsKey(set)) {
                        this.addedMax.put(set, this.newMaxConf.get(set));
                    }
                }
            }
            else {
                for (final String set : this.oldMaxConf.keySet()) {
                    this.deletedMax.put(set, this.oldMaxConf.get(set));
                }
            }
        }
        else if (this.newMaxConf != null) {
            for (final String set : this.newMaxConf.keySet()) {
                this.addedMax.put(set, this.newMaxConf.get(set));
            }
        }
        if (this.oldDefaultConf != null) {
            if (this.newDefaultConf != null) {
                for (final String set : this.oldDefaultConf.keySet()) {
                    if (!this.newDefaultConf.containsKey(set)) {
                        this.deletedDefault.put(set, this.oldDefaultConf.get(set));
                    }
                    else {
                        if (this.oldDefaultConf.get(set).equals(this.newDefaultConf.get(set))) {
                            continue;
                        }
                        this.modifiedDefault.put(set, this.newDefaultConf.get(set));
                        this.modifiedOldDefault.put(set, this.oldDefaultConf.get(set));
                    }
                }
                for (final String set : this.newDefaultConf.keySet()) {
                    if (!this.oldDefaultConf.containsKey(set)) {
                        this.addedDefault.put(set, this.newDefaultConf.get(set));
                    }
                }
            }
            else {
                for (final String set : this.oldDefaultConf.keySet()) {
                    this.deletedDefault.put(set, this.oldDefaultConf.get(set));
                }
            }
        }
        else if (this.newDefaultConf != null) {
            for (final String set : this.newDefaultConf.keySet()) {
                this.addedDefault.put(set, this.newDefaultConf.get(set));
            }
        }
        if (this.operationType == DDChangeListener.MigrationType.UNINSTALL) {
            final String tempFilePath = new File(this.newExtendedDDPath) + File.separator + "ExtendedDD_old.atr";
            final ConfTree deletedOldConf = ParserUtil.parseExtendedDDConfTree(new File(tempFilePath));
            this.deletedOldMaxConf = ((deletedOldConf != null) ? this.getConfTree(deletedOldConf, "maxsize") : null);
            this.deletedOldDefConf = ((deletedOldConf != null) ? this.getConfTree(deletedOldConf, "defaultvalue") : null);
            ExtendedDDDiff.OUT.fine("deletedOldMaxConf :: " + this.deletedOldMaxConf);
        }
        ExtendedDDDiff.OUT.fine(" addedMax:: " + this.addedMax + " modifiedMax:: " + this.modifiedMax + " modifiedOldMax:: " + this.modifiedOldMax + " deletedMax:: " + this.deletedMax);
        ExtendedDDDiff.OUT.fine("addedDefault :: " + this.addedDefault + " modifiedDefault :: " + this.modifiedDefault + " modifiedOldDefault :: " + this.modifiedOldDefault + " deletedDefault :: " + this.deletedDefault);
    }
    
    void generateQueries() throws Exception {
        for (final String set : this.addedMax.keySet()) {
            final ColumnDefinition cd = this.getColDefinitionIfValuesDifferFromMetaData(set, this.addedMax.get(set), "maxSize");
            if (cd != null) {
                final ColumnDefinition newCD = (ColumnDefinition)cd.clone();
                newCD.setMaxLength(Integer.parseInt(this.addedMax.get(set)));
                if (this.operationType == DDChangeListener.MigrationType.INSTALL && (this.oldMaxConf == null || !this.oldMaxConf.containsKey(set))) {
                    this.newlyAddedKeys.add(set + "=" + MetaDataUtil.getTableDefinitionByName(cd.getTableName()).getColumnDefinitionByName(cd.getColumnName()).getMaxLength());
                }
                final String tableName = set.substring(0, set.indexOf("."));
                final String columnName = set.substring(set.indexOf(".") + 1, set.lastIndexOf("."));
                final String key = tableName + "." + columnName + ".defaultvalue";
                if (this.addedDefault != null && this.addedDefault.containsKey(key)) {
                    final ColumnDefinition modifiedCD = this.ModifyCDForAddDefault(key, newCD);
                    this.constructATQsForModifyColumn(cd.getTableName(), cd, modifiedCD, "both", "ignoreRevert");
                    this.addedDefault.remove(key);
                }
                else if (this.modifiedDefault != null && this.modifiedDefault.containsKey(key)) {
                    if (cd != null) {
                        cd.setDefaultValue(this.modifiedOldDefault.get(set));
                    }
                    final ColumnDefinition modifiedCD = this.ModifyCDForModifyDefault(key, newCD);
                    this.constructATQsForModifyColumn(cd.getTableName(), cd, modifiedCD, "both", "ignoreRevert");
                    this.modifiedDefault.remove(key);
                    this.modifiedOldDefault.remove(key);
                }
                else {
                    this.constructATQsForModifyColumn(cd.getTableName(), cd, newCD, "maxsize", "ignoreRevert");
                }
            }
        }
        for (final String set : this.modifiedMax.keySet()) {
            final ColumnDefinition cd = this.getColDefinitionIfValuesDifferFromMetaData(set, this.modifiedMax.get(set), "maxSize");
            if (cd != null) {
                cd.setMaxLength(Integer.parseInt(this.modifiedOldMax.get(set)));
                final ColumnDefinition newCD = (ColumnDefinition)cd.clone();
                newCD.setMaxLength(Integer.parseInt(this.modifiedMax.get(set)));
                final String tableName = set.substring(0, set.indexOf("."));
                final String columnName = set.substring(set.indexOf(".") + 1, set.lastIndexOf("."));
                final String key = tableName + "." + columnName + ".defaultvalue";
                if (this.addedDefault != null && this.addedDefault.containsKey(key)) {
                    final ColumnDefinition modifiedCD = this.ModifyCDForAddDefault(key, newCD);
                    this.constructATQsForModifyColumn(cd.getTableName(), cd, modifiedCD, "both", null);
                    this.addedDefault.remove(key);
                }
                else if (this.modifiedDefault != null && this.modifiedDefault.containsKey(key)) {
                    if (cd != null) {
                        cd.setDefaultValue(this.modifiedOldDefault.get(set));
                    }
                    final ColumnDefinition modifiedCD = this.ModifyCDForModifyDefault(key, newCD);
                    this.constructATQsForModifyColumn(cd.getTableName(), cd, modifiedCD, "both", null);
                    this.modifiedDefault.remove(key);
                    this.modifiedOldDefault.remove(key);
                }
                else {
                    this.constructATQsForModifyColumn(cd.getTableName(), cd, newCD, "maxsize", "ignoreRevert");
                }
            }
        }
        for (final String set : this.deletedMax.keySet()) {
            final String tableName2 = set.substring(0, set.indexOf("."));
            final String columnName2 = set.substring(set.indexOf(".") + 1, set.lastIndexOf("."));
            try {
                if (this.operationType != DDChangeListener.MigrationType.INSTALL) {
                    continue;
                }
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName2);
                if (td == null) {
                    continue;
                }
                final ColumnDefinition coldef = td.getColumnDefinitionByName(columnName2);
                if (coldef != null && (this.modifiedMaxSizeOfColsInDD == null || !this.modifiedMaxSizeOfColsInDD.contains(set))) {
                    throw new IllegalArgumentException("Entries in extended_dd.conf cannot be deleted with out modifying [" + coldef.getTableName() + "." + coldef.getColumnName() + "] column's respective data dictionary");
                }
                continue;
            }
            catch (final MetaDataException e) {
                ExtendedDDDiff.OUT.severe("exception while getting column definition of [" + tableName2 + "." + columnName2 + "] column");
                throw e;
            }
        }
        for (final String set : this.addedDefault.keySet()) {
            final ColumnDefinition cd = this.getColDefinitionIfValuesDifferFromMetaData(set, this.addedDefault.get(set), "defaultValue");
            if (cd != null) {
                final ColumnDefinition newCD = this.ModifyCDForAddDefault(set, cd);
                this.constructATQsForModifyColumn(cd.getTableName(), cd, newCD, "defaultValue", "ignoreRevert");
            }
        }
        for (final String set : this.modifiedDefault.keySet()) {
            final ColumnDefinition cd = this.getColDefinitionIfValuesDifferFromMetaData(set, this.modifiedDefault.get(set), "defaultValue");
            if (cd != null) {
                cd.setDefaultValue(this.modifiedOldDefault.get(set));
                final ColumnDefinition newCD = this.ModifyCDForModifyDefault(set, cd);
                this.constructATQsForModifyColumn(cd.getTableName(), cd, newCD, "defaultValue", null);
            }
        }
        for (final String set : this.deletedDefault.keySet()) {
            final String tableName2 = set.substring(0, set.indexOf("."));
            final String columnName2 = set.substring(set.indexOf(".") + 1, set.lastIndexOf("."));
            try {
                if (this.operationType != DDChangeListener.MigrationType.INSTALL) {
                    continue;
                }
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName2);
                if (td == null) {
                    continue;
                }
                final ColumnDefinition coldef = td.getColumnDefinitionByName(columnName2);
                if (coldef != null && (this.modifiedDefValOfColsInDD == null || !this.modifiedDefValOfColsInDD.contains(set))) {
                    throw new IllegalArgumentException("Entries in extended_dd.conf cannot be deleted with out modifying [" + coldef.getTableName() + "." + coldef.getColumnName() + "] column's respective data dictionary");
                }
                continue;
            }
            catch (final MetaDataException e) {
                ExtendedDDDiff.OUT.severe("exception while getting column definition of [" + tableName2 + "." + columnName2 + "] column");
                throw e;
            }
        }
        if (this.operationType == DDChangeListener.MigrationType.UNINSTALL && this.deletedOldMaxConf != null) {
            for (final String key2 : this.deletedOldMaxConf.keySet()) {
                final String tableName2 = key2.substring(0, key2.indexOf("."));
                final String columnName2 = key2.substring(key2.indexOf(".") + 1, key2.lastIndexOf("."));
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName2);
                if (td != null) {
                    final ColumnDefinition cd2 = td.getColumnDefinitionByName(columnName2);
                    if (cd2 == null) {
                        continue;
                    }
                    final ColumnDefinition newCD2 = (ColumnDefinition)cd2.clone();
                    newCD2.setMaxLength(Integer.parseInt(this.deletedOldMaxConf.get(key2)));
                    this.constructATQsForModifyColumn(cd2.getTableName(), cd2, newCD2, "maxsize", null);
                }
            }
            for (final String key2 : this.deletedOldDefConf.keySet()) {
                final String tableName2 = key2.substring(0, key2.indexOf("."));
                final String columnName2 = key2.substring(key2.indexOf(".") + 1, key2.lastIndexOf("."));
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName2);
                if (td != null) {
                    final ColumnDefinition cd2 = td.getColumnDefinitionByName(columnName2);
                    if (cd2 == null) {
                        continue;
                    }
                    final ColumnDefinition newCD2 = (ColumnDefinition)cd2.clone();
                    if (this.deletedOldDefConf.get(key2).equals("{null}")) {
                        newCD2.setDefaultValue(null);
                    }
                    else {
                        newCD2.setDefaultValue(this.deletedOldDefConf.get(key2));
                    }
                    this.constructATQsForModifyColumn(cd2.getTableName(), cd2, newCD2, "defaultValue", null);
                }
            }
            final File file = new File(this.oldExtendedDDPath + File.separator + "ExtendedDD_old.atr");
            if (file.exists() && !file.delete()) {
                ExtendedDDDiff.OUT.warning("Exception occured while deleting the file :: " + file.getPath());
            }
        }
        if (this.operationType == DDChangeListener.MigrationType.INSTALL && !this.newlyAddedKeys.isEmpty()) {
            this.writeToFile();
        }
    }
    
    private ColumnDefinition ModifyCDForAddDefault(final String set, final ColumnDefinition cd) {
        ColumnDefinition newCD = null;
        if (cd != null) {
            try {
                newCD = (ColumnDefinition)cd.clone();
                newCD.setDefaultValue(this.addedDefault.get(set));
                if (this.operationType == DDChangeListener.MigrationType.INSTALL && (this.oldDefaultConf == null || !this.oldDefaultConf.containsKey(set))) {
                    Object defaultValue = MetaDataUtil.getTableDefinitionByName(cd.getTableName()).getColumnDefinitionByName(cd.getColumnName()).getDefaultValue();
                    if (defaultValue == null) {
                        defaultValue = "{null}";
                    }
                    this.newlyAddedKeys.add(set + "=" + defaultValue);
                }
            }
            catch (final MetaDataException mde) {
                ExtendedDDDiff.OUT.log(Level.SEVERE, "Exception occured while constructing alterQuery for modified Default value :: " + mde.getMessage());
            }
            catch (final CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return newCD;
    }
    
    private ColumnDefinition ModifyCDForModifyDefault(final String set, final ColumnDefinition cd) {
        ColumnDefinition newCD = null;
        if (cd != null) {
            try {
                newCD = (ColumnDefinition)cd.clone();
                newCD.setDefaultValue(this.modifiedDefault.get(set));
                this.constructATQsForModifyColumn(cd.getTableName(), cd, newCD, "defaultValue", null);
            }
            catch (final MetaDataException mde) {
                ExtendedDDDiff.OUT.log(Level.SEVERE, "Exception occured while constructing alterQuery for modified Default value :: " + mde.getMessage());
            }
            catch (final CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return newCD;
    }
    
    List[] getQueries() {
        return new List[] { this.installQueries, this.revertQueries };
    }
    
    private ColumnDefinition getColDefinitionIfValuesDifferFromMetaData(final String set, final String value, final String type) {
        final String tableName = set.substring(0, set.indexOf("."));
        final String columnName = set.substring(set.indexOf(".") + 1, set.lastIndexOf("."));
        try {
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            if (td != null) {
                final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
                if (cd != null && type.equalsIgnoreCase("maxSize")) {
                    if (cd.getMaxLength() != Integer.parseInt(value)) {
                        return cd;
                    }
                    ExtendedDDDiff.OUT.info("max-length of column [" + tableName + "." + columnName + "] is already modified , hence skipping alter Query ");
                }
                else if (cd != null && type.equalsIgnoreCase("defaultValue")) {
                    if (cd.getDefaultValue() != value) {
                        return cd;
                    }
                    ExtendedDDDiff.OUT.info("default value of column [" + tableName + "." + columnName + "] is already modified , hence skipping alter Query ");
                }
                else if (cd == null) {
                    ExtendedDDDiff.OUT.info("column definition of [" + tableName + "." + columnName + "] is null, this may occur when drop column is executed for this column while applying data-dictionary changes or if column does not exist");
                }
            }
            else {
                ExtendedDDDiff.OUT.info("table definition of [" + tableName + "] is null, this may occur when drop table is executed for this table while applying data-dictionary changes or if table does not exist");
            }
            return null;
        }
        catch (final MetaDataException e) {
            ExtendedDDDiff.OUT.severe("Exception occured while getting meta info :: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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
    
    private void constructATQsForModifyColumn(final String tableName, final ColumnDefinition oldCD, final ColumnDefinition newCD, final String type, final String ignore) {
        final List<String> attributes = new ArrayList<String>();
        AlterTableQuery aq = null;
        AlterTableQuery aq2 = null;
        if (type.equalsIgnoreCase("defaultValue")) {
            attributes.add("default-value");
        }
        else if (type.equalsIgnoreCase("maxsize")) {
            attributes.add("max-size");
        }
        else if (type.equalsIgnoreCase("both")) {
            attributes.add("default-value");
            attributes.add("max-size");
        }
        if (ignore != null && ignore.equals("ignoreInstall")) {
            this.installQueries.add(aq);
        }
        else {
            ExtendedDDDiff.OUT.log(Level.INFO, "processing MODIFY_COLUMN query for column [ {0}.{1} ] modified in {2}", new Object[] { tableName, newCD.getColumnName(), attributes });
            aq = this.getATQforModifyColumn(tableName, oldCD, newCD, attributes);
            this.installQueries.add(aq);
        }
        if (ignore != null && ignore.equals("ignoreRevert")) {
            this.revertQueries.add(aq2);
        }
        else {
            aq2 = this.getATQforModifyColumn(tableName, newCD, oldCD, attributes);
            this.revertQueries.add(aq2);
        }
        if ((oldCD.getDataType().equals("CHAR") || oldCD.getDataType().equals("NCHAR") || oldCD.getDataType().equals("DECIMAL")) && this.ignoreMaxSizeReduction) {
            if (this.operationType == DDChangeListener.MigrationType.INSTALL && aq2 != null) {
                final AlterOperation ao = aq2.getAlterOperations().get(0);
                ao.ignoreMaxSizeReduction();
            }
            else if (aq != null) {
                final AlterOperation ao = aq.getAlterOperations().get(0);
                ao.ignoreMaxSizeReduction();
            }
        }
    }
    
    private void writeToFile() throws Exception {
        OutputStream out = null;
        try {
            final File file = new File(new File(this.oldExtendedDDPath) + File.separator + "ExtendedDD_old.atr");
            if (!file.exists()) {
                final File parent = file.getParentFile();
                parent.mkdirs();
            }
            for (final String b : this.newlyAddedKeys) {
                out = new FileOutputStream(file, true);
                out.write((b + "\n").getBytes());
            }
        }
        catch (final Exception e) {
            ExtendedDDDiff.OUT.severe("Exception while adding newly added enries to extendedDD_old.atr in PPM");
            e.printStackTrace();
            throw e;
        }
        finally {
            out.close();
        }
    }
    
    static {
        OUT = Logger.getLogger(ExtendedDDDiff.class.getName());
    }
}
