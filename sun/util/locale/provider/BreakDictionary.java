package sun.util.locale.provider;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.BufferedInputStream;
import java.util.MissingResourceException;
import java.io.IOException;
import sun.text.SupplementaryCharacterData;
import sun.text.CompactByteArray;

class BreakDictionary
{
    private static int supportedVersion;
    private CompactByteArray columnMap;
    private SupplementaryCharacterData supplementaryCharColumnMap;
    private int numCols;
    private int numColGroups;
    private short[] table;
    private short[] rowIndex;
    private int[] rowIndexFlags;
    private short[] rowIndexFlagsIndex;
    private byte[] rowIndexShifts;
    
    BreakDictionary(final String s) throws IOException, MissingResourceException {
        this.columnMap = null;
        this.supplementaryCharColumnMap = null;
        this.table = null;
        this.rowIndex = null;
        this.rowIndexFlags = null;
        this.rowIndexFlagsIndex = null;
        this.rowIndexShifts = null;
        this.readDictionaryFile(s);
    }
    
    private void readDictionaryFile(final String s) throws IOException, MissingResourceException {
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
        final byte[] array = new byte[8];
        if (bufferedInputStream.read(array) != 8) {
            throw new MissingResourceException("Wrong data length", s, "");
        }
        final int int1 = RuleBasedBreakIterator.getInt(array, 0);
        if (int1 != BreakDictionary.supportedVersion) {
            throw new MissingResourceException("Dictionary version(" + int1 + ") is unsupported", s, "");
        }
        final int int2 = RuleBasedBreakIterator.getInt(array, 4);
        final byte[] array2 = new byte[int2];
        if (bufferedInputStream.read(array2) != int2) {
            throw new MissingResourceException("Wrong data length", s, "");
        }
        bufferedInputStream.close();
        int n = 0;
        final int int3 = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        final short[] array3 = new short[int3];
        for (int i = 0; i < int3; ++i, n += 2) {
            array3[i] = RuleBasedBreakIterator.getShort(array2, n);
        }
        final int int4 = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        final byte[] array4 = new byte[int4];
        for (int j = 0; j < int4; ++j, ++n) {
            array4[j] = array2[n];
        }
        this.columnMap = new CompactByteArray(array3, array4);
        this.numCols = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        this.numColGroups = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        final int int5 = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        this.rowIndex = new short[int5];
        for (int k = 0; k < int5; ++k, n += 2) {
            this.rowIndex[k] = RuleBasedBreakIterator.getShort(array2, n);
        }
        final int int6 = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        this.rowIndexFlagsIndex = new short[int6];
        for (int l = 0; l < int6; ++l, n += 2) {
            this.rowIndexFlagsIndex[l] = RuleBasedBreakIterator.getShort(array2, n);
        }
        final int int7 = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        this.rowIndexFlags = new int[int7];
        for (int n2 = 0; n2 < int7; ++n2, n += 4) {
            this.rowIndexFlags[n2] = RuleBasedBreakIterator.getInt(array2, n);
        }
        final int int8 = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        this.rowIndexShifts = new byte[int8];
        for (int n3 = 0; n3 < int8; ++n3, ++n) {
            this.rowIndexShifts[n3] = array2[n];
        }
        final int int9 = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        this.table = new short[int9];
        for (int n4 = 0; n4 < int9; ++n4, n += 2) {
            this.table[n4] = RuleBasedBreakIterator.getShort(array2, n);
        }
        final int int10 = RuleBasedBreakIterator.getInt(array2, n);
        n += 4;
        final int[] array5 = new int[int10];
        for (int n5 = 0; n5 < int10; ++n5, n += 4) {
            array5[n5] = RuleBasedBreakIterator.getInt(array2, n);
        }
        this.supplementaryCharColumnMap = new SupplementaryCharacterData(array5);
    }
    
    public final short getNextStateFromCharacter(final int n, final int n2) {
        int n3;
        if (n2 < 65536) {
            n3 = this.columnMap.elementAt((char)n2);
        }
        else {
            n3 = this.supplementaryCharColumnMap.getValue(n2);
        }
        return this.getNextState(n, n3);
    }
    
    public final short getNextState(final int n, final int n2) {
        if (this.cellIsPopulated(n, n2)) {
            return this.internalAt(this.rowIndex[n], n2 + this.rowIndexShifts[n]);
        }
        return 0;
    }
    
    private boolean cellIsPopulated(final int n, final int n2) {
        if (this.rowIndexFlagsIndex[n] < 0) {
            return n2 == -this.rowIndexFlagsIndex[n];
        }
        return (this.rowIndexFlags[this.rowIndexFlagsIndex[n] + (n2 >> 5)] & 1 << (n2 & 0x1F)) != 0x0;
    }
    
    private short internalAt(final int n, final int n2) {
        return this.table[n * this.numCols + n2];
    }
    
    static {
        BreakDictionary.supportedVersion = 1;
    }
}
