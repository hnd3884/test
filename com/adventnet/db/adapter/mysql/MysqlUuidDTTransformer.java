package com.adventnet.db.adapter.mysql;

import java.util.UUID;
import com.adventnet.db.adapter.DTTransformer;

public class MysqlUuidDTTransformer implements DTTransformer
{
    @Override
    public Object transform(final String tableName, final String columnName, final Object value, final String dataType) throws Exception {
        String valueStr = null;
        if (value == null) {
            return null;
        }
        if (value instanceof UUID) {
            valueStr = ((UUID)value).toString().replace("-", "");
        }
        else {
            valueStr = String.valueOf(value).replace("-", "");
        }
        return valueStr;
    }
    
    @Override
    public Object unTransform(final String tableName, final String columnName, final Object value, final String dataType) throws Exception {
        if (value == null) {
            return null;
        }
        return UUID.fromString(this.formUUIDString(String.valueOf(value)));
    }
    
    private String formUUIDString(final String value) {
        final StringBuilder sb = new StringBuilder();
        sb.append(value.substring(0, 8) + "-");
        sb.append(value.substring(8, 12) + "-");
        sb.append(value.substring(12, 16) + "-");
        sb.append(value.substring(16, 20) + "-");
        sb.append(value.substring(20, 32));
        return sb.toString();
    }
}
