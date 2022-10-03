package com.lowagie.text.pdf.hyphenation;

import java.io.FileInputStream;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.io.IOException;
import com.lowagie.text.ExceptionConverter;
import java.io.InputStream;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.util.ArrayList;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;

public class SimplePatternParser implements SimpleXMLDocHandler, PatternConsumer
{
    int currElement;
    PatternConsumer consumer;
    StringBuffer token;
    ArrayList exception;
    char hyphenChar;
    SimpleXMLParser parser;
    static final int ELEM_CLASSES = 1;
    static final int ELEM_EXCEPTIONS = 2;
    static final int ELEM_PATTERNS = 3;
    static final int ELEM_HYPHEN = 4;
    
    public SimplePatternParser() {
        this.token = new StringBuffer();
        this.hyphenChar = '-';
    }
    
    public void parse(final InputStream stream, final PatternConsumer consumer) {
        this.consumer = consumer;
        try {
            SimpleXMLParser.parse(this, stream);
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
        finally {
            try {
                stream.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    protected static String getPattern(final String word) {
        final StringBuffer pat = new StringBuffer();
        for (int len = word.length(), i = 0; i < len; ++i) {
            if (!Character.isDigit(word.charAt(i))) {
                pat.append(word.charAt(i));
            }
        }
        return pat.toString();
    }
    
    protected ArrayList normalizeException(final ArrayList ex) {
        final ArrayList res = new ArrayList();
        for (int i = 0; i < ex.size(); ++i) {
            final Object item = ex.get(i);
            if (item instanceof String) {
                final String str = (String)item;
                final StringBuffer buf = new StringBuffer();
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
    
    protected String getExceptionWord(final ArrayList ex) {
        final StringBuffer res = new StringBuffer();
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
        final StringBuffer il = new StringBuffer();
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
    public void endDocument() {
    }
    
    @Override
    public void endElement(final String tag) {
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
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList)this.exception.clone());
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
    public void startDocument() {
    }
    
    @Override
    public void startElement(final String tag, final HashMap h) {
        if (tag.equals("hyphen-char")) {
            final String hh = h.get("value");
            if (hh != null && hh.length() == 1) {
                this.hyphenChar = hh.charAt(0);
            }
        }
        else if (tag.equals("classes")) {
            this.currElement = 1;
        }
        else if (tag.equals("patterns")) {
            this.currElement = 3;
        }
        else if (tag.equals("exceptions")) {
            this.currElement = 2;
            this.exception = new ArrayList();
        }
        else if (tag.equals("hyphen")) {
            if (this.token.length() > 0) {
                this.exception.add(this.token.toString());
            }
            this.exception.add(new Hyphen(h.get("pre"), h.get("no"), h.get("post")));
            this.currElement = 4;
        }
        this.token.setLength(0);
    }
    
    @Override
    public void text(final String str) {
        final StringTokenizer tk = new StringTokenizer(str);
        while (tk.hasMoreTokens()) {
            final String word = tk.nextToken();
            switch (this.currElement) {
                case 1: {
                    this.consumer.addClass(word);
                    continue;
                }
                case 2: {
                    this.exception.add(word);
                    this.exception = this.normalizeException(this.exception);
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList)this.exception.clone());
                    this.exception.clear();
                    continue;
                }
                case 3: {
                    this.consumer.addPattern(getPattern(word), getInterletterValues(word));
                    continue;
                }
            }
        }
    }
    
    @Override
    public void addClass(final String c) {
        System.out.println("class: " + c);
    }
    
    @Override
    public void addException(final String w, final ArrayList e) {
        System.out.println("exception: " + w + " : " + e.toString());
    }
    
    @Override
    public void addPattern(final String p, final String v) {
        System.out.println("pattern: " + p + " : " + v);
    }
    
    public static void main(final String[] args) {
        try {
            if (args.length > 0) {
                final SimplePatternParser pp = new SimplePatternParser();
                pp.parse(new FileInputStream(args[0]), pp);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
