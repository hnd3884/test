package com.adventnet.db.persistence.metadata;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.math.BigDecimal;
import java.util.Locale;
import java.io.ByteArrayInputStream;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;
import java.io.Serializable;

public class AllowedValues implements Serializable, Cloneable
{
    private static final long serialVersionUID = -6035310617471402605L;
    private avTypes avType;
    private boolean isValid;
    private Object fromVal;
    private Object toVal;
    private String tableName;
    private String columnName;
    private String dataType;
    private int maxSize;
    private List valueList;
    private String patternStr;
    private Pattern pattern;
    
    public AllowedValues() {
        this.avType = avTypes.UNASSIGNED;
        this.isValid = false;
        this.tableName = null;
        this.columnName = null;
        this.dataType = null;
        this.maxSize = 0;
        this.valueList = null;
        this.patternStr = null;
        this.pattern = null;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final AllowedValues copy = (AllowedValues)super.clone();
        if (this.valueList != null) {
            copy.valueList = new ArrayList(this.valueList);
        }
        return copy;
    }
    
    public Object getFromVal() {
        return this.fromVal;
    }
    
    public void setFromVal(final Object fromVal) {
        this.avType = avTypes.RANGE;
        this.fromVal = fromVal;
    }
    
    public Object getToVal() {
        return this.toVal;
    }
    
    public void setToVal(final Object toVal) {
        this.avType = avTypes.RANGE;
        this.toVal = toVal;
    }
    
    void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    void setDataType(final String dataType) {
        this.dataType = dataType;
    }
    
    void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
    
    public List getValueList() {
        return this.valueList;
    }
    
    public void addValue(final Object value) {
        if (this.avType != avTypes.UNASSIGNED && this.avType != avTypes.VALUES) {
            throw new IllegalArgumentException("This AllowedValues object is of type [" + this.avType.name() + "] and hence addValue() method cannot be used on this object.");
        }
        if (value == null) {
            throw new IllegalArgumentException("null cannot be an allowed value.");
        }
        if (this.valueList == null) {
            this.valueList = new ArrayList();
            this.avType = avTypes.VALUES;
        }
        this.valueList.add(value);
        this.isValid = false;
    }
    
    static void validateValueDataType(final Object value, final String dataType) {
        if (DataTypeUtil.isUDT(dataType)) {
            DataTypeManager.getDataTypeDefinition(dataType).getMeta().validate(value);
        }
        else {
            try {
                MetaDataUtil.validate(value, dataType);
            }
            catch (final MetaDataException e) {
                throw new IllegalArgumentException("The given value \"" + value.toString() + "\" is an instance of " + value.getClass().getName() + " and is not an value of \"" + dataType + "\" dataType", e);
            }
        }
    }
    
    void init() {
        if (this.dataType != null) {
            if (this.valueList != null) {
                for (int i = 0; i < this.valueList.size(); ++i) {
                    if ((this.dataType.equals("CHAR") || this.dataType.equals("NCHAR")) && !(this.valueList.get(i) instanceof byte[]) && !(this.valueList.get(i) instanceof ByteArrayInputStream)) {
                        this.valueList.set(i, this.valueList.get(i).toString().toLowerCase(Locale.ENGLISH));
                    }
                    else if (this.valueList.get(i) instanceof String) {
                        try {
                            this.valueList.set(i, MetaDataUtil.convert(this.valueList.get(i), this.dataType));
                        }
                        catch (final MetaDataException mde) {
                            throw new IllegalArgumentException(mde.getMessage());
                        }
                    }
                }
            }
            else if (this.fromVal != null || this.toVal != null) {
                if (this.fromVal != null && this.fromVal instanceof String) {
                    try {
                        this.fromVal = MetaDataUtil.convert((String)this.fromVal, this.dataType);
                    }
                    catch (final MetaDataException mde2) {
                        throw new IllegalArgumentException(mde2.getMessage());
                    }
                }
                if (this.toVal != null && this.toVal instanceof String) {
                    try {
                        this.toVal = MetaDataUtil.convert((String)this.toVal, this.dataType);
                    }
                    catch (final MetaDataException mde2) {
                        throw new IllegalArgumentException(mde2.getMessage());
                    }
                }
            }
        }
    }
    
    void validate() {
        if ((this.valueList != null && (this.fromVal != null || this.toVal != null)) || (this.valueList != null && this.pattern != null) || ((this.fromVal != null || this.toVal != null) && this.pattern != null)) {
            throw new IllegalArgumentException("AllowedValues object cannot have valueList, pattern and from or to values together for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
        }
        if (this.dataType != null) {
            try {
                if (this.dataType.equals("BLOB") || this.dataType.equals("SBLOB")) {
                    throw new IllegalArgumentException("Allowed-values not allowed for BLOB Types");
                }
                if (DataTypeUtil.isUDT(this.dataType)) {
                    DataTypeManager.getDataTypeDefinition(this.dataType).getMeta().validateAllowedValues(this);
                }
                else {
                    if (DataTypeUtil.isEDT(this.dataType)) {
                        throw new IllegalArgumentException(this.dataType + " is not supported for AllowedValues");
                    }
                    if (this.valueList != null) {
                        for (int i = 0; i < this.valueList.size(); ++i) {
                            validateValueDataType(this.valueList.get(i), this.dataType);
                            if (this.dataType.equals("CHAR") || this.dataType.equals("NCHAR") || this.dataType.equals("SCHAR")) {
                                final String value = this.valueList.get(i).toString();
                                if (this.maxSize > 0 && value.length() > this.maxSize) {
                                    throw new IllegalArgumentException("The size of the value \"" + value + "\" in AllowedValues is greater than the max-size(" + this.maxSize + ") of the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
                                }
                            }
                        }
                        for (int i = 0; i < this.valueList.size(); ++i) {
                            if (this.dataType.equals("CHAR") || this.dataType.equals("NCHAR")) {
                                this.valueList.set(i, String.valueOf(this.valueList.get(i)).toLowerCase(Locale.ENGLISH));
                            }
                            else if (this.valueList.get(i) instanceof String) {
                                this.valueList.set(i, MetaDataUtil.convert(String.valueOf(this.valueList.get(i)), this.dataType));
                            }
                        }
                    }
                    else if (this.fromVal != null || this.toVal != null) {
                        if (this.fromVal != null) {
                            validateValueDataType(this.fromVal, this.dataType);
                            if (this.fromVal instanceof String) {
                                this.fromVal = MetaDataUtil.convert((String)this.fromVal, this.dataType);
                            }
                        }
                        if (this.toVal != null) {
                            validateValueDataType(this.toVal, this.dataType);
                            if (this.toVal instanceof String) {
                                this.toVal = MetaDataUtil.convert((String)this.toVal, this.dataType);
                            }
                        }
                        if (!this.dataType.equals("FLOAT") && !this.dataType.equals("INTEGER") && !this.dataType.equals("TINYINT") && !this.dataType.equals("DOUBLE") && !this.dataType.equals("BIGINT") && !this.dataType.equals("DATE") && !this.dataType.equals("DATETIME") && !this.dataType.equals("TIME") && !this.dataType.equals("TIMESTAMP") && !this.dataType.equals("DECIMAL")) {
                            throw new IllegalArgumentException("The datatype \"" + this.dataType + "\" specified for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\" cannot have allowed values in a range");
                        }
                        if (this.fromVal != null && this.toVal != null) {
                            final Comparable from = (Comparable)this.fromVal;
                            if (from.compareTo(this.toVal) > 0) {
                                throw new IllegalArgumentException("The Fromvalue \"" + this.fromVal.toString() + "\" is greater than Tovalue \"" + this.toVal.toString() + "\" for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
                            }
                        }
                    }
                }
                return;
            }
            catch (final MetaDataException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            throw new IllegalArgumentException("Set the dataType and then set AllowedValues");
        }
        throw new IllegalArgumentException("Set the dataType and then set AllowedValues");
    }
    
    public void validateValue(final Object value) {
        if (value != null) {
            if (DataTypeUtil.isUDT(this.dataType)) {
                DataTypeManager.getDataTypeDefinition(this.dataType).getMeta().validateValueForAllowedValues(this, value);
            }
            else {
                validateValueDataType(value, this.dataType);
                Object convertedValue = value;
                try {
                    convertedValue = MetaDataUtil.convert(String.valueOf(convertedValue), this.dataType);
                }
                catch (final MetaDataException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
                if (this.valueList != null) {
                    if (this.dataType.equals("CHAR") || this.dataType.equals("NCHAR")) {
                        if (!this.valueList.contains(convertedValue.toString().toLowerCase(Locale.ENGLISH))) {
                            throw new IllegalArgumentException("The given value \"" + value + "\" is not there in allowed values list for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
                        }
                    }
                    else if (this.dataType.equals("DECIMAL")) {
                        final BigDecimal bdval = (BigDecimal)convertedValue;
                        boolean valueFound = false;
                        for (final Object valueInList : this.valueList) {
                            if (((BigDecimal)valueInList).compareTo(bdval) == 0) {
                                valueFound = true;
                                break;
                            }
                        }
                        if (!valueFound) {
                            throw new IllegalArgumentException("The given value \"" + value + "\" is not there in allowed values list for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
                        }
                    }
                    else if (!this.valueList.contains(convertedValue)) {
                        throw new IllegalArgumentException("The given value \"" + value + "\" is not there in allowed values list for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
                    }
                }
                else if (this.pattern != null) {
                    final Matcher match = this.pattern.matcher(convertedValue.toString());
                    if (!match.matches()) {
                        throw new IllegalArgumentException("The given value \"" + value + "\" does not match with the valid pattern \"" + this.pattern + "\" specified for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
                    }
                }
                else {
                    try {
                        final Comparable givenValue = (Comparable)convertedValue;
                        if (this.fromVal != null) {
                            final Comparable fromval = (Comparable)MetaDataUtil.convert(String.valueOf(this.fromVal), this.dataType);
                            if (fromval.compareTo(givenValue) > 0) {
                                throw new IllegalArgumentException("The given value \"" + value + "\" is lesser than the from-value \"" + this.fromVal + "\" in Allowed values range for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
                            }
                        }
                        if (this.toVal != null) {
                            final Comparable toval = (Comparable)MetaDataUtil.convert(String.valueOf(this.toVal), this.dataType);
                            if (toval.compareTo(givenValue) < 0) {
                                throw new IllegalArgumentException("The given value \"" + value + "\" is greater than the to-value \"" + this.toVal + "\" in Allowed values range for the column \"" + this.columnName + "\" of table \"" + this.tableName + "\"");
                            }
                        }
                    }
                    catch (final MetaDataException mde) {
                        throw new IllegalArgumentException(mde.getMessage(), mde);
                    }
                }
            }
        }
    }
    
    public void validateInput(final Object value) {
        this.validateValue(value);
    }
    
    public String getPattern() {
        return this.patternStr;
    }
    
    public void setPattern(final String patternStr) {
        if (patternStr == null || patternStr.trim().length() == 0) {
            throw new IllegalArgumentException("AllowedPattern cannot be null/empty");
        }
        if (this.avType == avTypes.UNASSIGNED || this.avType == avTypes.PATTERN) {
            this.avType = avTypes.PATTERN;
            this.patternStr = patternStr;
            this.pattern = Pattern.compile(this.patternStr);
            this.isValid = false;
            return;
        }
        throw new IllegalArgumentException("This AllowedValues object is of type [" + this.avType.name() + "] and hence setPattern() method cannot be used on this object.");
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(40);
        if (this.valueList != null) {
            for (int i = 0; i < this.valueList.size(); ++i) {
                sb.append("\n\t\t\t\t<value" + i + ">");
                sb.append(this.valueList.get(i));
                sb.append("</value" + i + ">");
            }
        }
        if (this.pattern != null) {
            sb.append("\n\t\t\t\t<pattern>" + this.pattern + "</pattern>");
        }
        if (this.fromVal != null && this.toVal != null) {
            sb.append("\n\t\t\t\t<from>");
            sb.append(this.fromVal);
            sb.append("</from>\n\t\t\t\t<to>");
            sb.append(this.toVal);
            sb.append("</to>");
        }
        sb.append("\n");
        return sb.toString();
    }
    
    public int getMaxSize() {
        return this.maxSize;
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        if (this.tableName != null && this.columnName != null) {
            jsonObject.put("tablename", (Object)this.tableName);
            jsonObject.put("columnname", (Object)this.columnName);
        }
        if (this.valueList != null) {
            final JSONArray jsonArray = new JSONArray();
            for (final Object value : this.valueList) {
                jsonArray.put(value);
            }
            jsonObject.put("valuelist", (Object)jsonArray);
        }
        if (this.pattern != null) {
            jsonObject.put("pattern", (Object)this.pattern);
        }
        if (this.fromVal != null) {
            jsonObject.put("fromval", this.fromVal);
        }
        if (this.toVal != null) {
            jsonObject.put("toval", this.toVal);
        }
        return jsonObject;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return this == obj;
        }
        final AllowedValues alv = (AllowedValues)obj;
        if (this.valueList != null) {
            return this.valueList.equals(alv.valueList);
        }
        if (this.fromVal != null || this.toVal != null) {
            boolean isEqual = true;
            if (this.fromVal != null) {
                isEqual &= this.fromVal.equals(alv.fromVal);
            }
            if (this.toVal != null) {
                isEqual &= this.toVal.equals(alv.toVal);
            }
            return isEqual;
        }
        return this.pattern == null || this.pattern.equals(alv.pattern);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    private enum avTypes
    {
        UNASSIGNED, 
        VALUES, 
        PATTERN, 
        RANGE;
    }
}
