package sun.font;

import java.nio.IntBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;

abstract class CMap
{
    static final short ShiftJISEncoding = 2;
    static final short GBKEncoding = 3;
    static final short Big5Encoding = 4;
    static final short WansungEncoding = 5;
    static final short JohabEncoding = 6;
    static final short MSUnicodeSurrogateEncoding = 10;
    static final char noSuchChar = '\ufffd';
    static final int SHORTMASK = 65535;
    static final int INTMASK = Integer.MAX_VALUE;
    static final char[][] converterMaps;
    char[] xlat;
    public static final NullCMapClass theNullCmap;
    
    static CMap initialize(final TrueTypeFont trueTypeFont) {
        CMap cMap = null;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        boolean b = false;
        final ByteBuffer tableBuffer = trueTypeFont.getTableBuffer(1668112752);
        trueTypeFont.getTableSize(1668112752);
        for (short short1 = tableBuffer.getShort(2), n9 = 0; n9 < short1; ++n9) {
            tableBuffer.position(n9 * 8 + 4);
            if (tableBuffer.getShort() == 3) {
                b = true;
                final short short2 = tableBuffer.getShort();
                final int int1 = tableBuffer.getInt();
                switch (short2) {
                    case 0: {
                        n = int1;
                        break;
                    }
                    case 1: {
                        n2 = int1;
                        break;
                    }
                    case 2: {
                        n3 = int1;
                        break;
                    }
                    case 3: {
                        n4 = int1;
                        break;
                    }
                    case 4: {
                        n5 = int1;
                        break;
                    }
                    case 5: {
                        n6 = int1;
                        break;
                    }
                    case 6: {
                        n7 = int1;
                        break;
                    }
                    case 10: {
                        n8 = int1;
                        break;
                    }
                }
            }
        }
        if (b) {
            if (n8 != 0) {
                cMap = createCMap(tableBuffer, n8, null);
            }
            else if (n != 0) {
                cMap = createCMap(tableBuffer, n, null);
            }
            else if (n2 != 0) {
                cMap = createCMap(tableBuffer, n2, null);
            }
            else if (n3 != 0) {
                cMap = createCMap(tableBuffer, n3, getConverterMap((short)2));
            }
            else if (n4 != 0) {
                cMap = createCMap(tableBuffer, n4, getConverterMap((short)3));
            }
            else if (n5 != 0) {
                if (FontUtilities.isSolaris && trueTypeFont.platName != null && (trueTypeFont.platName.startsWith("/usr/openwin/lib/locale/zh_CN.EUC/X11/fonts/TrueType") || trueTypeFont.platName.startsWith("/usr/openwin/lib/locale/zh_CN/X11/fonts/TrueType") || trueTypeFont.platName.startsWith("/usr/openwin/lib/locale/zh/X11/fonts/TrueType"))) {
                    cMap = createCMap(tableBuffer, n5, getConverterMap((short)3));
                }
                else {
                    cMap = createCMap(tableBuffer, n5, getConverterMap((short)4));
                }
            }
            else if (n6 != 0) {
                cMap = createCMap(tableBuffer, n6, getConverterMap((short)5));
            }
            else if (n7 != 0) {
                cMap = createCMap(tableBuffer, n7, getConverterMap((short)6));
            }
        }
        else {
            cMap = createCMap(tableBuffer, tableBuffer.getInt(8), null);
        }
        return cMap;
    }
    
    static char[] getConverter(final short n) {
        int n2 = 0;
        int n3 = 0;
        String s = null;
        switch (n) {
            case 2: {
                n2 = 33088;
                n3 = 64764;
                s = "SJIS";
                break;
            }
            case 3: {
                n2 = 33088;
                n3 = 65184;
                s = "GBK";
                break;
            }
            case 4: {
                n2 = 41280;
                n3 = 65278;
                s = "Big5";
                break;
            }
            case 5: {
                n2 = 41377;
                n3 = 65246;
                s = "EUC_KR";
                break;
            }
            case 6: {
                n2 = 33089;
                n3 = 65022;
                s = "Johab";
                break;
            }
            default: {
                return null;
            }
        }
        try {
            final char[] array = new char[65536];
            for (int i = 0; i < 65536; ++i) {
                array[i] = '\ufffd';
            }
            final byte[] array2 = new byte[(n3 - n2 + 1) * 2];
            final char[] array3 = new char[n3 - n2 + 1];
            int n4 = 0;
            if (n == 2) {
                for (int j = n2; j <= n3; ++j) {
                    final int n5 = j >> 8 & 0xFF;
                    if (n5 >= 161 && n5 <= 223) {
                        array2[n4++] = -1;
                        array2[n4++] = -1;
                    }
                    else {
                        array2[n4++] = (byte)n5;
                        array2[n4++] = (byte)(j & 0xFF);
                    }
                }
            }
            else {
                for (int k = n2; k <= n3; ++k) {
                    array2[n4++] = (byte)(k >> 8 & 0xFF);
                    array2[n4++] = (byte)(k & 0xFF);
                }
            }
            Charset.forName(s).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("\u0000").decode(ByteBuffer.wrap(array2, 0, array2.length), CharBuffer.wrap(array3, 0, array3.length), true);
            for (int l = 32; l <= 126; ++l) {
                array[l] = (char)l;
            }
            if (n == 2) {
                for (int n6 = 161; n6 <= 223; ++n6) {
                    array[n6] = (char)(n6 - 161 + 65377);
                }
            }
            System.arraycopy(array3, 0, array, n2, array3.length);
            final char[] array4 = new char[65536];
            for (int n7 = 0; n7 < 65536; ++n7) {
                if (array[n7] != '\ufffd') {
                    array4[array[n7]] = (char)n7;
                }
            }
            return array4;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    static char[] getConverterMap(final short n) {
        if (CMap.converterMaps[n] == null) {
            CMap.converterMaps[n] = getConverter(n);
        }
        return CMap.converterMaps[n];
    }
    
    static CMap createCMap(final ByteBuffer byteBuffer, final int n, final char[] array) {
        final char char1 = byteBuffer.getChar(n);
        long n2;
        if (char1 < '\b') {
            n2 = byteBuffer.getChar(n + 2);
        }
        else {
            n2 = (byteBuffer.getInt(n + 4) & Integer.MAX_VALUE);
        }
        if (n + n2 > byteBuffer.capacity() && FontUtilities.isLogging()) {
            FontUtilities.getLogger().warning("Cmap subtable overflows buffer.");
        }
        switch (char1) {
            case 0: {
                return new CMapFormat0(byteBuffer, n);
            }
            case 2: {
                return new CMapFormat2(byteBuffer, n, array);
            }
            case 4: {
                return new CMapFormat4(byteBuffer, n, array);
            }
            case 6: {
                return new CMapFormat6(byteBuffer, n, array);
            }
            case 8: {
                return new CMapFormat8(byteBuffer, n, array);
            }
            case 10: {
                return new CMapFormat10(byteBuffer, n, array);
            }
            case 12: {
                return new CMapFormat12(byteBuffer, n, array);
            }
            default: {
                throw new RuntimeException("Cmap format unimplemented: " + (int)byteBuffer.getChar(n));
            }
        }
    }
    
    abstract char getGlyph(final int p0);
    
    final int getControlCodeGlyph(final int n, final boolean b) {
        if (n < 16) {
            switch (n) {
                case 9:
                case 10:
                case 13: {
                    return 65535;
                }
            }
        }
        else if (n >= 8204) {
            if (n <= 8207 || (n >= 8232 && n <= 8238) || (n >= 8298 && n <= 8303)) {
                return 65535;
            }
            if (b && n >= 65535) {
                return 0;
            }
        }
        return -1;
    }
    
    static {
        converterMaps = new char[7][];
        theNullCmap = new NullCMapClass();
    }
    
    static class CMapFormat4 extends CMap
    {
        int segCount;
        int entrySelector;
        int rangeShift;
        char[] endCount;
        char[] startCount;
        short[] idDelta;
        char[] idRangeOffset;
        char[] glyphIds;
        
        CMapFormat4(final ByteBuffer byteBuffer, final int n, final char[] xlat) {
            this.xlat = xlat;
            byteBuffer.position(n);
            final CharBuffer charBuffer = byteBuffer.asCharBuffer();
            charBuffer.get();
            int value = charBuffer.get();
            if (n + value > byteBuffer.capacity()) {
                value = byteBuffer.capacity() - n;
            }
            charBuffer.get();
            this.segCount = charBuffer.get() / '\u0002';
            charBuffer.get();
            this.entrySelector = charBuffer.get();
            this.rangeShift = charBuffer.get() / '\u0002';
            this.startCount = new char[this.segCount];
            this.endCount = new char[this.segCount];
            this.idDelta = new short[this.segCount];
            this.idRangeOffset = new char[this.segCount];
            for (int i = 0; i < this.segCount; ++i) {
                this.endCount[i] = charBuffer.get();
            }
            charBuffer.get();
            for (int j = 0; j < this.segCount; ++j) {
                this.startCount[j] = charBuffer.get();
            }
            for (int k = 0; k < this.segCount; ++k) {
                this.idDelta[k] = (short)charBuffer.get();
            }
            for (int l = 0; l < this.segCount; ++l) {
                this.idRangeOffset[l] = (char)(charBuffer.get() >> 1 & 0xFFFF);
            }
            final int n2 = (this.segCount * 8 + 16) / 2;
            charBuffer.position(n2);
            final int n3 = value / 2 - n2;
            this.glyphIds = new char[n3];
            for (int n4 = 0; n4 < n3; ++n4) {
                this.glyphIds[n4] = charBuffer.get();
            }
        }
        
        @Override
        char getGlyph(int n) {
            int n2 = 0;
            final int controlCodeGlyph = this.getControlCodeGlyph(n, true);
            if (controlCodeGlyph >= 0) {
                return (char)controlCodeGlyph;
            }
            if (this.xlat != null) {
                n = this.xlat[n];
            }
            int i = 0;
            int length = this.startCount.length;
            int n3 = this.startCount.length >> 1;
            while (i < length) {
                if (this.endCount[n3] < n) {
                    i = n3 + 1;
                }
                else {
                    length = n3;
                }
                n3 = i + length >> 1;
            }
            if (n >= this.startCount[n3] && n <= this.endCount[n3]) {
                final char c = this.idRangeOffset[n3];
                if (c == '\0') {
                    n2 = (char)(n + this.idDelta[n3]);
                }
                else {
                    n2 = this.glyphIds[c - this.segCount + n3 + (n - this.startCount[n3])];
                    if (n2 != 0) {
                        n2 = (char)(n2 + this.idDelta[n3]);
                    }
                }
            }
            if (n2 != 0) {}
            return (char)n2;
        }
    }
    
    static class CMapFormat0 extends CMap
    {
        byte[] cmap;
        
        CMapFormat0(final ByteBuffer byteBuffer, final int n) {
            this.cmap = new byte[byteBuffer.getChar(n + 2) - '\u0006'];
            byteBuffer.position(n + 6);
            byteBuffer.get(this.cmap);
        }
        
        @Override
        char getGlyph(final int n) {
            if (n < 256) {
                if (n < 16) {
                    switch (n) {
                        case 9:
                        case 10:
                        case 13: {
                            return '\uffff';
                        }
                    }
                }
                return (char)(0xFF & this.cmap[n]);
            }
            return '\0';
        }
    }
    
    static class CMapFormat2 extends CMap
    {
        char[] subHeaderKey;
        char[] firstCodeArray;
        char[] entryCountArray;
        short[] idDeltaArray;
        char[] idRangeOffSetArray;
        char[] glyphIndexArray;
        
        CMapFormat2(final ByteBuffer byteBuffer, final int n, final char[] xlat) {
            this.subHeaderKey = new char[256];
            this.xlat = xlat;
            final char char1 = byteBuffer.getChar(n + 2);
            byteBuffer.position(n + 6);
            final CharBuffer charBuffer = byteBuffer.asCharBuffer();
            char c = '\0';
            for (int i = 0; i < 256; ++i) {
                this.subHeaderKey[i] = charBuffer.get();
                if (this.subHeaderKey[i] > c) {
                    c = this.subHeaderKey[i];
                }
            }
            final int n2 = (c >> 3) + 1;
            this.firstCodeArray = new char[n2];
            this.entryCountArray = new char[n2];
            this.idDeltaArray = new short[n2];
            this.idRangeOffSetArray = new char[n2];
            for (int j = 0; j < n2; ++j) {
                this.firstCodeArray[j] = charBuffer.get();
                this.entryCountArray[j] = charBuffer.get();
                this.idDeltaArray[j] = (short)charBuffer.get();
                this.idRangeOffSetArray[j] = charBuffer.get();
            }
            final int n3 = (char1 - '\u0206' - n2 * 8) / 2;
            this.glyphIndexArray = new char[n3];
            for (int k = 0; k < n3; ++k) {
                this.glyphIndexArray[k] = charBuffer.get();
            }
        }
        
        @Override
        char getGlyph(int n) {
            final int controlCodeGlyph = this.getControlCodeGlyph(n, true);
            if (controlCodeGlyph >= 0) {
                return (char)controlCodeGlyph;
            }
            if (this.xlat != null) {
                n = this.xlat[n];
            }
            final char c = (char)(n >> 8);
            final char c2 = (char)(n & 0xFF);
            final int n2 = this.subHeaderKey[c] >> 3;
            char c3;
            if (n2 != 0) {
                c3 = c2;
            }
            else {
                c3 = c;
                if (c3 == '\0') {
                    c3 = c2;
                }
            }
            final char c4 = this.firstCodeArray[n2];
            if (c3 < c4) {
                return '\0';
            }
            final char c5 = (char)(c3 - c4);
            if (c5 < this.entryCountArray[n2]) {
                final char c6 = this.glyphIndexArray[(this.idRangeOffSetArray[n2] - ((this.idRangeOffSetArray.length - n2) * 8 - 6)) / 2 + c5];
                if (c6 != '\0') {
                    return (char)(c6 + this.idDeltaArray[n2]);
                }
            }
            return '\0';
        }
    }
    
    static class CMapFormat6 extends CMap
    {
        char firstCode;
        char entryCount;
        char[] glyphIdArray;
        
        CMapFormat6(final ByteBuffer byteBuffer, final int n, final char[] array) {
            byteBuffer.position(n + 6);
            final CharBuffer charBuffer = byteBuffer.asCharBuffer();
            this.firstCode = charBuffer.get();
            this.entryCount = charBuffer.get();
            this.glyphIdArray = new char[this.entryCount];
            for (char c = '\0'; c < this.entryCount; ++c) {
                this.glyphIdArray[c] = charBuffer.get();
            }
        }
        
        @Override
        char getGlyph(int n) {
            final int controlCodeGlyph = this.getControlCodeGlyph(n, true);
            if (controlCodeGlyph >= 0) {
                return (char)controlCodeGlyph;
            }
            if (this.xlat != null) {
                n = this.xlat[n];
            }
            n -= this.firstCode;
            if (n < 0 || n >= this.entryCount) {
                return '\0';
            }
            return this.glyphIdArray[n];
        }
    }
    
    static class CMapFormat8 extends CMap
    {
        byte[] is32;
        int nGroups;
        int[] startCharCode;
        int[] endCharCode;
        int[] startGlyphID;
        
        CMapFormat8(final ByteBuffer byteBuffer, final int n, final char[] array) {
            this.is32 = new byte[8192];
            byteBuffer.position(12);
            byteBuffer.get(this.is32);
            this.nGroups = (byteBuffer.getInt() & Integer.MAX_VALUE);
            if (byteBuffer.remaining() < 12L * this.nGroups) {
                throw new RuntimeException("Format 8 table exceeded");
            }
            this.startCharCode = new int[this.nGroups];
            this.endCharCode = new int[this.nGroups];
            this.startGlyphID = new int[this.nGroups];
        }
        
        @Override
        char getGlyph(final int n) {
            if (this.xlat != null) {
                throw new RuntimeException("xlat array for cmap fmt=8");
            }
            return '\0';
        }
    }
    
    static class CMapFormat10 extends CMap
    {
        long firstCode;
        int entryCount;
        char[] glyphIdArray;
        
        CMapFormat10(final ByteBuffer byteBuffer, final int n, final char[] array) {
            byteBuffer.position(n + 12);
            this.firstCode = (byteBuffer.getInt() & Integer.MAX_VALUE);
            this.entryCount = (byteBuffer.getInt() & Integer.MAX_VALUE);
            if (byteBuffer.remaining() < 2L * this.entryCount) {
                throw new RuntimeException("Format 10 table exceeded");
            }
            final CharBuffer charBuffer = byteBuffer.asCharBuffer();
            this.glyphIdArray = new char[this.entryCount];
            for (int i = 0; i < this.entryCount; ++i) {
                this.glyphIdArray[i] = charBuffer.get();
            }
        }
        
        @Override
        char getGlyph(final int n) {
            if (this.xlat != null) {
                throw new RuntimeException("xlat array for cmap fmt=10");
            }
            final int n2 = (int)(n - this.firstCode);
            if (n2 < 0 || n2 >= this.entryCount) {
                return '\0';
            }
            return this.glyphIdArray[n2];
        }
    }
    
    static class CMapFormat12 extends CMap
    {
        int numGroups;
        int highBit;
        int power;
        int extra;
        long[] startCharCode;
        long[] endCharCode;
        int[] startGlyphID;
        
        CMapFormat12(ByteBuffer slice, final int n, final char[] array) {
            this.highBit = 0;
            if (array != null) {
                throw new RuntimeException("xlat array for cmap fmt=12");
            }
            slice.position(n + 12);
            this.numGroups = (slice.getInt() & Integer.MAX_VALUE);
            if (slice.remaining() < 12L * this.numGroups) {
                throw new RuntimeException("Format 12 table exceeded");
            }
            this.startCharCode = new long[this.numGroups];
            this.endCharCode = new long[this.numGroups];
            this.startGlyphID = new int[this.numGroups];
            slice = slice.slice();
            final IntBuffer intBuffer = slice.asIntBuffer();
            for (int i = 0; i < this.numGroups; ++i) {
                this.startCharCode[i] = (intBuffer.get() & Integer.MAX_VALUE);
                this.endCharCode[i] = (intBuffer.get() & Integer.MAX_VALUE);
                this.startGlyphID[i] = (intBuffer.get() & Integer.MAX_VALUE);
            }
            int numGroups = this.numGroups;
            if (numGroups >= 65536) {
                numGroups >>= 16;
                this.highBit += 16;
            }
            if (numGroups >= 256) {
                numGroups >>= 8;
                this.highBit += 8;
            }
            if (numGroups >= 16) {
                numGroups >>= 4;
                this.highBit += 4;
            }
            if (numGroups >= 4) {
                numGroups >>= 2;
                this.highBit += 2;
            }
            if (numGroups >= 2) {
                ++this.highBit;
            }
            this.power = 1 << this.highBit;
            this.extra = this.numGroups - this.power;
        }
        
        @Override
        char getGlyph(final int n) {
            final int controlCodeGlyph = this.getControlCodeGlyph(n, false);
            if (controlCodeGlyph >= 0) {
                return (char)controlCodeGlyph;
            }
            int i = this.power;
            int extra = 0;
            if (this.startCharCode[this.extra] <= n) {
                extra = this.extra;
            }
            while (i > 1) {
                i >>= 1;
                if (this.startCharCode[extra + i] <= n) {
                    extra += i;
                }
            }
            if (this.startCharCode[extra] <= n && this.endCharCode[extra] >= n) {
                return (char)(this.startGlyphID[extra] + (n - this.startCharCode[extra]));
            }
            return '\0';
        }
    }
    
    static class NullCMapClass extends CMap
    {
        @Override
        char getGlyph(final int n) {
            return '\0';
        }
    }
}
