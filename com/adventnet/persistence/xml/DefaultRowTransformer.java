package com.adventnet.persistence.xml;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.List;
import java.util.Map;
import com.adventnet.persistence.Row;
import org.xml.sax.Attributes;
import java.util.logging.Logger;

public class DefaultRowTransformer implements XmlRowTransformer
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    
    @Override
    public Row createRow(final String tableName, final Attributes atts) {
        final Row newrow = new Row(tableName);
        return newrow;
    }
    
    @Override
    public void setDisplayNames(final String tableName, final Map columnNameVsvalue) {
        final Properties visibilityprop = DynamicValueHandlerRepositry.getColumnVisibilityProperties(tableName);
        final Properties columnNamesProp = DynamicValueHandlerRepositry.getColumnNamesProperties(tableName);
        List columnstoHide = null;
        List columnstoShow = null;
        if (visibilityprop.containsKey("show")) {
            columnstoShow = ((Hashtable<K, List>)visibilityprop).get("show");
            final Iterator columnitr = columnNameVsvalue.keySet().iterator();
            while (columnitr.hasNext()) {
                final String colName = columnitr.next();
                if (columnstoShow.contains(colName)) {
                    continue;
                }
                columnitr.remove();
            }
        }
        if (visibilityprop.containsKey("hide")) {
            columnstoHide = ((Hashtable<K, List>)visibilityprop).get("hide");
            for (final String hideColumn : columnstoHide) {
                if (columnNameVsvalue.containsKey(hideColumn)) {
                    columnNameVsvalue.remove(hideColumn);
                }
            }
        }
        final Iterator namesItr = ((Hashtable<Object, V>)columnNamesProp).keySet().iterator();
        while (namesItr.hasNext()) {
            final String colName = namesItr.next().toString();
            final String displayName = columnNamesProp.getProperty(colName);
            if (columnNameVsvalue.containsKey(colName)) {
                columnNameVsvalue.put(displayName, columnNameVsvalue.get(colName));
                columnNameVsvalue.remove(colName);
            }
        }
        DefaultRowTransformer.LOGGER.log(Level.FINE, "translated columnname vs columnvalue:{0}", columnNameVsvalue);
    }
    
    @Override
    public void setColumnNames(final String tableName, final Map nodeNameVsValue) {
        final Properties nameProp = DynamicValueHandlerRepositry.getColumnNamesProperties(tableName);
        final Iterator namesitr = ((Hashtable<Object, V>)nameProp).keySet().iterator();
        while (namesitr.hasNext()) {
            final String colName = namesitr.next().toString();
            final String displayName = nameProp.getProperty(colName).toString();
            final String displayNameInLC = displayName.toLowerCase(Locale.ENGLISH);
            if (nodeNameVsValue.containsKey(displayName.toLowerCase())) {
                nodeNameVsValue.put(colName.toLowerCase(), nodeNameVsValue.get(displayNameInLC));
                nodeNameVsValue.remove(displayNameInLC);
            }
        }
        DefaultRowTransformer.LOGGER.log(Level.FINE, "actual columnname vs columnvalue;{0}", nodeNameVsValue);
    }
    
    static {
        CLASS_NAME = DefaultRowTransformer.class.getName();
        LOGGER = Logger.getLogger(DefaultRowTransformer.CLASS_NAME);
    }
}
