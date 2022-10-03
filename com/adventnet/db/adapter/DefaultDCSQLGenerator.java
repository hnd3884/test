package com.adventnet.db.adapter;

import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.Column;
import java.util.Map;
import java.util.logging.Logger;

public class DefaultDCSQLGenerator implements DCSQLGenerator
{
    private static final Logger LOGGER;
    protected SQLGenerator sqlGenerator;
    
    public DefaultDCSQLGenerator() {
        this.sqlGenerator = null;
    }
    
    @Override
    public void getSQLForInsert(final String tableName, final Map<Column, Object> values, final StringBuilder valueBuffer, final StringBuilder buffer) throws QueryConstructionException {
        final Set dyKeys = values.keySet();
        final Iterator itr = dyKeys.iterator();
        while (itr.hasNext()) {
            final Column column = itr.next();
            final Object value = values.get(column);
            final String columnName = this.getDCSpecificColumnName(column.getColumnName());
            buffer.append(columnName);
            if (value == QueryConstants.PREPARED_STMT_CONST) {
                valueBuffer.append(this.sqlGenerator.getDBSpecificEncryptionString(column, "?"));
            }
            else {
                final int type = column.getType();
                if (this.isNumeric(type)) {
                    if (!this.isNumeric(value)) {
                        throw new QueryConstructionException("Column Type and value doesn't match");
                    }
                    valueBuffer.append(value.toString());
                }
                else {
                    valueBuffer.append("'");
                    valueBuffer.append(value.toString());
                    valueBuffer.append("'");
                }
            }
            if (itr.hasNext()) {
                buffer.append(",");
                valueBuffer.append(",");
            }
        }
    }
    
    @Override
    public String getDCSpecificColumnName(final String columnName) {
        return this.sqlGenerator.getDBSpecificColumnName(columnName);
    }
    
    @Override
    public String getDCSpecificColumnName(final String tableName, final String columnName) {
        return this.getDCSpecificColumnName(columnName);
    }
    
    @Override
    public String getSQLForCast(final String columnString, final Column column) {
        return columnString;
    }
    
    @Override
    public String getDCDataType(final String columnName) {
        throw new UnsupportedOperationException("Not supported for defaultdc");
    }
    
    @Override
    public int getDCSQLType(final String columnName) {
        throw new UnsupportedOperationException("Not supported for defaultdc");
    }
    
    @Override
    public Map getModifiedValueForUpdate(final String tableName, final Map<Column, Object> dyValues) throws MetaDataException {
        return dyValues;
    }
    
    @Override
    public boolean isUniqueKeySupported() {
        return true;
    }
    
    @Override
    public void initSQLGenerator(final SQLGenerator sqlGenerator) {
        if (this.sqlGenerator == null) {
            this.sqlGenerator = sqlGenerator;
        }
        else {
            DefaultDCSQLGenerator.LOGGER.log(Level.WARNING, "SQLGenerator has already been initialized. Existing value :: {0}", this.sqlGenerator);
        }
    }
    
    protected boolean isNumeric(final Object value) {
        return value instanceof Number;
    }
    
    protected boolean isNumeric(final int type) {
        return type != 12 && (type == 4 || type == -5 || type == 6 || type == 8 || type == 2 || type == 3 || type == -6);
    }
    
    @Override
    public String getSQLForArchiveTableCheckConstraint(final String invisibleTable, final List<ColumnDefinition> colDefs) {
        throw new UnsupportedOperationException("DefaultDCSQLGenerator: Check constraint sql for archive table not supported.");
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultDCSQLGenerator.class.getName());
    }
}
