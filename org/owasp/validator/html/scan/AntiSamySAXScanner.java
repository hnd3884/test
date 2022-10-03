package org.owasp.validator.html.scan;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import javax.xml.transform.TransformerConfigurationException;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import javax.xml.transform.Transformer;
import org.cyberneko.html.parsers.SAXParser;
import java.util.Collection;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.XMLReader;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.w3c.dom.DocumentFragment;
import java.util.concurrent.Callable;
import java.io.Writer;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import javax.xml.transform.TransformerFactory;
import java.util.Queue;

public class AntiSamySAXScanner extends AbstractAntiSamyScanner
{
    private static final Queue<CachedItem> cachedItems;
    private static final TransformerFactory sTransformerFactory;
    
    public AntiSamySAXScanner(final Policy policy) {
        super(policy);
    }
    
    @Override
    public CleanResults getResults() {
        return null;
    }
    
    @Override
    public CleanResults scan(final String html) throws ScanException {
        return this.scan(html, this.policy);
    }
    
    public CleanResults scan(final String html, final Policy policy) throws ScanException {
        if (html == null) {
            throw new ScanException(new NullPointerException("Null html input"));
        }
        final int maxInputSize = this.policy.getMaxInputSize();
        if (html.length() > maxInputSize) {
            this.addError("error.size.toolarge", new Object[] { html.length(), maxInputSize });
            throw new ScanException(this.errorMessages.get(0));
        }
        final StringWriter out = new StringWriter();
        final StringReader reader = new StringReader(html);
        final CleanResults results = this.scan(reader, out);
        final String tainted = html;
        final Callable<String> cleanCallable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return AntiSamySAXScanner.this.trim(tainted, out.toString());
            }
        };
        return new CleanResults(results.getStartOfScan(), cleanCallable, null, results.getErrorMessages());
    }
    
    public CleanResults scan(final Reader reader, final Writer writer) throws ScanException {
        try {
            CachedItem candidateCachedItem = AntiSamySAXScanner.cachedItems.poll();
            if (candidateCachedItem == null) {
                candidateCachedItem = new CachedItem(getNewTransformer(), getParser(), new MagicSAXFilter(AntiSamySAXScanner.messages));
            }
            final CachedItem cachedItem = candidateCachedItem;
            final SAXParser parser = cachedItem.saxParser;
            String attributeLimit = this.policy.getAttributeLimit();
            if (attributeLimit == null) {
                attributeLimit = "1000";
            }
            parser.setProperty("http://cyberneko.org/html/zoho/features/attribute-limit", (Object)attributeLimit);
            cachedItem.magicSAXFilter.reset(this.policy);
            final long startOfScan = System.currentTimeMillis();
            final SAXSource source = new SAXSource((XMLReader)parser, new InputSource(reader));
            final Transformer transformer = cachedItem.transformer;
            final boolean formatOutput = this.policy.isFormatOutput();
            final boolean useXhtml = this.policy.isUseXhtml();
            final boolean omitXml = this.policy.isOmitXmlDeclaration();
            transformer.setOutputProperty("indent", formatOutput ? "yes" : "no");
            transformer.setOutputProperty("omit-xml-declaration", omitXml ? "yes" : "no");
            transformer.setOutputProperty("method", useXhtml ? "xml" : "html");
            final OutputFormat format = this.getOutputFormat();
            final HTMLSerializer serializer = this.getHTMLSerializer(writer, format);
            transformer.transform(source, new SAXResult((ContentHandler)serializer));
            this.errorMessages.clear();
            this.errorMessages.addAll(cachedItem.magicSAXFilter.getErrorMessages());
            AntiSamySAXScanner.cachedItems.add(cachedItem);
            return new CleanResults(startOfScan, (String)null, null, this.errorMessages);
        }
        catch (final Exception e) {
            throw new ScanException(e);
        }
    }
    
    private static synchronized Transformer getNewTransformer() {
        try {
            return AntiSamySAXScanner.sTransformerFactory.newTransformer();
        }
        catch (final TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static SAXParser getParser() {
        try {
            final SAXParser parser = new SAXParser();
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
            parser.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
            parser.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", (Object)"lower");
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", (Object)"match");
            parser.setProperty("http://cyberneko.org/html/properties/names/attrs", (Object)"no-change");
            return parser;
        }
        catch (final SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        cachedItems = new ConcurrentLinkedQueue<CachedItem>();
        (sTransformerFactory = TransformerFactory.newInstance()).setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        AntiSamySAXScanner.sTransformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
    }
    
    static class CachedItem
    {
        private final Transformer transformer;
        private final SAXParser saxParser;
        private final MagicSAXFilter magicSAXFilter;
        
        CachedItem(final Transformer transformer, final SAXParser saxParser, final MagicSAXFilter magicSAXFilter) {
            this.transformer = transformer;
            this.saxParser = saxParser;
            this.magicSAXFilter = magicSAXFilter;
            final XMLDocumentFilter[] filters = { (XMLDocumentFilter)magicSAXFilter };
            try {
                saxParser.setProperty("http://cyberneko.org/html/properties/filters", (Object)filters);
            }
            catch (final SAXNotRecognizedException | SAXNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
