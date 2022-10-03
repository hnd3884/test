package com.dd.plist;

import java.nio.charset.CharacterCodingException;
import java.io.UnsupportedEncodingException;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.text.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

public final class ASCIIPropertyListParser
{
    public static final char WHITESPACE_SPACE = ' ';
    public static final char WHITESPACE_TAB = '\t';
    public static final char WHITESPACE_NEWLINE = '\n';
    public static final char WHITESPACE_CARRIAGE_RETURN = '\r';
    public static final char ARRAY_BEGIN_TOKEN = '(';
    public static final char ARRAY_END_TOKEN = ')';
    public static final char ARRAY_ITEM_DELIMITER_TOKEN = ',';
    public static final char DICTIONARY_BEGIN_TOKEN = '{';
    public static final char DICTIONARY_END_TOKEN = '}';
    public static final char DICTIONARY_ASSIGN_TOKEN = '=';
    public static final char DICTIONARY_ITEM_DELIMITER_TOKEN = ';';
    public static final char QUOTEDSTRING_BEGIN_TOKEN = '\"';
    public static final char QUOTEDSTRING_END_TOKEN = '\"';
    public static final char QUOTEDSTRING_ESCAPE_TOKEN = '\\';
    public static final char DATA_BEGIN_TOKEN = '<';
    public static final char DATA_END_TOKEN = '>';
    public static final char DATA_GSOBJECT_BEGIN_TOKEN = '*';
    public static final char DATA_GSDATE_BEGIN_TOKEN = 'D';
    public static final char DATA_GSBOOL_BEGIN_TOKEN = 'B';
    public static final char DATA_GSBOOL_TRUE_TOKEN = 'Y';
    public static final char DATA_GSBOOL_FALSE_TOKEN = 'N';
    public static final char DATA_GSINT_BEGIN_TOKEN = 'I';
    public static final char DATA_GSREAL_BEGIN_TOKEN = 'R';
    public static final char DATE_DATE_FIELD_DELIMITER = '-';
    public static final char DATE_TIME_FIELD_DELIMITER = ':';
    public static final char DATE_GS_DATE_TIME_DELIMITER = ' ';
    public static final char DATE_APPLE_DATE_TIME_DELIMITER = 'T';
    public static final char DATE_APPLE_END_TOKEN = 'Z';
    public static final char COMMENT_BEGIN_TOKEN = '/';
    public static final char MULTILINE_COMMENT_SECOND_TOKEN = '*';
    public static final char SINGLELINE_COMMENT_SECOND_TOKEN = '/';
    public static final char MULTILINE_COMMENT_END_TOKEN = '/';
    private final byte[] data;
    private int index;
    
    private ASCIIPropertyListParser(final byte[] propertyListContent) {
        this.data = propertyListContent;
    }
    
    public static NSObject parse(final File f) throws IOException, ParseException {
        return parse(new FileInputStream(f));
    }
    
    public static NSObject parse(final InputStream in) throws ParseException, IOException {
        final byte[] buf = PropertyListParser.readAll(in);
        in.close();
        return parse(buf);
    }
    
    public static NSObject parse(final byte[] bytes) throws ParseException {
        final ASCIIPropertyListParser parser = new ASCIIPropertyListParser(bytes);
        return parser.parse();
    }
    
    private boolean acceptSequence(final char... sequence) {
        for (int i = 0; i < sequence.length; ++i) {
            if (this.data[this.index + i] != sequence[i]) {
                return false;
            }
        }
        return true;
    }
    
    private boolean accept(final char... acceptableSymbols) {
        boolean symbolPresent = false;
        for (final char c : acceptableSymbols) {
            if (this.data[this.index] == c) {
                symbolPresent = true;
            }
        }
        return symbolPresent;
    }
    
    private boolean accept(final char acceptableSymbol) {
        return this.data[this.index] == acceptableSymbol;
    }
    
    private void expect(final char... expectedSymbols) throws ParseException {
        if (!this.accept(expectedSymbols)) {
            final StringBuilder excString = new StringBuilder();
            excString.append("Expected '").append(expectedSymbols[0]).append("'");
            for (int i = 1; i < expectedSymbols.length; ++i) {
                excString.append(" or '").append(expectedSymbols[i]).append("'");
            }
            excString.append(" but found '").append((char)this.data[this.index]).append("'");
            throw new ParseException(excString.toString(), this.index);
        }
    }
    
    private void expect(final char expectedSymbol) throws ParseException {
        if (!this.accept(expectedSymbol)) {
            throw new ParseException("Expected '" + expectedSymbol + "' but found '" + (char)this.data[this.index] + "'", this.index);
        }
    }
    
    private void read(final char symbol) throws ParseException {
        this.expect(symbol);
        ++this.index;
    }
    
    private void skip() {
        ++this.index;
    }
    
    private void skip(final int numSymbols) {
        this.index += numSymbols;
    }
    
    private void skipWhitespacesAndComments() {
        boolean commentSkipped;
        do {
            commentSkipped = false;
            while (this.accept('\r', '\n', ' ', '\t')) {
                this.skip();
            }
            if (this.acceptSequence('/', '/')) {
                this.skip(2);
                this.readInputUntil('\r', '\n');
                commentSkipped = true;
            }
            else {
                if (!this.acceptSequence('/', '*')) {
                    continue;
                }
                this.skip(2);
                while (!this.acceptSequence('*', '/')) {
                    this.skip();
                }
                this.skip(2);
                commentSkipped = true;
            }
        } while (commentSkipped);
    }
    
    private String readInputUntil(final char... symbols) {
        final StringBuilder strBuf = new StringBuilder();
        while (!this.accept(symbols)) {
            strBuf.append((char)this.data[this.index]);
            this.skip();
        }
        return strBuf.toString();
    }
    
    private String readInputUntil(final char symbol) {
        final StringBuilder strBuf = new StringBuilder();
        while (!this.accept(symbol)) {
            strBuf.append((char)this.data[this.index]);
            this.skip();
        }
        return strBuf.toString();
    }
    
    public NSObject parse() throws ParseException {
        this.index = 0;
        if (this.data.length >= 3 && (this.data[0] & 0xFF) == 0xEF && (this.data[1] & 0xFF) == 0xBB && (this.data[2] & 0xFF) == 0xBF) {
            this.skip(3);
        }
        this.skipWhitespacesAndComments();
        this.expect('{', '(', '/');
        try {
            return this.parseObject();
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new ParseException("Reached end of input unexpectedly.", this.index);
        }
    }
    
    private NSObject parseObject() throws ParseException {
        switch (this.data[this.index]) {
            case 40: {
                return this.parseArray();
            }
            case 123: {
                return this.parseDictionary();
            }
            case 60: {
                return this.parseData();
            }
            case 34: {
                final String quotedString = this.parseQuotedString();
                if (quotedString.length() == 20 && quotedString.charAt(4) == '-') {
                    try {
                        return new NSDate(quotedString);
                    }
                    catch (final Exception ex) {
                        return new NSString(quotedString);
                    }
                }
                return new NSString(quotedString);
            }
            default: {
                if (this.data[this.index] > 47 && this.data[this.index] < 58) {
                    return this.parseDateString();
                }
                return new NSString(this.parseString());
            }
        }
    }
    
    private NSArray parseArray() throws ParseException {
        this.skip();
        this.skipWhitespacesAndComments();
        final List<NSObject> objects = new LinkedList<NSObject>();
        while (!this.accept(')')) {
            objects.add(this.parseObject());
            this.skipWhitespacesAndComments();
            if (!this.accept(',')) {
                break;
            }
            this.skip();
            this.skipWhitespacesAndComments();
        }
        this.read(')');
        return new NSArray((NSObject[])objects.toArray(new NSObject[objects.size()]));
    }
    
    private NSDictionary parseDictionary() throws ParseException {
        this.skip();
        this.skipWhitespacesAndComments();
        final NSDictionary dict = new NSDictionary();
        while (!this.accept('}')) {
            String keyString;
            if (this.accept('\"')) {
                keyString = this.parseQuotedString();
            }
            else {
                keyString = this.parseString();
            }
            this.skipWhitespacesAndComments();
            this.read('=');
            this.skipWhitespacesAndComments();
            final NSObject object = this.parseObject();
            dict.put(keyString, object);
            this.skipWhitespacesAndComments();
            this.read(';');
            this.skipWhitespacesAndComments();
        }
        this.skip();
        return dict;
    }
    
    private NSObject parseData() throws ParseException {
        NSObject obj = null;
        this.skip();
        if (this.accept('*')) {
            this.skip();
            this.expect('B', 'D', 'I', 'R');
            if (this.accept('B')) {
                this.skip();
                this.expect('Y', 'N');
                if (this.accept('Y')) {
                    obj = new NSNumber(true);
                }
                else {
                    obj = new NSNumber(false);
                }
                this.skip();
            }
            else if (this.accept('D')) {
                this.skip();
                final String dateString = this.readInputUntil('>');
                obj = new NSDate(dateString);
            }
            else if (this.accept('I', 'R')) {
                this.skip();
                final String numberString = this.readInputUntil('>');
                obj = new NSNumber(numberString);
            }
            this.read('>');
        }
        else {
            String dataString = this.readInputUntil('>');
            dataString = dataString.replaceAll("\\s+", "");
            final int numBytes = dataString.length() / 2;
            final byte[] bytes = new byte[numBytes];
            for (int i = 0; i < bytes.length; ++i) {
                final String byteString = dataString.substring(i * 2, i * 2 + 2);
                final int byteValue = Integer.parseInt(byteString, 16);
                bytes[i] = (byte)byteValue;
            }
            obj = new NSData(bytes);
            this.skip();
        }
        return obj;
    }
    
    private NSObject parseDateString() {
        final String numericalString = this.parseString();
        if (numericalString.length() > 4 && numericalString.charAt(4) == '-') {
            try {
                return new NSDate(numericalString);
            }
            catch (final Exception ex) {}
        }
        return new NSString(numericalString);
    }
    
    private String parseString() {
        return this.readInputUntil(' ', '\t', '\n', '\r', ',', ';', '=', ')');
    }
    
    private String parseQuotedString() throws ParseException {
        this.skip();
        final List<Byte> strBytes = new LinkedList<Byte>();
        boolean unescapedBackslash = true;
        while (this.data[this.index] != 34 || (this.data[this.index - 1] == 92 && unescapedBackslash)) {
            strBytes.add(this.data[this.index]);
            if (this.accept('\\')) {
                unescapedBackslash = (this.data[this.index - 1] != 92 || !unescapedBackslash);
            }
            this.skip();
        }
        final byte[] bytArr = new byte[strBytes.size()];
        int i = 0;
        for (final Byte b : strBytes) {
            bytArr[i] = b;
            ++i;
        }
        String unescapedString;
        try {
            unescapedString = parseQuotedString(new String(bytArr, "UTF-8"));
        }
        catch (final Exception ex) {
            throw new ParseException("The quoted string could not be parsed.", this.index);
        }
        this.skip();
        return unescapedString;
    }
    
    private static synchronized String parseQuotedString(final String s) throws UnsupportedEncodingException, CharacterCodingException {
        final StringBuffer result = new StringBuffer();
        final StringCharacterIterator iterator = new StringCharacterIterator(s);
        char c = iterator.current();
        while (iterator.getIndex() < iterator.getEndIndex()) {
            switch (c) {
                case '\\': {
                    result.append(parseEscapedSequence(iterator));
                    break;
                }
                default: {
                    result.append(c);
                    break;
                }
            }
            c = iterator.next();
        }
        return result.toString();
    }
    
    private static char parseEscapedSequence(final StringCharacterIterator iterator) throws UnsupportedEncodingException {
        final char c = iterator.next();
        switch (c) {
            case '\"':
            case '\\':
            case 'b':
            case 'n':
            case 'r':
            case 't': {
                return c;
            }
            case 'U':
            case 'u': {
                final String unicodeValue = new String(new char[] { iterator.next(), iterator.next(), iterator.next(), iterator.next() });
                return (char)Integer.parseInt(unicodeValue, 16);
            }
            default: {
                final String num = new String(new char[] { c, iterator.next(), iterator.next() });
                return (char)Integer.parseInt(num, 8);
            }
        }
    }
}
