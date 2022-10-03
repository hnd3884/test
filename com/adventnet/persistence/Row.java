package com.adventnet.persistence;

import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import com.adventnet.db.persistence.metadata.AllowedValues;
import com.zoho.conf.AppResources;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.extended.PiiValueHandler;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.logging.Logger;
import java.io.Externalizable;

public class Row implements Externalizable, Cloneable
{
    private static final long serialVersionUID = -6195622954204006929L;
    private static final transient Logger OUT;
    private String originalTableName;
    private transient TableDefinition tableDefinition;
    private String tableName;
    private Object[] values;
    private transient RowIdentifier t_pkValues;
    private transient RowIdentifier t_oldPkValues;
    private Object[] originalValues;
    private int[] dirtyArrColumns;
    String deletedAt;
    int operationIndex;
    int listIndex;
    private transient List columnTypes;
    private transient List tableColumns;
    private int rowState;
    private final PiiValueHandler piiValueHandler;
    private boolean containsUVGColInPK;
    transient boolean useOldPK;
    private boolean immutable;
    
    public Row() {
        this.t_pkValues = null;
        this.t_oldPkValues = null;
        this.dirtyArrColumns = null;
        this.deletedAt = "-1";
        this.operationIndex = -1;
        this.listIndex = -1;
        this.columnTypes = null;
        this.tableColumns = null;
        this.rowState = 0;
        this.piiValueHandler = MetaDataUtil.getPiiValueHandler();
        this.containsUVGColInPK = false;
        this.useOldPK = false;
        this.immutable = false;
    }
    
    public Row(final String tableName) {
        this(tableName, tableName);
    }
    
    public Row(final String originalTableName, final String tableName) {
        this.t_pkValues = null;
        this.t_oldPkValues = null;
        this.dirtyArrColumns = null;
        this.deletedAt = "-1";
        this.operationIndex = -1;
        this.listIndex = -1;
        this.columnTypes = null;
        this.tableColumns = null;
        this.rowState = 0;
        this.piiValueHandler = MetaDataUtil.getPiiValueHandler();
        this.containsUVGColInPK = false;
        this.useOldPK = false;
        this.immutable = false;
        if (originalTableName == null) {
            Row.OUT.log(Level.INFO, "Row object instantiated with table name as null");
            throw new IllegalArgumentException("Row instantiated with null table name");
        }
        if (tableName == null) {
            Row.OUT.log(Level.INFO, "Row object instantiated with table alias as null");
            throw new IllegalArgumentException("Row instantiated with null table alias");
        }
        this.originalTableName = originalTableName;
        this.tableName = tableName;
        final TableDefinition td = this.getTableDefinition();
        if (td == null) {
            Row.OUT.log(Level.INFO, "Row object instantiated for an unknown table {0}", originalTableName);
            throw new IllegalArgumentException("Row instantiated for an unknown table: " + originalTableName);
        }
        if (td.isTemplate() && originalTableName.equals(td.getTableName())) {
            throw new IllegalArgumentException("Specified table-name is a template-table [" + td.getTableName() + "]");
        }
        this.initFields(td);
        this.setDummyPKValues();
    }
    
    Row(final String originalTableName, final String tableName, final Object[] values) {
        this.t_pkValues = null;
        this.t_oldPkValues = null;
        this.dirtyArrColumns = null;
        this.deletedAt = "-1";
        this.operationIndex = -1;
        this.listIndex = -1;
        this.columnTypes = null;
        this.tableColumns = null;
        this.rowState = 0;
        this.piiValueHandler = MetaDataUtil.getPiiValueHandler();
        this.containsUVGColInPK = false;
        this.useOldPK = false;
        this.immutable = false;
        this.originalTableName = originalTableName;
        this.tableName = tableName;
        this.values = values;
        this.originalValues = this.cloneArray(values);
        this.setDummyPKValues();
    }
    
    private void setDummyPKValues() {
        final int[] pkColumns = this.getKeyIndices();
        for (int i = 0; i < pkColumns.length; ++i) {
            final String dataType = this.getColumnType(pkColumns[i]);
            if (DataTypeManager.getDataTypeDefinition(dataType) != null && DataTypeManager.getDataTypeDefinition(dataType).getMeta() != null && !DataTypeManager.getDataTypeDefinition(dataType).getMeta().processInput()) {
                final Object dummyVal = DataTypeManager.getDataTypeDefinition(dataType).getMeta().getValueHolder();
                this.set(pkColumns[i], dummyVal);
            }
        }
    }
    
    public Object get(final String columnName) {
        return this.get(this.findColumn(columnName));
    }
    
    private void checkRowState() {
        if (this.tableDefinition != null && this.rowState != this.tableDefinition.getState()) {
            throw new IllegalStateException("Row Structure has been modified and hence it can be no more used.");
        }
    }
    
    public boolean hasUVGColInPK() {
        return this.containsUVGColInPK;
    }
    
    boolean hasUVHValueInPK() {
        final int[] keyIndices = this.getKeyIndices();
        for (int i = 0; i < keyIndices.length; ++i) {
            if (this.values[i] instanceof UniqueValueHolder) {
                return true;
            }
        }
        return false;
    }
    
    public Object get(final int columnIndex) {
        try {
            return this.values[columnIndex - 1];
        }
        catch (final ArrayIndexOutOfBoundsException aie) {
            this.validateIndex(columnIndex);
            return null;
        }
    }
    
    public Integer getInt(final int columnIndex) {
        return this.getAs(columnIndex, Integer.class);
    }
    
    public Integer getInt(final String columnName) {
        return this.getAs(columnName, Integer.class);
    }
    
    public Long getLong(final int columnIndex) {
        return this.getAs(columnIndex, Long.class);
    }
    
    public Long getLong(final String columnName) {
        return this.getAs(columnName, Long.class);
    }
    
    public Float getFloat(final int columnIndex) {
        return this.getAs(columnIndex, Float.class);
    }
    
    public Float getFloat(final String columnName) {
        return this.getAs(columnName, Float.class);
    }
    
    public Double getDouble(final int columnIndex) {
        return this.getAs(columnIndex, Double.class);
    }
    
    public Double getDouble(final String columnName) {
        return this.getAs(columnName, Double.class);
    }
    
    public BigDecimal getBigDecimal(final int columnIndex) {
        return this.getAs(columnIndex, BigDecimal.class);
    }
    
    public BigDecimal getBigDecimal(final String columnName) {
        return this.getAs(columnName, BigDecimal.class);
    }
    
    public String getString(final int columnIndex) {
        return this.getAs(columnIndex, String.class);
    }
    
    public String getString(final String columnName) {
        return this.getAs(columnName, String.class);
    }
    
    public Boolean getBoolean(final int columnIndex) {
        return this.getAs(columnIndex, Boolean.class);
    }
    
    public Boolean getBoolean(final String columnName) {
        return this.getAs(columnName, Boolean.class);
    }
    
    public Date getDate(final int columnIndex) {
        return this.getAs(columnIndex, Date.class);
    }
    
    public Date getDate(final String columnName) {
        return this.getAs(columnName, Date.class);
    }
    
    public Time getTime(final int columnIndex) {
        return this.getAs(columnIndex, Time.class);
    }
    
    public Time getTime(final String columnName) {
        return this.getAs(columnName, Time.class);
    }
    
    public Timestamp getTimestamp(final int columnIndex) {
        return this.getAs(columnIndex, Timestamp.class);
    }
    
    public Timestamp getTimestamp(final String columnName) {
        return this.getAs(columnName, Timestamp.class);
    }
    
    public InputStream getBlob(final int columnIndex) {
        return this.getAs(columnIndex, InputStream.class);
    }
    
    public InputStream getBlob(final String columnName) {
        return this.getAs(columnName, InputStream.class);
    }
    
    public <T> T getAs(final int columnIndex, final Class<T> clazz) {
        final Object value = this.get(columnIndex);
        return (value == null) ? null : this.getAs(value, clazz);
    }
    
    public <T> T getAs(final String columnName, final Class<T> clazz) {
        final Object value = this.get(columnName);
        return (value == null) ? null : this.getAs(value, clazz);
    }
    
    private <T> T getAs(final Object value, final Class<T> clazz) {
        if (clazz.isInstance(value)) {
            return (T)value;
        }
        throw new IllegalStateException("Value of type " + value.getClass().getName() + " cannot be cast to " + clazz.getName());
    }
    
    public Object getOriginalValue(final String columnName) {
        return this.getOriginalValue(this.findColumn(columnName));
    }
    
    public Object getOriginalValue(final int columnIndex) {
        this.validateIndex(columnIndex);
        return (this.originalValues == null) ? null : this.originalValues[columnIndex - 1];
    }
    
    public void set(final String columnName, final Object value) {
        this.set(this.findColumn(columnName), value);
    }
    
    public void set(final int columnIndex, final Object value) {
        try {
            this.setBlindly(columnIndex, value);
        }
        catch (final ArrayIndexOutOfBoundsException aie) {
            this.validateIndex(columnIndex);
        }
    }
    
    void setAppropValue(final String columnName, final Object value) {
        final int index = this.findColumn(columnName);
        this.setAppropValue(index, value);
    }
    
    void setAppropValue(final int columnIndex, Object value) {
        Row.OUT.log(Level.FINE, "Set Approp value for Column {0}, value {1}", new Object[] { new Integer(columnIndex), value });
        if (!this.isExpectedType(columnIndex, value)) {
            value = this.convertType(columnIndex, value);
        }
        this.setBlindly(columnIndex, value, false);
    }
    
    void setAppropOrigValue(final String columnName, final Object value) {
        final int index = this.findColumn(columnName);
        this.setAppropOrigValue(index, value);
    }
    
    void setAppropOrigValue(final int columnIndex, Object value) {
        if (!this.isExpectedType(columnIndex, value)) {
            value = this.convertType(columnIndex, value);
        }
        final int pkColIndex = this.getPKColumnIndex(columnIndex);
        if (pkColIndex != -1) {
            this.setOldPKValue(pkColIndex, value);
        }
        this.originalValues[columnIndex - 1] = value;
    }
    
    private void setOldPKValue(final int columnIndex, final Object val) {
        if (this.t_oldPkValues == null) {
            this.t_oldPkValues = new RowIdentifier(this.tableName, this.getKeyIndices());
        }
        this.t_oldPkValues.set(columnIndex, val);
    }
    
    private boolean isExpectedType(final int columnIndex, final Object value) {
        if (value == null) {
            return true;
        }
        final Class cls = this.getClassType(columnIndex);
        final boolean isAssignableFrom = cls.isAssignableFrom(value.getClass());
        Row.OUT.log(Level.FINE, "Is Assignable {0} from {1} = {2}", new Object[] { cls, value.getClass(), isAssignableFrom });
        return isAssignableFrom;
    }
    
    private Class getClassType(final int columnIndex) {
        final String dataType = this.getColumnType(columnIndex);
        Row.OUT.log(Level.FINE, "GetClassType for {0} is {1} ", new Object[] { new Integer(columnIndex), dataType });
        if (dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR")) {
            return String.class;
        }
        if (dataType.equals("INTEGER") || dataType.equals("TINYINT")) {
            return Integer.class;
        }
        if (dataType.equals("BIGINT")) {
            return Long.class;
        }
        if (dataType.equals("BOOLEAN")) {
            return Boolean.class;
        }
        if (dataType.equals("FLOAT")) {
            return Float.class;
        }
        if (dataType.equals("DOUBLE")) {
            return Double.class;
        }
        if (dataType.equals("DATE")) {
            return java.util.Date.class;
        }
        if (dataType.equals("DATETIME") || dataType.equals("TIMESTAMP")) {
            return Timestamp.class;
        }
        if (dataType.equals("TIME")) {
            return Time.class;
        }
        if (dataType.equals("BLOB")) {
            return InputStream.class;
        }
        return String.class;
    }
    
    private Object convertType(final int columnIndex, final Object value) {
        if (value == null) {
            return null;
        }
        try {
            return MetaDataUtil.convert((String)value, this.getColumnType(columnIndex));
        }
        catch (final MetaDataException mde) {
            throw new IllegalArgumentException("Exception occured while converting the given object to its appropriate DataType . Value is " + value + " and columntype is " + this.getColumnType(columnIndex));
        }
    }
    
    void setBlindly(final int columnIndex, final Object value) {
        this.setBlindly(columnIndex, value, true);
    }
    
    void setBlindly(final int columnIndex, final Object value, final boolean dirtyFlag) {
        this.checkImmutable();
        final Object oldValue = this.values[columnIndex - 1];
        boolean useCompareTo = false;
        if (oldValue != null && oldValue.getClass().getName().equals("java.math.BigDecimal") && value != null && value.getClass().getName().equals("java.math.BigDecimal")) {
            useCompareTo = true;
        }
        Label_0151: {
            if (oldValue == null) {
                if (value != null) {
                    break Label_0151;
                }
            }
            else if (useCompareTo) {
                if (((BigDecimal)oldValue).compareTo((BigDecimal)value) != 0) {
                    break Label_0151;
                }
            }
            else if (!oldValue.equals(value)) {
                break Label_0151;
            }
            Row.OUT.log(Level.FINEST, "The old value {0} is same as the new value for the column index {1} in the table {2} with original table name {3}. Ignoring.", new Object[] { value, new Integer(columnIndex), this.tableName, this.originalTableName });
            return;
        }
        this.values[columnIndex - 1] = value;
        if (dirtyFlag) {
            this.markAsDirty(columnIndex);
        }
    }
    
    public void setAll(final List values) {
        this.checkImmutable();
        this.values = values.toArray(new Object[values.size()]);
        this.markAsDirty(-99);
    }
    
    public void setAll(final Map colVsValues) {
        this.checkImmutable();
        for (final Map.Entry entry : colVsValues.entrySet()) {
            final String columnName = entry.getKey();
            this.set(columnName, entry.getValue());
        }
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getOriginalTableName() {
        return this.originalTableName;
    }
    
    public int getOperationIndex() {
        return this.operationIndex;
    }
    
    public List getColumns() {
        if (this.tableColumns == null) {
            this.tableColumns = this.getTableDefinition().getColumnNames();
        }
        return this.tableColumns;
    }
    
    public List getColumnTypes() {
        if (this.columnTypes != null) {
            return this.columnTypes;
        }
        final List colList = this.getColumns();
        this.columnTypes = new ArrayList();
        final TableDefinition td = this.getTableDefinition();
        for (int colSize = colList.size(), i = 0; i < colSize; ++i) {
            final String columnName = colList.get(i);
            final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            this.columnTypes.add(cd.getDataType());
        }
        return this.columnTypes;
    }
    
    public String getColumnType(final String columnName) {
        final int index = this.findColumn(columnName);
        return this.getColumnType(index);
    }
    
    public String getColumnType(final int columnIndex) {
        return this.getColumnTypes().get(columnIndex - 1);
    }
    
    public int getSQLType(final int columnIndex) {
        return this.tableDefinition.getSQLType(columnIndex);
    }
    
    public List getPKColumns() {
        final PrimaryKeyDefinition pkDef = this.getTableDefinition().getPrimaryKey();
        return pkDef.getColumnList();
    }
    
    void useOldPK(final boolean flag) {
        this.useOldPK = flag;
    }
    
    public RowIdentifier getPKValues() {
        if (this.useOldPK && this.t_oldPkValues != null) {
            return this.t_oldPkValues;
        }
        if (this.t_pkValues != null) {
            return this.t_pkValues;
        }
        return this.t_pkValues = new RowIdentifier(this.tableName, this.getKeyIndices());
    }
    
    public boolean isPKColumn(final int columnIndex) {
        final int[] keys = this.getKeyIndices();
        for (int i = 0; i < keys.length; ++i) {
            if (columnIndex == keys[i]) {
                return true;
            }
        }
        return false;
    }
    
    private int getPKColumnIndex(final int columnIndex) {
        final int[] keys = this.getKeyIndices();
        for (int i = 0; i < keys.length; ++i) {
            if (columnIndex == keys[i]) {
                return i + 1;
            }
        }
        return -1;
    }
    
    public int[] getKeyIndices() {
        return this.getTableDefinition().getKeyIndices();
    }
    
    public List getValues() {
        return Arrays.asList(this.values);
    }
    
    public List getOriginalValues() {
        return (this.originalValues == null) ? null : Arrays.asList(this.originalValues);
    }
    
    public List getChangedColumnIndices() {
        if (this.dirtyArrColumns == null) {
            return null;
        }
        final int length = this.dirtyArrColumns.length;
        final ArrayList dirtyArrList = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            dirtyArrList.add(new Integer(this.dirtyArrColumns[i]));
        }
        return dirtyArrList;
    }
    
    public int[] getChangedColumnIndex() {
        return this.dirtyArrColumns;
    }
    
    public JSONObject getAsJSON() {
        final JSONObject json = new JSONObject();
        final List<Object> values = this.getValues();
        final List<String> columns = this.getColumns();
        for (int i = 0; i < values.size(); ++i) {
            if (values.get(i) != null) {
                try {
                    json.put(columns.get(i).toString().toLowerCase(), values.get(i));
                }
                catch (final JSONException e) {
                    Row.OUT.log(Level.SEVERE, "Exception occurred while getJSON", (Throwable)e);
                }
            }
        }
        return (json.length() == 0) ? null : json;
    }
    
    public void validate() throws DataAccessException {
        final Object[] newValues = this.values;
        if (newValues == null) {
            throw new DataAccessException("List of values set in a Row is null for the table " + this.tableName);
        }
        this.checkRowState();
        if (this.operationIndex == 1 || this.operationIndex == -1) {
            final int size = this.values.length;
            if (size != this.getColumns().size()) {
                throw new DataAccessException("Values for the row should match with the columns in number and type");
            }
            for (int i = 0; i < size; ++i) {
                this.validate(i + 1, this.values[i]);
            }
        }
        else if (this.dirtyArrColumns != null && this.dirtyArrColumns.length > 0) {
            for (final int j : this.dirtyArrColumns) {
                this.validate(j, this.values[j - 1]);
            }
        }
    }
    
    public void validate(final int index) throws DataAccessException {
        final List valList = this.getValues();
        final Object valueAtIndex = valList.get(index - 1);
        this.validate(index, valueAtIndex);
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("<").append(this.tableName).append(" ");
        final List columns = this.getColumns();
        for (int size = columns.size(), i = 0; i < size; ++i) {
            try {
                final String columnName = columns.get(i);
                final boolean isencrypted = this.getTableDefinition().getColumnDefinitionByName(columnName).isEncryptedColumn();
                final String piiValue = MetaDataUtil.getAttribute(this.getTableDefinition().getColumnDefinitionByName(columnName).getPiiKey());
                buff.append(columnName);
                buff.append("=\"");
                if (isencrypted) {
                    buff.append("******");
                }
                else if (piiValue == null) {
                    buff.append(this.get(i + 1));
                }
                else {
                    buff.append(this.piiValueHandler.getMaskedValue(this.get(i + 1), piiValue));
                }
                buff.append("\" ");
            }
            catch (final Exception e) {
                e.printStackTrace();
                buff.append("");
            }
        }
        buff.append("/>");
        return buff.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        Row compareRow = null;
        try {
            compareRow = (Row)obj;
        }
        catch (final ClassCastException ccex) {
            return false;
        }
        if (this.hashCode() != compareRow.hashCode()) {
            return false;
        }
        final String tableName = compareRow.getTableName();
        return tableName.equals(this.tableName) && compareRow.deletedAt.equals(this.deletedAt) && Arrays.equals(this.values, compareRow.values);
    }
    
    public Object clone() {
        return this.doClone(true);
    }
    
    public Object doClone(final boolean isValue) {
        Row newRow = null;
        try {
            newRow = (Row)super.clone();
            newRow.tableName = this.tableName;
            newRow.values = (isValue ? this.cloneArray(this.values) : this.values);
            newRow.originalValues = this.cloneArray(this.originalValues);
            if (this.dirtyArrColumns == null) {
                newRow.dirtyArrColumns = null;
            }
            else {
                final int length = this.dirtyArrColumns.length;
                newRow.dirtyArrColumns = new int[length];
                System.arraycopy(this.dirtyArrColumns, 0, newRow.dirtyArrColumns, 0, length);
            }
            newRow.t_pkValues = null;
            newRow.t_oldPkValues = null;
            newRow.operationIndex = -1;
            newRow.listIndex = -1;
            newRow.immutable = false;
        }
        catch (final CloneNotSupportedException cnse) {
            Row.OUT.log(Level.FINE, "Exception occured while cloning Row", cnse);
        }
        return newRow;
    }
    
    @Override
    public int hashCode() {
        return this.getPKValues().hashCode();
    }
    
    private Object[] cloneArray(final Object[] originalArray) {
        if (originalArray == null) {
            return null;
        }
        final Object[] newArr = new Object[originalArray.length];
        System.arraycopy(originalArray, 0, newArr, 0, originalArray.length);
        return newArr;
    }
    
    public TableDefinition getTableDefinition() {
        if (this.tableDefinition != null) {
            return this.tableDefinition;
        }
        try {
            this.tableDefinition = MetaDataUtil.getTableDefinitionByName(this.originalTableName);
            if (this.tableDefinition != null) {
                this.rowState = this.tableDefinition.getState();
            }
        }
        catch (final MetaDataException mde) {
            Row.OUT.log(Level.FINER, "Exception occured during getting definition for the table {0}\nMetaDataException: {1}", new Object[] { this.originalTableName, mde });
            final IllegalArgumentException iae = new IllegalArgumentException("Exception occured during getting definition for the table " + this.originalTableName);
            iae.initCause(mde);
            throw iae;
        }
        return this.tableDefinition;
    }
    
    private void initFields(final TableDefinition td) {
        final List columnList = td.getColumnList();
        final int size = columnList.size();
        this.values = new Object[size];
        for (int i = 0; i < size; ++i) {
            final ColumnDefinition cd = columnList.get(i);
            this.values[i] = cd.getDefaultValue();
            final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
            if (uvg != null) {
                final UniqueValueHolder uvh = new UniqueValueHolder();
                if (uvg.isInstanceSpecificSequenceGeneratorEnabled()) {
                    uvh.setGeneratorName(uvg.getGeneratorNameForTemplateInstance(this.originalTableName, cd.getColumnName()));
                    uvh.setTableName(this.originalTableName);
                }
                else {
                    uvh.setGeneratorName(uvg.getGeneratorName());
                    uvh.setTableName(td.getTableName());
                }
                this.values[i] = uvh;
                this.containsUVGColInPK = (Arrays.binarySearch(this.getKeyIndices(), i + 1) >= 0);
            }
        }
        this.originalValues = this.cloneArray(this.values);
    }
    
    public int findColumn(final String columnName) {
        if (columnName == null) {
            throw new IllegalArgumentException("Column name can not be null");
        }
        int index = this.getColumns().indexOf(columnName);
        if (index == -1) {
            final IllegalArgumentException iae = new IllegalArgumentException("Unknown column " + columnName + " in the table " + this.originalTableName);
            Row.OUT.log(Level.FINER, "Value can't be set to unknown column {0} in the row {1}", new Object[] { columnName, this });
            Row.OUT.log(Level.FINER, "Exception thrown", iae);
            throw iae;
        }
        return ++index;
    }
    
    private void validateIndex(final int columnIndex) {
        if (columnIndex < 1) {
            throw new ArrayIndexOutOfBoundsException("Column index starts from 1. Given index: " + columnIndex + ". Table: " + this.tableName);
        }
        if (columnIndex > this.values.length) {
            throw new ArrayIndexOutOfBoundsException("Column index out of bounds. Index: " + columnIndex + ", Column count: " + this.values.length + ". Table: " + this.tableName);
        }
    }
    
    private void validate(final int columnIndex, final Object value) throws DataAccessException {
        this.checkRowState();
        final TableDefinition td = this.getTableDefinition();
        final List<String> colList = td.getColumnNames();
        final String colName = colList.get(columnIndex - 1);
        if (!(value instanceof UniqueValueHolder)) {
            final ColumnDefinition colDefn = td.getColumnDefinitionByName(colName);
            String dataType = colDefn.getDataType();
            if (value == null) {
                if (!colDefn.isNullable()) {
                    if (colDefn.isDynamic()) {
                        throw new DataAccessException("The column, " + colName + " in " + td.getTableName() + " has null value but is specified a not null constraint in data-dictionary");
                    }
                    Row.OUT.log(Level.WARNING, "The column, {0} in {1} has null value but is specified a not null constraint in data-dictionary", new Object[] { colName, td.getTableName() });
                }
            }
            else {
                if (DataTypeManager.getDataTypeDefinition(dataType) != null) {
                    if (DataTypeManager.getDataTypeDefinition(dataType).getBaseType() != null) {
                        final DataTypeValidator dtv = DataTypeManager.getDataTypeDefinition(dataType).getValidator();
                        if (dtv != null) {
                            dtv.validate(value);
                        }
                        dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
                    }
                    else if (DataTypeManager.getDataTypeDefinition(dataType).getMeta() != null) {
                        DataTypeManager.getDataTypeDefinition(dataType).getMeta().validate(value, colDefn.getMaxLength(), colDefn.getPrecision());
                    }
                }
                if (!DataTypeManager.isDataTypeSupported(dataType)) {
                    try {
                        MetaDataUtil.validate(value, dataType);
                    }
                    catch (final MetaDataException e) {
                        throw new DataAccessException("The given value " + value + " for column " + colName + " in " + td.getTableName() + " is not compatible to " + dataType + " specified in the data-dictionary", e);
                    }
                }
                if (dataType.equals("INTEGER") && value instanceof Long) {
                    final Integer val = ((Long)value).intValue();
                    this.set(columnIndex, val);
                }
                else if ((dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR")) && value instanceof String) {
                    final String val2 = (String)value;
                    final int maxLength = colDefn.getMaxLength();
                    final boolean maxlengthcheck = AppResources.getBoolean("validate.textcolumn.maxlength", Boolean.valueOf(false));
                    if ("true".equalsIgnoreCase(PersistenceInitializer.getConfigurationValue("trim-data-if-length-exceeds")) && maxLength != -1 && val2.length() > maxLength) {
                        this.values[columnIndex - 1] = ((String)value).substring(0, maxLength);
                        Row.OUT.log(Level.WARNING, "The length of the data specified for the column [{0}.{1}] is more than the specified length [{2}].", new Object[] { td.getTableName(), colName, maxLength });
                    }
                    else if (colDefn.getMaxLength() != -1 && val2.length() > colDefn.getMaxLength() && (maxlengthcheck || (colDefn.getMaxLength() > 0 && colDefn.getMaxLength() < 256) || !PersistenceInitializer.onSAS())) {
                        final DataAccessException dataAccessException = new DataAccessException("The size of the value [" + value + "] of the column [" + colName + "] specified in the row [" + this + "] is more than the maxLength [" + new Integer(colDefn.getMaxLength()) + "] of the column.");
                        dataAccessException.setTableName(td.getTableName());
                        dataAccessException.setColumnNames(new String[] { colName });
                        throw dataAccessException;
                    }
                }
                else if ((dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR")) && !(value instanceof String)) {
                    try {
                        this.set(columnIndex, MetaDataUtil.convert(String.valueOf(value), dataType));
                        Row.OUT.log(Level.WARNING, "The given value " + value + " for column " + colName + " in " + td.getTableName() + " is converted from " + value.getClass().getName() + " to " + dataType + " specified in the data-dictionary");
                    }
                    catch (final MetaDataException e) {
                        Row.OUT.log(Level.WARNING, "The given value " + value + " for column " + colName + " in " + td.getTableName() + " cannot be converted from " + value.getClass().getName() + " to " + dataType + " specified in the data-dictionary");
                        e.printStackTrace();
                    }
                }
                else if ((dataType.equals("DATE") || dataType.equals("TIME") || dataType.equals("DATETIME") || dataType.equals("TIMESTAMP")) && value instanceof String) {
                    try {
                        this.set(columnIndex, MetaDataUtil.convert(String.valueOf(value), dataType));
                    }
                    catch (final MetaDataException e) {
                        Row.OUT.log(Level.WARNING, "The given value " + value + " for column " + colName + " in " + td.getTableName() + " cannot be converted from " + value.getClass().getName() + " to " + dataType + " specified in the data-dictionary");
                        e.printStackTrace();
                    }
                }
                else if (dataType.equals("BOOLEAN") && !(value instanceof Boolean)) {
                    try {
                        this.set(columnIndex, MetaDataUtil.convert(String.valueOf(value), dataType));
                        Row.OUT.log(Level.WARNING, "The given value " + value + " for column " + colName + " in " + td.getTableName() + " is converted from " + value.getClass().getName() + " to " + dataType + " specified in the data-dictionary");
                    }
                    catch (final MetaDataException e) {
                        Row.OUT.log(Level.WARNING, "The column {0} in {1} is given a value that is instance of {2} that contradicts with {3} specified in the data-dictionary", new Object[] { colName, td.getTableName(), value.getClass().getName(), dataType });
                        e.printStackTrace();
                    }
                }
                final AllowedValues allowedVal = colDefn.getAllowedValues();
                if (allowedVal != null) {
                    try {
                        allowedVal.validateValue(value);
                    }
                    catch (final IllegalArgumentException e2) {
                        if (PersistenceInitializer.onSAS()) {
                            final DataAccessException dataAccessException2 = new DataAccessException("Illegal AllowedValue for the column " + colDefn.getTableName() + "." + colDefn.getColumnName() + " :: " + e2.getMessage(), e2);
                            dataAccessException2.setTableName(td.getTableName());
                            dataAccessException2.setColumnNames(new String[] { colName });
                            throw dataAccessException2;
                        }
                        throw e2;
                    }
                }
            }
        }
    }
    
    void markAsClean() {
        this.originalValues = this.cloneArray(this.values);
        this.dirtyArrColumns = null;
        this.t_oldPkValues = null;
        this.operationIndex = -1;
        this.listIndex = -1;
    }
    
    void markAsDirty(final String columnName) {
        final int columnIndex = this.findColumn(columnName);
        this.markAsDirty(columnIndex);
    }
    
    public void markAsDirty(final int columnIndex) {
        if (columnIndex == -99) {
            final int size = this.getColumns().size();
            this.dirtyArrColumns = new int[size];
            for (int i = 1; i <= size; ++i) {
                this.dirtyArrColumns[i - 1] = i;
            }
        }
        else if (this.dirtyArrColumns == null) {
            (this.dirtyArrColumns = new int[1])[0] = columnIndex;
        }
        else {
            Arrays.sort(this.dirtyArrColumns);
            if (Arrays.binarySearch(this.dirtyArrColumns, columnIndex) < 0) {
                final int length = this.dirtyArrColumns.length;
                final int[] tempArr = new int[length + 1];
                System.arraycopy(this.dirtyArrColumns, 0, tempArr, 0, length);
                tempArr[length] = columnIndex;
                this.dirtyArrColumns = tempArr;
            }
        }
        final int pkColumnIndex = this.getPKColumnIndex(columnIndex);
        if (pkColumnIndex != -1 && this.t_pkValues != null) {
            this.t_oldPkValues = ((this.t_oldPkValues == null) ? this.t_pkValues : this.t_oldPkValues);
            this.t_pkValues = null;
        }
    }
    
    void makeImmutable() {
        this.immutable = true;
    }
    
    private void checkImmutable() {
        if (this.immutable) {
            throw new IllegalStateException("Row " + this + " is immutable. Cannot modify its content");
        }
    }
    
    void clearOldPKValues() {
        this.t_oldPkValues = null;
    }
    
    RowIdentifier getOldPKValues() {
        return this.t_oldPkValues;
    }
    
    boolean hasPKChanged() {
        return this.t_oldPkValues != null;
    }
    
    private void writeData(final ObjectOutput out, final Object[] values) throws IOException {
        for (final Object value : values) {
            if (value instanceof Integer) {
                out.writeInt(4);
                out.writeInt((int)value);
            }
            else if (value instanceof String) {
                out.writeInt(12);
                out.writeUTF((String)value);
            }
            else if (value instanceof Boolean) {
                out.writeInt(16);
                out.writeBoolean((boolean)value);
            }
            else if (value instanceof Long) {
                out.writeInt(-5);
                out.writeLong((long)value);
            }
            else if (value instanceof Double) {
                out.writeInt(8);
                out.writeDouble((double)value);
            }
            else if (value instanceof Float) {
                out.writeInt(6);
                out.writeFloat((float)value);
            }
            else if (value instanceof InputStream) {
                Row.OUT.log(Level.FINER, "Serializing BLOB value");
                final BlobVal bv = new BlobVal((InputStream)value);
                out.writeInt(2004);
                out.writeObject(bv);
            }
            else {
                out.writeInt(1111);
                out.writeObject(value);
            }
        }
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeUTF("_addons");
        out.writeUTF(this.tableName);
        out.writeUTF(this.originalTableName);
        out.writeInt(this.values.length);
        this.writeData(out, this.values);
        final boolean hasDirtyColumns = this.dirtyArrColumns != null && this.dirtyArrColumns.length > 0;
        out.writeBoolean(hasDirtyColumns);
        if (hasDirtyColumns) {
            out.writeObject(this.dirtyArrColumns);
        }
        final boolean hasOrigValues = this.originalValues != null && this.originalValues.length > 0;
        out.writeBoolean(hasOrigValues);
        if (hasOrigValues) {
            out.writeInt(this.originalValues.length);
            this.writeData(out, this.originalValues);
        }
        out.writeUTF("MetaDigest");
        out.writeLong(this.tableDefinition.metaDigest());
        Row.OUT.log(Level.FINE, "MetaDigest Written :: {0}", this.tableDefinition.metaDigest());
        out.writeUTF(this.deletedAt);
        out.flush();
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        long metaDigest = 0L;
        try {
            boolean old = Boolean.TRUE;
            this.tableName = in.readUTF();
            if (this.tableName.equalsIgnoreCase("_addons")) {
                this.tableName = in.readUTF();
                old = Boolean.FALSE;
            }
            this.originalTableName = in.readUTF();
            this.values = this.fillData(in);
            if (!old) {
                final boolean hasDirtyColumns = in.readBoolean();
                if (hasDirtyColumns) {
                    this.dirtyArrColumns = (int[])in.readObject();
                }
                final boolean hasOrigValues = in.readBoolean();
                if (hasOrigValues) {
                    this.originalValues = this.fillData(in);
                }
                final String metaCheck = in.readUTF();
                if (metaCheck.equals("MetaDigest")) {
                    metaDigest = in.readLong();
                    Row.OUT.log(Level.FINE, "MetaOriginal :: {0}", this.getTableDefinition().metaDigest());
                    Row.OUT.log(Level.FINE, "MetaSerialized :: {0}", metaDigest);
                    if (this.getTableDefinition().metaDigest() != metaDigest) {
                        throw new IOException("Table Definition altered! Cannot Deserialize!!!");
                    }
                    this.deletedAt = in.readUTF();
                }
                else {
                    this.deletedAt = metaCheck;
                }
            }
            else {
                this.deletedAt = in.readUTF();
            }
        }
        catch (final Exception e) {
            if (this.getTableDefinition().metaDigest() != metaDigest) {
                e.printStackTrace();
                final IOException ioe = new IOException("Table Definition altered! Cannot Deserialize!!!");
                ioe.initCause(e);
                throw ioe;
            }
            e.printStackTrace();
            final IOException ioe = new IOException("Exception while deserializing...");
            ioe.initCause(e);
            throw ioe;
        }
    }
    
    private Object[] fillData(final ObjectInput in) throws IOException, ClassNotFoundException {
        try {
            final int size = in.readInt();
            final Object[] values = new Object[size];
            Row.OUT.log(Level.FINEST, "DataType Size {0}", new Integer(size));
            for (int i = 0; i < size; ++i) {
                final int type = in.readInt();
                switch (type) {
                    case -6:
                    case 4: {
                        values[i] = new Integer(in.readInt());
                        break;
                    }
                    case 12: {
                        values[i] = in.readUTF();
                        break;
                    }
                    case 16: {
                        values[i] = in.readBoolean();
                        break;
                    }
                    case -5: {
                        values[i] = new Long(in.readLong());
                        break;
                    }
                    case 8: {
                        values[i] = new Double(in.readDouble());
                        break;
                    }
                    case 6: {
                        values[i] = new Float(in.readFloat());
                        break;
                    }
                    case 2004: {
                        Row.OUT.log(Level.FINER, "Deserializing BLOB value");
                        final BlobVal v = (BlobVal)in.readObject();
                        values[i] = v.getBinaryStream();
                        break;
                    }
                    default: {
                        values[i] = in.readObject();
                        break;
                    }
                }
            }
            return values;
        }
        catch (final IOException exc) {
            Row.OUT.log(Level.SEVERE, "Exception while fill data", exc);
            throw exc;
        }
    }
    
    void reinit(final String tableName, final String tableAlias, final Object[] values) {
        this.originalTableName = tableName;
        this.tableName = tableAlias;
        this.values = values;
        this.originalValues = values;
        this.tableDefinition = null;
        this.columnTypes = null;
        this.tableColumns = null;
        if (this.t_pkValues != null) {
            final int[] keyIndices = this.getKeyIndices();
            this.t_pkValues.init(tableAlias, keyIndices);
        }
        this.t_oldPkValues = null;
    }
    
    int size() {
        return this.values.length;
    }
    
    static {
        OUT = Logger.getLogger(Row.class.getName());
    }
    
    public class RowIdentifier implements Serializable
    {
        private static final long serialVersionUID = -6195622954204006929L;
        Object[] values;
        int hashCode;
        
        RowIdentifier(final String tableName, final int[] keys) {
            this.hashCode = 0;
            this.init(tableName, keys);
        }
        
        void init(final String tableName, final int[] keys) {
            (this.values = new Object[keys.length + 1])[0] = tableName;
            for (int i = 0; i < keys.length; ++i) {
                this.values[i + 1] = Row.this.get(keys[i]);
            }
            this.hashCode = 0;
        }
        
        @Override
        public int hashCode() {
            final int length = this.values.length;
            if (this.hashCode == 0) {
                for (int i = 0; i < length; ++i) {
                    this.hashCode += (i + 1) * ((this.values[i] != null) ? this.values[i].hashCode() : 0);
                }
            }
            return this.hashCode;
        }
        
        public void set(final int index, final Object value) {
            Row.this.checkImmutable();
            this.values[index] = value;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (this.hashCode() != obj.hashCode()) {
                return false;
            }
            final RowIdentifier rid = (RowIdentifier)obj;
            return Arrays.equals(this.values, rid.values);
        }
        
        @Override
        public String toString() {
            final StringBuffer buff = new StringBuffer();
            buff.append("<");
            buff.append(this.values[0]);
            buff.append("_PK ");
            final List columns = Row.this.getPKColumns();
            for (int i = 1; i < this.values.length; ++i) {
                final String columnName = columns.get(i - 1);
                buff.append(columnName);
                buff.append("=\"");
                buff.append(this.values[i]);
                buff.append("\"");
                if (i < this.values.length - 1) {
                    buff.append(", ");
                }
            }
            buff.append("/>");
            return buff.toString();
        }
    }
    
    class BlobVal implements Serializable
    {
        byte[] bytes;
        transient ByteArrayInputStream bis;
        
        BlobVal(final InputStream is) throws IOException {
            this.bytes = null;
            this.bis = null;
            try {
                final BufferedInputStream bis = new BufferedInputStream(is, 2048);
                final ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);
                int bytesRead = 0;
                final long offset = 0L;
                final byte[] readBytes = new byte[2048];
                do {
                    bytesRead = bis.read(readBytes);
                    if (bytesRead > 0) {
                        bos.write(readBytes, (int)offset, bytesRead);
                    }
                } while (bytesRead == 2048);
                this.bytes = bos.toByteArray();
                bis.close();
            }
            catch (final IOException ioe) {
                Row.OUT.log(Level.WARNING, "Exception occured while serializing BLOB column {0}", ioe);
                throw ioe;
            }
        }
        
        InputStream getBinaryStream() {
            if (this.bis == null) {
                this.bis = new ByteArrayInputStream(this.bytes);
            }
            return this.bis;
        }
    }
}
