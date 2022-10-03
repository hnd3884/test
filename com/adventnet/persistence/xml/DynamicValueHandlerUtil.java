package com.adventnet.persistence.xml;

import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.List;

public class DynamicValueHandlerUtil
{
    public HashMap getDynamicValues(final String tableName, final List columnNames, final Row row, final DataObject dobj) throws DynamicValueHandlingException {
        final HashMap dynamicValues = new HashMap();
        for (final String columnName : columnNames) {
            final Object temp = DynamicValueHandlerRepositry.getDVHandlerTemplate(tableName, columnName);
            if (temp != null) {
                final DVHandlerTemplate dvTemplate = (DVHandlerTemplate)temp;
                final DynamicValueHandler handler = dvTemplate.getDynamicValueHandler();
                if (handler == null) {
                    continue;
                }
                try {
                    handler.set(dobj);
                    final Object toSet = handler.getAttributeValue(tableName, columnName, dvTemplate.getConfiguredAttributes(), row.get(columnName));
                    dynamicValues.put(columnName, toSet);
                }
                finally {
                    handler.set(null);
                }
            }
        }
        return dynamicValues;
    }
}
