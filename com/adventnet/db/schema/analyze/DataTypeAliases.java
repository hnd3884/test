package com.adventnet.db.schema.analyze;

public class DataTypeAliases
{
    public static AliasesType getDataTypeAliases(final SchemaAnalyzerUtil.DBType dbType, final String dataType) {
        if (dbType == SchemaAnalyzerUtil.DBType.POSTGRES) {
            for (final PostgresAliases aliase : PostgresAliases.values()) {
                if (aliase.name().equalsIgnoreCase(dataType)) {
                    return aliase;
                }
            }
        }
        else if (dbType == SchemaAnalyzerUtil.DBType.MYSQL) {
            for (final MysqlAliases aliase2 : MysqlAliases.values()) {
                if (aliase2.name().equalsIgnoreCase(dataType)) {
                    return aliase2;
                }
            }
        }
        else if (dbType == SchemaAnalyzerUtil.DBType.MSSQL) {
            for (final MssqlAliases aliase3 : MssqlAliases.values()) {
                if (aliase3.name().equalsIgnoreCase(dataType)) {
                    return aliase3;
                }
            }
        }
        return null;
    }
    
    public enum MysqlAliases implements AliasesType
    {
        TINYINT(new String[] { "BIT" }), 
        BOOLEAN(new String[] { "BIT" });
        
        String[] aliases;
        
        private MysqlAliases(final String[] aliases) {
            this.aliases = aliases;
        }
        
        @Override
        public boolean contains(final String aliaseName) {
            for (final String name : this.aliases) {
                if (name.equalsIgnoreCase(aliaseName)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String getName() {
            return this.name();
        }
    }
    
    public enum MssqlAliases implements AliasesType
    {
        DOUBLEPRECISION(new String[] { "FLOAT" }), 
        FLOAT(new String[] { "REAL" });
        
        String[] aliases;
        
        private MssqlAliases(final String[] aliases) {
            this.aliases = aliases;
        }
        
        @Override
        public boolean contains(final String aliaseName) {
            for (final String name : this.aliases) {
                if (name.equalsIgnoreCase(aliaseName)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String getName() {
            return this.name();
        }
    }
    
    public enum PostgresAliases implements AliasesType
    {
        INT(new String[] { "int", "int4" }), 
        BIGINT(new String[] { "int8" }), 
        DECIMAL(new String[] { "numeric" }), 
        REAL(new String[] { "float4" }), 
        BOOLEAN(new String[] { "bool" }), 
        SMALLINT(new String[] { "int2" }), 
        DOUBLEPRECISION(new String[] { "float8" });
        
        String[] aliases;
        
        private PostgresAliases(final String[] aliases) {
            this.aliases = aliases;
        }
        
        @Override
        public boolean contains(final String aliaseName) {
            for (final String name : this.aliases) {
                if (name.equalsIgnoreCase(aliaseName)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String getName() {
            return this.name();
        }
    }
    
    interface AliasesType
    {
        boolean contains(final String p0);
        
        String getName();
    }
}
