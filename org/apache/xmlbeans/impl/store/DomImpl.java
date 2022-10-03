package org.apache.xmlbeans.impl.store;

import java.io.PrintStream;
import org.w3c.dom.CharacterData;
import javax.xml.transform.Source;
import org.apache.xmlbeans.impl.soap.SOAPPart;
import org.apache.xmlbeans.impl.soap.DetailEntry;
import org.apache.xmlbeans.impl.soap.Detail;
import org.apache.xmlbeans.impl.soap.SOAPBodyElement;
import org.apache.xmlbeans.impl.soap.SOAPFault;
import org.apache.xmlbeans.impl.soap.SOAPHeaderElement;
import org.apache.xmlbeans.impl.soap.SOAPHeader;
import org.apache.xmlbeans.impl.soap.SOAPEnvelope;
import org.apache.xmlbeans.impl.soap.SOAPBody;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.Name;
import java.util.Iterator;
import org.apache.xmlbeans.impl.soap.SOAPElement;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.UserDataHandler;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlRuntimeException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaType;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.apache.xmlbeans.impl.common.XMLChar;
import org.w3c.dom.NodeList;

final class DomImpl
{
    static final int ELEMENT = 1;
    static final int ATTR = 2;
    static final int TEXT = 3;
    static final int CDATA = 4;
    static final int ENTITYREF = 5;
    static final int ENTITY = 6;
    static final int PROCINST = 7;
    static final int COMMENT = 8;
    static final int DOCUMENT = 9;
    static final int DOCTYPE = 10;
    static final int DOCFRAG = 11;
    static final int NOTATION = 12;
    public static NodeList _emptyNodeList;
    
    static Dom parent(final Dom d) {
        return node_getParentNode(d);
    }
    
    static Dom firstChild(final Dom d) {
        return node_getFirstChild(d);
    }
    
    static Dom nextSibling(final Dom d) {
        return node_getNextSibling(d);
    }
    
    static Dom prevSibling(final Dom d) {
        return node_getPreviousSibling(d);
    }
    
    public static Dom append(final Dom n, final Dom p) {
        return node_insertBefore(p, n, null);
    }
    
    public static Dom insert(final Dom n, final Dom b) {
        assert b != null;
        return node_insertBefore(parent(b), n, b);
    }
    
    public static Dom remove(final Dom n) {
        final Dom p = parent(n);
        if (p != null) {
            node_removeChild(p, n);
        }
        return n;
    }
    
    static String nodeKindName(final int t) {
        switch (t) {
            case 2: {
                return "attribute";
            }
            case 4: {
                return "cdata section";
            }
            case 8: {
                return "comment";
            }
            case 11: {
                return "document fragment";
            }
            case 9: {
                return "document";
            }
            case 10: {
                return "document type";
            }
            case 1: {
                return "element";
            }
            case 6: {
                return "entity";
            }
            case 5: {
                return "entity reference";
            }
            case 12: {
                return "notation";
            }
            case 7: {
                return "processing instruction";
            }
            case 3: {
                return "text";
            }
            default: {
                throw new RuntimeException("Unknown node type");
            }
        }
    }
    
    private static String isValidChild(final Dom parent, final Dom child) {
        final int pk = parent.nodeType();
        final int ck = child.nodeType();
        Label_0248: {
            switch (pk) {
                case 9: {
                    switch (ck) {
                        case 1: {
                            if (document_getDocumentElement(parent) != null) {
                                return "Documents may only have a maximum of one document element";
                            }
                            return null;
                        }
                        case 10: {
                            if (document_getDoctype(parent) != null) {
                                return "Documents may only have a maximum of one document type node";
                            }
                            return null;
                        }
                        case 7:
                        case 8: {
                            return null;
                        }
                        default: {
                            break Label_0248;
                        }
                    }
                    break;
                }
                case 2: {
                    if (ck == 3 || ck == 5) {
                        return null;
                    }
                    break;
                }
                case 1:
                case 5:
                case 6:
                case 11: {
                    switch (ck) {
                        case 1:
                        case 3:
                        case 4:
                        case 5:
                        case 7:
                        case 8: {
                            return null;
                        }
                        default: {
                            break Label_0248;
                        }
                    }
                    break;
                }
                case 3:
                case 4:
                case 7:
                case 8:
                case 10:
                case 12: {
                    return nodeKindName(pk) + " nodes may not have any children";
                }
            }
        }
        return nodeKindName(pk) + " nodes may not have " + nodeKindName(ck) + " nodes as children";
    }
    
    private static void validateNewChild(Dom parent, final Dom child) {
        final String msg = isValidChild(parent, child);
        if (msg != null) {
            throw new HierarchyRequestErr(msg);
        }
        if (parent == child) {
            throw new HierarchyRequestErr("New child and parent are the same node");
        }
        while ((parent = parent(parent)) != null) {
            if (child.nodeType() == 5) {
                throw new NoModificationAllowedErr("Entity reference trees may not be modified");
            }
            if (child == parent) {
                throw new HierarchyRequestErr("New child is an ancestor node of the parent node");
            }
        }
    }
    
    private static String validatePrefix(String prefix, String uri, final String local, final boolean isAttr) {
        validateNcName(prefix);
        if (prefix == null) {
            prefix = "";
        }
        if (uri == null) {
            uri = "";
        }
        if (prefix.length() > 0 && uri.length() == 0) {
            throw new NamespaceErr("Attempt to give a prefix for no namespace");
        }
        if (prefix.equals("xml") && !uri.equals("http://www.w3.org/XML/1998/namespace")) {
            throw new NamespaceErr("Invalid prefix - begins with 'xml'");
        }
        if (isAttr) {
            if (prefix.length() > 0) {
                if (local.equals("xmlns")) {
                    throw new NamespaceErr("Invalid namespace - attr is default namespace already");
                }
                if (Locale.beginsWithXml(local)) {
                    throw new NamespaceErr("Invalid namespace - attr prefix begins with 'xml'");
                }
                if (prefix.equals("xmlns") && !uri.equals("http://www.w3.org/2000/xmlns/")) {
                    throw new NamespaceErr("Invalid namespace - uri is not 'http://www.w3.org/2000/xmlns/;");
                }
            }
            else if (local.equals("xmlns") && !uri.equals("http://www.w3.org/2000/xmlns/")) {
                throw new NamespaceErr("Invalid namespace - uri is not 'http://www.w3.org/2000/xmlns/;");
            }
        }
        else if (Locale.beginsWithXml(prefix)) {
            throw new NamespaceErr("Invalid prefix - begins with 'xml'");
        }
        return prefix;
    }
    
    private static void validateName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Name is empty");
        }
        if (!XMLChar.isValidName(name)) {
            throw new InvalidCharacterError("Name has an invalid character");
        }
    }
    
    private static void validateNcName(final String name) {
        if (name != null && name.length() > 0 && !XMLChar.isValidNCName(name)) {
            throw new InvalidCharacterError();
        }
    }
    
    private static void validateQualifiedName(final String name, String uri, final boolean isAttr) {
        assert name != null;
        if (uri == null) {
            uri = "";
        }
        final int i = name.indexOf(58);
        String local;
        if (i < 0) {
            local = name;
            validateNcName(name);
            if (isAttr && local.equals("xmlns") && !uri.equals("http://www.w3.org/2000/xmlns/")) {
                throw new NamespaceErr("Default xmlns attribute does not have namespace: http://www.w3.org/2000/xmlns/");
            }
        }
        else {
            if (i == 0) {
                throw new NamespaceErr("Invalid qualified name, no prefix specified");
            }
            final String prefix = name.substring(0, i);
            validateNcName(prefix);
            if (uri.length() == 0) {
                throw new NamespaceErr("Attempt to give a prefix for no namespace");
            }
            local = name.substring(i + 1);
            if (local.indexOf(58) >= 0) {
                throw new NamespaceErr("Invalid qualified name, more than one colon");
            }
            validateNcName(local);
            if (prefix.equals("xml") && !uri.equals("http://www.w3.org/XML/1998/namespace")) {
                throw new NamespaceErr("Invalid prefix - begins with 'xml'");
            }
        }
        if (local.length() == 0) {
            throw new NamespaceErr("Invalid qualified name, no local part specified");
        }
    }
    
    private static void removeNode(final Dom n) {
        assert n.nodeType() != 3 && n.nodeType() != 4;
        final Cur cFrom = n.tempCur();
        cFrom.toEnd();
        if (cFrom.next()) {
            final CharNode fromNodes = cFrom.getCharNodes();
            if (fromNodes != null) {
                cFrom.setCharNodes(null);
                final Cur cTo = n.tempCur();
                cTo.setCharNodes(CharNode.appendNodes(cTo.getCharNodes(), fromNodes));
                cTo.release();
            }
        }
        cFrom.release();
        Cur.moveNode((Xobj)n, null);
    }
    
    public static Document _domImplementation_createDocument(final Locale l, final String u, final String n, final DocumentType t) {
        if (l.noSync()) {
            l.enter();
            try {
                return domImplementation_createDocument(l, u, n, t);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return domImplementation_createDocument(l, u, n, t);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Document domImplementation_createDocument(final Locale l, final String namespaceURI, final String qualifiedName, final DocumentType doctype) {
        validateQualifiedName(qualifiedName, namespaceURI, false);
        final Cur c = l.tempCur();
        c.createDomDocumentRoot();
        final Document doc = (Document)c.getDom();
        c.next();
        c.createElement(l.makeQualifiedQName(namespaceURI, qualifiedName));
        if (doctype != null) {
            throw new RuntimeException("Not impl");
        }
        c.toParent();
        try {
            Locale.autoTypeDocument(c, null, null);
        }
        catch (final XmlException e) {
            throw new XmlRuntimeException(e);
        }
        c.release();
        return doc;
    }
    
    public static boolean _domImplementation_hasFeature(final Locale l, final String feature, final String version) {
        return feature != null && (version == null || version.length() <= 0 || version.equals("1.0") || version.equals("2.0")) && (feature.equalsIgnoreCase("core") || feature.equalsIgnoreCase("xml"));
    }
    
    public static Element _document_getDocumentElement(final Dom d) {
        final Locale l = d.locale();
        Dom e;
        if (l.noSync()) {
            l.enter();
            try {
                e = document_getDocumentElement(d);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    e = document_getDocumentElement(d);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Element)e;
    }
    
    public static Dom document_getDocumentElement(Dom d) {
        for (d = firstChild(d); d != null; d = nextSibling(d)) {
            if (d.nodeType() == 1) {
                return d;
            }
        }
        return null;
    }
    
    public static DocumentFragment _document_createDocumentFragment(final Dom d) {
        final Locale l = d.locale();
        Dom f;
        if (l.noSync()) {
            l.enter();
            try {
                f = document_createDocumentFragment(d);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    f = document_createDocumentFragment(d);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (DocumentFragment)f;
    }
    
    public static Dom document_createDocumentFragment(final Dom d) {
        final Cur c = d.locale().tempCur();
        c.createDomDocFragRoot();
        final Dom f = c.getDom();
        c.release();
        return f;
    }
    
    public static Element _document_createElement(final Dom d, final String name) {
        final Locale l = d.locale();
        Dom e;
        if (l.noSync()) {
            l.enter();
            try {
                e = document_createElement(d, name);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    e = document_createElement(d, name);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Element)e;
    }
    
    public static Dom document_createElement(final Dom d, final String name) {
        validateName(name);
        final Locale l = d.locale();
        final Cur c = l.tempCur();
        c.createElement(l.makeQualifiedQName("", name));
        final Dom e = c.getDom();
        c.release();
        ((Xobj.ElementXobj)e)._canHavePrefixUri = false;
        return e;
    }
    
    public static Element _document_createElementNS(final Dom d, final String uri, final String qname) {
        final Locale l = d.locale();
        Dom e;
        if (l.noSync()) {
            l.enter();
            try {
                e = document_createElementNS(d, uri, qname);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    e = document_createElementNS(d, uri, qname);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Element)e;
    }
    
    public static Dom document_createElementNS(final Dom d, final String uri, final String qname) {
        validateQualifiedName(qname, uri, false);
        final Locale l = d.locale();
        final Cur c = l.tempCur();
        c.createElement(l.makeQualifiedQName(uri, qname));
        final Dom e = c.getDom();
        c.release();
        return e;
    }
    
    public static Attr _document_createAttribute(final Dom d, final String name) {
        final Locale l = d.locale();
        Dom a;
        if (l.noSync()) {
            l.enter();
            try {
                a = document_createAttribute(d, name);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    a = document_createAttribute(d, name);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Attr)a;
    }
    
    public static Dom document_createAttribute(final Dom d, final String name) {
        validateName(name);
        final Locale l = d.locale();
        final Cur c = l.tempCur();
        c.createAttr(l.makeQualifiedQName("", name));
        final Dom e = c.getDom();
        c.release();
        ((Xobj.AttrXobj)e)._canHavePrefixUri = false;
        return e;
    }
    
    public static Attr _document_createAttributeNS(final Dom d, final String uri, final String qname) {
        final Locale l = d.locale();
        Dom a;
        if (l.noSync()) {
            l.enter();
            try {
                a = document_createAttributeNS(d, uri, qname);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    a = document_createAttributeNS(d, uri, qname);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Attr)a;
    }
    
    public static Dom document_createAttributeNS(final Dom d, final String uri, final String qname) {
        validateQualifiedName(qname, uri, true);
        final Locale l = d.locale();
        final Cur c = l.tempCur();
        c.createAttr(l.makeQualifiedQName(uri, qname));
        final Dom e = c.getDom();
        c.release();
        return e;
    }
    
    public static Comment _document_createComment(final Dom d, final String data) {
        final Locale l = d.locale();
        Dom c;
        if (l.noSync()) {
            l.enter();
            try {
                c = document_createComment(d, data);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    c = document_createComment(d, data);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Comment)c;
    }
    
    public static Dom document_createComment(final Dom d, final String data) {
        final Locale l = d.locale();
        final Cur c = l.tempCur();
        c.createComment();
        final Dom comment = c.getDom();
        if (data != null) {
            c.next();
            c.insertString(data);
        }
        c.release();
        return comment;
    }
    
    public static ProcessingInstruction _document_createProcessingInstruction(final Dom d, final String target, final String data) {
        final Locale l = d.locale();
        Dom pi;
        if (l.noSync()) {
            l.enter();
            try {
                pi = document_createProcessingInstruction(d, target, data);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    pi = document_createProcessingInstruction(d, target, data);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (ProcessingInstruction)pi;
    }
    
    public static Dom document_createProcessingInstruction(final Dom d, final String target, final String data) {
        if (target == null) {
            throw new IllegalArgumentException("Target is null");
        }
        if (target.length() == 0) {
            throw new IllegalArgumentException("Target is empty");
        }
        if (!XMLChar.isValidName(target)) {
            throw new InvalidCharacterError("Target has an invalid character");
        }
        if (Locale.beginsWithXml(target) && target.length() == 3) {
            throw new InvalidCharacterError("Invalid target - is 'xml'");
        }
        final Locale l = d.locale();
        final Cur c = l.tempCur();
        c.createProcinst(target);
        final Dom pi = c.getDom();
        if (data != null) {
            c.next();
            c.insertString(data);
        }
        c.release();
        return pi;
    }
    
    public static CDATASection _document_createCDATASection(final Dom d, final String data) {
        return (CDATASection)document_createCDATASection(d, data);
    }
    
    public static Dom document_createCDATASection(final Dom d, String data) {
        final TextNode t = d.locale().createCdataNode();
        if (data == null) {
            data = "";
        }
        t.setChars(data, 0, data.length());
        return t;
    }
    
    public static Text _document_createTextNode(final Dom d, final String data) {
        return (Text)document_createTextNode(d, data);
    }
    
    public static CharNode document_createTextNode(final Dom d, String data) {
        final TextNode t = d.locale().createTextNode();
        if (data == null) {
            data = "";
        }
        t.setChars(data, 0, data.length());
        return t;
    }
    
    public static EntityReference _document_createEntityReference(final Dom d, final String name) {
        throw new RuntimeException("Not implemented");
    }
    
    public static Element _document_getElementById(final Dom d, final String elementId) {
        throw new RuntimeException("Not implemented");
    }
    
    public static NodeList _document_getElementsByTagName(final Dom d, final String name) {
        final Locale l = d.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return document_getElementsByTagName(d, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return document_getElementsByTagName(d, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static NodeList document_getElementsByTagName(final Dom d, final String name) {
        return new ElementsByTagNameNodeList(d, name);
    }
    
    public static NodeList _document_getElementsByTagNameNS(final Dom d, final String uri, final String local) {
        final Locale l = d.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return document_getElementsByTagNameNS(d, uri, local);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return document_getElementsByTagNameNS(d, uri, local);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static NodeList document_getElementsByTagNameNS(final Dom d, final String uri, final String local) {
        return new ElementsByTagNameNSNodeList(d, uri, local);
    }
    
    public static DOMImplementation _document_getImplementation(final Dom d) {
        return d.locale();
    }
    
    public static Node _document_importNode(final Dom d, final Node n, final boolean deep) {
        final Locale l = d.locale();
        Dom i;
        if (l.noSync()) {
            l.enter();
            try {
                i = document_importNode(d, n, deep);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    i = document_importNode(d, n, deep);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)i;
    }
    
    public static Dom document_importNode(final Dom d, final Node n, final boolean deep) {
        if (n == null) {
            return null;
        }
        boolean copyChildren = false;
        Dom i = null;
        switch (n.getNodeType()) {
            case 9: {
                throw new NotSupportedError("Document nodes may not be imported");
            }
            case 10: {
                throw new NotSupportedError("Document type nodes may not be imported");
            }
            case 1: {
                final String local = n.getLocalName();
                if (local == null || local.length() == 0) {
                    i = document_createElement(d, n.getNodeName());
                }
                else {
                    final String prefix = n.getPrefix();
                    final String name = (prefix == null || prefix.length() == 0) ? local : (prefix + ":" + local);
                    final String uri = n.getNamespaceURI();
                    if (uri == null || uri.length() == 0) {
                        i = document_createElement(d, name);
                    }
                    else {
                        i = document_createElementNS(d, uri, name);
                    }
                }
                final NamedNodeMap attrs = n.getAttributes();
                for (int a = 0; a < attrs.getLength(); ++a) {
                    attributes_setNamedItem(i, document_importNode(d, attrs.item(a), true));
                }
                copyChildren = deep;
                break;
            }
            case 2: {
                final String local = n.getLocalName();
                if (local == null || local.length() == 0) {
                    i = document_createAttribute(d, n.getNodeName());
                }
                else {
                    final String prefix = n.getPrefix();
                    final String name = (prefix == null || prefix.length() == 0) ? local : (prefix + ":" + local);
                    final String uri = n.getNamespaceURI();
                    if (uri == null || uri.length() == 0) {
                        i = document_createAttribute(d, name);
                    }
                    else {
                        i = document_createAttributeNS(d, uri, name);
                    }
                }
                copyChildren = true;
                break;
            }
            case 11: {
                i = document_createDocumentFragment(d);
                copyChildren = deep;
                break;
            }
            case 7: {
                i = document_createProcessingInstruction(d, n.getNodeName(), n.getNodeValue());
                break;
            }
            case 8: {
                i = document_createComment(d, n.getNodeValue());
                break;
            }
            case 3: {
                i = document_createTextNode(d, n.getNodeValue());
                break;
            }
            case 4: {
                i = document_createCDATASection(d, n.getNodeValue());
                break;
            }
            case 5:
            case 6:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown kind");
            }
        }
        if (copyChildren) {
            final NodeList children = n.getChildNodes();
            for (int c = 0; c < children.getLength(); ++c) {
                node_insertBefore(i, document_importNode(d, children.item(c), true), null);
            }
        }
        return i;
    }
    
    public static DocumentType _document_getDoctype(final Dom d) {
        final Locale l = d.locale();
        Dom dt;
        if (l.noSync()) {
            l.enter();
            try {
                dt = document_getDoctype(d);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    dt = document_getDoctype(d);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (DocumentType)dt;
    }
    
    public static Dom document_getDoctype(final Dom d) {
        return null;
    }
    
    public static Document _node_getOwnerDocument(final Dom n) {
        final Locale l = n.locale();
        Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = node_getOwnerDocument(n);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = node_getOwnerDocument(n);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Document)d;
    }
    
    public static Dom node_getOwnerDocument(final Dom n) {
        if (n.nodeType() == 9) {
            return null;
        }
        final Locale l = n.locale();
        if (l._ownerDoc == null) {
            final Cur c = l.tempCur();
            c.createDomDocumentRoot();
            l._ownerDoc = c.getDom();
            c.release();
        }
        return l._ownerDoc;
    }
    
    public static Node _node_getParentNode(final Dom n) {
        final Locale l = n.locale();
        Dom p;
        if (l.noSync()) {
            l.enter();
            try {
                p = node_getParentNode(n);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    p = node_getParentNode(n);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)p;
    }
    
    public static Dom node_getParentNode(final Dom n) {
        Cur c = null;
        switch (n.nodeType()) {
            case 2:
            case 9:
            case 11: {
                break;
            }
            case 1:
            case 7:
            case 8: {
                if (!(c = n.tempCur()).toParentRaw()) {
                    c.release();
                    c = null;
                    break;
                }
                break;
            }
            case 3:
            case 4: {
                if ((c = n.tempCur()) != null) {
                    c.toParent();
                    break;
                }
                break;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown kind");
            }
        }
        if (c == null) {
            return null;
        }
        final Dom d = c.getDom();
        c.release();
        return d;
    }
    
    public static Node _node_getFirstChild(final Dom n) {
        final Locale l = n.locale();
        assert n instanceof Xobj;
        final Xobj node = (Xobj)n;
        if (!node.isVacant()) {
            if (node.isFirstChildPtrDomUsable()) {
                return (Node)node._firstChild;
            }
            final Xobj lastAttr = node.lastAttr();
            if (lastAttr != null && lastAttr.isNextSiblingPtrDomUsable()) {
                return (Xobj.NodeXobj)lastAttr._nextSibling;
            }
            if (node.isExistingCharNodesValueUsable()) {
                return node._charNodesValue;
            }
        }
        Dom fc;
        if (l.noSync()) {
            fc = node_getFirstChild(n);
        }
        else {
            synchronized (l) {
                fc = node_getFirstChild(n);
            }
        }
        return (Node)fc;
    }
    
    public static Dom node_getFirstChild(final Dom n) {
        final Dom fc = null;
        switch (n.nodeType()) {
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            case 1:
            case 2:
            case 9:
            case 11: {
                final Xobj node = (Xobj)n;
                node.ensureOccupancy();
                if (node.isFirstChildPtrDomUsable()) {
                    return (Xobj.NodeXobj)node._firstChild;
                }
                final Xobj lastAttr = node.lastAttr();
                if (lastAttr != null) {
                    if (lastAttr.isNextSiblingPtrDomUsable()) {
                        return (Xobj.NodeXobj)lastAttr._nextSibling;
                    }
                    if (lastAttr.isCharNodesAfterUsable()) {
                        return lastAttr._charNodesAfter;
                    }
                }
                if (node.isCharNodesValueUsable()) {
                    return node._charNodesValue;
                }
                break;
            }
        }
        return fc;
    }
    
    public static Node _node_getLastChild(final Dom n) {
        final Locale l = n.locale();
        Dom lc;
        if (l.noSync()) {
            l.enter();
            try {
                lc = node_getLastChild(n);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    lc = node_getLastChild(n);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)lc;
    }
    
    public static Dom node_getLastChild(final Dom n) {
        switch (n.nodeType()) {
            case 3:
            case 4:
            case 7:
            case 8: {
                return null;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                Dom lc = null;
                final Cur c = n.tempCur();
                CharNode nodes;
                if (c.toLastChild()) {
                    lc = c.getDom();
                    c.skip();
                    if ((nodes = c.getCharNodes()) != null) {
                        lc = null;
                    }
                }
                else {
                    c.next();
                    nodes = c.getCharNodes();
                }
                if (lc == null && nodes != null) {
                    while (nodes._next != null) {
                        nodes = nodes._next;
                    }
                    lc = nodes;
                }
                c.release();
                return lc;
            }
        }
    }
    
    public static Node _node_getNextSibling(final Dom n) {
        final Locale l = n.locale();
        Dom ns;
        if (l.noSync()) {
            ns = node_getNextSibling(n);
        }
        else {
            synchronized (l) {
                ns = node_getNextSibling(n);
            }
        }
        return (Node)ns;
    }
    
    public static Dom node_getNextSibling(final Dom n) {
        Dom ns = null;
        switch (n.nodeType()) {
            case 3:
            case 4: {
                final CharNode cn = (CharNode)n;
                if (!(cn._src instanceof Xobj)) {
                    return null;
                }
                final Xobj src = (Xobj)cn._src;
                src._charNodesAfter = Cur.updateCharNodes(src._locale, src, src._charNodesAfter, src._cchAfter);
                src._charNodesValue = Cur.updateCharNodes(src._locale, src, src._charNodesValue, src._cchValue);
                if (cn._next != null) {
                    ns = cn._next;
                    break;
                }
                final boolean isThisNodeAfterText = cn.isNodeAftertext();
                if (isThisNodeAfterText) {
                    ns = (Xobj.NodeXobj)src._nextSibling;
                    break;
                }
                ns = (Xobj.NodeXobj)src._firstChild;
                break;
            }
            case 1:
            case 7:
            case 8: {
                assert n instanceof Xobj : "PI, Comments and Elements always backed up by Xobj";
                final Xobj node = (Xobj)n;
                node.ensureOccupancy();
                if (node.isNextSiblingPtrDomUsable()) {
                    return (Xobj.NodeXobj)node._nextSibling;
                }
                if (node.isCharNodesAfterUsable()) {
                    return node._charNodesAfter;
                }
                break;
            }
            case 5:
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not implemented");
            }
        }
        return ns;
    }
    
    public static Node _node_getPreviousSibling(final Dom n) {
        final Locale l = n.locale();
        Dom ps;
        if (l.noSync()) {
            ps = node_getPreviousSibling(n);
        }
        else {
            synchronized (l) {
                ps = node_getPreviousSibling(n);
            }
        }
        return (Node)ps;
    }
    
    public static Dom node_getPreviousSibling(final Dom n) {
        Dom prev = null;
        switch (n.nodeType()) {
            case 3:
            case 4: {
                assert n instanceof CharNode : "Text/CData should be a CharNode";
                final CharNode node = (CharNode)n;
                if (!(node._src instanceof Xobj)) {
                    return null;
                }
                final Xobj src = (Xobj)node._src;
                src.ensureOccupancy();
                final boolean isThisNodeAfterText = node.isNodeAftertext();
                prev = node._prev;
                if (prev == null) {
                    prev = (isThisNodeAfterText ? ((Dom)src) : src._charNodesValue);
                    break;
                }
                break;
            }
            default: {
                assert n instanceof Xobj;
                final Xobj node2 = (Xobj)n;
                prev = (Dom)node2._prevSibling;
                if (prev == null && node2._parent != null) {
                    prev = node_getFirstChild((Dom)node2._parent);
                    break;
                }
                break;
            }
        }
        Dom temp = prev;
        while (temp != null && (temp = node_getNextSibling(temp)) != n) {
            prev = temp;
        }
        return prev;
    }
    
    public static boolean _node_hasAttributes(final Dom n) {
        final Locale l = n.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return node_hasAttributes(n);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return node_hasAttributes(n);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static boolean node_hasAttributes(final Dom n) {
        boolean hasAttrs = false;
        if (n.nodeType() == 1) {
            final Cur c = n.tempCur();
            hasAttrs = c.hasAttrs();
            c.release();
        }
        return hasAttrs;
    }
    
    public static boolean _node_isSupported(final Dom n, final String feature, final String version) {
        return _domImplementation_hasFeature(n.locale(), feature, version);
    }
    
    public static void _node_normalize(final Dom n) {
        final Locale l = n.locale();
        if (l.noSync()) {
            l.enter();
            try {
                node_normalize(n);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    node_normalize(n);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void node_normalize(final Dom n) {
        switch (n.nodeType()) {
            case 3:
            case 4:
            case 7:
            case 8: {
                return;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                final Cur c = n.tempCur();
                c.push();
                do {
                    c.nextWithAttrs();
                    CharNode cn = c.getCharNodes();
                    if (cn != null) {
                        if (!c.isText()) {
                            while (cn != null) {
                                cn.setChars(null, 0, 0);
                                cn = CharNode.remove(cn, cn);
                            }
                        }
                        else if (cn._next != null) {
                            while (cn._next != null) {
                                cn.setChars(null, 0, 0);
                                cn = CharNode.remove(cn, cn._next);
                            }
                            cn._cch = Integer.MAX_VALUE;
                        }
                        c.setCharNodes(cn);
                    }
                } while (!c.isAtEndOfLastPush());
                c.release();
                n.locale().invalidateDomCaches(n);
            }
        }
    }
    
    public static boolean _node_hasChildNodes(final Dom n) {
        return _node_getFirstChild(n) != null;
    }
    
    public static Node _node_appendChild(final Dom p, final Node newChild) {
        return _node_insertBefore(p, newChild, null);
    }
    
    public static Node _node_replaceChild(final Dom p, final Node newChild, final Node oldChild) {
        final Locale l = p.locale();
        if (newChild == null) {
            throw new IllegalArgumentException("Child to add is null");
        }
        if (oldChild == null) {
            throw new NotFoundErr("Child to replace is null");
        }
        final Dom nc;
        if (!(newChild instanceof Dom) || (nc = (Dom)newChild).locale() != l) {
            throw new WrongDocumentErr("Child to add is from another document");
        }
        Dom oc = null;
        if (!(oldChild instanceof Dom) || (oc = (Dom)oldChild).locale() != l) {
            throw new WrongDocumentErr("Child to replace is from another document");
        }
        Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = node_replaceChild(p, nc, oc);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = node_replaceChild(p, nc, oc);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)d;
    }
    
    public static Dom node_replaceChild(final Dom p, final Dom newChild, final Dom oldChild) {
        final Dom nextNode = node_getNextSibling(oldChild);
        node_removeChild(p, oldChild);
        try {
            node_insertBefore(p, newChild, nextNode);
        }
        catch (final DOMException e) {
            node_insertBefore(p, oldChild, nextNode);
            throw e;
        }
        return oldChild;
    }
    
    public static Node _node_insertBefore(final Dom p, final Node newChild, final Node refChild) {
        final Locale l = p.locale();
        if (newChild == null) {
            throw new IllegalArgumentException("Child to add is null");
        }
        final Dom nc;
        if (!(newChild instanceof Dom) || (nc = (Dom)newChild).locale() != l) {
            throw new WrongDocumentErr("Child to add is from another document");
        }
        Dom rc = null;
        if (refChild != null && (!(refChild instanceof Dom) || (rc = (Dom)refChild).locale() != l)) {
            throw new WrongDocumentErr("Reference child is from another document");
        }
        Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = node_insertBefore(p, nc, rc);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = node_insertBefore(p, nc, rc);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)d;
    }
    
    public static Dom node_insertBefore(final Dom p, final Dom nc, Dom rc) {
        assert nc != null;
        if (nc == rc) {
            return nc;
        }
        if (rc != null && parent(rc) != p) {
            throw new NotFoundErr("RefChild is not a child of this node");
        }
        final int nck = nc.nodeType();
        if (nck == 11) {
            for (Dom c = firstChild(nc); c != null; c = nextSibling(c)) {
                validateNewChild(p, c);
            }
            Dom n;
            for (Dom c = firstChild(nc); c != null; c = n) {
                n = nextSibling(c);
                if (rc == null) {
                    append(c, p);
                }
                else {
                    insert(c, rc);
                }
            }
            return nc;
        }
        validateNewChild(p, nc);
        remove(nc);
        final int pk = p.nodeType();
        assert pk == 1;
        switch (nck) {
            case 1:
            case 7:
            case 8: {
                if (rc == null) {
                    final Cur cTo = p.tempCur();
                    cTo.toEnd();
                    Cur.moveNode((Xobj)nc, cTo);
                    cTo.release();
                    break;
                }
                final int rck = rc.nodeType();
                if (rck == 3 || rck == 4) {
                    final ArrayList charNodes = new ArrayList();
                    while (rc != null && (rc.nodeType() == 3 || rc.nodeType() == 4)) {
                        final Dom next = nextSibling(rc);
                        charNodes.add(remove(rc));
                        rc = next;
                    }
                    if (rc == null) {
                        append(nc, p);
                    }
                    else {
                        insert(nc, rc);
                    }
                    rc = nextSibling(nc);
                    for (int i = 0; i < charNodes.size(); ++i) {
                        final Dom n2 = charNodes.get(i);
                        if (rc == null) {
                            append(n2, p);
                        }
                        else {
                            insert(n2, rc);
                        }
                    }
                }
                else {
                    if (rck == 5) {
                        throw new RuntimeException("Not implemented");
                    }
                    assert rck == 8;
                    final Cur cTo2 = rc.tempCur();
                    Cur.moveNode((Xobj)nc, cTo2);
                    cTo2.release();
                }
                break;
            }
            case 3:
            case 4: {
                final CharNode n3 = (CharNode)nc;
                assert n3._prev == null && n3._next == null;
                CharNode refCharNode = null;
                final Cur c2 = p.tempCur();
                if (rc == null) {
                    c2.toEnd();
                }
                else {
                    final int rck2 = rc.nodeType();
                    if (rck2 == 3 || rck2 == 4) {
                        c2.moveToCharNode(refCharNode = (CharNode)rc);
                    }
                    else {
                        if (rck2 == 5) {
                            throw new RuntimeException("Not implemented");
                        }
                        c2.moveToDom(rc);
                    }
                }
                CharNode nodes = c2.getCharNodes();
                nodes = CharNode.insertNode(nodes, n3, refCharNode);
                c2.insertChars(n3._src, n3._off, n3._cch);
                c2.setCharNodes(nodes);
                c2.release();
                break;
            }
            case 5: {
                throw new RuntimeException("Not implemented");
            }
            case 10: {
                throw new RuntimeException("Not implemented");
            }
            default: {
                throw new RuntimeException("Unexpected child node type");
            }
        }
        return nc;
    }
    
    public static Node _node_removeChild(final Dom p, final Node child) {
        final Locale l = p.locale();
        if (child == null) {
            throw new NotFoundErr("Child to remove is null");
        }
        final Dom c;
        if (!(child instanceof Dom) || (c = (Dom)child).locale() != l) {
            throw new WrongDocumentErr("Child to remove is from another document");
        }
        Dom d;
        if (l.noSync()) {
            l.enter();
            try {
                d = node_removeChild(p, c);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    d = node_removeChild(p, c);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)d;
    }
    
    public static Dom node_removeChild(final Dom parent, final Dom child) {
        if (parent(child) != parent) {
            throw new NotFoundErr("Child to remove is not a child of given parent");
        }
        switch (child.nodeType()) {
            case 2:
            case 9:
            case 11: {
                throw new IllegalStateException();
            }
            case 1:
            case 7:
            case 8: {
                removeNode(child);
                break;
            }
            case 3:
            case 4: {
                final Cur c = child.tempCur();
                final CharNode nodes = c.getCharNodes();
                final CharNode cn = (CharNode)child;
                assert cn._src instanceof Dom;
                cn.setChars(c.moveChars(null, cn._cch), c._offSrc, c._cchSrc);
                c.setCharNodes(CharNode.remove(nodes, cn));
                c.release();
                break;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown kind");
            }
        }
        return child;
    }
    
    public static Node _node_cloneNode(final Dom n, final boolean deep) {
        final Locale l = n.locale();
        Dom c;
        if (l.noSync()) {
            l.enter();
            try {
                c = node_cloneNode(n, deep);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    c = node_cloneNode(n, deep);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)c;
    }
    
    public static Dom node_cloneNode(final Dom n, final boolean deep) {
        final Locale l = n.locale();
        Dom clone = null;
        if (!deep) {
            Cur shallow = null;
            switch (n.nodeType()) {
                case 9: {
                    shallow = l.tempCur();
                    shallow.createDomDocumentRoot();
                    break;
                }
                case 11: {
                    shallow = l.tempCur();
                    shallow.createDomDocFragRoot();
                    break;
                }
                case 1: {
                    shallow = l.tempCur();
                    shallow.createElement(n.getQName());
                    final Element elem = (Element)shallow.getDom();
                    final NamedNodeMap attrs = ((Element)n).getAttributes();
                    for (int i = 0; i < attrs.getLength(); ++i) {
                        elem.setAttributeNodeNS((Attr)attrs.item(i).cloneNode(true));
                    }
                    break;
                }
                case 2: {
                    shallow = l.tempCur();
                    shallow.createAttr(n.getQName());
                    break;
                }
            }
            if (shallow != null) {
                clone = shallow.getDom();
                shallow.release();
            }
        }
        if (clone == null) {
            switch (n.nodeType()) {
                case 1:
                case 2:
                case 7:
                case 8:
                case 9:
                case 11: {
                    final Cur cClone = l.tempCur();
                    final Cur cSrc = n.tempCur();
                    cSrc.copyNode(cClone);
                    clone = cClone.getDom();
                    cClone.release();
                    cSrc.release();
                    break;
                }
                case 3:
                case 4: {
                    final Cur c = n.tempCur();
                    final CharNode cn = (n.nodeType() == 3) ? l.createTextNode() : l.createCdataNode();
                    cn.setChars(c.getChars(((CharNode)n)._cch), c._offSrc, c._cchSrc);
                    clone = cn;
                    c.release();
                    break;
                }
                case 5:
                case 6:
                case 10:
                case 12: {
                    throw new RuntimeException("Not impl");
                }
                default: {
                    throw new RuntimeException("Unknown kind");
                }
            }
        }
        return clone;
    }
    
    public static String _node_getLocalName(final Dom n) {
        if (!n.nodeCanHavePrefixUri()) {
            return null;
        }
        final QName name = n.getQName();
        return (name == null) ? "" : name.getLocalPart();
    }
    
    public static String _node_getNamespaceURI(final Dom n) {
        if (!n.nodeCanHavePrefixUri()) {
            return null;
        }
        final QName name = n.getQName();
        return (name == null) ? "" : name.getNamespaceURI();
    }
    
    public static void _node_setPrefix(final Dom n, final String prefix) {
        final Locale l = n.locale();
        if (l.noSync()) {
            l.enter();
            try {
                node_setPrefix(n, prefix);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    node_setPrefix(n, prefix);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void node_setPrefix(final Dom n, String prefix) {
        if (n.nodeType() == 1 || n.nodeType() == 2) {
            final Cur c = n.tempCur();
            final QName name = c.getName();
            final String uri = name.getNamespaceURI();
            final String local = name.getLocalPart();
            prefix = validatePrefix(prefix, uri, local, n.nodeType() == 2);
            c.setName(n.locale().makeQName(uri, local, prefix));
            c.release();
        }
        else {
            validatePrefix(prefix, "", "", false);
        }
    }
    
    public static String _node_getPrefix(final Dom n) {
        if (!n.nodeCanHavePrefixUri()) {
            return null;
        }
        final QName name = n.getQName();
        return (name == null) ? "" : name.getPrefix();
    }
    
    public static String _node_getNodeName(final Dom n) {
        switch (n.nodeType()) {
            case 4: {
                return "#cdata-section";
            }
            case 8: {
                return "#comment";
            }
            case 11: {
                return "#document-fragment";
            }
            case 9: {
                return "#document";
            }
            case 7: {
                return n.getQName().getLocalPart();
            }
            case 3: {
                return "#text";
            }
            case 1:
            case 2: {
                final QName name = n.getQName();
                final String prefix = name.getPrefix();
                return (prefix.length() == 0) ? name.getLocalPart() : (prefix + ":" + name.getLocalPart());
            }
            case 5:
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown node type");
            }
        }
    }
    
    public static short _node_getNodeType(final Dom n) {
        return (short)n.nodeType();
    }
    
    public static void _node_setNodeValue(final Dom n, final String nodeValue) {
        final Locale l = n.locale();
        if (l.noSync()) {
            l.enter();
            try {
                node_setNodeValue(n, nodeValue);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    node_setNodeValue(n, nodeValue);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void node_setNodeValue(final Dom n, String nodeValue) {
        if (nodeValue == null) {
            nodeValue = "";
        }
        switch (n.nodeType()) {
            case 3:
            case 4: {
                final CharNode cn = (CharNode)n;
                final Cur c;
                if ((c = cn.tempCur()) != null) {
                    c.moveChars(null, cn._cch);
                    cn._cch = nodeValue.length();
                    c.insertString(nodeValue);
                    c.release();
                    break;
                }
                cn.setChars(nodeValue, 0, nodeValue.length());
                break;
            }
            case 2: {
                final NodeList children = ((Node)n).getChildNodes();
                while (children.getLength() > 1) {
                    node_removeChild(n, (Dom)children.item(1));
                }
                if (children.getLength() == 0) {
                    final TextNode tn = n.locale().createTextNode();
                    tn.setChars(nodeValue, 0, nodeValue.length());
                    node_insertBefore(n, tn, null);
                }
                else {
                    assert children.getLength() == 1;
                    children.item(0).setNodeValue(nodeValue);
                }
                if (((Xobj.AttrXobj)n).isId()) {
                    final Dom d = node_getOwnerDocument(n);
                    final String val = node_getNodeValue(n);
                    if (d instanceof Xobj.DocumentXobj) {
                        ((Xobj.DocumentXobj)d).removeIdElement(val);
                        ((Xobj.DocumentXobj)d).addIdElement(nodeValue, attr_getOwnerElement(n));
                    }
                    break;
                }
                break;
            }
            case 7:
            case 8: {
                final Cur c2 = n.tempCur();
                c2.next();
                c2.getChars(-1);
                c2.moveChars(null, c2._cchSrc);
                c2.insertString(nodeValue);
                c2.release();
                break;
            }
        }
    }
    
    public static String _node_getNodeValue(final Dom n) {
        final Locale l = n.locale();
        if (l.noSync()) {
            return node_getNodeValue(n);
        }
        synchronized (l) {
            return node_getNodeValue(n);
        }
    }
    
    public static String node_getNodeValue(final Dom n) {
        String s = null;
        switch (n.nodeType()) {
            case 2:
            case 7:
            case 8: {
                s = ((Xobj)n).getValueAsString();
                break;
            }
            case 3:
            case 4: {
                assert n instanceof CharNode : "Text/CData should be a CharNode";
                final CharNode node = (CharNode)n;
                if (!(node._src instanceof Xobj)) {
                    s = CharUtil.getString(node._src, node._off, node._cch);
                    break;
                }
                final Xobj src = (Xobj)node._src;
                src.ensureOccupancy();
                final boolean isThisNodeAfterText = node.isNodeAftertext();
                if (isThisNodeAfterText) {
                    src._charNodesAfter = Cur.updateCharNodes(src._locale, src, src._charNodesAfter, src._cchAfter);
                    s = src.getCharsAfterAsString(node._off, node._cch);
                }
                else {
                    src._charNodesValue = Cur.updateCharNodes(src._locale, src, src._charNodesValue, src._cchValue);
                    s = src.getCharsValueAsString(node._off, node._cch);
                }
                break;
            }
        }
        return s;
    }
    
    public static Object _node_getUserData(final Dom n, final String key) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static Object _node_setUserData(final Dom n, final String key, final Object data, final UserDataHandler handler) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static Object _node_getFeature(final Dom n, final String feature, final String version) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static boolean _node_isEqualNode(final Dom n, final Node arg) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static boolean _node_isSameNode(final Dom n, final Node arg) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static String _node_lookupNamespaceURI(final Dom n, final String prefix) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static boolean _node_isDefaultNamespace(final Dom n, final String namespaceURI) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static String _node_lookupPrefix(final Dom n, final String namespaceURI) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static void _node_setTextContent(final Dom n, final String textContent) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static String _node_getTextContent(final Dom n) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static short _node_compareDocumentPosition(final Dom n, final Node other) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static String _node_getBaseURI(final Dom n) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static Node _childNodes_item(final Dom n, final int i) {
        final Locale l = n.locale();
        if (i == 0) {
            return _node_getFirstChild(n);
        }
        Dom d;
        if (l.noSync()) {
            d = childNodes_item(n, i);
        }
        else {
            synchronized (l) {
                d = childNodes_item(n, i);
            }
        }
        return (Node)d;
    }
    
    public static Dom childNodes_item(final Dom n, final int i) {
        if (i < 0) {
            return null;
        }
        switch (n.nodeType()) {
            case 3:
            case 4:
            case 7:
            case 8: {
                return null;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                if (i == 0) {
                    return node_getFirstChild(n);
                }
                return n.locale().findDomNthChild(n, i);
            }
        }
    }
    
    public static int _childNodes_getLength(final Dom n) {
        final Locale l = n.locale();
        assert n instanceof Xobj;
        final Xobj node = (Xobj)n;
        final int count;
        if (!node.isVacant() && (count = node.getDomZeroOneChildren()) < 2) {
            return count;
        }
        if (l.noSync()) {
            return childNodes_getLength(n);
        }
        synchronized (l) {
            return childNodes_getLength(n);
        }
    }
    
    public static int childNodes_getLength(final Dom n) {
        switch (n.nodeType()) {
            case 3:
            case 4:
            case 7:
            case 8: {
                return 0;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                assert n instanceof Xobj;
                final Xobj node = (Xobj)n;
                node.ensureOccupancy();
                final int count;
                if ((count = node.getDomZeroOneChildren()) < 2) {
                    return count;
                }
                return n.locale().domLength(n);
            }
        }
    }
    
    public static String _element_getTagName(final Dom e) {
        return _node_getNodeName(e);
    }
    
    public static Attr _element_getAttributeNode(final Dom e, final String name) {
        return (Attr)_attributes_getNamedItem(e, name);
    }
    
    public static Attr _element_getAttributeNodeNS(final Dom e, final String uri, final String local) {
        return (Attr)_attributes_getNamedItemNS(e, uri, local);
    }
    
    public static Attr _element_setAttributeNode(final Dom e, final Attr newAttr) {
        return (Attr)_attributes_setNamedItem(e, newAttr);
    }
    
    public static Attr _element_setAttributeNodeNS(final Dom e, final Attr newAttr) {
        return (Attr)_attributes_setNamedItemNS(e, newAttr);
    }
    
    public static String _element_getAttribute(final Dom e, final String name) {
        final Node a = _attributes_getNamedItem(e, name);
        return (a == null) ? "" : a.getNodeValue();
    }
    
    public static String _element_getAttributeNS(final Dom e, final String uri, final String local) {
        final Node a = _attributes_getNamedItemNS(e, uri, local);
        return (a == null) ? "" : a.getNodeValue();
    }
    
    public static boolean _element_hasAttribute(final Dom e, final String name) {
        return _attributes_getNamedItem(e, name) != null;
    }
    
    public static boolean _element_hasAttributeNS(final Dom e, final String uri, final String local) {
        return _attributes_getNamedItemNS(e, uri, local) != null;
    }
    
    public static void _element_removeAttribute(final Dom e, final String name) {
        try {
            _attributes_removeNamedItem(e, name);
        }
        catch (final NotFoundErr notFoundErr) {}
    }
    
    public static void _element_removeAttributeNS(final Dom e, final String uri, final String local) {
        try {
            _attributes_removeNamedItemNS(e, uri, local);
        }
        catch (final NotFoundErr notFoundErr) {}
    }
    
    public static Attr _element_removeAttributeNode(final Dom e, final Attr oldAttr) {
        if (oldAttr == null) {
            throw new NotFoundErr("Attribute to remove is null");
        }
        if (oldAttr.getOwnerElement() != e) {
            throw new NotFoundErr("Attribute to remove does not belong to this element");
        }
        return (Attr)_attributes_removeNamedItem(e, oldAttr.getNodeName());
    }
    
    public static void _element_setAttribute(final Dom e, final String name, final String value) {
        final Locale l = e.locale();
        if (l.noSync()) {
            l.enter();
            try {
                element_setAttribute(e, name, value);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    element_setAttribute(e, name, value);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void element_setAttribute(final Dom e, final String name, final String value) {
        Dom a = attributes_getNamedItem(e, name);
        if (a == null) {
            a = document_createAttribute(node_getOwnerDocument(e), name);
            attributes_setNamedItem(e, a);
        }
        node_setNodeValue(a, value);
    }
    
    public static void _element_setAttributeNS(final Dom e, final String uri, final String qname, final String value) {
        final Locale l = e.locale();
        if (l.noSync()) {
            l.enter();
            try {
                element_setAttributeNS(e, uri, qname, value);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    element_setAttributeNS(e, uri, qname, value);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void element_setAttributeNS(final Dom e, final String uri, final String qname, final String value) {
        validateQualifiedName(qname, uri, true);
        final QName name = e.locale().makeQualifiedQName(uri, qname);
        final String local = name.getLocalPart();
        final String prefix = validatePrefix(name.getPrefix(), uri, local, true);
        Dom a = attributes_getNamedItemNS(e, uri, local);
        if (a == null) {
            a = document_createAttributeNS(node_getOwnerDocument(e), uri, local);
            attributes_setNamedItemNS(e, a);
        }
        node_setPrefix(a, prefix);
        node_setNodeValue(a, value);
    }
    
    public static NodeList _element_getElementsByTagName(final Dom e, final String name) {
        final Locale l = e.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return element_getElementsByTagName(e, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return element_getElementsByTagName(e, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static NodeList element_getElementsByTagName(final Dom e, final String name) {
        return new ElementsByTagNameNodeList(e, name);
    }
    
    public static NodeList _element_getElementsByTagNameNS(final Dom e, final String uri, final String local) {
        final Locale l = e.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return element_getElementsByTagNameNS(e, uri, local);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return element_getElementsByTagNameNS(e, uri, local);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static NodeList element_getElementsByTagNameNS(final Dom e, final String uri, final String local) {
        return new ElementsByTagNameNSNodeList(e, uri, local);
    }
    
    public static int _attributes_getLength(final Dom e) {
        final Locale l = e.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return attributes_getLength(e);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return attributes_getLength(e);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static int attributes_getLength(final Dom e) {
        int n = 0;
        final Cur c = e.tempCur();
        while (c.toNextAttr()) {
            ++n;
        }
        c.release();
        return n;
    }
    
    public static Node _attributes_setNamedItem(final Dom e, final Node attr) {
        final Locale l = e.locale();
        if (attr == null) {
            throw new IllegalArgumentException("Attr to set is null");
        }
        final Dom a;
        if (!(attr instanceof Dom) || (a = (Dom)attr).locale() != l) {
            throw new WrongDocumentErr("Attr to set is from another document");
        }
        Dom oldA;
        if (l.noSync()) {
            l.enter();
            try {
                oldA = attributes_setNamedItem(e, a);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    oldA = attributes_setNamedItem(e, a);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)oldA;
    }
    
    public static Dom attributes_setNamedItem(final Dom e, final Dom a) {
        if (attr_getOwnerElement(a) != null) {
            throw new InuseAttributeError();
        }
        if (a.nodeType() != 2) {
            throw new HierarchyRequestErr("Node is not an attribute");
        }
        final String name = _node_getNodeName(a);
        Dom oldAttr = null;
        final Cur c = e.tempCur();
        while (c.toNextAttr()) {
            final Dom aa = c.getDom();
            if (_node_getNodeName(aa).equals(name)) {
                if (oldAttr == null) {
                    oldAttr = aa;
                }
                else {
                    removeNode(aa);
                    c.toPrevAttr();
                }
            }
        }
        if (oldAttr == null) {
            c.moveToDom(e);
            c.next();
            Cur.moveNode((Xobj)a, c);
        }
        else {
            c.moveToDom(oldAttr);
            Cur.moveNode((Xobj)a, c);
            removeNode(oldAttr);
        }
        c.release();
        return oldAttr;
    }
    
    public static Node _attributes_getNamedItem(final Dom e, final String name) {
        final Locale l = e.locale();
        Dom n;
        if (l.noSync()) {
            l.enter();
            try {
                n = attributes_getNamedItem(e, name);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    n = attributes_getNamedItem(e, name);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)n;
    }
    
    public static Dom attributes_getNamedItem(final Dom e, final String name) {
        Dom a = null;
        final Cur c = e.tempCur();
        while (c.toNextAttr()) {
            final Dom d = c.getDom();
            if (_node_getNodeName(d).equals(name)) {
                a = d;
                break;
            }
        }
        c.release();
        return a;
    }
    
    public static Node _attributes_getNamedItemNS(final Dom e, final String uri, final String local) {
        final Locale l = e.locale();
        Dom n;
        if (l.noSync()) {
            l.enter();
            try {
                n = attributes_getNamedItemNS(e, uri, local);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    n = attributes_getNamedItemNS(e, uri, local);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)n;
    }
    
    public static Dom attributes_getNamedItemNS(final Dom e, String uri, final String local) {
        if (uri == null) {
            uri = "";
        }
        Dom a = null;
        final Cur c = e.tempCur();
        while (c.toNextAttr()) {
            final Dom d = c.getDom();
            final QName n = d.getQName();
            if (n.getNamespaceURI().equals(uri) && n.getLocalPart().equals(local)) {
                a = d;
                break;
            }
        }
        c.release();
        return a;
    }
    
    public static Node _attributes_removeNamedItem(final Dom e, final String name) {
        final Locale l = e.locale();
        Dom n;
        if (l.noSync()) {
            l.enter();
            try {
                n = attributes_removeNamedItem(e, name);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    n = attributes_removeNamedItem(e, name);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)n;
    }
    
    public static Dom attributes_removeNamedItem(final Dom e, final String name) {
        Dom oldAttr = null;
        final Cur c = e.tempCur();
        while (c.toNextAttr()) {
            final Dom aa = c.getDom();
            if (_node_getNodeName(aa).equals(name)) {
                if (oldAttr == null) {
                    oldAttr = aa;
                }
                if (((Xobj.AttrXobj)aa).isId()) {
                    final Dom d = node_getOwnerDocument(aa);
                    final String val = node_getNodeValue(aa);
                    if (d instanceof Xobj.DocumentXobj) {
                        ((Xobj.DocumentXobj)d).removeIdElement(val);
                    }
                }
                removeNode(aa);
                c.toPrevAttr();
            }
        }
        c.release();
        if (oldAttr == null) {
            throw new NotFoundErr("Named item not found: " + name);
        }
        return oldAttr;
    }
    
    public static Node _attributes_removeNamedItemNS(final Dom e, final String uri, final String local) {
        final Locale l = e.locale();
        Dom n;
        if (l.noSync()) {
            l.enter();
            try {
                n = attributes_removeNamedItemNS(e, uri, local);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    n = attributes_removeNamedItemNS(e, uri, local);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)n;
    }
    
    public static Dom attributes_removeNamedItemNS(final Dom e, String uri, final String local) {
        if (uri == null) {
            uri = "";
        }
        Dom oldAttr = null;
        final Cur c = e.tempCur();
        while (c.toNextAttr()) {
            final Dom aa = c.getDom();
            final QName qn = aa.getQName();
            if (qn.getNamespaceURI().equals(uri) && qn.getLocalPart().equals(local)) {
                if (oldAttr == null) {
                    oldAttr = aa;
                }
                if (((Xobj.AttrXobj)aa).isId()) {
                    final Dom d = node_getOwnerDocument(aa);
                    final String val = node_getNodeValue(aa);
                    if (d instanceof Xobj.DocumentXobj) {
                        ((Xobj.DocumentXobj)d).removeIdElement(val);
                    }
                }
                removeNode(aa);
                c.toPrevAttr();
            }
        }
        c.release();
        if (oldAttr == null) {
            throw new NotFoundErr("Named item not found: uri=" + uri + ", local=" + local);
        }
        return oldAttr;
    }
    
    public static Node _attributes_setNamedItemNS(final Dom e, final Node attr) {
        final Locale l = e.locale();
        if (attr == null) {
            throw new IllegalArgumentException("Attr to set is null");
        }
        final Dom a;
        if (!(attr instanceof Dom) || (a = (Dom)attr).locale() != l) {
            throw new WrongDocumentErr("Attr to set is from another document");
        }
        Dom oldA;
        if (l.noSync()) {
            l.enter();
            try {
                oldA = attributes_setNamedItemNS(e, a);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    oldA = attributes_setNamedItemNS(e, a);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)oldA;
    }
    
    public static Dom attributes_setNamedItemNS(final Dom e, final Dom a) {
        final Dom owner = attr_getOwnerElement(a);
        if (owner == e) {
            return a;
        }
        if (owner != null) {
            throw new InuseAttributeError();
        }
        if (a.nodeType() != 2) {
            throw new HierarchyRequestErr("Node is not an attribute");
        }
        final QName name = a.getQName();
        Dom oldAttr = null;
        final Cur c = e.tempCur();
        while (c.toNextAttr()) {
            final Dom aa = c.getDom();
            if (aa.getQName().equals(name)) {
                if (oldAttr == null) {
                    oldAttr = aa;
                }
                else {
                    removeNode(aa);
                    c.toPrevAttr();
                }
            }
        }
        if (oldAttr == null) {
            c.moveToDom(e);
            c.next();
            Cur.moveNode((Xobj)a, c);
        }
        else {
            c.moveToDom(oldAttr);
            Cur.moveNode((Xobj)a, c);
            removeNode(oldAttr);
        }
        c.release();
        return oldAttr;
    }
    
    public static Node _attributes_item(final Dom e, final int index) {
        final Locale l = e.locale();
        Dom a;
        if (l.noSync()) {
            l.enter();
            try {
                a = attributes_item(e, index);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    a = attributes_item(e, index);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Node)a;
    }
    
    public static Dom attributes_item(final Dom e, int index) {
        if (index < 0) {
            return null;
        }
        final Cur c = e.tempCur();
        Dom a = null;
        while (c.toNextAttr()) {
            if (index-- == 0) {
                a = c.getDom();
                break;
            }
        }
        c.release();
        return a;
    }
    
    public static String _processingInstruction_getData(final Dom p) {
        return _node_getNodeValue(p);
    }
    
    public static String _processingInstruction_getTarget(final Dom p) {
        return _node_getNodeName(p);
    }
    
    public static void _processingInstruction_setData(final Dom p, final String data) {
        _node_setNodeValue(p, data);
    }
    
    public static boolean _attr_getSpecified(final Dom a) {
        return true;
    }
    
    public static Element _attr_getOwnerElement(final Dom a) {
        final Locale l = a.locale();
        Dom e;
        if (l.noSync()) {
            l.enter();
            try {
                e = attr_getOwnerElement(a);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    e = attr_getOwnerElement(a);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Element)e;
    }
    
    public static Dom attr_getOwnerElement(final Dom n) {
        final Cur c = n.tempCur();
        if (!c.toParentRaw()) {
            c.release();
            return null;
        }
        final Dom p = c.getDom();
        c.release();
        return p;
    }
    
    public static void _characterData_appendData(final Dom cd, final String arg) {
        if (arg != null && arg.length() != 0) {
            _node_setNodeValue(cd, _node_getNodeValue(cd) + arg);
        }
    }
    
    public static void _characterData_deleteData(final Dom c, final int offset, int count) {
        final String s = _characterData_getData(c);
        if (offset < 0 || offset > s.length() || count < 0) {
            throw new IndexSizeError();
        }
        if (offset + count > s.length()) {
            count = s.length() - offset;
        }
        if (count > 0) {
            _characterData_setData(c, s.substring(0, offset) + s.substring(offset + count));
        }
    }
    
    public static String _characterData_getData(final Dom c) {
        return _node_getNodeValue(c);
    }
    
    public static int _characterData_getLength(final Dom c) {
        return _characterData_getData(c).length();
    }
    
    public static void _characterData_insertData(final Dom c, final int offset, final String arg) {
        final String s = _characterData_getData(c);
        if (offset < 0 || offset > s.length()) {
            throw new IndexSizeError();
        }
        if (arg != null && arg.length() > 0) {
            _characterData_setData(c, s.substring(0, offset) + arg + s.substring(offset));
        }
    }
    
    public static void _characterData_replaceData(final Dom c, final int offset, int count, final String arg) {
        final String s = _characterData_getData(c);
        if (offset < 0 || offset > s.length() || count < 0) {
            throw new IndexSizeError();
        }
        if (offset + count > s.length()) {
            count = s.length() - offset;
        }
        if (count > 0) {
            _characterData_setData(c, s.substring(0, offset) + ((arg == null) ? "" : arg) + s.substring(offset + count));
        }
    }
    
    public static void _characterData_setData(final Dom c, final String data) {
        _node_setNodeValue(c, data);
    }
    
    public static String _characterData_substringData(final Dom c, final int offset, int count) {
        final String s = _characterData_getData(c);
        if (offset < 0 || offset > s.length() || count < 0) {
            throw new IndexSizeError();
        }
        if (offset + count > s.length()) {
            count = s.length() - offset;
        }
        return s.substring(offset, offset + count);
    }
    
    public static Text _text_splitText(final Dom t, final int offset) {
        assert t.nodeType() == 3;
        final String s = _characterData_getData(t);
        if (offset < 0 || offset > s.length()) {
            throw new IndexSizeError();
        }
        _characterData_deleteData(t, offset, s.length() - offset);
        final Dom t2 = (Dom)_document_createTextNode(t, s.substring(offset));
        final Dom p = (Dom)_node_getParentNode(t);
        if (p != null) {
            _node_insertBefore(p, (Node)t2, _node_getNextSibling(t));
            t.locale().invalidateDomCaches(p);
        }
        return (Text)t2;
    }
    
    public static String _text_getWholeText(final Dom t) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static boolean _text_isElementContentWhitespace(final Dom t) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static Text _text_replaceWholeText(final Dom t, final String content) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
    
    public static XMLStreamReader _getXmlStreamReader(final Dom n) {
        final Locale l = n.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return getXmlStreamReader(n);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return getXmlStreamReader(n);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static XMLStreamReader getXmlStreamReader(final Dom n) {
        XMLStreamReader xs = null;
        switch (n.nodeType()) {
            case 1:
            case 2:
            case 7:
            case 8:
            case 9:
            case 11: {
                final Cur c = n.tempCur();
                xs = Jsr173.newXmlStreamReader(c, null);
                c.release();
                break;
            }
            case 3:
            case 4: {
                final CharNode cn = (CharNode)n;
                Cur c2;
                if ((c2 = cn.tempCur()) == null) {
                    c2 = n.locale().tempCur();
                    xs = Jsr173.newXmlStreamReader(c2, cn._src, cn._off, cn._cch);
                }
                else {
                    xs = Jsr173.newXmlStreamReader(c2, c2.getChars(cn._cch), c2._offSrc, c2._cchSrc);
                }
                c2.release();
                break;
            }
            case 5:
            case 6:
            case 10:
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown kind");
            }
        }
        return xs;
    }
    
    public static XmlCursor _getXmlCursor(final Dom n) {
        final Locale l = n.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return getXmlCursor(n);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return getXmlCursor(n);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static XmlCursor getXmlCursor(final Dom n) {
        final Cur c = n.tempCur();
        final Cursor xc = new Cursor(c);
        c.release();
        return xc;
    }
    
    public static XmlObject _getXmlObject(final Dom n) {
        final Locale l = n.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return getXmlObject(n);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return getXmlObject(n);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static XmlObject getXmlObject(final Dom n) {
        final Cur c = n.tempCur();
        final XmlObject x = c.getObject();
        c.release();
        return x;
    }
    
    public static boolean _soapText_isComment(final Dom n) {
        final Locale l = n.locale();
        final org.apache.xmlbeans.impl.soap.Text text = (org.apache.xmlbeans.impl.soap.Text)n;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapText_isComment(text);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapText_isComment(text);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void _soapNode_detachNode(final Dom n) {
        final Locale l = n.locale();
        final org.apache.xmlbeans.impl.soap.Node node = (org.apache.xmlbeans.impl.soap.Node)n;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapNode_detachNode(node);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapNode_detachNode(node);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void _soapNode_recycleNode(final Dom n) {
        final Locale l = n.locale();
        final org.apache.xmlbeans.impl.soap.Node node = (org.apache.xmlbeans.impl.soap.Node)n;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapNode_recycleNode(node);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapNode_recycleNode(node);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static String _soapNode_getValue(final Dom n) {
        final Locale l = n.locale();
        final org.apache.xmlbeans.impl.soap.Node node = (org.apache.xmlbeans.impl.soap.Node)n;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapNode_getValue(node);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapNode_getValue(node);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void _soapNode_setValue(final Dom n, final String value) {
        final Locale l = n.locale();
        final org.apache.xmlbeans.impl.soap.Node node = (org.apache.xmlbeans.impl.soap.Node)n;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapNode_setValue(node, value);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapNode_setValue(node, value);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static SOAPElement _soapNode_getParentElement(final Dom n) {
        final Locale l = n.locale();
        final org.apache.xmlbeans.impl.soap.Node node = (org.apache.xmlbeans.impl.soap.Node)n;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapNode_getParentElement(node);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapNode_getParentElement(node);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void _soapNode_setParentElement(final Dom n, final SOAPElement p) {
        final Locale l = n.locale();
        final org.apache.xmlbeans.impl.soap.Node node = (org.apache.xmlbeans.impl.soap.Node)n;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapNode_setParentElement(node, p);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapNode_setParentElement(node, p);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void _soapElement_removeContents(final Dom d) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapElement_removeContents(se);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapElement_removeContents(se);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static String _soapElement_getEncodingStyle(final Dom d) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getEncodingStyle(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getEncodingStyle(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void _soapElement_setEncodingStyle(final Dom d, final String encodingStyle) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapElement_setEncodingStyle(se, encodingStyle);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapElement_setEncodingStyle(se, encodingStyle);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static boolean _soapElement_removeNamespaceDeclaration(final Dom d, final String prefix) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_removeNamespaceDeclaration(se, prefix);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_removeNamespaceDeclaration(se, prefix);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator _soapElement_getAllAttributes(final Dom d) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getAllAttributes(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getAllAttributes(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator _soapElement_getChildElements(final Dom d) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getChildElements(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getChildElements(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator _soapElement_getNamespacePrefixes(final Dom d) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getNamespacePrefixes(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getNamespacePrefixes(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPElement _soapElement_addAttribute(final Dom d, final Name name, final String value) throws SOAPException {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_addAttribute(se, name, value);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_addAttribute(se, name, value);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPElement _soapElement_addChildElement(final Dom d, final SOAPElement oldChild) throws SOAPException {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, oldChild);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, oldChild);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPElement _soapElement_addChildElement(final Dom d, final Name name) throws SOAPException {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPElement _soapElement_addChildElement(final Dom d, final String localName) throws SOAPException {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, localName);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, localName);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPElement _soapElement_addChildElement(final Dom d, final String localName, final String prefix) throws SOAPException {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, localName, prefix);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, localName, prefix);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPElement _soapElement_addChildElement(final Dom d, final String localName, final String prefix, final String uri) throws SOAPException {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, localName, prefix, uri);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_addChildElement(se, localName, prefix, uri);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPElement _soapElement_addNamespaceDeclaration(final Dom d, final String prefix, final String uri) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_addNamespaceDeclaration(se, prefix, uri);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_addNamespaceDeclaration(se, prefix, uri);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPElement _soapElement_addTextNode(final Dom d, final String data) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_addTextNode(se, data);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_addTextNode(se, data);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static String _soapElement_getAttributeValue(final Dom d, final Name name) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getAttributeValue(se, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getAttributeValue(se, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator _soapElement_getChildElements(final Dom d, final Name name) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getChildElements(se, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getChildElements(se, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Name _soapElement_getElementName(final Dom d) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getElementName(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getElementName(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static String _soapElement_getNamespaceURI(final Dom d, final String prefix) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getNamespaceURI(se, prefix);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getNamespaceURI(se, prefix);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator _soapElement_getVisibleNamespacePrefixes(final Dom d) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_getVisibleNamespacePrefixes(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_getVisibleNamespacePrefixes(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static boolean _soapElement_removeAttribute(final Dom d, final Name name) {
        final Locale l = d.locale();
        final SOAPElement se = (SOAPElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapElement_removeAttribute(se, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapElement_removeAttribute(se, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPBody _soapEnvelope_addBody(final Dom d) throws SOAPException {
        final Locale l = d.locale();
        final SOAPEnvelope se = (SOAPEnvelope)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_addBody(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_addBody(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPBody _soapEnvelope_getBody(final Dom d) throws SOAPException {
        final Locale l = d.locale();
        final SOAPEnvelope se = (SOAPEnvelope)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_getBody(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_getBody(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPHeader _soapEnvelope_getHeader(final Dom d) throws SOAPException {
        final Locale l = d.locale();
        final SOAPEnvelope se = (SOAPEnvelope)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_getHeader(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_getHeader(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPHeader _soapEnvelope_addHeader(final Dom d) throws SOAPException {
        final Locale l = d.locale();
        final SOAPEnvelope se = (SOAPEnvelope)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_addHeader(se);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_addHeader(se);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Name _soapEnvelope_createName(final Dom d, final String localName) {
        final Locale l = d.locale();
        final SOAPEnvelope se = (SOAPEnvelope)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_createName(se, localName);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_createName(se, localName);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Name _soapEnvelope_createName(final Dom d, final String localName, final String prefix, final String namespaceURI) {
        final Locale l = d.locale();
        final SOAPEnvelope se = (SOAPEnvelope)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_createName(se, localName, prefix, namespaceURI);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapEnvelope_createName(se, localName, prefix, namespaceURI);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator soapHeader_examineAllHeaderElements(final Dom d) {
        final Locale l = d.locale();
        final SOAPHeader sh = (SOAPHeader)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapHeader_examineAllHeaderElements(sh);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapHeader_examineAllHeaderElements(sh);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator soapHeader_extractAllHeaderElements(final Dom d) {
        final Locale l = d.locale();
        final SOAPHeader sh = (SOAPHeader)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapHeader_extractAllHeaderElements(sh);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapHeader_extractAllHeaderElements(sh);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator soapHeader_examineHeaderElements(final Dom d, final String actor) {
        final Locale l = d.locale();
        final SOAPHeader sh = (SOAPHeader)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapHeader_examineHeaderElements(sh, actor);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapHeader_examineHeaderElements(sh, actor);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator soapHeader_examineMustUnderstandHeaderElements(final Dom d, final String mustUnderstandString) {
        final Locale l = d.locale();
        final SOAPHeader sh = (SOAPHeader)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapHeader_examineMustUnderstandHeaderElements(sh, mustUnderstandString);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapHeader_examineMustUnderstandHeaderElements(sh, mustUnderstandString);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator soapHeader_extractHeaderElements(final Dom d, final String actor) {
        final Locale l = d.locale();
        final SOAPHeader sh = (SOAPHeader)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapHeader_extractHeaderElements(sh, actor);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapHeader_extractHeaderElements(sh, actor);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPHeaderElement soapHeader_addHeaderElement(final Dom d, final Name name) {
        final Locale l = d.locale();
        final SOAPHeader sh = (SOAPHeader)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapHeader_addHeaderElement(sh, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapHeader_addHeaderElement(sh, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static boolean soapBody_hasFault(final Dom d) {
        final Locale l = d.locale();
        final SOAPBody sb = (SOAPBody)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapBody_hasFault(sb);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapBody_hasFault(sb);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPFault soapBody_addFault(final Dom d) throws SOAPException {
        final Locale l = d.locale();
        final SOAPBody sb = (SOAPBody)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapBody_addFault(sb);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapBody_addFault(sb);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPFault soapBody_getFault(final Dom d) {
        final Locale l = d.locale();
        final SOAPBody sb = (SOAPBody)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapBody_getFault(sb);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapBody_getFault(sb);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPBodyElement soapBody_addBodyElement(final Dom d, final Name name) {
        final Locale l = d.locale();
        final SOAPBody sb = (SOAPBody)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapBody_addBodyElement(sb, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapBody_addBodyElement(sb, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPBodyElement soapBody_addDocument(final Dom d, final Document document) {
        final Locale l = d.locale();
        final SOAPBody sb = (SOAPBody)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapBody_addDocument(sb, document);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapBody_addDocument(sb, document);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPFault soapBody_addFault(final Dom d, final Name name, final String s) throws SOAPException {
        final Locale l = d.locale();
        final SOAPBody sb = (SOAPBody)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapBody_addFault(sb, name, s);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapBody_addFault(sb, name, s);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPFault soapBody_addFault(final Dom d, final Name faultCode, final String faultString, final java.util.Locale locale) throws SOAPException {
        final Locale l = d.locale();
        final SOAPBody sb = (SOAPBody)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapBody_addFault(sb, faultCode, faultString, locale);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapBody_addFault(sb, faultCode, faultString, locale);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void soapFault_setFaultString(final Dom d, final String faultString) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapFault_setFaultString(sf, faultString);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapFault_setFaultString(sf, faultString);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void soapFault_setFaultString(final Dom d, final String faultString, final java.util.Locale locale) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapFault_setFaultString(sf, faultString, locale);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapFault_setFaultString(sf, faultString, locale);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void soapFault_setFaultCode(final Dom d, final Name faultCodeName) throws SOAPException {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapFault_setFaultCode(sf, faultCodeName);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapFault_setFaultCode(sf, faultCodeName);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void soapFault_setFaultActor(final Dom d, final String faultActorString) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapFault_setFaultActor(sf, faultActorString);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapFault_setFaultActor(sf, faultActorString);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static String soapFault_getFaultActor(final Dom d) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultActor(sf);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultActor(sf);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static String soapFault_getFaultCode(final Dom d) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultCode(sf);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultCode(sf);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void soapFault_setFaultCode(final Dom d, final String faultCode) throws SOAPException {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapFault_setFaultCode(sf, faultCode);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapFault_setFaultCode(sf, faultCode);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static java.util.Locale soapFault_getFaultStringLocale(final Dom d) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultStringLocale(sf);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultStringLocale(sf);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Name soapFault_getFaultCodeAsName(final Dom d) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultCodeAsName(sf);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultCodeAsName(sf);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static String soapFault_getFaultString(final Dom d) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultString(sf);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapFault_getFaultString(sf);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Detail soapFault_addDetail(final Dom d) throws SOAPException {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapFault_addDetail(sf);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapFault_addDetail(sf);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Detail soapFault_getDetail(final Dom d) {
        final Locale l = d.locale();
        final SOAPFault sf = (SOAPFault)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapFault_getDetail(sf);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapFault_getDetail(sf);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void soapHeaderElement_setMustUnderstand(final Dom d, final boolean mustUnderstand) {
        final Locale l = d.locale();
        final SOAPHeaderElement she = (SOAPHeaderElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapHeaderElement_setMustUnderstand(she, mustUnderstand);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapHeaderElement_setMustUnderstand(she, mustUnderstand);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static boolean soapHeaderElement_getMustUnderstand(final Dom d) {
        final Locale l = d.locale();
        final SOAPHeaderElement she = (SOAPHeaderElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapHeaderElement_getMustUnderstand(she);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapHeaderElement_getMustUnderstand(she);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void soapHeaderElement_setActor(final Dom d, final String actor) {
        final Locale l = d.locale();
        final SOAPHeaderElement she = (SOAPHeaderElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapHeaderElement_setActor(she, actor);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapHeaderElement_setActor(she, actor);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static String soapHeaderElement_getActor(final Dom d) {
        final Locale l = d.locale();
        final SOAPHeaderElement she = (SOAPHeaderElement)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapHeaderElement_getActor(she);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapHeaderElement_getActor(she);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static DetailEntry detail_addDetailEntry(final Dom d, final Name name) {
        final Locale l = d.locale();
        final Detail detail = (Detail)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.detail_addDetailEntry(detail, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.detail_addDetailEntry(detail, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator detail_getDetailEntries(final Dom d) {
        final Locale l = d.locale();
        final Detail detail = (Detail)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.detail_getDetailEntries(detail);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.detail_getDetailEntries(detail);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void _soapPart_removeAllMimeHeaders(final Dom d) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapPart_removeAllMimeHeaders(sp);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapPart_removeAllMimeHeaders(sp);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void _soapPart_removeMimeHeader(final Dom d, final String name) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapPart_removeMimeHeader(sp, name);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapPart_removeMimeHeader(sp, name);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static Iterator _soapPart_getAllMimeHeaders(final Dom d) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapPart_getAllMimeHeaders(sp);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapPart_getAllMimeHeaders(sp);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static SOAPEnvelope _soapPart_getEnvelope(final Dom d) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapPart_getEnvelope(sp);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapPart_getEnvelope(sp);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Source _soapPart_getContent(final Dom d) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapPart_getContent(sp);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapPart_getContent(sp);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void _soapPart_setContent(final Dom d, final Source source) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapPart_setContent(sp, source);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapPart_setContent(sp, source);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static String[] _soapPart_getMimeHeader(final Dom d, final String name) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapPart_getMimeHeader(sp, name);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapPart_getMimeHeader(sp, name);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void _soapPart_addMimeHeader(final Dom d, final String name, final String value) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapPart_addMimeHeader(sp, name, value);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapPart_addMimeHeader(sp, name, value);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void _soapPart_setMimeHeader(final Dom d, final String name, final String value) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                l._saaj.soapPart_setMimeHeader(sp, name, value);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    l._saaj.soapPart_setMimeHeader(sp, name, value);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static Iterator _soapPart_getMatchingMimeHeaders(final Dom d, final String[] names) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapPart_getMatchingMimeHeaders(sp, names);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapPart_getMatchingMimeHeaders(sp, names);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Iterator _soapPart_getNonMatchingMimeHeaders(final Dom d, final String[] names) {
        final Locale l = d.locale();
        final SOAPPart sp = (SOAPPart)d;
        if (l.noSync()) {
            l.enter();
            try {
                return l._saaj.soapPart_getNonMatchingMimeHeaders(sp, names);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return l._saaj.soapPart_getNonMatchingMimeHeaders(sp, names);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static void saajCallback_setSaajData(final Dom d, final Object o) {
        final Locale l = d.locale();
        if (l.noSync()) {
            l.enter();
            try {
                impl_saajCallback_setSaajData(d, o);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    impl_saajCallback_setSaajData(d, o);
                }
                finally {
                    l.exit();
                }
            }
        }
    }
    
    public static void impl_saajCallback_setSaajData(final Dom d, final Object o) {
        final Locale l = d.locale();
        final Cur c = l.tempCur();
        c.moveToDom(d);
        SaajData sd = null;
        if (o != null) {
            sd = (SaajData)c.getBookmark(SaajData.class);
            if (sd == null) {
                sd = new SaajData();
            }
            sd._obj = o;
        }
        c.setBookmark(SaajData.class, sd);
        c.release();
    }
    
    public static Object saajCallback_getSaajData(final Dom d) {
        final Locale l = d.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return impl_saajCallback_getSaajData(d);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return impl_saajCallback_getSaajData(d);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Object impl_saajCallback_getSaajData(final Dom d) {
        final Locale l = d.locale();
        final Cur c = l.tempCur();
        c.moveToDom(d);
        final SaajData sd = (SaajData)c.getBookmark(SaajData.class);
        final Object o = (sd == null) ? null : sd._obj;
        c.release();
        return o;
    }
    
    public static Element saajCallback_createSoapElement(final Dom d, final QName name, final QName parentName) {
        final Locale l = d.locale();
        Dom e;
        if (l.noSync()) {
            l.enter();
            try {
                e = impl_saajCallback_createSoapElement(d, name, parentName);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    e = impl_saajCallback_createSoapElement(d, name, parentName);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Element)e;
    }
    
    public static Dom impl_saajCallback_createSoapElement(final Dom d, final QName name, final QName parentName) {
        final Cur c = d.locale().tempCur();
        c.createElement(name, parentName);
        final Dom e = c.getDom();
        c.release();
        return e;
    }
    
    public static Element saajCallback_importSoapElement(final Dom d, final Element elem, final boolean deep, final QName parentName) {
        final Locale l = d.locale();
        Dom e;
        if (l.noSync()) {
            l.enter();
            try {
                e = impl_saajCallback_importSoapElement(d, elem, deep, parentName);
            }
            finally {
                l.exit();
            }
        }
        else {
            synchronized (l) {
                l.enter();
                try {
                    e = impl_saajCallback_importSoapElement(d, elem, deep, parentName);
                }
                finally {
                    l.exit();
                }
            }
        }
        return (Element)e;
    }
    
    public static Dom impl_saajCallback_importSoapElement(final Dom d, final Element elem, final boolean deep, final QName parentName) {
        throw new RuntimeException("Not impl");
    }
    
    public static Text saajCallback_ensureSoapTextNode(final Dom d) {
        final Locale l = d.locale();
        if (l.noSync()) {
            l.enter();
            try {
                return impl_saajCallback_ensureSoapTextNode(d);
            }
            finally {
                l.exit();
            }
        }
        synchronized (l) {
            l.enter();
            try {
                return impl_saajCallback_ensureSoapTextNode(d);
            }
            finally {
                l.exit();
            }
        }
    }
    
    public static Text impl_saajCallback_ensureSoapTextNode(final Dom d) {
        return null;
    }
    
    static {
        DomImpl._emptyNodeList = new EmptyNodeList();
    }
    
    static class HierarchyRequestErr extends DOMException
    {
        HierarchyRequestErr() {
            this("This node isn't allowed there");
        }
        
        HierarchyRequestErr(final String message) {
            super((short)3, message);
        }
    }
    
    static class WrongDocumentErr extends DOMException
    {
        WrongDocumentErr() {
            this("Nodes do not belong to the same document");
        }
        
        WrongDocumentErr(final String message) {
            super((short)4, message);
        }
    }
    
    static class NotFoundErr extends DOMException
    {
        NotFoundErr() {
            this("Node not found");
        }
        
        NotFoundErr(final String message) {
            super((short)8, message);
        }
    }
    
    static class NamespaceErr extends DOMException
    {
        NamespaceErr() {
            this("Namespace error");
        }
        
        NamespaceErr(final String message) {
            super((short)14, message);
        }
    }
    
    static class NoModificationAllowedErr extends DOMException
    {
        NoModificationAllowedErr() {
            this("No modification allowed error");
        }
        
        NoModificationAllowedErr(final String message) {
            super((short)7, message);
        }
    }
    
    static class InuseAttributeError extends DOMException
    {
        InuseAttributeError() {
            this("Attribute currently in use error");
        }
        
        InuseAttributeError(final String message) {
            super((short)10, message);
        }
    }
    
    static class IndexSizeError extends DOMException
    {
        IndexSizeError() {
            this("Index Size Error");
        }
        
        IndexSizeError(final String message) {
            super((short)1, message);
        }
    }
    
    static class NotSupportedError extends DOMException
    {
        NotSupportedError() {
            this("This operation is not supported");
        }
        
        NotSupportedError(final String message) {
            super((short)9, message);
        }
    }
    
    static class InvalidCharacterError extends DOMException
    {
        InvalidCharacterError() {
            this("The name contains an invalid character");
        }
        
        InvalidCharacterError(final String message) {
            super((short)5, message);
        }
    }
    
    private static final class EmptyNodeList implements NodeList
    {
        @Override
        public int getLength() {
            return 0;
        }
        
        @Override
        public Node item(final int i) {
            return null;
        }
    }
    
    private abstract static class ElementsNodeList implements NodeList
    {
        private Dom _root;
        private Locale _locale;
        private long _version;
        private ArrayList _elements;
        
        ElementsNodeList(final Dom root) {
            assert root.nodeType() == 1;
            this._root = root;
            this._locale = this._root.locale();
            this._version = 0L;
        }
        
        @Override
        public int getLength() {
            this.ensureElements();
            return this._elements.size();
        }
        
        @Override
        public Node item(final int i) {
            this.ensureElements();
            return (i < 0 || i >= this._elements.size()) ? null : this._elements.get(i);
        }
        
        private void ensureElements() {
            if (this._version == this._locale.version()) {
                return;
            }
            this._version = this._locale.version();
            this._elements = new ArrayList();
            final Locale l = this._locale;
            if (l.noSync()) {
                l.enter();
                try {
                    this.addElements(this._root);
                }
                finally {
                    l.exit();
                }
            }
            else {
                synchronized (l) {
                    l.enter();
                    try {
                        this.addElements(this._root);
                    }
                    finally {
                        l.exit();
                    }
                }
            }
        }
        
        private void addElements(final Dom node) {
            for (Dom c = DomImpl.firstChild(node); c != null; c = DomImpl.nextSibling(c)) {
                if (c.nodeType() == 1) {
                    if (this.match(c)) {
                        this._elements.add(c);
                    }
                    this.addElements(c);
                }
            }
        }
        
        protected abstract boolean match(final Dom p0);
    }
    
    private static class ElementsByTagNameNodeList extends ElementsNodeList
    {
        private String _name;
        
        ElementsByTagNameNodeList(final Dom root, final String name) {
            super(root);
            this._name = name;
        }
        
        @Override
        protected boolean match(final Dom element) {
            return this._name.equals("*") || DomImpl._node_getNodeName(element).equals(this._name);
        }
    }
    
    private static class ElementsByTagNameNSNodeList extends ElementsNodeList
    {
        private String _uri;
        private String _local;
        
        ElementsByTagNameNSNodeList(final Dom root, final String uri, final String local) {
            super(root);
            this._uri = ((uri == null) ? "" : uri);
            this._local = local;
        }
        
        @Override
        protected boolean match(final Dom element) {
            if (!this._uri.equals("*")) {
                if (!DomImpl._node_getNamespaceURI(element).equals(this._uri)) {
                    return false;
                }
            }
            return this._local.equals("*") || DomImpl._node_getLocalName(element).equals(this._local);
        }
    }
    
    abstract static class CharNode implements Dom, Node, CharacterData
    {
        private Locale _locale;
        CharNode _next;
        CharNode _prev;
        private Object _src;
        int _off;
        int _cch;
        
        public CharNode(final Locale l) {
            assert l != null;
            this._locale = l;
        }
        
        @Override
        public QName getQName() {
            return null;
        }
        
        @Override
        public Locale locale() {
            assert this.isValid();
            return (this._locale == null) ? ((Dom)this._src).locale() : this._locale;
        }
        
        public void setChars(final Object src, final int off, final int cch) {
            assert CharUtil.isValid(src, off, cch);
            assert !(!(this._src instanceof Dom));
            if (this._locale == null) {
                this._locale = ((Dom)this._src).locale();
            }
            this._src = src;
            this._off = off;
            this._cch = cch;
        }
        
        public Dom getDom() {
            assert this.isValid();
            if (this._src instanceof Dom) {
                return (Dom)this._src;
            }
            return null;
        }
        
        public void setDom(final Dom d) {
            assert d != null;
            this._src = d;
            this._locale = null;
        }
        
        @Override
        public Cur tempCur() {
            assert this.isValid();
            if (!(this._src instanceof Dom)) {
                return null;
            }
            final Cur c = this.locale().tempCur();
            c.moveToCharNode(this);
            return c;
        }
        
        private boolean isValid() {
            if (this._src instanceof Dom) {
                return this._locale == null;
            }
            return this._locale != null;
        }
        
        public static boolean isOnList(final CharNode nodes, final CharNode node) {
            assert node != null;
            for (CharNode cn = nodes; cn != null; cn = cn._next) {
                if (cn == node) {
                    return true;
                }
            }
            return false;
        }
        
        public static CharNode remove(CharNode nodes, final CharNode node) {
            assert isOnList(nodes, node);
            if (nodes == node) {
                nodes = node._next;
            }
            else {
                node._prev._next = node._next;
            }
            if (node._next != null) {
                node._next._prev = node._prev;
            }
            final CharNode charNode = null;
            node._next = charNode;
            node._prev = charNode;
            return nodes;
        }
        
        public static CharNode insertNode(CharNode nodes, final CharNode newNode, final CharNode before) {
            assert !isOnList(nodes, newNode);
            assert !(!isOnList(nodes, before));
            assert newNode != null;
            assert newNode._prev == null && newNode._next == null;
            if (nodes == null) {
                assert before == null;
                nodes = newNode;
            }
            else if (nodes == before) {
                nodes._prev = newNode;
                newNode._next = nodes;
                nodes = newNode;
            }
            else {
                CharNode n;
                for (n = nodes; n._next != before; n = n._next) {}
                if ((newNode._next = n._next) != null) {
                    n._next._prev = newNode;
                }
                newNode._prev = n;
                n._next = newNode;
            }
            return nodes;
        }
        
        public static CharNode appendNode(final CharNode nodes, final CharNode newNode) {
            return insertNode(nodes, newNode, null);
        }
        
        public static CharNode appendNodes(final CharNode nodes, final CharNode newNodes) {
            assert newNodes != null;
            assert newNodes._prev == null;
            if (nodes == null) {
                return newNodes;
            }
            CharNode n;
            for (n = nodes; n._next != null; n = n._next) {}
            n._next = newNodes;
            newNodes._prev = n;
            return nodes;
        }
        
        public static CharNode copyNodes(CharNode nodes, final Object newSrc) {
            CharNode newNodes = null;
            CharNode n = null;
            while (nodes != null) {
                CharNode newNode;
                if (nodes instanceof TextNode) {
                    newNode = nodes.locale().createTextNode();
                }
                else {
                    newNode = nodes.locale().createCdataNode();
                }
                newNode.setChars(newSrc, nodes._off, nodes._cch);
                if (newNodes == null) {
                    newNodes = newNode;
                }
                if (n != null) {
                    n._next = newNode;
                    newNode._prev = n;
                }
                n = newNode;
                nodes = nodes._next;
            }
            return newNodes;
        }
        
        @Override
        public boolean nodeCanHavePrefixUri() {
            return false;
        }
        
        public boolean isNodeAftertext() {
            assert this._src instanceof Xobj : "this method is to only be used for nodes backed up by Xobjs";
            final Xobj src = (Xobj)this._src;
            return src._charNodesValue == null || (src._charNodesAfter != null && isOnList(src._charNodesAfter, this));
        }
        
        @Override
        public void dump(final PrintStream o, final Object ref) {
            if (this._src instanceof Dom) {
                ((Dom)this._src).dump(o, ref);
            }
            else {
                o.println("Lonely CharNode: \"" + CharUtil.getString(this._src, this._off, this._cch) + "\"");
            }
        }
        
        @Override
        public void dump(final PrintStream o) {
            this.dump(o, this);
        }
        
        @Override
        public void dump() {
            this.dump(System.out);
        }
        
        @Override
        public Node appendChild(final Node newChild) {
            return DomImpl._node_appendChild(this, newChild);
        }
        
        @Override
        public Node cloneNode(final boolean deep) {
            return DomImpl._node_cloneNode(this, deep);
        }
        
        @Override
        public NamedNodeMap getAttributes() {
            return null;
        }
        
        @Override
        public NodeList getChildNodes() {
            return DomImpl._emptyNodeList;
        }
        
        @Override
        public Node getParentNode() {
            return DomImpl._node_getParentNode(this);
        }
        
        @Override
        public Node removeChild(final Node oldChild) {
            return DomImpl._node_removeChild(this, oldChild);
        }
        
        @Override
        public Node getFirstChild() {
            return null;
        }
        
        @Override
        public Node getLastChild() {
            return null;
        }
        
        @Override
        public String getLocalName() {
            return DomImpl._node_getLocalName(this);
        }
        
        @Override
        public String getNamespaceURI() {
            return DomImpl._node_getNamespaceURI(this);
        }
        
        @Override
        public Node getNextSibling() {
            return DomImpl._node_getNextSibling(this);
        }
        
        @Override
        public String getNodeName() {
            return DomImpl._node_getNodeName(this);
        }
        
        @Override
        public short getNodeType() {
            return DomImpl._node_getNodeType(this);
        }
        
        @Override
        public String getNodeValue() {
            return DomImpl._node_getNodeValue(this);
        }
        
        @Override
        public Document getOwnerDocument() {
            return DomImpl._node_getOwnerDocument(this);
        }
        
        @Override
        public String getPrefix() {
            return DomImpl._node_getPrefix(this);
        }
        
        @Override
        public Node getPreviousSibling() {
            return DomImpl._node_getPreviousSibling(this);
        }
        
        @Override
        public boolean hasAttributes() {
            return false;
        }
        
        @Override
        public boolean hasChildNodes() {
            return false;
        }
        
        @Override
        public Node insertBefore(final Node newChild, final Node refChild) {
            return DomImpl._node_insertBefore(this, newChild, refChild);
        }
        
        @Override
        public boolean isSupported(final String feature, final String version) {
            return DomImpl._node_isSupported(this, feature, version);
        }
        
        @Override
        public void normalize() {
            DomImpl._node_normalize(this);
        }
        
        @Override
        public Node replaceChild(final Node newChild, final Node oldChild) {
            return DomImpl._node_replaceChild(this, newChild, oldChild);
        }
        
        @Override
        public void setNodeValue(final String nodeValue) {
            DomImpl._node_setNodeValue(this, nodeValue);
        }
        
        @Override
        public void setPrefix(final String prefix) {
            DomImpl._node_setPrefix(this, prefix);
        }
        
        @Override
        public Object getUserData(final String key) {
            return DomImpl._node_getUserData(this, key);
        }
        
        @Override
        public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
            return DomImpl._node_setUserData(this, key, data, handler);
        }
        
        @Override
        public Object getFeature(final String feature, final String version) {
            return DomImpl._node_getFeature(this, feature, version);
        }
        
        @Override
        public boolean isEqualNode(final Node arg) {
            return DomImpl._node_isEqualNode(this, arg);
        }
        
        @Override
        public boolean isSameNode(final Node arg) {
            return DomImpl._node_isSameNode(this, arg);
        }
        
        @Override
        public String lookupNamespaceURI(final String prefix) {
            return DomImpl._node_lookupNamespaceURI(this, prefix);
        }
        
        @Override
        public String lookupPrefix(final String namespaceURI) {
            return DomImpl._node_lookupPrefix(this, namespaceURI);
        }
        
        @Override
        public boolean isDefaultNamespace(final String namespaceURI) {
            return DomImpl._node_isDefaultNamespace(this, namespaceURI);
        }
        
        @Override
        public void setTextContent(final String textContent) {
            DomImpl._node_setTextContent(this, textContent);
        }
        
        @Override
        public String getTextContent() {
            return DomImpl._node_getTextContent(this);
        }
        
        @Override
        public short compareDocumentPosition(final Node other) {
            return DomImpl._node_compareDocumentPosition(this, other);
        }
        
        @Override
        public String getBaseURI() {
            return DomImpl._node_getBaseURI(this);
        }
        
        @Override
        public void appendData(final String arg) {
            DomImpl._characterData_appendData(this, arg);
        }
        
        @Override
        public void deleteData(final int offset, final int count) {
            DomImpl._characterData_deleteData(this, offset, count);
        }
        
        @Override
        public String getData() {
            return DomImpl._characterData_getData(this);
        }
        
        @Override
        public int getLength() {
            return DomImpl._characterData_getLength(this);
        }
        
        @Override
        public void insertData(final int offset, final String arg) {
            DomImpl._characterData_insertData(this, offset, arg);
        }
        
        @Override
        public void replaceData(final int offset, final int count, final String arg) {
            DomImpl._characterData_replaceData(this, offset, count, arg);
        }
        
        @Override
        public void setData(final String data) {
            DomImpl._characterData_setData(this, data);
        }
        
        @Override
        public String substringData(final int offset, final int count) {
            return DomImpl._characterData_substringData(this, offset, count);
        }
    }
    
    static class TextNode extends CharNode implements Text
    {
        TextNode(final Locale l) {
            super(l);
        }
        
        @Override
        public int nodeType() {
            return 3;
        }
        
        public String name() {
            return "#text";
        }
        
        @Override
        public Text splitText(final int offset) {
            return DomImpl._text_splitText(this, offset);
        }
        
        @Override
        public String getWholeText() {
            return DomImpl._text_getWholeText(this);
        }
        
        @Override
        public boolean isElementContentWhitespace() {
            return DomImpl._text_isElementContentWhitespace(this);
        }
        
        @Override
        public Text replaceWholeText(final String content) {
            return DomImpl._text_replaceWholeText(this, content);
        }
    }
    
    static class CdataNode extends TextNode implements CDATASection
    {
        CdataNode(final Locale l) {
            super(l);
        }
        
        @Override
        public int nodeType() {
            return 4;
        }
        
        @Override
        public String name() {
            return "#cdata-section";
        }
    }
    
    static class SaajTextNode extends TextNode implements org.apache.xmlbeans.impl.soap.Text
    {
        SaajTextNode(final Locale l) {
            super(l);
        }
        
        @Override
        public boolean isComment() {
            return DomImpl._soapText_isComment(this);
        }
        
        @Override
        public void detachNode() {
            DomImpl._soapNode_detachNode(this);
        }
        
        @Override
        public void recycleNode() {
            DomImpl._soapNode_recycleNode(this);
        }
        
        @Override
        public String getValue() {
            return DomImpl._soapNode_getValue(this);
        }
        
        @Override
        public void setValue(final String value) {
            DomImpl._soapNode_setValue(this, value);
        }
        
        @Override
        public SOAPElement getParentElement() {
            return DomImpl._soapNode_getParentElement(this);
        }
        
        @Override
        public void setParentElement(final SOAPElement p) {
            DomImpl._soapNode_setParentElement(this, p);
        }
    }
    
    static class SaajCdataNode extends CdataNode implements org.apache.xmlbeans.impl.soap.Text
    {
        public SaajCdataNode(final Locale l) {
            super(l);
        }
        
        @Override
        public boolean isComment() {
            return DomImpl._soapText_isComment(this);
        }
        
        @Override
        public void detachNode() {
            DomImpl._soapNode_detachNode(this);
        }
        
        @Override
        public void recycleNode() {
            DomImpl._soapNode_recycleNode(this);
        }
        
        @Override
        public String getValue() {
            return DomImpl._soapNode_getValue(this);
        }
        
        @Override
        public void setValue(final String value) {
            DomImpl._soapNode_setValue(this, value);
        }
        
        @Override
        public SOAPElement getParentElement() {
            return DomImpl._soapNode_getParentElement(this);
        }
        
        @Override
        public void setParentElement(final SOAPElement p) {
            DomImpl._soapNode_setParentElement(this, p);
        }
    }
    
    private static class SaajData
    {
        Object _obj;
    }
    
    interface Dom
    {
        Locale locale();
        
        int nodeType();
        
        Cur tempCur();
        
        QName getQName();
        
        boolean nodeCanHavePrefixUri();
        
        void dump();
        
        void dump(final PrintStream p0);
        
        void dump(final PrintStream p0, final Object p1);
    }
}
