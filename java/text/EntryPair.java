package java.text;

final class EntryPair
{
    public String entryName;
    public int value;
    public boolean fwd;
    
    public EntryPair(final String s, final int n) {
        this(s, n, true);
    }
    
    public EntryPair(final String entryName, final int value, final boolean fwd) {
        this.entryName = entryName;
        this.value = value;
        this.fwd = fwd;
    }
}
