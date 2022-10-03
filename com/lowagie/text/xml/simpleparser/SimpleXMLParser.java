package com.lowagie.text.xml.simpleparser;

import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import org.mozilla.universalchardet.CharsetListener;
import org.mozilla.universalchardet.UniversalDetector;
import java.io.InputStream;
import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Stack;

@Deprecated
public final class SimpleXMLParser
{
    private static final int UNKNOWN = 0;
    private static final int TEXT = 1;
    private static final int TAG_ENCOUNTERED = 2;
    private static final int EXAMIN_TAG = 3;
    private static final int TAG_EXAMINED = 4;
    private static final int IN_CLOSETAG = 5;
    private static final int SINGLE_TAG = 6;
    private static final int CDATA = 7;
    private static final int COMMENT = 8;
    private static final int PI = 9;
    private static final int ENTITY = 10;
    private static final int QUOTE = 11;
    private static final int ATTRIBUTE_KEY = 12;
    private static final int ATTRIBUTE_EQUAL = 13;
    private static final int ATTRIBUTE_VALUE = 14;
    Stack stack;
    int character;
    int previousCharacter;
    int lines;
    int columns;
    boolean eol;
    boolean nowhite;
    int state;
    boolean html;
    StringBuffer text;
    StringBuffer entity;
    String tag;
    HashMap attributes;
    SimpleXMLDocHandler doc;
    SimpleXMLDocHandlerComment comment;
    int nested;
    int quoteCharacter;
    String attributekey;
    String attributevalue;
    
    private SimpleXMLParser(final SimpleXMLDocHandler doc, final SimpleXMLDocHandlerComment comment, final boolean html) {
        this.character = 0;
        this.previousCharacter = -1;
        this.lines = 1;
        this.columns = 0;
        this.eol = false;
        this.nowhite = false;
        this.text = new StringBuffer();
        this.entity = new StringBuffer();
        this.tag = null;
        this.attributes = null;
        this.nested = 0;
        this.quoteCharacter = 34;
        this.attributekey = null;
        this.attributevalue = null;
        this.doc = doc;
        this.comment = comment;
        this.html = html;
        this.stack = new Stack();
        this.state = (html ? 1 : 0);
    }
    
    private void go(final Reader r) throws IOException {
        BufferedReader reader;
        if (r instanceof BufferedReader) {
            reader = (BufferedReader)r;
        }
        else {
            reader = new BufferedReader(r);
        }
        this.doc.startDocument();
        while (true) {
            if (this.previousCharacter == -1) {
                this.character = reader.read();
            }
            else {
                this.character = this.previousCharacter;
                this.previousCharacter = -1;
            }
            if (this.character == -1) {
                if (this.html) {
                    if (this.html && this.state == 1) {
                        this.flush();
                    }
                    this.doc.endDocument();
                }
                else {
                    this.throwException(MessageLocalization.getComposedMessage("missing.end.tag"));
                }
                return;
            }
            if (this.character == 10 && this.eol) {
                this.eol = false;
            }
            else {
                if (this.eol) {
                    this.eol = false;
                }
                else if (this.character == 10) {
                    ++this.lines;
                    this.columns = 0;
                }
                else if (this.character == 13) {
                    this.eol = true;
                    this.character = 10;
                    ++this.lines;
                    this.columns = 0;
                }
                else {
                    ++this.columns;
                }
                switch (this.state) {
                    case 0: {
                        if (this.character == 60) {
                            this.saveState(1);
                            this.state = 2;
                            continue;
                        }
                        continue;
                    }
                    case 1: {
                        if (this.character == 60) {
                            this.flush();
                            this.saveState(this.state);
                            this.state = 2;
                            continue;
                        }
                        if (this.character == 38) {
                            this.saveState(this.state);
                            this.entity.setLength(0);
                            this.state = 10;
                            this.nowhite = true;
                            continue;
                        }
                        if (Character.isWhitespace((char)this.character)) {
                            if (this.nowhite) {
                                this.text.append((char)this.character);
                            }
                            this.nowhite = false;
                            continue;
                        }
                        this.text.append((char)this.character);
                        this.nowhite = true;
                        continue;
                    }
                    case 2: {
                        this.initTag();
                        if (this.character == 47) {
                            this.state = 5;
                            continue;
                        }
                        if (this.character == 63) {
                            this.restoreState();
                            this.state = 9;
                            continue;
                        }
                        this.text.append((char)this.character);
                        this.state = 3;
                        continue;
                    }
                    case 3: {
                        if (this.character == 62) {
                            this.doTag();
                            this.processTag(true);
                            this.initTag();
                            this.state = this.restoreState();
                            continue;
                        }
                        if (this.character == 47) {
                            this.state = 6;
                            continue;
                        }
                        if (this.character == 45 && this.text.toString().equals("!-")) {
                            this.flush();
                            this.state = 8;
                            continue;
                        }
                        if (this.character == 91 && this.text.toString().equals("![CDATA")) {
                            this.flush();
                            this.state = 7;
                            continue;
                        }
                        if (this.character == 69 && this.text.toString().equals("!DOCTYP")) {
                            this.flush();
                            this.state = 9;
                            continue;
                        }
                        if (Character.isWhitespace((char)this.character)) {
                            this.doTag();
                            this.state = 4;
                            continue;
                        }
                        this.text.append((char)this.character);
                        continue;
                    }
                    case 4: {
                        if (this.character == 62) {
                            this.processTag(true);
                            this.initTag();
                            this.state = this.restoreState();
                            continue;
                        }
                        if (this.character == 47) {
                            this.state = 6;
                            continue;
                        }
                        if (Character.isWhitespace((char)this.character)) {
                            continue;
                        }
                        this.text.append((char)this.character);
                        this.state = 12;
                        continue;
                    }
                    case 5: {
                        if (this.character == 62) {
                            this.doTag();
                            this.processTag(false);
                            if (!this.html && this.nested == 0) {
                                return;
                            }
                            this.state = this.restoreState();
                            continue;
                        }
                        else {
                            if (!Character.isWhitespace((char)this.character)) {
                                this.text.append((char)this.character);
                                continue;
                            }
                            continue;
                        }
                        break;
                    }
                    case 6: {
                        if (this.character != 62) {
                            this.throwException(MessageLocalization.getComposedMessage("expected.gt.for.tag.lt.1.gt", this.tag));
                        }
                        this.doTag();
                        this.processTag(true);
                        this.processTag(false);
                        this.initTag();
                        if (!this.html && this.nested == 0) {
                            this.doc.endDocument();
                            return;
                        }
                        this.state = this.restoreState();
                        continue;
                    }
                    case 7: {
                        if (this.character == 62 && this.text.toString().endsWith("]]")) {
                            this.text.setLength(this.text.length() - 2);
                            this.flush();
                            this.state = this.restoreState();
                            continue;
                        }
                        this.text.append((char)this.character);
                        continue;
                    }
                    case 8: {
                        if (this.character == 62 && this.text.toString().endsWith("--")) {
                            this.text.setLength(this.text.length() - 2);
                            this.flush();
                            this.state = this.restoreState();
                            continue;
                        }
                        this.text.append((char)this.character);
                        continue;
                    }
                    case 9: {
                        if (this.character != 62) {
                            continue;
                        }
                        this.state = this.restoreState();
                        if (this.state == 1) {
                            this.state = 0;
                            continue;
                        }
                        continue;
                    }
                    case 10: {
                        if (this.character == 59) {
                            this.state = this.restoreState();
                            final String cent = this.entity.toString();
                            this.entity.setLength(0);
                            final char ce = EntitiesToUnicode.decodeEntity(cent);
                            if (ce == '\0') {
                                this.text.append('&').append(cent).append(';');
                            }
                            else {
                                this.text.append(ce);
                            }
                            continue;
                        }
                        if ((this.character != 35 && (this.character < 48 || this.character > 57) && (this.character < 97 || this.character > 122) && (this.character < 65 || this.character > 90)) || this.entity.length() >= 7) {
                            this.state = this.restoreState();
                            this.previousCharacter = this.character;
                            this.text.append('&').append(this.entity.toString());
                            this.entity.setLength(0);
                            continue;
                        }
                        this.entity.append((char)this.character);
                        continue;
                    }
                    case 11: {
                        if (this.html && this.quoteCharacter == 32 && this.character == 62) {
                            this.flush();
                            this.processTag(true);
                            this.initTag();
                            this.state = this.restoreState();
                            continue;
                        }
                        if (this.html && this.quoteCharacter == 32 && Character.isWhitespace((char)this.character)) {
                            this.flush();
                            this.state = 4;
                            continue;
                        }
                        if (this.html && this.quoteCharacter == 32) {
                            this.text.append((char)this.character);
                            continue;
                        }
                        if (this.character == this.quoteCharacter) {
                            this.flush();
                            this.state = 4;
                            continue;
                        }
                        if (" \r\n\t".indexOf(this.character) >= 0) {
                            this.text.append(' ');
                            continue;
                        }
                        if (this.character == 38) {
                            this.saveState(this.state);
                            this.state = 10;
                            this.entity.setLength(0);
                            continue;
                        }
                        this.text.append((char)this.character);
                        continue;
                    }
                    case 12: {
                        if (Character.isWhitespace((char)this.character)) {
                            this.flush();
                            this.state = 13;
                            continue;
                        }
                        if (this.character == 61) {
                            this.flush();
                            this.state = 14;
                            continue;
                        }
                        if (this.html && this.character == 62) {
                            this.text.setLength(0);
                            this.processTag(true);
                            this.initTag();
                            this.state = this.restoreState();
                            continue;
                        }
                        this.text.append((char)this.character);
                        continue;
                    }
                    case 13: {
                        if (this.character == 61) {
                            this.state = 14;
                            continue;
                        }
                        if (Character.isWhitespace((char)this.character)) {
                            continue;
                        }
                        if (this.html && this.character == 62) {
                            this.text.setLength(0);
                            this.processTag(true);
                            this.initTag();
                            this.state = this.restoreState();
                            continue;
                        }
                        if (this.html && this.character == 47) {
                            this.flush();
                            this.state = 6;
                            continue;
                        }
                        if (this.html) {
                            this.flush();
                            this.text.append((char)this.character);
                            this.state = 12;
                            continue;
                        }
                        this.throwException(MessageLocalization.getComposedMessage("error.in.attribute.processing"));
                        continue;
                    }
                    case 14: {
                        if (this.character == 34 || this.character == 39) {
                            this.quoteCharacter = this.character;
                            this.state = 11;
                            continue;
                        }
                        if (Character.isWhitespace((char)this.character)) {
                            continue;
                        }
                        if (this.html && this.character == 62) {
                            this.flush();
                            this.processTag(true);
                            this.initTag();
                            this.state = this.restoreState();
                            continue;
                        }
                        if (this.html) {
                            this.text.append((char)this.character);
                            this.quoteCharacter = 32;
                            this.state = 11;
                            continue;
                        }
                        this.throwException(MessageLocalization.getComposedMessage("error.in.attribute.processing"));
                        continue;
                    }
                }
            }
        }
    }
    
    private int restoreState() {
        if (!this.stack.empty()) {
            return this.stack.pop();
        }
        return 0;
    }
    
    private void saveState(final int s) {
        this.stack.push(new Integer(s));
    }
    
    private void flush() {
        switch (this.state) {
            case 1:
            case 7: {
                if (this.text.length() > 0) {
                    this.doc.text(this.text.toString());
                    break;
                }
                break;
            }
            case 8: {
                if (this.comment != null) {
                    this.comment.comment(this.text.toString());
                    break;
                }
                break;
            }
            case 12: {
                this.attributekey = this.text.toString();
                if (this.html) {
                    this.attributekey = this.attributekey.toLowerCase();
                    break;
                }
                break;
            }
            case 11:
            case 14: {
                this.attributevalue = this.text.toString();
                this.attributes.put(this.attributekey, this.attributevalue);
                break;
            }
        }
        this.text.setLength(0);
    }
    
    private void initTag() {
        this.tag = null;
        this.attributes = new HashMap();
    }
    
    private void doTag() {
        if (this.tag == null) {
            this.tag = this.text.toString();
        }
        if (this.html) {
            this.tag = this.tag.toLowerCase();
        }
        this.text.setLength(0);
    }
    
    private void processTag(final boolean start) {
        if (start) {
            ++this.nested;
            this.doc.startElement(this.tag, this.attributes);
        }
        else {
            --this.nested;
            this.doc.endElement(this.tag);
        }
    }
    
    private void throwException(final String s) throws IOException {
        throw new IOException(MessageLocalization.getComposedMessage("1.near.line.2.column.3", s, String.valueOf(this.lines), String.valueOf(this.columns)));
    }
    
    public static void parse(final SimpleXMLDocHandler doc, final SimpleXMLDocHandlerComment comment, final Reader r, final boolean html) throws IOException {
        final SimpleXMLParser parser = new SimpleXMLParser(doc, comment, html);
        parser.go(r);
    }
    
    public static void parse(final SimpleXMLDocHandler doc, final InputStream in) throws IOException {
        final byte[] b4 = new byte[4];
        final int count = in.read(b4);
        if (count != 4) {
            throw new IOException(MessageLocalization.getComposedMessage("insufficient.length"));
        }
        final UniversalDetector detector = new UniversalDetector((CharsetListener)null);
        detector.handleData(b4, 0, count);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        String decl = null;
        if (encoding.equals("UTF-8")) {
            final StringBuffer sb = new StringBuffer();
            int c;
            while ((c = in.read()) != -1 && c != 62) {
                sb.append((char)c);
            }
            decl = sb.toString();
        }
        else if (encoding.equals("CP037")) {
            final ByteArrayOutputStream bi = new ByteArrayOutputStream();
            int c;
            while ((c = in.read()) != -1 && c != 110) {
                bi.write(c);
            }
            decl = new String(bi.toByteArray(), "CP037");
        }
        if (decl != null) {
            decl = getDeclaredEncoding(decl);
            if (decl != null) {
                encoding = decl;
            }
        }
        parse(doc, new InputStreamReader(in, IanaEncodings.getJavaEncoding(encoding)));
    }
    
    private static String getDeclaredEncoding(final String decl) {
        if (decl == null) {
            return null;
        }
        final int idx = decl.indexOf("encoding");
        if (idx < 0) {
            return null;
        }
        final int idx2 = decl.indexOf(34, idx);
        final int idx3 = decl.indexOf(39, idx);
        if (idx2 == idx3) {
            return null;
        }
        if ((idx2 < 0 && idx3 > 0) || (idx3 > 0 && idx3 < idx2)) {
            final int idx4 = decl.indexOf(39, idx3 + 1);
            if (idx4 < 0) {
                return null;
            }
            return decl.substring(idx3 + 1, idx4);
        }
        else {
            if ((idx3 >= 0 || idx2 <= 0) && (idx2 <= 0 || idx2 >= idx3)) {
                return null;
            }
            final int idx4 = decl.indexOf(34, idx2 + 1);
            if (idx4 < 0) {
                return null;
            }
            return decl.substring(idx2 + 1, idx4);
        }
    }
    
    public static void parse(final SimpleXMLDocHandler doc, final Reader r) throws IOException {
        parse(doc, null, r, false);
    }
}
