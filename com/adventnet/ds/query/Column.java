package com.adventnet.ds.query;

import java.util.concurrent.ConcurrentHashMap;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.logging.Level;
import java.lang.ref.SoftReference;
import java.util.Map;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.logging.Logger;
import java.io.Externalizable;

public class Column implements Externalizable, Cloneable
{
    private static final long serialVersionUID = -1989098407084027698L;
    private static final transient Logger OUT;
    public static final int DISTINCT = 1;
    public static final int COUNT = 2;
    public static final int MIN = 3;
    public static final int MAX = 4;
    public static final int SUM = 5;
    public static final int AVG = 6;
    private String tableAlias;
    private String columnName;
    private int columnIndex;
    private String columnAlias;
    private Column column;
    private int function;
    private int type;
    private ColumnDefinition columnDefinition;
    private boolean encrypted;
    private static final Map<String, SoftReference<Column>> KEYVSCOLUMN;
    private boolean immutable;
    private String key;
    int hashCode;
    private String dataType;
    
    @Deprecated
    public Column() {
        this.tableAlias = null;
        this.columnName = null;
        this.columnIndex = -1;
        this.columnAlias = null;
        this.column = null;
        this.function = 0;
        this.type = 1111;
        this.columnDefinition = null;
        this.encrypted = false;
        this.immutable = false;
        this.key = null;
        this.hashCode = -999999999;
        this.dataType = null;
    }
    
    public Column(final String tableAlias, final String columnName) {
        this(tableAlias, columnName, columnName);
    }
    
    public Column(final String tableAlias, final int columnIndex) {
        this(tableAlias, null);
        this.columnIndex = columnIndex;
    }
    
    public Column(final String tableAlias, final String columnName, final String columnAlias) {
        this.tableAlias = null;
        this.columnName = null;
        this.columnIndex = -1;
        this.columnAlias = null;
        this.column = null;
        this.function = 0;
        this.type = 1111;
        this.columnDefinition = null;
        this.encrypted = false;
        this.immutable = false;
        this.key = null;
        this.hashCode = -999999999;
        this.dataType = null;
        this.tableAlias = tableAlias;
        this.columnName = columnName;
        this.columnAlias = columnAlias;
    }
    
    public Column(final String tableAlias, final int columnIndex, final String columnAlias) {
        this(tableAlias, null, columnAlias);
        this.columnIndex = columnIndex;
    }
    
    public static Column getColumn(final String tableAlias, final String columnName) {
        return getColumn(tableAlias, columnName, null);
    }
    
    public static Column getColumn(final String tableAlias, final String columnName, final String columnAlias) {
        if ((tableAlias != null && !tableAlias.equals("") && columnName != null && !columnName.equals("")) || (tableAlias == null && columnName.equals("*"))) {
            final String keyName = (((tableAlias == null) ? "" : (tableAlias + "_")) + columnName + ((columnAlias == null || columnAlias.equals("")) ? "" : ("_" + columnAlias))).intern();
            final SoftReference<Column> sr = Column.KEYVSCOLUMN.get(keyName);
            Column retCol = null;
            if (sr != null) {
                retCol = sr.get();
                if (retCol == null) {
                    Column.OUT.log(Level.FINER, "SoftReference object cleared but finalize method not invoked hence  NULL Column obtained from Cache for the tableAlias :: [${0}] columnName :: [${1} ] columnAlias :: [${2}]", new Object[] { tableAlias, columnName, columnAlias });
                    retCol = getNewColumnInstanceFromCache(tableAlias, columnName, columnAlias, keyName);
                }
            }
            else {
                retCol = getNewColumnInstanceFromCache(tableAlias, columnName, columnAlias, keyName);
            }
            return retCol;
        }
        throw new IllegalArgumentException("TableAlias and ColumnName cannot be null/empty.");
    }
    
    private static Column getNewColumnInstanceFromCache(final String tableName, final String columnName, final String columnAlias, final String keyName) {
        Column retCol = null;
        if (columnAlias == null || columnAlias.equals("")) {
            retCol = new Column(tableName, columnName);
        }
        else {
            retCol = new Column(tableName, columnName, columnAlias);
        }
        final SoftReference<Column> sr = new SoftReference<Column>(retCol);
        Column.KEYVSCOLUMN.put(keyName, sr);
        retCol.key = keyName;
        retCol.immutable = true;
        return retCol;
    }
    
    private Column(final Column column, final int function) {
        this.tableAlias = null;
        this.columnName = null;
        this.columnIndex = -1;
        this.columnAlias = null;
        this.column = null;
        this.function = 0;
        this.type = 1111;
        this.columnDefinition = null;
        this.encrypted = false;
        this.immutable = false;
        this.key = null;
        this.hashCode = -999999999;
        this.dataType = null;
        this.column = column;
        this.function = function;
    }
    
    public Column distinct() {
        return new Column(this, 1);
    }
    
    public Column count() {
        return new Column(this, 2);
    }
    
    public Column minimum() {
        return new Column(this, 3);
    }
    
    public Column maximum() {
        return new Column(this, 4);
    }
    
    public Column summation() {
        return new Column(this, 5);
    }
    
    public Column average() {
        return new Column(this, 6);
    }
    
    public Column getColumn() {
        return this.column;
    }
    
    public String getTableAlias() {
        return this.tableAlias;
    }
    
    @Deprecated
    public void setTableAlias(final String tableAlias) {
        if (this.immutable) {
            throw new UnsupportedOperationException("Trying to change the tableAlias for a column instance which has been instantiated using Column.getColumn().");
        }
        this.tableAlias = tableAlias;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public int getColumnIndex() {
        return (this.getDefinition() != null) ? this.getDefinition().index() : this.columnIndex;
    }
    
    @Deprecated
    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }
    
    @Deprecated
    public void setColumnName(final String columnName) {
        if (this.immutable) {
            throw new UnsupportedOperationException("Trying to change the columnName for a column instance which has been instantiated using Column.getColumn().");
        }
        this.columnName = columnName;
    }
    
    public String getColumnAlias() {
        return this.columnAlias;
    }
    
    public void setColumnAlias(final String columnAlias) {
        if (this.immutable) {
            throw new UnsupportedOperationException("Trying to change the columnAlias for a column instance which has been instantiated using Column.getColumn().");
        }
        this.columnAlias = columnAlias;
    }
    
    public int getFunction() {
        return this.function;
    }
    
    public int getType() {
        if (this.columnDefinition != null) {
            return this.columnDefinition.getSQLType();
        }
        if (this.dataType != null) {
            return MetaDataUtil.getJavaSQLType(this.dataType);
        }
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public void setDefinition(final ColumnDefinition colDef) {
        this.columnDefinition = colDef;
        if (this.columnDefinition != null) {
            this.encrypted = this.columnDefinition.isEncryptedColumn();
        }
    }
    
    public ColumnDefinition getDefinition() {
        return this.columnDefinition;
    }
    
    public boolean isEncrypted() {
        return (this.getDefinition() != null) ? this.getDefinition().isEncryptedColumn() : this.encrypted;
    }
    
    @Override
    public boolean equals(final Object obj) {
        Column col = null;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Column)) {
            return false;
        }
        col = (Column)obj;
        if (col.getColumn() != null) {
            return col.getFunction() == this.getFunction() && this.getColumn().equals(col.getColumn());
        }
        boolean equals = false;
        equals = this.equals(this.tableAlias, col.getTableAlias());
        if (!equals) {
            return false;
        }
        if (col.getColumnIndex() != -1 && this.getColumnIndex() != -1) {
            return col.getColumnIndex() == this.getColumnIndex();
        }
        if (this.columnName != null && col.getColumnName() != null && !"<DUMMY_COLUMN>".equalsIgnoreCase(col.getColumnName())) {
            return this.columnName.equals(col.getColumnName());
        }
        return this.equals(this.columnAlias, col.getColumnAlias());
    }
    
    private boolean equals(final Object o1, final Object o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -999999999) {
            this.hashCode = this.hashCode(this.tableAlias);
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder columnBuffer = new StringBuilder();
        this.processColumn(this, columnBuffer);
        this.getAliasedColumn(this, columnBuffer);
        return columnBuffer.toString();
    }
    
    private void processColumn(final Column column, final StringBuilder columnBuffer) {
        if (column == null) {
            return;
        }
        Column subColumn = column.getColumn();
        if (subColumn != null) {
            this.processColumn(subColumn, columnBuffer);
        }
        else {
            subColumn = column;
            final String tAlias = column.getTableAlias();
            String cName = column.getColumnName();
            if (cName == null && this.getColumnIndex() != -1) {
                cName = String.valueOf(this.getColumnIndex());
            }
            if (tAlias != null) {
                columnBuffer.append(tAlias).append(".").append(cName);
            }
            else {
                columnBuffer.append(cName);
            }
        }
        final int func = column.getFunction();
        if (func != 0 && func == 1) {
            columnBuffer.insert(0, "DISTINCT(").append(")");
        }
        else if (func != 0 && func == 2) {
            columnBuffer.insert(0, "COUNT(").append(")");
        }
        else if (func != 0 && func == 3) {
            columnBuffer.insert(0, "MIN(").append(")");
        }
        else if (func != 0 && func == 4) {
            columnBuffer.insert(0, "MAX(").append(")");
        }
        else if (func != 0 && func == 5) {
            columnBuffer.insert(0, "SUM(").append(")");
        }
        else if (func != 0 && func == 6) {
            columnBuffer.insert(0, "AVG(").append(")");
        }
    }
    
    private void getAliasedColumn(final Column column, final StringBuilder colBuffer) {
        final String colAlias = column.getColumnAlias();
        final String colName = column.getColumnName();
        if ((colAlias != null && colName != null && !colName.equals(colAlias)) || (column.getFunction() != 0 && column.getFunction() != 1 && colAlias != null)) {
            colBuffer.append(" ").append(colAlias);
        }
    }
    
    public Object clone() {
        try {
            final Column newColumn = (Column)super.clone();
            newColumn.hashCode = -999999999;
            newColumn.immutable = false;
            return newColumn;
        }
        catch (final CloneNotSupportedException clne) {
            return null;
        }
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeUTF(this.tableAlias);
        if (this.columnName == null) {
            this.columnName = "";
        }
        out.writeUTF(this.columnName);
        out.writeInt(this.columnIndex);
        if (this.columnAlias == null) {
            this.columnAlias = "";
        }
        out.writeUTF(this.columnAlias);
        if (this.column == null) {
            out.writeUTF("COLUMN_NULL");
        }
        else {
            out.writeUTF("COLUMN_NOT_NULL");
            this.column.writeExternal(out);
        }
        out.writeInt(this.function);
        out.writeInt(this.type);
        out.writeBoolean(this.encrypted);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.tableAlias = in.readUTF();
        this.columnName = in.readUTF();
        if (this.columnName.trim().length() == 0) {
            this.columnName = null;
        }
        this.columnIndex = in.readInt();
        this.columnAlias = in.readUTF();
        if (this.columnAlias.trim().length() == 0) {
            this.columnAlias = null;
        }
        final String colNull = in.readUTF();
        if (colNull.equals("COLUMN_NULL")) {
            this.column = null;
        }
        else {
            (this.column = new Column()).readExternal(in);
        }
        this.function = in.readInt();
        this.type = in.readInt();
        this.encrypted = in.readBoolean();
    }
    
    public String getDataType() {
        if (this.columnDefinition != null) {
            return this.columnDefinition.getDataType();
        }
        return this.dataType;
    }
    
    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }
    
    public static Function createFunction(final String functionName, final Object... args) {
        return new Function(functionName, args);
    }
    
    public static Operation createOperation(final Operation.operationType operation, final Object lhsArg, final Object rhsArg) {
        return new Operation(operation, lhsArg, rhsArg);
    }
    
    void setFunction(final int function) {
        this.function = function;
    }
    
    void setColumn(final Column subColumn) {
        this.column = subColumn;
    }
    
    static {
        OUT = Logger.getLogger(Column.class.getName());
        KEYVSCOLUMN = new ConcurrentHashMap<String, SoftReference<Column>>(2500, 0.25f, 5);
    }
}
