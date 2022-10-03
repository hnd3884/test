package com.sun.xml.internal.messaging.saaj.soap.name;

import org.w3c.dom.Element;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import java.util.logging.Logger;
import javax.xml.soap.Name;

public class NameImpl implements Name
{
    public static final String XML_NAMESPACE_PREFIX = "xml";
    public static final String XML_SCHEMA_NAMESPACE_PREFIX = "xs";
    public static final String SOAP_ENVELOPE_PREFIX = "SOAP-ENV";
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    public static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
    public static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    protected String uri;
    protected String localName;
    protected String prefix;
    private String qualifiedName;
    protected static final Logger log;
    public static final String XMLNS_URI;
    
    protected NameImpl(final String name) {
        this.uri = "";
        this.localName = "";
        this.prefix = "";
        this.qualifiedName = null;
        this.localName = ((name == null) ? "" : name);
    }
    
    protected NameImpl(final String name, final String prefix, final String uri) {
        this.uri = "";
        this.localName = "";
        this.prefix = "";
        this.qualifiedName = null;
        this.uri = ((uri == null) ? "" : uri);
        this.localName = ((name == null) ? "" : name);
        this.prefix = ((prefix == null) ? "" : prefix);
        if (this.prefix.equals("xmlns") && this.uri.equals("")) {
            this.uri = NameImpl.XMLNS_URI;
        }
        if (this.uri.equals(NameImpl.XMLNS_URI) && this.prefix.equals("")) {
            this.prefix = "xmlns";
        }
    }
    
    public static Name convertToName(final QName qname) {
        return new NameImpl(qname.getLocalPart(), qname.getPrefix(), qname.getNamespaceURI());
    }
    
    public static QName convertToQName(final Name name) {
        return new QName(name.getURI(), name.getLocalName(), name.getPrefix());
    }
    
    public static NameImpl createFromUnqualifiedName(final String name) {
        return new NameImpl(name);
    }
    
    public static Name createFromTagName(final String tagName) {
        return createFromTagAndUri(tagName, "");
    }
    
    public static Name createFromQualifiedName(final String qualifiedName, final String uri) {
        return createFromTagAndUri(qualifiedName, uri);
    }
    
    protected static Name createFromTagAndUri(final String tagName, final String uri) {
        if (tagName == null) {
            NameImpl.log.severe("SAAJ0201.name.not.created.from.null.tag");
            throw new IllegalArgumentException("Cannot create a name from a null tag.");
        }
        final int index = tagName.indexOf(58);
        if (index < 0) {
            return new NameImpl(tagName, "", uri);
        }
        return new NameImpl(tagName.substring(index + 1), tagName.substring(0, index), uri);
    }
    
    protected static int getPrefixSeparatorIndex(final String qualifiedName) {
        final int index = qualifiedName.indexOf(58);
        if (index < 0) {
            NameImpl.log.log(Level.SEVERE, "SAAJ0202.name.invalid.arg.format", new String[] { qualifiedName });
            throw new IllegalArgumentException("Argument \"" + qualifiedName + "\" must be of the form: \"prefix:localName\"");
        }
        return index;
    }
    
    public static String getPrefixFromQualifiedName(final String qualifiedName) {
        return qualifiedName.substring(0, getPrefixSeparatorIndex(qualifiedName));
    }
    
    public static String getLocalNameFromQualifiedName(final String qualifiedName) {
        return qualifiedName.substring(getPrefixSeparatorIndex(qualifiedName) + 1);
    }
    
    public static String getPrefixFromTagName(final String tagName) {
        if (isQualified(tagName)) {
            return getPrefixFromQualifiedName(tagName);
        }
        return "";
    }
    
    public static String getLocalNameFromTagName(final String tagName) {
        if (isQualified(tagName)) {
            return getLocalNameFromQualifiedName(tagName);
        }
        return tagName;
    }
    
    public static boolean isQualified(final String tagName) {
        return tagName.indexOf(58) >= 0;
    }
    
    public static NameImpl create(String name, String prefix, String uri) {
        if (prefix == null) {
            prefix = "";
        }
        if (uri == null) {
            uri = "";
        }
        if (name == null) {
            name = "";
        }
        if (!uri.equals("") && !name.equals("")) {
            if (uri.equals("http://schemas.xmlsoap.org/soap/envelope/")) {
                if (name.equalsIgnoreCase("Envelope")) {
                    return createEnvelope1_1Name(prefix);
                }
                if (name.equalsIgnoreCase("Header")) {
                    return createHeader1_1Name(prefix);
                }
                if (name.equalsIgnoreCase("Body")) {
                    return createBody1_1Name(prefix);
                }
                if (name.equalsIgnoreCase("Fault")) {
                    return createFault1_1Name(prefix);
                }
                return new SOAP1_1Name(name, prefix);
            }
            else if (uri.equals("http://www.w3.org/2003/05/soap-envelope")) {
                if (name.equalsIgnoreCase("Envelope")) {
                    return createEnvelope1_2Name(prefix);
                }
                if (name.equalsIgnoreCase("Header")) {
                    return createHeader1_2Name(prefix);
                }
                if (name.equalsIgnoreCase("Body")) {
                    return createBody1_2Name(prefix);
                }
                if (name.equals("Fault") || name.equals("Reason") || name.equals("Detail")) {
                    return createFault1_2Name(name, prefix);
                }
                if (name.equals("Code") || name.equals("Subcode")) {
                    return createCodeSubcode1_2Name(prefix, name);
                }
                return new SOAP1_2Name(name, prefix);
            }
        }
        return new NameImpl(name, prefix, uri);
    }
    
    public static String createQName(final String prefix, final String localName) {
        if (prefix == null || prefix.equals("")) {
            return localName;
        }
        return prefix + ":" + localName;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Name)) {
            return false;
        }
        final Name otherName = (Name)obj;
        return this.uri.equals(otherName.getURI()) && this.localName.equals(otherName.getLocalName());
    }
    
    @Override
    public int hashCode() {
        return this.localName.hashCode();
    }
    
    @Override
    public String getLocalName() {
        return this.localName;
    }
    
    @Override
    public String getPrefix() {
        return this.prefix;
    }
    
    @Override
    public String getURI() {
        return this.uri;
    }
    
    @Override
    public String getQualifiedName() {
        if (this.qualifiedName == null) {
            if (this.prefix != null && this.prefix.length() > 0) {
                this.qualifiedName = this.prefix + ":" + this.localName;
            }
            else {
                this.qualifiedName = this.localName;
            }
        }
        return this.qualifiedName;
    }
    
    public static NameImpl createEnvelope1_1Name(final String prefix) {
        return new Envelope1_1Name(prefix);
    }
    
    public static NameImpl createEnvelope1_2Name(final String prefix) {
        return new Envelope1_2Name(prefix);
    }
    
    public static NameImpl createHeader1_1Name(final String prefix) {
        return new Header1_1Name(prefix);
    }
    
    public static NameImpl createHeader1_2Name(final String prefix) {
        return new Header1_2Name(prefix);
    }
    
    public static NameImpl createBody1_1Name(final String prefix) {
        return new Body1_1Name(prefix);
    }
    
    public static NameImpl createBody1_2Name(final String prefix) {
        return new Body1_2Name(prefix);
    }
    
    public static NameImpl createFault1_1Name(final String prefix) {
        return new Fault1_1Name(prefix);
    }
    
    public static NameImpl createNotUnderstood1_2Name(final String prefix) {
        return new NotUnderstood1_2Name(prefix);
    }
    
    public static NameImpl createUpgrade1_2Name(final String prefix) {
        return new Upgrade1_2Name(prefix);
    }
    
    public static NameImpl createSupportedEnvelope1_2Name(final String prefix) {
        return new SupportedEnvelope1_2Name(prefix);
    }
    
    public static NameImpl createFault1_2Name(final String localName, final String prefix) {
        return new Fault1_2Name(localName, prefix);
    }
    
    public static NameImpl createCodeSubcode1_2Name(final String prefix, final String localName) {
        return new CodeSubcode1_2Name(localName, prefix);
    }
    
    public static NameImpl createDetail1_1Name() {
        return new Detail1_1Name();
    }
    
    public static NameImpl createDetail1_1Name(final String prefix) {
        return new Detail1_1Name(prefix);
    }
    
    public static NameImpl createFaultElement1_1Name(final String localName) {
        return new FaultElement1_1Name(localName);
    }
    
    public static NameImpl createFaultElement1_1Name(final String localName, final String prefix) {
        return new FaultElement1_1Name(localName, prefix);
    }
    
    public static NameImpl createSOAP11Name(final String string) {
        return new SOAP1_1Name(string, null);
    }
    
    public static NameImpl createSOAP12Name(final String string) {
        return new SOAP1_2Name(string, null);
    }
    
    public static NameImpl createSOAP12Name(final String localName, final String prefix) {
        return new SOAP1_2Name(localName, prefix);
    }
    
    public static NameImpl createXmlName(final String localName) {
        return new NameImpl(localName, "xml", "http://www.w3.org/XML/1998/namespace");
    }
    
    public static Name copyElementName(final Element element) {
        final String localName = element.getLocalName();
        final String prefix = element.getPrefix();
        final String uri = element.getNamespaceURI();
        return create(localName, prefix, uri);
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.name", "com.sun.xml.internal.messaging.saaj.soap.name.LocalStrings");
        XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
    }
    
    static class SOAP1_1Name extends NameImpl
    {
        SOAP1_1Name(final String name, final String prefix) {
            super(name, (prefix == null || prefix.equals("")) ? "SOAP-ENV" : prefix, "http://schemas.xmlsoap.org/soap/envelope/");
        }
    }
    
    static class Envelope1_1Name extends SOAP1_1Name
    {
        Envelope1_1Name(final String prefix) {
            super("Envelope", prefix);
        }
    }
    
    static class Header1_1Name extends SOAP1_1Name
    {
        Header1_1Name(final String prefix) {
            super("Header", prefix);
        }
    }
    
    static class Body1_1Name extends SOAP1_1Name
    {
        Body1_1Name(final String prefix) {
            super("Body", prefix);
        }
    }
    
    static class Fault1_1Name extends NameImpl
    {
        Fault1_1Name(final String prefix) {
            super("Fault", (prefix == null || prefix.equals("")) ? "SOAP-ENV" : prefix, "http://schemas.xmlsoap.org/soap/envelope/");
        }
    }
    
    static class Detail1_1Name extends NameImpl
    {
        Detail1_1Name() {
            super("detail");
        }
        
        Detail1_1Name(final String prefix) {
            super("detail", prefix, "");
        }
    }
    
    static class FaultElement1_1Name extends NameImpl
    {
        FaultElement1_1Name(final String localName) {
            super(localName);
        }
        
        FaultElement1_1Name(final String localName, final String prefix) {
            super(localName, prefix, "");
        }
    }
    
    static class SOAP1_2Name extends NameImpl
    {
        SOAP1_2Name(final String name, final String prefix) {
            super(name, (prefix == null || prefix.equals("")) ? "env" : prefix, "http://www.w3.org/2003/05/soap-envelope");
        }
    }
    
    static class Envelope1_2Name extends SOAP1_2Name
    {
        Envelope1_2Name(final String prefix) {
            super("Envelope", prefix);
        }
    }
    
    static class Header1_2Name extends SOAP1_2Name
    {
        Header1_2Name(final String prefix) {
            super("Header", prefix);
        }
    }
    
    static class Body1_2Name extends SOAP1_2Name
    {
        Body1_2Name(final String prefix) {
            super("Body", prefix);
        }
    }
    
    static class Fault1_2Name extends NameImpl
    {
        Fault1_2Name(final String name, final String prefix) {
            super((name == null || name.equals("")) ? "Fault" : name, (prefix == null || prefix.equals("")) ? "env" : prefix, "http://www.w3.org/2003/05/soap-envelope");
        }
    }
    
    static class NotUnderstood1_2Name extends NameImpl
    {
        NotUnderstood1_2Name(final String prefix) {
            super("NotUnderstood", (prefix == null || prefix.equals("")) ? "env" : prefix, "http://www.w3.org/2003/05/soap-envelope");
        }
    }
    
    static class Upgrade1_2Name extends NameImpl
    {
        Upgrade1_2Name(final String prefix) {
            super("Upgrade", (prefix == null || prefix.equals("")) ? "env" : prefix, "http://www.w3.org/2003/05/soap-envelope");
        }
    }
    
    static class SupportedEnvelope1_2Name extends NameImpl
    {
        SupportedEnvelope1_2Name(final String prefix) {
            super("SupportedEnvelope", (prefix == null || prefix.equals("")) ? "env" : prefix, "http://www.w3.org/2003/05/soap-envelope");
        }
    }
    
    static class CodeSubcode1_2Name extends SOAP1_2Name
    {
        CodeSubcode1_2Name(final String prefix, final String localName) {
            super(prefix, localName);
        }
    }
}
