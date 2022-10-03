package java.text;

import sun.text.IntHashtable;
import java.util.Vector;
import sun.text.UCompactIntArray;

final class RBCollationTables
{
    static final int EXPANDCHARINDEX = 2113929216;
    static final int CONTRACTCHARINDEX = 2130706432;
    static final int UNMAPPED = -1;
    static final int PRIMARYORDERMASK = -65536;
    static final int SECONDARYORDERMASK = 65280;
    static final int TERTIARYORDERMASK = 255;
    static final int PRIMARYDIFFERENCEONLY = -65536;
    static final int SECONDARYDIFFERENCEONLY = -256;
    static final int PRIMARYORDERSHIFT = 16;
    static final int SECONDARYORDERSHIFT = 8;
    private String rules;
    private boolean frenchSec;
    private boolean seAsianSwapping;
    private UCompactIntArray mapping;
    private Vector<Vector<EntryPair>> contractTable;
    private Vector<int[]> expandTable;
    private IntHashtable contractFlags;
    private short maxSecOrder;
    private short maxTerOrder;
    
    public RBCollationTables(final String rules, final int n) throws ParseException {
        this.rules = null;
        this.frenchSec = false;
        this.seAsianSwapping = false;
        this.mapping = null;
        this.contractTable = null;
        this.expandTable = null;
        this.contractFlags = null;
        this.maxSecOrder = 0;
        this.maxTerOrder = 0;
        this.rules = rules;
        new RBTableBuilder(new BuildAPI()).build(rules, n);
    }
    
    public String getRules() {
        return this.rules;
    }
    
    public boolean isFrenchSec() {
        return this.frenchSec;
    }
    
    public boolean isSEAsianSwapping() {
        return this.seAsianSwapping;
    }
    
    Vector<EntryPair> getContractValues(final int n) {
        return this.getContractValuesImpl(this.mapping.elementAt(n) - 2130706432);
    }
    
    private Vector<EntryPair> getContractValuesImpl(final int n) {
        if (n >= 0) {
            return this.contractTable.elementAt(n);
        }
        return null;
    }
    
    boolean usedInContractSeq(final int n) {
        return this.contractFlags.get(n) == 1;
    }
    
    int getMaxExpansion(final int n) {
        int n2 = 1;
        if (this.expandTable != null) {
            for (int i = 0; i < this.expandTable.size(); ++i) {
                final int[] array = this.expandTable.elementAt(i);
                final int length = array.length;
                if (length > n2 && array[length - 1] == n) {
                    n2 = length;
                }
            }
        }
        return n2;
    }
    
    final int[] getExpandValueList(final int n) {
        return this.expandTable.elementAt(n - 2113929216);
    }
    
    int getUnicodeOrder(final int n) {
        return this.mapping.elementAt(n);
    }
    
    short getMaxSecOrder() {
        return this.maxSecOrder;
    }
    
    short getMaxTerOrder() {
        return this.maxTerOrder;
    }
    
    static void reverse(final StringBuffer sb, final int n, final int n2) {
        for (int i = n, n3 = n2 - 1; i < n3; ++i, --n3) {
            final char char1 = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(n3));
            sb.setCharAt(n3, char1);
        }
    }
    
    static final int getEntry(final Vector<EntryPair> vector, final String s, final boolean b) {
        for (int i = 0; i < vector.size(); ++i) {
            final EntryPair entryPair = vector.elementAt(i);
            if (entryPair.fwd == b && entryPair.entryName.equals(s)) {
                return i;
            }
        }
        return -1;
    }
    
    final class BuildAPI
    {
        private BuildAPI() {
        }
        
        void fillInTables(final boolean b, final boolean b2, final UCompactIntArray uCompactIntArray, final Vector<Vector<EntryPair>> vector, final Vector<int[]> vector2, final IntHashtable intHashtable, final short n, final short n2) {
            RBCollationTables.this.frenchSec = b;
            RBCollationTables.this.seAsianSwapping = b2;
            RBCollationTables.this.mapping = uCompactIntArray;
            RBCollationTables.this.contractTable = vector;
            RBCollationTables.this.expandTable = vector2;
            RBCollationTables.this.contractFlags = intHashtable;
            RBCollationTables.this.maxSecOrder = n;
            RBCollationTables.this.maxTerOrder = n2;
        }
    }
}
