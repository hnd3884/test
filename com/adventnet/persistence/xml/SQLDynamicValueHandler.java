package com.adventnet.persistence.xml;

import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class SQLDynamicValueHandler implements DynamicValueHandler
{
    private static final Logger LOGGER;
    private static ThreadLocal tlObj;
    
    @Override
    public Object getColumnValue(final String tableName, final String columnName, final Properties configuredDHVP, final String xmlAttribute) throws DynamicValueHandlingException {
        if (configuredDHVP == null) {
            return null;
        }
        SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH Properties is {0}", new Object[] { configuredDHVP });
        String masterTableName = configuredDHVP.getProperty("referred-table");
        String masterColumnName = configuredDHVP.getProperty("referred-column");
        SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH  referred-table is  {0}", new Object[] { masterTableName });
        SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH  xmlAttribute is {0}", new Object[] { xmlAttribute });
        SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH referred-column is {0} ", new Object[] { masterColumnName });
        if (masterTableName == null && masterColumnName == null) {
            SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH: Both MasterTableName and MasterColumnName are Null, hence computing them from data-dictionary.xml");
            final String[] values = this.updateMasterTableAndColumnValue(tableName, columnName);
            masterTableName = values[0];
            masterColumnName = values[1];
            if (masterTableName == null || masterColumnName == null) {
                throw new DynamicValueHandlingException("Could not resolve the master table/column for the given configuration of " + tableName + ":" + columnName + ":" + xmlAttribute);
            }
        }
        final String comparator = configuredDHVP.getProperty("criteria-comparator");
        if (comparator == "" || comparator == null) {
            throw new DynamicValueHandlingException("Comparator not configured, unable to generate Dynamic values for " + columnName + " in table " + tableName);
        }
        final String criteriaColumn = configuredDHVP.getProperty("criteria-column");
        if (criteriaColumn == null || criteriaColumn == "") {
            throw new DynamicValueHandlingException("Criteria Column not configured, unable to generate Dynamic values for " + columnName + " in table " + tableName);
        }
        String criteriaValue = configuredDHVP.getProperty("criteria-value");
        if (criteriaValue == null || criteriaValue == "") {
            SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH: The criteria-value is not configured, hence taking the xmlAttribute as criteria-value");
            criteriaValue = xmlAttribute;
        }
        SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH: criteria-column is {0} ", new Object[] { criteriaColumn });
        SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH: criteria-value is {0} ", new Object[] { criteriaValue });
        final Table table = new Table(masterTableName, masterTableName);
        final SelectQueryImpl sql = new SelectQueryImpl(table);
        final Column column = new Column(masterTableName, "*");
        SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH: Calling the getValue() method ");
        Object value = this.getValue(masterTableName, criteriaColumn, criteriaValue);
        if (value != null && value instanceof String) {
            value = ((String)value).trim();
        }
        SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH: VALUE IS obtained is {0}", new Object[] { value });
        final int int_comapator = this.getIntValueForComparator(comparator);
        final Criteria criteria = new Criteria(new Column(masterTableName, criteriaColumn), value, int_comapator);
        sql.addSelectColumn(column);
        sql.setCriteria(criteria);
        try {
            SQLDynamicValueHandler.LOGGER.log(Level.FINE, "SQLDVH: SQL formed is {0}", sql);
            Object result = null;
            final DataObject dob = this.get();
            if (dob != null) {
                result = dob.getValue(masterTableName, masterColumnName, criteria);
                if (result != null) {
                    return result;
                }
            }
            final DataObject data = DataAccess.get(sql);
            SQLDynamicValueHandler.LOGGER.log(Level.FINE, "DynamicValueGeneration: DataObject obtained from the Query is {0}", data);
            if (data.containsTable(masterTableName)) {
                result = data.getFirstValue(masterTableName, masterColumnName);
            }
            SQLDynamicValueHandler.LOGGER.log(Level.FINE, "Result obtained from the query is {0}", result);
            return result;
        }
        catch (final DataAccessException dataExp) {
            final DynamicValueHandlingException dyExp = new DynamicValueHandlingException(dataExp.getMessage());
            dyExp.initCause(dataExp);
            throw dyExp;
        }
        catch (final Exception sqle) {
            final DynamicValueHandlingException dyExp = new DynamicValueHandlingException(sqle.getMessage());
            dyExp.initCause(sqle);
            throw dyExp;
        }
    }
    
    private Object getValue(final String tableName, final String columnName, final String columnValue) throws DynamicValueHandlingException {
        TableDefinition tableDef = null;
        try {
            tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
            SQLDynamicValueHandler.LOGGER.log(Level.FINE, "DEBUG TableDefinition is ******{0}**********{1}", new Object[] { tableDef, tableName });
            SQLDynamicValueHandler.LOGGER.log(Level.FINE, "DEBUG COLUMNDEF IS ******{0}**********", columnName);
            final ColumnDefinition colDef = tableDef.getColumnDefinitionByName(columnName);
            SQLDynamicValueHandler.LOGGER.log(Level.FINE, "DEBUG getColumnValue() ******{0}**********{1}", new Object[] { colDef, columnValue });
            if (colDef == null) {
                throw new DynamicValueHandlingException("No ColumnDefinition found for Column " + columnName + " in table " + tableName);
            }
            final String dataType = colDef.getDataType();
            return XmlDoUtil.convert(columnValue, dataType);
        }
        catch (final MetaDataException metaExp) {
            final DynamicValueHandlingException dyExp = new DynamicValueHandlingException(metaExp.getMessage());
            dyExp.initCause(metaExp);
            throw dyExp;
        }
    }
    
    private String[] updateMasterTableAndColumnValue(final String tableName, final String columnName) throws DynamicValueHandlingException {
        final String[] values = new String[2];
        TableDefinition tabDef = null;
        try {
            tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException exp) {
            final DynamicValueHandlingException dyExp = new DynamicValueHandlingException(exp.getMessage());
            dyExp.initCause(exp);
            throw dyExp;
        }
        final List fkDefs = tabDef.getForeignKeyList();
        if (fkDefs == null) {
            return values;
        }
        boolean breakLoop = false;
        for (int i = 0; i < fkDefs.size() && !breakLoop; ++i) {
            final ForeignKeyDefinition fkDef = fkDefs.get(i);
            final List fkColumns = fkDef.getForeignKeyColumns();
            if (fkColumns != null) {
                for (int j = 0; j < fkColumns.size(); ++j) {
                    final ForeignKeyColumnDefinition fkColDef = fkColumns.get(j);
                    final ColumnDefinition local = fkColDef.getLocalColumnDefinition();
                    final String localColName = local.getColumnName();
                    if (localColName.equals(columnName)) {
                        final ColumnDefinition masterCol = fkColDef.getReferencedColumnDefinition();
                        values[0] = masterCol.getTableName();
                        values[1] = masterCol.getColumnName();
                        breakLoop = true;
                        break;
                    }
                }
            }
        }
        return values;
    }
    
    private int getIntValueForComparator(String comparator) {
        comparator = comparator.toUpperCase();
        if (comparator.equals("EQUAL")) {
            return 0;
        }
        if (comparator.equals("NOT_EQUAL")) {
            return 1;
        }
        if (comparator.equals("LIKE")) {
            return 2;
        }
        if (comparator.equals("NOT_LIKE")) {
            return 3;
        }
        if (comparator.equals("GREATER_EQUAL")) {
            return 4;
        }
        if (comparator.equals("GREATER_THAN")) {
            return 5;
        }
        if (comparator.equals("LESS_EQUAL")) {
            return 6;
        }
        if (comparator.equals("LESS_THAN")) {
            return 7;
        }
        if (comparator.equals("IN")) {
            return 8;
        }
        if (comparator.equals("NOT_IN")) {
            return 9;
        }
        return 0;
    }
    
    @Override
    public String getAttributeValue(final String tableName, final String columnName, final Properties configuredDHVP, final Object columnValue) throws DynamicValueHandlingException {
        final String result = configuredDHVP.getProperty("returnValue");
        if (result == null) {
            final String tbName = configuredDHVP.getProperty("referred-table");
            final String colName = configuredDHVP.getProperty("referred-column");
            final String comparator = configuredDHVP.getProperty("criteria-comparator");
            final String returnColumnName = configuredDHVP.getProperty("criteria-column");
            final DataObject dob = this.get();
            try {
                final Criteria criteria = new Criteria(new Column(tbName, colName), columnValue, this.getIntValueForComparator(comparator));
                if (dob != null) {
                    final Row r = dob.getRow(tbName, criteria);
                    if (r != null) {
                        final Object returnColumnValue = r.get(returnColumnName);
                        return (returnColumnValue != null) ? returnColumnValue.toString() : null;
                    }
                }
                final SelectQuery sq = new SelectQueryImpl(new Table(tbName));
                sq.addSelectColumn(new Column(null, "*"));
                sq.setCriteria(criteria);
                final DataObject data = DataAccess.get(sq);
                final Row row = data.getRow(tbName);
                if (row != null) {
                    final Object returnColumnValue2 = row.get(returnColumnName);
                    return (returnColumnValue2 != null) ? returnColumnValue2.toString() : null;
                }
                return (columnValue != null) ? columnValue.toString() : null;
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw new DynamicValueHandlingException("Exception occurred while getAttributeValue for " + tbName + " " + colName + " " + comparator + " " + returnColumnName, e);
            }
        }
        return result;
    }
    
    @Override
    public void set(final Object dobj) {
        SQLDynamicValueHandler.tlObj.set(dobj);
    }
    
    @Override
    public DataObject get() {
        return SQLDynamicValueHandler.tlObj.get();
    }
    
    static {
        LOGGER = Logger.getLogger(SQLDynamicValueHandler.class.getName());
        SQLDynamicValueHandler.tlObj = new ThreadLocal();
    }
}
