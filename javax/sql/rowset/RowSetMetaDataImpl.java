package javax.sql.rowset;

import java.sql.Clob;
import java.sql.Blob;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.sql.Types;
import java.sql.SQLException;
import java.io.Serializable;
import javax.sql.RowSetMetaData;

public class RowSetMetaDataImpl implements RowSetMetaData, Serializable
{
    private int colCount;
    private ColInfo[] colInfo;
    static final long serialVersionUID = 6893806403181801867L;
    
    private void checkColRange(final int n) throws SQLException {
        if (n <= 0 || n > this.colCount) {
            throw new SQLException("Invalid column index :" + n);
        }
    }
    
    private void checkColType(final int n) throws SQLException {
        try {
            final Class<Types> clazz = Types.class;
            final Field[] fields = clazz.getFields();
            for (int i = 0; i < fields.length; ++i) {
                if (fields[i].getInt(clazz) == n) {
                    return;
                }
            }
        }
        catch (final Exception ex) {
            throw new SQLException(ex.getMessage());
        }
        throw new SQLException("Invalid SQL type for column");
    }
    
    @Override
    public void setColumnCount(final int colCount) throws SQLException {
        if (colCount <= 0) {
            throw new SQLException("Invalid column count. Cannot be less or equal to zero");
        }
        this.colCount = colCount;
        if (this.colCount != Integer.MAX_VALUE) {
            this.colInfo = new ColInfo[this.colCount + 1];
            for (int i = 1; i <= this.colCount; ++i) {
                this.colInfo[i] = new ColInfo();
            }
        }
    }
    
    @Override
    public void setAutoIncrement(final int n, final boolean autoIncrement) throws SQLException {
        this.checkColRange(n);
        this.colInfo[n].autoIncrement = autoIncrement;
    }
    
    @Override
    public void setCaseSensitive(final int n, final boolean caseSensitive) throws SQLException {
        this.checkColRange(n);
        this.colInfo[n].caseSensitive = caseSensitive;
    }
    
    @Override
    public void setSearchable(final int n, final boolean searchable) throws SQLException {
        this.checkColRange(n);
        this.colInfo[n].searchable = searchable;
    }
    
    @Override
    public void setCurrency(final int n, final boolean currency) throws SQLException {
        this.checkColRange(n);
        this.colInfo[n].currency = currency;
    }
    
    @Override
    public void setNullable(final int n, final int nullable) throws SQLException {
        if (nullable < 0 || nullable > 2) {
            throw new SQLException("Invalid nullable constant set. Must be either columnNoNulls, columnNullable or columnNullableUnknown");
        }
        this.checkColRange(n);
        this.colInfo[n].nullable = nullable;
    }
    
    @Override
    public void setSigned(final int n, final boolean signed) throws SQLException {
        this.checkColRange(n);
        this.colInfo[n].signed = signed;
    }
    
    @Override
    public void setColumnDisplaySize(final int n, final int columnDisplaySize) throws SQLException {
        if (columnDisplaySize < 0) {
            throw new SQLException("Invalid column display size. Cannot be less than zero");
        }
        this.checkColRange(n);
        this.colInfo[n].columnDisplaySize = columnDisplaySize;
    }
    
    @Override
    public void setColumnLabel(final int n, final String columnLabel) throws SQLException {
        this.checkColRange(n);
        if (columnLabel != null) {
            this.colInfo[n].columnLabel = columnLabel;
        }
        else {
            this.colInfo[n].columnLabel = "";
        }
    }
    
    @Override
    public void setColumnName(final int n, final String columnName) throws SQLException {
        this.checkColRange(n);
        if (columnName != null) {
            this.colInfo[n].columnName = columnName;
        }
        else {
            this.colInfo[n].columnName = "";
        }
    }
    
    @Override
    public void setSchemaName(final int n, final String schemaName) throws SQLException {
        this.checkColRange(n);
        if (schemaName != null) {
            this.colInfo[n].schemaName = schemaName;
        }
        else {
            this.colInfo[n].schemaName = "";
        }
    }
    
    @Override
    public void setPrecision(final int n, final int colPrecision) throws SQLException {
        if (colPrecision < 0) {
            throw new SQLException("Invalid precision value. Cannot be less than zero");
        }
        this.checkColRange(n);
        this.colInfo[n].colPrecision = colPrecision;
    }
    
    @Override
    public void setScale(final int n, final int colScale) throws SQLException {
        if (colScale < 0) {
            throw new SQLException("Invalid scale size. Cannot be less than zero");
        }
        this.checkColRange(n);
        this.colInfo[n].colScale = colScale;
    }
    
    @Override
    public void setTableName(final int n, final String tableName) throws SQLException {
        this.checkColRange(n);
        if (tableName != null) {
            this.colInfo[n].tableName = tableName;
        }
        else {
            this.colInfo[n].tableName = "";
        }
    }
    
    @Override
    public void setCatalogName(final int n, final String catName) throws SQLException {
        this.checkColRange(n);
        if (catName != null) {
            this.colInfo[n].catName = catName;
        }
        else {
            this.colInfo[n].catName = "";
        }
    }
    
    @Override
    public void setColumnType(final int n, final int colType) throws SQLException {
        this.checkColType(colType);
        this.checkColRange(n);
        this.colInfo[n].colType = colType;
    }
    
    @Override
    public void setColumnTypeName(final int n, final String colTypeName) throws SQLException {
        this.checkColRange(n);
        if (colTypeName != null) {
            this.colInfo[n].colTypeName = colTypeName;
        }
        else {
            this.colInfo[n].colTypeName = "";
        }
    }
    
    @Override
    public int getColumnCount() throws SQLException {
        return this.colCount;
    }
    
    @Override
    public boolean isAutoIncrement(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].autoIncrement;
    }
    
    @Override
    public boolean isCaseSensitive(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].caseSensitive;
    }
    
    @Override
    public boolean isSearchable(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].searchable;
    }
    
    @Override
    public boolean isCurrency(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].currency;
    }
    
    @Override
    public int isNullable(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].nullable;
    }
    
    @Override
    public boolean isSigned(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].signed;
    }
    
    @Override
    public int getColumnDisplaySize(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].columnDisplaySize;
    }
    
    @Override
    public String getColumnLabel(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].columnLabel;
    }
    
    @Override
    public String getColumnName(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].columnName;
    }
    
    @Override
    public String getSchemaName(final int n) throws SQLException {
        this.checkColRange(n);
        String schemaName = "";
        if (this.colInfo[n].schemaName != null) {
            schemaName = this.colInfo[n].schemaName;
        }
        return schemaName;
    }
    
    @Override
    public int getPrecision(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].colPrecision;
    }
    
    @Override
    public int getScale(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].colScale;
    }
    
    @Override
    public String getTableName(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].tableName;
    }
    
    @Override
    public String getCatalogName(final int n) throws SQLException {
        this.checkColRange(n);
        String catName = "";
        if (this.colInfo[n].catName != null) {
            catName = this.colInfo[n].catName;
        }
        return catName;
    }
    
    @Override
    public int getColumnType(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].colType;
    }
    
    @Override
    public String getColumnTypeName(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].colTypeName;
    }
    
    @Override
    public boolean isReadOnly(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].readOnly;
    }
    
    @Override
    public boolean isWritable(final int n) throws SQLException {
        this.checkColRange(n);
        return this.colInfo[n].writable;
    }
    
    @Override
    public boolean isDefinitelyWritable(final int n) throws SQLException {
        this.checkColRange(n);
        return true;
    }
    
    @Override
    public String getColumnClassName(final int n) throws SQLException {
        String s = String.class.getName();
        switch (this.getColumnType(n)) {
            case 2:
            case 3: {
                s = BigDecimal.class.getName();
                break;
            }
            case -7: {
                s = Boolean.class.getName();
                break;
            }
            case -6: {
                s = Byte.class.getName();
                break;
            }
            case 5: {
                s = Short.class.getName();
                break;
            }
            case 4: {
                s = Integer.class.getName();
                break;
            }
            case -5: {
                s = Long.class.getName();
                break;
            }
            case 7: {
                s = Float.class.getName();
                break;
            }
            case 6:
            case 8: {
                s = Double.class.getName();
                break;
            }
            case -4:
            case -3:
            case -2: {
                s = "byte[]";
                break;
            }
            case 91: {
                s = Date.class.getName();
                break;
            }
            case 92: {
                s = Time.class.getName();
                break;
            }
            case 93: {
                s = Timestamp.class.getName();
                break;
            }
            case 2004: {
                s = Blob.class.getName();
                break;
            }
            case 2005: {
                s = Clob.class.getName();
                break;
            }
        }
        return s;
    }
    
    @Override
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        if (this.isWrapperFor(clazz)) {
            return clazz.cast(this);
        }
        throw new SQLException("unwrap failed for:" + clazz);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        return clazz.isInstance(this);
    }
    
    private class ColInfo implements Serializable
    {
        public boolean autoIncrement;
        public boolean caseSensitive;
        public boolean currency;
        public int nullable;
        public boolean signed;
        public boolean searchable;
        public int columnDisplaySize;
        public String columnLabel;
        public String columnName;
        public String schemaName;
        public int colPrecision;
        public int colScale;
        public String tableName;
        public String catName;
        public int colType;
        public String colTypeName;
        public boolean readOnly;
        public boolean writable;
        static final long serialVersionUID = 5490834817919311283L;
        
        private ColInfo() {
            this.tableName = "";
            this.readOnly = false;
            this.writable = true;
        }
    }
}
