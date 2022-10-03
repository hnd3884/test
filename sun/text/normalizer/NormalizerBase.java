package sun.text.normalizer;

import java.text.Normalizer;
import java.text.CharacterIterator;

public final class NormalizerBase implements Cloneable
{
    private char[] buffer;
    private int bufferStart;
    private int bufferPos;
    private int bufferLimit;
    private UCharacterIterator text;
    private Mode mode;
    private int options;
    private int currentIndex;
    private int nextIndex;
    public static final int UNICODE_3_2 = 32;
    public static final int DONE = -1;
    public static final Mode NONE;
    public static final Mode NFD;
    public static final Mode NFKD;
    public static final Mode NFC;
    public static final Mode NFKC;
    public static final QuickCheckResult NO;
    public static final QuickCheckResult YES;
    public static final QuickCheckResult MAYBE;
    private static final int MAX_BUF_SIZE_COMPOSE = 2;
    private static final int MAX_BUF_SIZE_DECOMPOSE = 3;
    public static final int UNICODE_3_2_0_ORIGINAL = 262432;
    public static final int UNICODE_LATEST = 0;
    
    public NormalizerBase(final String s, final Mode mode, final int options) {
        this.buffer = new char[100];
        this.bufferStart = 0;
        this.bufferPos = 0;
        this.bufferLimit = 0;
        this.mode = NormalizerBase.NFC;
        this.options = 0;
        this.text = UCharacterIterator.getInstance(s);
        this.mode = mode;
        this.options = options;
    }
    
    public NormalizerBase(final CharacterIterator characterIterator, final Mode mode) {
        this(characterIterator, mode, 0);
    }
    
    public NormalizerBase(final CharacterIterator characterIterator, final Mode mode, final int options) {
        this.buffer = new char[100];
        this.bufferStart = 0;
        this.bufferPos = 0;
        this.bufferLimit = 0;
        this.mode = NormalizerBase.NFC;
        this.options = 0;
        this.text = UCharacterIterator.getInstance((CharacterIterator)characterIterator.clone());
        this.mode = mode;
        this.options = options;
    }
    
    public Object clone() {
        try {
            final NormalizerBase normalizerBase = (NormalizerBase)super.clone();
            normalizerBase.text = (UCharacterIterator)this.text.clone();
            if (this.buffer != null) {
                normalizerBase.buffer = new char[this.buffer.length];
                System.arraycopy(this.buffer, 0, normalizerBase.buffer, 0, this.buffer.length);
            }
            return normalizerBase;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex.toString(), ex);
        }
    }
    
    public static String compose(final String s, final boolean b, int n) {
        char[] array;
        char[] array2;
        if (n == 262432) {
            final String convert = NormalizerImpl.convert(s);
            array = new char[convert.length() * 2];
            array2 = convert.toCharArray();
        }
        else {
            array = new char[s.length() * 2];
            array2 = s.toCharArray();
        }
        final UnicodeSet nx = NormalizerImpl.getNX(n);
        n &= 0xFFFFCF00;
        if (b) {
            n |= 0x1000;
        }
        int compose;
        while (true) {
            compose = NormalizerImpl.compose(array2, 0, array2.length, array, 0, array.length, n, nx);
            if (compose <= array.length) {
                break;
            }
            array = new char[compose];
        }
        return new String(array, 0, compose);
    }
    
    public static String decompose(final String s, final boolean b) {
        return decompose(s, b, 0);
    }
    
    public static String decompose(final String s, final boolean b, final int n) {
        final int[] array = { 0 };
        final UnicodeSet nx = NormalizerImpl.getNX(n);
        if (n == 262432) {
            final String convert = NormalizerImpl.convert(s);
            char[] array2 = new char[convert.length() * 3];
            int decompose;
            while (true) {
                decompose = NormalizerImpl.decompose(convert.toCharArray(), 0, convert.length(), array2, 0, array2.length, b, array, nx);
                if (decompose <= array2.length) {
                    break;
                }
                array2 = new char[decompose];
            }
            return new String(array2, 0, decompose);
        }
        char[] array3 = new char[s.length() * 3];
        int decompose2;
        while (true) {
            decompose2 = NormalizerImpl.decompose(s.toCharArray(), 0, s.length(), array3, 0, array3.length, b, array, nx);
            if (decompose2 <= array3.length) {
                break;
            }
            array3 = new char[decompose2];
        }
        return new String(array3, 0, decompose2);
    }
    
    public static int normalize(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final Mode mode, final int n5) {
        final int normalize = mode.normalize(array, n, n2, array2, n3, n4, n5);
        if (normalize <= n4 - n3) {
            return normalize;
        }
        throw new IndexOutOfBoundsException(Integer.toString(normalize));
    }
    
    public int current() {
        if (this.bufferPos < this.bufferLimit || this.nextNormalize()) {
            return this.getCodePointAt(this.bufferPos);
        }
        return -1;
    }
    
    public int next() {
        if (this.bufferPos < this.bufferLimit || this.nextNormalize()) {
            final int codePoint = this.getCodePointAt(this.bufferPos);
            this.bufferPos += ((codePoint > 65535) ? 2 : 1);
            return codePoint;
        }
        return -1;
    }
    
    public int previous() {
        if (this.bufferPos > 0 || this.previousNormalize()) {
            final int codePoint = this.getCodePointAt(this.bufferPos - 1);
            this.bufferPos -= ((codePoint > 65535) ? 2 : 1);
            return codePoint;
        }
        return -1;
    }
    
    public void reset() {
        this.text.setIndex(0);
        final int n = 0;
        this.nextIndex = n;
        this.currentIndex = n;
        this.clearBuffer();
    }
    
    public void setIndexOnly(final int currentIndex) {
        this.text.setIndex(currentIndex);
        this.nextIndex = currentIndex;
        this.currentIndex = currentIndex;
        this.clearBuffer();
    }
    
    @Deprecated
    public int setIndex(final int indexOnly) {
        this.setIndexOnly(indexOnly);
        return this.current();
    }
    
    @Deprecated
    public int getBeginIndex() {
        return 0;
    }
    
    @Deprecated
    public int getEndIndex() {
        return this.endIndex();
    }
    
    public int getIndex() {
        if (this.bufferPos < this.bufferLimit) {
            return this.currentIndex;
        }
        return this.nextIndex;
    }
    
    public int endIndex() {
        return this.text.getLength();
    }
    
    public void setMode(final Mode mode) {
        this.mode = mode;
    }
    
    public Mode getMode() {
        return this.mode;
    }
    
    public void setText(final String s) {
        final UCharacterIterator instance = UCharacterIterator.getInstance(s);
        if (instance == null) {
            throw new InternalError("Could not create a new UCharacterIterator");
        }
        this.text = instance;
        this.reset();
    }
    
    public void setText(final CharacterIterator characterIterator) {
        final UCharacterIterator instance = UCharacterIterator.getInstance(characterIterator);
        if (instance == null) {
            throw new InternalError("Could not create a new UCharacterIterator");
        }
        this.text = instance;
        final int n = 0;
        this.nextIndex = n;
        this.currentIndex = n;
        this.clearBuffer();
    }
    
    private static long getPrevNorm32(final UCharacterIterator uCharacterIterator, final int n, final int n2, final char[] array) {
        final int previous;
        if ((previous = uCharacterIterator.previous()) == -1) {
            return 0L;
        }
        array[0] = (char)previous;
        array[1] = '\0';
        if (array[0] < n) {
            return 0L;
        }
        if (!UTF16.isSurrogate(array[0])) {
            return NormalizerImpl.getNorm32(array[0]);
        }
        if (UTF16.isLeadSurrogate(array[0]) || uCharacterIterator.getIndex() == 0) {
            array[1] = (char)uCharacterIterator.current();
            return 0L;
        }
        final int n3 = 1;
        final char c = (char)uCharacterIterator.previous();
        array[n3] = c;
        if (!UTF16.isLeadSurrogate(c)) {
            uCharacterIterator.moveIndex(1);
            return 0L;
        }
        final long norm32 = NormalizerImpl.getNorm32(array[1]);
        if ((norm32 & (long)n2) == 0x0L) {
            return 0L;
        }
        return NormalizerImpl.getNorm32FromSurrogatePair(norm32, array[0]);
    }
    
    private static int findPreviousIterationBoundary(final UCharacterIterator uCharacterIterator, final IsPrevBoundary isPrevBoundary, final int n, final int n2, char[] array, final int[] array2) {
        final char[] array3 = new char[2];
        array2[0] = array.length;
        array3[0] = '\0';
        while (uCharacterIterator.getIndex() > 0 && array3[0] != -1) {
            final boolean prevBoundary = isPrevBoundary.isPrevBoundary(uCharacterIterator, n, n2, array3);
            if (array2[0] < ((array3[1] == '\0') ? 1 : 2)) {
                final char[] array4 = new char[array.length * 2];
                System.arraycopy(array, array2[0], array4, array4.length - (array.length - array2[0]), array.length - array2[0]);
                final int n3 = 0;
                array2[n3] += array4.length - array.length;
                array = array4;
            }
            final char[] array5 = array;
            final int n4 = 0;
            array5[--array2[n4]] = array3[0];
            if (array3[1] != '\0') {
                final char[] array6 = array;
                final int n5 = 0;
                array6[--array2[n5]] = array3[1];
            }
            if (prevBoundary) {
                break;
            }
        }
        return array.length - array2[0];
    }
    
    private static int previous(final UCharacterIterator uCharacterIterator, final char[] array, final int n, final int n2, final Mode mode, final boolean b, final boolean[] array2, final int n3) {
        final int n4 = n2 - n;
        int normalize = 0;
        if (array2 != null) {
            array2[0] = false;
        }
        final char c = (char)mode.getMinC();
        final int mask = mode.getMask();
        final IsPrevBoundary prevBoundary = mode.getPrevBoundary();
        if (prevBoundary == null) {
            int n5 = 0;
            int previous;
            if ((previous = uCharacterIterator.previous()) >= 0) {
                n5 = 1;
                if (UTF16.isTrailSurrogate((char)previous)) {
                    final int previous2 = uCharacterIterator.previous();
                    if (previous2 != -1) {
                        if (UTF16.isLeadSurrogate((char)previous2)) {
                            if (n4 >= 2) {
                                array[1] = (char)previous;
                                n5 = 2;
                            }
                            previous = previous2;
                        }
                        else {
                            uCharacterIterator.moveIndex(1);
                        }
                    }
                }
                if (n4 > 0) {
                    array[0] = (char)previous;
                }
            }
            return n5;
        }
        final char[] array3 = new char[100];
        final int[] array4 = { 0 };
        final int previousIterationBoundary = findPreviousIterationBoundary(uCharacterIterator, prevBoundary, c, mask, array3, array4);
        if (previousIterationBoundary > 0) {
            if (b) {
                normalize = normalize(array3, array4[0], array4[0] + previousIterationBoundary, array, n, n2, mode, n3);
                if (array2 != null) {
                    array2[0] = (normalize != previousIterationBoundary || Utility.arrayRegionMatches(array3, 0, array, n, n2));
                }
            }
            else if (n4 > 0) {
                System.arraycopy(array3, array4[0], array, 0, (previousIterationBoundary < n4) ? previousIterationBoundary : n4);
            }
        }
        return normalize;
    }
    
    private static long getNextNorm32(final UCharacterIterator uCharacterIterator, final int n, final int n2, final int[] array) {
        array[0] = uCharacterIterator.next();
        array[1] = 0;
        if (array[0] < n) {
            return 0L;
        }
        final long norm32 = NormalizerImpl.getNorm32((char)array[0]);
        if (UTF16.isLeadSurrogate((char)array[0])) {
            if (uCharacterIterator.current() != -1) {
                final int n3 = 1;
                final int current = uCharacterIterator.current();
                array[n3] = current;
                if (UTF16.isTrailSurrogate((char)current)) {
                    uCharacterIterator.moveIndex(1);
                    if ((norm32 & (long)n2) == 0x0L) {
                        return 0L;
                    }
                    return NormalizerImpl.getNorm32FromSurrogatePair(norm32, (char)array[1]);
                }
            }
            return 0L;
        }
        return norm32;
    }
    
    private static int findNextIterationBoundary(final UCharacterIterator uCharacterIterator, final IsNextBoundary isNextBoundary, final int n, final int n2, char[] array) {
        if (uCharacterIterator.current() == -1) {
            return 0;
        }
        final int[] array2 = { uCharacterIterator.next(), 0 };
        array[0] = (char)array2[0];
        int n3 = 1;
        if (UTF16.isLeadSurrogate((char)array2[0]) && uCharacterIterator.current() != -1) {
            final int[] array3 = array2;
            final int n4 = 1;
            final int next = uCharacterIterator.next();
            array3[n4] = next;
            if (UTF16.isTrailSurrogate((char)next)) {
                array[n3++] = (char)array2[1];
            }
            else {
                uCharacterIterator.moveIndex(-1);
            }
        }
        while (uCharacterIterator.current() != -1) {
            if (isNextBoundary.isNextBoundary(uCharacterIterator, n, n2, array2)) {
                uCharacterIterator.moveIndex((array2[1] == 0) ? -1 : -2);
                break;
            }
            if (n3 + ((array2[1] == 0) ? 1 : 2) <= array.length) {
                array[n3++] = (char)array2[0];
                if (array2[1] == 0) {
                    continue;
                }
                array[n3++] = (char)array2[1];
            }
            else {
                final char[] array4 = new char[array.length * 2];
                System.arraycopy(array, 0, array4, 0, n3);
                array = array4;
                array[n3++] = (char)array2[0];
                if (array2[1] == 0) {
                    continue;
                }
                array[n3++] = (char)array2[1];
            }
        }
        return n3;
    }
    
    private static int next(final UCharacterIterator uCharacterIterator, final char[] array, final int n, final int n2, final Mode mode, final boolean b, final boolean[] array2, final int n3) {
        final int n4 = n2 - n;
        int normalize = 0;
        if (array2 != null) {
            array2[0] = false;
        }
        final char c = (char)mode.getMinC();
        final int mask = mode.getMask();
        final IsNextBoundary nextBoundary = mode.getNextBoundary();
        if (nextBoundary == null) {
            int n5 = 0;
            final int next = uCharacterIterator.next();
            if (next != -1) {
                n5 = 1;
                if (UTF16.isLeadSurrogate((char)next)) {
                    final int next2 = uCharacterIterator.next();
                    if (next2 != -1) {
                        if (UTF16.isTrailSurrogate((char)next2)) {
                            if (n4 >= 2) {
                                array[1] = (char)next2;
                                n5 = 2;
                            }
                        }
                        else {
                            uCharacterIterator.moveIndex(-1);
                        }
                    }
                }
                if (n4 > 0) {
                    array[0] = (char)next;
                }
            }
            return n5;
        }
        final char[] array3 = new char[100];
        final int[] array4 = { 0 };
        final int nextIterationBoundary = findNextIterationBoundary(uCharacterIterator, nextBoundary, c, mask, array3);
        if (nextIterationBoundary > 0) {
            if (b) {
                normalize = mode.normalize(array3, array4[0], nextIterationBoundary, array, n, n2, n3);
                if (array2 != null) {
                    array2[0] = (normalize != nextIterationBoundary || Utility.arrayRegionMatches(array3, array4[0], array, n, normalize));
                }
            }
            else if (n4 > 0) {
                System.arraycopy(array3, 0, array, n, Math.min(nextIterationBoundary, n4));
            }
        }
        return normalize;
    }
    
    private void clearBuffer() {
        final int bufferLimit = 0;
        this.bufferPos = bufferLimit;
        this.bufferStart = bufferLimit;
        this.bufferLimit = bufferLimit;
    }
    
    private boolean nextNormalize() {
        this.clearBuffer();
        this.currentIndex = this.nextIndex;
        this.text.setIndex(this.nextIndex);
        this.bufferLimit = next(this.text, this.buffer, this.bufferStart, this.buffer.length, this.mode, true, null, this.options);
        this.nextIndex = this.text.getIndex();
        return this.bufferLimit > 0;
    }
    
    private boolean previousNormalize() {
        this.clearBuffer();
        this.nextIndex = this.currentIndex;
        this.text.setIndex(this.currentIndex);
        this.bufferLimit = previous(this.text, this.buffer, this.bufferStart, this.buffer.length, this.mode, true, null, this.options);
        this.currentIndex = this.text.getIndex();
        this.bufferPos = this.bufferLimit;
        return this.bufferLimit > 0;
    }
    
    private int getCodePointAt(final int n) {
        if (UTF16.isSurrogate(this.buffer[n])) {
            if (UTF16.isLeadSurrogate(this.buffer[n])) {
                if (n + 1 < this.bufferLimit && UTF16.isTrailSurrogate(this.buffer[n + 1])) {
                    return UCharacterProperty.getRawSupplementary(this.buffer[n], this.buffer[n + 1]);
                }
            }
            else if (UTF16.isTrailSurrogate(this.buffer[n]) && n > 0 && UTF16.isLeadSurrogate(this.buffer[n - 1])) {
                return UCharacterProperty.getRawSupplementary(this.buffer[n - 1], this.buffer[n]);
            }
        }
        return this.buffer[n];
    }
    
    public static boolean isNFSkippable(final int n, final Mode mode) {
        return mode.isNFSkippable(n);
    }
    
    public NormalizerBase(final String s, final Mode mode) {
        this(s, mode, 0);
    }
    
    public static String normalize(final String s, final Normalizer.Form form) {
        return normalize(s, form, 0);
    }
    
    public static String normalize(final String s, final Normalizer.Form form, final int n) {
        final int length = s.length();
        boolean b = true;
        if (length < 80) {
            for (int i = 0; i < length; ++i) {
                if (s.charAt(i) > '\u007f') {
                    b = false;
                    break;
                }
            }
        }
        else {
            final char[] charArray = s.toCharArray();
            for (int j = 0; j < length; ++j) {
                if (charArray[j] > '\u007f') {
                    b = false;
                    break;
                }
            }
        }
        switch (form) {
            case NFC: {
                return b ? s : NormalizerBase.NFC.normalize(s, n);
            }
            case NFD: {
                return b ? s : NormalizerBase.NFD.normalize(s, n);
            }
            case NFKC: {
                return b ? s : NormalizerBase.NFKC.normalize(s, n);
            }
            case NFKD: {
                return b ? s : NormalizerBase.NFKD.normalize(s, n);
            }
            default: {
                throw new IllegalArgumentException("Unexpected normalization form: " + form);
            }
        }
    }
    
    public static boolean isNormalized(final String s, final Normalizer.Form form) {
        return isNormalized(s, form, 0);
    }
    
    public static boolean isNormalized(final String s, final Normalizer.Form form, final int n) {
        switch (form) {
            case NFC: {
                return NormalizerBase.NFC.quickCheck(s.toCharArray(), 0, s.length(), false, NormalizerImpl.getNX(n)) == NormalizerBase.YES;
            }
            case NFD: {
                return NormalizerBase.NFD.quickCheck(s.toCharArray(), 0, s.length(), false, NormalizerImpl.getNX(n)) == NormalizerBase.YES;
            }
            case NFKC: {
                return NormalizerBase.NFKC.quickCheck(s.toCharArray(), 0, s.length(), false, NormalizerImpl.getNX(n)) == NormalizerBase.YES;
            }
            case NFKD: {
                return NormalizerBase.NFKD.quickCheck(s.toCharArray(), 0, s.length(), false, NormalizerImpl.getNX(n)) == NormalizerBase.YES;
            }
            default: {
                throw new IllegalArgumentException("Unexpected normalization form: " + form);
            }
        }
    }
    
    static {
        NONE = new Mode(1);
        NFD = new NFDMode(2);
        NFKD = new NFKDMode(3);
        NFC = new NFCMode(4);
        NFKC = new NFKCMode(5);
        NO = new QuickCheckResult(0);
        YES = new QuickCheckResult(1);
        MAYBE = new QuickCheckResult(2);
    }
    
    public static class Mode
    {
        private int modeValue;
        
        private Mode(final int modeValue) {
            this.modeValue = modeValue;
        }
        
        protected int normalize(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final UnicodeSet set) {
            final int n5 = n2 - n;
            if (n5 > n4 - n3) {
                return n5;
            }
            System.arraycopy(array, n, array2, n3, n5);
            return n5;
        }
        
        protected int normalize(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final int n5) {
            return this.normalize(array, n, n2, array2, n3, n4, NormalizerImpl.getNX(n5));
        }
        
        protected String normalize(final String s, final int n) {
            return s;
        }
        
        protected int getMinC() {
            return -1;
        }
        
        protected int getMask() {
            return -1;
        }
        
        protected IsPrevBoundary getPrevBoundary() {
            return null;
        }
        
        protected IsNextBoundary getNextBoundary() {
            return null;
        }
        
        protected QuickCheckResult quickCheck(final char[] array, final int n, final int n2, final boolean b, final UnicodeSet set) {
            if (b) {
                return NormalizerBase.MAYBE;
            }
            return NormalizerBase.NO;
        }
        
        protected boolean isNFSkippable(final int n) {
            return true;
        }
    }
    
    private static final class NFDMode extends Mode
    {
        private NFDMode(final int n) {
            super(n);
        }
        
        @Override
        protected int normalize(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final UnicodeSet set) {
            return NormalizerImpl.decompose(array, n, n2, array2, n3, n4, false, new int[1], set);
        }
        
        @Override
        protected String normalize(final String s, final int n) {
            return NormalizerBase.decompose(s, false, n);
        }
        
        @Override
        protected int getMinC() {
            return 768;
        }
        
        @Override
        protected IsPrevBoundary getPrevBoundary() {
            return new IsPrevNFDSafe();
        }
        
        @Override
        protected IsNextBoundary getNextBoundary() {
            return new IsNextNFDSafe();
        }
        
        @Override
        protected int getMask() {
            return 65284;
        }
        
        @Override
        protected QuickCheckResult quickCheck(final char[] array, final int n, final int n2, final boolean b, final UnicodeSet set) {
            return NormalizerImpl.quickCheck(array, n, n2, NormalizerImpl.getFromIndexesArr(8), 4, 0, b, set);
        }
        
        @Override
        protected boolean isNFSkippable(final int n) {
            return NormalizerImpl.isNFSkippable(n, this, 65284L);
        }
    }
    
    private static final class NFKDMode extends Mode
    {
        private NFKDMode(final int n) {
            super(n);
        }
        
        @Override
        protected int normalize(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final UnicodeSet set) {
            return NormalizerImpl.decompose(array, n, n2, array2, n3, n4, true, new int[1], set);
        }
        
        @Override
        protected String normalize(final String s, final int n) {
            return NormalizerBase.decompose(s, true, n);
        }
        
        @Override
        protected int getMinC() {
            return 768;
        }
        
        @Override
        protected IsPrevBoundary getPrevBoundary() {
            return new IsPrevNFDSafe();
        }
        
        @Override
        protected IsNextBoundary getNextBoundary() {
            return new IsNextNFDSafe();
        }
        
        @Override
        protected int getMask() {
            return 65288;
        }
        
        @Override
        protected QuickCheckResult quickCheck(final char[] array, final int n, final int n2, final boolean b, final UnicodeSet set) {
            return NormalizerImpl.quickCheck(array, n, n2, NormalizerImpl.getFromIndexesArr(9), 8, 4096, b, set);
        }
        
        @Override
        protected boolean isNFSkippable(final int n) {
            return NormalizerImpl.isNFSkippable(n, this, 65288L);
        }
    }
    
    private static final class NFCMode extends Mode
    {
        private NFCMode(final int n) {
            super(n);
        }
        
        @Override
        protected int normalize(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final UnicodeSet set) {
            return NormalizerImpl.compose(array, n, n2, array2, n3, n4, 0, set);
        }
        
        @Override
        protected String normalize(final String s, final int n) {
            return NormalizerBase.compose(s, false, n);
        }
        
        @Override
        protected int getMinC() {
            return NormalizerImpl.getFromIndexesArr(6);
        }
        
        @Override
        protected IsPrevBoundary getPrevBoundary() {
            return new IsPrevTrueStarter();
        }
        
        @Override
        protected IsNextBoundary getNextBoundary() {
            return new IsNextTrueStarter();
        }
        
        @Override
        protected int getMask() {
            return 65297;
        }
        
        @Override
        protected QuickCheckResult quickCheck(final char[] array, final int n, final int n2, final boolean b, final UnicodeSet set) {
            return NormalizerImpl.quickCheck(array, n, n2, NormalizerImpl.getFromIndexesArr(6), 17, 0, b, set);
        }
        
        @Override
        protected boolean isNFSkippable(final int n) {
            return NormalizerImpl.isNFSkippable(n, this, 65473L);
        }
    }
    
    private static final class NFKCMode extends Mode
    {
        private NFKCMode(final int n) {
            super(n);
        }
        
        @Override
        protected int normalize(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final UnicodeSet set) {
            return NormalizerImpl.compose(array, n, n2, array2, n3, n4, 4096, set);
        }
        
        @Override
        protected String normalize(final String s, final int n) {
            return NormalizerBase.compose(s, true, n);
        }
        
        @Override
        protected int getMinC() {
            return NormalizerImpl.getFromIndexesArr(7);
        }
        
        @Override
        protected IsPrevBoundary getPrevBoundary() {
            return new IsPrevTrueStarter();
        }
        
        @Override
        protected IsNextBoundary getNextBoundary() {
            return new IsNextTrueStarter();
        }
        
        @Override
        protected int getMask() {
            return 65314;
        }
        
        @Override
        protected QuickCheckResult quickCheck(final char[] array, final int n, final int n2, final boolean b, final UnicodeSet set) {
            return NormalizerImpl.quickCheck(array, n, n2, NormalizerImpl.getFromIndexesArr(7), 34, 4096, b, set);
        }
        
        @Override
        protected boolean isNFSkippable(final int n) {
            return NormalizerImpl.isNFSkippable(n, this, 65474L);
        }
    }
    
    public static final class QuickCheckResult
    {
        private int resultValue;
        
        private QuickCheckResult(final int resultValue) {
            this.resultValue = resultValue;
        }
    }
    
    private static final class IsPrevNFDSafe implements IsPrevBoundary
    {
        @Override
        public boolean isPrevBoundary(final UCharacterIterator uCharacterIterator, final int n, final int n2, final char[] array) {
            return NormalizerImpl.isNFDSafe(getPrevNorm32(uCharacterIterator, n, n2, array), n2, n2 & 0x3F);
        }
    }
    
    private static final class IsPrevTrueStarter implements IsPrevBoundary
    {
        @Override
        public boolean isPrevBoundary(final UCharacterIterator uCharacterIterator, final int n, final int n2, final char[] array) {
            final int n3 = n2 << 2 & 0xF;
            return NormalizerImpl.isTrueStarter(getPrevNorm32(uCharacterIterator, n, n2 | n3, array), n2, n3);
        }
    }
    
    private static final class IsNextNFDSafe implements IsNextBoundary
    {
        @Override
        public boolean isNextBoundary(final UCharacterIterator uCharacterIterator, final int n, final int n2, final int[] array) {
            return NormalizerImpl.isNFDSafe(getNextNorm32(uCharacterIterator, n, n2, array), n2, n2 & 0x3F);
        }
    }
    
    private static final class IsNextTrueStarter implements IsNextBoundary
    {
        @Override
        public boolean isNextBoundary(final UCharacterIterator uCharacterIterator, final int n, final int n2, final int[] array) {
            final int n3 = n2 << 2 & 0xF;
            return NormalizerImpl.isTrueStarter(getNextNorm32(uCharacterIterator, n, n2 | n3, array), n2, n3);
        }
    }
    
    private interface IsNextBoundary
    {
        boolean isNextBoundary(final UCharacterIterator p0, final int p1, final int p2, final int[] p3);
    }
    
    private interface IsPrevBoundary
    {
        boolean isPrevBoundary(final UCharacterIterator p0, final int p1, final int p2, final char[] p3);
    }
}
