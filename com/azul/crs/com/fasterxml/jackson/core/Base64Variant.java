package com.azul.crs.com.fasterxml.jackson.core;

import com.azul.crs.com.fasterxml.jackson.core.util.ByteArrayBuilder;
import java.util.Arrays;
import java.io.Serializable;

public final class Base64Variant implements Serializable
{
    private static final int INT_SPACE = 32;
    private static final long serialVersionUID = 1L;
    protected static final char PADDING_CHAR_NONE = '\0';
    public static final int BASE64_VALUE_INVALID = -1;
    public static final int BASE64_VALUE_PADDING = -2;
    private final transient int[] _asciiToBase64;
    private final transient char[] _base64ToAsciiC;
    private final transient byte[] _base64ToAsciiB;
    final String _name;
    private final char _paddingChar;
    private final int _maxLineLength;
    private final boolean _writePadding;
    private final PaddingReadBehaviour _paddingReadBehaviour;
    
    public Base64Variant(final String name, final String base64Alphabet, final boolean writePadding, final char paddingChar, final int maxLineLength) {
        this._asciiToBase64 = new int[128];
        this._base64ToAsciiC = new char[64];
        this._base64ToAsciiB = new byte[64];
        this._name = name;
        this._writePadding = writePadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
        final int alphaLen = base64Alphabet.length();
        if (alphaLen != 64) {
            throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + alphaLen + ")");
        }
        base64Alphabet.getChars(0, alphaLen, this._base64ToAsciiC, 0);
        Arrays.fill(this._asciiToBase64, -1);
        for (int i = 0; i < alphaLen; ++i) {
            final char alpha = this._base64ToAsciiC[i];
            this._base64ToAsciiB[i] = (byte)alpha;
            this._asciiToBase64[alpha] = i;
        }
        if (writePadding) {
            this._asciiToBase64[paddingChar] = -2;
        }
        this._paddingReadBehaviour = (writePadding ? PaddingReadBehaviour.PADDING_REQUIRED : PaddingReadBehaviour.PADDING_FORBIDDEN);
    }
    
    public Base64Variant(final Base64Variant base, final String name, final int maxLineLength) {
        this(base, name, base._writePadding, base._paddingChar, maxLineLength);
    }
    
    public Base64Variant(final Base64Variant base, final String name, final boolean writePadding, final char paddingChar, final int maxLineLength) {
        this(base, name, writePadding, paddingChar, base._paddingReadBehaviour, maxLineLength);
    }
    
    private Base64Variant(final Base64Variant base, final String name, final boolean writePadding, final char paddingChar, final PaddingReadBehaviour paddingReadBehaviour, final int maxLineLength) {
        this._asciiToBase64 = new int[128];
        this._base64ToAsciiC = new char[64];
        this._base64ToAsciiB = new byte[64];
        this._name = name;
        final byte[] srcB = base._base64ToAsciiB;
        System.arraycopy(srcB, 0, this._base64ToAsciiB, 0, srcB.length);
        final char[] srcC = base._base64ToAsciiC;
        System.arraycopy(srcC, 0, this._base64ToAsciiC, 0, srcC.length);
        final int[] srcV = base._asciiToBase64;
        System.arraycopy(srcV, 0, this._asciiToBase64, 0, srcV.length);
        this._writePadding = writePadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
        this._paddingReadBehaviour = paddingReadBehaviour;
    }
    
    private Base64Variant(final Base64Variant base, final PaddingReadBehaviour paddingReadBehaviour) {
        this(base, base._name, base._writePadding, base._paddingChar, paddingReadBehaviour, base._maxLineLength);
    }
    
    public Base64Variant withPaddingAllowed() {
        return this.withReadPadding(PaddingReadBehaviour.PADDING_ALLOWED);
    }
    
    public Base64Variant withPaddingRequired() {
        return this.withReadPadding(PaddingReadBehaviour.PADDING_REQUIRED);
    }
    
    public Base64Variant withPaddingForbidden() {
        return this.withReadPadding(PaddingReadBehaviour.PADDING_FORBIDDEN);
    }
    
    public Base64Variant withReadPadding(final PaddingReadBehaviour readPadding) {
        return (readPadding == this._paddingReadBehaviour) ? this : new Base64Variant(this, readPadding);
    }
    
    public Base64Variant withWritePadding(final boolean writePadding) {
        return (writePadding == this._writePadding) ? this : new Base64Variant(this, this._name, writePadding, this._paddingChar, this._maxLineLength);
    }
    
    protected Object readResolve() {
        final Base64Variant base = Base64Variants.valueOf(this._name);
        if (this._writePadding != base._writePadding || this._paddingChar != base._paddingChar || this._paddingReadBehaviour != base._paddingReadBehaviour || this._maxLineLength != base._maxLineLength || this._writePadding != base._writePadding) {
            return new Base64Variant(base, this._name, this._writePadding, this._paddingChar, this._paddingReadBehaviour, this._maxLineLength);
        }
        return base;
    }
    
    public String getName() {
        return this._name;
    }
    
    public boolean usesPadding() {
        return this._writePadding;
    }
    
    public boolean requiresPaddingOnRead() {
        return this._paddingReadBehaviour == PaddingReadBehaviour.PADDING_REQUIRED;
    }
    
    public boolean acceptsPaddingOnRead() {
        return this._paddingReadBehaviour != PaddingReadBehaviour.PADDING_FORBIDDEN;
    }
    
    public boolean usesPaddingChar(final char c) {
        return c == this._paddingChar;
    }
    
    public boolean usesPaddingChar(final int ch) {
        return ch == this._paddingChar;
    }
    
    public PaddingReadBehaviour paddingReadBehaviour() {
        return this._paddingReadBehaviour;
    }
    
    public char getPaddingChar() {
        return this._paddingChar;
    }
    
    public byte getPaddingByte() {
        return (byte)this._paddingChar;
    }
    
    public int getMaxLineLength() {
        return this._maxLineLength;
    }
    
    public int decodeBase64Char(final char c) {
        final int ch = c;
        return (ch <= 127) ? this._asciiToBase64[ch] : -1;
    }
    
    public int decodeBase64Char(final int ch) {
        return (ch <= 127) ? this._asciiToBase64[ch] : -1;
    }
    
    public int decodeBase64Byte(final byte b) {
        final int ch = b;
        if (ch < 0) {
            return -1;
        }
        return this._asciiToBase64[ch];
    }
    
    public char encodeBase64BitsAsChar(final int value) {
        return this._base64ToAsciiC[value];
    }
    
    public int encodeBase64Chunk(final int b24, final char[] buffer, int ptr) {
        buffer[ptr++] = this._base64ToAsciiC[b24 >> 18 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiC[b24 >> 12 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiC[b24 >> 6 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiC[b24 & 0x3F];
        return ptr;
    }
    
    public void encodeBase64Chunk(final StringBuilder sb, final int b24) {
        sb.append(this._base64ToAsciiC[b24 >> 18 & 0x3F]);
        sb.append(this._base64ToAsciiC[b24 >> 12 & 0x3F]);
        sb.append(this._base64ToAsciiC[b24 >> 6 & 0x3F]);
        sb.append(this._base64ToAsciiC[b24 & 0x3F]);
    }
    
    public int encodeBase64Partial(final int bits, final int outputBytes, final char[] buffer, int outPtr) {
        buffer[outPtr++] = this._base64ToAsciiC[bits >> 18 & 0x3F];
        buffer[outPtr++] = this._base64ToAsciiC[bits >> 12 & 0x3F];
        if (this.usesPadding()) {
            buffer[outPtr++] = ((outputBytes == 2) ? this._base64ToAsciiC[bits >> 6 & 0x3F] : this._paddingChar);
            buffer[outPtr++] = this._paddingChar;
        }
        else if (outputBytes == 2) {
            buffer[outPtr++] = this._base64ToAsciiC[bits >> 6 & 0x3F];
        }
        return outPtr;
    }
    
    public void encodeBase64Partial(final StringBuilder sb, final int bits, final int outputBytes) {
        sb.append(this._base64ToAsciiC[bits >> 18 & 0x3F]);
        sb.append(this._base64ToAsciiC[bits >> 12 & 0x3F]);
        if (this.usesPadding()) {
            sb.append((outputBytes == 2) ? this._base64ToAsciiC[bits >> 6 & 0x3F] : this._paddingChar);
            sb.append(this._paddingChar);
        }
        else if (outputBytes == 2) {
            sb.append(this._base64ToAsciiC[bits >> 6 & 0x3F]);
        }
    }
    
    public byte encodeBase64BitsAsByte(final int value) {
        return this._base64ToAsciiB[value];
    }
    
    public int encodeBase64Chunk(final int b24, final byte[] buffer, int ptr) {
        buffer[ptr++] = this._base64ToAsciiB[b24 >> 18 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiB[b24 >> 12 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiB[b24 >> 6 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiB[b24 & 0x3F];
        return ptr;
    }
    
    public int encodeBase64Partial(final int bits, final int outputBytes, final byte[] buffer, int outPtr) {
        buffer[outPtr++] = this._base64ToAsciiB[bits >> 18 & 0x3F];
        buffer[outPtr++] = this._base64ToAsciiB[bits >> 12 & 0x3F];
        if (this.usesPadding()) {
            final byte pb = (byte)this._paddingChar;
            buffer[outPtr++] = ((outputBytes == 2) ? this._base64ToAsciiB[bits >> 6 & 0x3F] : pb);
            buffer[outPtr++] = pb;
        }
        else if (outputBytes == 2) {
            buffer[outPtr++] = this._base64ToAsciiB[bits >> 6 & 0x3F];
        }
        return outPtr;
    }
    
    public String encode(final byte[] input) {
        return this.encode(input, false);
    }
    
    public String encode(final byte[] input, final boolean addQuotes) {
        final int inputEnd = input.length;
        final StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
        if (addQuotes) {
            sb.append('\"');
        }
        int chunksBeforeLF = this.getMaxLineLength() >> 2;
        int inputPtr = 0;
        final int safeInputEnd = inputEnd - 3;
        while (inputPtr <= safeInputEnd) {
            int b24 = input[inputPtr++] << 8;
            b24 |= (input[inputPtr++] & 0xFF);
            b24 = (b24 << 8 | (input[inputPtr++] & 0xFF));
            this.encodeBase64Chunk(sb, b24);
            if (--chunksBeforeLF <= 0) {
                sb.append('\\');
                sb.append('n');
                chunksBeforeLF = this.getMaxLineLength() >> 2;
            }
        }
        final int inputLeft = inputEnd - inputPtr;
        if (inputLeft > 0) {
            int b25 = input[inputPtr++] << 16;
            if (inputLeft == 2) {
                b25 |= (input[inputPtr++] & 0xFF) << 8;
            }
            this.encodeBase64Partial(sb, b25, inputLeft);
        }
        if (addQuotes) {
            sb.append('\"');
        }
        return sb.toString();
    }
    
    public String encode(final byte[] input, final boolean addQuotes, final String linefeed) {
        final int inputEnd = input.length;
        final StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
        if (addQuotes) {
            sb.append('\"');
        }
        int chunksBeforeLF = this.getMaxLineLength() >> 2;
        int inputPtr = 0;
        final int safeInputEnd = inputEnd - 3;
        while (inputPtr <= safeInputEnd) {
            int b24 = input[inputPtr++] << 8;
            b24 |= (input[inputPtr++] & 0xFF);
            b24 = (b24 << 8 | (input[inputPtr++] & 0xFF));
            this.encodeBase64Chunk(sb, b24);
            if (--chunksBeforeLF <= 0) {
                sb.append(linefeed);
                chunksBeforeLF = this.getMaxLineLength() >> 2;
            }
        }
        final int inputLeft = inputEnd - inputPtr;
        if (inputLeft > 0) {
            int b25 = input[inputPtr++] << 16;
            if (inputLeft == 2) {
                b25 |= (input[inputPtr++] & 0xFF) << 8;
            }
            this.encodeBase64Partial(sb, b25, inputLeft);
        }
        if (addQuotes) {
            sb.append('\"');
        }
        return sb.toString();
    }
    
    public byte[] decode(final String input) throws IllegalArgumentException {
        final ByteArrayBuilder b = new ByteArrayBuilder();
        this.decode(input, b);
        return b.toByteArray();
    }
    
    public void decode(final String str, final ByteArrayBuilder builder) throws IllegalArgumentException {
        int ptr = 0;
        final int len = str.length();
        while (ptr < len) {
            char ch = str.charAt(ptr++);
            if (ch > ' ') {
                int bits = this.decodeBase64Char(ch);
                if (bits < 0) {
                    this._reportInvalidBase64(ch, 0, null);
                }
                int decodedData = bits;
                if (ptr >= len) {
                    this._reportBase64EOF();
                }
                ch = str.charAt(ptr++);
                bits = this.decodeBase64Char(ch);
                if (bits < 0) {
                    this._reportInvalidBase64(ch, 1, null);
                }
                decodedData = (decodedData << 6 | bits);
                if (ptr >= len) {
                    if (!this.requiresPaddingOnRead()) {
                        decodedData >>= 4;
                        builder.append(decodedData);
                        break;
                    }
                    this._reportBase64EOF();
                }
                ch = str.charAt(ptr++);
                bits = this.decodeBase64Char(ch);
                if (bits < 0) {
                    if (bits != -2) {
                        this._reportInvalidBase64(ch, 2, null);
                    }
                    if (!this.acceptsPaddingOnRead()) {
                        this._reportBase64UnexpectedPadding();
                    }
                    if (ptr >= len) {
                        this._reportBase64EOF();
                    }
                    ch = str.charAt(ptr++);
                    if (!this.usesPaddingChar(ch)) {
                        this._reportInvalidBase64(ch, 3, "expected padding character '" + this.getPaddingChar() + "'");
                    }
                    decodedData >>= 4;
                    builder.append(decodedData);
                    continue;
                }
                decodedData = (decodedData << 6 | bits);
                if (ptr >= len) {
                    if (!this.requiresPaddingOnRead()) {
                        decodedData >>= 2;
                        builder.appendTwoBytes(decodedData);
                        break;
                    }
                    this._reportBase64EOF();
                }
                ch = str.charAt(ptr++);
                bits = this.decodeBase64Char(ch);
                if (bits < 0) {
                    if (bits != -2) {
                        this._reportInvalidBase64(ch, 3, null);
                    }
                    if (!this.acceptsPaddingOnRead()) {
                        this._reportBase64UnexpectedPadding();
                    }
                    decodedData >>= 2;
                    builder.appendTwoBytes(decodedData);
                }
                else {
                    decodedData = (decodedData << 6 | bits);
                    builder.appendThreeBytes(decodedData);
                }
                continue;
            }
        }
    }
    
    @Override
    public String toString() {
        return this._name;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final Base64Variant other = (Base64Variant)o;
        return other._paddingChar == this._paddingChar && other._maxLineLength == this._maxLineLength && other._writePadding == this._writePadding && other._paddingReadBehaviour == this._paddingReadBehaviour && this._name.equals(other._name);
    }
    
    @Override
    public int hashCode() {
        return this._name.hashCode();
    }
    
    protected void _reportInvalidBase64(final char ch, final int bindex, final String msg) throws IllegalArgumentException {
        String base;
        if (ch <= ' ') {
            base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
        }
        else if (this.usesPaddingChar(ch)) {
            base = "Unexpected padding character ('" + this.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
        }
        else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
            base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        }
        else {
            base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        }
        if (msg != null) {
            base = base + ": " + msg;
        }
        throw new IllegalArgumentException(base);
    }
    
    protected void _reportBase64EOF() throws IllegalArgumentException {
        throw new IllegalArgumentException(this.missingPaddingMessage());
    }
    
    protected void _reportBase64UnexpectedPadding() throws IllegalArgumentException {
        throw new IllegalArgumentException(this.unexpectedPaddingMessage());
    }
    
    protected String unexpectedPaddingMessage() {
        return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects no padding at the end while decoding. This Base64Variant might have been incorrectly configured", this.getName());
    }
    
    public String missingPaddingMessage() {
        return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects padding (one or more '%c' characters) at the end. This Base64Variant might have been incorrectly configured", this.getName(), this.getPaddingChar());
    }
    
    public enum PaddingReadBehaviour
    {
        PADDING_FORBIDDEN, 
        PADDING_REQUIRED, 
        PADDING_ALLOWED;
    }
}
