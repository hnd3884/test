package java.text;

import sun.text.ComposedCharIter;
import sun.text.normalizer.NormalizerImpl;
import java.util.Vector;
import sun.text.UCompactIntArray;
import sun.text.IntHashtable;

final class RBTableBuilder
{
    static final int CHARINDEX = 1879048192;
    private static final int IGNORABLEMASK = 65535;
    private static final int PRIMARYORDERINCREMENT = 65536;
    private static final int SECONDARYORDERINCREMENT = 256;
    private static final int TERTIARYORDERINCREMENT = 1;
    private static final int INITIALTABLESIZE = 20;
    private static final int MAXKEYSIZE = 5;
    private RBCollationTables.BuildAPI tables;
    private MergeCollation mPattern;
    private boolean isOverIgnore;
    private char[] keyBuf;
    private IntHashtable contractFlags;
    private boolean frenchSec;
    private boolean seAsianSwapping;
    private UCompactIntArray mapping;
    private Vector<Vector<EntryPair>> contractTable;
    private Vector<int[]> expandTable;
    private short maxSecOrder;
    private short maxTerOrder;
    
    public RBTableBuilder(final RBCollationTables.BuildAPI tables) {
        this.tables = null;
        this.mPattern = null;
        this.isOverIgnore = false;
        this.keyBuf = new char[5];
        this.contractFlags = new IntHashtable(100);
        this.frenchSec = false;
        this.seAsianSwapping = false;
        this.mapping = null;
        this.contractTable = null;
        this.expandTable = null;
        this.maxSecOrder = 0;
        this.maxTerOrder = 0;
        this.tables = tables;
    }
    
    public void build(String canonicalDecomposeWithSingleQuotation, final int n) throws ParseException {
        if (canonicalDecomposeWithSingleQuotation.length() == 0) {
            throw new ParseException("Build rules empty.", 0);
        }
        this.mapping = new UCompactIntArray(-1);
        canonicalDecomposeWithSingleQuotation = NormalizerImpl.canonicalDecomposeWithSingleQuotation(canonicalDecomposeWithSingleQuotation);
        this.mPattern = new MergeCollation(canonicalDecomposeWithSingleQuotation);
        int increment = 0;
        for (int i = 0; i < this.mPattern.getCount(); ++i) {
            final PatternEntry item = this.mPattern.getItemAt(i);
            if (item != null) {
                String s = item.getChars();
                if (s.length() > 1) {
                    switch (s.charAt(s.length() - 1)) {
                        case '@': {
                            this.frenchSec = true;
                            s = s.substring(0, s.length() - 1);
                            break;
                        }
                        case '!': {
                            this.seAsianSwapping = true;
                            s = s.substring(0, s.length() - 1);
                            break;
                        }
                    }
                }
                increment = this.increment(item.getStrength(), increment);
                final String extension = item.getExtension();
                if (extension.length() != 0) {
                    this.addExpandOrder(s, extension, increment);
                }
                else if (s.length() > 1) {
                    final char char1 = s.charAt(0);
                    if (Character.isHighSurrogate(char1) && s.length() == 2) {
                        this.addOrder(Character.toCodePoint(char1, s.charAt(1)), increment);
                    }
                    else {
                        this.addContractOrder(s, increment);
                    }
                }
                else {
                    this.addOrder(s.charAt(0), increment);
                }
            }
        }
        this.addComposedChars();
        this.commit();
        this.mapping.compact();
        this.tables.fillInTables(this.frenchSec, this.seAsianSwapping, this.mapping, this.contractTable, this.expandTable, this.contractFlags, this.maxSecOrder, this.maxTerOrder);
    }
    
    private void addComposedChars() throws ParseException {
        final ComposedCharIter composedCharIter = new ComposedCharIter();
        int next;
        while ((next = composedCharIter.next()) != -1) {
            if (this.getCharOrder(next) == -1) {
                final String decomposition = composedCharIter.decomposition();
                if (decomposition.length() == 1) {
                    final int charOrder = this.getCharOrder(decomposition.charAt(0));
                    if (charOrder == -1) {
                        continue;
                    }
                    this.addOrder(next, charOrder);
                }
                else if (decomposition.length() == 2 && Character.isHighSurrogate(decomposition.charAt(0))) {
                    final int charOrder2 = this.getCharOrder(decomposition.codePointAt(0));
                    if (charOrder2 == -1) {
                        continue;
                    }
                    this.addOrder(next, charOrder2);
                }
                else {
                    final int contractOrder = this.getContractOrder(decomposition);
                    if (contractOrder != -1) {
                        this.addOrder(next, contractOrder);
                    }
                    else {
                        boolean b = true;
                        for (int i = 0; i < decomposition.length(); ++i) {
                            if (this.getCharOrder(decomposition.charAt(i)) == -1) {
                                b = false;
                                break;
                            }
                        }
                        if (!b) {
                            continue;
                        }
                        this.addExpandOrder(next, decomposition, -1);
                    }
                }
            }
        }
    }
    
    private final void commit() {
        if (this.expandTable != null) {
            for (int i = 0; i < this.expandTable.size(); ++i) {
                final int[] array = this.expandTable.elementAt(i);
                for (int j = 0; j < array.length; ++j) {
                    final int n = array[j];
                    if (n < 2113929216 && n > 1879048192) {
                        final int n2 = n - 1879048192;
                        final int charOrder = this.getCharOrder(n2);
                        if (charOrder == -1) {
                            array[j] = (0xFFFF & n2);
                        }
                        else {
                            array[j] = charOrder;
                        }
                    }
                }
            }
        }
    }
    
    private final int increment(final int n, int n2) {
        switch (n) {
            case 0: {
                n2 += 65536;
                n2 &= 0xFFFF0000;
                this.isOverIgnore = true;
                break;
            }
            case 1: {
                n2 += 256;
                n2 &= 0xFFFFFF00;
                if (!this.isOverIgnore) {
                    ++this.maxSecOrder;
                    break;
                }
                break;
            }
            case 2: {
                ++n2;
                if (!this.isOverIgnore) {
                    ++this.maxTerOrder;
                    break;
                }
                break;
            }
        }
        return n2;
    }
    
    private final void addOrder(final int n, final int n2) {
        if (this.mapping.elementAt(n) >= 2130706432) {
            int chars = 1;
            if (Character.isSupplementaryCodePoint(n)) {
                chars = Character.toChars(n, this.keyBuf, 0);
            }
            else {
                this.keyBuf[0] = (char)n;
            }
            this.addContractOrder(new String(this.keyBuf, 0, chars), n2);
        }
        else {
            this.mapping.setElementAt(n, n2);
        }
    }
    
    private final void addContractOrder(final String s, final int n) {
        this.addContractOrder(s, n, true);
    }
    
    private final void addContractOrder(final String s, final int value, final boolean b) {
        if (this.contractTable == null) {
            this.contractTable = new Vector<Vector<EntryPair>>(20);
        }
        final int codePoint = s.codePointAt(0);
        final int element = this.mapping.elementAt(codePoint);
        Vector<EntryPair> contractValuesImpl = this.getContractValuesImpl(element - 2130706432);
        if (contractValuesImpl == null) {
            final int n = 2130706432 + this.contractTable.size();
            contractValuesImpl = new Vector<EntryPair>(20);
            this.contractTable.addElement(contractValuesImpl);
            contractValuesImpl.addElement(new EntryPair(s.substring(0, Character.charCount(codePoint)), element));
            this.mapping.setElementAt(codePoint, n);
        }
        final int entry = RBCollationTables.getEntry(contractValuesImpl, s, b);
        if (entry != -1) {
            ((EntryPair)contractValuesImpl.elementAt(entry)).value = value;
        }
        else if (s.length() > contractValuesImpl.lastElement().entryName.length()) {
            contractValuesImpl.addElement(new EntryPair(s, value, b));
        }
        else {
            contractValuesImpl.insertElementAt(new EntryPair(s, value, b), contractValuesImpl.size() - 1);
        }
        if (b && s.length() > 1) {
            this.addContractFlags(s);
            this.addContractOrder(new StringBuffer(s).reverse().toString(), value, false);
        }
    }
    
    private int getContractOrder(final String s) {
        int value = -1;
        if (this.contractTable != null) {
            final Vector<EntryPair> contractValues = this.getContractValues(s.codePointAt(0));
            if (contractValues != null) {
                final int entry = RBCollationTables.getEntry(contractValues, s, true);
                if (entry != -1) {
                    value = contractValues.elementAt(entry).value;
                }
            }
        }
        return value;
    }
    
    private final int getCharOrder(final int n) {
        int n2 = this.mapping.elementAt(n);
        if (n2 >= 2130706432) {
            n2 = this.getContractValuesImpl(n2 - 2130706432).firstElement().value;
        }
        return n2;
    }
    
    private Vector<EntryPair> getContractValues(final int n) {
        return this.getContractValuesImpl(this.mapping.elementAt(n) - 2130706432);
    }
    
    private Vector<EntryPair> getContractValuesImpl(final int n) {
        if (n >= 0) {
            return this.contractTable.elementAt(n);
        }
        return null;
    }
    
    private final void addExpandOrder(final String s, final String s2, final int n) throws ParseException {
        final int addExpansion = this.addExpansion(n, s2);
        if (s.length() > 1) {
            final char char1 = s.charAt(0);
            if (Character.isHighSurrogate(char1) && s.length() == 2) {
                final char char2 = s.charAt(1);
                if (Character.isLowSurrogate(char2)) {
                    this.addOrder(Character.toCodePoint(char1, char2), addExpansion);
                }
            }
            else {
                this.addContractOrder(s, addExpansion);
            }
        }
        else {
            this.addOrder(s.charAt(0), addExpansion);
        }
    }
    
    private final void addExpandOrder(final int n, final String s, final int n2) throws ParseException {
        this.addOrder(n, this.addExpansion(n2, s));
    }
    
    private int addExpansion(final int n, final String s) {
        if (this.expandTable == null) {
            this.expandTable = new Vector<int[]>(20);
        }
        final int n2 = (n != -1) ? 1 : 0;
        int[] array = new int[s.length() + n2];
        if (n2 == 1) {
            array[0] = n;
        }
        int n3 = n2;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            int codePoint;
            if (Character.isHighSurrogate(char1)) {
                if (++i == s.length()) {
                    break;
                }
                final char char2;
                if (!Character.isLowSurrogate(char2 = s.charAt(i))) {
                    break;
                }
                codePoint = Character.toCodePoint(char1, char2);
            }
            else {
                codePoint = char1;
            }
            final int charOrder = this.getCharOrder(codePoint);
            if (charOrder != -1) {
                array[n3++] = charOrder;
            }
            else {
                array[n3++] = 1879048192 + codePoint;
            }
        }
        if (n3 < array.length) {
            final int[] array2 = new int[n3];
            while (--n3 >= 0) {
                array2[n3] = array[n3];
            }
            array = array2;
        }
        final int n4 = 2113929216 + this.expandTable.size();
        this.expandTable.addElement(array);
        return n4;
    }
    
    private void addContractFlags(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            this.contractFlags.put(Character.isHighSurrogate(char1) ? Character.toCodePoint(char1, s.charAt(++i)) : char1, 1);
        }
    }
}
