package com.adventnet.db.migration.task;

import java.sql.ResultSetMetaData;
import com.adventnet.db.adapter.TypeTransformer;
import java.util.function.IntFunction;
import com.adventnet.db.adapter.ResultSetAdapter;
import com.adventnet.db.adapter.mssql.MssqlResultSetAdapter;
import com.adventnet.db.adapter.mysql.MysqlResultSetAdapter;
import com.adventnet.db.adapter.postgres.PostgresResultSetAdapter;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.db.adapter.mssql.MssqlSQLGenerator;
import java.sql.SQLException;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.sql.ResultSet;

class ExtdResultSetForBulkCopy
{
    public static ResultSet of(final ResultSet rs, final TableDefinition tableDef) throws SQLException {
        return of(rs, tableDef, null);
    }
    
    public static ResultSet of(final ResultSet rs, final List<String> destDataTypes) throws SQLException {
        return of(rs, null, destDataTypes);
    }
    
    private static ResultSet of(final ResultSet rs, final TableDefinition tableDefinition, final List<String> destDataTypes) throws SQLException {
        final boolean isCharTreatedAsNChar = ((MssqlSQLGenerator)DBMigrationUtil.getDestDBAdapter().getSQLGenerator()).isCharTreatedAsNChar();
        final IntFunction<String> dataTypeFunction = column -> {
            if (tableDefinition != null) {
                final ColumnDefinition cd = tableDefinition.getColumnList().get(column - 1);
                final String dataType = cd.getDataType();
                if (DataTypeUtil.isEDT(dataType)) {
                    return DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
                }
                else {
                    return dataType;
                }
            }
            else {
                return (String)destDataTypes.get(column - 1);
            }
        };
        if (DBMigrationUtil.getSrcDBType() == DBMigrationUtil.DBType.POSTGRES) {
            final TypeTransformer transformer = (orginalType, alteredType, column, rsmd) -> {
                if (orginalType == 1111) {
                    final String dataType2 = dataTypeFunction.apply(column);
                    if (dataType2.equals("NCHAR") || dataType2.equalsIgnoreCase("nvarchar") || (dataType2.equals("CHAR") && isCharTreatedAsNChar)) {
                        return -9;
                    }
                    else {
                        return 12;
                    }
                }
                else if (orginalType == -2) {
                    return -3;
                }
                else if (orginalType == 91 || orginalType == 92) {
                    return 93;
                }
                else {
                    return orginalType;
                }
            };
            return new PostgresResultSetAdapter(rs, transformer);
        }
        if (DBMigrationUtil.getSrcDBType() == DBMigrationUtil.DBType.MYSQL) {
            final TypeTransformer transformer = (orginalType, alteredType, columnPos, rsmd) -> {
                if (orginalType == 12) {
                    final String dataType3 = dataTypeFunction.apply(columnPos);
                    if (dataType3.equals("NCHAR") || dataType3.equalsIgnoreCase("nvarchar") || (dataType3.equals("CHAR") && isCharTreatedAsNChar)) {
                        return -9;
                    }
                    else {
                        return 12;
                    }
                }
                else if (orginalType == -1) {
                    final String dataType4 = dataTypeFunction.apply(columnPos);
                    if (dataType4.equals("SCHAR")) {
                        return -4;
                    }
                    else {
                        return -1;
                    }
                }
                else if (orginalType == 91 || orginalType == 92) {
                    return 93;
                }
                else {
                    return orginalType;
                }
            };
            return new MysqlResultSetAdapter(rs, transformer);
        }
        if (DBMigrationUtil.getSrcDBType() == DBMigrationUtil.DBType.MSSQL) {
            final TypeTransformer transformer = (orginalType, alteredType, columnPos, rsmd) -> (orginalType == -151) ? 93 : orginalType;
            return new MssqlResultSetAdapter(rs, transformer);
        }
        return new ResultSetAdapter(rs);
    }
}
