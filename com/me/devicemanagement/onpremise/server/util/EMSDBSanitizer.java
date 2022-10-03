package com.me.devicemanagement.onpremise.server.util;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.adapter.SQLGenerator;
import java.sql.SQLException;
import com.adventnet.persistence.DataAccessException;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.api.RelationalAPI;
import org.json.JSONObject;
import java.util.logging.Logger;

public class EMSDBSanitizer
{
    private static final String SUCCESS = "200";
    private static final String HANDLED_EXCEPTION = "400";
    private static final String UNKNOWN_EXCEPTION = "500";
    protected static Logger logger;
    
    public static JSONObject createUKForColumns(final String tableName, final String columnName) throws Exception {
        final JSONObject successObject = new JSONObject();
        try {
            final SQLGenerator generator = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator();
            final UniqueKeyDefinition uniqueKeyDefinition = new UniqueKeyDefinition();
            uniqueKeyDefinition.addColumn(columnName);
            final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
            final List<UniqueKeyDefinition> uniqueKeys = tableDefinition.getUniqueKeys();
            int count;
            String uniqueKeyName;
            for (count = ((uniqueKeys == null) ? 0 : uniqueKeys.size()), uniqueKeyName = tableName + "_UK"; tableDefinition.getUniqueKeyDefinitionByName(uniqueKeyName + count) != null; ++count) {}
            uniqueKeyName += count;
            uniqueKeyDefinition.setName(uniqueKeyName);
            final AlterTableQuery alterTableQuery = (AlterTableQuery)new AlterTableQueryImpl(tableName);
            alterTableQuery.addUniqueKey(uniqueKeyDefinition);
            EMSDBSanitizer.logger.info("Going to execute :: " + generator.getSQLForAlterTable(alterTableQuery));
            DataAccess.alterTable(alterTableQuery);
            successObject.put("status", (Object)"200");
            successObject.put("uniqueKeyName", (Object)uniqueKeyName);
            successObject.put("exception", (Object)"");
            successObject.put("errorDetails", (Object)"");
            successObject.put("columnName", (Object)"");
            successObject.put("duplicateValue", (Object)"");
        }
        catch (final DataAccessException | SQLException ex) {
            final JSONObject errorObject = new JSONObject();
            EMSDBSanitizer.logger.severe("Exception while adding unique-key for column :: \"" + tableName + "\".\"" + columnName + "\"");
            EMSDBSanitizer.logger.log(Level.SEVERE, "Message :: ", ex);
            final String[] keyValueArray = StringUtils.substringsBetween(ex.getMessage(), "(", ")");
            if (keyValueArray == null) {
                errorObject.put("status", (Object)"500");
                errorObject.put("exception", (Object)ex.toString());
                errorObject.put("errorDetails", (Object)ex.getMessage());
                errorObject.put("uniqueKeyName", (Object)"");
                errorObject.put("columnName", (Object)"");
                errorObject.put("duplicateValue", (Object)"");
                return errorObject;
            }
            final List<String> keyValueList = Arrays.asList(keyValueArray);
            if (keyValueList.size() != 2) {
                errorObject.put("status", (Object)"500");
                errorObject.put("exception", (Object)ex.toString());
                errorObject.put("errorDetails", (Object)ex.getMessage());
                errorObject.put("uniqueKeyName", (Object)"");
                errorObject.put("columnName", (Object)"");
                errorObject.put("duplicateValue", (Object)"");
                return errorObject;
            }
            errorObject.put("status", (Object)"400");
            for (final String pair : keyValueList) {
                if (pair.equalsIgnoreCase(columnName)) {
                    errorObject.put("columnName", (Object)columnName);
                }
                else {
                    errorObject.put("duplicateValue", (Object)pair);
                }
            }
            errorObject.put("exception", (Object)ex.toString());
            errorObject.put("errorDetails", (Object)ex.getMessage());
            errorObject.put("uniqueKeyName", (Object)"");
            return errorObject;
        }
        catch (final Exception ex) {
            final JSONObject errorObject = new JSONObject();
            EMSDBSanitizer.logger.log(Level.SEVERE, ex.toString() + " occured ", ex);
            errorObject.put("status", (Object)"500");
            errorObject.put("exception", (Object)ex.toString());
            errorObject.put("errorDetails", (Object)ex.getMessage());
            errorObject.put("uniqueKeyName", (Object)"");
            errorObject.put("columnName", (Object)"");
            errorObject.put("duplicateValue", (Object)"");
            return errorObject;
        }
        return successObject;
    }
    
    public static boolean deleteDuplicatedRows(final String tableName, final String columnName, final String duplicatedEntry, final boolean sortOrder) {
        try {
            EMSDBSanitizer.logger.info("Deleting duplicated entries in ");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
            selectQuery.addSelectColumn(Column.getColumn(tableName, "*"));
            final Criteria criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)duplicatedEntry, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
            final List<String> primaryKeyList = tableDefinition.getPrimaryKey().getColumnList();
            final List<SortColumn> sortColumns = new ArrayList<SortColumn>();
            for (final String primaryKey : primaryKeyList) {
                sortColumns.add(new SortColumn(tableName, primaryKey, sortOrder));
            }
            if (dataObject != null && !dataObject.isEmpty() && dataObject.size(tableName) > 1) {
                dataObject.sortRows(tableName, (SortColumn[])sortColumns.toArray(new SortColumn[0]));
                for (int size = dataObject.size(tableName), i = 1; i < size; ++i) {
                    dataObject.deleteRow(dataObject.getRow(tableName));
                }
                SyMUtil.getPersistence().update(dataObject);
                return true;
            }
        }
        catch (final DataAccessException ex) {
            EMSDBSanitizer.logger.log(Level.SEVERE, "DataAccess Exception occured : ", (Throwable)ex);
        }
        catch (final Exception ex2) {
            EMSDBSanitizer.logger.log(Level.SEVERE, ex2.toString() + " occured : ", ex2);
        }
        return false;
    }
    
    static {
        EMSDBSanitizer.logger = Logger.getLogger(EMSDBSanitizer.class.getName());
    }
}
