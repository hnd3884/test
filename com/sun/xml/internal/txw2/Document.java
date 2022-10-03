package com.sun.xml.internal.txw2;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.sun.xml.internal.txw2.output.XmlSerializer;

public final class Document
{
    private final XmlSerializer out;
    private boolean started;
    private Content current;
    private final Map<Class, DatatypeWriter> datatypeWriters;
    private int iota;
    private final NamespaceSupport inscopeNamespace;
    private NamespaceDecl activeNamespaces;
    private final ContentVisitor visitor;
    private final StringBuilder prefixSeed;
    private int prefixIota;
    static final char MAGIC = '\0';
    
    Document(final XmlSerializer out) {
        this.started = false;
        this.current = null;
        this.datatypeWriters = new HashMap<Class, DatatypeWriter>();
        this.iota = 1;
        this.inscopeNamespace = new NamespaceSupport();
        this.visitor = new ContentVisitor() {
            @Override
            public void onStartDocument() {
                throw new IllegalStateException();
            }
            
            @Override
            public void onEndDocument() {
                Document.this.out.endDocument();
            }
            
            @Override
            public void onEndTag() {
                Document.this.out.endTag();
                Document.this.inscopeNamespace.popContext();
                Document.this.activeNamespaces = null;
            }
            
            @Override
            public void onPcdata(StringBuilder buffer) {
                if (Document.this.activeNamespaces != null) {
                    buffer = Document.this.fixPrefix(buffer);
                }
                Document.this.out.text(buffer);
            }
            
            @Override
            public void onCdata(StringBuilder buffer) {
                if (Document.this.activeNamespaces != null) {
                    buffer = Document.this.fixPrefix(buffer);
                }
                Document.this.out.cdata(buffer);
            }
            
            @Override
            public void onComment(StringBuilder buffer) {
                if (Document.this.activeNamespaces != null) {
                    buffer = Document.this.fixPrefix(buffer);
                }
                Document.this.out.comment(buffer);
            }
            
            @Override
            public void onStartTag(final String nsUri, final String localName, final Attribute attributes, final NamespaceDecl namespaces) {
                assert nsUri != null;
                assert localName != null;
                Document.this.activeNamespaces = namespaces;
                if (!Document.this.started) {
                    Document.this.started = true;
                    Document.this.out.startDocument();
                }
                Document.this.inscopeNamespace.pushContext();
                for (NamespaceDecl ns = namespaces; ns != null; ns = ns.next) {
                    ns.declared = false;
                    if (ns.prefix != null) {
                        final String uri = Document.this.inscopeNamespace.getURI(ns.prefix);
                        if (uri == null || !uri.equals(ns.uri)) {
                            Document.this.inscopeNamespace.declarePrefix(ns.prefix, ns.uri);
                            ns.declared = true;
                        }
                    }
                }
                for (NamespaceDecl ns = namespaces; ns != null; ns = ns.next) {
                    if (ns.prefix == null) {
                        if (Document.this.inscopeNamespace.getURI("").equals(ns.uri)) {
                            ns.prefix = "";
                        }
                        else {
                            String p = Document.this.inscopeNamespace.getPrefix(ns.uri);
                            if (p == null) {
                                while (Document.this.inscopeNamespace.getURI(p = Document.this.newPrefix()) != null) {}
                                ns.declared = true;
                                Document.this.inscopeNamespace.declarePrefix(p, ns.uri);
                            }
                            ns.prefix = p;
                        }
                    }
                }
                assert namespaces.uri.equals(nsUri);
                assert namespaces.prefix != null : "a prefix must have been all allocated";
                Document.this.out.beginStartTag(nsUri, localName, namespaces.prefix);
                for (NamespaceDecl ns = namespaces; ns != null; ns = ns.next) {
                    if (ns.declared) {
                        Document.this.out.writeXmlns(ns.prefix, ns.uri);
                    }
                }
                for (Attribute a = attributes; a != null; a = a.next) {
                    String prefix;
                    if (a.nsUri.length() == 0) {
                        prefix = "";
                    }
                    else {
                        prefix = Document.this.inscopeNamespace.getPrefix(a.nsUri);
                    }
                    Document.this.out.writeAttribute(a.nsUri, a.localName, prefix, Document.this.fixPrefix(a.value));
                }
                Document.this.out.endStartTag(nsUri, localName, namespaces.prefix);
            }
        };
        this.prefixSeed = new StringBuilder("ns");
        this.prefixIota = 0;
        this.out = out;
        for (final DatatypeWriter dw : DatatypeWriter.BUILTIN) {
            this.datatypeWriters.put(dw.getType(), dw);
        }
    }
    
    void flush() {
        this.out.flush();
    }
    
    void setFirstContent(final Content c) {
        assert this.current == null;
        (this.current = new StartDocument()).setNext(this, c);
    }
    
    public void addDatatypeWriter(final DatatypeWriter<?> dw) {
        this.datatypeWriters.put(dw.getType(), dw);
    }
    
    void run() {
        while (true) {
            final Content next = this.current.getNext();
            if (next == null || !next.isReadyToCommit()) {
                break;
            }
            next.accept(this.visitor);
            next.written();
            this.current = next;
        }
    }
    
    void writeValue(final Object obj, final NamespaceResolver nsResolver, final StringBuilder buf) {
        if (obj == null) {
            throw new IllegalArgumentException("argument contains null");
        }
        if (obj instanceof Object[]) {
            for (final Object o : (Object[])obj) {
                this.writeValue(o, nsResolver, buf);
            }
            return;
        }
        if (obj instanceof Iterable) {
            for (final Object o2 : (Iterable)obj) {
                this.writeValue(o2, nsResolver, buf);
            }
            return;
        }
        if (buf.length() > 0) {
            buf.append(' ');
        }
        for (Class c = obj.getClass(); c != null; c = c.getSuperclass()) {
            final DatatypeWriter dw = this.datatypeWriters.get(c);
            if (dw != null) {
                dw.print(obj, nsResolver, buf);
                return;
            }
        }
        buf.append(obj);
    }
    
    private String newPrefix() {
        this.prefixSeed.setLength(2);
        this.prefixSeed.append(++this.prefixIota);
        return this.prefixSeed.toString();
    }
    
    private StringBuilder fixPrefix(final StringBuilder buf) {
        assert this.activeNamespaces != null;
        int len;
        int i;
        for (len = buf.length(), i = 0; i < len && buf.charAt(i) != '\0'; ++i) {}
        if (i == len) {
            return buf;
        }
        while (i < len) {
            char uriIdx;
            NamespaceDecl ns;
            for (uriIdx = buf.charAt(i + 1), ns = this.activeNamespaces; ns != null && ns.uniqueId != uriIdx; ns = ns.next) {}
            if (ns == null) {
                throw new IllegalStateException("Unexpected use of prefixes " + (Object)buf);
            }
            int length = 2;
            final String prefix = ns.prefix;
            if (prefix.length() == 0) {
                if (buf.length() <= i + 2 || buf.charAt(i + 2) != ':') {
                    throw new IllegalStateException("Unexpected use of prefixes " + (Object)buf);
                }
                length = 3;
            }
            buf.replace(i, i + length, prefix);
            for (len += prefix.length() - length; i < len && buf.charAt(i) != '\0'; ++i) {}
        }
        return buf;
    }
    
    char assignNewId() {
        return (char)(this.iota++);
    }
}
