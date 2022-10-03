package com.sun.mail.iap;

import java.io.ByteArrayInputStream;
import com.sun.mail.util.ASCIIUtility;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Response
{
    protected int index;
    protected int pindex;
    protected int size;
    protected byte[] buffer;
    protected int type;
    protected String tag;
    protected Exception ex;
    protected boolean utf8;
    private static final int increment = 100;
    public static final int TAG_MASK = 3;
    public static final int CONTINUATION = 1;
    public static final int TAGGED = 2;
    public static final int UNTAGGED = 3;
    public static final int TYPE_MASK = 28;
    public static final int OK = 4;
    public static final int NO = 8;
    public static final int BAD = 12;
    public static final int BYE = 16;
    public static final int SYNTHETIC = 32;
    private static String ATOM_CHAR_DELIM;
    private static String ASTRING_CHAR_DELIM;
    
    public Response(final String s) {
        this(s, true);
    }
    
    public Response(final String s, final boolean supportsUtf8) {
        this.buffer = null;
        this.type = 0;
        this.tag = null;
        if (supportsUtf8) {
            this.buffer = s.getBytes(StandardCharsets.UTF_8);
        }
        else {
            this.buffer = s.getBytes(StandardCharsets.US_ASCII);
        }
        this.size = this.buffer.length;
        this.utf8 = supportsUtf8;
        this.parse();
    }
    
    public Response(final Protocol p) throws IOException, ProtocolException {
        this.buffer = null;
        this.type = 0;
        this.tag = null;
        final ByteArray ba = p.getResponseBuffer();
        final ByteArray response = p.getInputStream().readResponse(ba);
        this.buffer = response.getBytes();
        this.size = response.getCount() - 2;
        this.utf8 = p.supportsUtf8();
        this.parse();
    }
    
    public Response(final Response r) {
        this.buffer = null;
        this.type = 0;
        this.tag = null;
        this.index = r.index;
        this.pindex = r.pindex;
        this.size = r.size;
        this.buffer = r.buffer;
        this.type = r.type;
        this.tag = r.tag;
        this.ex = r.ex;
        this.utf8 = r.utf8;
    }
    
    public static Response byeResponse(final Exception ex) {
        String err = "* BYE JavaMail Exception: " + ex.toString();
        err = err.replace('\r', ' ').replace('\n', ' ');
        final Response response;
        final Response r = response = new Response(err);
        response.type |= 0x20;
        r.ex = ex;
        return r;
    }
    
    public boolean supportsUtf8() {
        return this.utf8;
    }
    
    private void parse() {
        this.index = 0;
        if (this.size == 0) {
            return;
        }
        if (this.buffer[this.index] == 43) {
            this.type |= 0x1;
            ++this.index;
            return;
        }
        if (this.buffer[this.index] == 42) {
            this.type |= 0x3;
            ++this.index;
        }
        else {
            this.type |= 0x2;
            this.tag = this.readAtom();
            if (this.tag == null) {
                this.tag = "";
            }
        }
        final int mark = this.index;
        String s = this.readAtom();
        if (s == null) {
            s = "";
        }
        if (s.equalsIgnoreCase("OK")) {
            this.type |= 0x4;
        }
        else if (s.equalsIgnoreCase("NO")) {
            this.type |= 0x8;
        }
        else if (s.equalsIgnoreCase("BAD")) {
            this.type |= 0xC;
        }
        else if (s.equalsIgnoreCase("BYE")) {
            this.type |= 0x10;
        }
        else {
            this.index = mark;
        }
        this.pindex = this.index;
    }
    
    public void skipSpaces() {
        while (this.index < this.size && this.buffer[this.index] == 32) {
            ++this.index;
        }
    }
    
    public boolean isNextNonSpace(final char c) {
        this.skipSpaces();
        if (this.index < this.size && this.buffer[this.index] == (byte)c) {
            ++this.index;
            return true;
        }
        return false;
    }
    
    public void skipToken() {
        while (this.index < this.size && this.buffer[this.index] != 32) {
            ++this.index;
        }
    }
    
    public void skip(final int count) {
        this.index += count;
    }
    
    public byte peekByte() {
        if (this.index < this.size) {
            return this.buffer[this.index];
        }
        return 0;
    }
    
    public byte readByte() {
        if (this.index < this.size) {
            return this.buffer[this.index++];
        }
        return 0;
    }
    
    public String readAtom() {
        return this.readDelimString(Response.ATOM_CHAR_DELIM);
    }
    
    private String readDelimString(final String delim) {
        this.skipSpaces();
        if (this.index >= this.size) {
            return null;
        }
        final int start = this.index;
        int b;
        while (this.index < this.size && (b = (this.buffer[this.index] & 0xFF)) >= 32 && delim.indexOf((char)b) < 0 && b != 127) {
            ++this.index;
        }
        return this.toString(this.buffer, start, this.index);
    }
    
    public String readString(final char delim) {
        this.skipSpaces();
        if (this.index >= this.size) {
            return null;
        }
        final int start = this.index;
        while (this.index < this.size && this.buffer[this.index] != delim) {
            ++this.index;
        }
        return this.toString(this.buffer, start, this.index);
    }
    
    public String[] readStringList() {
        return this.readStringList(false);
    }
    
    public String[] readAtomStringList() {
        return this.readStringList(true);
    }
    
    private String[] readStringList(final boolean atom) {
        this.skipSpaces();
        if (this.buffer[this.index] != 40) {
            return null;
        }
        ++this.index;
        final List<String> result = new ArrayList<String>();
        while (!this.isNextNonSpace(')')) {
            final String s = atom ? this.readAtomString() : this.readString();
            if (s == null) {
                break;
            }
            result.add(s);
        }
        return result.toArray(new String[result.size()]);
    }
    
    public int readNumber() {
        this.skipSpaces();
        final int start = this.index;
        while (this.index < this.size && Character.isDigit((char)this.buffer[this.index])) {
            ++this.index;
        }
        if (this.index > start) {
            try {
                return ASCIIUtility.parseInt(this.buffer, start, this.index);
            }
            catch (final NumberFormatException ex) {}
        }
        return -1;
    }
    
    public long readLong() {
        this.skipSpaces();
        final int start = this.index;
        while (this.index < this.size && Character.isDigit((char)this.buffer[this.index])) {
            ++this.index;
        }
        if (this.index > start) {
            try {
                return ASCIIUtility.parseLong(this.buffer, start, this.index);
            }
            catch (final NumberFormatException ex) {}
        }
        return -1L;
    }
    
    public String readString() {
        return (String)this.parseString(false, true);
    }
    
    public ByteArrayInputStream readBytes() {
        final ByteArray ba = this.readByteArray();
        if (ba != null) {
            return ba.toByteArrayInputStream();
        }
        return null;
    }
    
    public ByteArray readByteArray() {
        if (this.isContinuation()) {
            this.skipSpaces();
            return new ByteArray(this.buffer, this.index, this.size - this.index);
        }
        return (ByteArray)this.parseString(false, false);
    }
    
    public String readAtomString() {
        return (String)this.parseString(true, true);
    }
    
    private Object parseString(final boolean parseAtoms, final boolean returnString) {
        this.skipSpaces();
        byte b = this.buffer[this.index];
        if (b == 34) {
            ++this.index;
            final int start = this.index;
            int copyto = this.index;
            while (this.index < this.size && (b = this.buffer[this.index]) != 34) {
                if (b == 92) {
                    ++this.index;
                }
                if (this.index != copyto) {
                    this.buffer[copyto] = this.buffer[this.index];
                }
                ++copyto;
                ++this.index;
            }
            if (this.index >= this.size) {
                return null;
            }
            ++this.index;
            if (returnString) {
                return this.toString(this.buffer, start, copyto);
            }
            return new ByteArray(this.buffer, start, copyto - start);
        }
        else if (b == 123) {
            int start = ++this.index;
            while (this.buffer[this.index] != 125) {
                ++this.index;
            }
            int count = 0;
            try {
                count = ASCIIUtility.parseInt(this.buffer, start, this.index);
            }
            catch (final NumberFormatException nex) {
                return null;
            }
            start = this.index + 3;
            this.index = start + count;
            if (returnString) {
                return this.toString(this.buffer, start, start + count);
            }
            return new ByteArray(this.buffer, start, count);
        }
        else if (parseAtoms) {
            final int start = this.index;
            final String s = this.readDelimString(Response.ASTRING_CHAR_DELIM);
            if (returnString) {
                return s;
            }
            return new ByteArray(this.buffer, start, this.index);
        }
        else {
            if (b == 78 || b == 110) {
                this.index += 3;
                return null;
            }
            return null;
        }
    }
    
    private String toString(final byte[] buffer, final int start, final int end) {
        return this.utf8 ? new String(buffer, start, end - start, StandardCharsets.UTF_8) : ASCIIUtility.toString(buffer, start, end);
    }
    
    public int getType() {
        return this.type;
    }
    
    public boolean isContinuation() {
        return (this.type & 0x3) == 0x1;
    }
    
    public boolean isTagged() {
        return (this.type & 0x3) == 0x2;
    }
    
    public boolean isUnTagged() {
        return (this.type & 0x3) == 0x3;
    }
    
    public boolean isOK() {
        return (this.type & 0x1C) == 0x4;
    }
    
    public boolean isNO() {
        return (this.type & 0x1C) == 0x8;
    }
    
    public boolean isBAD() {
        return (this.type & 0x1C) == 0xC;
    }
    
    public boolean isBYE() {
        return (this.type & 0x1C) == 0x10;
    }
    
    public boolean isSynthetic() {
        return (this.type & 0x20) == 0x20;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public String getRest() {
        this.skipSpaces();
        return this.toString(this.buffer, this.index, this.size);
    }
    
    public Exception getException() {
        return this.ex;
    }
    
    public void reset() {
        this.index = this.pindex;
    }
    
    @Override
    public String toString() {
        return this.toString(this.buffer, 0, this.size);
    }
    
    static {
        Response.ATOM_CHAR_DELIM = " (){%*\"\\]";
        Response.ASTRING_CHAR_DELIM = " (){%*\"\\";
    }
}
