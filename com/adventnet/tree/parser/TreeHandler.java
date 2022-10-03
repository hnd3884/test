package com.adventnet.tree.parser;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

public interface TreeHandler
{
    void handle_is_sibling_ordered(final String p0, final Attributes p1) throws SAXException;
    
    void handle_base_treenode_table(final String p0, final Attributes p1) throws SAXException;
    
    void start_table_in_tree(final Attributes p0) throws SAXException;
    
    void end_table_in_tree() throws SAXException;
    
    void handle_treenode_table(final String p0, final Attributes p1) throws SAXException;
    
    void start_tables_in_tree(final Attributes p0) throws SAXException;
    
    void end_tables_in_tree() throws SAXException;
    
    void handle_tree_type(final String p0, final Attributes p1) throws SAXException;
    
    void handle_table_name(final String p0, final Attributes p1) throws SAXException;
    
    void handle_tree_info_table(final String p0, final Attributes p1) throws SAXException;
    
    void start_tree_definition(final Attributes p0) throws SAXException;
    
    void end_tree_definition() throws SAXException;
    
    void start_tree_definitions(final Attributes p0) throws SAXException;
    
    void end_tree_definitions() throws SAXException;
    
    void start_tree_identifier_columns(final Attributes p0) throws SAXException;
    
    void end_tree_identifier_columns() throws SAXException;
    
    void handle_column_name(final String p0, final Attributes p1) throws SAXException;
}
