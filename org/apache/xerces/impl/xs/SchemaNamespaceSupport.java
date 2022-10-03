package org.apache.xerces.impl.xs;

import org.w3c.dom.NamedNodeMap;
import org.apache.xerces.xni.NamespaceContext;
import org.w3c.dom.Node;
import org.apache.xerces.util.XMLSymbols;
import org.w3c.dom.Attr;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Document;
import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.apache.xerces.util.SymbolTable;
import org.w3c.dom.Element;
import org.apache.xerces.util.NamespaceSupport;

public class SchemaNamespaceSupport extends NamespaceSupport
{
    private SchemaRootContext fSchemaRootContext;
    
    public SchemaNamespaceSupport(final Element element, final SymbolTable symbolTable) {
        this.fSchemaRootContext = null;
        if (element != null && !(element instanceof ElementImpl)) {
            final Document ownerDocument = element.getOwnerDocument();
            if (ownerDocument != null && element != ownerDocument.getDocumentElement()) {
                this.fSchemaRootContext = new SchemaRootContext(element, symbolTable);
            }
        }
    }
    
    public SchemaNamespaceSupport(final SchemaNamespaceSupport schemaNamespaceSupport) {
        this.fSchemaRootContext = null;
        this.fSchemaRootContext = schemaNamespaceSupport.fSchemaRootContext;
        this.fNamespaceSize = schemaNamespaceSupport.fNamespaceSize;
        if (this.fNamespace.length < this.fNamespaceSize) {
            this.fNamespace = new String[this.fNamespaceSize];
        }
        System.arraycopy(schemaNamespaceSupport.fNamespace, 0, this.fNamespace, 0, this.fNamespaceSize);
        this.fCurrentContext = schemaNamespaceSupport.fCurrentContext;
        if (this.fContext.length <= this.fCurrentContext) {
            this.fContext = new int[this.fCurrentContext + 1];
        }
        System.arraycopy(schemaNamespaceSupport.fContext, 0, this.fContext, 0, this.fCurrentContext + 1);
    }
    
    public void setEffectiveContext(final String[] array) {
        if (array == null || array.length == 0) {
            return;
        }
        this.pushContext();
        final int fNamespaceSize = this.fNamespaceSize + array.length;
        if (this.fNamespace.length < fNamespaceSize) {
            final String[] fNamespace = new String[fNamespaceSize];
            System.arraycopy(this.fNamespace, 0, fNamespace, 0, this.fNamespace.length);
            this.fNamespace = fNamespace;
        }
        System.arraycopy(array, 0, this.fNamespace, this.fNamespaceSize, array.length);
        this.fNamespaceSize = fNamespaceSize;
    }
    
    public String[] getEffectiveLocalContext() {
        Object o = null;
        if (this.fCurrentContext >= 3) {
            final int n = this.fContext[3];
            final int n2 = this.fNamespaceSize - n;
            if (n2 > 0) {
                o = new String[n2];
                System.arraycopy(this.fNamespace, n, o, 0, n2);
            }
        }
        return (String[])o;
    }
    
    public void makeGlobal() {
        if (this.fCurrentContext >= 3) {
            this.fCurrentContext = 3;
            this.fNamespaceSize = this.fContext[3];
        }
    }
    
    public String getURI(final String s) {
        String s2 = super.getURI(s);
        if (s2 == null && this.fSchemaRootContext != null) {
            if (!this.fSchemaRootContext.fDOMContextBuilt) {
                this.fSchemaRootContext.fillNamespaceContext();
                this.fSchemaRootContext.fDOMContextBuilt = true;
            }
            if (this.fSchemaRootContext.fNamespaceSize > 0 && !this.containsPrefix(s)) {
                s2 = this.fSchemaRootContext.getURI(s);
            }
        }
        return s2;
    }
    
    static final class SchemaRootContext
    {
        String[] fNamespace;
        int fNamespaceSize;
        boolean fDOMContextBuilt;
        private final Element fSchemaRoot;
        private final SymbolTable fSymbolTable;
        private final QName fAttributeQName;
        
        SchemaRootContext(final Element fSchemaRoot, final SymbolTable fSymbolTable) {
            this.fNamespace = new String[32];
            this.fNamespaceSize = 0;
            this.fDOMContextBuilt = false;
            this.fAttributeQName = new QName();
            this.fSchemaRoot = fSchemaRoot;
            this.fSymbolTable = fSymbolTable;
        }
        
        void fillNamespaceContext() {
            if (this.fSchemaRoot != null) {
                for (Node node = this.fSchemaRoot.getParentNode(); node != null; node = node.getParentNode()) {
                    if (1 == node.getNodeType()) {
                        final NamedNodeMap attributes = node.getAttributes();
                        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                            final Attr attr = (Attr)attributes.item(i);
                            String s = attr.getValue();
                            if (s == null) {
                                s = XMLSymbols.EMPTY_STRING;
                            }
                            this.fillQName(this.fAttributeQName, attr);
                            if (this.fAttributeQName.uri == NamespaceContext.XMLNS_URI) {
                                if (this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                                    this.declarePrefix(this.fAttributeQName.localpart, (s.length() != 0) ? this.fSymbolTable.addSymbol(s) : null);
                                }
                                else {
                                    this.declarePrefix(XMLSymbols.EMPTY_STRING, (s.length() != 0) ? this.fSymbolTable.addSymbol(s) : null);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        String getURI(final String s) {
            for (int i = 0; i < this.fNamespaceSize; i += 2) {
                if (this.fNamespace[i] == s) {
                    return this.fNamespace[i + 1];
                }
            }
            return null;
        }
        
        private void declarePrefix(final String s, final String s2) {
            if (this.fNamespaceSize == this.fNamespace.length) {
                final String[] fNamespace = new String[this.fNamespaceSize * 2];
                System.arraycopy(this.fNamespace, 0, fNamespace, 0, this.fNamespaceSize);
                this.fNamespace = fNamespace;
            }
            this.fNamespace[this.fNamespaceSize++] = s;
            this.fNamespace[this.fNamespaceSize++] = s2;
        }
        
        private void fillQName(final QName qName, final Node node) {
            final String prefix = node.getPrefix();
            final String localName = node.getLocalName();
            final String nodeName = node.getNodeName();
            final String namespaceURI = node.getNamespaceURI();
            qName.prefix = ((prefix != null) ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING);
            qName.localpart = ((localName != null) ? this.fSymbolTable.addSymbol(localName) : XMLSymbols.EMPTY_STRING);
            qName.rawname = ((nodeName != null) ? this.fSymbolTable.addSymbol(nodeName) : XMLSymbols.EMPTY_STRING);
            qName.uri = ((namespaceURI != null && namespaceURI.length() > 0) ? this.fSymbolTable.addSymbol(namespaceURI) : null);
        }
    }
}
