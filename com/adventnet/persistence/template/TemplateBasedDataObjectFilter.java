package com.adventnet.persistence.template;

import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.DataObjectFilter;

public class TemplateBasedDataObjectFilter extends DataObjectFilter implements MessageFilter
{
    public TemplateBasedDataObjectFilter() {
    }
    
    public TemplateBasedDataObjectFilter(final String... tableNames) {
        if (tableNames.length > 0) {
            this.setTableList((List)Arrays.asList(tableNames));
        }
    }
    
    protected boolean isTableNameMatches(final List<String> tableNames, final List<String> incomingTableNames) {
        if (tableNames == null) {
            return true;
        }
        for (final String incomingTableName : incomingTableNames) {
            if (tableNames.contains(this.getTableName(incomingTableName))) {
                return true;
            }
        }
        return false;
    }
    
    private String getTableName(final String tableName) {
        try {
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            return (td != null) ? td.getTableName() : tableName;
        }
        catch (final MetaDataException ignored) {
            ignored.printStackTrace();
            return tableName;
        }
    }
}
