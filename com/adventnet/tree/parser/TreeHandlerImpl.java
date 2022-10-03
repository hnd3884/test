package com.adventnet.tree.parser;

import java.net.URL;
import org.xml.sax.EntityResolver;
import java.io.File;
import com.adventnet.persistence.DataAccess;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class TreeHandlerImpl implements TreeHandler
{
    Logger logger;
    static HashMap treeDefinitionMap;
    ArrayList tablesInTree;
    DataObject treeDef;
    Row baseRow;
    Row tableInTree;
    ArrayList treeTypes;
    ArrayList treeIdentifierColumns;
    
    public TreeHandlerImpl() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public ArrayList getTreeTypes() {
        return this.treeTypes;
    }
    
    public static DataObject getTreeDefinition(final String treeType) {
        return TreeHandlerImpl.treeDefinitionMap.get(treeType);
    }
    
    @Override
    public void handle_is_sibling_ordered(final String data, final Attributes meta) throws SAXException {
        this.logger.finer("in siblingordered ");
        this.baseRow.set("SIBLINGORDERED", (Object)new Boolean(data));
    }
    
    @Override
    public void handle_base_treenode_table(final String data, final Attributes meta) throws SAXException {
        this.logger.finer("in basetreenodetable ");
        this.baseRow.set("BASETREENODETABLE", (Object)data);
    }
    
    @Override
    public void start_table_in_tree(final Attributes meta) throws SAXException {
        this.logger.finer("method beginning In Start_table_in_tree :");
        (this.tableInTree = new Row("TablesInTree")).set("TREETYPE", this.baseRow.get("TREETYPE"));
    }
    
    @Override
    public void end_table_in_tree() throws SAXException {
        this.logger.finer("method beginning In end_table_in_tree :");
        this.tablesInTree.add(this.tableInTree);
    }
    
    @Override
    public void handle_treenode_table(final String data, final Attributes meta) throws SAXException {
        this.logger.finer("method beginning In handle_treenode_table :");
        this.tableInTree.set("TREENODETABLE", (Object)data);
    }
    
    @Override
    public void start_tables_in_tree(final Attributes meta) throws SAXException {
        this.logger.finer("method beginning In start_tables_in_treee :");
        this.tablesInTree = new ArrayList();
    }
    
    @Override
    public void start_tree_identifier_columns(final Attributes meta) throws SAXException {
        this.logger.finer("method beginning In start_tree_identifier_columns :");
        this.treeIdentifierColumns = new ArrayList();
    }
    
    @Override
    public void end_tables_in_tree() throws SAXException {
        this.logger.finer("method beginning In end_tables_in_tree :");
        for (int i = 0; i < this.tablesInTree.size(); ++i) {
            this.addRow(this.treeDef, this.tablesInTree.get(i));
        }
    }
    
    @Override
    public void end_tree_identifier_columns() throws SAXException {
        this.logger.finer("method beginning In end_tree_identifier_columns :");
        for (int i = 0; i < this.treeIdentifierColumns.size(); ++i) {
            this.addRow(this.treeDef, this.treeIdentifierColumns.get(i));
        }
    }
    
    @Override
    public void handle_column_name(final String data, final Attributes meta) throws SAXException {
        this.logger.finer("method beginning In handle_column_name :");
        final Row treeIdentifier = new Row("TreeIdentifierColumns");
        treeIdentifier.set("TREETYPE", this.baseRow.get("TREETYPE"));
        treeIdentifier.set("COLUMNNAME", (Object)data);
        this.treeIdentifierColumns.add(treeIdentifier);
    }
    
    @Override
    public void handle_tree_type(final String data, final Attributes meta) throws SAXException {
        this.logger.finer("method beginning In handle_tree_type :");
        this.baseRow.set("TREETYPE", (Object)data);
        this.treeTypes.add(data);
    }
    
    @Override
    public void handle_table_name(final String data, final Attributes meta) throws SAXException {
        this.logger.finer("method beginning In handle_table_name :");
        this.tableInTree.set("TABLENAME", (Object)data);
    }
    
    @Override
    public void handle_tree_info_table(final String data, final Attributes meta) throws SAXException {
        this.logger.finer("method beginning In handle_tree_finer_table :");
        this.baseRow.set("TREEINFOTABLE", (Object)data);
    }
    
    @Override
    public void start_tree_definition(final Attributes meta) throws SAXException {
        try {
            this.logger.finer("method beginning In Start_tree_definition :");
            this.treeDef = DataAccess.constructDataObject();
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new SAXException("Exception while constructDataObject()", e);
        }
        this.baseRow = new Row("TreeDefinition");
        this.logger.finer("method ending In Start_tree_definition :");
    }
    
    @Override
    public void end_tree_definition() throws SAXException {
        try {
            this.logger.finer("In end_tree_definition :");
            this.addRow(this.treeDef, this.baseRow);
            TreeHandlerImpl.treeDefinitionMap.put(this.baseRow.get("TREETYPE"), this.treeDef);
        }
        catch (final Exception e) {
            throw new SAXException("Exception while treeDefinitionMap.put)", e);
        }
    }
    
    @Override
    public void start_tree_definitions(final Attributes meta) throws SAXException {
        this.treeTypes = new ArrayList();
    }
    
    @Override
    public void end_tree_definitions() throws SAXException {
    }
    
    public static void main(final String[] args) throws Exception {
        System.out.println("########### START ");
        final String filePath = "./conf/tree-definition.xml";
        final URL url = new File(filePath).toURL();
        final TreeHandlerImpl impl = new TreeHandlerImpl();
        final TreeParser parser = new TreeParser(impl, new TreeDefEntityResolverForTesting());
        parser.parse(url);
        final DataObject def = getTreeDefinition(args[0]);
        System.out.println("tree def : " + def);
    }
    
    void addRow(final DataObject doo, final Row row) throws SAXException {
        try {
            doo.addRow(row);
        }
        catch (final Exception e) {
            throw new SAXException(e);
        }
    }
    
    static {
        TreeHandlerImpl.treeDefinitionMap = new HashMap();
    }
}
