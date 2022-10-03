package com.lowagie.text.pdf.fonts.cmaps;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.PushbackInputStream;
import java.io.InputStream;

public class CMapParser
{
    private static final String BEGIN_CODESPACE_RANGE = "begincodespacerange";
    private static final String BEGIN_BASE_FONT_CHAR = "beginbfchar";
    private static final String BEGIN_BASE_FONT_RANGE = "beginbfrange";
    private static final String MARK_END_OF_DICTIONARY = ">>";
    private static final String MARK_END_OF_ARRAY = "]";
    private byte[] tokenParserByteBuffer;
    
    public CMapParser() {
        this.tokenParserByteBuffer = new byte[512];
    }
    
    public CMap parse(final InputStream input) throws IOException {
        final PushbackInputStream cmapStream = new PushbackInputStream(input);
        final CMap result = new CMap();
        Object previousToken = null;
        Object token = null;
        while ((token = this.parseNextToken(cmapStream)) != null) {
            if (token instanceof Operator) {
                final Operator op = (Operator)token;
                if (op.op.equals("begincodespacerange")) {
                    final Number cosCount = (Number)previousToken;
                    for (int j = 0; j < cosCount.intValue(); ++j) {
                        final byte[] startRange = (byte[])this.parseNextToken(cmapStream);
                        final byte[] endRange = (byte[])this.parseNextToken(cmapStream);
                        final CodespaceRange range = new CodespaceRange();
                        range.setStart(startRange);
                        range.setEnd(endRange);
                        result.addCodespaceRange(range);
                    }
                }
                else if (op.op.equals("beginbfchar")) {
                    final Number cosCount = (Number)previousToken;
                    for (int j = 0; j < cosCount.intValue(); ++j) {
                        final byte[] inputCode = (byte[])this.parseNextToken(cmapStream);
                        final Object nextToken = this.parseNextToken(cmapStream);
                        if (nextToken instanceof byte[]) {
                            final byte[] bytes = (byte[])nextToken;
                            final String value = this.createStringFromBytes(bytes);
                            result.addMapping(inputCode, value);
                        }
                        else {
                            if (!(nextToken instanceof LiteralName)) {
                                throw new IOException(MessageLocalization.getComposedMessage("error.parsing.cmap.beginbfchar.expected.cosstring.or.cosname.and.not.1", nextToken));
                            }
                            result.addMapping(inputCode, ((LiteralName)nextToken).name);
                        }
                    }
                }
                else if (op.op.equals("beginbfrange")) {
                    final Number cosCount = (Number)previousToken;
                    for (int j = 0; j < cosCount.intValue(); ++j) {
                        final byte[] startCode = (byte[])this.parseNextToken(cmapStream);
                        final byte[] endCode = (byte[])this.parseNextToken(cmapStream);
                        final Object nextToken2 = this.parseNextToken(cmapStream);
                        List array = null;
                        byte[] tokenBytes = null;
                        if (nextToken2 instanceof List) {
                            array = (List)nextToken2;
                            tokenBytes = array.get(0);
                        }
                        else {
                            tokenBytes = (byte[])nextToken2;
                        }
                        String value2 = null;
                        int arrayIndex = 0;
                        boolean done = false;
                        while (!done) {
                            if (this.compare(startCode, endCode) >= 0) {
                                done = true;
                            }
                            value2 = this.createStringFromBytes(tokenBytes);
                            result.addMapping(startCode, value2);
                            this.increment(startCode);
                            if (array == null) {
                                this.increment(tokenBytes);
                            }
                            else {
                                if (++arrayIndex >= array.size()) {
                                    continue;
                                }
                                tokenBytes = array.get(arrayIndex);
                            }
                        }
                    }
                }
            }
            previousToken = token;
        }
        return result;
    }
    
    private Object parseNextToken(final PushbackInputStream is) throws IOException {
        Object retval = null;
        int nextByte;
        for (nextByte = is.read(); nextByte == 9 || nextByte == 32 || nextByte == 13 || nextByte == 10; nextByte = is.read()) {}
        switch (nextByte) {
            case 37: {
                final StringBuffer buffer = new StringBuffer();
                buffer.append((char)nextByte);
                this.readUntilEndOfLine(is, buffer);
                retval = buffer.toString();
                break;
            }
            case 40: {
                final StringBuffer buffer = new StringBuffer();
                for (int stringByte = is.read(); stringByte != -1 && stringByte != 41; stringByte = is.read()) {
                    buffer.append((char)stringByte);
                }
                retval = buffer.toString();
                break;
            }
            case 62: {
                final int secondCloseBrace = is.read();
                if (secondCloseBrace == 62) {
                    retval = ">>";
                    break;
                }
                throw new IOException(MessageLocalization.getComposedMessage("error.expected.the.end.of.a.dictionary"));
            }
            case 93: {
                retval = "]";
                break;
            }
            case 91: {
                final List list = new ArrayList();
                for (Object nextToken = this.parseNextToken(is); nextToken != "]"; nextToken = this.parseNextToken(is)) {
                    list.add(nextToken);
                }
                retval = list;
                break;
            }
            case 60: {
                int theNextByte = is.read();
                if (theNextByte == 60) {
                    final Map result = new HashMap();
                    for (Object key = this.parseNextToken(is); key instanceof LiteralName && key != ">>"; key = this.parseNextToken(is)) {
                        final Object value = this.parseNextToken(is);
                        result.put(((LiteralName)key).name, value);
                    }
                    retval = result;
                    break;
                }
                int multiplyer = 16;
                int bufferIndex = -1;
                while (theNextByte != -1 && theNextByte != 62) {
                    int intValue = 0;
                    if (theNextByte == 32 || theNextByte == 9 || theNextByte == 10 || theNextByte == 13 || theNextByte == 12) {
                        theNextByte = is.read();
                    }
                    else {
                        if (theNextByte >= 48 && theNextByte <= 57) {
                            intValue = theNextByte - 48;
                        }
                        else if (theNextByte >= 65 && theNextByte <= 70) {
                            intValue = 10 + theNextByte - 65;
                        }
                        else {
                            if (theNextByte < 97 || theNextByte > 102) {
                                throw new IOException(MessageLocalization.getComposedMessage("error.expected.hex.character.and.not.char.thenextbyte.1", theNextByte));
                            }
                            intValue = 10 + theNextByte - 97;
                        }
                        intValue *= multiplyer;
                        if (multiplyer == 16) {
                            ++bufferIndex;
                            this.tokenParserByteBuffer[bufferIndex] = 0;
                            multiplyer = 1;
                        }
                        else {
                            multiplyer = 16;
                        }
                        final byte[] tokenParserByteBuffer = this.tokenParserByteBuffer;
                        final int n = bufferIndex;
                        tokenParserByteBuffer[n] += (byte)intValue;
                        theNextByte = is.read();
                    }
                }
                final byte[] finalResult = new byte[bufferIndex + 1];
                System.arraycopy(this.tokenParserByteBuffer, 0, finalResult, 0, bufferIndex + 1);
                retval = finalResult;
                break;
            }
            case 47: {
                final StringBuffer buffer = new StringBuffer();
                for (int stringByte = is.read(); !this.isWhitespaceOrEOF(stringByte); stringByte = is.read()) {
                    buffer.append((char)stringByte);
                }
                retval = new LiteralName(buffer.toString());
                break;
            }
            case -1: {
                break;
            }
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57: {
                final StringBuffer buffer = new StringBuffer();
                buffer.append((char)nextByte);
                for (nextByte = is.read(); !this.isWhitespaceOrEOF(nextByte) && (Character.isDigit((char)nextByte) || nextByte == 46); nextByte = is.read()) {
                    buffer.append((char)nextByte);
                }
                is.unread(nextByte);
                final String value2 = buffer.toString();
                if (value2.indexOf(46) >= 0) {
                    retval = new Double(value2);
                    break;
                }
                retval = new Integer(buffer.toString());
                break;
            }
            default: {
                final StringBuffer buffer = new StringBuffer();
                buffer.append((char)nextByte);
                for (nextByte = is.read(); !this.isWhitespaceOrEOF(nextByte); nextByte = is.read()) {
                    buffer.append((char)nextByte);
                }
                retval = new Operator(buffer.toString());
                break;
            }
        }
        return retval;
    }
    
    private void readUntilEndOfLine(final InputStream is, final StringBuffer buf) throws IOException {
        for (int nextByte = is.read(); nextByte != -1 && nextByte != 13 && nextByte != 10; nextByte = is.read()) {
            buf.append((char)nextByte);
        }
    }
    
    private boolean isWhitespaceOrEOF(final int aByte) {
        return aByte == -1 || aByte == 32 || aByte == 13 || aByte == 10;
    }
    
    private void increment(final byte[] data) {
        this.increment(data, data.length - 1);
    }
    
    private void increment(final byte[] data, final int position) {
        if (position > 0 && (data[position] + 256) % 256 == 255) {
            data[position] = 0;
            this.increment(data, position - 1);
        }
        else {
            ++data[position];
        }
    }
    
    private String createStringFromBytes(final byte[] bytes) throws IOException {
        String retval = null;
        if (bytes.length == 1) {
            retval = new String(bytes);
        }
        else {
            retval = new String(bytes, StandardCharsets.UTF_16BE);
        }
        return retval;
    }
    
    private int compare(final byte[] first, final byte[] second) {
        int retval = 1;
        boolean done = false;
        for (int i = 0; i < first.length && !done; ++i) {
            if (first[i] != second[i]) {
                if ((first[i] + 256) % 256 < (second[i] + 256) % 256) {
                    done = true;
                    retval = -1;
                }
                else {
                    done = true;
                    retval = 1;
                }
            }
        }
        return retval;
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: java org.pdfbox.cmapparser.CMapParser <CMAP File>");
            System.exit(-1);
        }
        final CMapParser parser = new CMapParser();
        final CMap result = parser.parse(new FileInputStream(args[0]));
        System.out.println("Result:" + result);
    }
    
    private class LiteralName
    {
        private String name;
        
        private LiteralName(final String theName) {
            this.name = theName;
        }
    }
    
    private class Operator
    {
        private String op;
        
        private Operator(final String theOp) {
            this.op = theOp;
        }
    }
}
