package com.adventnet.db.schema.analyze;

public class SchemaComparatorBuilder extends SchemaComparatorBuilderBase<SchemaComparatorBuilder>
{
    public static SchemaComparatorBuilder config() {
        return new SchemaComparatorBuilder();
    }
    
    public SchemaComparatorBuilder() {
        super(new SchemaComparatorObject());
    }
    
    public SchemaComparatorObject build() {
        return this.getInstance();
    }
}
