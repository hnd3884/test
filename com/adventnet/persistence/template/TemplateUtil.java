package com.adventnet.persistence.template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.template.AlterQueryInstanceWrapper;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.List;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;

public class TemplateUtil
{
    public static TableDefinition createTableDefnForTemplateInstance(final TableDefinition template_td, final String instanceId) throws CloneNotSupportedException {
        if (!template_td.isTemplate()) {
            throw new UnsupportedOperationException("Not supported. The table [" + template_td.getTableName() + "] is NOT a template-table");
        }
        final TableDefinition toRet = (TableDefinition)template_td.clone();
        toRet.setTemplate(false);
        final String newTableName = getTableName(template_td.getTableName(), instanceId);
        toRet.setTableName(newTableName);
        for (final Object colDefn : toRet.getColumnList()) {
            ((ColumnDefinition)colDefn).setTableName(newTableName);
        }
        final PrimaryKeyDefinition pkDefn = toRet.getPrimaryKey();
        pkDefn.setTableName(newTableName);
        pkDefn.setName(getPKDefnName(pkDefn.getName(), instanceId));
        TableDefinition mastertd = null;
        final List<ForeignKeyDefinition> fks = toRet.getForeignKeyList();
        if (fks != null && fks.size() > 0) {
            for (final ForeignKeyDefinition fk : fks) {
                fk.setSlaveTableName(newTableName);
                String masterTableName = fk.getMasterTableName();
                mastertd = _getTableDefinitionByName(masterTableName);
                if (mastertd != null && mastertd.isTemplate()) {
                    masterTableName = getTableName(fk.getMasterTableName(), instanceId);
                }
                fk.setMasterTableName(masterTableName);
                fk.setName(getFKDefnName(fk.getName(), instanceId));
                for (final Object fkColDefObj : fk.getForeignKeyColumns()) {
                    final ForeignKeyColumnDefinition fkColDef = (ForeignKeyColumnDefinition)fkColDefObj;
                    fkColDef.getLocalColumnDefinition().setTableName(newTableName);
                    fkColDef.getReferencedColumnDefinition().setTableName(newTableName);
                }
            }
        }
        if (toRet.getUniqueKeys() != null) {
            for (final Object obj : toRet.getUniqueKeys()) {
                final UniqueKeyDefinition ukDefn = (UniqueKeyDefinition)obj;
                ukDefn.setName(getUKDefnName(ukDefn.getName(), instanceId));
            }
        }
        if (toRet.getIndexes() != null) {
            for (final Object obj : toRet.getIndexes()) {
                final IndexDefinition indexDefn = (IndexDefinition)obj;
                indexDefn.setName(getIndexDefnName(indexDefn.getName(), instanceId));
            }
        }
        return toRet;
    }
    
    public static AlterTableQuery createAlterQueryForTemplateInst(final AlterTableQuery template_aq, final String instanceId) throws MetaDataException {
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName(template_aq.getTableName());
        if (td != null && !td.isTemplate()) {
            throw new UnsupportedOperationException("Not supported. The table [" + template_aq.getTableName() + "] is NOT a template-table");
        }
        return new AlterQueryInstanceWrapper(template_aq, instanceId);
    }
    
    public static boolean isTemplate(final String tableName) {
        final TableDefinition td = _getTableDefinitionByName(tableName);
        return td != null && td.isTemplate();
    }
    
    public static boolean isInstanceOfTemplate(final String tableName, final String templateTable) {
        final TableDefinition td = _getTableDefinitionByName(tableName);
        return td != null && td.isTemplate() && templateTable.equals(td.getTableName()) && !tableName.equals(templateTable);
    }
    
    private static TableDefinition _getTableDefinitionByName(final String tableName) {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }
    
    public static String getTableName(final String templateTableName, final String instanceId) {
        final TableDefinition td = _getTableDefinitionByName(templateTableName);
        if (td != null && td.getTemplateInstancePatternName() != null) {
            final String pattern = td.getTemplateInstancePatternName();
            return pattern.replace("${instancename}", instanceId);
        }
        return templateTableName + "_" + instanceId;
    }
    
    public static String getPKDefnName(final String templatePkDefnName, final String instanceId) {
        return templatePkDefnName + "_" + instanceId;
    }
    
    public static String getIndexDefnName(final String templateIndexDefnName, final String instanceId) {
        return templateIndexDefnName + "_" + instanceId;
    }
    
    public static String getUKDefnName(final String templateUniqueKeyDefnName, final String instanceId) {
        return templateUniqueKeyDefnName + "_" + instanceId;
    }
    
    public static String getFKDefnName(final String templateForeignKeyDefnName, final String instanceId) {
        return templateForeignKeyDefnName + "_" + instanceId;
    }
    
    public static String getTemplateInstaceId(final String templateName, final String templateInstaceName) throws MetaDataException {
        final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(templateName);
        if (tabDef.getTemplateInstancePatternName() != null) {
            String pattern = "(" + tabDef.getTemplateInstancePatternName() + ")";
            pattern = pattern.replace("${instancename}", ")(.*)(");
            final Matcher matches = Pattern.compile(pattern, 2).matcher(templateInstaceName);
            if (matches.matches()) {
                return matches.group(2);
            }
        }
        return templateInstaceName.substring(templateName.length() + 1, templateInstaceName.length());
    }
}
