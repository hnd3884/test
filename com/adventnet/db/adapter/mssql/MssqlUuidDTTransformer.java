package com.adventnet.db.adapter.mssql;

import java.util.UUID;
import com.adventnet.db.adapter.DTTransformer;

public class MssqlUuidDTTransformer implements DTTransformer
{
    @Override
    public Object transform(final String tableName, final String columnName, final Object value, final String dataType) throws Exception {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID) {
            return ((UUID)value).toString();
        }
        return String.valueOf(value);
    }
    
    @Override
    public Object unTransform(final String tableName, final String columnName, final Object value, final String dataType) throws Exception {
        if (value == null) {
            return null;
        }
        return UUID.fromString(String.valueOf(value));
    }
}
