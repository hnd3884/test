package com.adventnet.db.persistence.metadata.extended;

import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Map;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import java.io.IOException;
import com.adventnet.persistence.DataObject;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class ZohoCustomAttributeHandler implements CustomAttributeHandler
{
    private static final Logger LOGGER;
    
    @Override
    public boolean setAttribute(final String tableName, final String columnName, final String attributeName, final String value) throws IOException, DataAccessException {
        final DataObject dobj = DataAccess.get("CustomAttributes", (Criteria)null);
        final Properties p = new Properties();
        Row customAttrRow = null;
        String out = null;
        if (dobj.isEmpty()) {
            p.setProperty(this.getKey(tableName, columnName, attributeName), value);
            customAttrRow = new Row("CustomAttributes");
            final StringWriter sw = new StringWriter();
            p.store(sw, null);
            out = sw.toString();
            customAttrRow.set("ATTRIBUTES", out.substring(out.indexOf("\n") + 1));
            dobj.addRow(customAttrRow);
        }
        else {
            customAttrRow = dobj.getFirstRow("CustomAttributes");
            final String attributes = (String)customAttrRow.get("ATTRIBUTES");
            p.load(new StringReader(attributes));
            p.setProperty(this.getKey(tableName, columnName, attributeName), value);
            final StringWriter sw2 = new StringWriter();
            p.store(sw2, null);
            out = sw2.toString();
            customAttrRow.set("ATTRIBUTES", out.substring(out.indexOf("\n") + 1));
            dobj.updateRow(customAttrRow);
        }
        DataAccess.update(dobj);
        return true;
    }
    
    @Override
    public boolean setAttribute(final String tableName, final String attributeName, final String value) throws IOException, DataAccessException {
        return this.setAttribute(tableName, null, attributeName, value);
    }
    
    @Override
    public boolean removeAttribute(final String tableName, final String columnName, final String attributeName) throws IOException, DataAccessException {
        final DataObject dobj = DataAccess.get("CustomAttributes", (Criteria)null);
        if (dobj.isEmpty()) {
            ZohoCustomAttributeHandler.LOGGER.log(Level.WARNING, "There are no custom attributes to delete");
            return false;
        }
        final Row customAttrRow = dobj.getFirstRow("CustomAttributes");
        final String attributes = (String)customAttrRow.get("ATTRIBUTES");
        final Properties p = new Properties();
        p.load(new StringReader(attributes));
        final String key = this.getKey(tableName, columnName, attributeName);
        if (p.getProperty(key) == null) {
            ZohoCustomAttributeHandler.LOGGER.log(Level.WARNING, "There is no custom attribute with key :: " + this.getKey(tableName, columnName, attributeName));
            return false;
        }
        p.remove(key);
        final StringWriter sw = new StringWriter();
        p.store(sw, null);
        final String out = sw.toString();
        customAttrRow.set("ATTRIBUTES", out.substring(out.indexOf("\n") + 1));
        dobj.updateRow(customAttrRow);
        DataAccess.update(dobj);
        return true;
    }
    
    @Override
    public boolean removeAttribute(final String tableName, final String attributeName) throws IOException, DataAccessException {
        return this.removeAttribute(tableName, null, attributeName);
    }
    
    @Override
    public ConcurrentHashMap<String, String> loadDynamicCustomDDAttributes() throws IOException, DataAccessException, MetaDataException {
        if (MetaDataUtil.getTableDefinitionByName("CustomAttributes") == null) {
            return new ConcurrentHashMap<String, String>();
        }
        final DataObject dobj = DataAccess.get("CustomAttributes", (Criteria)null);
        if (dobj.isEmpty()) {
            return new ConcurrentHashMap<String, String>();
        }
        final Row customAttrRow = dobj.getFirstRow("CustomAttributes");
        final String attributes = (String)customAttrRow.get("ATTRIBUTES");
        final Properties p = new Properties();
        p.load(new StringReader(attributes));
        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>((Map<? extends String, ? extends String>)p);
        return map;
    }
    
    private String getKey(final String tableName, final String columnName, final String attributeName) {
        String key = null;
        if (columnName != null) {
            key = tableName + "." + columnName + "." + attributeName;
        }
        else {
            key = tableName + "." + attributeName;
        }
        return key;
    }
    
    static {
        LOGGER = Logger.getLogger(ZohoCustomAttributeHandler.class.getName());
    }
}
