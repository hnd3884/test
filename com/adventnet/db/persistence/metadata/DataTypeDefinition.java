package com.adventnet.db.persistence.metadata;

import java.sql.Connection;
import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Locale;
import java.util.Map;
import com.adventnet.db.adapter.DTTransformer;
import com.adventnet.db.adapter.DTResultSetAdapter;
import com.adventnet.db.adapter.DTSQLGenerator;
import com.adventnet.db.adapter.DTAdapter;
import com.adventnet.persistence.DataTypeValidator;

public class DataTypeDefinition
{
    private String dataType;
    private String baseType;
    private AllowedValues allowedValues;
    private Object defaultValue;
    private int maxLength;
    private int precision;
    private DataTypeValidator validator;
    private DataTypeMetaInfo meta;
    private DTAdapter[] dtAdapters;
    private DTSQLGenerator[] dtSQLGenerators;
    private DTResultSetAdapter[] dtResultSetAdapters;
    private DTTransformer[] dtTransformer;
    private boolean isValidated;
    Map<String, Integer> databaseVsIndex;
    
    public DataTypeDefinition(final String dataType, final String baseType, final int maxLength, final int precision, final AllowedValues allowedValues, final Object defaultValue) {
        this.maxLength = 0;
        this.precision = 0;
        this.validator = null;
        this.meta = null;
        this.dtAdapters = null;
        this.dtSQLGenerators = null;
        this.dtResultSetAdapters = null;
        this.dtTransformer = null;
        this.isValidated = false;
        this.databaseVsIndex = null;
        if (dataType == null || dataType.equals("")) {
            throw new IllegalArgumentException("Type Name cannot be null");
        }
        if (baseType == null || baseType.equals("")) {
            throw new IllegalArgumentException("Base Type cannot be null");
        }
        this.dataType = dataType.toUpperCase(Locale.ENGLISH);
        this.baseType = baseType.toUpperCase(Locale.ENGLISH);
        this.maxLength = maxLength;
        this.precision = precision;
        this.allowedValues = allowedValues;
        MetaDataUtil.getJavaSQLType(this.baseType);
        if (this.baseType.equals("CHAR") || this.baseType.equals("NCHAR") || this.baseType.equals("SCHAR")) {
            this.maxLength = ((this.maxLength == 0) ? 50 : this.maxLength);
        }
        else if (this.baseType.equals("DECIMAL")) {
            this.maxLength = ((this.maxLength == 0) ? 15 : this.maxLength);
            this.precision = ((this.precision == 0) ? 4 : this.precision);
        }
        if (this.allowedValues != null) {
            this.allowedValues.setMaxSize(this.maxLength);
            this.allowedValues.setDataType(this.baseType);
            this.allowedValues.init();
        }
        this.defaultValue = defaultValue;
    }
    
    public DataTypeDefinition(final String dataType, final DataTypeMetaInfo meta) {
        this.maxLength = 0;
        this.precision = 0;
        this.validator = null;
        this.meta = null;
        this.dtAdapters = null;
        this.dtSQLGenerators = null;
        this.dtResultSetAdapters = null;
        this.dtTransformer = null;
        this.isValidated = false;
        this.databaseVsIndex = null;
        if (dataType == null || dataType.equals("")) {
            throw new IllegalArgumentException("Type Name cannot be null");
        }
        this.dataType = dataType.toUpperCase(Locale.ENGLISH);
        this.meta = meta;
        final List<String> databases = new ArrayList<String>();
        databases.add("mysql");
        databases.add("postgres");
        databases.add("mssql");
        this.dtAdapters = new DTAdapter[databases.size()];
        this.dtSQLGenerators = new DTSQLGenerator[databases.size()];
        this.dtResultSetAdapters = new DTResultSetAdapter[databases.size()];
        this.dtTransformer = new DTTransformer[databases.size()];
        this.databaseVsIndex = new HashMap<String, Integer>();
        int index = 0;
        for (final String database : databases) {
            this.databaseVsIndex.put(database, index++);
        }
    }
    
    public String getDataType() {
        return this.dataType;
    }
    
    public String getBaseType() {
        return this.baseType;
    }
    
    public AllowedValues getAllowedValues() {
        try {
            if (this.allowedValues == null) {
                return null;
            }
            return (AllowedValues)this.allowedValues.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
    
    public int getMaxLength() {
        return this.maxLength;
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    public void validate() {
        if (getBasicDataTypes().contains(this.dataType)) {
            throw new IllegalArgumentException("UDT Data type cannot be a basic datatype supported by framework");
        }
        if (this.meta != null) {
            for (final String database : this.databaseVsIndex.keySet()) {
                if (this.getDTAdapter(database) != null) {
                    if (this.getDTSQLGenerator(database) == null) {
                        throw new IllegalArgumentException("DTSQLGenerator not defined for the type " + this.dataType + " for database " + database);
                    }
                    if (this.getDTResultSetAdapter(database) == null) {
                        throw new IllegalArgumentException("DTResultSetAdapter not defined for the type " + this.dataType + " for database " + database);
                    }
                    if (this.getDTTransformer(database) == null) {
                        throw new IllegalArgumentException("DTTransformer not defined for the type :: " + this.dataType + " for database " + database);
                    }
                    continue;
                }
            }
            for (final String database : PersistenceInitializer.getDatabases()) {
                if (this.getDTAdapter(database) == null) {
                    throw new IllegalArgumentException(this.dataType + " not defined for database :: " + database);
                }
            }
        }
        else {
            if (this.baseType != null && !getBasicDataTypes().contains(this.baseType)) {
                throw new IllegalArgumentException("BaseType can be a datatype supported by framework");
            }
            if (this.maxLength < -1) {
                throw new IllegalArgumentException("MaxLength can be -1 and above");
            }
            if (this.precision < 0) {
                throw new IllegalArgumentException("Precision can be 0 and above");
            }
            if (this.defaultValue != null) {
                if (this.baseType.equals("BLOB") || this.baseType.equals("SBLOB") || this.baseType.equals("SCHAR")) {
                    throw new IllegalArgumentException("DataType :: " + this.dataType + " with BaseType :: " + this.baseType + " can't have a default value");
                }
                try {
                    MetaDataUtil.validate(this.defaultValue, this.baseType);
                }
                catch (final MetaDataException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
            if (this.allowedValues != null) {
                this.validateAllowedValues();
                if (this.defaultValue != null) {
                    this.allowedValues.validateValue(this.defaultValue);
                }
            }
        }
        this.isValidated = true;
    }
    
    public void validateAllowedValues() {
        if (this.allowedValues != null) {
            this.allowedValues.validate();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<dataType>");
        sb.append(this.dataType);
        sb.append("</dataType>\n");
        if (this.baseType != null) {
            sb.append("<baseType>");
            sb.append(this.baseType);
            sb.append("</baseType>\n");
            sb.append("<maxLength>");
            sb.append(this.maxLength);
            sb.append("</maxLength>\n");
            sb.append("<precision>");
            sb.append(this.precision);
            sb.append("</precision>\n");
            sb.append("<allowedValues>");
            sb.append(this.allowedValues);
            sb.append("</allowedValues>\n");
            sb.append("<defaultValue>");
            sb.append(this.defaultValue);
            sb.append("</defaultValue>");
        }
        else {
            sb.append("<meta>");
            sb.append(this.meta.getClass().getCanonicalName());
            sb.append("</meta>\n");
            for (final String database : this.databaseVsIndex.keySet()) {
                if (this.getDTAdapter(database) != null) {
                    sb.append("<database name=\"");
                    sb.append(database);
                    sb.append("\">\n");
                    sb.append("\t\t<dtadapter>");
                    sb.append(this.getDTAdapter(database).getClass().getCanonicalName());
                    sb.append("</dtadapter>\n");
                    sb.append("\t\t<dtsqlgenerator>");
                    sb.append(this.getDTSQLGenerator(database).getClass().getCanonicalName());
                    sb.append("</dtsqlgenerator>\n");
                    sb.append("\t\t<dtresultsetadapter>");
                    sb.append(this.getDTResultSetAdapter(database).getClass().getCanonicalName());
                    sb.append("</dtresultsetadapter>\n");
                    sb.append("\t\t<dttransformer>");
                    sb.append(this.getDTTransformer(database).getClass().getCanonicalName());
                    sb.append("</dttransformer>\n");
                    sb.append("</database>\n");
                }
            }
        }
        return sb.toString();
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("datatype", (Object)this.dataType);
        if (this.baseType != null) {
            jsonObject.put("baseType", (Object)this.baseType);
            jsonObject.put("maxlength", this.maxLength);
            jsonObject.put("precision", this.precision);
            jsonObject.put("allowedvalues", (Object)this.allowedValues.toJSON());
            jsonObject.put("defaultvalue", this.defaultValue);
        }
        else {
            jsonObject.put("meta", (Object)this.meta.getClass().getCanonicalName());
            for (final String database : this.databaseVsIndex.keySet()) {
                if (this.getDTAdapter(database) != null) {
                    jsonObject.put(database + ".dtadapter", (Object)this.getDTAdapter(database).getClass().getCanonicalName());
                    jsonObject.put(database + ".dtsqlgenerator", (Object)this.getDTSQLGenerator(database).getClass().getCanonicalName());
                    jsonObject.put(database + ".dtresultsetadapter", (Object)this.getDTResultSetAdapter(database).getClass().getCanonicalName());
                    jsonObject.put(database + ".dttransformer", (Object)this.getDTTransformer(database).getClass().getCanonicalName());
                }
            }
        }
        return jsonObject;
    }
    
    public void setValidator(final DataTypeValidator validator) {
        this.validator = validator;
    }
    
    public DataTypeValidator getValidator() {
        return this.validator;
    }
    
    public DTAdapter getDTAdapter(final String database) {
        if (this.getIndex(database) != -1) {
            return this.dtAdapters[this.getIndex(database)];
        }
        return null;
    }
    
    public void setDTAdapter(final DTAdapter dtAdapter, final String database) {
        if (this.getIndex(database) != -1) {
            this.dtAdapters[this.getIndex(database)] = dtAdapter;
        }
    }
    
    public DTSQLGenerator getDTSQLGenerator(final String database) {
        if (this.getIndex(database) != -1) {
            return this.dtSQLGenerators[this.getIndex(database)];
        }
        return null;
    }
    
    public void setDTSQLGenerator(final DTSQLGenerator dtSQLGenerator, final String database) {
        if (this.getIndex(database) != -1) {
            this.dtSQLGenerators[this.getIndex(database)] = dtSQLGenerator;
        }
    }
    
    public DTResultSetAdapter getDTResultSetAdapter(final String database) {
        if (this.getIndex(database) != -1) {
            return this.dtResultSetAdapters[this.getIndex(database)];
        }
        return null;
    }
    
    public void setDTResultSetAdapter(final DTResultSetAdapter dtResultSetAdapter, final String database) {
        if (this.getIndex(database) != -1) {
            this.dtResultSetAdapters[this.getIndex(database)] = dtResultSetAdapter;
        }
    }
    
    public DTTransformer getDTTransformer(final String database) {
        if (this.getIndex(database) != -1) {
            return this.dtTransformer[this.getIndex(database)];
        }
        return null;
    }
    
    public void setDTTransformer(final DTTransformer transformer, final String database) {
        if (this.getIndex(database) != -1) {
            this.dtTransformer[this.getIndex(database)] = transformer;
        }
    }
    
    public DataTypeMetaInfo getMeta() {
        return this.meta;
    }
    
    private int getIndex(final String database) {
        if (this.databaseVsIndex.get(database) != null) {
            return this.databaseVsIndex.get(database);
        }
        return -1;
    }
    
    public void validateDTAdapters(final Connection c, final String dbType) {
        if (this.dtAdapters != null) {
            this.getDTAdapter(dbType).validateVersion(c);
        }
    }
    
    public static List<String> getBasicDataTypes() {
        final List<String> dataTypes = new ArrayList<String>();
        dataTypes.add("CHAR");
        dataTypes.add("INTEGER");
        dataTypes.add("BIGINT");
        dataTypes.add("BOOLEAN");
        dataTypes.add("DATE");
        dataTypes.add("DATETIME");
        dataTypes.add("TIME");
        dataTypes.add("TIMESTAMP");
        dataTypes.add("BLOB");
        dataTypes.add("FLOAT");
        dataTypes.add("DOUBLE");
        dataTypes.add("TINYINT");
        dataTypes.add("DECIMAL");
        dataTypes.add("SCHAR");
        dataTypes.add("SBLOB");
        dataTypes.add("NCHAR");
        return dataTypes;
    }
    
    public boolean isValidated() {
        return this.isValidated;
    }
}
