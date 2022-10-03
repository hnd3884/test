package sun.nio.cs.ext;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.nio.charset.CoderResult;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class JISAutoDetect extends Charset implements HistoricallyNamedCharset
{
    private static final int EUCJP_MASK = 1;
    private static final int SJIS2B_MASK = 2;
    private static final int SJIS1B_MASK = 4;
    private static final int EUCJP_KANA1_MASK = 8;
    private static final int EUCJP_KANA2_MASK = 16;
    
    public JISAutoDetect() {
        super("x-JISAutoDetect", ExtendedCharsets.aliasesFor("x-JISAutoDetect"));
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof SJIS || charset instanceof EUC_JP || charset instanceof ISO2022_JP;
    }
    
    @Override
    public boolean canEncode() {
        return false;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public String historicalName() {
        return "JISAutoDetect";
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        throw new UnsupportedOperationException();
    }
    
    public static byte[] getByteMask1() {
        return Decoder.maskTable1;
    }
    
    public static byte[] getByteMask2() {
        return Decoder.maskTable2;
    }
    
    public static final boolean canBeSJIS1B(final int n) {
        return (n & 0x4) != 0x0;
    }
    
    public static final boolean canBeEUCJP(final int n) {
        return (n & 0x1) != 0x0;
    }
    
    public static final boolean canBeEUCKana(final int n, final int n2) {
        return (n & 0x8) != 0x0 && (n2 & 0x10) != 0x0;
    }
    
    private static boolean looksLikeJapanese(final CharBuffer charBuffer) {
        int n = 0;
        int n2 = 0;
        while (charBuffer.hasRemaining()) {
            final char value = charBuffer.get();
            if ('\u3040' <= value && value <= '\u309f' && ++n > 1) {
                return true;
            }
            if ('\uff65' <= value && value <= '\uff9f' && ++n2 > 1) {
                return true;
            }
        }
        return false;
    }
    
    private static class Decoder extends CharsetDecoder
    {
        private static final String SJISName;
        private static final String EUCJPName;
        private DelegatableDecoder detectedDecoder;
        private static final byte[] maskTable1;
        private static final byte[] maskTable2;
        
        public Decoder(final Charset charset) {
            super(charset, 0.5f, 1.0f);
            this.detectedDecoder = null;
        }
        
        private static boolean isPlainASCII(final byte b) {
            return b >= 0 && b != 27;
        }
        
        private static void copyLeadingASCII(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            final int position = byteBuffer.position();
            int n;
            int n2;
            byte value;
            for (n = position + Math.min(byteBuffer.remaining(), charBuffer.remaining()), n2 = position; n2 < n && isPlainASCII(value = byteBuffer.get(n2)); ++n2) {
                charBuffer.put((char)(value & 0xFF));
            }
            byteBuffer.position(n2);
        }
        
        private CoderResult decodeLoop(final Charset charset, final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            this.detectedDecoder = (DelegatableDecoder)charset.newDecoder();
            return this.detectedDecoder.decodeLoop(byteBuffer, charBuffer);
        }
        
        @Override
        protected CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (this.detectedDecoder != null) {
                return this.detectedDecoder.decodeLoop(byteBuffer, charBuffer);
            }
            copyLeadingASCII(byteBuffer, charBuffer);
            if (!byteBuffer.hasRemaining()) {
                return CoderResult.UNDERFLOW;
            }
            if (!charBuffer.hasRemaining()) {
                return CoderResult.OVERFLOW;
            }
            final int n = (int)(byteBuffer.limit() * (double)this.maxCharsPerByte());
            final CharBuffer allocate = CharBuffer.allocate(n);
            final Charset forName = Charset.forName("ISO-2022-JP");
            if (!((DelegatableDecoder)forName.newDecoder()).decodeLoop(byteBuffer.asReadOnlyBuffer(), allocate).isError()) {
                return this.decodeLoop(forName, byteBuffer, charBuffer);
            }
            final Charset forName2 = Charset.forName(Decoder.EUCJPName);
            final Charset forName3 = Charset.forName(Decoder.SJISName);
            final DelegatableDecoder delegatableDecoder = (DelegatableDecoder)forName2.newDecoder();
            final ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
            allocate.clear();
            if (delegatableDecoder.decodeLoop(readOnlyBuffer, allocate).isError()) {
                return this.decodeLoop(forName3, byteBuffer, charBuffer);
            }
            final DelegatableDecoder delegatableDecoder2 = (DelegatableDecoder)forName3.newDecoder();
            final ByteBuffer readOnlyBuffer2 = byteBuffer.asReadOnlyBuffer();
            if (delegatableDecoder2.decodeLoop(readOnlyBuffer2, CharBuffer.allocate(n)).isError()) {
                return this.decodeLoop(forName2, byteBuffer, charBuffer);
            }
            if (readOnlyBuffer.position() > readOnlyBuffer2.position()) {
                return this.decodeLoop(forName2, byteBuffer, charBuffer);
            }
            if (readOnlyBuffer.position() < readOnlyBuffer2.position()) {
                return this.decodeLoop(forName3, byteBuffer, charBuffer);
            }
            if (byteBuffer.position() == readOnlyBuffer.position()) {
                return CoderResult.UNDERFLOW;
            }
            allocate.flip();
            return this.decodeLoop(looksLikeJapanese(allocate) ? forName2 : forName3, byteBuffer, charBuffer);
        }
        
        @Override
        protected void implReset() {
            this.detectedDecoder = null;
        }
        
        @Override
        protected CoderResult implFlush(final CharBuffer charBuffer) {
            if (this.detectedDecoder != null) {
                return this.detectedDecoder.implFlush(charBuffer);
            }
            return super.implFlush(charBuffer);
        }
        
        @Override
        public boolean isAutoDetecting() {
            return true;
        }
        
        @Override
        public boolean isCharsetDetected() {
            return this.detectedDecoder != null;
        }
        
        @Override
        public Charset detectedCharset() {
            if (this.detectedDecoder == null) {
                throw new IllegalStateException("charset not yet detected");
            }
            return ((CharsetDecoder)this.detectedDecoder).charset();
        }
        
        private static String getSJISName() {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.name"));
            if (s.equals("Solaris") || s.equals("SunOS")) {
                return "PCK";
            }
            if (s.startsWith("Windows")) {
                return "windows-31J";
            }
            return "Shift_JIS";
        }
        
        private static String getEUCJPName() {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.name"));
            if (s.equals("Solaris") || s.equals("SunOS")) {
                return "x-eucjp-open";
            }
            return "EUC_JP";
        }
        
        static {
            SJISName = getSJISName();
            EUCJPName = getEUCJPName();
            maskTable1 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 5, 5, 5, 13, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 0 };
            maskTable2 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 0 };
        }
    }
}
