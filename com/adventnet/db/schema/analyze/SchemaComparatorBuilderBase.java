package com.adventnet.db.schema.analyze;

import java.util.List;
import com.adventnet.db.adapter.DBAdapter;
import javax.sql.DataSource;

class SchemaComparatorBuilderBase<GeneratorT extends SchemaComparatorBuilderBase<GeneratorT>>
{
    private SchemaComparatorObject compObj;
    
    public SchemaComparatorBuilderBase(final SchemaComparatorObject compObj) {
        this.compObj = compObj;
    }
    
    public SchemaComparatorObject getInstance() {
        return this.compObj;
    }
    
    public GeneratorT setDataSource(final DataSource ds) {
        this.getInstance().setSrcDataSource(ds);
        return (GeneratorT)this;
    }
    
    public GeneratorT setDBAdapter(final DBAdapter dba) {
        this.getInstance().setSrcDBAdapter(dba);
        return (GeneratorT)this;
    }
    
    public GeneratorT withDBType(final SchemaAnalyzerUtil.DBType type) {
        this.getInstance().setSrcDBType(type);
        return (GeneratorT)this;
    }
    
    public GeneratorT setSrcDataSource(final DataSource ds) {
        this.getInstance().setSrcDataSource(ds);
        return (GeneratorT)this;
    }
    
    public GeneratorT setDestDataSource(final DataSource ds) {
        this.getInstance().setDestDataSource(ds);
        return (GeneratorT)this;
    }
    
    public GeneratorT setSrcDBAdapter(final DBAdapter dba) {
        this.getInstance().setSrcDBAdapter(dba);
        return (GeneratorT)this;
    }
    
    public GeneratorT setDestDBAdapter(final DBAdapter dba) {
        this.getInstance().setDestDBAdapter(dba);
        return (GeneratorT)this;
    }
    
    public GeneratorT withSrcDBType(final SchemaAnalyzerUtil.DBType type) {
        this.getInstance().setSrcDBType(type);
        return (GeneratorT)this;
    }
    
    public GeneratorT withDestDBType(final SchemaAnalyzerUtil.DBType type) {
        this.getInstance().setDestDBType(type);
        return (GeneratorT)this;
    }
    
    public GeneratorT usingSchemaComparatorHandler(final SchemaComparatorHandler handler) {
        this.getInstance().setComparatorHandler(handler);
        return (GeneratorT)this;
    }
    
    public GeneratorT WithTableNames(final List<String> list) {
        this.getInstance().setTableNames(list);
        return (GeneratorT)this;
    }
    
    public GeneratorT withComparatorType(final SchemaComparator.ComparatorType ctype) {
        this.getInstance().setComparatorType(ctype);
        return (GeneratorT)this;
    }
    
    public GeneratorT exitOnFirstDiff(final boolean exit) {
        this.getInstance().exitOnFirstDiff(exit);
        return (GeneratorT)this;
    }
}
