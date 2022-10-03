package com.adventnet.db.adapter;

public class SerialDTTransformer implements DTTransformer
{
    @Override
    public Object transform(final String tableName, final String columnName, final Object value, final String dataType) throws Exception {
        return value;
    }
    
    @Override
    public Object unTransform(final String tableName, final String columnName, final Object value, final String dataType) throws Exception {
        return value;
    }
}
