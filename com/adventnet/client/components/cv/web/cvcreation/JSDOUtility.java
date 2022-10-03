package com.adventnet.client.components.cv.web.cvcreation;

import java.text.StringCharacterIterator;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;

public class JSDOUtility
{
    public static String generateJS(final DataObject dob, final String jsDOVariableName) throws DataAccessException {
        return generateJS(dob, jsDOVariableName, false);
    }
    
    public static String generateJS(final DataObject dob, final String jsDOVariableName, final boolean escape) throws DataAccessException {
        List tabNames = dob.getTableNames();
        tabNames = PersistenceUtil.sortTables(tabNames);
        final String variableName = jsDOVariableName;
        final StringBuffer jsCode = new StringBuffer();
        for (int i = 0; i < tabNames.size(); ++i) {
            final String tmpTable = tabNames.get(i);
            final int rowIndex = 0;
            final Iterator rowsInTable = dob.getRows(tmpTable);
            while (rowsInTable.hasNext()) {
                final Row rw = rowsInTable.next();
                final String rowScript = generateRow(tmpTable, rw, variableName, escape);
                jsCode.append("\n");
                jsCode.append(rowScript);
            }
        }
        return jsCode.toString();
    }
    
    public static String generateRow(final String tableName, final Row rw, final String variableName) {
        return generateRow(tableName, rw, variableName, false);
    }
    
    public static String generateRow(final String tableName, final Row rw, final String variableName, final boolean escape) {
        final StringBuffer buff = new StringBuffer();
        buff.append(variableName);
        buff.append(".");
        buff.append("addRowsForTable");
        buff.append("(");
        buff.append("\"");
        buff.append(tableName);
        buff.append("\"");
        buff.append(",");
        buff.append("new Array");
        buff.append("(");
        TableDefinition tabDef = null;
        try {
            tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException exp) {
            exp.printStackTrace();
        }
        final List list = rw.getColumns();
        for (int i = 0; i < list.size(); ++i) {
            final String colName = list.get(i);
            Object colValue = rw.get((String)list.get(i));
            if (tabDef != null) {
                final ColumnDefinition colDef = tabDef.getColumnDefinitionByName(colName);
                final UniqueValueGeneration uvg = colDef.getUniqueValueGeneration();
                if (uvg != null && colValue instanceof UniqueValueHolder && ((UniqueValueHolder)colValue).getValue() == null) {
                    final String pattern = tableName + ":" + colName.toLowerCase() + ":" + colValue;
                    ((UniqueValueHolder)colValue).setValue((Object)pattern);
                }
            }
            buff.append("new Array");
            buff.append("(");
            buff.append("\"");
            buff.append(colName);
            buff.append("\"");
            buff.append(",");
            buff.append("\"");
            if (colValue != null) {
                if (colValue instanceof UniqueValueHolder) {
                    final Object temp = ((UniqueValueHolder)colValue).getValue();
                    colValue = ((temp == null) ? colValue : temp);
                }
                buff.append(escape ? escape(colValue.toString()) : colValue.toString());
            }
            else {
                buff.append("");
            }
            buff.append("\"");
            buff.append(")");
            if (i != list.size() - 1) {
                buff.append(",");
            }
        }
        buff.append(")");
        buff.append(")");
        buff.append(";");
        return buff.toString();
    }
    
    private static String escape(final String str) {
        final StringBuffer result = new StringBuffer();
        final StringCharacterIterator iterator = new StringCharacterIterator(str);
        for (char character = iterator.current(); character != '\uffff'; character = iterator.next()) {
            if (character == '\n') {
                result.append("\\n");
            }
            else if (character == '\r') {
                result.append("\\r");
            }
            else if (character == '\"') {
                result.append("\\\"");
            }
            else {
                result.append(character);
            }
        }
        return result.toString();
    }
}
