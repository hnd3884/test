package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSInput;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import org.w3c.dom.ls.LSSerializer;
import com.sun.org.apache.xerces.internal.parsers.XIncludeAwareParserConfiguration;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.parsers.DOMParserImpl;
import com.sun.org.apache.xerces.internal.parsers.DTDConfiguration;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.DOMImplementation;

public class CoreDOMImplementationImpl implements DOMImplementation, DOMImplementationLS
{
    private static final int SIZE = 2;
    private RevalidationHandler[] validators;
    private RevalidationHandler[] dtdValidators;
    private int freeValidatorIndex;
    private int freeDTDValidatorIndex;
    private int currentSize;
    private int docAndDoctypeCounter;
    static CoreDOMImplementationImpl singleton;
    
    public CoreDOMImplementationImpl() {
        this.validators = new RevalidationHandler[2];
        this.dtdValidators = new RevalidationHandler[2];
        this.freeValidatorIndex = -1;
        this.freeDTDValidatorIndex = -1;
        this.currentSize = 2;
        this.docAndDoctypeCounter = 0;
    }
    
    public static DOMImplementation getDOMImplementation() {
        return CoreDOMImplementationImpl.singleton;
    }
    
    @Override
    public boolean hasFeature(String feature, final String version) {
        final boolean anyVersion = version == null || version.length() == 0;
        Label_0098: {
            if (feature.equalsIgnoreCase("+XPath")) {
                if (!anyVersion) {
                    if (!version.equals("3.0")) {
                        break Label_0098;
                    }
                }
                try {
                    final Class xpathClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
                    final Class[] interfaces = xpathClass.getInterfaces();
                    for (int i = 0; i < interfaces.length; ++i) {
                        if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                            return true;
                        }
                    }
                }
                catch (final Exception e) {
                    return false;
                }
                return true;
            }
        }
        if (feature.startsWith("+")) {
            feature = feature.substring(1);
        }
        return (feature.equalsIgnoreCase("Core") && (anyVersion || version.equals("1.0") || version.equals("2.0") || version.equals("3.0"))) || (feature.equalsIgnoreCase("XML") && (anyVersion || version.equals("1.0") || version.equals("2.0") || version.equals("3.0"))) || (feature.equalsIgnoreCase("LS") && (anyVersion || version.equals("3.0")));
    }
    
    @Override
    public DocumentType createDocumentType(final String qualifiedName, final String publicID, final String systemID) {
        this.checkQName(qualifiedName);
        return new DocumentTypeImpl(null, qualifiedName, publicID, systemID);
    }
    
    final void checkQName(final String qname) {
        final int index = qname.indexOf(58);
        final int lastIndex = qname.lastIndexOf(58);
        final int length = qname.length();
        if (index == 0 || index == length - 1 || lastIndex != index) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException((short)14, msg);
        }
        int start = 0;
        if (index > 0) {
            if (!XMLChar.isNCNameStart(qname.charAt(start))) {
                final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException((short)5, msg2);
            }
            for (int i = 1; i < index; ++i) {
                if (!XMLChar.isNCName(qname.charAt(i))) {
                    final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                    throw new DOMException((short)5, msg3);
                }
            }
            start = index + 1;
        }
        if (!XMLChar.isNCNameStart(qname.charAt(start))) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg2);
        }
        for (int i = start + 1; i < length; ++i) {
            if (!XMLChar.isNCName(qname.charAt(i))) {
                final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException((short)5, msg3);
            }
        }
    }
    
    @Override
    public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
        if (doctype != null && doctype.getOwnerDocument() != null) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException((short)4, msg);
        }
        final CoreDocumentImpl doc = new CoreDocumentImpl(doctype);
        final Element e = doc.createElementNS(namespaceURI, qualifiedName);
        doc.appendChild(e);
        return doc;
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        if (CoreDOMImplementationImpl.singleton.hasFeature(feature, version)) {
            if (feature.equalsIgnoreCase("+XPath")) {
                try {
                    final Class xpathClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
                    final Class[] interfaces = xpathClass.getInterfaces();
                    for (int i = 0; i < interfaces.length; ++i) {
                        if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                            return xpathClass.newInstance();
                        }
                    }
                    return null;
                }
                catch (final Exception e) {
                    return null;
                }
            }
            return CoreDOMImplementationImpl.singleton;
        }
        return null;
    }
    
    @Override
    public LSParser createLSParser(final short mode, final String schemaType) throws DOMException {
        if (mode != 1 || (schemaType != null && !"http://www.w3.org/2001/XMLSchema".equals(schemaType) && !"http://www.w3.org/TR/REC-xml".equals(schemaType))) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException((short)9, msg);
        }
        if (schemaType != null && schemaType.equals("http://www.w3.org/TR/REC-xml")) {
            return new DOMParserImpl(new DTDConfiguration(), schemaType);
        }
        return new DOMParserImpl(new XIncludeAwareParserConfiguration(), schemaType);
    }
    
    @Override
    public LSSerializer createLSSerializer() {
        return new DOMSerializerImpl();
    }
    
    @Override
    public LSInput createLSInput() {
        return new DOMInputImpl();
    }
    
    synchronized RevalidationHandler getValidator(final String schemaType) {
        if (schemaType == "http://www.w3.org/2001/XMLSchema") {
            if (this.freeValidatorIndex < 0) {
                return (RevalidationHandler)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator", ObjectFactory.findClassLoader(), true);
            }
            final RevalidationHandler val = this.validators[this.freeValidatorIndex];
            this.validators[this.freeValidatorIndex--] = null;
            return val;
        }
        else {
            if (schemaType != "http://www.w3.org/TR/REC-xml") {
                return null;
            }
            if (this.freeDTDValidatorIndex < 0) {
                return (RevalidationHandler)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator", ObjectFactory.findClassLoader(), true);
            }
            final RevalidationHandler val = this.dtdValidators[this.freeDTDValidatorIndex];
            this.dtdValidators[this.freeDTDValidatorIndex--] = null;
            return val;
        }
    }
    
    synchronized void releaseValidator(final String schemaType, final RevalidationHandler validator) {
        if (schemaType == "http://www.w3.org/2001/XMLSchema") {
            ++this.freeValidatorIndex;
            if (this.validators.length == this.freeValidatorIndex) {
                this.currentSize += 2;
                final RevalidationHandler[] newarray = new RevalidationHandler[this.currentSize];
                System.arraycopy(this.validators, 0, newarray, 0, this.validators.length);
                this.validators = newarray;
            }
            this.validators[this.freeValidatorIndex] = validator;
        }
        else if (schemaType == "http://www.w3.org/TR/REC-xml") {
            ++this.freeDTDValidatorIndex;
            if (this.dtdValidators.length == this.freeDTDValidatorIndex) {
                this.currentSize += 2;
                final RevalidationHandler[] newarray = new RevalidationHandler[this.currentSize];
                System.arraycopy(this.dtdValidators, 0, newarray, 0, this.dtdValidators.length);
                this.dtdValidators = newarray;
            }
            this.dtdValidators[this.freeDTDValidatorIndex] = validator;
        }
    }
    
    protected synchronized int assignDocumentNumber() {
        return ++this.docAndDoctypeCounter;
    }
    
    protected synchronized int assignDocTypeNumber() {
        return ++this.docAndDoctypeCounter;
    }
    
    @Override
    public LSOutput createLSOutput() {
        return new DOMOutputImpl();
    }
    
    static {
        CoreDOMImplementationImpl.singleton = new CoreDOMImplementationImpl();
    }
}
