package org.cyberneko.html.filters;

import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import java.util.Vector;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.HTMLEntities;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.cyberneko.html.HTMLElements;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Writer extends DefaultFilter
{
    public static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    public static final String NOTIFY_HTML_BUILTIN_REFS = "http://cyberneko.org/html/features/scanner/notify-builtin-refs";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String FILTERS = "http://cyberneko.org/html/properties/filters";
    protected String fEncoding;
    protected PrintWriter fPrinter;
    protected boolean fSeenRootElement;
    protected boolean fSeenHttpEquiv;
    protected int fElementDepth;
    protected boolean fNormalize;
    protected boolean fPrintChars;
    
    public Writer() {
        try {
            this.fEncoding = "UTF-8";
            this.fPrinter = new PrintWriter(new OutputStreamWriter(System.out, this.fEncoding));
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public Writer(final OutputStream outputStream, final String encoding) throws UnsupportedEncodingException {
        this(new OutputStreamWriter(outputStream, encoding), encoding);
    }
    
    public Writer(final java.io.Writer writer, final String encoding) {
        this.fEncoding = encoding;
        if (writer instanceof PrintWriter) {
            this.fPrinter = (PrintWriter)writer;
        }
        else {
            this.fPrinter = new PrintWriter(writer);
        }
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) throws XNIException {
        this.fSeenRootElement = false;
        this.fSeenHttpEquiv = false;
        this.fElementDepth = 0;
        this.fNormalize = true;
        this.fPrintChars = true;
        super.startDocument(locator, encoding, nscontext, augs);
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }
    
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fSeenRootElement && this.fElementDepth <= 0) {
            this.fPrinter.println();
        }
        this.fPrinter.print("<!--");
        this.printCharacters(text, false);
        this.fPrinter.print("-->");
        if (!this.fSeenRootElement) {
            this.fPrinter.println();
        }
        this.fPrinter.flush();
    }
    
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.fSeenRootElement = true;
        ++this.fElementDepth;
        this.fNormalize = !HTMLElements.getElement(element.rawname).isSpecial();
        this.printStartElement(element, attributes);
        super.startElement(element, attributes, augs);
    }
    
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.fSeenRootElement = true;
        this.printStartElement(element, attributes);
        super.emptyElement(element, attributes, augs);
    }
    
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fPrintChars) {
            this.printCharacters(text, this.fNormalize);
        }
        super.characters(text, augs);
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        --this.fElementDepth;
        this.fNormalize = true;
        this.printEndElement(element);
        super.endElement(element, augs);
    }
    
    public void startGeneralEntity(String name, final XMLResourceIdentifier id, final String encoding, final Augmentations augs) throws XNIException {
        this.fPrintChars = false;
        if (name.startsWith("#")) {
            try {
                final boolean hex = name.startsWith("#x");
                final int offset = hex ? 2 : 1;
                final int base = hex ? 16 : 10;
                final int value = Integer.parseInt(name.substring(offset), base);
                final String entity = HTMLEntities.get(value);
                if (entity != null) {
                    name = entity;
                }
            }
            catch (final NumberFormatException ex) {}
        }
        this.printEntity(name);
        super.startGeneralEntity(name, id, encoding, augs);
    }
    
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        this.fPrintChars = true;
        super.endGeneralEntity(name, augs);
    }
    
    protected void printAttributeValue(final String text) {
        for (int length = text.length(), j = 0; j < length; ++j) {
            final char c = text.charAt(j);
            if (c == '\"') {
                this.fPrinter.print("&quot;");
            }
            else {
                this.fPrinter.print(c);
            }
        }
        this.fPrinter.flush();
    }
    
    protected void printCharacters(final XMLString text, final boolean normalize) {
        if (normalize) {
            for (int i = 0; i < text.length; ++i) {
                final char c = text.ch[text.offset + i];
                if (c != '\n') {
                    final String entity = HTMLEntities.get(c);
                    if (entity != null) {
                        this.printEntity(entity);
                    }
                    else {
                        this.fPrinter.print(c);
                    }
                }
                else {
                    this.fPrinter.println();
                }
            }
        }
        else {
            for (int i = 0; i < text.length; ++i) {
                final char c = text.ch[text.offset + i];
                this.fPrinter.print(c);
            }
        }
        this.fPrinter.flush();
    }
    
    protected void printStartElement(final QName element, final XMLAttributes attributes) {
        int contentIndex = -1;
        String originalContent = null;
        if (element.rawname.toLowerCase().equals("meta")) {
            String httpEquiv = null;
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final String aname = attributes.getQName(i).toLowerCase();
                if (aname.equals("http-equiv")) {
                    httpEquiv = attributes.getValue(i);
                }
                else if (aname.equals("content")) {
                    contentIndex = i;
                }
            }
            if (httpEquiv != null && httpEquiv.toLowerCase().equals("content-type")) {
                this.fSeenHttpEquiv = true;
                String content = null;
                if (contentIndex != -1) {
                    originalContent = attributes.getValue(contentIndex);
                    content = originalContent.toLowerCase();
                }
                if (content != null) {
                    final int charsetIndex = content.indexOf("charset=");
                    if (charsetIndex != -1) {
                        content = content.substring(0, charsetIndex + 8);
                    }
                    else {
                        content += ";charset=";
                    }
                    content += this.fEncoding;
                    attributes.setValue(contentIndex, content);
                }
            }
        }
        this.fPrinter.print('<');
        this.fPrinter.print(element.rawname);
        for (int attrCount = (attributes != null) ? attributes.getLength() : 0, j = 0; j < attrCount; ++j) {
            final String aname2 = attributes.getQName(j);
            final String avalue = attributes.getValue(j);
            this.fPrinter.print(' ');
            this.fPrinter.print(aname2);
            this.fPrinter.print("=\"");
            this.printAttributeValue(avalue);
            this.fPrinter.print('\"');
        }
        this.fPrinter.print('>');
        this.fPrinter.flush();
        if (contentIndex != -1 && originalContent != null) {
            attributes.setValue(contentIndex, originalContent);
        }
    }
    
    protected void printEndElement(final QName element) {
        this.fPrinter.print("</");
        this.fPrinter.print(element.rawname);
        this.fPrinter.print('>');
        this.fPrinter.flush();
    }
    
    protected void printEntity(final String name) {
        this.fPrinter.print('&');
        this.fPrinter.print(name);
        this.fPrinter.print(';');
        this.fPrinter.flush();
    }
    
    public static void main(final String[] argv) throws Exception {
        if (argv.length == 0) {
            printUsage();
            System.exit(1);
        }
        final XMLParserConfiguration parser = (XMLParserConfiguration)new HTMLConfiguration();
        parser.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
        parser.setFeature("http://cyberneko.org/html/features/scanner/notify-builtin-refs", true);
        String iencoding = null;
        String oencoding = "Windows-1252";
        boolean identity = false;
        boolean purify = false;
        for (int i = 0; i < argv.length; ++i) {
            final String arg = argv[i];
            if (arg.equals("-ie")) {
                iencoding = argv[++i];
            }
            else if (arg.equals("-e") || arg.equals("-oe")) {
                oencoding = argv[++i];
            }
            else if (arg.equals("-i")) {
                identity = true;
            }
            else if (arg.equals("-p")) {
                purify = true;
            }
            else {
                if (arg.equals("-h")) {
                    printUsage();
                    System.exit(1);
                }
                final Vector filtersVector = new Vector(2);
                if (identity) {
                    filtersVector.addElement(new Identity());
                }
                else if (purify) {
                    filtersVector.addElement(new Purifier());
                }
                filtersVector.addElement(new Writer(System.out, oencoding));
                final XMLDocumentFilter[] filters = new XMLDocumentFilter[filtersVector.size()];
                filtersVector.copyInto(filters);
                parser.setProperty("http://cyberneko.org/html/properties/filters", (Object)filters);
                final XMLInputSource source = new XMLInputSource((String)null, arg, (String)null);
                source.setEncoding(iencoding);
                parser.parse(source);
            }
        }
    }
    
    private static void printUsage() {
        System.err.println("usage: java " + Writer.class.getName() + " (options) file ...");
        System.err.println();
        System.err.println("options:");
        System.err.println("  -ie name  Specify IANA name of input encoding.");
        System.err.println("  -oe name  Specify IANA name of output encoding.");
        System.err.println("  -i        Perform identity transform.");
        System.err.println("  -p        Purify output to ensure XML well-formedness.");
        System.err.println("  -h        Display help screen.");
        System.err.println();
        System.err.println("notes:");
        System.err.println("  The -i and -p options are mutually exclusive.");
        System.err.println("  The -e option has been replaced with -oe.");
    }
}
