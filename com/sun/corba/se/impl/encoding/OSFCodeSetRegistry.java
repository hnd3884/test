package com.sun.corba.se.impl.encoding;

public final class OSFCodeSetRegistry
{
    public static final int ISO_8859_1_VALUE = 65537;
    public static final int UTF_16_VALUE = 65801;
    public static final int UTF_8_VALUE = 83951617;
    public static final int UCS_2_VALUE = 65792;
    public static final int ISO_646_VALUE = 65568;
    public static final Entry ISO_8859_1;
    static final Entry UTF_16BE;
    static final Entry UTF_16LE;
    public static final Entry UTF_16;
    public static final Entry UTF_8;
    public static final Entry UCS_2;
    public static final Entry ISO_646;
    
    private OSFCodeSetRegistry() {
    }
    
    public static Entry lookupEntry(final int n) {
        switch (n) {
            case 65537: {
                return OSFCodeSetRegistry.ISO_8859_1;
            }
            case 65801: {
                return OSFCodeSetRegistry.UTF_16;
            }
            case 83951617: {
                return OSFCodeSetRegistry.UTF_8;
            }
            case 65568: {
                return OSFCodeSetRegistry.ISO_646;
            }
            case 65792: {
                return OSFCodeSetRegistry.UCS_2;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        ISO_8859_1 = new Entry("ISO-8859-1", 65537, true, 1);
        UTF_16BE = new Entry("UTF-16BE", -1, true, 2);
        UTF_16LE = new Entry("UTF-16LE", -2, true, 2);
        UTF_16 = new Entry("UTF-16", 65801, true, 4);
        UTF_8 = new Entry("UTF-8", 83951617, false, 6);
        UCS_2 = new Entry("UCS-2", 65792, true, 2);
        ISO_646 = new Entry("US-ASCII", 65568, true, 1);
    }
    
    public static final class Entry
    {
        private String javaName;
        private int encodingNum;
        private boolean isFixedWidth;
        private int maxBytesPerChar;
        
        private Entry(final String javaName, final int encodingNum, final boolean isFixedWidth, final int maxBytesPerChar) {
            this.javaName = javaName;
            this.encodingNum = encodingNum;
            this.isFixedWidth = isFixedWidth;
            this.maxBytesPerChar = maxBytesPerChar;
        }
        
        public String getName() {
            return this.javaName;
        }
        
        public int getNumber() {
            return this.encodingNum;
        }
        
        public boolean isFixedWidth() {
            return this.isFixedWidth;
        }
        
        public int getMaxBytesPerChar() {
            return this.maxBytesPerChar;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry entry = (Entry)o;
            return this.javaName.equals(entry.javaName) && this.encodingNum == entry.encodingNum && this.isFixedWidth == entry.isFixedWidth && this.maxBytesPerChar == entry.maxBytesPerChar;
        }
        
        @Override
        public int hashCode() {
            return this.encodingNum;
        }
    }
}
