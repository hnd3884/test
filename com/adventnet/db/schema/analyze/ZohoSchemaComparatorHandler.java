package com.adventnet.db.schema.analyze;

import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Properties;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;
import java.util.logging.Logger;

public class ZohoSchemaComparatorHandler implements SchemaComparatorHandler
{
    private static final Logger LOGGER;
    private Set<String> ukModules;
    private SchemaComparator.ComparatorType type;
    
    public ZohoSchemaComparatorHandler() {
        this.ukModules = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.type = null;
        try {
            final Properties props = PersistenceInitializer.getDBProps();
            final String modules = props.getProperty("ukmodules");
            if (modules != null) {
                for (final String module : modules.split(",")) {
                    this.ukModules.add(module.trim());
                }
            }
        }
        catch (final Exception e) {
            ZohoSchemaComparatorHandler.LOGGER.severe("Exception occurred:: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void preInvoke(final String tableName) throws Exception {
    }
    
    @Override
    public boolean compareFKConstrains(final String tableName) {
        return true;
    }
    
    @Override
    public boolean compareUniqueConstraints(final String tableName) {
        if (this.type.equals(SchemaComparator.ComparatorType.METADATA_VS_DATABASE)) {
            try {
                final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
                if (tabDef != null) {
                    return this.ukModules.contains(tabDef.getModuleName());
                }
            }
            catch (final Exception e) {
                ZohoSchemaComparatorHandler.LOGGER.severe("Exception occurred:: " + e.getMessage());
                e.printStackTrace();
                return true;
            }
        }
        return true;
    }
    
    @Override
    public boolean comparePKColumns(final String tableName) {
        return true;
    }
    
    @Override
    public boolean compareColumns(final String tableName) {
        return true;
    }
    
    @Override
    public boolean compareTableSchema(final String tableName) {
        return true;
    }
    
    @Override
    public boolean ignoreTableRowCount(final String tableName) throws Exception {
        return false;
    }
    
    @Override
    public boolean compareIndexes(final String tableName) {
        return true;
    }
    
    @Override
    public void postInvoke(final String tableName, final JSONArray diffstrings) throws Exception {
    }
    
    @Override
    public void setComparatorType(final SchemaComparator.ComparatorType type) {
        this.type = type;
    }
    
    @Override
    public boolean isDiffIgnorable(final String tableName, final JSONObject diffstring) {
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(ZohoSchemaComparatorHandler.class.getName());
    }
}
