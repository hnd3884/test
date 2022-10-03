package com.adventnet.db.persistence.metadata;

import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Objects;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.Locale;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.io.Serializable;

public class ColumnDefinition implements Serializable, Cloneable
{
    public static final String CHAR = "CHAR";
    public static final String INTEGER = "INTEGER";
    public static final String BIGINT = "BIGINT";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String DATE = "DATE";
    public static final String DATETIME = "DATETIME";
    public static final String TIME = "TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String BLOB = "BLOB";
    public static final String FLOAT = "FLOAT";
    public static final String DOUBLE = "DOUBLE";
    public static final String TINYINT = "TINYINT";
    public static final String DECIMAL = "DECIMAL";
    public static final String SCHAR = "SCHAR";
    public static final String SBLOB = "SBLOB";
    public static final String NCHAR = "NCHAR";
    public static final String DCJSON = "DCJSON";
    private static final Logger out;
    private String columnName;
    private String dataType;
    private int precision;
    private int sqlType;
    private int maxLength;
    private Object defaultValue;
    private AllowedValues allowedValues;
    private boolean nullable;
    private List constraints;
    private boolean unique;
    private String tableName;
    private boolean key;
    private boolean encrypted;
    private UniqueValueGeneration uniqueValueGeneration;
    private boolean isDynamic;
    private String physicalColumn;
    private String displayName;
    private String piiKey;
    private Long columnID;
    private ColumnDefinition rootColumn;
    private String description;
    private ColumnDefinition parentColumnDefn;
    private ColumnDefinition childColumnDefn;
    private int index;
    private long metaDigest;
    
    public ColumnDefinition() {
        this.precision = 0;
        this.maxLength = 0;
        this.defaultValue = null;
        this.allowedValues = null;
        this.nullable = true;
        this.constraints = null;
        this.encrypted = false;
        this.uniqueValueGeneration = null;
        this.isDynamic = false;
        this.physicalColumn = null;
        this.piiKey = null;
        this.columnID = null;
        this.rootColumn = null;
        this.parentColumnDefn = null;
        this.childColumnDefn = null;
        this.index = -1;
    }
    
    public void setColumnID(final Long id) {
        this.columnID = id;
    }
    
    @Deprecated
    public Long getColumnID() {
        return this.columnID;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final ColumnDefinition copy = (ColumnDefinition)super.clone();
        if (this.constraints != null) {
            copy.constraints = new ArrayList(this.constraints);
        }
        if (this.allowedValues != null) {
            copy.allowedValues = (AllowedValues)this.allowedValues.clone();
        }
        return copy;
    }
    
    public void setTemplateName(final String templateName) {
    }
    
    public ColumnDefinition getRootColumn() {
        return this.rootColumn;
    }
    
    public void setRootColumn(final ColumnDefinition cd) {
        this.rootColumn = cd;
    }
    
    public String getDisplayName() {
        if (this.displayName == null) {
            this.displayName = this.columnName;
        }
        return this.displayName;
    }
    
    public void setDisplayName(final String v) {
        this.displayName = v;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String v) {
        this.description = v;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public String getDataType() {
        return this.dataType;
    }
    
    public void setDataType(String dataType) {
        dataType = dataType.toUpperCase(Locale.ENGLISH);
        this.dataType = dataType;
        final DataTypeDefinition udt = DataTypeManager.getDataTypeDefinition(dataType);
        if (DataTypeUtil.isEDT(dataType)) {
            this.allowedValues = udt.getAllowedValues();
            this.sqlType = MetaDataUtil.getJavaSQLType(udt.getBaseType());
            this.maxLength = ((this.maxLength == 0) ? udt.getMaxLength() : this.maxLength);
            this.precision = ((this.precision == 0) ? udt.getPrecision() : this.precision);
            this.encrypted = (udt.getBaseType().equals("SCHAR") || udt.getBaseType().equals("SBLOB"));
            this.defaultValue = udt.getDefaultValue();
        }
        else if (DataTypeUtil.isUDT(dataType)) {
            this.sqlType = MetaDataUtil.getJavaSQLType(dataType);
            this.maxLength = udt.getMeta().getMaxLength(this.maxLength);
            this.precision = udt.getMeta().getPrecision(this.precision);
            this.encrypted = udt.getMeta().isEncrypted();
        }
        else {
            this.sqlType = MetaDataUtil.getJavaSQLType(dataType);
            this.encrypted = (dataType.equals("SCHAR") || dataType.equals("SBLOB"));
            if (this.dataType.equals("CHAR") || this.dataType.equals("NCHAR") || this.dataType.equals("SCHAR")) {
                this.maxLength = ((this.maxLength == 0) ? 50 : this.maxLength);
            }
            else if (!dataType.equals("BIGINT")) {
                if (!dataType.equals("INTEGER")) {
                    if (this.dataType.equals("DECIMAL")) {
                        this.maxLength = ((this.maxLength == 0) ? 16 : this.maxLength);
                        this.precision = ((this.precision == 0) ? 4 : this.precision);
                    }
                }
            }
        }
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public void setPrecision(final int decimalPlaces) {
        this.precision = decimalPlaces;
    }
    
    public int getSQLType() {
        return this.sqlType;
    }
    
    public void setSQLType(final int sqlType) {
        this.sqlType = sqlType;
    }
    
    public int getMaxLength() {
        return this.maxLength;
    }
    
    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }
    
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(final Object defaultValue) throws MetaDataException {
        this.defaultValue = defaultValue;
    }
    
    public AllowedValues getAllowedValues() {
        return this.allowedValues;
    }
    
    public void validateAllowedValues() {
        if (this.allowedValues != null) {
            this.allowedValues.validate();
        }
    }
    
    public void validateUniqueValueGeneration() {
        if (this.uniqueValueGeneration != null) {
            this.uniqueValueGeneration.validate();
        }
    }
    
    public void init() {
        if (this.allowedValues != null) {
            this.allowedValues.setTableName(this.tableName);
            this.allowedValues.setColumnName(this.columnName);
            if (DataTypeUtil.isEDT(this.dataType)) {
                this.allowedValues.setDataType(DataTypeManager.getDataTypeDefinition(this.dataType).getBaseType());
            }
            else if (DataTypeUtil.isUDT(this.dataType)) {
                this.allowedValues.setDataType(MetaDataUtil.getSQLTypeAsString(DataTypeManager.getSQLType(this.dataType)));
            }
            else {
                this.allowedValues.setDataType(this.dataType);
            }
            this.allowedValues.setMaxSize(this.maxLength);
            this.allowedValues.init();
        }
        if (this.defaultValue != null && this.defaultValue instanceof String) {
            try {
                this.defaultValue = convert((String)this.defaultValue, this.dataType);
            }
            catch (final MetaDataException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }
    
    public void setAllowedValues(final AllowedValues allowedValues) {
        this.allowedValues = allowedValues;
    }
    
    public boolean isNullable() {
        return this.nullable;
    }
    
    public void setNullable(final boolean nullable) {
        this.nullable = nullable;
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public void setUnique(final boolean unique) {
        this.unique = unique;
    }
    
    public List getConstraints() {
        return this.constraints;
    }
    
    public void setConstraints(final List constraints) {
        this.constraints = constraints;
    }
    
    public boolean isKey() {
        return this.key;
    }
    
    public void setKey(final boolean flag) {
        this.key = flag;
    }
    
    public boolean isEncryptedColumn() {
        return this.encrypted;
    }
    
    public UniqueValueGeneration getUniqueValueGeneration() {
        return this.uniqueValueGeneration;
    }
    
    public void setUniqueValueGeneration(final UniqueValueGeneration v) {
        this.uniqueValueGeneration = v;
    }
    
    public boolean matches(final ColumnDefinition toMatch) {
        if (!DataTypeManager.isDataTypeSupported(toMatch.dataType) || DataTypeManager.getDataTypeDefinition(toMatch.dataType).getMeta() == null) {
            return this == toMatch || this.dataType.equals(toMatch.dataType);
        }
        if (DataTypeManager.getDataTypeDefinition(toMatch.dataType).getMeta().isReferenceable()) {
            final List<String> dataTypes = DataTypeManager.getDataTypeDefinition(toMatch.dataType).getMeta().referenceableTypes();
            return this.dataType.equals(toMatch.dataType) || (dataTypes != null && dataTypes.contains(this.dataType));
        }
        throw new IllegalArgumentException("Datatype " + toMatch.dataType + " cannot be used as referenced column.");
    }
    
    public static Object convert(final String value, final String dataType) throws MetaDataException {
        if (dataType.equals("BLOB") || dataType.equals("SBLOB")) {
            throw new MetaDataException("Default value not allowed for BLOB Types");
        }
        final Object retVal = MetaDataUtil.convert(value, dataType);
        return retVal;
    }
    
    private boolean isEquals(final Object o1, final Object o2) {
        if (o1 == null) {
            if (o2 != null) {
                return false;
            }
        }
        else if (!o1.equals(o2)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ColumnDefinition) {
            final ColumnDefinition colDef = (ColumnDefinition)o;
            if (this.isEquals(colDef.getTableName(), this.getTableName()) && this.isEquals(colDef.getColumnName(), this.getColumnName()) && this.isEquals(colDef.getDataType(), this.getDataType()) && this.isEquals(colDef.getMaxLength(), this.getMaxLength()) && this.isEquals(colDef.getPrecision(), this.getPrecision()) && this.isEquals(colDef.isNullable(), this.isNullable()) && this.isEquals(colDef.getDefaultValue(), this.getDefaultValue()) && this.isEquals(colDef.getAllowedValues(), this.getAllowedValues()) && this.isEquals(colDef.getUniqueValueGeneration(), this.getUniqueValueGeneration()) && this.isEquals(colDef.getDescription(), this.getDescription()) && this.isEquals(colDef.getDisplayName(), this.getDisplayName()) && this.isEquals(colDef.getParentColumn(), this.getParentColumn()) && this.isEquals(colDef.getRootColumn(), this.getRootColumn())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.tableName, this.columnName, this.dataType, this.maxLength, this.precision, this.nullable, this.defaultValue, this.allowedValues, this.uniqueValueGeneration, this.description, this.displayName, this.parentColumnDefn, this.rootColumn);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("\n\t\t<ColumnDefinition>");
        sb.append("\n\t\t\t<tablename>" + this.tableName + "</tablename>");
        sb.append("\n\t\t\t<columnname>" + this.columnName + "</columnname>");
        sb.append("\n\t\t\t<datatype>" + this.dataType + "</datatype>");
        sb.append("\n\t\t\t<max-size>" + this.maxLength + "</max-size>");
        sb.append("\n\t\t\t<precision>" + this.precision + "</precision>");
        sb.append("\n\t\t\t<isnullable>" + this.isNullable() + "</isnullable>");
        sb.append("\n\t\t\t<isunique>" + this.isUnique() + "</isunique>");
        sb.append("\n\t\t\t<defaultvalue>" + this.defaultValue + "</defaultvalue>");
        sb.append("\n\t\t\t<sequencegenerator>" + this.uniqueValueGeneration + "</sequencegenerator>");
        sb.append("\n\t\t\t<constraints>" + this.constraints + "</constraints>");
        sb.append("\n\t\t\t<allowedvalues>" + this.allowedValues + "\t\t\t</allowedvalues>");
        sb.append("\n\t\t\t<isDynamic>" + this.isDynamic + "</isDynamic>");
        sb.append("\n\t\t\t<physicalColumn>" + this.physicalColumn + "</physicalColumn>");
        sb.append("\n\t\t</ColumnDefinition>");
        return sb.toString();
    }
    
    public void validate() {
        if (this.columnName == null) {
            throw new IllegalArgumentException("Column Name cannot be null for table " + this.tableName);
        }
        if (this.dataType == null) {
            throw new IllegalArgumentException("DataType for the column " + this.columnName + " cannot be null for table " + this.tableName);
        }
        this.validateDataType();
        this.init();
        this.validateAllowedValues();
        this.validateDefaultValue();
        if (this.uniqueValueGeneration != null && !this.dataType.equals("BIGINT") && !this.dataType.equals("INTEGER")) {
            throw new IllegalArgumentException("The Column " + this.columnName + " with data-type " + this.dataType + " cannot have uniqueValueGeneration for table " + this.tableName);
        }
        if (this.uniqueValueGeneration != null && this.uniqueValueGeneration.getGeneratorClass() != null && !this.dataType.equals("BIGINT")) {
            throw new IllegalArgumentException("The Column " + this.columnName + " with data-type " + this.dataType + " cannot have NonGapSequenceGenerator for table " + this.tableName + ". It can be set only for BIGINT DataType Column");
        }
        this.validateUniqueValueGeneration();
        if (this.isDynamic && this.isUnique()) {
            throw new IllegalArgumentException("The dynamic column " + this.columnName + " with data-type " + this.dataType + "for table " + this.tableName + "cannot be unique");
        }
    }
    
    public boolean isChildOf(final String parentTable) {
        return this.parentColumnDefn != null && this.parentColumnDefn.getTableName().equals(parentTable);
    }
    
    public void setParentColumn(final ColumnDefinition parentColumnDefn) {
        this.parentColumnDefn = parentColumnDefn;
    }
    
    void setChildColumn(final ColumnDefinition childColumnDefn) {
        this.childColumnDefn = childColumnDefn;
    }
    
    public ColumnDefinition getParentColumn() {
        return this.parentColumnDefn;
    }
    
    public ColumnDefinition getChildColumn() {
        return this.childColumnDefn;
    }
    
    public void validateDataType() {
        final List<String> dataTypes = DataTypeDefinition.getBasicDataTypes();
        if (!dataTypes.contains(this.dataType) && !DataTypeManager.isDataTypeSupported(this.dataType)) {
            throw new IllegalArgumentException("DataType :: " + this.dataType + " is not supported");
        }
    }
    
    @Deprecated
    public String name() {
        return this.columnName;
    }
    
    void setIndex(final int index) {
        this.index = index;
    }
    
    public int index() {
        if (this.index == -1) {
            throw new IllegalStateException("Index is not set for this column [" + this.columnName + "]");
        }
        return this.index;
    }
    
    public Object defaultValue() {
        return this.defaultValue;
    }
    
    public boolean isChild() {
        return this.parentColumnDefn != null;
    }
    
    public String UVGName() {
        final UniqueValueGeneration uvg = this.getUniqueValueGeneration();
        return (uvg != null) ? uvg.getGeneratorName() : null;
    }
    
    public long metaDigest() {
        if (this.metaDigest == 0L) {
            this.metaDigest = this.columnName.hashCode() + this.index() + this.sqlType + toDigestInt(this.isKey()) + toDigestInt(this.isNullable()) + toDigestInt(this.isChild());
        }
        return this.metaDigest;
    }
    
    public void resetMetaDigest() {
        this.metaDigest = 0L;
    }
    
    private static int toDigestInt(final boolean value) {
        return value ? 2 : 1;
    }
    
    public void validateDefaultValue() {
        if (this.defaultValue != null) {
            if (this.dataType == null) {
                throw new IllegalArgumentException("Set the datatype and then set Default-value");
            }
            String dataType = this.dataType;
            if (DataTypeUtil.isEDT(dataType)) {
                dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            }
            if ((dataType.equals("CHAR") || dataType.equals("NCHAR")) && (this.maxLength > 255 || this.maxLength == -1)) {
                throw new IllegalArgumentException("CHAR column '" + this.tableName + "." + this.columnName + "' with max-size equal to -1 or greater than 255 can't have a default value");
            }
            if (dataType.equals("BLOB") || dataType.equals("SBLOB") || dataType.equals("SCHAR")) {
                throw new IllegalArgumentException("BLOB/TEXT column '" + this.tableName + "." + this.columnName + "' can't have a default value");
            }
            if (!(this.defaultValue instanceof String)) {
                try {
                    AllowedValues.validateValueDataType(this.defaultValue, dataType);
                }
                catch (final IllegalArgumentException iae) {
                    throw new IllegalArgumentException("The given default-value \"" + this.defaultValue + "\" is not an value of \"" + this.dataType + "\" for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"", iae);
                }
            }
            if (this.allowedValues != null) {
                try {
                    this.allowedValues.validateValue(this.defaultValue);
                }
                catch (final IllegalArgumentException iae) {
                    String msg = iae.getMessage();
                    msg = msg.replaceFirst("The given value ", "The given Default value ");
                    throw new IllegalArgumentException(msg);
                }
            }
        }
    }
    
    public boolean isDynamic() {
        return this.isDynamic;
    }
    
    public void setDynamic(final boolean isDynamic) {
        this.isDynamic = isDynamic;
    }
    
    public String getPhysicalColumn() {
        return this.physicalColumn;
    }
    
    public void setPhysicalColumn(final String physicalColumn) {
        this.physicalColumn = physicalColumn;
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("tablename", (Object)this.tableName);
        jsonObject.put("columnname", (Object)this.columnName);
        jsonObject.put("datatype", (Object)this.dataType);
        jsonObject.put("maxlength", this.maxLength);
        jsonObject.put("precision", this.precision);
        jsonObject.put("isnullable", this.isNullable());
        jsonObject.put("isunique", this.isUnique());
        jsonObject.put("defaultvalue", this.defaultValue);
        if (this.uniqueValueGeneration != null) {
            jsonObject.put("uniquevaluegeneration", (Object)this.uniqueValueGeneration.toJSON());
        }
        jsonObject.put("sequencegenerator", (Object)this.tableName);
        if (this.constraints != null) {
            final JSONArray jsonArray = new JSONArray();
            for (final Object constraint : this.constraints) {
                jsonArray.put(constraint);
            }
            jsonObject.put("constraints", (Object)jsonArray);
        }
        if (this.allowedValues != null) {
            jsonObject.put("allowedvalues", (Object)this.allowedValues.toJSON());
        }
        jsonObject.put("isdynamic", this.isDynamic);
        jsonObject.put("physicalcolumn", (Object)this.physicalColumn);
        jsonObject.put("description", (Object)this.description);
        return jsonObject;
    }
    
    public String getPiiKey() {
        if (this.piiKey != null) {
            return this.piiKey;
        }
        return this.piiKey = this.tableName + "." + this.columnName + ".pii";
    }
    
    static {
        out = Logger.getLogger(ColumnDefinition.class.getName());
    }
}
