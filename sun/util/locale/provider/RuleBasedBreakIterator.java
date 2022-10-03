package sun.util.locale.provider;

import java.text.StringCharacterIterator;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.BufferedInputStream;
import java.util.MissingResourceException;
import java.io.IOException;
import java.text.CharacterIterator;
import sun.text.SupplementaryCharacterData;
import sun.text.CompactByteArray;
import java.text.BreakIterator;

class RuleBasedBreakIterator extends BreakIterator
{
    protected static final byte IGNORE = -1;
    private static final short START_STATE = 1;
    private static final short STOP_STATE = 0;
    static final byte[] LABEL;
    static final int LABEL_LENGTH;
    static final byte supportedVersion = 1;
    private static final int HEADER_LENGTH = 36;
    private static final int BMP_INDICES_LENGTH = 512;
    private CompactByteArray charCategoryTable;
    private SupplementaryCharacterData supplementaryCharCategoryTable;
    private short[] stateTable;
    private short[] backwardsStateTable;
    private boolean[] endStates;
    private boolean[] lookaheadStates;
    private byte[] additionalData;
    private int numCategories;
    private CharacterIterator text;
    private long checksum;
    private int cachedLastKnownBreak;
    
    RuleBasedBreakIterator(final String s) throws IOException, MissingResourceException {
        this.charCategoryTable = null;
        this.supplementaryCharCategoryTable = null;
        this.stateTable = null;
        this.backwardsStateTable = null;
        this.endStates = null;
        this.lookaheadStates = null;
        this.additionalData = null;
        this.text = null;
        this.cachedLastKnownBreak = -1;
        this.readTables(s);
    }
    
    protected final void readTables(final String s) throws IOException, MissingResourceException {
        final byte[] file = this.readFile(s);
        final int int1 = getInt(file, 0);
        final int int2 = getInt(file, 4);
        final int int3 = getInt(file, 8);
        final int int4 = getInt(file, 12);
        final int int5 = getInt(file, 16);
        final int int6 = getInt(file, 20);
        final int int7 = getInt(file, 24);
        this.checksum = getLong(file, 28);
        this.stateTable = new short[int1];
        int n = 36;
        for (int i = 0; i < int1; ++i, n += 2) {
            this.stateTable[i] = getShort(file, n);
        }
        this.backwardsStateTable = new short[int2];
        for (int j = 0; j < int2; ++j, n += 2) {
            this.backwardsStateTable[j] = getShort(file, n);
        }
        this.endStates = new boolean[int3];
        for (int k = 0; k < int3; ++k, ++n) {
            this.endStates[k] = (file[n] == 1);
        }
        this.lookaheadStates = new boolean[int4];
        for (int l = 0; l < int4; ++l, ++n) {
            this.lookaheadStates[l] = (file[n] == 1);
        }
        final short[] array = new short[512];
        for (int n2 = 0; n2 < 512; ++n2, n += 2) {
            array[n2] = getShort(file, n);
        }
        final byte[] array2 = new byte[int5];
        System.arraycopy(file, n, array2, 0, int5);
        int n3 = n + int5;
        this.charCategoryTable = new CompactByteArray(array, array2);
        final int[] array3 = new int[int6];
        for (int n4 = 0; n4 < int6; ++n4, n3 += 4) {
            array3[n4] = getInt(file, n3);
        }
        this.supplementaryCharCategoryTable = new SupplementaryCharacterData(array3);
        if (int7 > 0) {
            System.arraycopy(file, n3, this.additionalData = new byte[int7], 0, int7);
        }
        this.numCategories = this.stateTable.length / this.endStates.length;
    }
    
    protected byte[] readFile(final String s) throws IOException, MissingResourceException {
        BufferedInputStream bufferedInputStream;
        try {
            bufferedInputStream = AccessController.doPrivileged((PrivilegedExceptionAction<BufferedInputStream>)new PrivilegedExceptionAction<BufferedInputStream>() {
                @Override
                public BufferedInputStream run() throws Exception {
                    return new BufferedInputStream(this.getClass().getResourceAsStream("/sun/text/resources/" + s));
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new InternalError(ex.toString(), ex);
        }
        int n = 0;
        final int n2 = RuleBasedBreakIterator.LABEL_LENGTH + 5;
        final byte[] array = new byte[n2];
        if (bufferedInputStream.read(array) != n2) {
            throw new MissingResourceException("Wrong header length", s, "");
        }
        for (int i = 0; i < RuleBasedBreakIterator.LABEL_LENGTH; ++i, ++n) {
            if (array[n] != RuleBasedBreakIterator.LABEL[n]) {
                throw new MissingResourceException("Wrong magic number", s, "");
            }
        }
        if (array[n] != 1) {
            throw new MissingResourceException("Unsupported version(" + array[n] + ")", s, "");
        }
        final int int1 = getInt(array, ++n);
        final byte[] array2 = new byte[int1];
        if (bufferedInputStream.read(array2) != int1) {
            throw new MissingResourceException("Wrong data length", s, "");
        }
        bufferedInputStream.close();
        return array2;
    }
    
    byte[] getAdditionalData() {
        return this.additionalData;
    }
    
    void setAdditionalData(final byte[] additionalData) {
        this.additionalData = additionalData;
    }
    
    @Override
    public Object clone() {
        final RuleBasedBreakIterator ruleBasedBreakIterator = (RuleBasedBreakIterator)super.clone();
        if (this.text != null) {
            ruleBasedBreakIterator.text = (CharacterIterator)this.text.clone();
        }
        return ruleBasedBreakIterator;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            if (o == null) {
                return false;
            }
            final RuleBasedBreakIterator ruleBasedBreakIterator = (RuleBasedBreakIterator)o;
            if (this.checksum != ruleBasedBreakIterator.checksum) {
                return false;
            }
            if (this.text == null) {
                return ruleBasedBreakIterator.text == null;
            }
            return this.text.equals(ruleBasedBreakIterator.text);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append("checksum=0x");
        sb.append(Long.toHexString(this.checksum));
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return (int)this.checksum;
    }
    
    @Override
    public int first() {
        final CharacterIterator text = this.getText();
        text.first();
        return text.getIndex();
    }
    
    @Override
    public int last() {
        final CharacterIterator text = this.getText();
        text.setIndex(text.getEndIndex());
        return text.getIndex();
    }
    
    @Override
    public int next(int i) {
        int n = this.current();
        while (i > 0) {
            n = this.handleNext();
            --i;
        }
        while (i < 0) {
            n = this.previous();
            ++i;
        }
        return n;
    }
    
    @Override
    public int next() {
        return this.handleNext();
    }
    
    @Override
    public int previous() {
        final CharacterIterator text = this.getText();
        if (this.current() == text.getBeginIndex()) {
            return -1;
        }
        final int current = this.current();
        int cachedLastKnownBreak = this.cachedLastKnownBreak;
        if (cachedLastKnownBreak >= current || cachedLastKnownBreak <= -1) {
            this.getPrevious();
            cachedLastKnownBreak = this.handlePrevious();
        }
        else {
            text.setIndex(cachedLastKnownBreak);
        }
        for (int handleNext = cachedLastKnownBreak; handleNext != -1 && handleNext < current; handleNext = this.handleNext()) {
            cachedLastKnownBreak = handleNext;
        }
        text.setIndex(cachedLastKnownBreak);
        return this.cachedLastKnownBreak = cachedLastKnownBreak;
    }
    
    private int getPrevious() {
        final char previous = this.text.previous();
        if (Character.isLowSurrogate(previous) && this.text.getIndex() > this.text.getBeginIndex()) {
            final char previous2 = this.text.previous();
            if (Character.isHighSurrogate(previous2)) {
                return Character.toCodePoint(previous2, previous);
            }
            this.text.next();
        }
        return previous;
    }
    
    int getCurrent() {
        final char current = this.text.current();
        if (Character.isHighSurrogate(current) && this.text.getIndex() < this.text.getEndIndex()) {
            final char next = this.text.next();
            this.text.previous();
            if (Character.isLowSurrogate(next)) {
                return Character.toCodePoint(current, next);
            }
        }
        return current;
    }
    
    private int getCurrentCodePointCount() {
        if (Character.isHighSurrogate(this.text.current()) && this.text.getIndex() < this.text.getEndIndex()) {
            final char next = this.text.next();
            this.text.previous();
            if (Character.isLowSurrogate(next)) {
                return 2;
            }
        }
        return 1;
    }
    
    int getNext() {
        final int index = this.text.getIndex();
        final int endIndex = this.text.getEndIndex();
        final int index2;
        if (index == endIndex || (index2 = index + this.getCurrentCodePointCount()) >= endIndex) {
            return 65535;
        }
        this.text.setIndex(index2);
        return this.getCurrent();
    }
    
    private int getNextIndex() {
        final int n = this.text.getIndex() + this.getCurrentCodePointCount();
        final int endIndex = this.text.getEndIndex();
        if (n > endIndex) {
            return endIndex;
        }
        return n;
    }
    
    protected static final void checkOffset(final int n, final CharacterIterator characterIterator) {
        if (n < characterIterator.getBeginIndex() || n > characterIterator.getEndIndex()) {
            throw new IllegalArgumentException("offset out of bounds");
        }
    }
    
    @Override
    public int following(final int index) {
        final CharacterIterator text = this.getText();
        checkOffset(index, text);
        text.setIndex(index);
        if (index == text.getBeginIndex()) {
            return this.cachedLastKnownBreak = this.handleNext();
        }
        int n = this.cachedLastKnownBreak;
        if (n >= index || n <= -1) {
            n = this.handlePrevious();
        }
        else {
            text.setIndex(n);
        }
        while (n != -1 && n <= index) {
            n = this.handleNext();
        }
        return this.cachedLastKnownBreak = n;
    }
    
    @Override
    public int preceding(final int index) {
        final CharacterIterator text = this.getText();
        checkOffset(index, text);
        text.setIndex(index);
        return this.previous();
    }
    
    @Override
    public boolean isBoundary(final int n) {
        final CharacterIterator text = this.getText();
        checkOffset(n, text);
        return n == text.getBeginIndex() || this.following(n - 1) == n;
    }
    
    @Override
    public int current() {
        return this.getText().getIndex();
    }
    
    @Override
    public CharacterIterator getText() {
        if (this.text == null) {
            this.text = new StringCharacterIterator("");
        }
        return this.text;
    }
    
    @Override
    public void setText(final CharacterIterator text) {
        final int endIndex = text.getEndIndex();
        boolean b;
        try {
            text.setIndex(endIndex);
            b = (text.getIndex() == endIndex);
        }
        catch (final IllegalArgumentException ex) {
            b = false;
        }
        if (b) {
            this.text = text;
        }
        else {
            this.text = new SafeCharIterator(text);
        }
        this.text.first();
        this.cachedLastKnownBreak = -1;
    }
    
    protected int handleNext() {
        final CharacterIterator text = this.getText();
        if (text.getIndex() == text.getEndIndex()) {
            return -1;
        }
        int index = this.getNextIndex();
        int nextIndex = 0;
        int lookupState;
        int n;
        for (lookupState = 1, n = this.getCurrent(); n != 65535 && lookupState != 0; n = this.getNext()) {
            final int lookupCategory = this.lookupCategory(n);
            if (lookupCategory != -1) {
                lookupState = this.lookupState(lookupState, lookupCategory);
            }
            if (this.lookaheadStates[lookupState]) {
                if (this.endStates[lookupState]) {
                    index = nextIndex;
                }
                else {
                    nextIndex = this.getNextIndex();
                }
            }
            else if (this.endStates[lookupState]) {
                index = this.getNextIndex();
            }
        }
        if (n == 65535 && nextIndex == text.getEndIndex()) {
            index = nextIndex;
        }
        text.setIndex(index);
        return index;
    }
    
    protected int handlePrevious() {
        final CharacterIterator text = this.getText();
        int lookupBackwardState = 1;
        int lookupCategory = 0;
        int n = 0;
        int n2;
        for (n2 = this.getCurrent(); n2 != 65535 && lookupBackwardState != 0; n2 = this.getPrevious()) {
            n = lookupCategory;
            lookupCategory = this.lookupCategory(n2);
            if (lookupCategory != -1) {
                lookupBackwardState = this.lookupBackwardState(lookupBackwardState, lookupCategory);
            }
        }
        if (n2 != 65535) {
            if (n != -1) {
                this.getNext();
                this.getNext();
            }
            else {
                this.getNext();
            }
        }
        return text.getIndex();
    }
    
    protected int lookupCategory(final int n) {
        if (n < 65536) {
            return this.charCategoryTable.elementAt((char)n);
        }
        return this.supplementaryCharCategoryTable.getValue(n);
    }
    
    protected int lookupState(final int n, final int n2) {
        return this.stateTable[n * this.numCategories + n2];
    }
    
    protected int lookupBackwardState(final int n, final int n2) {
        return this.backwardsStateTable[n * this.numCategories + n2];
    }
    
    static long getLong(final byte[] array, final int n) {
        long n2 = array[n] & 0xFF;
        for (int i = 1; i < 8; ++i) {
            n2 = (n2 << 8 | (long)(array[n + i] & 0xFF));
        }
        return n2;
    }
    
    static int getInt(final byte[] array, final int n) {
        int n2 = array[n] & 0xFF;
        for (int i = 1; i < 4; ++i) {
            n2 = (n2 << 8 | (array[n + i] & 0xFF));
        }
        return n2;
    }
    
    static short getShort(final byte[] array, final int n) {
        return (short)((short)(array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF));
    }
    
    static {
        LABEL = new byte[] { 66, 73, 100, 97, 116, 97, 0 };
        LABEL_LENGTH = RuleBasedBreakIterator.LABEL.length;
    }
    
    private static final class SafeCharIterator implements CharacterIterator, Cloneable
    {
        private CharacterIterator base;
        private int rangeStart;
        private int rangeLimit;
        private int currentIndex;
        
        SafeCharIterator(final CharacterIterator base) {
            this.base = base;
            this.rangeStart = base.getBeginIndex();
            this.rangeLimit = base.getEndIndex();
            this.currentIndex = base.getIndex();
        }
        
        @Override
        public char first() {
            return this.setIndex(this.rangeStart);
        }
        
        @Override
        public char last() {
            return this.setIndex(this.rangeLimit - 1);
        }
        
        @Override
        public char current() {
            if (this.currentIndex < this.rangeStart || this.currentIndex >= this.rangeLimit) {
                return '\uffff';
            }
            return this.base.setIndex(this.currentIndex);
        }
        
        @Override
        public char next() {
            ++this.currentIndex;
            if (this.currentIndex >= this.rangeLimit) {
                this.currentIndex = this.rangeLimit;
                return '\uffff';
            }
            return this.base.setIndex(this.currentIndex);
        }
        
        @Override
        public char previous() {
            --this.currentIndex;
            if (this.currentIndex < this.rangeStart) {
                this.currentIndex = this.rangeStart;
                return '\uffff';
            }
            return this.base.setIndex(this.currentIndex);
        }
        
        @Override
        public char setIndex(final int currentIndex) {
            if (currentIndex < this.rangeStart || currentIndex > this.rangeLimit) {
                throw new IllegalArgumentException("Invalid position");
            }
            this.currentIndex = currentIndex;
            return this.current();
        }
        
        @Override
        public int getBeginIndex() {
            return this.rangeStart;
        }
        
        @Override
        public int getEndIndex() {
            return this.rangeLimit;
        }
        
        @Override
        public int getIndex() {
            return this.currentIndex;
        }
        
        @Override
        public Object clone() {
            SafeCharIterator safeCharIterator;
            try {
                safeCharIterator = (SafeCharIterator)super.clone();
            }
            catch (final CloneNotSupportedException ex) {
                throw new Error("Clone not supported: " + ex);
            }
            safeCharIterator.base = (CharacterIterator)this.base.clone();
            return safeCharIterator;
        }
    }
}
