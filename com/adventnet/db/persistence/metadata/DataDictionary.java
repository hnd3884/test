package com.adventnet.db.persistence.metadata;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import com.adventnet.db.persistence.metadata.util.TemplateMetaHandler;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

public class DataDictionary implements Serializable
{
    private static final long serialVersionUID = -3061882583755673631L;
    private ArrayList<TableDefinition> tableDefinitions;
    private HashMap<String, TableDefinition> tableDefMap;
    private boolean isValidated;
    private TemplateMetaHandler templateMetaHandler;
    private String dcType;
    private static Logger out;
    private String name;
    private String description;
    String url;
    private String templateMetaHandlerClassName;
    
    public DataDictionary(final String name) {
        this.tableDefinitions = null;
        this.tableDefMap = null;
        this.isValidated = false;
        this.templateMetaHandler = null;
        this.dcType = null;
        this.name = null;
        this.description = null;
        this.url = null;
        this.templateMetaHandlerClassName = null;
        this.name = name;
        this.tableDefinitions = new ArrayList<TableDefinition>(10);
        this.tableDefMap = new LinkedHashMap<String, TableDefinition>();
    }
    
    public DataDictionary(final String name, final boolean needToValidate) {
        this(name);
        this.isValidated = !needToValidate;
    }
    
    public DataDictionary(final String name, final String url) {
        this(name);
        this.url = url;
    }
    
    public DataDictionary(final String name, final String url, final String templateMetaHandler) {
        this(name, url);
        this.templateMetaHandlerClassName = templateMetaHandler;
    }
    
    public DataDictionary(final String name, final String url, final String templateMetaHandler, final String dcType) {
        this(name, url);
        this.templateMetaHandler = initTemplateMetaHandler(templateMetaHandler);
        this.dcType = dcType;
    }
    
    public TemplateMetaHandler getTemplateMetaHandler() {
        return this.templateMetaHandler;
    }
    
    public String getTemplateMetaHandlerClassName() {
        return this.templateMetaHandlerClassName;
    }
    
    public static TemplateMetaHandler initTemplateMetaHandler(final String templateMetaHandler) {
        TemplateMetaHandler tmh = null;
        try {
            if (templateMetaHandler != null) {
                tmh = (TemplateMetaHandler)Class.forName(templateMetaHandler).newInstance();
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return tmh;
    }
    
    public void setTemplateMetaHandler(final String templateMetaHandler) {
        if (this.templateMetaHandler != null) {
            DataDictionary.out.warning("Old Template_Meta_Handler instance is deleted and new instance is being created");
        }
        this.templateMetaHandler = initTemplateMetaHandler(templateMetaHandler);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getURL() {
        return this.url;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean isValidated() {
        return this.isValidated;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public List<TableDefinition> getTableDefinitions() {
        return this.tableDefinitions;
    }
    
    public void addTableDefinition(final TableDefinition td) {
        final String tableName = td.getTableName();
        this.tableDefinitions.add(td);
        this.tableDefMap.put(tableName, td);
    }
    
    public void removeTableDefinition(final String tableName) {
        final TableDefinition td = this.tableDefMap.remove(tableName);
        this.tableDefinitions.remove(td);
    }
    
    public TableDefinition getTableDefinitionByName(final String name) {
        return this.tableDefMap.get(name);
    }
    
    public ForeignKeyDefinition getForeignKeyDefinitionByName(final String fkName) {
        ForeignKeyDefinition fd = null;
        for (int size = this.tableDefinitions.size(), i = 0; i < size; ++i) {
            final TableDefinition td = this.tableDefinitions.get(i);
            fd = td.getForeignKeyDefinitionByName(fkName);
            if (fd != null) {
                return fd;
            }
        }
        return fd;
    }
    
    public void renameTable(final String oldTableName, final String newTableName) {
        final TableDefinition td = this.tableDefMap.remove(oldTableName);
        this.tableDefinitions.remove(td);
        td.renameTableName(newTableName);
        this.tableDefinitions.add(td);
        this.tableDefMap.put(newTableName, td);
    }
    
    public void setDynamicColumnType(final String dcType) {
        this.dcType = dcType;
    }
    
    public String getDynamicColumnType() {
        return this.dcType;
    }
    
    static {
        DataDictionary.out = Logger.getLogger(DataDictionary.class.getName());
    }
}
