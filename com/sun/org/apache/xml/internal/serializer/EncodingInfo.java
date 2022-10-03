package com.sun.org.apache.xml.internal.serializer;

public final class EncodingInfo
{
    final String name;
    final String javaName;
    private InEncoding m_encoding;
    
    public boolean isInEncoding(final char ch) {
        if (this.m_encoding == null) {
            this.m_encoding = new EncodingImpl();
        }
        return this.m_encoding.isInEncoding(ch);
    }
    
    public boolean isInEncoding(final char high, final char low) {
        if (this.m_encoding == null) {
            this.m_encoding = new EncodingImpl();
        }
        return this.m_encoding.isInEncoding(high, low);
    }
    
    public EncodingInfo(final String name, final String javaName) {
        this.name = name;
        this.javaName = javaName;
    }
    
    private static boolean inEncoding(final char ch, final String encoding) {
        boolean isInEncoding;
        try {
            final char[] cArray = { ch };
            final String s = new String(cArray);
            final byte[] bArray = s.getBytes(encoding);
            isInEncoding = inEncoding(ch, bArray);
        }
        catch (final Exception e) {
            isInEncoding = false;
            if (encoding == null) {
                isInEncoding = true;
            }
        }
        return isInEncoding;
    }
    
    private static boolean inEncoding(final char high, final char low, final String encoding) {
        boolean isInEncoding;
        try {
            final char[] cArray = { high, low };
            final String s = new String(cArray);
            final byte[] bArray = s.getBytes(encoding);
            isInEncoding = inEncoding(high, bArray);
        }
        catch (final Exception e) {
            isInEncoding = false;
        }
        return isInEncoding;
    }
    
    private static boolean inEncoding(final char ch, final byte[] data) {
        final boolean isInEncoding = data != null && data.length != 0 && data[0] != 0 && (data[0] != 63 || ch == '?');
        return isInEncoding;
    }
    
    private class EncodingImpl implements InEncoding
    {
        private final String m_encoding;
        private final int m_first;
        private final int m_explFirst;
        private final int m_explLast;
        private final int m_last;
        private InEncoding m_before;
        private InEncoding m_after;
        private static final int RANGE = 128;
        private final boolean[] m_alreadyKnown;
        private final boolean[] m_isInEncoding;
        
        @Override
        public boolean isInEncoding(final char ch1) {
            final int codePoint = Encodings.toCodePoint(ch1);
            boolean ret;
            if (codePoint < this.m_explFirst) {
                if (this.m_before == null) {
                    this.m_before = new EncodingImpl(this.m_encoding, this.m_first, this.m_explFirst - 1, codePoint);
                }
                ret = this.m_before.isInEncoding(ch1);
            }
            else if (this.m_explLast < codePoint) {
                if (this.m_after == null) {
                    this.m_after = new EncodingImpl(this.m_encoding, this.m_explLast + 1, this.m_last, codePoint);
                }
                ret = this.m_after.isInEncoding(ch1);
            }
            else {
                final int idx = codePoint - this.m_explFirst;
                if (this.m_alreadyKnown[idx]) {
                    ret = this.m_isInEncoding[idx];
                }
                else {
                    ret = inEncoding(ch1, this.m_encoding);
                    this.m_alreadyKnown[idx] = true;
                    this.m_isInEncoding[idx] = ret;
                }
            }
            return ret;
        }
        
        @Override
        public boolean isInEncoding(final char high, final char low) {
            final int codePoint = Encodings.toCodePoint(high, low);
            boolean ret;
            if (codePoint < this.m_explFirst) {
                if (this.m_before == null) {
                    this.m_before = new EncodingImpl(this.m_encoding, this.m_first, this.m_explFirst - 1, codePoint);
                }
                ret = this.m_before.isInEncoding(high, low);
            }
            else if (this.m_explLast < codePoint) {
                if (this.m_after == null) {
                    this.m_after = new EncodingImpl(this.m_encoding, this.m_explLast + 1, this.m_last, codePoint);
                }
                ret = this.m_after.isInEncoding(high, low);
            }
            else {
                final int idx = codePoint - this.m_explFirst;
                if (this.m_alreadyKnown[idx]) {
                    ret = this.m_isInEncoding[idx];
                }
                else {
                    ret = inEncoding(high, low, this.m_encoding);
                    this.m_alreadyKnown[idx] = true;
                    this.m_isInEncoding[idx] = ret;
                }
            }
            return ret;
        }
        
        private EncodingImpl(final EncodingInfo encodingInfo) {
            this(encodingInfo, encodingInfo.javaName, 0, Integer.MAX_VALUE, 0);
        }
        
        private EncodingImpl(final String encoding, final int first, final int last, final int codePoint) {
            this.m_alreadyKnown = new boolean[128];
            this.m_isInEncoding = new boolean[128];
            this.m_first = first;
            this.m_last = last;
            this.m_explFirst = codePoint / 128 * 128;
            this.m_explLast = this.m_explFirst + 127;
            this.m_encoding = encoding;
            if (EncodingInfo.this.javaName != null) {
                if (0 <= this.m_explFirst && this.m_explFirst <= 127 && ("UTF8".equals(EncodingInfo.this.javaName) || "UTF-16".equals(EncodingInfo.this.javaName) || "ASCII".equals(EncodingInfo.this.javaName) || "US-ASCII".equals(EncodingInfo.this.javaName) || "Unicode".equals(EncodingInfo.this.javaName) || "UNICODE".equals(EncodingInfo.this.javaName) || EncodingInfo.this.javaName.startsWith("ISO8859"))) {
                    for (int unicode = 1; unicode < 127; ++unicode) {
                        final int idx = unicode - this.m_explFirst;
                        if (0 <= idx && idx < 128) {
                            this.m_alreadyKnown[idx] = true;
                            this.m_isInEncoding[idx] = true;
                        }
                    }
                }
                if (EncodingInfo.this.javaName == null) {
                    for (int idx2 = 0; idx2 < this.m_alreadyKnown.length; ++idx2) {
                        this.m_alreadyKnown[idx2] = true;
                        this.m_isInEncoding[idx2] = true;
                    }
                }
            }
        }
    }
    
    private interface InEncoding
    {
        boolean isInEncoding(final char p0);
        
        boolean isInEncoding(final char p0, final char p1);
    }
}
