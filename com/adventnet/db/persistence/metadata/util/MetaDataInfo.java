package com.adventnet.db.persistence.metadata.util;

import com.adventnet.persistence.DataAccessException;
import java.io.IOException;
import java.net.URL;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.List;
import java.util.Enumeration;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.util.Set;

public interface MetaDataInfo
{
    Set getAllModuleNames();
    
    void addDataDictionaryConfiguration(final DataDictionary p0) throws MetaDataException;
    
    void addTableDefinition(final String p0, final TableDefinition p1) throws MetaDataException;
    
    void alterTableDefinition(final AlterTableQuery p0) throws MetaDataException;
    
    Enumeration getAllTableDefinitions() throws MetaDataException;
    
    List<TableDefinition> getTableDefinitions() throws MetaDataException;
    
    List<String> getTableNamesInDefinedOrder() throws MetaDataException;
    
    TableDefinition getTableDefinitionByName(final String p0) throws MetaDataException;
    
    ForeignKeyDefinition getForeignKeyDefinitionByName(final String p0) throws MetaDataException;
    
    DataDictionary getDataDictionary(final String p0) throws MetaDataException;
    
    List<TableDefinition> getAllRelatedTableDefinitions(final String p0) throws MetaDataException;
    
    List getForeignKeys(final String p0, final String p1) throws MetaDataException;
    
    String getDefinedTableName(final String p0) throws MetaDataException;
    
    String getDefinedTableNameByDisplayName(final String p0) throws MetaDataException;
    
    String getDefinedColumnName(final String p0, final String p1) throws MetaDataException;
    
    String getModuleNameOfTable(final String p0) throws MetaDataException;
    
    void removeDataDictionaryConfiguration(final String p0) throws MetaDataException;
    
    void removeTableDefinition(final String p0) throws MetaDataException;
    
    List getReferringForeignKeyDefinitions(final String p0) throws MetaDataException;
    
    List getReferringForeignKeyDefinitions(final String p0, final String p1) throws MetaDataException;
    
    List<String> getReferringTableNames(final String p0);
    
    void dump() throws MetaDataException;
    
    DataDictionary loadDataDictionary(final URL p0) throws MetaDataException;
    
    DataDictionary loadDataDictionary(final URL p0, final boolean p1) throws MetaDataException;
    
    DataDictionary loadDataDictionary(final URL p0, final boolean p1, final String p2) throws MetaDataException;
    
    void validateTableDefinition(final TableDefinition p0) throws MetaDataException;
    
    String[] getAllDataDictionarNames() throws MetaDataException;
    
    void setLoaded(final boolean p0);
    
    boolean isLoaded();
    
    void addTemplateInstance(final String p0, final String p1) throws MetaDataException;
    
    boolean removeTemplateInstance(final String p0, final String p1) throws MetaDataException;
    
    TemplateMetaHandler getTemplateHandler(final String p0) throws MetaDataException;
    
    void validateAlterTableQuery(final AlterTableQuery p0) throws MetaDataException;
    
    String getAttribute(final String p0, final String p1, final String p2) throws MetaDataException;
    
    String getAttribute(final String p0, final String p1) throws MetaDataException;
    
    String getAttribute(final String p0) throws MetaDataException;
    
    void loadCustomAttributes(final List<URL> p0, final boolean p1, final boolean p2) throws Exception;
    
    boolean setAttribute(final String p0, final String p1, final String p2, final String p3) throws MetaDataException, IOException, DataAccessException;
    
    boolean setAttribute(final String p0, final String p1, final String p2) throws MetaDataException, IOException, DataAccessException;
    
    boolean removeAttribute(final String p0, final String p1, final String p2) throws IOException, DataAccessException, MetaDataException;
    
    boolean removeAttribute(final String p0, final String p1) throws IOException, DataAccessException, MetaDataException;
    
    void loadDynamicCustomDDAttributes() throws IOException, DataAccessException, MetaDataException;
    
    void removeCustomAttributeConfigurations();
}
