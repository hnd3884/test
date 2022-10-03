package com.adventnet.persistence.xml;

import com.adventnet.persistence.ConcurrentStartupUtil;
import java.util.Map;
import com.adventnet.persistence.Row;
import org.xml.sax.Attributes;
import java.util.logging.Logger;

public class ModuleTableRowTransformer implements XmlRowTransformer
{
    private static final Logger OUT;
    
    @Override
    public Row createRow(final String tableName, final Attributes atts) {
        final Row newrow = new Row(tableName);
        return newrow;
    }
    
    @Override
    public void setDisplayNames(final String tableName, final Map columnNameVsValue) {
    }
    
    @Override
    public void setColumnNames(final String tableName, final Map nodeNameVsValue) {
        if (ConcurrentStartupUtil.isConcurrentModulesEnabled()) {
            if (!nodeNameVsValue.containsKey("modulehierarchy")) {
                throw new RuntimeException("Module entry does not have 'modulehierarchy' attribute. 'modulehierarchy' Attribute is mandatory when hierarchical module creation is enabled");
            }
            ConcurrentStartupUtil.setModuleLevel(nodeNameVsValue.get("modulename").toString(), Integer.parseInt(nodeNameVsValue.get("modulehierarchy").toString()));
        }
    }
    
    static {
        OUT = Logger.getLogger(ModuleTableRowTransformer.class.getName());
    }
}
