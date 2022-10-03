package org.apache.xerces.dom;

import org.w3c.dom.ls.LSOutput;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.impl.RevalidationHandler;
import org.w3c.dom.ls.LSInput;
import org.apache.xml.serialize.DOMSerializerImpl;
import org.w3c.dom.ls.LSSerializer;
import org.apache.xerces.parsers.DOMParserImpl;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.xerces.util.XMLChar;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import java.lang.ref.SoftReference;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.DOMImplementation;

public class CoreDOMImplementationImpl implements DOMImplementation, DOMImplementationLS
{
    private static final int SIZE = 2;
    private SoftReference[] schemaValidators;
    private SoftReference[] xml10DTDValidators;
    private SoftReference[] xml11DTDValidators;
    private int freeSchemaValidatorIndex;
    private int freeXML10DTDValidatorIndex;
    private int freeXML11DTDValidatorIndex;
    private int schemaValidatorsCurrentSize;
    private int xml10DTDValidatorsCurrentSize;
    private int xml11DTDValidatorsCurrentSize;
    private SoftReference[] xml10DTDLoaders;
    private SoftReference[] xml11DTDLoaders;
    private int freeXML10DTDLoaderIndex;
    private int freeXML11DTDLoaderIndex;
    private int xml10DTDLoaderCurrentSize;
    private int xml11DTDLoaderCurrentSize;
    private int docAndDoctypeCounter;
    static final CoreDOMImplementationImpl singleton;
    
    public CoreDOMImplementationImpl() {
        this.schemaValidators = new SoftReference[2];
        this.xml10DTDValidators = new SoftReference[2];
        this.xml11DTDValidators = new SoftReference[2];
        this.freeSchemaValidatorIndex = -1;
        this.freeXML10DTDValidatorIndex = -1;
        this.freeXML11DTDValidatorIndex = -1;
        this.schemaValidatorsCurrentSize = 2;
        this.xml10DTDValidatorsCurrentSize = 2;
        this.xml11DTDValidatorsCurrentSize = 2;
        this.xml10DTDLoaders = new SoftReference[2];
        this.xml11DTDLoaders = new SoftReference[2];
        this.freeXML10DTDLoaderIndex = -1;
        this.freeXML11DTDLoaderIndex = -1;
        this.xml10DTDLoaderCurrentSize = 2;
        this.xml11DTDLoaderCurrentSize = 2;
        this.docAndDoctypeCounter = 0;
    }
    
    public static DOMImplementation getDOMImplementation() {
        return CoreDOMImplementationImpl.singleton;
    }
    
    public boolean hasFeature(String substring, final String s) {
        final boolean b = s == null || s.length() == 0;
        Label_0101: {
            if (substring.equalsIgnoreCase("+XPath")) {
                if (!b) {
                    if (!s.equals("3.0")) {
                        break Label_0101;
                    }
                }
                try {
                    final Class[] interfaces = ObjectFactory.findProviderClass("org.apache.xpath.domapi.XPathEvaluatorImpl", ObjectFactory.findClassLoader(), true).getInterfaces();
                    for (int i = 0; i < interfaces.length; ++i) {
                        if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                            return true;
                        }
                    }
                }
                catch (final Exception ex) {
                    return false;
                }
                return true;
            }
        }
        if (substring.startsWith("+")) {
            substring = substring.substring(1);
        }
        return (substring.equalsIgnoreCase("Core") && (b || s.equals("1.0") || s.equals("2.0") || s.equals("3.0"))) || (substring.equalsIgnoreCase("XML") && (b || s.equals("1.0") || s.equals("2.0") || s.equals("3.0"))) || (substring.equalsIgnoreCase("XMLVersion") && (b || s.equals("1.0") || s.equals("1.1"))) || (substring.equalsIgnoreCase("LS") && (b || s.equals("3.0"))) || (substring.equalsIgnoreCase("ElementTraversal") && (b || s.equals("1.0")));
    }
    
    public DocumentType createDocumentType(final String s, final String s2, final String s3) {
        this.checkQName(s);
        return new DocumentTypeImpl(null, s, s2, s3);
    }
    
    final void checkQName(final String s) {
        final int index = s.indexOf(58);
        final int lastIndex = s.lastIndexOf(58);
        final int length = s.length();
        if (index == 0 || index == length - 1 || lastIndex != index) {
            throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
        }
        int n = 0;
        if (index > 0) {
            if (!XMLChar.isNCNameStart(s.charAt(n))) {
                throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
            }
            for (int i = 1; i < index; ++i) {
                if (!XMLChar.isNCName(s.charAt(i))) {
                    throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
                }
            }
            n = index + 1;
        }
        if (!XMLChar.isNCNameStart(s.charAt(n))) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
        for (int j = n + 1; j < length; ++j) {
            if (!XMLChar.isNCName(s.charAt(j))) {
                throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
            }
        }
    }
    
    public Document createDocument(final String s, final String s2, final DocumentType documentType) throws DOMException {
        if (documentType != null && documentType.getOwnerDocument() != null) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
        }
        final CoreDocumentImpl document = this.createDocument(documentType);
        if (s2 != null || s != null) {
            document.appendChild(document.createElementNS(s, s2));
        }
        return document;
    }
    
    protected CoreDocumentImpl createDocument(final DocumentType documentType) {
        return new CoreDocumentImpl(documentType);
    }
    
    public Object getFeature(final String s, final String s2) {
        if (CoreDOMImplementationImpl.singleton.hasFeature(s, s2)) {
            if (s.equalsIgnoreCase("+XPath")) {
                try {
                    final Class providerClass = ObjectFactory.findProviderClass("org.apache.xpath.domapi.XPathEvaluatorImpl", ObjectFactory.findClassLoader(), true);
                    final Class[] interfaces = providerClass.getInterfaces();
                    for (int i = 0; i < interfaces.length; ++i) {
                        if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                            return providerClass.newInstance();
                        }
                    }
                    return null;
                }
                catch (final Exception ex) {
                    return null;
                }
            }
            return CoreDOMImplementationImpl.singleton;
        }
        return null;
    }
    
    public LSParser createLSParser(final short n, final String s) throws DOMException {
        if (n != 1 || (s != null && !"http://www.w3.org/2001/XMLSchema".equals(s) && !"http://www.w3.org/TR/REC-xml".equals(s))) {
            throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
        }
        if (s != null && s.equals("http://www.w3.org/TR/REC-xml")) {
            return new DOMParserImpl("org.apache.xerces.parsers.XML11DTDConfiguration", s);
        }
        return new DOMParserImpl("org.apache.xerces.parsers.XIncludeAwareParserConfiguration", s);
    }
    
    public LSSerializer createLSSerializer() {
        try {
            return ObjectFactory.findProviderClass("org.apache.xml.serializer.dom3.LSSerializerImpl", ObjectFactory.findClassLoader(), true).newInstance();
        }
        catch (final Exception ex) {
            return new DOMSerializerImpl();
        }
    }
    
    public LSInput createLSInput() {
        return new DOMInputImpl();
    }
    
    synchronized RevalidationHandler getValidator(final String s, final String s2) {
        if (s == "http://www.w3.org/2001/XMLSchema") {
            while (this.freeSchemaValidatorIndex >= 0) {
                final RevalidationHandlerHolder revalidationHandlerHolder = this.schemaValidators[this.freeSchemaValidatorIndex].get();
                if (revalidationHandlerHolder != null && revalidationHandlerHolder.handler != null) {
                    final RevalidationHandler handler = revalidationHandlerHolder.handler;
                    revalidationHandlerHolder.handler = null;
                    --this.freeSchemaValidatorIndex;
                    return handler;
                }
                this.schemaValidators[this.freeSchemaValidatorIndex--] = null;
            }
            return (RevalidationHandler)ObjectFactory.newInstance("org.apache.xerces.impl.xs.XMLSchemaValidator", ObjectFactory.findClassLoader(), true);
        }
        if (s != "http://www.w3.org/TR/REC-xml") {
            return null;
        }
        if ("1.1".equals(s2)) {
            while (this.freeXML11DTDValidatorIndex >= 0) {
                final RevalidationHandlerHolder revalidationHandlerHolder2 = this.xml11DTDValidators[this.freeXML11DTDValidatorIndex].get();
                if (revalidationHandlerHolder2 != null && revalidationHandlerHolder2.handler != null) {
                    final RevalidationHandler handler2 = revalidationHandlerHolder2.handler;
                    revalidationHandlerHolder2.handler = null;
                    --this.freeXML11DTDValidatorIndex;
                    return handler2;
                }
                this.xml11DTDValidators[this.freeXML11DTDValidatorIndex--] = null;
            }
            return (RevalidationHandler)ObjectFactory.newInstance("org.apache.xerces.impl.dtd.XML11DTDValidator", ObjectFactory.findClassLoader(), true);
        }
        while (this.freeXML10DTDValidatorIndex >= 0) {
            final RevalidationHandlerHolder revalidationHandlerHolder3 = this.xml10DTDValidators[this.freeXML10DTDValidatorIndex].get();
            if (revalidationHandlerHolder3 != null && revalidationHandlerHolder3.handler != null) {
                final RevalidationHandler handler3 = revalidationHandlerHolder3.handler;
                revalidationHandlerHolder3.handler = null;
                --this.freeXML10DTDValidatorIndex;
                return handler3;
            }
            this.xml10DTDValidators[this.freeXML10DTDValidatorIndex--] = null;
        }
        return (RevalidationHandler)ObjectFactory.newInstance("org.apache.xerces.impl.dtd.XMLDTDValidator", ObjectFactory.findClassLoader(), true);
    }
    
    synchronized void releaseValidator(final String s, final String s2, final RevalidationHandler handler) {
        if (s == "http://www.w3.org/2001/XMLSchema") {
            ++this.freeSchemaValidatorIndex;
            if (this.schemaValidators.length == this.freeSchemaValidatorIndex) {
                this.schemaValidatorsCurrentSize += 2;
                final SoftReference[] schemaValidators = new SoftReference[this.schemaValidatorsCurrentSize];
                System.arraycopy(this.schemaValidators, 0, schemaValidators, 0, this.schemaValidators.length);
                this.schemaValidators = schemaValidators;
            }
            final SoftReference softReference = this.schemaValidators[this.freeSchemaValidatorIndex];
            if (softReference != null) {
                final RevalidationHandlerHolder revalidationHandlerHolder = softReference.get();
                if (revalidationHandlerHolder != null) {
                    revalidationHandlerHolder.handler = handler;
                    return;
                }
            }
            this.schemaValidators[this.freeSchemaValidatorIndex] = new SoftReference((T)new RevalidationHandlerHolder(handler));
        }
        else if (s == "http://www.w3.org/TR/REC-xml") {
            if ("1.1".equals(s2)) {
                ++this.freeXML11DTDValidatorIndex;
                if (this.xml11DTDValidators.length == this.freeXML11DTDValidatorIndex) {
                    this.xml11DTDValidatorsCurrentSize += 2;
                    final SoftReference[] xml11DTDValidators = new SoftReference[this.xml11DTDValidatorsCurrentSize];
                    System.arraycopy(this.xml11DTDValidators, 0, xml11DTDValidators, 0, this.xml11DTDValidators.length);
                    this.xml11DTDValidators = xml11DTDValidators;
                }
                final SoftReference softReference2 = this.xml11DTDValidators[this.freeXML11DTDValidatorIndex];
                if (softReference2 != null) {
                    final RevalidationHandlerHolder revalidationHandlerHolder2 = softReference2.get();
                    if (revalidationHandlerHolder2 != null) {
                        revalidationHandlerHolder2.handler = handler;
                        return;
                    }
                }
                this.xml11DTDValidators[this.freeXML11DTDValidatorIndex] = new SoftReference((T)new RevalidationHandlerHolder(handler));
            }
            else {
                ++this.freeXML10DTDValidatorIndex;
                if (this.xml10DTDValidators.length == this.freeXML10DTDValidatorIndex) {
                    this.xml10DTDValidatorsCurrentSize += 2;
                    final SoftReference[] xml10DTDValidators = new SoftReference[this.xml10DTDValidatorsCurrentSize];
                    System.arraycopy(this.xml10DTDValidators, 0, xml10DTDValidators, 0, this.xml10DTDValidators.length);
                    this.xml10DTDValidators = xml10DTDValidators;
                }
                final SoftReference softReference3 = this.xml10DTDValidators[this.freeXML10DTDValidatorIndex];
                if (softReference3 != null) {
                    final RevalidationHandlerHolder revalidationHandlerHolder3 = softReference3.get();
                    if (revalidationHandlerHolder3 != null) {
                        revalidationHandlerHolder3.handler = handler;
                        return;
                    }
                }
                this.xml10DTDValidators[this.freeXML10DTDValidatorIndex] = new SoftReference((T)new RevalidationHandlerHolder(handler));
            }
        }
    }
    
    final synchronized XMLDTDLoader getDTDLoader(final String s) {
        if ("1.1".equals(s)) {
            while (this.freeXML11DTDLoaderIndex >= 0) {
                final XMLDTDLoaderHolder xmldtdLoaderHolder = this.xml11DTDLoaders[this.freeXML11DTDLoaderIndex].get();
                if (xmldtdLoaderHolder != null && xmldtdLoaderHolder.loader != null) {
                    final XMLDTDLoader loader = xmldtdLoaderHolder.loader;
                    xmldtdLoaderHolder.loader = null;
                    --this.freeXML11DTDLoaderIndex;
                    return loader;
                }
                this.xml11DTDLoaders[this.freeXML11DTDLoaderIndex--] = null;
            }
            return (XMLDTDLoader)ObjectFactory.newInstance("org.apache.xerces.impl.dtd.XML11DTDProcessor", ObjectFactory.findClassLoader(), true);
        }
        while (this.freeXML10DTDLoaderIndex >= 0) {
            final XMLDTDLoaderHolder xmldtdLoaderHolder2 = this.xml10DTDLoaders[this.freeXML10DTDLoaderIndex].get();
            if (xmldtdLoaderHolder2 != null && xmldtdLoaderHolder2.loader != null) {
                final XMLDTDLoader loader2 = xmldtdLoaderHolder2.loader;
                xmldtdLoaderHolder2.loader = null;
                --this.freeXML10DTDLoaderIndex;
                return loader2;
            }
            this.xml10DTDLoaders[this.freeXML10DTDLoaderIndex--] = null;
        }
        return new XMLDTDLoader();
    }
    
    final synchronized void releaseDTDLoader(final String s, final XMLDTDLoader xmldtdLoader) {
        if ("1.1".equals(s)) {
            ++this.freeXML11DTDLoaderIndex;
            if (this.xml11DTDLoaders.length == this.freeXML11DTDLoaderIndex) {
                this.xml11DTDLoaderCurrentSize += 2;
                final SoftReference[] xml11DTDLoaders = new SoftReference[this.xml11DTDLoaderCurrentSize];
                System.arraycopy(this.xml11DTDLoaders, 0, xml11DTDLoaders, 0, this.xml11DTDLoaders.length);
                this.xml11DTDLoaders = xml11DTDLoaders;
            }
            final SoftReference softReference = this.xml11DTDLoaders[this.freeXML11DTDLoaderIndex];
            if (softReference != null) {
                final XMLDTDLoaderHolder xmldtdLoaderHolder = softReference.get();
                if (xmldtdLoaderHolder != null) {
                    xmldtdLoaderHolder.loader = xmldtdLoader;
                    return;
                }
            }
            this.xml11DTDLoaders[this.freeXML11DTDLoaderIndex] = new SoftReference((T)new XMLDTDLoaderHolder(xmldtdLoader));
        }
        else {
            ++this.freeXML10DTDLoaderIndex;
            if (this.xml10DTDLoaders.length == this.freeXML10DTDLoaderIndex) {
                this.xml10DTDLoaderCurrentSize += 2;
                final SoftReference[] xml10DTDLoaders = new SoftReference[this.xml10DTDLoaderCurrentSize];
                System.arraycopy(this.xml10DTDLoaders, 0, xml10DTDLoaders, 0, this.xml10DTDLoaders.length);
                this.xml10DTDLoaders = xml10DTDLoaders;
            }
            final SoftReference softReference2 = this.xml10DTDLoaders[this.freeXML10DTDLoaderIndex];
            if (softReference2 != null) {
                final XMLDTDLoaderHolder xmldtdLoaderHolder2 = softReference2.get();
                if (xmldtdLoaderHolder2 != null) {
                    xmldtdLoaderHolder2.loader = xmldtdLoader;
                    return;
                }
            }
            this.xml10DTDLoaders[this.freeXML10DTDLoaderIndex] = new SoftReference((T)new XMLDTDLoaderHolder(xmldtdLoader));
        }
    }
    
    protected synchronized int assignDocumentNumber() {
        return ++this.docAndDoctypeCounter;
    }
    
    protected synchronized int assignDocTypeNumber() {
        return ++this.docAndDoctypeCounter;
    }
    
    public LSOutput createLSOutput() {
        return new DOMOutputImpl();
    }
    
    static {
        singleton = new CoreDOMImplementationImpl();
    }
    
    static final class RevalidationHandlerHolder
    {
        RevalidationHandler handler;
        
        RevalidationHandlerHolder(final RevalidationHandler handler) {
            this.handler = handler;
        }
    }
    
    static final class XMLDTDLoaderHolder
    {
        XMLDTDLoader loader;
        
        XMLDTDLoaderHolder(final XMLDTDLoader loader) {
            this.loader = loader;
        }
    }
}
