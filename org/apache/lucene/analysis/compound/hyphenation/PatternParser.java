package org.apache.lucene.analysis.compound.hyphenation;

import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import java.util.ArrayList;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class PatternParser extends DefaultHandler
{
    XMLReader parser;
    int currElement;
    PatternConsumer consumer;
    StringBuilder token;
    ArrayList<Object> exception;
    char hyphenChar;
    String errMsg;
    static final int ELEM_CLASSES = 1;
    static final int ELEM_EXCEPTIONS = 2;
    static final int ELEM_PATTERNS = 3;
    static final int ELEM_HYPHEN = 4;
    
    public PatternParser() {
        this.token = new StringBuilder();
        (this.parser = createParser()).setContentHandler(this);
        this.parser.setErrorHandler(this);
        this.parser.setEntityResolver(this);
        this.hyphenChar = '-';
    }
    
    public PatternParser(final PatternConsumer consumer) {
        this();
        this.consumer = consumer;
    }
    
    public void setConsumer(final PatternConsumer consumer) {
        this.consumer = consumer;
    }
    
    public void parse(final String filename) throws IOException {
        this.parse(new InputSource(filename));
    }
    
    public void parse(final InputSource source) throws IOException {
        try {
            this.parser.parse(source);
        }
        catch (final SAXException e) {
            throw new IOException(e);
        }
    }
    
    static XMLReader createParser() {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newSAXParser().getXMLReader();
        }
        catch (final Exception e) {
            throw new RuntimeException("Couldn't create XMLReader: " + e.getMessage());
        }
    }
    
    protected String readToken(final StringBuilder chars) {
        boolean space = false;
        int i;
        for (i = 0; i < chars.length() && Character.isWhitespace(chars.charAt(i)); ++i) {
            space = true;
        }
        if (space) {
            for (int countr = i; countr < chars.length(); ++countr) {
                chars.setCharAt(countr - i, chars.charAt(countr));
            }
            chars.setLength(chars.length() - i);
            if (this.token.length() > 0) {
                final String word = this.token.toString();
                this.token.setLength(0);
                return word;
            }
        }
        space = false;
        for (i = 0; i < chars.length(); ++i) {
            if (Character.isWhitespace(chars.charAt(i))) {
                space = true;
                break;
            }
        }
        this.token.append(chars.toString().substring(0, i));
        for (int countr = i; countr < chars.length(); ++countr) {
            chars.setCharAt(countr - i, chars.charAt(countr));
        }
        chars.setLength(chars.length() - i);
        if (space) {
            final String word = this.token.toString();
            this.token.setLength(0);
            return word;
        }
        this.token.append((CharSequence)chars);
        return null;
    }
    
    protected static String getPattern(final String word) {
        final StringBuilder pat = new StringBuilder();
        for (int len = word.length(), i = 0; i < len; ++i) {
            if (!Character.isDigit(word.charAt(i))) {
                pat.append(word.charAt(i));
            }
        }
        return pat.toString();
    }
    
    protected ArrayList<Object> normalizeException(final ArrayList<?> ex) {
        final ArrayList<Object> res = new ArrayList<Object>();
        for (int i = 0; i < ex.size(); ++i) {
            final Object item = ex.get(i);
            if (item instanceof String) {
                final String str = (String)item;
                final StringBuilder buf = new StringBuilder();
                for (int j = 0; j < str.length(); ++j) {
                    final char c = str.charAt(j);
                    if (c != this.hyphenChar) {
                        buf.append(c);
                    }
                    else {
                        res.add(buf.toString());
                        buf.setLength(0);
                        final char[] h = { this.hyphenChar };
                        res.add(new Hyphen(new String(h), null, null));
                    }
                }
                if (buf.length() > 0) {
                    res.add(buf.toString());
                }
            }
            else {
                res.add(item);
            }
        }
        return res;
    }
    
    protected String getExceptionWord(final ArrayList<?> ex) {
        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < ex.size(); ++i) {
            final Object item = ex.get(i);
            if (item instanceof String) {
                res.append((String)item);
            }
            else if (((Hyphen)item).noBreak != null) {
                res.append(((Hyphen)item).noBreak);
            }
        }
        return res.toString();
    }
    
    protected static String getInterletterValues(final String pat) {
        final StringBuilder il = new StringBuilder();
        final String word = pat + "a";
        for (int len = word.length(), i = 0; i < len; ++i) {
            final char c = word.charAt(i);
            if (Character.isDigit(c)) {
                il.append(c);
                ++i;
            }
            else {
                il.append('0');
            }
        }
        return il.toString();
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) {
        if ((systemId != null && systemId.matches("(?i).*\\bhyphenation.dtd\\b.*")) || "hyphenation-info".equals(publicId)) {
            return new InputSource(this.getClass().getResource("hyphenation.dtd").toExternalForm());
        }
        return null;
    }
    
    @Override
    public void startElement(final String uri, final String local, final String raw, final Attributes attrs) {
        if (local.equals("hyphen-char")) {
            final String h = attrs.getValue("value");
            if (h != null && h.length() == 1) {
                this.hyphenChar = h.charAt(0);
            }
        }
        else if (local.equals("classes")) {
            this.currElement = 1;
        }
        else if (local.equals("patterns")) {
            this.currElement = 3;
        }
        else if (local.equals("exceptions")) {
            this.currElement = 2;
            this.exception = new ArrayList<Object>();
        }
        else if (local.equals("hyphen")) {
            if (this.token.length() > 0) {
                this.exception.add(this.token.toString());
            }
            this.exception.add(new Hyphen(attrs.getValue("pre"), attrs.getValue("no"), attrs.getValue("post")));
            this.currElement = 4;
        }
        this.token.setLength(0);
    }
    
    @Override
    public void endElement(final String uri, final String local, final String raw) {
        if (this.token.length() > 0) {
            final String word = this.token.toString();
            switch (this.currElement) {
                case 1: {
                    this.consumer.addClass(word);
                    break;
                }
                case 2: {
                    this.exception.add(word);
                    this.exception = this.normalizeException(this.exception);
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList<Object>)this.exception.clone());
                    break;
                }
                case 3: {
                    this.consumer.addPattern(getPattern(word), getInterletterValues(word));
                    break;
                }
            }
            if (this.currElement != 4) {
                this.token.setLength(0);
            }
        }
        if (this.currElement == 4) {
            this.currElement = 2;
        }
        else {
            this.currElement = 0;
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        final StringBuilder chars = new StringBuilder(length);
        chars.append(ch, start, length);
        for (String word = this.readToken(chars); word != null; word = this.readToken(chars)) {
            switch (this.currElement) {
                case 1: {
                    this.consumer.addClass(word);
                    break;
                }
                case 2: {
                    this.exception.add(word);
                    this.exception = this.normalizeException(this.exception);
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList<Object>)this.exception.clone());
                    this.exception.clear();
                    break;
                }
                case 3: {
                    this.consumer.addPattern(getPattern(word), getInterletterValues(word));
                    break;
                }
            }
        }
    }
    
    private String getLocationString(final SAXParseException ex) {
        final StringBuilder str = new StringBuilder();
        String systemId = ex.getSystemId();
        if (systemId != null) {
            final int index = systemId.lastIndexOf(47);
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());
        return str.toString();
    }
}
