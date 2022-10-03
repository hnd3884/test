package com.adventnet.tree.parser;

import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParserFactory;
import java.net.URL;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.EntityResolver;
import java.util.Stack;
import org.xml.sax.ContentHandler;

public class TreeParser implements ContentHandler
{
    private StringBuffer buffer;
    private TreeHandler handler;
    private Stack context;
    private EntityResolver resolver;
    
    public TreeParser(final TreeHandler handler, final EntityResolver resolver) {
        this.handler = handler;
        this.resolver = resolver;
        this.buffer = new StringBuffer(111);
        this.context = new Stack();
    }
    
    @Override
    public final void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public final void startDocument() throws SAXException {
    }
    
    @Override
    public final void endDocument() throws SAXException {
    }
    
    @Override
    public final void startElement(final String ns, String name, final String qname, final Attributes attrs) throws SAXException {
        this.dispatch(true);
        name = qname;
        this.context.push(new Object[] { qname, new AttributesImpl(attrs) });
        if ("table-in-tree".equals(name)) {
            this.handler.start_table_in_tree(attrs);
        }
        else if ("tables-in-tree".equals(name)) {
            this.handler.start_tables_in_tree(attrs);
        }
        else if ("tree-definition".equals(name)) {
            this.handler.start_tree_definition(attrs);
        }
        else if ("tree-definitions".equals(name)) {
            this.handler.start_tree_definitions(attrs);
        }
        else if ("tree-identifier-columns".equals(name)) {
            this.handler.start_tree_identifier_columns(attrs);
        }
    }
    
    @Override
    public final void endElement(final String ns, String name, final String qname) throws SAXException {
        this.dispatch(false);
        name = qname;
        this.context.pop();
        if ("table-in-tree".equals(name)) {
            this.handler.end_table_in_tree();
        }
        else if ("tables-in-tree".equals(name)) {
            this.handler.end_tables_in_tree();
        }
        else if ("tree-definition".equals(name)) {
            this.handler.end_tree_definition();
        }
        else if ("tree-definitions".equals(name)) {
            this.handler.end_tree_definitions();
        }
        else if ("tree-identifier-columns".equals(name)) {
            this.handler.end_tree_identifier_columns();
        }
    }
    
    @Override
    public final void characters(final char[] chars, final int start, final int len) throws SAXException {
        this.buffer.append(chars, start, len);
    }
    
    @Override
    public final void ignorableWhitespace(final char[] chars, final int start, final int len) throws SAXException {
    }
    
    @Override
    public final void processingInstruction(final String target, final String data) throws SAXException {
    }
    
    @Override
    public final void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }
    
    @Override
    public final void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public final void skippedEntity(final String name) throws SAXException {
    }
    
    private void dispatch(final boolean fireOnlyIfMixed) throws SAXException {
        if (fireOnlyIfMixed && this.buffer.length() == 0) {
            return;
        }
        final Object[] ctx = this.context.peek();
        final String here = (String)ctx[0];
        final Attributes attrs = (Attributes)ctx[1];
        if ("is-sibling-ordered".equals(here)) {
            if (fireOnlyIfMixed) {
                throw new IllegalStateException("Unexpected characters() event! (Missing DTD?)");
            }
            this.handler.handle_is_sibling_ordered((this.buffer.length() == 0) ? null : this.buffer.toString(), attrs);
        }
        if ("base-treenode-table".equals(here)) {
            if (fireOnlyIfMixed) {
                throw new IllegalStateException("Unexpected characters() event! (Missing DTD?)");
            }
            this.handler.handle_base_treenode_table((this.buffer.length() == 0) ? null : this.buffer.toString(), attrs);
        }
        else if ("treenode-table".equals(here)) {
            if (fireOnlyIfMixed) {
                throw new IllegalStateException("Unexpected characters() event! (Missing DTD?)");
            }
            this.handler.handle_treenode_table((this.buffer.length() == 0) ? null : this.buffer.toString(), attrs);
        }
        else if ("tree-type".equals(here)) {
            if (fireOnlyIfMixed) {
                throw new IllegalStateException("Unexpected characters() event! (Missing DTD?)");
            }
            this.handler.handle_tree_type((this.buffer.length() == 0) ? null : this.buffer.toString(), attrs);
        }
        else if ("table-name".equals(here)) {
            if (fireOnlyIfMixed) {
                throw new IllegalStateException("Unexpected characters() event! (Missing DTD?)");
            }
            this.handler.handle_table_name((this.buffer.length() == 0) ? null : this.buffer.toString(), attrs);
        }
        else if ("tree-info-table".equals(here)) {
            if (fireOnlyIfMixed) {
                throw new IllegalStateException("Unexpected characters() event! (Missing DTD?)");
            }
            this.handler.handle_tree_info_table((this.buffer.length() == 0) ? null : this.buffer.toString(), attrs);
        }
        else if ("column-name".equals(here)) {
            if (fireOnlyIfMixed) {
                throw new IllegalStateException("Unexpected characters() event! (Missing DTD?)");
            }
            this.handler.handle_column_name((this.buffer.length() == 0) ? null : this.buffer.toString(), attrs);
        }
        this.buffer.delete(0, this.buffer.length());
    }
    
    public void parse(final InputSource input) throws SAXException, ParserConfigurationException, IOException {
        parse(input, this);
    }
    
    public void parse(final URL url) throws SAXException, ParserConfigurationException, IOException {
        parse(new InputSource(url.toExternalForm()), this);
    }
    
    public static void parse(final InputSource input, final TreeHandler handler) throws SAXException, ParserConfigurationException, IOException {
        parse(input, new TreeParser(handler, null));
    }
    
    public static void parse(final URL url, final TreeHandler handler) throws SAXException, ParserConfigurationException, IOException {
        parse(new InputSource(url.toExternalForm()), handler);
    }
    
    private static void parse(final InputSource input, final TreeParser recognizer) throws SAXException, ParserConfigurationException, IOException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(false);
        final XMLReader parser = factory.newSAXParser().getXMLReader();
        parser.setContentHandler(recognizer);
        parser.setErrorHandler(recognizer.getDefaultErrorHandler());
        if (recognizer.resolver != null) {
            parser.setEntityResolver(recognizer.resolver);
        }
        parser.parse(input);
    }
    
    protected ErrorHandler getDefaultErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void error(final SAXParseException ex) throws SAXException {
                if (TreeParser.this.context.isEmpty()) {
                    System.err.println("Missing DOCTYPE.");
                }
                throw ex;
            }
            
            @Override
            public void fatalError(final SAXParseException ex) throws SAXException {
                throw ex;
            }
            
            @Override
            public void warning(final SAXParseException ex) throws SAXException {
            }
        };
    }
}
