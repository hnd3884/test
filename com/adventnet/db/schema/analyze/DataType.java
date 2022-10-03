package com.adventnet.db.schema.analyze;

import java.sql.Timestamp;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.logging.Logger;

public class DataType
{
    private static final Logger LOGGER;
    
    public static Type getDBSpecificDataType(final SchemaAnalyzerUtil.DBType dbtype, final String dataType) {
        switch (dbtype) {
            case MYSQL: {
                for (final Mysql myCons : Mysql.values()) {
                    if (myCons.toString().equalsIgnoreCase(dataType)) {
                        return myCons;
                    }
                }
                return Mysql.UNKNOWN;
            }
            case MSSQL: {
                for (final Mssql msCons : Mssql.values()) {
                    if (msCons.toString().equalsIgnoreCase(dataType)) {
                        return msCons;
                    }
                }
                return Mssql.UNKNOWN;
            }
            case FIREBIRD: {
                for (final Firebird fbCons : Firebird.values()) {
                    if (fbCons.toString().equalsIgnoreCase(dataType)) {
                        return fbCons;
                    }
                }
                return Firebird.UNKNOWN;
            }
            case POSTGRES: {
                for (final Postgres pgCons : Postgres.values()) {
                    if (pgCons.toString().equalsIgnoreCase(dataType)) {
                        return pgCons;
                    }
                }
                return Postgres.UNKNOWN;
            }
            default: {
                throw new IllegalArgumentException("Unknown DBType [" + dbtype + "] specified.");
            }
        }
    }
    
    protected static String getDefaultValueFromMetaDataString(final String metaString, final String dataType) {
        if (metaString != null) {
            final String replacedStr = metaString.replace("'", "").replace("(", "").replace(")", "");
            return dataType.equals("BOOLEAN") ? Boolean.toString(replacedStr.equals("1")) : replacedStr;
        }
        return metaString;
    }
    
    protected static String getEquivalentFKConstraintString(final String constraintStr) {
        return constraintStr;
    }
    
    public static String getFKConstraintString(final String constraintStr, final SchemaAnalyzerUtil.DBType dbType) {
        switch (dbType) {
            case MYSQL: {
                return Mysql.UNKNOWN.getEquivalentFKConstraintString(constraintStr);
            }
            case MSSQL: {
                return Mssql.UNKNOWN.getEquivalentFKConstraintString(constraintStr);
            }
            case FIREBIRD: {
                return Firebird.UNKNOWN.getEquivalentFKConstraintString(constraintStr);
            }
            case POSTGRES: {
                return Postgres.UNKNOWN.getEquivalentFKConstraintString(constraintStr);
            }
            default: {
                throw new IllegalArgumentException("Unknown DBType [" + dbType + "] specified.");
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DataType.class.getName());
    }
    
    public enum Mysql implements Type
    {
        INT(new Mssql[] { Mssql.INT }, new Postgres[] { Postgres.INT4 }, new Firebird[] { Firebird.INTEGER }), 
        BIGINT(new Mssql[] { Mssql.BIGINT }, new Postgres[] { Postgres.INT8 }, new Firebird[] { Firebird.BIGINT }), 
        DOUBLE(new Mssql[] { Mssql.FLOAT }, new Postgres[] { Postgres.FLOAT8 }, new Firebird[] { Firebird.DOUBLE_PRECISION }), 
        FLOAT(new Mssql[] { Mssql.REAL, Mssql.FLOAT }, new Postgres[] { Postgres.FLOAT4 }, new Firebird[] { Firebird.FLOAT }), 
        DECIMAL(new Mssql[] { Mssql.DECIMAL }, new Postgres[] { Postgres.NUMERIC }, new Firebird[] { Firebird.DECIMAL }), 
        TINYINT(new Mssql[] { Mssql.TINYINT, Mssql.BIT }, new Postgres[] { Postgres.SMALLINT, Postgres.BOOL, Postgres.INT2 }, new Firebird[] { Firebird.SMALLINT, Firebird.INTEGER }), 
        BIT(new Mssql[] { Mssql.TINYINT, Mssql.BIT }, new Postgres[] { Postgres.SMALLINT, Postgres.BOOL, Postgres.INT2 }, new Firebird[] { Firebird.SMALLINT, Firebird.INTEGER }), 
        VARCHAR(new Mssql[] { Mssql.VARCHAR, Mssql.NVARCHAR }, new Postgres[] { Postgres.CITEXT }, new Firebird[] { Firebird.VARCHAR }), 
        TEXT(new Mssql[] { Mssql.VARCHAR, Mssql.NVARCHAR, Mssql.TEXT, Mssql.NTEXT }, new Postgres[] { Postgres.CITEXT }, new Firebird[] { Firebird.VARCHAR, Firebird.BLOB }), 
        LONGTEXT(new Mssql[] { Mssql.VARCHAR, Mssql.TEXT }, new Postgres[] { Postgres.CITEXT }, new Firebird[] { Firebird.BLOB }), 
        BLOB(new Mssql[] { Mssql.VARBINARY, Mssql.IMAGE }, new Postgres[] { Postgres.BYTEA }, new Firebird[] { Firebird.VARCHAR, Firebird.BLOB }), 
        LONGBLOB(new Mssql[] { Mssql.VARBINARY, Mssql.IMAGE }, new Postgres[] { Postgres.BYTEA }, new Firebird[] { Firebird.BLOB }), 
        DATE(new Mssql[] { Mssql.DATETIME }, new Postgres[] { Postgres.DATE }, new Firebird[] { Firebird.DATE }), 
        TIME(new Mssql[] { Mssql.DATETIME }, new Postgres[] { Postgres.TIMESTAMP, Postgres.TIME }, new Firebird[] { Firebird.TIMESTAMP }), 
        TIMESTAMP(new Mssql[] { Mssql.DATETIME }, new Postgres[] { Postgres.TIMESTAMP }, new Firebird[] { Firebird.TIMESTAMP }), 
        DATETIME(new Mssql[] { Mssql.DATETIME }, new Postgres[] { Postgres.TIMESTAMP }, new Firebird[] { Firebird.TIMESTAMP }), 
        BINARY(new Mssql[] { Mssql.UNIQUEIDENTIFIER }, new Postgres[] { Postgres.UUID }, new Firebird[] { Firebird.VARCHAR }), 
        UNKNOWN(new Mssql[0], new Postgres[0], new Firebird[0]);
        
        Mssql[] mssqlTypes;
        Postgres[] postgresType;
        Firebird[] firebirdTypes;
        
        private Mysql(final Mssql[] msTypes, final Postgres[] pgTypes, final Firebird[] fbTypes) {
            this.mssqlTypes = msTypes;
            this.postgresType = pgTypes;
            this.firebirdTypes = fbTypes;
        }
        
        @Override
        public boolean contains(final SchemaAnalyzerUtil.DBType dbName, final Type dataTypeString) {
            if (Mysql.UNKNOWN.toString().equals(dataTypeString)) {
                DataType.LOGGER.info("Unknown datatype specified " + dbName + "." + dataTypeString);
                throw new IllegalArgumentException("Unknown datatype specified " + dbName + "." + dataTypeString);
            }
            switch (dbName) {
                case MSSQL: {
                    return this.isContains(this.mssqlTypes, dataTypeString);
                }
                case POSTGRES: {
                    return this.isContains(this.postgresType, dataTypeString);
                }
                case FIREBIRD: {
                    return this.isContains(this.firebirdTypes, dataTypeString);
                }
                default: {
                    return false;
                }
            }
        }
        
        private boolean isContains(final Type[] dataTypes, final Type dataType) {
            for (final Type type : dataTypes) {
                if (type.toString().equalsIgnoreCase(dataType.toString())) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public Object getDefaultValue(final String valueString) {
            Object defValue = valueString;
            if (this == Mysql.DATE) {
                try {
                    defValue = ("0000-00-00".equals(valueString) ? null : ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, this.toString()) : valueString));
                }
                catch (final MetaDataException e) {
                    e.printStackTrace();
                }
            }
            else {
                if (this != Mysql.TIMESTAMP && this != Mysql.TIME && this != Mysql.DATETIME && this != Mysql.DOUBLE) {
                    if (this != Mysql.FLOAT) {
                        if (this == Mysql.DECIMAL) {
                            defValue = ((valueString != null && !valueString.equals("")) ? Double.valueOf(Double.parseDouble(valueString)) : valueString);
                            return (defValue != null && defValue.equals("")) ? null : defValue;
                        }
                        return (defValue != null && defValue.equals("")) ? null : defValue;
                    }
                }
                try {
                    defValue = ("0000-00-00 00:00:00".equals(valueString) ? null : ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, this.toString()) : valueString));
                }
                catch (final MetaDataException e) {
                    e.printStackTrace();
                }
            }
            return (defValue != null && defValue.equals("")) ? null : defValue;
        }
        
        @Override
        public String getDefaultValueFromMetaDataString(final String metaString, final String dataType) {
            final String defVal = DataType.getDefaultValueFromMetaDataString(metaString, dataType);
            return (defVal == null) ? null : (defVal.equalsIgnoreCase("CURRENT_TIMESTAMP") ? null : defVal);
        }
        
        @Override
        public String getEquivalentFKConstraintString(final String constraintStr) {
            return DataType.getEquivalentFKConstraintString(constraintStr);
        }
    }
    
    public enum Postgres implements Type
    {
        INT4(new Mysql[] { Mysql.INT }, new Mssql[] { Mssql.INT }, new Firebird[] { Firebird.INTEGER }), 
        INT8(new Mysql[] { Mysql.BIGINT }, new Mssql[] { Mssql.BIGINT }, new Firebird[] { Firebird.BIGINT }), 
        FLOAT8(new Mysql[] { Mysql.DOUBLE }, new Mssql[] { Mssql.FLOAT }, new Firebird[] { Firebird.DOUBLE_PRECISION }), 
        FLOAT4(new Mysql[] { Mysql.FLOAT }, new Mssql[] { Mssql.REAL, Mssql.FLOAT }, new Firebird[] { Firebird.FLOAT }), 
        NUMERIC(new Mysql[] { Mysql.DECIMAL }, new Mssql[] { Mssql.DECIMAL }, new Firebird[] { Firebird.DECIMAL }), 
        SMALLINT(new Mysql[] { Mysql.TINYINT }, new Mssql[] { Mssql.TINYINT }, new Firebird[] { Firebird.SMALLINT }), 
        INT2(new Mysql[] { Mysql.TINYINT }, new Mssql[] { Mssql.TINYINT }, new Firebird[] { Firebird.SMALLINT }), 
        BOOL(new Mysql[] { Mysql.TINYINT, Mysql.BIT }, new Mssql[] { Mssql.BIT }, new Firebird[] { Firebird.INTEGER }), 
        CITEXT(new Mysql[] { Mysql.VARCHAR, Mysql.TEXT, Mysql.LONGTEXT }, new Mssql[] { Mssql.VARCHAR, Mssql.NVARCHAR, Mssql.TEXT, Mssql.NTEXT }, new Firebird[] { Firebird.VARCHAR }), 
        UUID(new Mysql[] { Mysql.BINARY }, new Mssql[] { Mssql.UNIQUEIDENTIFIER }, new Firebird[] { Firebird.VARCHAR }), 
        BYTEA(new Mysql[] { Mysql.BLOB, Mysql.LONGBLOB }, new Mssql[] { Mssql.VARBINARY, Mssql.IMAGE }, new Firebird[] { Firebird.BLOB }), 
        DATE(new Mysql[] { Mysql.DATE }, new Mssql[] { Mssql.DATETIME }, new Firebird[] { Firebird.DATE }), 
        TIME(new Mysql[] { Mysql.TIME }, new Mssql[] { Mssql.DATETIME }, new Firebird[] { Firebird.TIME }), 
        TIMESTAMP(new Mysql[] { Mysql.TIMESTAMP, Mysql.DATETIME }, new Mssql[] { Mssql.DATETIME }, new Firebird[] { Firebird.TIMESTAMP }), 
        TEXT(new Mysql[] { Mysql.VARCHAR, Mysql.TEXT, Mysql.LONGTEXT }, new Mssql[] { Mssql.VARCHAR, Mssql.NVARCHAR, Mssql.TEXT, Mssql.NTEXT }, new Firebird[] { Firebird.VARCHAR }), 
        UNKNOWN(new Mysql[0], new Mssql[0], new Firebird[0]);
        
        Mysql[] mysqlTypes;
        Mssql[] mssqlType;
        Firebird[] firebirdTypes;
        String toString;
        
        private Postgres() {
            this.toString = null;
        }
        
        private Postgres(final Mysql[] myTypes, final Mssql[] msTypes, final Firebird[] fbType) {
            this.toString = null;
            this.mysqlTypes = myTypes;
            this.mssqlType = msTypes;
            this.firebirdTypes = fbType;
        }
        
        private Postgres(final Mysql[] myTypes, final Mssql[] msTypes, final Firebird[] fbType, final String stringRepresentation) {
            this.toString = null;
            this.mysqlTypes = myTypes;
            this.mssqlType = msTypes;
            this.firebirdTypes = fbType;
            this.toString = stringRepresentation;
        }
        
        @Override
        public boolean contains(final SchemaAnalyzerUtil.DBType dbName, final Type dataTypeString) {
            if (Postgres.UNKNOWN.toString().equals(dataTypeString)) {
                DataType.LOGGER.info("Unknown datatype specified " + dbName + "." + dataTypeString);
                throw new IllegalArgumentException("Unknown datatype specified " + dbName + "." + dataTypeString);
            }
            switch (dbName) {
                case MYSQL: {
                    return this.isContains(this.mysqlTypes, dataTypeString);
                }
                case MSSQL: {
                    return this.isContains(this.mssqlType, dataTypeString);
                }
                case FIREBIRD: {
                    return this.isContains(this.firebirdTypes, dataTypeString);
                }
                default: {
                    return false;
                }
            }
        }
        
        private boolean isContains(final Type[] dataTypes, final Type dataType) {
            for (final Type type : dataTypes) {
                if (type.toString().equalsIgnoreCase(dataType.toString()) || type == dataType) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            return (this.toString != null) ? this.toString : super.toString();
        }
        
        @Override
        public Object getDefaultValue(final String valueString) {
            Object defValue = valueString;
            if (this != Postgres.DATE) {
                if (this != Postgres.TIME) {
                    if (this == Postgres.TIMESTAMP) {
                        try {
                            if (valueString != null && valueString != "") {
                                final Timestamp ts = (Timestamp)MetaDataUtil.convert(valueString, this.toString());
                                if (ts != null) {
                                    ts.setNanos(0);
                                }
                                return ts;
                            }
                            defValue = valueString;
                        }
                        catch (final MetaDataException e) {
                            e.printStackTrace();
                        }
                        return (defValue != null && defValue.equals("")) ? null : defValue;
                    }
                    if (this == Postgres.BOOL) {
                        defValue = ((valueString != null) ? (valueString.equalsIgnoreCase("true") ? "1" : (valueString.equalsIgnoreCase("false") ? "0" : valueString)) : valueString);
                        return (defValue != null && defValue.equals("")) ? null : defValue;
                    }
                    if (this == Postgres.FLOAT4) {
                        try {
                            defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, "FLOAT") : valueString);
                        }
                        catch (final MetaDataException e) {
                            e.printStackTrace();
                        }
                        return (defValue != null && defValue.equals("")) ? null : defValue;
                    }
                    if (this == Postgres.FLOAT8) {
                        try {
                            defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, "DOUBLE") : valueString);
                        }
                        catch (final MetaDataException e) {
                            e.printStackTrace();
                        }
                        return (defValue != null && defValue.equals("")) ? null : defValue;
                    }
                    if (this == Postgres.NUMERIC) {
                        defValue = ((valueString != null) ? Double.valueOf(Double.parseDouble(valueString)) : valueString);
                        return (defValue != null && defValue.equals("")) ? null : defValue;
                    }
                    return (defValue != null && defValue.equals("")) ? null : defValue;
                }
            }
            try {
                defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, this.toString()) : valueString);
            }
            catch (final MetaDataException e) {
                e.printStackTrace();
            }
            return (defValue != null && defValue.equals("")) ? null : defValue;
        }
        
        @Override
        public String getDefaultValueFromMetaDataString(final String metaString, final String dataType) {
            if (dataType.equals("BOOLEAN") && metaString != null) {
                return Boolean.toString(Boolean.valueOf(metaString.replace("'", "").replace("(", "").replace(")", "")));
            }
            return DataType.getDefaultValueFromMetaDataString((metaString != null) ? metaString.replaceAll("::.*$", "") : metaString, dataType);
        }
        
        @Override
        public String getEquivalentFKConstraintString(final String constraintStr) {
            return DataType.getEquivalentFKConstraintString(constraintStr);
        }
    }
    
    public enum Mssql implements Type
    {
        INT(new Mysql[] { Mysql.INT }, new Postgres[] { Postgres.INT4 }, new Firebird[] { Firebird.INTEGER }), 
        BIGINT(new Mysql[] { Mysql.BIGINT }, new Postgres[] { Postgres.INT8 }, new Firebird[] { Firebird.BIGINT }), 
        FLOAT(new Mysql[] { Mysql.DOUBLE }, new Postgres[] { Postgres.FLOAT8, Postgres.FLOAT4 }, new Firebird[] { Firebird.DOUBLE_PRECISION }), 
        REAL(new Mysql[] { Mysql.FLOAT }, new Postgres[] { Postgres.FLOAT4 }, new Firebird[] { Firebird.FLOAT }), 
        DECIMAL(new Mysql[] { Mysql.DECIMAL }, new Postgres[] { Postgres.NUMERIC }, new Firebird[] { Firebird.DECIMAL }), 
        TINYINT(new Mysql[] { Mysql.TINYINT }, new Postgres[] { Postgres.SMALLINT, Postgres.INT2 }, new Firebird[] { Firebird.SMALLINT }), 
        BIT(new Mysql[] { Mysql.TINYINT }, new Postgres[] { Postgres.BOOL, Postgres.INT2, Postgres.SMALLINT }, new Firebird[] { Firebird.INTEGER }), 
        VARCHAR(new Mysql[] { Mysql.VARCHAR, Mysql.TEXT, Mysql.LONGTEXT }, new Postgres[] { Postgres.CITEXT, Postgres.TEXT }, new Firebird[] { Firebird.VARCHAR }), 
        UNIQUEIDENTIFIER(new Mysql[] { Mysql.BINARY }, new Postgres[] { Postgres.UUID }, new Firebird[] { Firebird.VARCHAR }), 
        NVARCHAR(new Mysql[] { Mysql.VARCHAR, Mysql.TEXT, Mysql.LONGTEXT }, new Postgres[] { Postgres.CITEXT }, new Firebird[] { Firebird.VARCHAR }), 
        VARBINARY(new Mysql[] { Mysql.BLOB, Mysql.LONGBLOB }, new Postgres[] { Postgres.BYTEA }, new Firebird[] { Firebird.BLOB }), 
        IMAGE(new Mysql[] { Mysql.BLOB, Mysql.LONGBLOB }, new Postgres[] { Postgres.BYTEA }, new Firebird[] { Firebird.BLOB }), 
        DATETIME(new Mysql[] { Mysql.DATE, Mysql.TIME, Mysql.TIMESTAMP, Mysql.DATETIME }, new Postgres[] { Postgres.DATE, Postgres.TIME, Postgres.TIMESTAMP }, new Firebird[] { Firebird.DATE, Firebird.TIME, Firebird.TIMESTAMP }), 
        TEXT(new Mysql[] { Mysql.TEXT, Mysql.LONGTEXT }, new Postgres[] { Postgres.CITEXT }, new Firebird[] { Firebird.VARCHAR }), 
        NTEXT(new Mysql[] { Mysql.TEXT, Mysql.LONGTEXT }, new Postgres[] { Postgres.CITEXT }, new Firebird[] { Firebird.VARCHAR }), 
        UNKNOWN(new Mysql[0], new Postgres[0], new Firebird[0]);
        
        Mysql[] mysqlTypes;
        Postgres[] pgsqlType;
        Firebird[] firebirdTypes;
        String toString;
        
        private Mssql() {
            this.toString = null;
        }
        
        private Mssql(final Mysql[] myTypes, final Postgres[] pgTypes, final Firebird[] fbType) {
            this.toString = null;
            this.mysqlTypes = myTypes;
            this.pgsqlType = pgTypes;
            this.firebirdTypes = fbType;
        }
        
        private Mssql(final Mysql[] myTypes, final Postgres[] pgTypes, final Firebird[] fbType, final String stringRepresentation) {
            this.toString = null;
            this.mysqlTypes = myTypes;
            this.pgsqlType = pgTypes;
            this.firebirdTypes = fbType;
            this.toString = stringRepresentation;
        }
        
        @Override
        public boolean contains(final SchemaAnalyzerUtil.DBType dbName, final Type dataTypeString) {
            if (Mssql.UNKNOWN.toString().equals(dataTypeString)) {
                DataType.LOGGER.info("Unknown datatype specified " + dbName + "." + dataTypeString);
                throw new IllegalArgumentException("Unknown datatype specified " + dbName + "." + dataTypeString);
            }
            switch (dbName) {
                case MYSQL: {
                    return this.isContains(this.mysqlTypes, dataTypeString);
                }
                case POSTGRES: {
                    return this.isContains(this.pgsqlType, dataTypeString);
                }
                case FIREBIRD: {
                    return this.isContains(this.firebirdTypes, dataTypeString);
                }
                default: {
                    return false;
                }
            }
        }
        
        private boolean isContains(final Type[] dataTypes, final Type dataType) {
            for (final Type type : dataTypes) {
                if (type.toString().equalsIgnoreCase(dataType.toString()) || type == dataType) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            return (this.toString != null) ? this.toString : super.toString();
        }
        
        @Override
        public Object getDefaultValue(final String valueString) {
            Object defValue = valueString;
            if (this == Mssql.DECIMAL) {
                defValue = ((valueString != null) ? Double.valueOf(Double.parseDouble(valueString)) : valueString);
            }
            else if (this == Mssql.DATETIME) {
                try {
                    defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, this.toString()) : valueString);
                }
                catch (final MetaDataException e) {
                    try {
                        defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, "DATE") : valueString);
                    }
                    catch (final MetaDataException e2) {
                        try {
                            defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, "TIMESTAMP") : valueString);
                        }
                        catch (final MetaDataException e3) {
                            try {
                                defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, "TIME") : valueString);
                            }
                            catch (final MetaDataException e4) {
                                e4.printStackTrace();
                            }
                        }
                    }
                }
            }
            else if (this == Mssql.REAL) {
                try {
                    defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, "FLOAT") : valueString);
                }
                catch (final MetaDataException e) {
                    e.printStackTrace();
                }
            }
            else if (this == Mssql.FLOAT) {
                try {
                    defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, "DOUBLE") : valueString);
                }
                catch (final MetaDataException e) {
                    e.printStackTrace();
                }
            }
            return (defValue != null && defValue.equals("")) ? null : defValue;
        }
        
        @Override
        public String getDefaultValueFromMetaDataString(final String metaString, final String dataType) {
            final String defVal = DataType.getDefaultValueFromMetaDataString(metaString, dataType);
            return (defVal == null) ? null : ((dataType.equalsIgnoreCase("DATETIME") && defVal.equals("1970-01-01 05:30:00.0")) ? "1970-01-01 05:30:00" : ((dataType.equalsIgnoreCase("DOUBLE") && defVal.equals("0.0")) ? "0" : defVal));
        }
        
        @Override
        public String getEquivalentFKConstraintString(final String constraintStr) {
            return "".equalsIgnoreCase(constraintStr) ? "ON DELETE CASCADE" : DataType.getEquivalentFKConstraintString(constraintStr);
        }
    }
    
    public enum Firebird implements Type
    {
        INTEGER(new Mysql[] { Mysql.INT }, new Postgres[] { Postgres.INT4, Postgres.BOOL }, new Mssql[] { Mssql.INT, Mssql.BIT }), 
        BIGINT(new Mysql[] { Mysql.BIGINT }, new Postgres[] { Postgres.INT8 }, new Mssql[] { Mssql.BIGINT }), 
        DOUBLE_PRECISION(new Mysql[] { Mysql.DOUBLE }, new Postgres[] { Postgres.FLOAT8 }, new Mssql[] { Mssql.FLOAT }, "DOUBLE PRECISION"), 
        FLOAT(new Mysql[] { Mysql.FLOAT }, new Postgres[] { Postgres.FLOAT4 }, new Mssql[] { Mssql.REAL }), 
        DECIMAL(new Mysql[] { Mysql.DECIMAL }, new Postgres[] { Postgres.NUMERIC }, new Mssql[] { Mssql.DECIMAL }), 
        SMALLINT(new Mysql[] { Mysql.TINYINT }, new Postgres[] { Postgres.SMALLINT, Postgres.INT2 }, new Mssql[] { Mssql.TINYINT }), 
        VARCHAR(new Mysql[] { Mysql.VARCHAR, Mysql.TEXT }, new Postgres[] { Postgres.CITEXT, Postgres.BYTEA }, new Mssql[] { Mssql.VARCHAR, Mssql.NVARCHAR, Mssql.VARBINARY, Mssql.TEXT, Mssql.NTEXT }), 
        BLOB(new Mysql[] { Mysql.BLOB, Mysql.LONGBLOB }, new Postgres[] { Postgres.BYTEA }, new Mssql[] { Mssql.VARBINARY }, "BLOB SUB_TYPE 0"), 
        BLOB1(new Mysql[] { Mysql.BLOB, Mysql.LONGBLOB }, new Postgres[] { Postgres.BYTEA, Postgres.CITEXT }, new Mssql[] { Mssql.VARBINARY, Mssql.VARCHAR, Mssql.TEXT, Mssql.NVARCHAR, Mssql.NTEXT }, "BLOB SUB_TYPE 1"), 
        DATE(new Mysql[] { Mysql.DATE }, new Postgres[] { Postgres.DATE }, new Mssql[] { Mssql.DATETIME }), 
        TIME(new Mysql[] { Mysql.TIME }, new Postgres[] { Postgres.TIME }, new Mssql[] { Mssql.DATETIME }), 
        TIMESTAMP(new Mysql[] { Mysql.DATETIME }, new Postgres[] { Postgres.TIMESTAMP }, new Mssql[] { Mssql.DATETIME }), 
        UNKNOWN(new Mysql[0], new Postgres[0], new Mssql[0]);
        
        Mysql[] mysqlTypes;
        Postgres[] pgsqlType;
        Mssql[] mssqlTypes;
        String toString;
        
        private Firebird() {
            this.toString = null;
        }
        
        private Firebird(final Mysql[] myTypes, final Postgres[] pgTypes, final Mssql[] msType) {
            this.toString = null;
            this.mysqlTypes = myTypes;
            this.pgsqlType = pgTypes;
            this.mssqlTypes = msType;
        }
        
        private Firebird(final Mysql[] myTypes, final Postgres[] pgTypes, final Mssql[] msType, final String stringRepresentation) {
            this.toString = null;
            this.mysqlTypes = myTypes;
            this.pgsqlType = pgTypes;
            this.mssqlTypes = msType;
            this.toString = stringRepresentation;
        }
        
        @Override
        public boolean contains(final SchemaAnalyzerUtil.DBType dbName, final Type dataTypeString) {
            if (Firebird.UNKNOWN.toString().equals(dataTypeString)) {
                DataType.LOGGER.info("Unknown datatype specified " + dbName + "." + dataTypeString);
                throw new IllegalArgumentException("Unknown datatype specified " + dbName + "." + dataTypeString);
            }
            switch (dbName) {
                case MYSQL: {
                    return this.isContains(this.mysqlTypes, dataTypeString);
                }
                case MSSQL: {
                    return this.isContains(this.mssqlTypes, dataTypeString);
                }
                case POSTGRES: {
                    return this.isContains(this.pgsqlType, dataTypeString);
                }
                default: {
                    return false;
                }
            }
        }
        
        private boolean isContains(final Type[] dataTypes, final Type dataType) {
            for (final Type type : dataTypes) {
                if (type.toString().equalsIgnoreCase(dataType.toString()) || type == dataType) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            return (this.toString != null) ? this.toString : super.toString();
        }
        
        @Override
        public Object getDefaultValue(final String valueString) {
            Object defValue = valueString;
            if (this == Firebird.DATE) {
                try {
                    defValue = ((valueString != null && valueString.length() > 0) ? MetaDataUtil.convert(valueString, this.toString()) : valueString);
                }
                catch (final MetaDataException e) {
                    e.printStackTrace();
                }
            }
            else if (this == Firebird.DECIMAL) {
                defValue = ((valueString != null && valueString.length() > 0) ? Double.valueOf(Double.parseDouble(valueString)) : valueString);
            }
            return (defValue != null && defValue.equals("")) ? null : defValue;
        }
        
        @Override
        public String getDefaultValueFromMetaDataString(final String metaString, final String dataType) {
            final String defVal = DataType.getDefaultValueFromMetaDataString(metaString, dataType);
            return (defVal == null) ? null : (dataType.equalsIgnoreCase("INTEGER") ? (defVal.equals("0") ? "false" : (defVal.equals("1") ? "true" : defVal)) : defVal);
        }
        
        @Override
        public String getEquivalentFKConstraintString(final String constraintStr) {
            return DataType.getEquivalentFKConstraintString(constraintStr);
        }
    }
    
    interface Type
    {
        boolean contains(final SchemaAnalyzerUtil.DBType p0, final Type p1);
        
        Object getDefaultValue(final String p0);
        
        String getDefaultValueFromMetaDataString(final String p0, final String p1);
        
        String getEquivalentFKConstraintString(final String p0);
    }
}
